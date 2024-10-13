package com.system.internship.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.system.internship.filter.JwtFilter;
import com.system.internship.handler.CustomAccessDeniedHandler;
import com.system.internship.handler.CustomAuthenticationEntryPoint;
import com.system.internship.services.MyUserDetailsService;

import jakarta.servlet.http.HttpServletResponse;

@Configuration
public class SecurityConfig {

  @Autowired
  private MyUserDetailsService userDetailsService;

  @Autowired
  private JwtFilter jwtFilter;

  @Autowired
  private CustomAccessDeniedHandler customAccessDeniedHandler;

  @Autowired
  private CustomAuthenticationEntryPoint customAuthenticationEntryPoint;

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http = http.csrf(customzer -> customzer.disable())
        .cors(customizer -> customizer.disable())
        .httpBasic(customizer -> customizer.disable())
        .authorizeHttpRequests(customizer -> customizer
            .requestMatchers("/api/student/hello").hasRole("STUDENT")
            .requestMatchers("/api/staff/hello").hasRole("STAFF")
            .requestMatchers("/api/admin/**", "/api/auth/login", "/api/department-coordinator/**",
                "/api/head-coordinator/**")
            .permitAll()
            .requestMatchers("/api/account/forgot-password").permitAll()
            .requestMatchers("/api/account/**").authenticated()
            .requestMatchers("/api/email/**").permitAll())
        .exceptionHandling(customizer -> {
          customizer.authenticationEntryPoint(customAuthenticationEntryPoint);
          customizer.accessDeniedHandler(customAccessDeniedHandler);
        })
        .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
        .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

    return http.build();
  }

  @Bean
  public AuthenticationProvider authenticator() {
    DaoAuthenticationProvider dao = new DaoAuthenticationProvider();
    dao.setUserDetailsService(userDetailsService);
    dao.setPasswordEncoder(new BCryptPasswordEncoder());

    return dao;
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
    return config.getAuthenticationManager();
  }

}
