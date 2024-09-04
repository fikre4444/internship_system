package com.system.internship.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.system.internship.domain.Role;

public interface RoleRepository extends JpaRepository<Role, Long> {

}
