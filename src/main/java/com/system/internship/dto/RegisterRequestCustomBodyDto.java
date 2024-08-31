package com.system.internship.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegisterRequestCustomBodyDto {

  private TypeOfUser typeOfUser; // whether staff or student

  private String firstName;
  private String lastName;
  private String username;
  private String gender;
  private String email; // optional

  // these will be filled based on the type of user
  private String department;
  private Float courseLoad;
  private String stream;
  private Float grade;

  public enum TypeOfUser {
    STAFF, STUDENT
  }

}
