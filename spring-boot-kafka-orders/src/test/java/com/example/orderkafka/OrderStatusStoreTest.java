package com.example.orderkafka;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;
import java.util.UUID;

import org.junit.jupiter.api.Test;

class OrderStatusStoreTest {
    private final OrderStatusStore store = new OrderStatusStore();

    @Test
    void recordsProcessedOrder() {
        UUID orderId = UUID.randomUUID();
        store.markProcessed(new OrderCreatedEvent(
                orderId, "Avery", "Keyboard", 1, new BigDecimal("10.00"), new BigDecimal("10.00")));

        assertThat(store.findById(orderId))
                .hasValueSatisfying(response -> assertThat(response.status()).isEqualTo("PROCESSED"));
    }

    @Test
    void returnsEmptyForUnknownOrder() {
        assertThat(store.findById(UUID.randomUUID())).isEmpty();
    }

    @Test
    void rejectsNullEventOrOrderId() {
        assertThatThrownBy(() -> store.markProcessed(null))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> store.markProcessed(
                new OrderCreatedEvent(null, "Avery", "Keyboard", 1, BigDecimal.ONE, BigDecimal.ONE)))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
