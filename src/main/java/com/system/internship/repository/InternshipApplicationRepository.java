package com.system.internship.repository;

import com.system.internship.domain.InternshipApplication;

import org.springframework.data.jpa.repository.JpaRepository;

public interface InternshipApplicationRepository extends JpaRepository<InternshipApplication, Long> {
}
