package com.system.internship.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.system.internship.domain.Account;
import com.system.internship.domain.OpenPassword;
import com.system.internship.dto.AccountDto;
import com.system.internship.dto.PasswordUpdateDto;
import com.system.internship.repository.AccountRepository;
import com.system.internship.repository.OpenPasswordRepository;
import com.system.internship.util.PasswordGenerator;

import java.util.Optional;
import java.util.Map;

import lombok.val;

@Service
public class AccountService {

  @Autowired
  private OpenPasswordRepository opRepo;

  @Autowired
  private PasswordEncoder passwordEncoder;

  @Autowired
  private AccountRepository accountRepository;

  @Autowired
  private PasswordService passwordService;

  @Autowired
  private EmailService emailService;

  @Autowired
  private JwtService jwtService;

  public AccountDto getAccountDto() {

    val accountDto = AccountDto.builder();

    Account currentAccount = (Account) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    System.out.println(currentAccount);
    Optional<OpenPassword> openOpt = opRepo.findByAccount(currentAccount);
    if (openOpt.isPresent()) { // if the password exists in the openpassword table then it needs reset
      accountDto.passwordNeedChange(true);
    } else {
      accountDto.passwordNeedChange(false);
    }
    currentAccount.setPassword(null);
    accountDto.account(currentAccount);
    return accountDto.build();
  }

  public String updatePassword(PasswordUpdateDto passwordUpdateDto) {
    Account currentAccount = (Account) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    String current = passwordUpdateDto.getCurrentPassword();
    if (!passwordEncoder.matches(current, currentAccount.getPassword())) {
      return "The Passwords don't match";
    }
    String newPassword = passwordUpdateDto.getNewPassword();
    String repeatPassword = passwordUpdateDto.getRepeatPassword();

    if (newPassword.equals(repeatPassword)) {
      Optional<OpenPassword> opPass = opRepo.findByAccount(currentAccount);
      if (opPass.isPresent()) { // if it is present, then remove it
        opRepo.delete(opPass.get());
      }
      currentAccount.setPassword(passwordEncoder.encode(newPassword));
      accountRepository.save(currentAccount);
      return "Password Changed Successfully! Please don't forget the password";
    }
    return "The new and repeated Passwords don't match";
  }

  public Map<String, Object> resetAccountPassword(String username) {
    Optional<Account> accountOpt = accountRepository.findByUsername(username);
    if (accountOpt.isPresent()) {
      Account account = accountOpt.get();
      String generatedPassword = PasswordGenerator.generateRandomPassword(8);
      account.setPassword(passwordEncoder.encode(generatedPassword));
      OpenPassword op = passwordService.getPassword(account);
      op.setPassword(generatedPassword);
      opRepo.save(op);
      accountRepository.save(account);
      return Map.of("result", "success", "password", generatedPassword);
    }
    return Map.of("result", "failure", "password", null);
  }

  public String resetPasswordThroughEmail(String username, String inputtedPassword) {
    Optional<Account> accountOpt = accountRepository.findByUsername(username);
    if (accountOpt.isPresent()) {
      Account account = accountOpt.get();
      account.setPassword(passwordEncoder.encode(inputtedPassword));
      Optional<OpenPassword> op = opRepo.findByAccount(account);
      if (op.isPresent()) { // if there was an open password then remove it
        opRepo.delete(op.get());
      }
      accountRepository.save(account);
      return "Account Password Reset Successfully!";
    }
    return "There was an error trying to reset your password, please try again later!";
  }

  public String forgotPassword(String username) {

    String passwordResetToken = jwtService.generateTokenForPasswordReset(username);
    String email = getEmailFromUsername(username);
    if (passwordResetToken == null || email == null) { // if the user doesn't exis
      return "The username doesn't exist please contact the administrator!";
    }
    String link = "http://192.168.1.11:5173/reset-password?token=" + passwordResetToken;
    String content = "Dear user, you have requested a password reset request click the link below to reset the password";
    content += "<br>If you haven't requested this, ignore the message.<br>";
    content += link;
    content += "<br>Note that the Link only works for 15 minutes";
    String emailAddress = "fikretesfay4444@gmail.com";
    emailService.sendEmail(emailAddress, "password reset", content);
    return "The password Link was sent to your corresponding email check it and click the link!";

  }

  public String getEmailFromUsername(String username) {
    String email = null;
    Optional<Account> actOpt = accountRepository.findByUsername(username);
    if (actOpt.isPresent()) {
      Account account = actOpt.get();
      if (emailService.validateEmail(account.getEmail())) {
        email = account.getEmail();
      }
    }

    return email;
  }

}
