package com.example.orderkafka;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

@ExtendWith(MockitoExtension.class)
class OrderEventPublisherTest {
    @Mock
    private KafkaTemplate<String, OrderCreatedEvent> kafkaTemplate;

    @Test
    void publishesEventAndWaitsForBrokerAcknowledgement() {
        OrderCreatedEvent event = event();
        when(kafkaTemplate.send("orders.created", event.orderId().toString(), event))
                .thenReturn(CompletableFuture.completedFuture(null));

        publisher().publish(event);

        verify(kafkaTemplate).send("orders.created", event.orderId().toString(), event);
    }

    @Test
    void reportsBrokerFailure() {
        OrderCreatedEvent event = event();
        when(kafkaTemplate.send("orders.created", event.orderId().toString(), event))
                .thenReturn(CompletableFuture.failedFuture(new IllegalStateException("broker unavailable")));

        assertThatThrownBy(() -> publisher().publish(event))
                .isInstanceOf(OrderEventPublishException.class)
                .hasCauseInstanceOf(IllegalStateException.class);
    }

    @Test
    void rejectsNullEvent() {
        assertThatThrownBy(() -> publisher().publish(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("event must not be null");
    }

    private OrderEventPublisher publisher() {
        return new OrderEventPublisher(kafkaTemplate, "orders.created", Duration.ofSeconds(1));
    }

    private OrderCreatedEvent event() {
        return new OrderCreatedEvent(
                UUID.fromString("34d66b51-ef8c-4c5c-90b2-a2e4e74e2ca9"),
                "Avery Lee",
                "Keyboard",
                2,
                new BigDecimal("49.95"),
                new BigDecimal("99.90")
        );
    }
}
