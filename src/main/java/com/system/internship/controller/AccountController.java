package com.system.internship.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.system.internship.domain.Account;
import com.system.internship.dto.PasswordUpdateDto;
import com.system.internship.services.AccountService;

@RestController
@RequestMapping("/api/account")
public class AccountController {

  @Autowired
  private AccountService accountService;

  @GetMapping("/get-account")
  public ResponseEntity<?> getAccountDetails() {
    return ResponseEntity.ok(accountService.getAccountDto()); // pass an account service here
  }

  @PutMapping("/update-password")
  public ResponseEntity<?> updatePassword(@RequestBody PasswordUpdateDto passwordUpdateDto) {
    return ResponseEntity.ok(accountService.updatePassword(passwordUpdateDto));
  }

  @PutMapping("/forgot-password")
  public ResponseEntity<?> forgotPassword(@RequestParam String username) {
    return ResponseEntity.ok(accountService.forgotPassword(username));
  }

}
