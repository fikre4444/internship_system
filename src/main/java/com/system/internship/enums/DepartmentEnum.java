package com.system.internship.enums;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.system.internship.serializer.DepartmentSerializer;

@JsonSerialize(using = DepartmentSerializer.class)
public enum DepartmentEnum {

  CHEMICAL("Chemical Engineering"),
  INDUSTRIAL("Industrial Engineering"),
  CIVIL("Civil Engineering"),
  MECHANICAL("Mechanical Engineering"),
  ELECTRICAL("Electrical Engineering"),
  ARCHITECTURE("Architecture"),
  TEXTILE("Textile Engineering");

  private String departmentName;

  private DepartmentEnum(String name) {
    this.departmentName = name;
  }

  public String getDepartmentName() {
    return this.departmentName;
  }

  public static DepartmentEnum fromName(String name) {
    for (DepartmentEnum departmentEnum : DepartmentEnum.values()) {
      if (departmentEnum.getDepartmentName().equalsIgnoreCase(name)) { // Case insensitive comparison
        return departmentEnum;
      }
    }
    throw new IllegalArgumentException("No enum constant with department name " + name);
  }

}
