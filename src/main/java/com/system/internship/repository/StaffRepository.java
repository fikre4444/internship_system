package com.system.internship.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.system.internship.domain.Account;
import com.system.internship.domain.Staff;
import com.system.internship.enums.DepartmentEnum;
import com.system.internship.enums.GenderEnum;

import java.util.List;
import java.util.Optional;

public interface StaffRepository extends JpaRepository<Staff, Long> {
    List<Staff> findAllByUsernameIn(List<String> usernames);

    Optional<Staff> findByUsername(String username);

    @Query("SELECT s FROM Staff s WHERE s.department = :department")
    List<Staff> findByDepartment(@Param("department") DepartmentEnum department);

    @Modifying
    @Query("DELETE FROM Staff s WHERE s.department = :department")
    void deleteStaffsByDepartment(@Param("department") DepartmentEnum department);

    @Query("SELECT a FROM Staff a WHERE " +
            "((:firstName IS NULL OR LOWER(a.firstName) LIKE LOWER(CONCAT('%', :firstName, '%'))) OR " +
            "(:lastName IS NULL OR LOWER(a.lastName) LIKE LOWER(CONCAT('%', :lastName, '%'))) OR " +
            "(:username IS NULL OR LOWER(a.username) LIKE LOWER(CONCAT('%', :username, '%')))) AND " +
            "(:gender IS NULL OR a.gender = :gender) AND " +
            "(:department IS NULL OR a.department = :department)")
    List<Account> searchAccountsInclusive(
            @Param("firstName") String firstName,
            @Param("lastName") String lastName,
            @Param("username") String username,
            @Param("gender") GenderEnum gender,
            @Param("department") DepartmentEnum department);

    @Query("SELECT a FROM Staff a WHERE " +
            "((:firstName IS NULL OR LOWER(a.firstName) LIKE LOWER(CONCAT('%', :firstName, '%'))) AND " +
            "(:lastName IS NULL OR LOWER(a.lastName) LIKE LOWER(CONCAT('%', :lastName, '%'))) AND " +
            "(:username IS NULL OR LOWER(a.username) LIKE LOWER(CONCAT('%', :username, '%')))) AND " +
            "(:gender IS NULL OR a.gender = :gender) AND " +
            "(:department IS NULL OR a.department = :department)")
    List<Account> searchAccountsRestrictive(
            @Param("firstName") String firstName,
            @Param("lastName") String lastName,
            @Param("username") String username,
            @Param("gender") GenderEnum gender,
            @Param("department") DepartmentEnum department);

    @Query("SELECT COUNT(s) FROM Staff s WHERE s.department = :department")
    long countByDepartment(@Param("department") DepartmentEnum department);

    @Query("SELECT s.department, COUNT(s) FROM Staff s GROUP BY s.department")
    List<Object[]> countStaffByDepartmentGroup();

}
