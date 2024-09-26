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
import com.system.internship.domain.Staff;
import com.system.internship.domain.Student;
import com.system.internship.dto.*;
import com.system.internship.enums.DepartmentEnum;
import com.system.internship.enums.RoleEnum;
import com.system.internship.enums.TypeUserEnum;
import com.system.internship.exception.UsernameNotFoundException;
import com.system.internship.repository.*;

import com.system.internship.services.strategy.AccountRegistrationStrategy;
import com.system.internship.services.strategy.StaffRegistrationStrategy;
import com.system.internship.services.strategy.StudentRegistrationStrategy;

import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

import java.util.Map;

@Service
public class AdministratorService {

  @Autowired
  private AccountRepository accountRepository;

  @Autowired
  private OpenPasswordRepository opRepo;

  @Autowired
  private BulkEmailService bulkEmailService;

  @Autowired
  private AccountService accountService;

  @Autowired
  private StudentRepository studentRepository;

  @Autowired
  private StaffRepository staffRepository;

  @Autowired
  PasswordService passwordService;

  private final Map<TypeUserEnum, AccountRegistrationStrategy> strategies;
  private final RoleService roleService;

  // create the two strategies for registration (for student and staff)
  public AdministratorService(RestTemplate restTemplate, StaffRepository staffRepository,
      StudentRepository studentRepository, PasswordEncoder passwordEncoder,
      OpenPasswordRepository openPasswordRepository, RoleService roleService, AccountService accountService) {
    strategies = new HashMap<>();
    this.roleService = roleService;
    strategies.put(TypeUserEnum.STAFF, new StaffRegistrationStrategy(
        restTemplate, staffRepository, passwordEncoder, openPasswordRepository, roleService, accountService));
    strategies.put(TypeUserEnum.STUDENT, new StudentRegistrationStrategy(
        restTemplate, studentRepository, passwordEncoder, openPasswordRepository, roleService, accountService));
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
    addThePasswords(registerResponseDto); // adds the open password to the accounts if there is one
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
    addThePasswords(registerResponseDto); // adds the open password to the accounts if there is uno
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
    if (!accountOpt.isPresent()) {
      throw new UsernameNotFoundException(username);
    }
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
  }

  public String removeRole(String username, RoleEnum role) {
    Optional<Account> accountOpt = accountRepository.findByUsername(username);
    if (!accountOpt.isPresent()) {
      throw new UsernameNotFoundException(username);
    }
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
  }

  public String setEnableness(String username, Boolean enabled) {
    Optional<Account> accountOpt = accountRepository.findByUsername(username);
    if (!accountOpt.isPresent()) {
      throw new UsernameNotFoundException(username);
    }
    Account account = accountOpt.get();
    account.setEnabled(enabled);
    accountRepository.save(account);
    return "Set the user enableness successfully";
  }

  public String resetAccountPassword(String username) {
    Map<String, Object> result = accountService.resetAccountPassword(username);
    if (((String) result.get("result")).equals("success")) {
      return "The password of the user \"" + username + "\" has been successfully reset to \"" + result.get("password")
          + "\"!";
    }
    return "There was an error, either the username is incorrect or network failure";
  }

  public void addThePasswords(RegisterResponseDto response) {
    if (response.getExistingStaffs() != null) {
      setPasswords(response.getExistingStaffs());
    }
    if (response.getExistingStudents() != null) {
      setPasswords(response.getExistingStudents());
    }
    if (response.getRegisteredStaffs() != null) {
      setPasswords(response.getRegisteredStaffs());
    }
    if (response.getRegisteredStudents() != null) {
      setPasswords(response.getRegisteredStudents());
    }
  }

  public void setPasswords(List<? extends Account> accounts) {
    accounts.forEach(account -> {
      String openPassword = passwordService.getOpenPasswordOfAccount(account);
      account.setPassword(openPassword);
    });
  }

  public Account deleteAccount(String username) {
    Optional<Account> accountOpt = accountRepository.findByUsername(username);
    Account account = null;
    if (accountOpt.isPresent()) {
      account = accountOpt.get();
      Optional<OpenPassword> opOpt = opRepo.findByAccount(account);
      if (opOpt.isPresent())
        opRepo.delete(opOpt.get());
      accountRepository.delete(account);
    }
    return account;
  }

  @Transactional
  public List<? extends Account> deleteAccountsByDepartment(String department,
      String typeUser) throws Exception {
    TypeUserEnum typeUserEnum = TypeUserEnum.valueOf(typeUser.toUpperCase());
    DepartmentEnum departmentEnum = DepartmentEnum.valueOf(department);

    if (typeUserEnum.equals(TypeUserEnum.STUDENT)) {
      List<Student> students = studentRepository.findByDepartment(departmentEnum);
      deleteOpenPasswordsOfAccounts(students);
      studentRepository.deleteStudentsByDepartment(departmentEnum);
      return students;
    } else if (typeUserEnum.equals(TypeUserEnum.STAFF)) {
      List<Staff> staffs = staffRepository.findByDepartment(departmentEnum);
      deleteOpenPasswordsOfAccounts(staffs);
      staffRepository.deleteStaffsByDepartment(departmentEnum);
      return staffs;
    } else {
      throw new Exception("The Type of user you input is invalid must only be STAFF/STUDENT.");
    }
  }

  public void deleteOpenPasswordsOfAccounts(List<? extends Account> accounts) {
    opRepo.deleteByAccountIn(accounts);
  }

  public Account getAccount(String username) {
    Optional<Account> accountOpt = accountRepository.findByUsername(username);
    if (accountOpt.isPresent()) {
      Account acc = accountOpt.get();
      acc.setPassword(passwordService.getOpenPasswordOfAccount(acc));
      return acc;
    }
    return null;
  }

  public List<? extends Account> getAccountsByDepartment(String department, String typeUser) throws Exception {
    TypeUserEnum typeUserEnum = TypeUserEnum.valueOf(typeUser.toUpperCase());
    DepartmentEnum departmentEnum = DepartmentEnum.valueOf(department);
    if (typeUserEnum.equals(TypeUserEnum.STUDENT)) {
      List<Student> students = studentRepository.findByDepartment(departmentEnum);
      setPasswords(students);
      return students;
    } else if (typeUserEnum.equals(TypeUserEnum.STAFF)) {
      List<Staff> staffs = staffRepository.findByDepartment(departmentEnum);
      setPasswords(staffs);
      return staffs;
    } else {
      throw new Exception("The Type of user you input is invalid must only be STAFF/STUDENT.");
    }
  }

}
