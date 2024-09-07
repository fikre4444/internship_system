package com.system.internship.dto;

import com.system.internship.enums.TypeUserEnum;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RegisterRequestBodyDto {
  private String department;
  private AmountEnum amount; // whether by batch or sinler
  private TypeUserEnum typeUser; // whether student or staff
  private String username;

  public enum AmountEnum {
    BATCH, SINGLE
  }

}
