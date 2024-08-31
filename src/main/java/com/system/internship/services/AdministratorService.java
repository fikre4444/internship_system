package com.system.internship.services;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.system.internship.domain.*;
import com.system.internship.dto.*;
import com.system.internship.enums.GenderEnum;
import com.system.internship.repository.*;
import com.system.internship.util.PasswordGenerator;

import lombok.val;

@Service
public class AdministratorService {

  @Autowired
  private RestTemplate restTemplate;

  @Autowired
  private StudentRepository studentRepository;

  @Autowired
  private StaffRepository staffRepository;

  @Autowired
  private OpenPasswordRepository openPasswordRepository;

  public RegisterResponseDto register(RegisterRequestBodyDto body) {
    RegisterResponseDto registerResponseDto = new RegisterResponseDto();
    // get the url webservice you need to request from the body
    try {
      checkBodyIntegrity(body); // check if the body is correct
      URI uri = getUriFromBody(body);
      if (body.getAmount().equals(RegisterRequestBodyDto.AmountEnum.BATCH)) {
        // might use an abstract factory later
        if (body.getTypeUser().equals(RegisterRequestBodyDto.TypeUserEnum.STAFF)) {
          StaffDto[] staffDtos = restTemplate.getForObject(uri, StaffDto[].class);
          System.out.println("we have this many staff " + staffDtos.length);
          List<Staff> staffs = convertToStaffs(staffDtos);
          // this creates new objects so when you do the filtering it won't find any equal
          // objects
          List<Staff> existingStaffs = staffRepository.findAllByUsernameIn(staffs.stream()
              .map(Staff::getUsername)
              .collect(Collectors.toList()));

          List<Staff> unRegisteredStaffs = staffs.stream()
              .filter(staff -> !existingStaffs.contains(staff)).collect(Collectors.toList());
          List<OpenPassword> generatedOpenPasswords = new ArrayList<>();
          unRegisteredStaffs.forEach(staff -> {
            String generatedPassword = PasswordGenerator.generateRandomPassword(8);
            staff.setPassword(generatedPassword);
            OpenPassword op = OpenPassword.builder().password(generatedPassword).account(staff).build();
            generatedOpenPasswords.add(op);
          });
          staffRepository.saveAll(unRegisteredStaffs);
          openPasswordRepository.saveAll(generatedOpenPasswords);

          registerResponseDto = RegisterResponseDto.builder()
              .existingStaffs(existingStaffs)
              .registeredStaffs(unRegisteredStaffs) // since they were just regsitered in the above line.
              .build();

        } else if (body.getTypeUser().equals(RegisterRequestBodyDto.TypeUserEnum.STUDENT)) {
          StudentDto[] studentDtos = restTemplate.getForObject(uri, StudentDto[].class);
          List<Student> students = convertToStudents(studentDtos);
          List<Student> existingStudents = studentRepository.findAllByUsernameIn(students.stream()
              .map(Student::getUsername)
              .collect(Collectors.toList()));
          List<Student> unRegisteredStudents = students.stream()
              .filter(student -> !existingStudents.contains(student)).collect(Collectors.toList());

          List<OpenPassword> generatedOpenPasswords = new ArrayList<>();
          unRegisteredStudents.forEach(student -> {
            String generatedPassword = PasswordGenerator.generateRandomPassword(8);
            student.setPassword(generatedPassword);
            OpenPassword op = OpenPassword.builder().password(generatedPassword).account(student).build();
            generatedOpenPasswords.add(op);
          });
          studentRepository.saveAll(unRegisteredStudents);
          openPasswordRepository.saveAll(generatedOpenPasswords);

          registerResponseDto = RegisterResponseDto.builder()
              .existingStudents(existingStudents)
              .registeredStudents(unRegisteredStudents) // since they were just registered in the above line
              .build();
        }
      } else if (body.getAmount().equals(RegisterRequestBodyDto.AmountEnum.SINGLE)) {
        if (body.getTypeUser().equals(RegisterRequestBodyDto.TypeUserEnum.STAFF)) {
          StaffDto staffDto = restTemplate.getForObject(uri, StaffDto.class);
          Staff staff = convertToStaff(staffDto);
          if (!staffRepository.findByUsername(staff.getUsername()).isPresent()) {
            String generatedPassword = PasswordGenerator.generateRandomPassword(8);
            staff.setPassword(generatedPassword);
            OpenPassword op = OpenPassword.builder().password(generatedPassword).account(staff).build();
            staffRepository.save(staff);
            openPasswordRepository.save(op);
            registerResponseDto = RegisterResponseDto.builder()
                .registeredStaffs(List.of(staff))
                .build();
          } else {
            System.out.println("This is already present");
            registerResponseDto = RegisterResponseDto.builder()
                .existingStaffs(List.of(staff))
                .build();
          }
        } else if (body.getTypeUser().equals(RegisterRequestBodyDto.TypeUserEnum.STUDENT)) {
          StudentDto studentDto = restTemplate.getForObject(uri, StudentDto.class);
          Student student = convertToStudent(studentDto);
          if (!studentRepository.findByUsername(student.getUsername()).isPresent()) {
            String generatedPassword = PasswordGenerator.generateRandomPassword(8);
            student.setPassword(generatedPassword);
            OpenPassword op = OpenPassword.builder().password(generatedPassword).account(student).build();
            studentRepository.save(student);
            openPasswordRepository.save(op);
            registerResponseDto = RegisterResponseDto.builder()
                .registeredStudents(List.of(student))
                .build();
          } else {
            System.out.println("This is already present");
            registerResponseDto = RegisterResponseDto.builder()
                .existingStudents(List.of(student))
                .build();
          }
        }
      }
    } catch (HttpClientErrorException exception) {
      registerResponseDto = RegisterResponseDto.builder()
          .errorResponse(true)
          .build();
    } catch (Exception ex) {
      registerResponseDto = RegisterResponseDto.builder()
          .incorrectBody(true)
          .build();
    }
    return registerResponseDto;
  }

