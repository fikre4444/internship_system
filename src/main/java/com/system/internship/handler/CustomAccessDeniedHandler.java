package com.system.internship.handler;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.system.internship.dto.ErrorResponse;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

  @Override
  public void handle(HttpServletRequest request, HttpServletResponse response,
      AccessDeniedException accessDeniedException) throws IOException, ServletException {
    ErrorResponse errorResponse = new ErrorResponse();
    ObjectMapper objectMapper = new ObjectMapper();
    errorResponse.setErrorType("NOT_ALLOWED");
    errorResponse.setMessage("You are not allowed to access this endpoint");

    // Set response status and content type
    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
    response.setContentType("application/json");

    // Write the JSON response
    response.getWriter().write(objectMapper.writeValueAsString(errorResponse));

  }

}
