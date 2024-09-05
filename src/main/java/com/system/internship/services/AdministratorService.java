package com.system.internship.services;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.system.internship.dto.*;
import com.system.internship.enums.TypeUserEnum;
import com.system.internship.repository.*;

import com.system.internship.services.strategy.AccountRegistrationStrategy;
import com.system.internship.services.strategy.StaffRegistrationStrategy;
import com.system.internship.services.strategy.StudentRegistrationStrategy;

@Service
public class AdministratorService {

  private final Map<TypeUserEnum, AccountRegistrationStrategy> strategies;

  // create the two strategies for registration (for student and staff)
  public AdministratorService(RestTemplate restTemplate, StaffRepository staffRepository,
      StudentRepository studentRepository, PasswordEncoder passwordEncoder,
      OpenPasswordRepository openPasswordRepository) {
    strategies = new HashMap<>();
    strategies.put(TypeUserEnum.STAFF, new StaffRegistrationStrategy(
        restTemplate, staffRepository, passwordEncoder, openPasswordRepository));
    strategies.put(TypeUserEnum.STUDENT, new StudentRegistrationStrategy(
        restTemplate, studentRepository, passwordEncoder, openPasswordRepository));
  }

  public RegisterResponseDto register(RegisterRequestBodyDto body) {
    RegisterResponseDto registerResponseDto = new RegisterResponseDto();
    try {
      RegisterRequestUtil.checkBodyIntegrity(body); // Validate request body
      URI uri = RegisterRequestUtil.getUriFromBody(body);
      // get registration strategy based on the type of user (whether student or
      // staff)
      AccountRegistrationStrategy strategy = strategies.get(body.getTypeUser());
      if (body.getAmount().equals(RegisterRequestBodyDto.AmountEnum.BATCH)) {
        registerResponseDto = strategy.registerBatch(uri);
      } else if (body.getAmount().equals(RegisterRequestBodyDto.AmountEnum.SINGLE)) {
        registerResponseDto = strategy.registerSingle(uri);
      }
    } catch (HttpClientErrorException exception) {
      registerResponseDto = RegisterResponseDto.builder().errorResponse(true).build();
    } catch (Exception ex) {
      registerResponseDto = RegisterResponseDto.builder().incorrectBody(true).build();
    }
    return registerResponseDto;
  }

  public RegisterResponseDto registerCustom(RegisterRequestCustomBodyDto registerDto) {
    RegisterResponseDto registerResponseDto = new RegisterResponseDto();
    // if body is incorrect set the response and return
    if (!RegisterRequestUtil.checkCustomBodyIntegrity(registerDto)) {
      registerResponseDto = RegisterResponseDto.builder().incorrectBody(true).build();
      return registerResponseDto;
    }
    // get strategy based on type of user
    AccountRegistrationStrategy strategy = strategies.get(registerDto.getTypeUser());
    registerResponseDto = strategy.registerCustom(registerDto);
    return registerResponseDto;
  }

}
