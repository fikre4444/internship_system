package com.system.internship.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.system.internship.dto.InternshipOpportunityDto;

import java.util.List;
import java.util.Map;

@Service
public class CompanyFillerService {

  public Map<String, Object> postCompanyInternships(List<InternshipOpportunityDto> ioDtoList) {
    ioDtoList.forEach(ioDto -> {
      System.out.println(ioDto.toString());
    });
    return Map.of("result", "success", "message", "The Internships Have Been Successfully Saved!");
  }

}
