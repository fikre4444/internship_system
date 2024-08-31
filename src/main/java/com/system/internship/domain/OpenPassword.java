package com.system.internship.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "password")
public class OpenPassword {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  Long id;

  private String password;

  @OneToOne
  @JoinColumn(name = "account_id")
  private Account account;

}