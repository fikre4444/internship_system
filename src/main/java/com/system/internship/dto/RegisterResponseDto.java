package com.system.internship.dto;

import java.util.List;

import com.system.internship.domain.Staff;
import com.system.internship.domain.Student;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterResponseDto {
  private List<Staff> registeredStaffs;
  private List<Staff> existingStaffs;
  private List<Student> registeredStudents;
  private List<Student> existingStudents;
  private boolean errorResponse;
  private boolean incorrectBody;
}
