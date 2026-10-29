package com.example.employees;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record EmployeeRequest(
        @NotBlank String name,
        @NotBlank @Email String email,
        @NotNull Designation designation
) {
}
