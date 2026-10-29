package com.example.employees;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class EmployeeSalaryApiApplicationTests {
    @Autowired
    private EmployeeRepository employeeRepository;

    @Test
    void applicationContextLoads() {
    }

    @Test
    void startupSeedsDemoEmployeesWhenDatabaseIsEmpty() {
        assertThat(employeeRepository.findAll())
                .extracting(Employee::getEmail)
                .containsExactlyInAnyOrder(
                        "avery@example.com",
                        "jordan@example.com",
                        "morgan@example.com"
                );
    }
}
