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

}
