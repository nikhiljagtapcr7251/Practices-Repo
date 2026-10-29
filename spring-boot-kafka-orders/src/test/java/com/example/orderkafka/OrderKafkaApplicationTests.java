package com.example.orderkafka;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.Duration;

import org.awaitility.Awaitility;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.context.EmbeddedKafka;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@EmbeddedKafka(
        partitions = 1,
        topics = "orders.created",
        bootstrapServersProperty = "spring.kafka.bootstrap-servers"
)
class OrderKafkaApplicationTests {
    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderStatusStore statusStore;

    @Test
    void applicationContextLoads() {
        assertThat(orderService).isNotNull();
    }

    @Test
    void publishedOrderEventIsConsumedAndMarkedProcessed() {
        OrderResponse accepted = orderService.create(
                new OrderRequest("Avery Lee", "Keyboard", 2, new BigDecimal("49.95")));

        Awaitility.await()
                .atMost(Duration.ofSeconds(10))
                .untilAsserted(() -> assertThat(statusStore.findById(accepted.orderId()))
                        .hasValueSatisfying(response -> assertThat(response.status()).isEqualTo("PROCESSED")));
    }
}
