package com.system.internship.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.system.internship.domain.InternshipOpportunity;

public interface InternshipOpportunityRepository extends JpaRepository<InternshipOpportunity, Long> {

  List<InternshipOpportunity> findAllByUniqueIdentifierIn(List<String> uniqueIdentifiers);

}
