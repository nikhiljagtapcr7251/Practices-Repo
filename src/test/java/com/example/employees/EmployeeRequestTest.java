package com.example.employees;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;

class EmployeeRequestTest {
    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    void acceptsValidRequest() {
        EmployeeRequest request = new EmployeeRequest("Avery Lee", "avery@example.com", Designation.TEAM_LEAD);

        assertThat(validator.validate(request)).isEmpty();
    }

    @Test
    void rejectsBlankNameInvalidEmailAndMissingDesignation() {
        EmployeeRequest request = new EmployeeRequest("  ", "not-an-email", null);

        assertThat(validator.validate(request)).hasSize(3);
    }
}