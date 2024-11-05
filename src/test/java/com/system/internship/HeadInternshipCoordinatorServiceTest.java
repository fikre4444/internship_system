package com.system.internship;

import com.system.internship.domain.InternshipOpportunity;
import com.system.internship.dto.InternshipOpportunityDto;
import com.system.internship.enums.DepartmentEnum;
import com.system.internship.repository.InternshipOpportunityRepository;
import com.system.internship.services.HeadInternshipCoordinatorService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Map;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

public class HeadInternshipCoordinatorServiceTest {

  @InjectMocks
  private HeadInternshipCoordinatorService coordinatorService;

  @Mock
  private InternshipOpportunityRepository ioRepo;

  @BeforeEach
  public void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  public void testSaveInternshipOpportunity() {
    // Arrange
    InternshipOpportunityDto dto = new InternshipOpportunityDto("Company", "Location", DepartmentEnum.MECHANICAL, 5,
        "MU_PROVIDED", true, "OPEN");
    InternshipOpportunity expectedIo = InternshipOpportunity.builder()
        .companyName("Company")
        .location("Location")
        .department(DepartmentEnum.MECHANICAL)
        .noOfStudents(5)
        .typeOfInternship("MU_PROVIDED")
        .pocketMoney(true)
        .internshipStatus("OPEN")
        .uniqueIdentifier("generatedHash")
        .build();

    when(ioRepo.save(any(InternshipOpportunity.class))).thenReturn(expectedIo);

    // Act
    Map<String, Object> result = coordinatorService.saveInternshipOpportunity(dto);

    // Assert
    assertEquals("success", result.get("result"));
    assertEquals("posted internship successfully.", result.get("message"));
    assertEquals(expectedIo, result.get("internshipOpportunity"));
    verify(ioRepo, times(1)).save(any(InternshipOpportunity.class));
  }

  @Test
  public void testSaveInternshipOpportunities() {
    // Arrange
    InternshipOpportunityDto dto1 = new InternshipOpportunityDto("Company1", "Location1", DepartmentEnum.MECHANICAL, 5,
        "MU_PROVIDED", true, "OPEN");
    InternshipOpportunityDto dto2 = new InternshipOpportunityDto("Company2", "Location2", DepartmentEnum.CHEMICAL, 3,
        "MU_PROVIDED", false, "CLOSED");
    List<InternshipOpportunityDto> dtoList = List.of(dto1, dto2);

    InternshipOpportunity io1 = InternshipOpportunity.builder()
        .companyName("Company1")
        .location("Location1")
        .department(DepartmentEnum.MECHANICAL)
        .noOfStudents(5)
        .typeOfInternship("MU_PROVIDED")
        .pocketMoney(true)
        .internshipStatus("OPEN")
        .uniqueIdentifier("generatedHash1")
        .build();

    InternshipOpportunity io2 = InternshipOpportunity.builder()
        .companyName("Company2")
        .location("Location2")
        .department(DepartmentEnum.CHEMICAL)
        .noOfStudents(3)
        .typeOfInternship("MU_PROVIDED")
        .pocketMoney(false)
        .internshipStatus("CLOSED")
        .uniqueIdentifier("generatedHash2")
        .build();

    when(ioRepo.saveAll(anyList())).thenReturn(List.of(io1, io2));

    // Act
    Map<String, Object> result = coordinatorService.saveInternshipOpportunities(dtoList);

    // Assert
    assertEquals("success", result.get("result"));
    assertEquals("posted internship successfully.", result.get("message"));
    assertEquals(List.of(io1, io2), result.get("internshipOpportunities"));
    verify(ioRepo, times(1)).saveAll(anyList());
  }

}
