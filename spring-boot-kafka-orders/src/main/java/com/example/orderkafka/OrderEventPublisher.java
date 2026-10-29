package com.example.orderkafka;

import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class OrderEventPublisher {
    private final KafkaTemplate<String, OrderCreatedEvent> kafkaTemplate;
    private final String topicName;
    private final long publishTimeoutMillis;

    public OrderEventPublisher(
            KafkaTemplate<String, OrderCreatedEvent> kafkaTemplate,
            @Value("${app.kafka.orders-created-topic}") String topicName,
            @Value("${app.kafka.publish-timeout}") java.time.Duration publishTimeout
    ) {
        this.kafkaTemplate = kafkaTemplate;
        this.topicName = topicName;
        this.publishTimeoutMillis = publishTimeout.toMillis();
    }

    public void publish(OrderCreatedEvent event) {
        Objects.requireNonNull(event, "event must not be null");
        String key = Objects.requireNonNull(event.orderId(), "orderId must not be null").toString();

        try {
            kafkaTemplate.send(topicName, key, event).get(publishTimeoutMillis, TimeUnit.MILLISECONDS);
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new OrderEventPublishException("Interrupted while publishing order event", exception);
        } catch (ExecutionException exception) {
            throw new OrderEventPublishException("Could not publish order event", exception.getCause());
        } catch (TimeoutException | RuntimeException exception) {
            throw new OrderEventPublishException("Could not publish order event", exception);
        }
    }
}
