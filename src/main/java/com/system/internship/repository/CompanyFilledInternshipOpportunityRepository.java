package com.system.internship.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.system.internship.domain.CompanyFilledInternshipOpportunity;

public interface CompanyFilledInternshipOpportunityRepository
    extends JpaRepository<CompanyFilledInternshipOpportunity, Long> {

}
