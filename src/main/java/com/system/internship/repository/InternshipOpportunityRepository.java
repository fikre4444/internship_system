package com.system.internship.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.system.internship.domain.InternshipOpportunity;

public interface InternshipOpportunityRepository extends JpaRepository<InternshipOpportunity, Long> {

}
