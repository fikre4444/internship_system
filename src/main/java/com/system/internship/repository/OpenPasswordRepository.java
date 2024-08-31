package com.system.internship.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.system.internship.domain.OpenPassword;

public interface OpenPasswordRepository extends JpaRepository<OpenPassword, Long> {

}
