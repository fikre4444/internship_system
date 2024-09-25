package com.system.internship.handler;

import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.system.internship.dto.ErrorResponse;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

  @Override
  public void commence(HttpServletRequest request,
      HttpServletResponse response,
      AuthenticationException authException) throws IOException {

    String expiredMessage = (String) request.getAttribute("expired");
    ObjectMapper objectMapper = new ObjectMapper();
    // Create a response body
    ErrorResponse errorResponse = new ErrorResponse();

    if (expiredMessage != null) {
      // Token has expired
      errorResponse.setErrorType("JWT_EXPIRED");
      errorResponse.setMessage("Please Login again");
    } else if (authException.getClass().getSimpleName().equals("BadCredentialsException")) {
      // The login credentials are incorrect
      errorResponse.setErrorType("INVALID_CREDENTIALS");
      errorResponse.setMessage("The credentials you have input are incorrect.");
    } else if (authException instanceof DisabledException || request.getAttribute("disabled") != null) {
      // the account is disabled
      errorResponse.setErrorType("DISABLED_ACCOUNT");
      errorResponse.setMessage("Your account has been disabled.");
    } else {
      // Other authentication errors
      errorResponse.setErrorType("UNAUTHORIZED");
      errorResponse.setMessage("Unauthorized access");
    }

    // Set response properties
    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    response.setContentType("application/json");

    // Write JSON response body
    response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
  }
}
