package com.example.orderkafka;

import java.util.UUID;

public record OrderResponse(UUID orderId, String status, String message) {
}
