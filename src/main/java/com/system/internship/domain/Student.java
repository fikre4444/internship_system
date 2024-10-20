package com.system.internship.domain;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.util.Objects;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.system.internship.enums.DepartmentEnum;

@ToString(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
@Table(name = "student")
public class Student extends Account {

  @Enumerated(EnumType.STRING)
  private DepartmentEnum department;
  private String stream;
  private Float grade;

  @ManyToOne
  @JoinColumn(name = "assigned_internship_opportunity_id") // Customize the foreign key column name
  private InternshipOpportunity assignedInternship;

  private String assignedInternshipStatus; // whether pending or final

  // the many to many relationship of internship applictions
  @OneToMany(mappedBy = "student", fetch = FetchType.LAZY)
  // @JsonIgnore
  private Set<InternshipApplication> internshipApplications;

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;
    Student student = (Student) o;
    return Objects.equals(this.getUsername(), student.getUsername()); // Compare based on unique field 'username'
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.getUsername()); // Use the same unique field for hashCode
  }

}
