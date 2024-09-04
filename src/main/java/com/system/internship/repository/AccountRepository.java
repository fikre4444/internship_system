package com.system.internship.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.system.internship.domain.Account;

import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {
  Optional<Account> findByUsername(String username);

  @Query("SELECT a FROM Account a WHERE a.username = :username AND TYPE(a) = Account")
  Optional<Account> findAccountByUsername(@Param("username") String username);
}
