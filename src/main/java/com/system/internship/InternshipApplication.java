package com.system.internship;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;

import com.system.internship.domain.InternshipOpportunity;
import com.system.internship.domain.Student;
import com.system.internship.enums.DepartmentEnum;
import com.system.internship.repository.InternshipApplicationRepository;
import com.system.internship.repository.InternshipOpportunityRepository;
import com.system.internship.repository.StudentRepository;

@EnableAsync
@SpringBootApplication
public class InternshipApplication {

	@Autowired
	StudentRepository studentRepository;

	@Autowired
	InternshipOpportunityRepository ioRepo;

	@Autowired
	InternshipApplicationRepository iaRepo;

	public static void main(String[] args) {
		SpringApplication.run(InternshipApplication.class, args);
	}

	// @Bean
	// public CommandLineRunner commandLineRunner() {
	// return args -> {
	// // List<Student> students =
	// studentRepository.findByDepartment(DepartmentEnum.MECHANICAL);
	// // students.forEach(student -> {
	// // List<com.system.internship.domain.InternshipApplication>
	// internshipApplications = applyForStudent(student);
	// // iaRepo.saveAll(internshipApplications);
	// // });
	// };
	// }

	public List<com.system.internship.domain.InternshipApplication> applyForStudent(Student student) {
		List<com.system.internship.domain.InternshipApplication> listApplications = new ArrayList<>();
		List<InternshipOpportunity> listOpportunities = ioRepo.findAllByDepartment(student.getDepartment()).stream()
				.filter(internshipOpportunity -> internshipOpportunity.getTypeOfInternship().equals("MU_PROVIDED"))
				.collect(Collectors.toList());
		int[] sequentialNumbers = new int[listOpportunities.size()];
		// generate random numbers
		for (int i = 0; i < listOpportunities.size(); i++) {
			sequentialNumbers[i] = i + 1;
		}

		int[] priorities = shuffleArray(sequentialNumbers);
		for (int i = 0; i < listOpportunities.size(); i++) {
			listApplications.add(com.system.internship.domain.InternshipApplication.builder().student(student)
					.internshipOpportunity(listOpportunities.get(i)).priority(priorities[i]).build());
		}
		System.out.println("The priority for he student " + student.getFirstName() + " is ");
		for (int i = 0; i < priorities.length; i++) {
			System.out.print(priorities[i] + " ");
		}
		System.out.println("we are done");

		return listApplications;
	}

	public static int[] shuffleArray(int[] array) {
		// Convert the int[] array to a List<Integer>
		List<Integer> list = new ArrayList<>();
		for (int num : array) {
			list.add(num);
		}

		// Shuffle the list
		Collections.shuffle(list);

		// Convert the List<Integer> back to int[]
		for (int i = 0; i < array.length; i++) {
			array[i] = list.get(i);
		}

		return array; // Return the shuffled array
	}

}
