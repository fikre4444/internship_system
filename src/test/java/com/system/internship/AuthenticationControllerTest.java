package com.system.internship;

import com.system.internship.controller.AuthController;
import com.system.internship.dto.LoginDto;
import com.system.internship.services.AuthService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

public class AuthenticationControllerTest {

  @InjectMocks
  private AuthController authenticationController;

  @Mock
  private AuthService authService;

  public AuthenticationControllerTest() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  public void testLoginSuccessful() {
    // Arrange
    LoginDto loginDto = new LoginDto("user", "password");
    String expectedToken = "generated-jwt-token";

    when(authService.authenticateAccount(loginDto)).thenReturn(expectedToken);

    // Act
    ResponseEntity<?> response = authenticationController.login(loginDto);

    // Assert
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(expectedToken, response.getBody());
  }
}
