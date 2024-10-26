package com.system.internship.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.system.internship.domain.InternshipOpportunity;
import com.system.internship.enums.DepartmentEnum;

public interface InternshipOpportunityRepository extends JpaRepository<InternshipOpportunity, Long> {

  List<InternshipOpportunity> findAllByUniqueIdentifierIn(List<String> uniqueIdentifiers);

  Optional<InternshipOpportunity> findByUniqueIdentifier(String uniqueIdentifier);

  List<InternshipOpportunity> findAllByDepartment(DepartmentEnum departmentEnum);

}
