package com.system.internship.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "internship_application", uniqueConstraints = {
    @UniqueConstraint(columnNames = { "student_id", "internship_opportunity_id" })
})
// the above constraint serves as a primary key constraint for the combination
// of student and internship opportunity
public class InternshipApplication {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "student_id", nullable = false)
  @JsonIgnore
  private Student student;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "internship_opportunity_id", nullable = false)
  @JsonIgnore
  private InternshipOpportunity internshipOpportunity;

  private Integer priority; // Additional column for priority
}
