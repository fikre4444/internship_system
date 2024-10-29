package com.system.internship.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

import com.system.internship.domain.CompanyFilledInternshipOpportunity;
import com.system.internship.domain.InternshipOpportunity;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InternshipApprovalDto {

  private List<InternshipOpportunity> approved;
  private List<CompanyFilledInternshipOpportunity> rejected;

}
