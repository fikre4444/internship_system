package com.system.internship.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.system.internship.domain.Staff;

import java.util.List;
import java.util.Optional;

public interface StaffRepository extends JpaRepository<Staff, Long> {
  List<Staff> findAllByUsernameIn(List<String> usernames);

  Optional<Staff> findByUsername(String username);
}
