package com.example.orderkafka;

import java.math.BigDecimal;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class OrderService {
    private final OrderEventPublisher eventPublisher;
    private final OrderStatusStore statusStore;

    public OrderService(OrderEventPublisher eventPublisher, OrderStatusStore statusStore) {
        this.eventPublisher = eventPublisher;
        this.statusStore = statusStore;
    }

    public OrderResponse create(OrderRequest request) {
        UUID orderId = UUID.randomUUID();
        BigDecimal totalPrice = request.unitPrice().multiply(BigDecimal.valueOf(request.quantity()));
        OrderCreatedEvent event = new OrderCreatedEvent(
                orderId,
                request.customerName(),
                request.productName(),
                request.quantity(),
                request.unitPrice(),
                totalPrice
        );
        eventPublisher.publish(event);
        return new OrderResponse(orderId, "ACCEPTED", "Order event published");
    }

    public OrderResponse getStatus(UUID orderId) {
        return statusStore.findById(orderId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order has not been processed"));
    }
}
