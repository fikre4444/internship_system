package com.system.internship.services;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
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
import com.system.internship.enums.GenderEnum;
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

  public Map<String, Object> sendEmails(List<String> usernames) {
    try {
      List<Account> accounts = accountRepository.findAllByUsernameIn(usernames);
      List<Account> validEmailAccounts = accounts.stream().filter(account -> validateEmail(account.getEmail()))
          .collect(Collectors.toList());
      List<OpenPassword> validEmailPasswords = opRepo.findByAccountIn(validEmailAccounts);
      validEmailAccounts.forEach(validAccount -> {
        System.out.println(validAccount);
      });

      List<EmailDetailsDto> emailDtos = new ArrayList<>();

      validEmailAccounts.forEach(validAccount -> {
        String to = validAccount.getEmail();
        String subject = "Notifying User about username and password";
        String password = validEmailPasswords.stream().filter(op -> op.getAccount().equals(validAccount))
            .findFirst().get().getPassword();
        String username = validAccount.getUsername();
        String text = "<h1>Internship Managmenet System</h1><br/>";
        text += "Hello " + validAccount.getFirstName()
            + ", your username and password for the Internship Management System of Mekelle University is "
            + "<br>\"Username\"->" + username + "<br>\"Password\"->" + password;
        emailDtos.add(new EmailDetailsDto(to, subject, text));
      });

      System.out.println("The list of Email Dtos are ");
      emailDtos.forEach(emailDto -> System.out.println(emailDto));

      // bulkEmailService.sendBulkEmails(emailDtos);
      return Map.of("result", "success", "message", "Successfully Notified the ones with valid Emails!");
    } catch (Exception ex) {
      System.out.println(("an error occured while sending the emails"));
      System.out.println(ex);
      ex.printStackTrace();
      return Map.of("result", "error", "message", "an error occured while sending the emails");
    }
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

  public Map<String, Object> addRole(String username, RoleEnum role) {
    Optional<Account> accountOpt = accountRepository.findByUsername(username);
    if (!accountOpt.isPresent()) {
      throw new UsernameNotFoundException(username);
    }
    Account account = accountOpt.get();
    Set<Role> existingRoles = account.getRoles();
    Optional<Role> existingOpt = existingRoles.stream().filter(existingRole -> existingRole.getName().equals(role))
        .findFirst();
    if (existingOpt.isPresent()) {
      return Map.of("result", "failure", "existingRole", existingOpt.get(), "message", "the role already exists!");
    }
    if ((account instanceof Student)) {
      return Map.of("result", "failure", "message", "This role cannot be applied to a student account.");
    }
    if ((account instanceof Staff) && role.equals(RoleEnum.valueOf("ROLE_STUDENT"))) {
      return Map.of("result", "failure", "message", "A Staff cannot have a role of Student");
    }
    Role neededRole = roleService.getRole(role);
    existingRoles.add(neededRole);
    account.setRoles(existingRoles);
    accountRepository.save(account);
    return Map.of("result", "success", "addedRole", neededRole, "message", "successfully added the role.");
  }

  public Map<String, Object> removeRole(String username, RoleEnum role) {
    Optional<Account> accountOpt = accountRepository.findByUsername(username);
    if (!accountOpt.isPresent()) {
      throw new UsernameNotFoundException(username);
    }
    Account account = accountOpt.get();
    Set<Role> existingRoles = account.getRoles();
    Optional<Role> toBeRemovedOpt = existingRoles.stream().filter(existingRole -> existingRole.getName().equals(role))
        .findFirst();
    if (!toBeRemovedOpt.isPresent()) { // if the role is not present
      return Map.of("result", "failure", "message", "the role is not present for the user", "role",
          Role.builder().name(role).build());

    }
    Role toBeRemoved = toBeRemovedOpt.get();
    if ((toBeRemoved.getName().equals(RoleEnum.ROLE_STUDENT) && account instanceof Student)
        || (toBeRemoved.getName().equals(RoleEnum.ROLE_STAFF) && account instanceof Staff)) {
      return Map.of("result", "failure", "message", "the role cannot be removed for this type of user", "role",
          Role.builder().name(role).build());
    }
    existingRoles.remove(toBeRemoved);
    account.setRoles(existingRoles);
    accountRepository.save(account);
    return Map.of("result", "success", "removedRole", toBeRemoved, "message", "successfully removed the role.");

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

  public Map<String, Object> resetAccountPassword(String username) {
    Map<String, Object> result = accountService.resetAccountPassword(username);
    if (((String) result.get("result")).equals("success")) {
      return result;
    }
    result.put("result", "failure");
    result.put("password", null);
    return result;
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
    if (!accountOpt.isPresent()) {
      throw new UsernameNotFoundException(username);
    }
    Account account = accountOpt.get();
    Optional<OpenPassword> opOpt = opRepo.findByAccount(account);
    if (opOpt.isPresent())
      opRepo.delete(opOpt.get());
    accountRepository.delete(account);
    return account;
  }

  @Transactional
  public List<? extends Account> deleteAccountsByDepartment(String department,
      String typeUser) throws Exception {
    DepartmentEnum departmentEnum = DepartmentEnum.valueOf(department);
    List<Account> accounts = new ArrayList<>();
    if (typeUser.equalsIgnoreCase("BOTH")
        || typeUser.equalsIgnoreCase("STUDENT")) {
      List<Student> students = studentRepository.findByDepartment(departmentEnum);
      deleteOpenPasswordsOfAccounts(students);
      studentRepository.deleteStudentsByDepartment(departmentEnum);
      accounts.addAll(students);
    }
    if (typeUser.equalsIgnoreCase("BOTH")
        || typeUser.equalsIgnoreCase("STAFF")) {
      List<Staff> staffs = staffRepository.findByDepartment(departmentEnum);
      deleteOpenPasswordsOfAccounts(staffs);
      staffRepository.deleteStaffsByDepartment(departmentEnum);
      accounts.addAll(staffs);
    } else {
      throw new Exception("The Type of user you input is invalid must only be STAFF/STUDENT.");
    }
    return accounts;
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

  public List<? extends Account> getAccountsByUsername(String username) {
    List<Account> accounts = accountRepository.findByUsernameContainingIgnoreCase(username);
    setPasswords(accounts);
    return accounts;
  }

  public List<Account> getAccountsBySearchTerm(String searchTerm) {
    List<Account> accounts = accountRepository.findBySearchTerm(searchTerm);
    setPasswords(accounts);
    return accounts;
  }

  public DepartmentEnum changeDepartment(String username, DepartmentEnum departmentEnum) {
    Optional<Account> accountOpt = accountRepository.findByUsername(username);
    if (!accountOpt.isPresent()) {
      throw new UsernameNotFoundException(username);
    }
    Account account = accountOpt.get();
    if (account instanceof Student) {
      Student student = (Student) account;
      student.setDepartment(departmentEnum);
      studentRepository.save(student);
    }
    if (account instanceof Staff) {
      Staff staff = (Staff) account;
      staff.setDepartment(departmentEnum);
      staffRepository.save(staff);
    }
    return departmentEnum;
  }

  public List<Account> searchInclusive(Map<String, String> requestObject) {
    Set<Account> accounts = new HashSet<>();
    // we use firstName, lastName, username, gender, department, staff or student or
    // both
    String firstName = requestObject.get("firstName");
    String lastName = requestObject.get("lastName");
    String username = requestObject.get("username");
    GenderEnum gender;
    DepartmentEnum department;
    try {
      gender = GenderEnum.valueOf(requestObject.get("gender"));
    } catch (Exception e) {
      gender = null;
    }
    try {
      department = DepartmentEnum.valueOf(requestObject.get("department"));
    } catch (Exception e) {
      department = null;
    }

    if (requestObject.get("typeOfUser").equals("BOTH")) {
      accounts.addAll(studentRepository.searchAccountsInclusive(firstName, lastName, username, gender, department));
      accounts.addAll(staffRepository.searchAccountsInclusive(firstName, lastName, username, gender, department));
    } else if (requestObject.get("typeOfUser").equals("STAFF")) {
      accounts.addAll(staffRepository.searchAccountsInclusive(firstName, lastName, username, gender, department));
    } else if (requestObject.get("typeOfUser").equals("STUDENT")) {
      accounts.addAll(studentRepository.searchAccountsInclusive(firstName, lastName, username, gender, department));
    }
    List<Account> listAccounts = accounts.stream().toList();
    setPasswords(listAccounts);
    return listAccounts;
  }

  public List<Account> searchRestrictive(Map<String, String> requestObject) {
    Set<Account> accounts = new HashSet<>();
    // we use firstName, lastName, username, gender, department, staff or student or
    // both
    String firstName = requestObject.get("firstName");
    String lastName = requestObject.get("lastName");
    String username = requestObject.get("username");
    GenderEnum gender;
    DepartmentEnum department;
    try {
      gender = GenderEnum.valueOf(requestObject.get("gender"));
    } catch (Exception e) {
      gender = null;
    }
    try {
      department = DepartmentEnum.valueOf(requestObject.get("department"));
    } catch (Exception e) {
      department = null;
    }

    if (requestObject.get("typeOfUser").equals("BOTH")) {
      accounts.addAll(studentRepository.searchAccountsRestrictive(firstName, lastName, username, gender, department));
      accounts.addAll(staffRepository.searchAccountsRestrictive(firstName, lastName, username, gender, department));
    } else if (requestObject.get("typeOfUser").equals("STAFF")) {
      accounts.addAll(staffRepository.searchAccountsRestrictive(firstName, lastName, username, gender, department));
    } else if (requestObject.get("typeOfUser").equals("STUDENT")) {
      accounts.addAll(studentRepository.searchAccountsRestrictive(firstName, lastName, username, gender, department));
    }
    List<Account> listAccounts = accounts.stream().toList();
    setPasswords(listAccounts);
    return listAccounts;

  }

  public Map<String, Object> getStats() {
    long amountOfStudents, amountOfStaff;
    amountOfStudents = studentRepository.count();
    amountOfStaff = staffRepository.count();
    Map<String, Object> studentNode = Map.of("name", "Students", "value", amountOfStudents);
    Map<String, Object> staffNode = Map.of("name", "Staff", "value", amountOfStaff);
    List<Map<String, Object>> accountTypeList = List.of(studentNode, staffNode);

    // Fetch student and staff counts
    List<Object[]> studentCounts = studentRepository.countStudentsByDepartmentGroup();
    List<Object[]> staffCounts = staffRepository.countStaffByDepartmentGroup();
    List<Map<String, Object>> departmentsList = new ArrayList<>();
    DepartmentEnum[] departments = DepartmentEnum.values();
    for (int i = 0; i < departments.length; i++) {
      long departmentAmountStudent = getAmountOfDepartment(studentCounts, departments[i]);
      long departmentAmountStaff = getAmountOfDepartment(staffCounts, departments[i]);
      long total = departmentAmountStaff + departmentAmountStudent;
      String departmentName = departments[i].getDepartmentName().split(" ")[0];
      Map<String, Object> singleDepartmentNode = Map.of("department", departmentName, "accounts", total);
      departmentsList.add(singleDepartmentNode);
    }
    Map<String, Object> response = Map.of("accountTypeData", accountTypeList, "departmentData", departmentsList);
    return response;
  }

  public long getAmountOfDepartment(List<Object[]> listOfPairs, DepartmentEnum departmentEnum) {
    // the pair is pair of DepartmentEnum and Amount (long)
    long result = 0;
    for (Object[] pair : listOfPairs) {
      DepartmentEnum currentdepartmentEnum = (DepartmentEnum) pair[0];
      if (currentdepartmentEnum.equals(departmentEnum)) {
        result = (Long) pair[1];
        ;
      }
    }
    return result;
  }

  public List<Account> getAllAccounts() {
    List<Account> accounts = accountRepository.findAll();
    return accounts;
  }

}
