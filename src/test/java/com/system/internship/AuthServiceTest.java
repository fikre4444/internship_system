package com.system.internship;

import com.system.internship.dto.LoginDto;
import com.system.internship.services.AuthService;
import com.system.internship.services.JwtService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class AuthServiceTest {

  @InjectMocks
  private AuthService authService;

  @Mock
  private AuthenticationManager authManager;

  @Mock
  private JwtService jwtService;

  @Mock
  private Authentication authentication;

  @BeforeEach
  public void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  public void testAuthenticateAccountSuccessful() {
    // Arrange
    LoginDto loginDto = new LoginDto("user", "password");
    String expectedToken = "generated-jwt-token";

    when(authManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
        .thenReturn(authentication);
    when(authentication.isAuthenticated()).thenReturn(true);
    when(jwtService.generateToken(loginDto.getUsername())).thenReturn(expectedToken);

    // Act
    String result = authService.authenticateAccount(loginDto);

    // Assert with custom message
    assertEquals(expectedToken, result, "The token returned should match the expected generated token.");
    verify(authManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
    verify(jwtService, times(1)).generateToken(loginDto.getUsername());
  }
}
