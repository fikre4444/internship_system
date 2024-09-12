package com.system.internship.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.system.internship.domain.Account;
import com.system.internship.domain.OpenPassword;
import com.system.internship.repository.OpenPasswordRepository;
import com.system.internship.util.PasswordGenerator;

import java.util.Optional;

@Service
public class PasswordService {

  @Autowired
  OpenPasswordRepository openPasswordRepository;

  public OpenPassword getPassword(Account account) {
    Optional<OpenPassword> opOpt = openPasswordRepository.findByAccount(account);
    if (opOpt.isPresent()) {
      return opOpt.get();
    } else {
      String generatedPassword = PasswordGenerator.generateRandomPassword(8);
      OpenPassword openPassword = OpenPassword.builder().password(generatedPassword).account(account).build();
      return openPassword;
    }
  }

}
