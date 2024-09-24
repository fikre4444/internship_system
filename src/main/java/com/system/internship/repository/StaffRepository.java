package com.system.internship.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.system.internship.domain.Staff;

import java.util.List;
import java.util.Optional;

public interface StaffRepository extends JpaRepository<Staff, Long> {
  List<Staff> findAllByUsernameIn(List<String> usernames);

  Optional<Staff> findByUsername(String username);

  @Query("SELECT s FROM Staff s WHERE s.department = :department")
  List<Staff> findByDepartment(@Param("department") String department);

  @Modifying
  @Query("DELETE FROM Staff s WHERE s.department = :department")
  void deleteStaffsByDepartment(@Param("department") String department);
}
