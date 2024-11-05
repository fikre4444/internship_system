package com.system.internship.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.system.internship.domain.Account;
import com.system.internship.domain.Notification;
import com.system.internship.domain.Student;
import com.system.internship.domain.TemporaryPlacement;
import com.system.internship.repository.AccountRepository;
import com.system.internship.repository.NotificationRepository;
import com.system.internship.repository.TemporaryPlacementRepository;

import jakarta.persistence.PreRemove;

@Component
public class AccountListener {

  private static AccountRepository accountRepository;
  private static TemporaryPlacementRepository temporaryPlacementRepository;
  private static NotificationRepository notificationRepository;

  @Autowired
  public void setAccountRepository(AccountRepository repository) {
    AccountListener.accountRepository = repository;
  }

  @Autowired
  public void setTemporaryPlacementRepository(TemporaryPlacementRepository temporaryPlacementRepository) {
    AccountListener.temporaryPlacementRepository = temporaryPlacementRepository;
  }

  @Autowired
  public void setNotificationRepository(NotificationRepository notificationRepository) {
    AccountListener.notificationRepository = notificationRepository;
  }

  @PreRemove
  public void saySomething(Account account) {
    System.out.println("Deleting aint gonna happen.");
    // notificationRepository.deleteByAccount(account);
    notificationRepository.deleteBySentBy(account);
    notificationRepository.deleteBySentTo(account);
  }

}
