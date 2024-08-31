package com.system.internship.domain;

import com.system.internship.enums.GenderEnum;
import com.system.internship.enums.RoleEnum;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "account")
@Inheritance(strategy = InheritanceType.JOINED)
@SuperBuilder
public class Account {

  @Id
  @SequenceGenerator(name = "account_sequence", sequenceName = "account_sequence", allocationSize = 1)
  @GeneratedValue(generator = "account_sequence", strategy = GenerationType.SEQUENCE)
  private Long id;
  private String firstName;
  private String lastName;

  @Column(unique = true, nullable = false)
  private String username;
  private String password;

  private String email;

  @Enumerated(EnumType.STRING)
  private GenderEnum gender;

  @Enumerated(EnumType.STRING)
  private RoleEnum role;

}
