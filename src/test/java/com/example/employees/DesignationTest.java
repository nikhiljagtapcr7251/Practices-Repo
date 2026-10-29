package com.example.employees;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

class DesignationTest {
    @Test
    void exposesConfiguredSalaryForEveryDesignation() {
        assertThat(Designation.values()).containsExactly(
                Designation.INTERN,
                Designation.JUNIOR_DEVELOPER,
                Designation.SENIOR_DEVELOPER,
                Designation.TEAM_LEAD,
                Designation.MANAGER
        );
        assertThat(Designation.INTERN.getSalary()).isEqualByComparingTo(new BigDecimal("30000"));
        assertThat(Designation.JUNIOR_DEVELOPER.getSalary()).isEqualByComparingTo(new BigDecimal("60000"));
        assertThat(Designation.SENIOR_DEVELOPER.getSalary()).isEqualByComparingTo(new BigDecimal("100000"));
        assertThat(Designation.TEAM_LEAD.getSalary()).isEqualByComparingTo(new BigDecimal("130000"));
        assertThat(Designation.MANAGER.getSalary()).isEqualByComparingTo(new BigDecimal("150000"));
    }
}