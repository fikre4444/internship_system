package com.system.internship.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.system.internship.domain.InternshipOpportunity;
import com.system.internship.dto.InternshipOpportunityDto;
import com.system.internship.enums.DepartmentEnum;
import com.system.internship.repository.InternshipOpportunityRepository;
import com.system.internship.util.HashUtil;

@Service
public class HeadInternshipCoordinatorService {

  @Autowired
  private InternshipOpportunityRepository ioRepo;

  public Map<String, Object> saveInternshipOpportunity(InternshipOpportunityDto iod) {
    InternshipOpportunity io = convertToInternshipOpportunity(iod);
    InternshipOpportunity savedIo = ioRepo.save(io);
    return Map.of("result", "success", "message", "posted internship successfully.", "internshipOpportunity", savedIo);
  }

  public Map<String, Object> saveInternshipOpportunities(List<InternshipOpportunityDto> iodList) {
    List<InternshipOpportunity> ioList = convertToInternshipOpportunities(iodList);
    List<InternshipOpportunity> savedIos = ioRepo.saveAll(ioList);
    return Map.of("result", "success", "message", "posted internship successfully.", "internshipOpportunities",
        savedIos);
  }

  public List<InternshipOpportunity> convertToInternshipOpportunities(List<InternshipOpportunityDto> iodList) {
    return iodList.stream().map(iod -> convertToInternshipOpportunity(iod)).collect(Collectors.toList());
  }

  public InternshipOpportunity convertToInternshipOpportunity(InternshipOpportunityDto iod) {
    InternshipOpportunity io = InternshipOpportunity.builder()
        .companyName(iod.getCompanyName()).department(iod.getDepartment())
        .location(iod.getLocation()).noOfStudents(iod.getNoOfStudents())
        .pocketMoney(iod.isPocketMoney()).internshipStatus(iod.getInternshipStatus())
        .typeOfInternship("MU_PROVIDED")
        .uniqueIdentifier(
            HashUtil.generateHashFromInternshipOpportunityMU(
                iod.getCompanyName(),
                iod.getLocation(),
                iod.getDepartment()))
        .build();
    return io;
  }

}
