package com.system.internship.dto;

import com.system.internship.enums.DepartmentEnum;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class InternshipOpportunityDto {

  private String companyName;
  private String location;
  private DepartmentEnum department;
  private Integer noOfStudents;
  private String typeOfInternship;
  private boolean pocketMoney;
  private String internshipStatus;

}
