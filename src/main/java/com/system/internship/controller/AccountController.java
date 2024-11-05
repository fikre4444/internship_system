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
import com.system.internship.dto.LoginDto;
import com.system.internship.dto.PasswordUpdateDto;
import com.system.internship.services.AccountService;
import com.system.internship.services.NotificationService;

@RestController
@RequestMapping("/api/account")
public class AccountController {

  @Autowired
  private AccountService accountService;

  @Autowired
  private NotificationService notificationService;

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

  @PutMapping("/reset-password")
  public ResponseEntity<?> resetPassword(@RequestBody LoginDto loginDto) {
    System.out.println(loginDto.getUsername());
    System.out.println(loginDto.getPassword());
    return ResponseEntity.ok(accountService.resetPasswordThroughEmail(loginDto.getUsername(), loginDto.getPassword()));

  }

  @GetMapping("/check-telegram-registeration")
  public ResponseEntity<?> checkTelegramRegisteration(@RequestParam String username) {
    return ResponseEntity.ok(accountService.checkTelegramRegisteration(username));
  }

  @GetMapping("/get-notifications")
  public ResponseEntity<?> getNotifications() {
    return ResponseEntity.ok(notificationService.getNotifications());
  }

}
