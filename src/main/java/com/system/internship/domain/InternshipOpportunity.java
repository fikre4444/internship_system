package com.system.internship.domain;

import com.system.internship.enums.DepartmentEnum;

import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(uniqueConstraints = {
    @UniqueConstraint(columnNames = { "companyName", "location", "department" })
})
public class InternshipOpportunity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String companyName;
  @Column(nullable = false)
  private String location; // city where the company is located
  private DepartmentEnum department; // for which department is this opportunity
  private Integer noOfStudents;

  @Column(nullable = false)
  private String typeOfInternship; // either self secured or provided by MU
  private boolean pocketMoney; // either they get paid or not

  private String internshipStatus; // whether available or filled

  @OneToMany(mappedBy = "internshipOpportunity")
  private Set<InternshipApplication> internshipApplications;

}