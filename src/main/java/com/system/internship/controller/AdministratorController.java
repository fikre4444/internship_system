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
    administratorService.sendEmails(usernames);
    return ResponseEntity.ok(null);
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

  @DeleteMapping("/delete-account")
  public ResponseEntity<?> deleteAccount(@RequestParam String username) {
    Account acc = administratorService.deleteAccount(username);
    if (acc == null) {
      HttpHeaders headers = new HttpHeaders();
      headers.add("Error-Message", "Username not found");
      return ResponseEntity.status(HttpStatus.NOT_FOUND).headers(headers).build();
    }
    return ResponseEntity.ok(acc);
  }

  @DeleteMapping("/delete-accounts-by-department")
  public ResponseEntity<?> deleteAccountsByDepartment(@RequestBody Map<String, String> request) {
    // should use "department" and "typeUser" as the two arguments to get from the
    // json body
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

}
