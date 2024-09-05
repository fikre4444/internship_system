package com.system.internship.services;

import java.net.URI;

import org.springframework.web.util.UriComponentsBuilder;

import com.system.internship.dto.RegisterRequestBodyDto;
import com.system.internship.dto.RegisterRequestCustomBodyDto;
import com.system.internship.enums.TypeUserEnum;

public class RegisterRequestUtil {

  public static void checkBodyIntegrity(RegisterRequestBodyDto body) throws Exception {
    if (body.getAmount().equals(RegisterRequestBodyDto.AmountEnum.BATCH)) {
      if (body.getDepartment() != null && body.getTypeUser() != null) {
        return;
      }
    }
    if (body.getAmount().equals(RegisterRequestBodyDto.AmountEnum.SINGLE)) {
      if (body.getUsername() != null && body.getTypeUser() != null) {
        return;
      }
    }
    throw new Exception();
  }

  public static URI getUriFromBody(RegisterRequestBodyDto body) {
    String baseUrl = "http://localhost:7000/api";
    URI uri;

    // get the whether student or staff
    if (body.getTypeUser().equals(TypeUserEnum.STAFF)) {
      baseUrl += "/staff";
    } else if (body.getTypeUser().equals(TypeUserEnum.STUDENT)) {
      baseUrl += "/student";
    }

    // get whether single (by username ) or department
    if (body.getAmount().equals(RegisterRequestBodyDto.AmountEnum.BATCH)) {
      baseUrl += "/departments";
      uri = UriComponentsBuilder.fromHttpUrl(baseUrl)
          .queryParam("departmentName", body.getDepartment())
          .build()
          .toUri();
    } else if (body.getAmount().equals(RegisterRequestBodyDto.AmountEnum.SINGLE)) {
      baseUrl += "/byusername";
      uri = UriComponentsBuilder.fromHttpUrl(baseUrl)
          .queryParam("username", body.getUsername())
          .build()
          .toUri();
    } else {
      uri = UriComponentsBuilder.fromHttpUrl("").build().toUri();
    }

    return uri;
  }

  public static boolean checkCustomBodyIntegrity(RegisterRequestCustomBodyDto registerDto) {
    System.out.println("the dto is " + registerDto);
    if (registerDto.getTypeUser() == null)
      return false;
    if (registerDto.getFirstName() == null || registerDto.getFirstName().equals(""))
      return false;
    if (registerDto.getLastName() == null || registerDto.getLastName().equals(""))
      return false;
    if (registerDto.getUsername() == null || registerDto.getUsername().equals(""))
      return false;
    if (registerDto.getGender() == null || registerDto.getGender().equals(""))
      return false;
    if (registerDto.getTypeUser().equals(TypeUserEnum.STAFF)) {
      if (registerDto.getDepartment() == null || registerDto.getDepartment().equals(""))
        return false;
      if (registerDto.getCourseLoad() == null)
        return false;
    }
    if (registerDto.getTypeUser().equals(TypeUserEnum.STUDENT)) {
      if (registerDto.getDepartment() == null || registerDto.getDepartment().equals(""))
        return false;
      if (registerDto.getGrade() == null)
        return false;
    }

    return true;

  }

}
