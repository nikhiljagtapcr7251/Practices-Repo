package com.example.orderkafka;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.springframework.stereotype.Component;

@Component
public class OrderStatusStore {
    private final ConcurrentMap<UUID, OrderResponse> orders = new ConcurrentHashMap<>();

    public void markProcessed(OrderCreatedEvent event) {
        if (event == null || event.orderId() == null) {
            throw new IllegalArgumentException("Order event and order ID are required");
        }
        orders.put(event.orderId(), new OrderResponse(event.orderId(), "PROCESSED", "Order event consumed by Kafka"));
    }

    public Optional<OrderResponse> findById(UUID orderId) {
        return Optional.ofNullable(orders.get(orderId));
    }
}
