package com.example.orderkafka;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {
    @Mock
    private OrderEventPublisher eventPublisher;

    @Mock
    private OrderStatusStore statusStore;

    @InjectMocks
    private OrderService orderService;

    @Test
    void createsOrderAndPublishesCalculatedTotal() {
        OrderResponse response = orderService.create(
                new OrderRequest("Avery Lee", "Keyboard", 2, new BigDecimal("49.95")));

        assertThat(response.status()).isEqualTo("ACCEPTED");
        ArgumentCaptor<OrderCreatedEvent> eventCaptor = ArgumentCaptor.forClass(OrderCreatedEvent.class);
        verify(eventPublisher).publish(eventCaptor.capture());
        assertThat(eventCaptor.getValue().orderId()).isEqualTo(response.orderId());
        assertThat(eventCaptor.getValue().totalPrice()).isEqualByComparingTo(new BigDecimal("99.90"));
    }

    @Test
    void doesNotAcceptOrderWhenPublishingFails() {
        doThrow(new OrderEventPublishException("Kafka unavailable", new IllegalStateException()))
            .when(eventPublisher).publish(any(OrderCreatedEvent.class));

        assertThatThrownBy(() -> orderService.create(
                new OrderRequest("Avery Lee", "Keyboard", 1, new BigDecimal("49.95"))))
                .isInstanceOf(OrderEventPublishException.class);
    }

    @Test
    void returnsProcessedStatusWhenPresent() {
        UUID orderId = UUID.randomUUID();
        OrderResponse processed = new OrderResponse(orderId, "PROCESSED", "Order event consumed by Kafka");
        when(statusStore.findById(orderId)).thenReturn(Optional.of(processed));

        assertThat(orderService.getStatus(orderId)).isEqualTo(processed);
    }

    @Test
    void returnsNotFoundWhenOrderHasNotBeenProcessed() {
        UUID orderId = UUID.randomUUID();
        when(statusStore.findById(orderId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> orderService.getStatus(orderId))
                .isInstanceOfSatisfying(ResponseStatusException.class,
                        exception -> assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND));
    }
}
