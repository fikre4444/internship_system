package com.system.internship.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.system.internship.domain.CompanyFilledInternshipOpportunity;
import com.system.internship.domain.InternshipOpportunity;
import com.system.internship.dto.InternshipOpportunityDto;
import com.system.internship.repository.CompanyFilledInternshipOpportunityRepository;
import com.system.internship.util.HashUtil;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CompanyFillerService {

  @Autowired
  CompanyFilledInternshipOpportunityRepository cfioRepo;

  public Map<String, Object> postCompanyInternships(List<InternshipOpportunityDto> ioDtoList) {
    ioDtoList.forEach(ioDto -> {
      System.out.println(ioDto.toString());
    });
    List<CompanyFilledInternshipOpportunity> cfio = convertToCFInternshipOpportunities(ioDtoList);
    cfioRepo.saveAll(cfio);
    return Map.of("result", "success", "message", "The Internships Have Been Successfully Saved!");
  }

  public List<CompanyFilledInternshipOpportunity> convertToCFInternshipOpportunities(
      List<InternshipOpportunityDto> iodList) {
    return iodList.stream().map(iod -> convertToCFInternshipOpportunity(iod)).collect(Collectors.toList());
  }

  public CompanyFilledInternshipOpportunity convertToCFInternshipOpportunity(InternshipOpportunityDto iod) {
    CompanyFilledInternshipOpportunity io = CompanyFilledInternshipOpportunity.builder()
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
