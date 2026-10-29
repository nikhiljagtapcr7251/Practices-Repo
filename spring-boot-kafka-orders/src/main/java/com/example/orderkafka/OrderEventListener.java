package com.example.orderkafka;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class OrderEventListener {
    private final OrderStatusStore statusStore;

    public OrderEventListener(OrderStatusStore statusStore) {
        this.statusStore = statusStore;
    }

    @KafkaListener(topics = "${app.kafka.orders-created-topic}")
    public void onOrderCreated(OrderCreatedEvent event) {
        statusStore.markProcessed(event);
    }
}
