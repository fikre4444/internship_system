package com.system.internship.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.system.internship.domain.InternshipOpportunity;
import com.system.internship.domain.Student;
import com.system.internship.domain.TemporaryPlacement;

public interface TemporaryPlacementRepository extends JpaRepository<TemporaryPlacement, Long> {
  List<TemporaryPlacement> findAllByInternshipOpportunityIn(List<InternshipOpportunity> internshipOpportunities);

  Optional<TemporaryPlacement> findByStudent(Student student);
}