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

  public String forgotPassword(String username) {
    // Map<String, Object> result = resetAccountPassword(username);
    // if(((String)result.get("result")).equals("success")){
    // //send an email here and then return the response
    // return "The password has been sent to your email"
    // }
    return "There was an error while resetting the password, please contact your administrator for password resetting!";
  }

}
