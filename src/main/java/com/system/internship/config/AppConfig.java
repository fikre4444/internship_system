package com.system.internship.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import com.system.internship.util.NetworkUtils;

@Configuration
public class AppConfig {

  @Bean
  public RestTemplate restTemplate() {
    return new RestTemplate();
  }

  @Bean
  public String baseUrl() {
    String ipAddress = NetworkUtils.getLocalIpAddress();
    return "http://" + ipAddress + ":5173";
  }

}
