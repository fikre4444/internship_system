package com.system.internship.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.system.internship.services.AdministratorService;
//import com.system.internship.services.DepartmentInternshipCoordinatorService;
import com.system.internship.services.DepartmentInternshipCoordinatorService;

@RestController
@RequestMapping("/api/department-coordinator")
public class DepartmentInternshipCoordinatorController {

  @Autowired
  AdministratorService administratorService;

  @Autowired
  DepartmentInternshipCoordinatorService departmentCoordinatorService;

  @GetMapping("/hello")
  public String sayHello() {
    return "Hello";
  }

  @GetMapping("/get-student")
  public ResponseEntity<?> getStudent(@RequestParam String username) {
    return ResponseEntity.ok(administratorService.getAccount(username));
  }

  @PostMapping("/add-self-internship")
  public ResponseEntity<?> addSelfInternship(@RequestBody Map<String, String> requestBody) {
    return ResponseEntity.ok(departmentCoordinatorService.addSelfInternship(requestBody));
  }

  @GetMapping("/get-students")
  public ResponseEntity<?> getStudents(@RequestParam String searchTerm) {
    return ResponseEntity.ok(departmentCoordinatorService.getStudents(searchTerm));
  }

}
