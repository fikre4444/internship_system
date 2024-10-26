package com.system.internship.domain;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.system.internship.enums.GenderEnum;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
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
public class Account implements UserDetails {

  @Id
  @SequenceGenerator(name = "account_sequence", sequenceName = "account_sequence", allocationSize = 1)
  @GeneratedValue(generator = "account_sequence", strategy = GenerationType.SEQUENCE)
  @JsonIgnore
  private Long id;
  private String firstName;
  private String lastName;

  @Column(unique = true, nullable = false)
  private String username;
  private String password;

  private String email;

  @Builder.Default
  private boolean enabled = true;

  @Enumerated(EnumType.STRING)
  private GenderEnum gender;

  @OneToOne(mappedBy = "associatedAccount", cascade = CascadeType.REMOVE, orphanRemoval = true)
  private ChatId chatId;

  @ManyToMany(fetch = FetchType.EAGER)
  @JoinTable(name = "account_role", joinColumns = @JoinColumn(name = "account_id"), inverseJoinColumns = @JoinColumn(name = "role_id"))
  private Set<Role> roles;

  @JsonIgnore
  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return roles.stream()
        .map(role -> new SimpleGrantedAuthority(role.getName().name()))
        .collect(Collectors.toList());
  }

  @Override
  public boolean isEnabled() {
    return this.enabled;
  }

  @JsonIgnore
  public boolean isAccountNonExpired() {
    return true;
  }

  @JsonIgnore
  public boolean isAccountNonLocked() {
    return true;
  }

  @JsonIgnore
  public boolean isCredentialsNonExpired() {
    return true;
  }

}
