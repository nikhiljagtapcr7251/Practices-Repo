package com.example.employees;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

class EmployeeResponseTest {
    @Test
    void mapsEmployeeFieldsAndDerivesSalaryFromDesignation() {
        Employee employee = new Employee("Avery Lee", "avery@example.com", Designation.TEAM_LEAD);

        EmployeeResponse response = EmployeeResponse.from(employee);

        assertThat(response.name()).isEqualTo("Avery Lee");
        assertThat(response.email()).isEqualTo("avery@example.com");
        assertThat(response.designation()).isEqualTo(Designation.TEAM_LEAD);
        assertThat(response.salary()).isEqualByComparingTo(new BigDecimal("130000"));
    }
}