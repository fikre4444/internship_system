package com.system.internship.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.system.internship.domain.Account;
import com.system.internship.domain.Notification;

import jakarta.transaction.Transactional;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

  List<Notification> findAllBySentBy(Account account);

  List<Notification> findAllBySentTo(Account account);

  List<Notification> findTop10BySentToOrderByIdDesc(Account account);

  @Transactional
  void deleteBySentBy(Account account);

  @Transactional
  void deleteBySentTo(Account account);

}
