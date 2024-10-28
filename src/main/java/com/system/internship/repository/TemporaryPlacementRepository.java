package com.system.internship.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.system.internship.domain.TemporaryPlacement;

public interface TemporaryPlacementRepository extends JpaRepository<TemporaryPlacement, Long> {

}
