package com.system.internship.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.system.internship.dto.InternshipApplicationDto;
import com.system.internship.services.StudentService;

@RestController
@RequestMapping("/api/student")
public class StudentController {

  @Autowired
  private StudentService studentService;

  @GetMapping("/hello")
  public String helloStudent() {
    return "Hello Student";
  }

  @GetMapping("/get-student")
  public ResponseEntity<?> getStudent(@RequestParam String username) {
    return ResponseEntity.ok(studentService.getStudent(username));
  }

  @PostMapping("/apply-internships")
  public ResponseEntity<?> applyInternships(@RequestBody InternshipApplicationDto applications) {
    return ResponseEntity.ok(studentService.applyInternships(applications));
  }

}
