package com.system.internship.config;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

public class InternetConnectivityCondition implements Condition {
  @Override
  public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
    try (Socket socket = new Socket()) {
      // Try to connect to Google's public DNS server
      socket.connect(new InetSocketAddress("8.8.8.8", 53), 2000); // Timeout of 2 seconds
      return true;
    } catch (IOException e) {
      return false; // No internet connection
    }
  }
}
