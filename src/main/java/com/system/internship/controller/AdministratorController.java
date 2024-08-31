package com.system.internship.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.system.internship.dto.RegisterRequestBodyDto;
import com.system.internship.dto.RegisterRequestCustomBodyDto;
import com.system.internship.dto.RegisterResponseDto;
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

}
