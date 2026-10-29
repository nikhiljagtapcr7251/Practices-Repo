package com.example.orderkafka;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;

class OrderRequestTest {
    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    void acceptsValidOrderRequest() {
        OrderRequest request = new OrderRequest("Avery Lee", "Keyboard", 2, new BigDecimal("49.95"));

        assertThat(validator.validate(request)).isEmpty();
    }

    @Test
    void rejectsBlankCustomerAndProductNames() {
        OrderRequest request = new OrderRequest(" ", "", 1, new BigDecimal("10.00"));

        assertThat(validator.validate(request))
            .extracting(error -> error.getPropertyPath().toString())
            .contains("customerName", "productName");
    }

    @Test
    void rejectsZeroOrNegativeQuantity() {
        assertThat(validator.validate(new OrderRequest("Avery", "Keyboard", 0, new BigDecimal("10.00"))))
                .anySatisfy(error -> assertThat(error.getPropertyPath().toString()).isEqualTo("quantity"));
        assertThat(validator.validate(new OrderRequest("Avery", "Keyboard", -1, new BigDecimal("10.00"))))
                .anySatisfy(error -> assertThat(error.getPropertyPath().toString()).isEqualTo("quantity"));
    }

    @Test
    void rejectsNullOrNonPositiveUnitPrice() {
        assertThat(validator.validate(new OrderRequest("Avery", "Keyboard", 1, null)))
                .anySatisfy(error -> assertThat(error.getPropertyPath().toString()).isEqualTo("unitPrice"));
        assertThat(validator.validate(new OrderRequest("Avery", "Keyboard", 1, BigDecimal.ZERO)))
                .anySatisfy(error -> assertThat(error.getPropertyPath().toString()).isEqualTo("unitPrice"));
    }
}
