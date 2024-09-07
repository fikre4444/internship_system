package com.system.internship.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.system.internship.dto.EmailDetailsDto;

import java.util.List;

@Service
public class BulkEmailService {

  @Autowired
  private EmailService emailService;

  @Async
  public void sendBulkEmails(List<EmailDetailsDto> emailDetailsList) {
    emailDetailsList.forEach(emailDetails -> {
      emailService.sendEmail(emailDetails.getTo(), emailDetails.getSubject(), emailDetails.getText());
    });
  }
}
