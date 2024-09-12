package com.system.internship.dto;

import com.system.internship.domain.Account;
import com.system.internship.enums.GenderEnum;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountDto {

  private Account account;
  private boolean passwordNeedChange;

}
