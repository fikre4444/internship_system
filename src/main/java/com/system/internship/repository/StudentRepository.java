package com.system.internship.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.system.internship.domain.Student;

import java.util.List;
import java.util.Optional;

public interface StudentRepository extends JpaRepository<Student, Long> {
  List<Student> findAllByUsernameIn(List<String> usernames);

  Optional<Student> findByUsername(String username);
}
