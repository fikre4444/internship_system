package com.system.internship.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InternshipChangeRequestDto {

  private String studentUsername;
  private String internshipOpportunityUniqueIdentifier;
  private int priority;

}