  private void checkBodyIntegrity(RegisterRequestBodyDto body) throws Exception {
    if (body.getAmount().equals(RegisterRequestBodyDto.AmountEnum.BATCH)) {
      if (body.getDepartment() != null && body.getTypeUser() != null) {
        return;
      }
    }
    if (body.getAmount().equals(RegisterRequestBodyDto.AmountEnum.SINGLE)) {
      if (body.getUsername() != null && body.getTypeUser() != null) {
        return;
      }
    }
    throw new Exception();
  }

  private URI getUriFromBody(RegisterRequestBodyDto body) {
    String baseUrl = "http://localhost:7000/api";
    URI uri;

    // get the whether student or staff
    if (body.getTypeUser().equals(RegisterRequestBodyDto.TypeUserEnum.STAFF)) {
      baseUrl += "/staff";
    } else if (body.getTypeUser().equals(RegisterRequestBodyDto.TypeUserEnum.STUDENT)) {
      baseUrl += "/student";
    }

    // get whether single (by username ) or department
    if (body.getAmount().equals(RegisterRequestBodyDto.AmountEnum.BATCH)) {
      baseUrl += "/departments";
      uri = UriComponentsBuilder.fromHttpUrl(baseUrl)
          .queryParam("departmentName", body.getDepartment())
          .build()
          .toUri();
    } else if (body.getAmount().equals(RegisterRequestBodyDto.AmountEnum.SINGLE)) {
      baseUrl += "/byusername";
      uri = UriComponentsBuilder.fromHttpUrl(baseUrl)
          .queryParam("username", body.getUsername())
          .build()
          .toUri();
    } else {
      uri = UriComponentsBuilder.fromHttpUrl("").build().toUri();
    }

    return uri;
  }

  private List<Student> convertToStudents(StudentDto[] studentDtos) {
    List<Student> students = new ArrayList<>();
    for (StudentDto studentDto : studentDtos) {
      Student student = convertToStudent(studentDto);
      students.add(student);
    }

    return students;
  }

  private List<Staff> convertToStaffs(StaffDto[] staffDtos) {
    List<Staff> staffs = new ArrayList<>();
    for (StaffDto staffDto : staffDtos) {
      Staff staff = convertToStaff(staffDto);
      staffs.add(staff);
    }

    return staffs;
  }

  private Student convertToStudent(StudentDto studentDto) {
    val student = Student.builder()
        .firstName(studentDto.getFirstName())
        .lastName(studentDto.getLastName())
        .username(studentDto.getUsername())
        .gender(GenderEnum.fromName(studentDto.getGender()))
        .department(studentDto.getDepartment())
        .stream(studentDto.getStream())
        .grade(studentDto.getGrade());
    if (studentDto.getEmail() != null && !studentDto.getEmail().equals("")) {
      student.email(studentDto.getEmail());
    }

    return student.build();
  }

  private Staff convertToStaff(StaffDto staffDto) {
    val staff = Staff.builder()
        .firstName(staffDto.getFirstName())
        .lastName(staffDto.getLastName())
        .username(staffDto.getUsername())
        .gender(GenderEnum.fromName(staffDto.getGender()))
        .department(staffDto.getDepartment())
        .courseLoad(staffDto.getCourseLoad());
    if (staffDto.getEmail() != null && !staffDto.getEmail().equals("")) {
      staff.email(staffDto.getEmail());
    }

    return staff.build();
  }

