package com.system.internship.services.strategy;

import com.system.internship.domain.Account;
import com.system.internship.domain.OpenPassword;
import com.system.internship.dto.RegisterRequestCustomBodyDto;
import com.system.internship.dto.RegisterResponseDto;
import com.system.internship.repository.OpenPasswordRepository;
import com.system.internship.util.PasswordGenerator;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.crypto.password.PasswordEncoder;

public interface AccountRegistrationStrategy {
  RegisterResponseDto registerBatch(URI uri);

  RegisterResponseDto registerSingle(URI uri);

  RegisterResponseDto registerCustom(RegisterRequestCustomBodyDto registerDto);

  default void generateAndSavePasswordsForNewUsers(List<? extends Account> accounts, JpaRepository repository,
      OpenPasswordRepository openPasswordRepository, PasswordEncoder passwordEncoder) {
    List<OpenPassword> generatedOpenPasswords = new ArrayList<>();
    accounts.forEach(account -> {
      String generatedPassword = PasswordGenerator.generateRandomPassword(8);
      account.setPassword(passwordEncoder.encode(generatedPassword));
      OpenPassword op = OpenPassword.builder().password(generatedPassword).account(account).build();
      generatedOpenPasswords.add(op);
    });
    repository.saveAll(accounts);
    openPasswordRepository.saveAll(generatedOpenPasswords);
  }
}
