package com.system.internship.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.system.internship.domain.Account;
import com.system.internship.domain.OpenPassword;

import java.util.List;
import java.util.Optional;

public interface OpenPasswordRepository extends JpaRepository<OpenPassword, Long> {

  Optional<OpenPassword> findByAccount(Account account);

  List<OpenPassword> findByAccountIn(List<? extends Account> accounts);

  @Modifying
  @Query("DELETE FROM OpenPassword op WHERE op.account IN :accounts")
  void deleteByAccountIn(@Param("accounts") List<? extends Account> accounts);

}
