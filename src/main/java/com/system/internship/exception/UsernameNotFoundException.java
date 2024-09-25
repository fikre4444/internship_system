package com.system.internship.exception;

public class UsernameNotFoundException extends RuntimeException {

  public UsernameNotFoundException(String username) {
    super("Account with the username " + username + " was not found");
  }

}
