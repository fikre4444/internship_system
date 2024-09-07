package com.system.internship.services;

import java.util.concurrent.CompletableFuture;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {

  @Autowired
  JavaMailSender javaMailSender;

  public CompletableFuture<Void> sendEmail(String to, String subject, String text) {
    return CompletableFuture.runAsync(() -> {
      try {
        if (!validateEmail(to)) { // if it's not valid
          throw new MessagingException();
        }
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(text, true);

        javaMailSender.send(message);

        // Log success to the console
        System.out.println("Email sent successfully to: " + to);

      } catch (MessagingException e) {
        // Log failure to the console
        System.out.println("Error occurred while sending email to " + to + ": " + e.getMessage());
        throw new RuntimeException("Failed to send email to " + to, e);
      }
    }).exceptionally(ex -> {
      // Log the exception to the console
      System.out.println("Error sending email: " + to);
      return null;
    });

  }

  private boolean validateEmail(String email) {
    if (email == null)
      return false;
    if (email.equals(""))
      return false;
    String EMAIL_PATTERN = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
    Pattern pattern = Pattern.compile(EMAIL_PATTERN);
    Matcher matcher = pattern.matcher(email);
    return matcher.matches();
  }

}
