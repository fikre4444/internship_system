package com.system.internship;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.system.internship.dto.RegisterRequestBodyDto;
import com.system.internship.dto.RegisterResponseDto;
import com.system.internship.dto.RegisterRequestBodyDto.AmountEnum;
import com.system.internship.enums.TypeUserEnum;
import com.system.internship.services.AdministratorService;

@SpringBootTest
public class AdminServiceTest {

  @Autowired
  private AdministratorService adminService;

  @Test
  public void testBatchRegistration() {
    System.out.println("Running the test batch registeration of staff in checmical engineering which are 8 in number");
    RegisterRequestBodyDto registerBody = RegisterRequestBodyDto.builder()
        .amount(AmountEnum.BATCH).typeUser(TypeUserEnum.STAFF).department("Chemical Engineering").build();

    RegisterResponseDto response = adminService.register(registerBody);

    assertNotNull(response);
    assertEquals(response.getRegisteredStaffs().size(), 8);

  }

}
