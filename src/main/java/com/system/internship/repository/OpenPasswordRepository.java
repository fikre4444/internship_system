package com.system.internship.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.system.internship.domain.Account;
import com.system.internship.domain.OpenPassword;

import java.util.List;
import java.util.Optional;

public interface OpenPasswordRepository extends JpaRepository<OpenPassword, Long> {

  Optional<OpenPassword> findByAccount(Account account);

  List<OpenPassword> findByAccountIn(List<? extends Account> accounts);

}
