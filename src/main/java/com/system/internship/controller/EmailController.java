package com.system.internship.controller;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.system.internship.dto.EmailDetailsDto;
import com.system.internship.services.BulkEmailService;
import com.system.internship.services.EmailService;

@RestController
@RequestMapping("/api/email")
public class EmailController {

  @Autowired
  private EmailService emailService;

  @Autowired
  private BulkEmailService bulkEmailService;

  @PostMapping("/send")
  public String sendEmail(@RequestBody EmailDetailsDto edto) {
    emailService.sendEmail(edto.getTo(), edto.getSubject(), edto.getText());
    return "Email sent!";
  }

  @PostMapping("/send-bulk-emails")
  public String sendBulkEmails(@RequestBody List<EmailDetailsDto> emailDetailsList) {
    bulkEmailService.sendBulkEmails(emailDetailsList);
    return "Bulk email sending started. You will receive logs for each email.";
  }

}
