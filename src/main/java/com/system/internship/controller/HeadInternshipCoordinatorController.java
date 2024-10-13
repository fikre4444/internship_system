package com.system.internship.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.system.internship.domain.InternshipOpportunity;
import com.system.internship.dto.InternshipOpportunityDto;
import com.system.internship.services.HeadInternshipCoordinatorService;

@RestController
@RequestMapping("/api/head-coordinator")
public class HeadInternshipCoordinatorController {

  @Autowired
  private HeadInternshipCoordinatorService hics;

  @GetMapping("/hello")
  public String sayHello() {
    return "hello";
  }

  @PostMapping("/post-internship-opportunity")
  public ResponseEntity<?> postInternship(@RequestBody InternshipOpportunityDto iod) {

    return ResponseEntity.ok(hics.saveInternshipOpportunity(iod));

  }

}
