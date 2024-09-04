package com.system.internship.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/staff")
public class StaffController {

  @GetMapping("/hello")
  public String helloStaff() {
    return "Hello Staff";
  }

}
