package com.system.internship.services;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.system.internship.domain.InternshipApplication;
import com.system.internship.domain.InternshipOpportunity;
import com.system.internship.domain.Student;
import com.system.internship.domain.TemporaryPlacement;
import com.system.internship.dto.CompanyRequestDto;
import com.system.internship.dto.InternshipChangeRequestDto;
import com.system.internship.dto.InternshipOpportunityDto;
import com.system.internship.enums.DepartmentEnum;
import com.system.internship.repository.InternshipApplicationRepository;
import com.system.internship.repository.InternshipOpportunityRepository;
import com.system.internship.repository.StudentRepository;
import com.system.internship.repository.TemporaryPlacementRepository;
import com.system.internship.util.HashUtil;

@Service
public class HeadInternshipCoordinatorService {

  @Autowired
  private InternshipOpportunityRepository ioRepo;

  @Autowired
  private StudentRepository studentRepo;

  @Autowired
  private InternshipApplicationRepository iaRepo;

  @Autowired
  private TemporaryPlacementRepository tempRepo;

  @Autowired
  private JwtService jwtService;

  @Autowired
  private EmailService emailService;

  public Map<String, Object> saveInternshipOpportunity(InternshipOpportunityDto iod) {
    InternshipOpportunity io = convertToInternshipOpportunity(iod);
    InternshipOpportunity savedIo = ioRepo.save(io);
    return Map.of("result", "success", "message", "posted internship successfully.", "internshipOpportunity", savedIo);
  }

  public Map<String, Object> saveInternshipOpportunities(List<InternshipOpportunityDto> iodList) {
    List<InternshipOpportunity> ioList = convertToInternshipOpportunities(iodList);
    List<InternshipOpportunity> savedIos = ioRepo.saveAll(ioList);
    return Map.of("result", "success", "message", "posted internship successfully.", "internshipOpportunities",
        savedIos);
  }

  public List<InternshipOpportunity> convertToInternshipOpportunities(List<InternshipOpportunityDto> iodList) {
    return iodList.stream().map(iod -> convertToInternshipOpportunity(iod)).collect(Collectors.toList());
  }

  public InternshipOpportunity convertToInternshipOpportunity(InternshipOpportunityDto iod) {
    InternshipOpportunity io = InternshipOpportunity.builder()
        .companyName(iod.getCompanyName()).department(iod.getDepartment())
        .location(iod.getLocation()).noOfStudents(iod.getNoOfStudents())
        .pocketMoney(iod.isPocketMoney()).internshipStatus(iod.getInternshipStatus())
        .typeOfInternship("MU_PROVIDED")
        .uniqueIdentifier(
            HashUtil.generateHashFromInternshipOpportunityMU(
                iod.getCompanyName(),
                iod.getLocation(),
                iod.getDepartment()))
        .build();
    return io;
  }

  public Map<String, Object> assignInternships(String department) {
    DepartmentEnum departmentEnum = DepartmentEnum.valueOf(department);
    List<Student> students = studentRepo.findByDepartment(departmentEnum);
    List<InternshipOpportunity> internshipOpportunties = ioRepo.findAllByDepartment(departmentEnum)
        .stream().filter(io -> io.getTypeOfInternship().equals("MU_PROVIDED"))
        .collect(Collectors.toList());

    Collections.sort(students, new Comparator<Student>() {
      public int compare(Student s1, Student s2) {
        return Float.compare(s1.getGrade(), s2.getGrade());
      }
    });

    // holds the current number of students assigned to an internship opportunity
    Map<Long, Integer> ioIdToStudentAmount = new HashMap<>();
    Map<Student, InternshipOpportunity> matchedResults = new HashMap<>();
    List<TemporaryPlacement> temporaryPlacements = new ArrayList<>();

    System.out.println("The student grades are:");
    for (int i = students.size() - 1; i >= 0; i--) {
      Student currentStudent = students.get(i);
      Set<InternshipApplication> applications = currentStudent.getInternshipApplications();
      // convert the set into a list of sorted priorities
      List<InternshipApplication> sortedByPriorityApplications = applications.stream()
          .sorted(new Comparator<InternshipApplication>() {
            public int compare(InternshipApplication ia1, InternshipApplication ia2) {
              return Integer.compare(ia1.getPriority(), ia2.getPriority());
            }
          }).collect(Collectors.toList());

      // used for displaying the details
      System.out.println(currentStudent.getFirstName() + " grade:" + currentStudent.getGrade());
      System.out.println("The and priorities are");
      sortedByPriorityApplications.forEach(internshipApplication -> {
        System.out.print(internshipApplication.getPriority() + " ");
      });
      System.out.println();
      for (int j = 0; j < sortedByPriorityApplications.size(); j++) {
        InternshipApplication internshipApplication = sortedByPriorityApplications.get(j);
        InternshipOpportunity io = internshipApplication.getInternshipOpportunity();
        if (!ioIdToStudentAmount.containsKey(io.getId())) {
          // this means if this opportunity hasn't had anyone claim it yet.
          ioIdToStudentAmount.put(io.getId(), 1);
          matchedResults.put(currentStudent, io);
          // System.out.println(currentStudent.getFirstName() + " has been put to " +
          // io.getCompanyName()
          // + " with the priority of " + internshipApplication.getPriority());
          TemporaryPlacement temporaryPlacement = TemporaryPlacement.builder().student(currentStudent)
              .internshipOpportunity(io).priority(internshipApplication.getPriority()).build();
          temporaryPlacements.add(temporaryPlacement);
          break;
        } else if (ioIdToStudentAmount.get(io.getId()) < io.getNoOfStudents()) {
          // increment by one if the opportunity isn't full
          ioIdToStudentAmount.put(io.getId(), ioIdToStudentAmount.get(io.getId()) + 1);
          matchedResults.put(currentStudent, io);
          // System.out.println(currentStudent.getFirstName() + " has been put to " +
          // io.getCompanyName()
          // + " with the priority of " + internshipApplication.getPriority());
          TemporaryPlacement temporaryPlacement = TemporaryPlacement.builder().student(currentStudent)
              .internshipOpportunity(io).priority(internshipApplication.getPriority()).build();
          temporaryPlacements.add(temporaryPlacement);
          break;
        }
      }
    }

    temporaryPlacements.forEach(temporaryPlacement -> {
      System.out.print("Student name: " + temporaryPlacement.getStudent().getFirstName() + "  ");
      System.out
          .print("Internship Opportunity: " + temporaryPlacement.getInternshipOpportunity().getCompanyName() + "  ");
      System.out.print("Priority: " + temporaryPlacement.getPriority() + "  ");
      System.out.println();
    });

    tempRepo.saveAll(temporaryPlacements);
    List<TemporaryPlacement> temporaryPlacementsEssentials = getImportantDetails(temporaryPlacements);

    return Map.of("result", "success", "message", "Students Have been placed successfully.", "temporaryPlacements",
        temporaryPlacementsEssentials);
  }

