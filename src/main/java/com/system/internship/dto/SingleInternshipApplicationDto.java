package com.system.internship.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SingleInternshipApplicationDto {

  private String internshipOpportunityUniqueIdentifier;
  private int priority;

}
