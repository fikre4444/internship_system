package com.system.internship.services.strategy;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.client.RestTemplate;

import com.system.internship.domain.Account;
import com.system.internship.domain.OpenPassword;
import com.system.internship.domain.Role;
import com.system.internship.domain.Staff;
import com.system.internship.domain.Student;
import com.system.internship.dto.RegisterRequestCustomBodyDto;
import com.system.internship.dto.RegisterResponseDto;
import com.system.internship.dto.StudentDto;
import com.system.internship.enums.GenderEnum;
import com.system.internship.enums.RoleEnum;
import com.system.internship.repository.OpenPasswordRepository;
import com.system.internship.repository.StudentRepository;
import com.system.internship.services.AccountService;
import com.system.internship.services.RoleService;
import com.system.internship.util.DepartmentUtil;
import com.system.internship.util.PasswordGenerator;

import lombok.val;

public class StudentRegistrationStrategy implements AccountRegistrationStrategy {

  private final RestTemplate restTemplate;
  private final StudentRepository studentRepository;
  private final OpenPasswordRepository openPasswordRepository;
  private final PasswordEncoder passwordEncoder;
  private final RoleService roleService;
  private final AccountService accountService;

  public StudentRegistrationStrategy(RestTemplate restTemplate, StudentRepository studentRepository,
      PasswordEncoder passwordEncoder, OpenPasswordRepository openPasswordRepository,
      RoleService roleService, AccountService accountService) {
    this.restTemplate = restTemplate;
    this.studentRepository = studentRepository;
    this.passwordEncoder = passwordEncoder;
    this.openPasswordRepository = openPasswordRepository;
    this.roleService = roleService;
    this.accountService = accountService;
  }

  @Override
  public RegisterResponseDto registerBatch(URI uri) {
    StudentDto[] studentDtos = restTemplate.getForObject(uri, StudentDto[].class);
    List<Student> students = convertToStudents(studentDtos);
    List<Student> existingStudents = studentRepository.findAllByUsernameIn(students.stream()
        .map(Student::getUsername)
        .collect(Collectors.toList()));
    List<Student> unRegisteredStudents = students.stream()
        .filter(student -> !existingStudents.contains(student)).collect(Collectors.toList());

    generateAndSavePasswordsForNewUsers(unRegisteredStudents, studentRepository, openPasswordRepository,
        passwordEncoder);
    return RegisterResponseDto.builder().existingStudents(existingStudents).registeredStudents(unRegisteredStudents)
        .build();
  }

  @Override
  public RegisterResponseDto registerSingle(URI uri) {
    StudentDto studentDto = restTemplate.getForObject(uri, StudentDto.class);
    Student student = convertToStudent(studentDto);
    Optional<Student> existingStudent = studentRepository.findByUsername(student.getUsername());
    if (existingStudent.isEmpty()) {
      String generatedPassword = PasswordGenerator.generateRandomPassword(8);
      student.setPassword(passwordEncoder.encode(generatedPassword));
      OpenPassword op = OpenPassword.builder().password(generatedPassword).account(student).build();
      studentRepository.save(student);
      openPasswordRepository.save(op);
      return RegisterResponseDto.builder().registeredStudents(List.of(student)).build();
    } else {
      return RegisterResponseDto.builder().existingStudents(List.of(student)).build();
    }
  }

  @Override
  public RegisterResponseDto registerCustom(RegisterRequestCustomBodyDto registerDto) {
    val rrd = RegisterResponseDto.builder();
    Student student = convertFromCustomToStudent(registerDto);
    Account account = accountService.checkAccountExistenceFromUsername(student.getUsername());
    if (account == null) {
      String generatedPassword = PasswordGenerator.generateRandomPassword(8);
      student.setPassword(passwordEncoder.encode(generatedPassword));
      OpenPassword op = OpenPassword.builder().password(generatedPassword).account(student).build();
      student = studentRepository.save(student);
      openPasswordRepository.save(op);
      rrd.registeredStudents(List.of(student));
    } else {
      if (account instanceof Student) {
        rrd.existingStudents(List.of((Student) account));
      } else if (account instanceof Staff) {
        rrd.existingStaffs(List.of((Staff) account));
      }
    }
    return rrd.build();
  }

  public RegisterResponseDto registerCustomMultiple(List<RegisterRequestCustomBodyDto> registerDtoMultiple) {
    List<Student> registeredStudents = new ArrayList<>();
    List<Student> existingStudents = new ArrayList<>();
    List<Staff> existingStaff = new ArrayList<>();
    List<OpenPassword> openPasswords = new ArrayList<>();
    val rrd = RegisterResponseDto.builder();
    registerDtoMultiple.forEach(registerDto -> {
      Student student = convertFromCustomToStudent(registerDto);
      Account account = accountService.checkAccountExistenceFromUsername(student.getUsername());
      if (account == null) { // if account doesn't exist
        String generatedPassword = PasswordGenerator.generateRandomPassword(8);
        student.setPassword(passwordEncoder.encode(generatedPassword));
        OpenPassword op = OpenPassword.builder().password(generatedPassword).account(student).build();
        // student = studentRepository.save(student);
        registeredStudents.add(student); // instead of saving add to list
        // openPasswordRepository.save(op);
        openPasswords.add(op);
      } else {
        if (account instanceof Student) {
          existingStudents.add((Student) account);
          // rrd.existingStudents(List.of((Student) account));
        } else if (account instanceof Staff) {
          existingStaff.add((Staff) account);
        }
      }
    });
    studentRepository.saveAll(registeredStudents); // save the students
    openPasswordRepository.saveAll(openPasswords); // save the staff
    rrd.registeredStudents(registeredStudents);
    rrd.existingStudents(existingStudents);
    rrd.existingStaffs(existingStaff);
    return rrd.build();
  }

  private List<Student> convertToStudents(StudentDto[] studentDtos) {
    List<Student> students = new ArrayList<>();
    for (StudentDto studentDto : studentDtos) {
      Student student = convertToStudent(studentDto);
      students.add(student);
    }

    return students;
  }

  private Student convertToStudent(StudentDto studentDto) {
    val student = Student.builder()
        .firstName(studentDto.getFirstName())
        .lastName(studentDto.getLastName())
        .username(studentDto.getUsername())
        .gender(GenderEnum.fromName(studentDto.getGender()))
        .roles(Set.of(roleService.getRole(RoleEnum.ROLE_STUDENT)))
        .department(DepartmentUtil.convertDepartmentStringToEnum(studentDto.getDepartment(), "Estudent"))
        .stream(studentDto.getStream())
        .grade(studentDto.getGrade());
    if (studentDto.getEmail() != null && !studentDto.getEmail().equals("")) {
      student.email(studentDto.getEmail());
    }

    return student.build();
  }

  private Student convertFromCustomToStudent(RegisterRequestCustomBodyDto registerDto) {
    val student = Student.builder()
        .firstName(registerDto.getFirstName())
        .lastName(registerDto.getLastName())
        .username(registerDto.getUsername())
        .gender(GenderEnum.fromName(registerDto.getGender()))
        .roles(Set.of(roleService.getRole(RoleEnum.ROLE_STUDENT)))
        .department(registerDto.getDepartment())
        .stream(registerDto.getStream())
        .grade(registerDto.getGrade());
    if (registerDto.getEmail() != null && !registerDto.getEmail().equals("")) {
      student.email(registerDto.getEmail());
    }

    return student.build();
  }

}
