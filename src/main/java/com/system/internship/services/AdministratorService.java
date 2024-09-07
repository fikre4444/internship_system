package com.system.internship.services;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.system.internship.domain.Account;
import com.system.internship.domain.OpenPassword;
import com.system.internship.domain.Role;
import com.system.internship.dto.*;
import com.system.internship.enums.RoleEnum;
import com.system.internship.enums.TypeUserEnum;
import com.system.internship.repository.*;

import com.system.internship.services.strategy.AccountRegistrationStrategy;
import com.system.internship.services.strategy.StaffRegistrationStrategy;
import com.system.internship.services.strategy.StudentRegistrationStrategy;

@Service
public class AdministratorService {

  @Autowired
  private AccountRepository accountRepository;

  @Autowired
  private OpenPasswordRepository opRepo;

  @Autowired
  private BulkEmailService bulkEmailService;

  private final Map<TypeUserEnum, AccountRegistrationStrategy> strategies;
  private final RoleService roleService;

  // create the two strategies for registration (for student and staff)
  public AdministratorService(RestTemplate restTemplate, StaffRepository staffRepository,
      StudentRepository studentRepository, PasswordEncoder passwordEncoder,
      OpenPasswordRepository openPasswordRepository, RoleService roleService) {
    strategies = new HashMap<>();
    this.roleService = roleService;
    strategies.put(TypeUserEnum.STAFF, new StaffRegistrationStrategy(
        restTemplate, staffRepository, passwordEncoder, openPasswordRepository, roleService));
    strategies.put(TypeUserEnum.STUDENT, new StudentRegistrationStrategy(
        restTemplate, studentRepository, passwordEncoder, openPasswordRepository, roleService));
  }

  public RegisterResponseDto register(RegisterRequestBodyDto body) {
    RegisterResponseDto registerResponseDto = new RegisterResponseDto();
    try {
      RegisterRequestUtil.checkBodyIntegrity(body); // Validate request body
      URI uri = RegisterRequestUtil.getUriFromBody(body);
      // get registration strategy based on the type of user (whether student or
      // staff)
      AccountRegistrationStrategy strategy = strategies.get(body.getTypeUser());
      if (body.getAmount().equals(RegisterRequestBodyDto.AmountEnum.BATCH)) {
        registerResponseDto = strategy.registerBatch(uri);
      } else if (body.getAmount().equals(RegisterRequestBodyDto.AmountEnum.SINGLE)) {
        registerResponseDto = strategy.registerSingle(uri);
      }
    } catch (HttpClientErrorException exception) {
      registerResponseDto = RegisterResponseDto.builder().errorResponse(true).build();
    } catch (Exception ex) {
      ex.printStackTrace();
      registerResponseDto = RegisterResponseDto.builder().incorrectBody(true).build();
    }
    return registerResponseDto;
  }

  public RegisterResponseDto registerCustom(RegisterRequestCustomBodyDto registerDto) {
    RegisterResponseDto registerResponseDto = new RegisterResponseDto();
    // if body is incorrect set the response and return
    if (!RegisterRequestUtil.checkCustomBodyIntegrity(registerDto)) {
      registerResponseDto = RegisterResponseDto.builder().incorrectBody(true).build();
      return registerResponseDto;
    }
    // get strategy based on type of user
    AccountRegistrationStrategy strategy = strategies.get(registerDto.getTypeUser());
    registerResponseDto = strategy.registerCustom(registerDto);
    return registerResponseDto;
  }

  public void sendEmails(List<String> usernames) {
    List<Account> accounts = accountRepository.findAllByUsernameIn(usernames);
    List<Account> validEmailAccounts = accounts.stream().filter(account -> validateEmail(account.getEmail()))
        .collect(Collectors.toList());
    List<Long> validEmailAccountIds = validEmailAccounts.stream().map(account -> account.getId())
        .collect(Collectors.toList());
    List<OpenPassword> validEmailPasswords = opRepo.findAllById(validEmailAccountIds);

    List<EmailDetailsDto> emailDtos = new ArrayList<>();

    validEmailAccounts.forEach(validAccount -> {
      String to = validAccount.getEmail();
      String subject = "Notifying User about username and password";
      String password = validEmailPasswords.stream().filter(op -> op.getAccount().equals(validAccount))
          .findFirst().get().getPassword();
      String username = validAccount.getUsername();
      String text = "Hello " + validAccount.getFirstName()
          + ", your username and password for the Internship Management System of Mekelle University is "
          + "<br>\"Username\"->" + username + "<br>\"Password\"->" + password;
      emailDtos.add(new EmailDetailsDto(to, subject, text));
    });

    System.out.println("The list of Email Dtos are ");
    emailDtos.forEach(emailDto -> System.out.println(emailDto));

    bulkEmailService.sendBulkEmails(emailDtos);

  }

  private boolean validateEmail(String email) {
    if (email == null)
      return false;
    if (email.equals(""))
      return false;
    String EMAIL_PATTERN = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
    Pattern pattern = Pattern.compile(EMAIL_PATTERN);
    Matcher matcher = pattern.matcher(email);
    return matcher.matches();
  }

  public String addRole(String username, RoleEnum role) {
    Optional<Account> accountOpt = accountRepository.findByUsername(username);
    if (accountOpt.isPresent()) {
      Account account = accountOpt.get();
      Set<Role> existingRoles = account.getRoles();
      Optional<Role> existingOpt = existingRoles.stream().filter(existingRole -> existingRole.getName().equals(role))
          .findFirst();
      if (existingOpt.isPresent()) {
        return "The role: " + role + " already exists for the User";
      }
      Role neededRole = roleService.getRole(role);
      existingRoles.add(neededRole);
      account.setRoles(existingRoles);
      accountRepository.save(account);
      return "Added the role: " + role + " Successfully!";
    } else {
      return "The username doesn't exist";
    }
  }

  public String removeRole(String username, RoleEnum role) {
    Optional<Account> accountOpt = accountRepository.findByUsername(username);
    if (accountOpt.isPresent()) {
      Account account = accountOpt.get();
      Set<Role> existingRoles = account.getRoles();
      Optional<Role> toBeRemovedOpt = existingRoles.stream().filter(existingRole -> existingRole.getName().equals(role))
          .findFirst();
      if (!toBeRemovedOpt.isPresent()) {
        return "The role " + role + " doesn't exist for the user!";
      }
      Role toBeRemoved = toBeRemovedOpt.get();
      if (toBeRemoved.getName().equals(RoleEnum.ROLE_STUDENT) || toBeRemoved.getName().equals(RoleEnum.ROLE_STAFF)) {
        return "The role " + role + " cannot be removed for anyone";
      }
      existingRoles.remove(toBeRemoved);
      account.setRoles(existingRoles);
      accountRepository.save(account);
      return "Removed the role: " + role + " Successfully!";
    } else {
      return "The username doesn't exist";
    }
  }

}
