package com.system.internship.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.system.internship.dto.InternshipOpportunityDto;
import com.system.internship.services.CompanyFillerService;
import java.util.List;

@RestController
@RequestMapping("/api/company-filler")
public class CompanyFillerController {

  @Autowired
  private CompanyFillerService companyFillerService;

  @GetMapping("/hello")
  public String hello() {
    return "Hello";
  }

  @PostMapping("/post-company-interships")
  public ResponseEntity<?> postCompanyInternships(@RequestBody List<InternshipOpportunityDto> iodList) {
    return ResponseEntity.ok(companyFillerService.postCompanyInternships(iodList));
  }

}
