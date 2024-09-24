package com.system.internship.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.system.internship.domain.Student;
import com.system.internship.enums.DepartmentEnum;

import jakarta.transaction.Transactional;

import java.util.List;
import java.util.Optional;

public interface StudentRepository extends JpaRepository<Student, Long> {

  List<Student> findAllByUsernameIn(List<String> usernames);

  Optional<Student> findByUsername(String username);

  @Query("SELECT s FROM Student s WHERE s.department = :department")
  List<Student> findByDepartment(@Param("department") DepartmentEnum department);

  @Modifying
  @Query("DELETE FROM Student s WHERE s.department = :department")
  void deleteStudentsByDepartment(@Param("department") DepartmentEnum department);

}
