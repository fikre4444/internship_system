package com.system.internship.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.system.internship.domain.Account;

import java.util.Optional;
import java.util.List;

public interface AccountRepository extends JpaRepository<Account, Long> {
  Optional<Account> findByUsername(String username);

  List<Account> findAllByUsernameIn(List<String> usernames);

  List<Account> findByUsernameContainingIgnoreCase(String username);

  List<Account> findByFirstNameContainingIgnoreCase(String firstName);

  List<Account> findByLastNameContainingIgnoreCase(String lastName);
}
