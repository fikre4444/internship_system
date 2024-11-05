package com.system.internship.controller;

import java.util.Optional;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;

import com.system.internship.domain.Account;
import com.system.internship.dto.EnablenessRequest;
import com.system.internship.dto.RegisterRequestBodyDto;
import com.system.internship.dto.RegisterRequestCustomBodyDto;
import com.system.internship.dto.RegisterResponseDto;
import com.system.internship.dto.RoleRequest;
import com.system.internship.dto.TableDto;
import com.system.internship.enums.DepartmentEnum;
import com.system.internship.enums.RoleEnum;
import com.system.internship.repository.AccountRepository;
import com.system.internship.services.AdministratorService;

@RestController
@RequestMapping("/api/admin/")
public class AdministratorController {

  @Autowired
  private AdministratorService administratorService;

  @GetMapping("/hello")
  public String sayHello() {
    return "Hello";
  }

  @PostMapping("/register")
  public ResponseEntity<?> RegisterUsers(@RequestBody RegisterRequestBodyDto registerDto) {
    RegisterResponseDto registerResponseDto = administratorService.register(registerDto);
    return ResponseEntity.ok(registerResponseDto);
  }

  @PostMapping("/registerCustom")
  public ResponseEntity<?> RegisterUserCustom(@RequestBody RegisterRequestCustomBodyDto registerDto) {
    RegisterResponseDto registerResponseDto = administratorService.registerCustom(registerDto);
    return ResponseEntity.ok(registerResponseDto);
  }

  @PostMapping("/notify-through-email")
  public ResponseEntity<?> sendEmail(@RequestBody List<String> usernames) {
    return ResponseEntity.ok(administratorService.sendEmails(usernames));
  }

  @PutMapping("/add-role")
  public ResponseEntity<?> addRole(@RequestBody RoleRequest roleRequest) {
    return ResponseEntity.ok(administratorService.addRole(roleRequest.getUsername(), roleRequest.getRole()));
  }

  @PutMapping("/remove-role")
  public ResponseEntity<?> removeRole(@RequestBody RoleRequest roleRequest) {
    return ResponseEntity.ok(administratorService.removeRole(roleRequest.getUsername(), roleRequest.getRole()));
  }

  @PutMapping("/set-enableness")
  public ResponseEntity<?> setEnableness(@RequestBody EnablenessRequest request) {
    return ResponseEntity.ok(administratorService.setEnableness(request.getUsername(), request.getEnabled()));
  }

  @PutMapping("/reset-password")
  public ResponseEntity<?> resetAccountPassword(@RequestParam String username) {
    System.out.println("the username is " + username);
    return ResponseEntity.ok(administratorService.resetAccountPassword(username));
  }

  @GetMapping("/get-accounts-by-username")
  public ResponseEntity<?> getAccountsByUsername(@RequestParam String username) {
    return ResponseEntity.ok(administratorService.getAccountsByUsername(username));
  }

  @GetMapping("/simple-search")
  public ResponseEntity<?> getAccountBySearchTerm(@RequestParam String searchTerm) {
    return ResponseEntity.ok(administratorService.getAccountsBySearchTerm(searchTerm));
  }

  @GetMapping("/get-account")
  public ResponseEntity<?> getAccount(@RequestParam String username) {
    Account acc = administratorService.getAccount(username);
    if (acc == null) {
      HttpHeaders headers = new HttpHeaders();
      headers.add("Error-Message", "Username not found");
      return ResponseEntity.status(HttpStatus.NOT_FOUND).headers(headers).build();
    }
    return ResponseEntity.ok(acc);
  }

  @GetMapping("/get-accounts-by-department")
  public ResponseEntity<?> getStudents(@RequestBody Map<String, String> request) {
    // should use "department" and "typeUser" as the two arguments to get from the
    // json body
    String department = request.get("department");
    String typeUser = request.get("typeUser");
    try {
      return ResponseEntity.ok(administratorService.getAccountsByDepartment(department, typeUser));
    } catch (Exception ex) {
      HttpHeaders headers = new HttpHeaders();
      headers.add("Error-Message", ex.getMessage());
      return ResponseEntity.status(HttpStatus.NOT_FOUND).headers(headers).build();
    }
  }

  @PutMapping("/change-department")
  public ResponseEntity<?> changeDepartment(@RequestBody Map<String, String> request) {
    String username = request.get("username");
    DepartmentEnum departmentEnum = DepartmentEnum.valueOf(request.get("department"));
    return ResponseEntity.ok(administratorService.changeDepartment(username, departmentEnum));
  }

  @DeleteMapping("/delete-account")
  public ResponseEntity<?> deleteAccount(@RequestParam String username) {
    return ResponseEntity.ok(administratorService.deleteAccount(username));
  }

  @DeleteMapping("/delete-accounts-by-department")
  public ResponseEntity<?> deleteAccountsByDepartment(@RequestBody Map<String, String> request) {
    // should use "department" and "typeUser" as the two arguments to get from the
    // json body
    System.out.println("here in the delete something");
    String department = request.get("department");
    String typeUser = request.get("typeUser");
    try {
      return ResponseEntity.ok(administratorService.deleteAccountsByDepartment(department, typeUser));
    } catch (Exception ex) {
      HttpHeaders headers = new HttpHeaders();
      headers.add("Error-Message", ex.getMessage());
      return ResponseEntity.status(HttpStatus.NOT_FOUND).headers(headers).build();
    }
  }

  @PostMapping("/complex-search/inclusive")
  public ResponseEntity<?> complexSearchInclusive(@RequestBody Map<String, String> requestBody) {
    return ResponseEntity.ok(administratorService.searchInclusive(requestBody));
  }

  @PostMapping("/complex-search/restrictive")
  public ResponseEntity<?> complexSearchRestrictive(@RequestBody Map<String, String> requestBody) {
    return ResponseEntity.ok(administratorService.searchRestrictive(requestBody));

  }

  @GetMapping("/get-stats")
  public ResponseEntity<?> getStats() {
    return ResponseEntity.ok(administratorService.getStats());
  }

  // @GetMapping("/get-all-accounts")
  // public ResponseEntity<?> getAllAccounts() {
  // return ResponseEntity.ok(administratorService.getAllAccounts());
  // }

  @GetMapping("/get-all-accounts")
  public ResponseEntity<?> getAllAccounts(
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size) {
    return ResponseEntity.ok(administratorService.getAllAccounts(page, size));
  }

  @PostMapping("/register-students-by-table")
  public ResponseEntity<?> registerStudentsByTable(@RequestBody List<RegisterRequestCustomBodyDto> rows) {
    return ResponseEntity.ok(administratorService.registerStudentByTable(rows));
  }

  @PostMapping("/register-staff-by-table")
  public ResponseEntity<?> registerStaffByTable(@RequestBody List<RegisterRequestCustomBodyDto> rows) {
    return ResponseEntity.ok(administratorService.registerStaffByTable(rows));
  }

}
