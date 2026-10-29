package com.example.employees;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class EmployeeRepositoryTest {
    @Autowired
    private EmployeeRepository employeeRepository;

    @Test
    void savesAndLoadsEmployeeWithDesignation() {
        Employee saved = employeeRepository.saveAndFlush(
                new Employee("Avery Lee", "avery@example.com", Designation.SENIOR_DEVELOPER));

        Employee loaded = employeeRepository.findById(saved.getId()).orElseThrow();

        assertThat(loaded.getName()).isEqualTo("Avery Lee");
        assertThat(loaded.getEmail()).isEqualTo("avery@example.com");
        assertThat(loaded.getDesignation()).isEqualTo(Designation.SENIOR_DEVELOPER);
    }

    @Test
    void deletesEmployee() {
        Employee saved = employeeRepository.saveAndFlush(
                new Employee("Avery Lee", "avery@example.com", Designation.INTERN));

        employeeRepository.delete(saved);

        assertThat(employeeRepository.findById(saved.getId())).isEmpty();
    }
}