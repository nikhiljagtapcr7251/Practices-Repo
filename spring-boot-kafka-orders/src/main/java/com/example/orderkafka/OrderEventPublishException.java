package com.example.orderkafka;

public class OrderEventPublishException extends RuntimeException {
    public OrderEventPublishException(String message, Throwable cause) {
        super(message, cause);
    }
}
