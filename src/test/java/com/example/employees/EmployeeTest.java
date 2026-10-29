package com.example.employees;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class EmployeeTest {
    @Test
    void constructorSetsEmployeeFields() {
        Employee employee = new Employee("Avery Lee", "avery@example.com", Designation.INTERN);

        assertThat(employee.getId()).isNull();
        assertThat(employee.getName()).isEqualTo("Avery Lee");
        assertThat(employee.getEmail()).isEqualTo("avery@example.com");
        assertThat(employee.getDesignation()).isEqualTo(Designation.INTERN);
    }

    @Test
    void updateReplacesEditableFields() {
        Employee employee = new Employee("Avery Lee", "avery@example.com", Designation.INTERN);

        employee.update("Jordan Kim", "jordan@example.com", Designation.MANAGER);

        assertThat(employee.getName()).isEqualTo("Jordan Kim");
        assertThat(employee.getEmail()).isEqualTo("jordan@example.com");
        assertThat(employee.getDesignation()).isEqualTo(Designation.MANAGER);
    }
}