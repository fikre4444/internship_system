package com.system.internship.services.strategy;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.client.RestTemplate;

import com.system.internship.domain.Account;
import com.system.internship.domain.OpenPassword;
import com.system.internship.domain.Role;
import com.system.internship.domain.Staff;
import com.system.internship.domain.Student;
import com.system.internship.dto.RegisterRequestCustomBodyDto;
import com.system.internship.dto.RegisterResponseDto;
import com.system.internship.dto.StaffDto;
import com.system.internship.enums.GenderEnum;
import com.system.internship.enums.RoleEnum;
import com.system.internship.repository.OpenPasswordRepository;
import com.system.internship.repository.RoleRepository;
import com.system.internship.repository.StaffRepository;
import com.system.internship.services.AccountService;
import com.system.internship.services.RoleService;
import com.system.internship.util.PasswordGenerator;

import lombok.val;

public class StaffRegistrationStrategy implements AccountRegistrationStrategy {

  private final RestTemplate restTemplate;
  private final StaffRepository staffRepository;
  private final PasswordEncoder passwordEncoder;
  private final OpenPasswordRepository openPasswordRepository;
  private final RoleService roleService;
  private final AccountService accountService;

  public StaffRegistrationStrategy(RestTemplate restTemplate, StaffRepository staffRepository,
      PasswordEncoder passwordEncoder, OpenPasswordRepository openPasswordRepository,
      RoleService roleService, AccountService accountService) {
    this.restTemplate = restTemplate;
    this.staffRepository = staffRepository;
    this.passwordEncoder = passwordEncoder;
    this.openPasswordRepository = openPasswordRepository;
    this.roleService = roleService;
    this.accountService = accountService;
  }

  @Override
  public RegisterResponseDto registerBatch(URI uri) {
    StaffDto[] staffDtos = restTemplate.getForObject(uri, StaffDto[].class);
    List<Staff> staffs = convertToStaffs(staffDtos);
    List<Staff> existingStaffs = staffRepository.findAllByUsernameIn(staffs.stream()
        .map(Staff::getUsername)
        .collect(Collectors.toList()));
    List<Staff> unRegisteredStaffs = staffs.stream()
        .filter(staff -> !existingStaffs.contains(staff)).collect(Collectors.toList());

    generateAndSavePasswordsForNewUsers(unRegisteredStaffs, staffRepository, openPasswordRepository, passwordEncoder);
    return RegisterResponseDto.builder().existingStaffs(existingStaffs).registeredStaffs(unRegisteredStaffs).build();
  }

  @Override
  public RegisterResponseDto registerSingle(URI uri) {
    StaffDto staffDto = restTemplate.getForObject(uri, StaffDto.class);
    Staff staff = convertToStaff(staffDto);
    Optional<Staff> existingStaff = staffRepository.findByUsername(staff.getUsername());
    if (existingStaff.isEmpty()) {
      String generatedPassword = PasswordGenerator.generateRandomPassword(8);
      staff.setPassword(passwordEncoder.encode(generatedPassword));
      OpenPassword op = OpenPassword.builder().password(generatedPassword).account(staff).build();
      staffRepository.save(staff);
      openPasswordRepository.save(op);
      return RegisterResponseDto.builder().registeredStaffs(List.of(staff)).build();
    } else {
      return RegisterResponseDto.builder().existingStaffs(List.of(staff)).build();
    }
  }

  @Override
  public RegisterResponseDto registerCustom(RegisterRequestCustomBodyDto registerDto) {
    Staff staff = convertFromCustomToStaff(registerDto);
    Account account = accountService.checkAccountExistenceFromUsername(staff.getUsername());
    if (account == null) { // if the account doesn't exist
      System.out.println("In the register custom empty");
      String generatedPassword = PasswordGenerator.generateRandomPassword(8);
      staff.setPassword(passwordEncoder.encode(generatedPassword));
      OpenPassword op = OpenPassword.builder().password(generatedPassword).account(staff).build();
      staff = staffRepository.save(staff);
      openPasswordRepository.save(op);
      return RegisterResponseDto.builder().registeredStaffs(List.of(staff)).build();
    } else {
      // if it's already preset check the type and set the appropriate
      // registerresponsedto
      val rrd = RegisterResponseDto.builder();
      if (account instanceof Student) {
        rrd.existingStudents(List.of((Student) account));
      } else if (account instanceof Staff) {
        rrd.existingStaffs(List.of((Staff) account));
      }
      return rrd.build();
    }
  }

  private List<Staff> convertToStaffs(StaffDto[] staffDtos) {
    List<Staff> staffs = new ArrayList<>();
    for (StaffDto staffDto : staffDtos) {
      Staff staff = convertToStaff(staffDto);
      staffs.add(staff);
    }

    return staffs;
  }

  private Staff convertToStaff(StaffDto staffDto) {
    val staff = Staff.builder()
        .firstName(staffDto.getFirstName())
        .lastName(staffDto.getLastName())
        .username(staffDto.getUsername())
        .roles(Set.of(roleService.getRole(RoleEnum.ROLE_STAFF)))
        .gender(GenderEnum.fromName(staffDto.getGender()))
        .department(staffDto.getDepartment())
        .courseLoad(staffDto.getCourseLoad());
    if (staffDto.getEmail() != null && !staffDto.getEmail().equals("")) {
      staff.email(staffDto.getEmail());
    }

    return staff.build();
  }

  private Staff convertFromCustomToStaff(RegisterRequestCustomBodyDto registerDto) {
    val staff = Staff.builder()
        .firstName(registerDto.getFirstName())
        .lastName(registerDto.getLastName())
        .username(registerDto.getUsername())
        .gender(GenderEnum.fromName(registerDto.getGender()))
        .roles(Set.of(roleService.getRole(RoleEnum.ROLE_STAFF)))
        .department(registerDto.getDepartment())
        .courseLoad(registerDto.getCourseLoad());
    if (registerDto.getEmail() != null && !registerDto.getEmail().equals("")) {
      staff.email(registerDto.getEmail());
    }

    return staff.build();

  }

}
