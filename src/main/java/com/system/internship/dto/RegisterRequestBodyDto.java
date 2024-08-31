package com.system.internship.dto;

import lombok.Data;

@Data
public class RegisterRequestBodyDto {
  private String department;
  private AmountEnum amount; // whether by batch or sinler
  private TypeUserEnum typeUser; // whether student or staff
  private String username;

  public enum AmountEnum {
    BATCH, SINGLE
  }

  public enum TypeUserEnum {
    STAFF, STUDENT
  }

}
