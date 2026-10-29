package com.example.employees;

import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class EmployeeSalaryApiApplication {
    public static void main(String[] args) {
        SpringApplication.run(EmployeeSalaryApiApplication.class, args);
    }

    @Bean
    CommandLineRunner seedDemoEmployees(EmployeeRepository employeeRepository) {
        return args -> {
            if (employeeRepository.count() == 0) {
                employeeRepository.saveAll(List.of(
                        new Employee("Avery Lee", "avery@example.com", Designation.SENIOR_DEVELOPER),
                        new Employee("Jordan Kim", "jordan@example.com", Designation.TEAM_LEAD),
                        new Employee("Morgan Chen", "morgan@example.com", Designation.MANAGER)
                ));
            }
        };
    }
}