  public RegisterResponseDto registerCustom(RegisterRequestCustomBodyDto registerDto) {
    RegisterResponseDto registerResponseDto = new RegisterResponseDto();
    if (!checkCustomBodyIntegrity(registerDto)) { // if the custom body is not valid
      registerResponseDto = RegisterResponseDto.builder().incorrectBody(true).build();
      return registerResponseDto;
    }
    if (registerDto.getTypeOfUser().equals(RegisterRequestCustomBodyDto.TypeOfUser.STAFF)) {
      Staff staff = convertFromCustomToStaff(registerDto);
      if (!staffRepository.findByUsername(staff.getUsername()).isPresent()) {
        String generatedPassword = PasswordGenerator.generateRandomPassword(8);
        staff.setPassword(generatedPassword);
        OpenPassword op = OpenPassword.builder().password(generatedPassword).account(staff).build();
        staffRepository.save(staff);
        openPasswordRepository.save(op);
        registerResponseDto = RegisterResponseDto.builder()
            .registeredStaffs(List.of(staff))
            .build();
      } else {
        System.out.println("This is already present");
        registerResponseDto = RegisterResponseDto.builder()
            .existingStaffs(List.of(staff))
            .build();
      }
    } else if (registerDto.getTypeOfUser().equals(RegisterRequestCustomBodyDto.TypeOfUser.STUDENT)) {
      Student student = convertFromCustomToStudent(registerDto);
      if (!studentRepository.findByUsername(student.getUsername()).isPresent()) {
        String generatedPassword = PasswordGenerator.generateRandomPassword(8);
        student.setPassword(generatedPassword);
        OpenPassword op = OpenPassword.builder().password(generatedPassword).account(student).build();
        studentRepository.save(student);
        openPasswordRepository.save(op);
        registerResponseDto = RegisterResponseDto.builder()
            .registeredStudents(List.of(student))
            .build();
      } else {
        System.out.println("This is already present");
        registerResponseDto = RegisterResponseDto.builder()
            .existingStudents(List.of(student))
            .build();
      }
    }

    return registerResponseDto;
  }

  private Staff convertFromCustomToStaff(RegisterRequestCustomBodyDto registerDto) {
    val staff = Staff.builder()
        .firstName(registerDto.getFirstName())
        .lastName(registerDto.getLastName())
        .username(registerDto.getUsername())
        .gender(GenderEnum.fromName(registerDto.getGender()))
        .department(registerDto.getDepartment())
        .courseLoad(registerDto.getCourseLoad());
    if (registerDto.getEmail() != null && !registerDto.getEmail().equals("")) {
      staff.email(registerDto.getEmail());
    }

    return staff.build();

  }

  private Student convertFromCustomToStudent(RegisterRequestCustomBodyDto registerDto) {
    val student = Student.builder()
        .firstName(registerDto.getFirstName())
        .lastName(registerDto.getLastName())
        .username(registerDto.getUsername())
        .gender(GenderEnum.fromName(registerDto.getGender()))
        .department(registerDto.getDepartment())
        .stream(registerDto.getStream())
        .grade(registerDto.getGrade());
    if (registerDto.getEmail() != null && !registerDto.getEmail().equals("")) {
      student.email(registerDto.getEmail());
    }

    return student.build();
  }

  private boolean checkCustomBodyIntegrity(RegisterRequestCustomBodyDto registerDto) {
    if (registerDto.getTypeOfUser() == null)
      return false;
    if (registerDto.getFirstName() == null || registerDto.getFirstName().equals(""))
      return false;
    if (registerDto.getLastName() == null || registerDto.getLastName().equals(""))
      return false;
    if (registerDto.getUsername() == null || registerDto.getUsername().equals(""))
      return false;
    if (registerDto.getGender() == null || registerDto.getGender().equals(""))
      return false;
    if (registerDto.getTypeOfUser().equals(RegisterRequestCustomBodyDto.TypeOfUser.STAFF)) {
      if (registerDto.getDepartment() == null || registerDto.getDepartment().equals(""))
        return false;
      if (registerDto.getCourseLoad() == null)
        return false;
    }
    if (registerDto.getTypeOfUser().equals(RegisterRequestCustomBodyDto.TypeOfUser.STUDENT)) {
      if (registerDto.getDepartment() == null || registerDto.getDepartment().equals(""))
        return false;
      if (registerDto.getGrade() == null)
        return false;
    }

    return true;

  }

}
