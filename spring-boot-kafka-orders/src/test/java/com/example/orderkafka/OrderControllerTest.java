package com.example.orderkafka;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

@WebMvcTest(OrderController.class)
@Import(OrderApiExceptionHandler.class)
class OrderControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private OrderService orderService;

    @Test
    void acceptsValidOrder() throws Exception {
        UUID orderId = UUID.randomUUID();
        when(orderService.create(any(OrderRequest.class)))
                .thenReturn(new OrderResponse(orderId, "ACCEPTED", "Order event published"));

        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"customerName":"Avery Lee","productName":"Keyboard","quantity":2,"unitPrice":49.95}
                                """))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.orderId").value(orderId.toString()))
                .andExpect(jsonPath("$.status").value("ACCEPTED"));

        verify(orderService).create(any(OrderRequest.class));
    }

    @Test
    void rejectsInvalidOrderWithoutCallingService() throws Exception {
        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"customerName":" ","productName":"Keyboard","quantity":0,"unitPrice":0}
                                """))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(orderService);
    }

    @Test
    void returnsServiceUnavailableWhenKafkaPublishFails() throws Exception {
        when(orderService.create(any(OrderRequest.class)))
                .thenThrow(new OrderEventPublishException("Kafka unavailable", new IllegalStateException()));

        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"customerName":"Avery Lee","productName":"Keyboard","quantity":1,"unitPrice":49.95}
                                """))
                .andExpect(status().isServiceUnavailable())
                .andExpect(jsonPath("$.detail").value("The order could not be published to Kafka. Please retry."));
    }

    @Test
    void returnsProcessedOrderStatus() throws Exception {
        UUID orderId = UUID.randomUUID();
        when(orderService.getStatus(orderId))
                .thenReturn(new OrderResponse(orderId, "PROCESSED", "Order event consumed by Kafka"));

        mockMvc.perform(get("/api/orders/{orderId}", orderId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("PROCESSED"));
    }

    @Test
    void returnsNotFoundForUnprocessedOrder() throws Exception {
        UUID orderId = UUID.randomUUID();
        when(orderService.getStatus(orderId)).thenThrow(
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Order has not been processed"));

        mockMvc.perform(get("/api/orders/{orderId}", orderId))
                .andExpect(status().isNotFound());
    }

    @Test
    void rejectsMalformedOrderId() throws Exception {
        mockMvc.perform(get("/api/orders/not-a-uuid"))
                .andExpect(status().isBadRequest());
    }
}
