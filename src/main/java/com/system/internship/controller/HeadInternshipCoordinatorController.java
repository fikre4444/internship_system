package com.system.internship.controller;

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

import com.system.internship.domain.InternshipOpportunity;
import com.system.internship.dto.InternshipChangeRequestDto;
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

  @PostMapping("/post-internship-opportunities")
  public ResponseEntity<?> postInternships(@RequestBody List<InternshipOpportunityDto> iodList) {
    return ResponseEntity.ok(hics.saveInternshipOpportunities(iodList));
  }

  @PostMapping("/apply-internships")
  public ResponseEntity<?> assignInternships(@RequestParam String department) {
    return ResponseEntity.ok(hics.assignInternships(department));
  }

  @GetMapping("/check-all-students-applied")
  public ResponseEntity<?> checkAllStudentsApplied(@RequestParam String department) {
    return ResponseEntity.ok(hics.checkAllStudentsApplied(department));
  }

  @PutMapping("/confirm-placements")
  public ResponseEntity<?> confirmPlacements(@RequestParam String department) {
    return ResponseEntity.ok(hics.confirmPlacements(department));
  }

  @PutMapping("/apply-changes-to-placements")
  public ResponseEntity<?> applyChanges(@RequestBody List<InternshipChangeRequestDto> listOfChanges) {
    return ResponseEntity.ok(hics.applyPlacementChanges(listOfChanges));
  }

}
