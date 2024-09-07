package com.system.internship.services;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.system.internship.domain.Role;
import com.system.internship.enums.RoleEnum;
import com.system.internship.repository.RoleRepository;

@Service
public class RoleService {

  @Autowired
  private RoleRepository roleRepository;

  public Role getRole(RoleEnum roleEnum) {
    Optional<Role> roleOpt = roleRepository.findByName(roleEnum);
    if (roleOpt.isPresent()) {
      return roleOpt.get();
    } else {
      Role savedRole = roleRepository.save(Role.builder().name(roleEnum).build());
      return savedRole;
    }
  }

}
