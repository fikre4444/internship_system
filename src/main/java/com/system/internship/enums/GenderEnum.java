package com.system.internship.enums;

public enum GenderEnum {

  MALE("Male"),
  FEMALE("Female");

  private String gender;

  private GenderEnum(String g) {
    this.gender = g;
  }

  public String getGender() {
    return this.gender;
  }

  public static GenderEnum fromName(String name) {
    for (GenderEnum genderEnum : GenderEnum.values()) {
      if (genderEnum.getGender().equalsIgnoreCase(name)) { // Case insensitive comparison
        return genderEnum;
      }
    }
    throw new IllegalArgumentException("No enum constant with gender name " + name);
  }

}
