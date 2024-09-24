package com.system.internship.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.util.Objects;

import com.system.internship.enums.DepartmentEnum;

@ToString(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
@Table(name = "staff")
public class Staff extends Account {

  @Enumerated(EnumType.STRING)
  private DepartmentEnum department;
  private Float courseLoad;

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;
    Staff staff = (Staff) o;
    return Objects.equals(this.getUsername(), staff.getUsername()); // Compare based on unique field 'username'
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.getUsername()); // Use the same unique field for hashCode
  }

}
