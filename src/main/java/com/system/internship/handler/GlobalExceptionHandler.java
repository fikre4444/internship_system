package com.system.internship.handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.system.internship.dto.ErrorResponse;
import com.system.internship.exception.UsernameNotFoundException;

@ControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(UsernameNotFoundException.class)
  public ResponseEntity<ErrorResponse> handleProductNotFoundException(UsernameNotFoundException ex) {
    ErrorResponse errorResponse = new ErrorResponse();
    errorResponse.setErrorType("USERNAME_NOT_FOUND");
    errorResponse.setMessage("The username you submitted wasn't found");
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handleGeneralException(Exception ex) throws Exception {
    if (ex instanceof org.springframework.security.core.AuthenticationException ||
        ex instanceof org.springframework.security.access.AccessDeniedException) {
      throw ex; // Let Spring Security handle these exceptions
    }
    ex.printStackTrace();
    System.out.println(ex);
    ErrorResponse errorResponse = new ErrorResponse();
    errorResponse.setErrorType("GENERAL_EXCEPTION_OCCURED");
    errorResponse.setMessage("Check the console for the actual exception trace.");
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
  }

}
