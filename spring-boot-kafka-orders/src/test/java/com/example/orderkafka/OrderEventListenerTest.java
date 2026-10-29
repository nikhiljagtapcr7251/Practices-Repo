package com.example.orderkafka;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.UUID;

import org.junit.jupiter.api.Test;

class OrderEventListenerTest {
    @Test
    void consumesEventAndUpdatesOrderStatus() {
        OrderStatusStore store = new OrderStatusStore();
        OrderEventListener listener = new OrderEventListener(store);
        UUID orderId = UUID.randomUUID();

        listener.onOrderCreated(new OrderCreatedEvent(
                orderId, "Avery", "Keyboard", 1, new BigDecimal("10.00"), new BigDecimal("10.00")));

        assertThat(store.findById(orderId)).hasValueSatisfying(
                response -> assertThat(response.status()).isEqualTo("PROCESSED"));
    }

    @Test
    void rejectsEventWithoutOrderId() {
        OrderEventListener listener = new OrderEventListener(new OrderStatusStore());

        org.assertj.core.api.Assertions.assertThatThrownBy(() -> listener.onOrderCreated(
                        new OrderCreatedEvent(null, "Avery", "Keyboard", 1, BigDecimal.ONE, BigDecimal.ONE)))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
