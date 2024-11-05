package com.system.internship.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.system.internship.domain.Account;
import com.system.internship.domain.Notification;
import com.system.internship.exception.UsernameNotFoundException;
import com.system.internship.repository.AccountRepository;
import com.system.internship.repository.NotificationRepository;

import java.util.Optional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;

@Service
public class NotificationService {

  @Autowired
  private NotificationRepository notificationRepository;

  @Autowired
  private AccountRepository accountRepository;

  public boolean sendNotificationToSingle(Account sentBy, Account sentTo, String content) {

    LocalDateTime rightNow = LocalDateTime.now();
    Notification notification = Notification.builder().sentBy(sentBy)
        .sentTo(sentTo).content(content).createdDate(rightNow).status(content).build();
    notificationRepository.save(notification);
    return true;

  }

  public List<Notification> getNotifications() {
    Account account = (Account) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    List<Notification> notificationList = notificationRepository.findAllBySentTo(account);
    notificationList.forEach(notification -> {
      notification.setSentTo(null);
    });
    return notificationList;
  }

  public boolean sendNotificationsToMultiple(Account sentBy, List<? extends Account> sentTo, String content) {
    List<Notification> notifications = new ArrayList<>();

    sentTo.forEach(account -> {
      LocalDateTime rightNow = LocalDateTime.now();
      Notification notification = Notification.builder().sentBy(sentBy)
          .sentTo(account).content(content).createdDate(rightNow).status(content).build();
      notifications.add(notification);
    });
    notificationRepository.saveAll(notifications);
    return true;
  }

}