  public List<TemporaryPlacement> getImportantDetails(List<TemporaryPlacement> temporaryPlacements) {
    List<TemporaryPlacement> importantDetails = new ArrayList<>();
    temporaryPlacements.forEach(temporaryPlacement -> {
      Student student = temporaryPlacement.getStudent();
      student.setPassword(null);
      student.setRoles(null);
      student.setDepartment(null);
      student.setStream(null);
      student.getInternshipApplications().forEach(internshipApplication -> {
        internshipApplication.setStudent(null);
      });
      temporaryPlacement.setStudent(student);

      InternshipOpportunity io = temporaryPlacement.getInternshipOpportunity();
      io.setId(null);
      io.setDepartment(null);
      temporaryPlacement.setInternshipOpportunity(io);
      importantDetails.add(temporaryPlacement);
    });

    return importantDetails;
  }

  public boolean checkAllStudentsApplied(String department) {
    // how to check that
    DepartmentEnum departmentEnum = DepartmentEnum.valueOf(department);
    List<Student> students = studentRepo.findByDepartment(departmentEnum);
    for (Student student : students) {
      if (student.getInternshipApplications().size() < 1) {
        return false;
      }
    }
    return true;
  }

  public Map<String, Object> confirmPlacements(String department) {
    // first fetch the ones with the department
    DepartmentEnum departmentEnum = DepartmentEnum.valueOf(department);
    List<InternshipOpportunity> iOpportunities = ioRepo.findAllByDepartment(departmentEnum);
    List<InternshipOpportunity> filteredForMu = iOpportunities.stream().filter(opportunity -> {
      return opportunity.getTypeOfInternship().equals("MU_PROVIDED");
    }).collect(Collectors.toList());
    List<TemporaryPlacement> tempos = tempRepo.findAllByInternshipOpportunityIn(filteredForMu);
    tempos.forEach(tempo -> {
      tempo.setConfirmed_by_coordinator(true);
    });
    tempRepo.saveAll(tempos);
    return Map.of("result", "success", "message", "successfully confirmed it");
  }

  public Map<String, Object> applyPlacementChanges(List<InternshipChangeRequestDto> listOfChanges) {
    // List<String> uniqueIdentifiers = listOfChanges.stream()
    // .map(change ->
    // change.getInternshipOpportunityUniqueIdentifier()).collect(Collectors.toList());
    // List<InternshipOpportunity> ios =
    // ioRepo.findAllByUniqueIdentifierIn(uniqueIdentifiers);
    // List<Student> students =
    // List<TemporaryPlacement> tempos =
    // tempRepo.findAllByInternshipOpportunityIn(ios);
    listOfChanges.forEach(change -> {
      Optional<Student> studentOpt = studentRepo.findByUsername(change.getStudentUsername());
      if (studentOpt.isPresent()) {
        Student student = studentOpt.get();
        Optional<InternshipOpportunity> ioOpt = ioRepo
            .findByUniqueIdentifier(change.getInternshipOpportunityUniqueIdentifier());
        if (ioOpt.isPresent()) {
          InternshipOpportunity io = ioOpt.get();
          Optional<TemporaryPlacement> tempoOpt = tempRepo.findByStudent(student);
          if (tempoOpt.isPresent()) {
            TemporaryPlacement temporary = tempoOpt.get();
            temporary.setInternshipOpportunity(io);
            temporary.setPriority(change.getPriority());
            tempRepo.save(temporary);
          }
        }
      }
    });

    Optional<Student> studentOpt = studentRepo.findByUsername(listOfChanges.get(0).getStudentUsername());
    if (studentOpt.isPresent()) {
      Student student = studentOpt.get();
      String department = student.getDepartment().name();
      confirmPlacements(department);
    }

    return Map.of("result", "success", "message", "successfully applied changes and confirmed it");
  }

  public Map<String, Object> sendRequestToCompany(CompanyRequestDto companyRequestDto) {
    // generate permission token for inputting internship
    String companyPermissionToken = jwtService.generateTokenForCompanyFiller();
    String email = companyRequestDto.getEmail();
    String link = "http://10.10.11.215:5173/companyPostingPage?token=" + companyPermissionToken;

    String content = companyRequestDto.getMessage();
    content += link;
    content += "<br>Note that the Link only works for 1 day";
    emailService.sendEmail(email, "Mekelle Intenship Form For Filling By the Company", content);
    return Map.of("result", "success", "message",
        "The Request Link has Been sent successfully!");
  }

}
