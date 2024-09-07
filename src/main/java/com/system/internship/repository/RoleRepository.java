package com.system.internship.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.system.internship.domain.Role;
import com.system.internship.enums.RoleEnum;

public interface RoleRepository extends JpaRepository<Role, Long> {
  Optional<Role> findByName(RoleEnum name);
}
