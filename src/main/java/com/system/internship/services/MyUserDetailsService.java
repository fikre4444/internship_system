package com.system.internship.services;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.system.internship.domain.Account;
import com.system.internship.repository.AccountRepository;

@Service
public class MyUserDetailsService implements UserDetailsService {

  @Autowired
  private AccountRepository accountRepository;

  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    Optional<Account> account = accountRepository.findByUsername(username);
    if (account.isPresent()) {
      return account.get();
    } else {
      throw new UsernameNotFoundException("The user name is not found");
    }
  }

}
