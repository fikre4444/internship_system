package com.system.internship.util;

import java.util.Random;

public class PasswordGenerator {
  private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789@";

  public static String generateRandomPassword(int length) {
    Random random = new Random();
    StringBuilder password = new StringBuilder(length);
    for (int i = 0; i < length; i++) {
      password.append(CHARACTERS.charAt(random.nextInt(CHARACTERS.length())));
    }
    return password.toString();
  }

}
