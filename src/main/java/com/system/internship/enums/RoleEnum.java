package com.system.internship.enums;

public enum RoleEnum {
  ROLE_STUDENT("Student"),
  ROLE_STAFF("Staff"),
  ROLE_ADVISOR("Advisor"),
  ROLE_HEAD_INTERNSHIP_COORDINATOR("Head Internship Coordinator"),
  ROLE_DEPARTMENT_INTERNSHIP_COORDINATOR("Department Internship Coordinator"),
  ROLE_ADMINISTRATOR("Administrator");

  private String name;

  private RoleEnum(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }

  public static RoleEnum fromName(String name) {
    for (RoleEnum roleEnum : RoleEnum.values()) {
      if (roleEnum.getName().equalsIgnoreCase(name)) { // Case insensitive comparison
        return roleEnum;
      }
    }
    throw new IllegalArgumentException("No enum constant with Role name " + name);
  }

}
