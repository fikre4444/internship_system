package com.system.internship.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.system.internship.domain.Account;

import java.util.Optional;
import java.util.List;

public interface AccountRepository extends JpaRepository<Account, Long> {
  Optional<Account> findByUsername(String username);

  List<Account> findAllByUsernameIn(List<String> usernames);

  List<Account> findByUsernameContainingIgnoreCase(String username);

  List<Account> findByFirstNameContainingIgnoreCase(String firstName);

  List<Account> findByLastNameContainingIgnoreCase(String lastName);

  @Query("""
        SELECT a FROM Account a
        LEFT JOIN Staff s ON a.id = s.id
        LEFT JOIN Student st ON a.id = st.id
        WHERE LOWER(a.firstName) LIKE LOWER(CONCAT('%', :searchTerm, '%'))
        OR LOWER(a.lastName) LIKE LOWER(CONCAT('%', :searchTerm, '%'))
        OR LOWER(a.username) LIKE LOWER(CONCAT('%', :searchTerm, '%'))
        OR LOWER(s.department) LIKE LOWER(CONCAT('%', :searchTerm, '%'))
        OR LOWER(st.department) LIKE LOWER(CONCAT('%', :searchTerm, '%'))
      """)
  List<Account> findBySearchTerm(String searchTerm);
}
