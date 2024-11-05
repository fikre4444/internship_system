package com.system.internship.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.system.internship.domain.InternshipApplication;
import com.system.internship.domain.InternshipOpportunity;
import com.system.internship.domain.Student;
import com.system.internship.dto.InternshipApplicationDto;
import com.system.internship.dto.SingleInternshipApplicationDto;
import com.system.internship.enums.DepartmentEnum;
import com.system.internship.exception.UsernameNotFoundException;
import com.system.internship.repository.AccountRepository;
import com.system.internship.repository.InternshipApplicationRepository;
import com.system.internship.repository.InternshipOpportunityRepository;
import com.system.internship.repository.StudentRepository;

@Service
public class StudentService {

  @Autowired
  private InternshipApplicationRepository iaRepo;
  @Autowired
  private InternshipOpportunityRepository ioRepo;
  @Autowired
  private AccountRepository accountRepo;
  @Autowired
  private StudentRepository studentRepo;

  public Map<String, Object> applyInternships(InternshipApplicationDto applications) {
    String username = applications.getUsername();
    List<SingleInternshipApplicationDto> singleApplications = applications.getApplications();

    // check if student is there
    Optional<Student> studentOpt = studentRepo.findByUsername(username);
    if (!studentOpt.isPresent()) {
      throw new UsernameNotFoundException(username);
    }

    Student student = studentOpt.get();

    List<InternshipOpportunity> opportunities = getInternshipOpportunities(singleApplications);
    List<InternshipApplication> internshipApplications = getInternshipApplications(student, opportunities,
        singleApplications);
    List<InternshipApplication> savedInternshipApplications = iaRepo.saveAll(internshipApplications);
    return Map.of("result", "success", "message", "successfully saved your applications", "applications",
        savedInternshipApplications);
  }

  public List<InternshipOpportunity> getInternshipOpportunities(
      List<SingleInternshipApplicationDto> singleApplications) {
    List<String> uniqueIdentifiers = singleApplications.stream()
        .map(singleApplication -> singleApplication.getInternshipOpportunityUniqueIdentifier())
        .collect(Collectors.toList());
    List<InternshipOpportunity> opportunities = ioRepo.findAllByUniqueIdentifierIn(uniqueIdentifiers);
    return opportunities;
  }

  public List<InternshipApplication> getInternshipApplications(Student student,
      List<InternshipOpportunity> opportunities, List<SingleInternshipApplicationDto> singleApplications) {

    List<InternshipApplication> internshipApplications = new ArrayList<>();
    opportunities.forEach(opportunity -> {
      singleApplications.forEach(singleApplication -> {
        if (opportunity.getUniqueIdentifier().equals(singleApplication.getInternshipOpportunityUniqueIdentifier())) {
          InternshipApplication ia = InternshipApplication.builder().student(student).internshipOpportunity(opportunity)
              .priority(singleApplication.getPriority()).build();
          internshipApplications.add(ia);
        }
      });
    });
    return internshipApplications;
  }

  public Student getStudent(String username) {
    Optional<Student> studentOpt = studentRepo.findByUsername(username);
    if (!studentOpt.isPresent()) {
      throw new UsernameNotFoundException(username);
    }

    Student student = studentOpt.get();
    Set<InternshipApplication> ias = student.getInternshipApplications();
    return student;
  }

  public List<InternshipOpportunity> getInternships(String username) {
    Optional<Student> studentOpt = studentRepo.findByUsername(username);
    if (!studentOpt.isPresent()) {
      throw new UsernameNotFoundException(username);
    }
    Student student = studentOpt.get();
    DepartmentEnum departmentEnum = student.getDepartment();
    List<InternshipOpportunity> internships = ioRepo.findAllByDepartment(departmentEnum);
    List<InternshipOpportunity> muProvidedInternships = internships.stream()
        .filter(internship -> {
          return internship.getTypeOfInternship().equals("MU_PROVIDED");
        })
        .collect(Collectors.toList());
    return muProvidedInternships;
  }

  public Map<String, Object> getSelfSecuredInternship(String username) {
    Optional<Student> studentOpt = studentRepo.findByUsername(username);
    if (!studentOpt.isPresent()) {
      throw new UsernameNotFoundException(username);
    }
    Student student = studentOpt.get();
    return Map.of("result", "success", "selfInternship", student.getAssignedInternship());
  }

}
