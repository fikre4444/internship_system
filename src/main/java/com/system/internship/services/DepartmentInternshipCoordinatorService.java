package com.system.internship.services;

import java.util.Map;
import java.util.Optional;
import java.util.List;
import java.util.stream.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.system.internship.domain.Account;
import com.system.internship.domain.InternshipOpportunity;
import com.system.internship.domain.Staff;
import com.system.internship.domain.Student;
import com.system.internship.exception.UsernameNotFoundException;
import com.system.internship.repository.AccountRepository;
import com.system.internship.repository.InternshipOpportunityRepository;
import com.system.internship.repository.StudentRepository;
import com.system.internship.services.bot.TelegramBot;
import com.system.internship.util.HashUtil;

@Service
public class DepartmentInternshipCoordinatorService {

  @Autowired
  AccountRepository accountRepo;
  @Autowired
  StudentRepository studentRepo;
  @Autowired
  InternshipOpportunityRepository internshipOpRepo;
  @Autowired
  TelegramBot telegramBot;

  public Map<String, Object> addSelfInternship(Map<String, String> requestBody) {
    // TODO fix it when you add the same self internship
    // maybe just don't save it and then just increment the noOfStudents
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
    io.setUniqueIdentifier(
        HashUtil.generateHashFromInternshipOpportunitySelf(companyName, location, student.getDepartment(),
            student.getUsername()));
    // save the internship opportunity and get the saved one
    InternshipOpportunity savedOne = internshipOpRepo.save(io);
    student.setAssignedInternship(savedOne); // set the svaed one to the student
    student.setAssignedInternshipStatus("PENDING");
    studentRepo.save(student);
    return Map.of("result", "success", "message", "the student has been assigned their own internship successfully.",
        "internship", savedOne);
  }

  public List<Account> getStudents(String searchTerm) {
    List<Account> accounts = accountRepo.findBySearchTerm(searchTerm);
    nullifyPasswords(accounts); // to hide the passwords
    // so that he doesn't include staff and himself here.
    accounts = accounts.stream().filter(account -> {
      return (account instanceof Student && account.isEnabled());
    }).collect(Collectors.toList());
    // only send the students with same department as the department coordinator
    List<Account> filteredAccounts = filterByMyDepartment(accounts);
    return filteredAccounts;
  }

  public void nullifyPasswords(List<Account> accounts) {
    accounts.forEach(account -> {
      account.setPassword(null);
    });
  }

  public List<Account> filterByMyDepartment(List<Account> accounts) {
    Account currentAccount = (Account) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    Staff departmentCoodinator = (Staff) currentAccount;
    List<Account> filteredAccount = accounts.stream().filter(account -> {
      Student student = (Student) account;
      return student.getDepartment().equals(departmentCoodinator.getDepartment());
    }).collect(Collectors.toList());
    return filteredAccount;
  }

  public Map<String, Object> editSelfInternship(Map<String, String> requestBody) {
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
    InternshipOpportunity existingIo = student.getAssignedInternship();
    // create the internship opportunity
    InternshipOpportunity io = InternshipOpportunity.builder()
        .companyName(companyName).department(student.getDepartment()).internshipStatus("FILLED")
        .location(location).typeOfInternship("SELF_PROVIDED").build();
    io.setUniqueIdentifier(
        HashUtil.generateHashFromInternshipOpportunitySelf(companyName, location, student.getDepartment(),
            student.getUsername()));
    // save the internship opportunity and get the saved one
    InternshipOpportunity savedOne = internshipOpRepo.save(io);
    student.setAssignedInternship(savedOne); // set the svaed one to the student
    student.setAssignedInternshipStatus("PENDING");
    studentRepo.save(student);
    // delete the existing internship
    internshipOpRepo.delete(existingIo);
    return Map.of("result", "success", "message", "the student has been assigned their own internship successfully.",
        "internship", savedOne);
  }

  public Map<String, Object> notifyStudentAboutInternshipAdd(Map<String, String> requestBody) {
    String username = requestBody.get("username");
    String typeOfNotification = requestBody.get("typeOfNotification");
    if (typeOfNotification.equals("add_internship")) {
      Optional<Student> studentOpt = studentRepo.findByUsername(username);
      if (studentOpt.isPresent()) {
        Student student = studentOpt.get();
        long chatId = student.getChatId().getChatId();
        String firstName = student.getFirstName();
        String companyName = student.getAssignedInternship().getCompanyName();
        String location = student.getAssignedInternship().getLocation();
        String message = "Dear <b>" + firstName
            + "</b>, Your Department Coordinator has Added the following Internship (that you have provided to them) for you =>\n\n<b>Company Name:"
            + companyName + "\nLocation:" + location
            + "</b>\n\nIf you have any problems with this please contact your Department Coordinator";
        telegramBot.sendMessage(chatId, message);
      }
    }
    return Map.of("result", "success", "message", "The Student Has been notified!");
  }

}
