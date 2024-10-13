package com.system.internship.services;

import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.system.internship.domain.InternshipOpportunity;
import com.system.internship.domain.Student;
import com.system.internship.exception.UsernameNotFoundException;
import com.system.internship.repository.AccountRepository;
import com.system.internship.repository.InternshipOpportunityRepository;
import com.system.internship.repository.StudentRepository;

@Service
public class DepartmentInternshipCoordinatorService {

  @Autowired
  AccountRepository accountRepo;
  @Autowired
  StudentRepository studentRepo;
  @Autowired
  InternshipOpportunityRepository internshipOpRepo;

  public Map<String, Object> addSelfInternship(Map<String, String> requestBody) {
    String username = requestBody.get("username");
    String companyName = requestBody.get("companyName");
    // the department can be known from the student
    // String department = requestBody.get("department");
    String location = requestBody.get("location");
    Optional<Student> studentOpt = studentRepo.findByUsername(username);
    if (!studentOpt.isPresent()) {
      throw new UsernameNotFoundException(username);
    }
    Student student = studentOpt.get();
    // create the internship opportunity
    InternshipOpportunity io = InternshipOpportunity.builder()
        .companyName(companyName).department(student.getDepartment()).internshipStatus("FILLED")
        .location(location).typeOfInternship("SELF_PROVIDED").build();
    // save the internship opportunity and get the saved one
    InternshipOpportunity savedOne = internshipOpRepo.save(io);
    student.setAssignedInternship(savedOne); // set the svaed one to the student
    student.setAssignedInternshipStatus("PENDING");
    studentRepo.save(student);
    return Map.of("result", "success", "message", "the student has been assigned their own internship successfully.");
  }

}
