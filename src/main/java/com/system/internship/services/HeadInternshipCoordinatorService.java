package com.system.internship.services;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.system.internship.domain.InternshipOpportunity;
import com.system.internship.dto.InternshipOpportunityDto;
import com.system.internship.enums.DepartmentEnum;
import com.system.internship.repository.InternshipOpportunityRepository;

import jakarta.persistence.Column;

@Service
public class HeadInternshipCoordinatorService {

  @Autowired
  private InternshipOpportunityRepository ioRepo;

  public Map<String, Object> saveInternshipOpportunity(InternshipOpportunityDto iod) {
    InternshipOpportunity io = convertToInternshipOpportunity(iod);
    InternshipOpportunity savedIo = ioRepo.save(io);
    return Map.of("result", "success", "message", "posted internship successfully.", "internshipOpportunity", savedIo);
  }

  public InternshipOpportunity convertToInternshipOpportunity(InternshipOpportunityDto iod) {
    InternshipOpportunity io = InternshipOpportunity.builder()
        .companyName(iod.getCompanyName()).department(iod.getDepartment())
        .location(iod.getLocation()).noOfStudents(iod.getNoOfStudents())
        .pocketMoney(iod.isPocketMoney()).internshipStatus(iod.getInternshipStatus())
        .typeOfInternship("MU_PROVIDED").build();
    return io;
  }

}
