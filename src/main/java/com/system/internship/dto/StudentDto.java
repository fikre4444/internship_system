package com.system.internship.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentDto {

  private String firstName;
  private String lastName;
  private String username;
  private String gender;
  private String department;
  private String email;
  private String stream;
  private Float grade;

}
