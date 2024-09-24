package com.system.internship.util;

import com.system.internship.enums.DepartmentEnum;

public class DepartmentUtil {

  public static DepartmentEnum convertDepartmentStringToEnum(String departmentString, String sourceType) {
    if (sourceType.equalsIgnoreCase("Estudent")) {
      return convertFromEstudentToDepartmentEnum(departmentString);
    }
    // Add more source types if needed
    throw new IllegalArgumentException("Invalid sourceType or departmentString");
  }

  public static DepartmentEnum convertFromEstudentToDepartmentEnum(String departmentString) {
    return DepartmentEnum.fromName(departmentString);
  }

  public static String getApiStringFromEnum(DepartmentEnum departmentEnum) {
    return departmentEnum.getDepartmentName();
  }

}
