package com.system.internship.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.persistence.GenerationType;

//this will hold the temporary placement of a student to company
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class TemporaryPlacement {

  // we will need to save the matching of the io and student and the priority it
  // was.
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  Long id;

  @OneToOne
  private Student student;

  @ManyToOne
  private InternshipOpportunity internshipOpportunity;

  int priority;

}
