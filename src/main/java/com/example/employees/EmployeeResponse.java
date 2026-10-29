package com.example.employees;

import java.math.BigDecimal;

public record EmployeeResponse(
        Long id,
        String name,
        String email,
        Designation designation,
        BigDecimal salary
) {
    public static EmployeeResponse from(Employee employee) {
        return new EmployeeResponse(
                employee.getId(),
                employee.getName(),
                employee.getEmail(),
                employee.getDesignation(),
                employee.getDesignation().getSalary()
        );
    }
}
