package com.system.internship.controller;

import java.util.Optional;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.system.internship.domain.Account;
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

}
