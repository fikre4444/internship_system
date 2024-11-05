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
import java.util.Map;
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
        .sentTo(sentTo).content(content).createdDate(rightNow).status("unread").build();
    notificationRepository.save(notification);
    return true;

  }

  public List<Notification> getNotifications() {
    Account account = (Account) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    List<Notification> notificationList = notificationRepository.findTop10BySentToOrderByIdDesc(account);
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
          .sentTo(account).content(content).createdDate(rightNow).status("unread").build();
      notifications.add(notification);
    });
    notificationRepository.saveAll(notifications);
    return true;
  }

  public Map<String, Object> markNotificationAsRead(Long notificationId) {
    Optional<Notification> notificationOpt = notificationRepository.findById(notificationId);
    if (notificationOpt.isPresent()) {
      Notification notification = notificationOpt.get();
      notification.setStatus("read"); // mark that specific notification as read
      notificationRepository.save(notification); // save the edited one
      return Map.of("result", "success", "message", "The notification has been mark as read successfully!");
    }
    return Map.of("result", "error", "message", "The notification was not found.");
  }

  public Map<String, Object> markAllAsRead(List<Long> notificationIds) {
    List<Notification> notifications = notificationRepository.findAllById(notificationIds);
    if (notifications.size() < 1) {
      return Map.of("result", "error", "message", "No notification by these Ids has been found.");
    }
    notifications.forEach(notification -> {
      notification.setStatus("read");
    });
    notificationRepository.saveAll(notifications);
    return Map.of("result", "success", "message", "All Notifications have been set as read successfully");
  }

}
