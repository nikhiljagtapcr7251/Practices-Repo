package com.example.employees;

import java.math.BigDecimal;

public enum Designation {
    INTERN("30000"),
    JUNIOR_DEVELOPER("60000"),
    SENIOR_DEVELOPER("100000"),
    TEAM_LEAD("130000"),
    MANAGER("150000");

    private final BigDecimal salary;

    Designation(String salary) {
        this.salary = new BigDecimal(salary);
    }

    public BigDecimal getSalary() {
        return salary;
    }
}
