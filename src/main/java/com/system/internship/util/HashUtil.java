package com.system.internship.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import com.system.internship.enums.DepartmentEnum;

public class HashUtil {

  public static String generateHashFromInternshipOpportunitySelf(String companyName, String location,
      DepartmentEnum department, String username) {
    // used for generating unique identifiers for Self provided internships
    try {
      // Concatenate the key fields
      String combined = String.format("%s-%s-%s-%s",
          companyName.trim().toLowerCase(),
          location.trim().toLowerCase(),
          department.name().toLowerCase(),
          username.strip().toLowerCase());

      // Create SHA-256 MessageDigest
      MessageDigest digest = MessageDigest.getInstance("SHA-256");
      byte[] hashBytes = digest.digest(combined.getBytes(StandardCharsets.UTF_8));

      // Convert to Base64 to get a readable string
      return Base64.getUrlEncoder().withoutPadding().encodeToString(hashBytes);
    } catch (NoSuchAlgorithmException e) {
      throw new RuntimeException("Error generating hash", e);
    }
  }

  public static String generateHashFromInternshipOpportunityMU(String companyName, String location,
      DepartmentEnum department) {
    // used for generating unique identifiers for MU provided internships

    try {
      // Concatenate the key fields
      String combined = String.format("%s-%s-%s",
          companyName.trim().toLowerCase(),
          location.trim().toLowerCase(),
          department.name().toLowerCase());

      // Create SHA-256 MessageDigest
      MessageDigest digest = MessageDigest.getInstance("SHA-256");
      byte[] hashBytes = digest.digest(combined.getBytes(StandardCharsets.UTF_8));

      // Convert to Base64 to get a readable string
      return Base64.getUrlEncoder().withoutPadding().encodeToString(hashBytes);
    } catch (NoSuchAlgorithmException e) {
      throw new RuntimeException("Error generating hash", e);
    }
  }
}
