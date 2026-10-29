# Spring Boot Kafka Orders

A standalone Spring Boot 3 REST API demonstrating an order event moving through Kafka.

## Use case

1. `POST /api/orders` validates an order, calculates its total, and publishes an `OrderCreatedEvent` to Kafka topic `orders.created`.
2. The Kafka listener consumes the event and marks the order `PROCESSED` in an in-memory status store.
3. `GET /api/orders/{orderId}` returns the processing status. It can return `404` briefly while the asynchronous consumer is catching up.

The status store is in memory and resets when the app stops. This is a learning demo, not durable order storage.

## Requirements

- Java 21
- Maven 3.6.3+
- Docker with Docker Compose

## Start Kafka and the API

From this project directory:

```bash
docker compose up -d kafka
mvn spring-boot:run
```

The API listens on `http://localhost:8082`.

## Create an order

```bash
curl -X POST http://localhost:8082/api/orders \
  -H "Content-Type: application/json" \
  -d '{"customerName":"Avery Lee","productName":"Keyboard","quantity":2,"unitPrice":49.95}'
```

A successful request returns `202 Accepted` with an `orderId`. Check its status with:

```bash
curl http://localhost:8082/api/orders/ORDER_ID_FROM_RESPONSE
```

The final response has status `PROCESSED`. Invalid names, quantity below 1, or a price below 0.01 return `400`; a Kafka publish failure returns `503`.

## Tests

Run all unit, MVC, and embedded-Kafka integration tests with:

```bash
mvn test
```

The tests use Spring Kafka's embedded broker, so Docker is not required for the test suite. See [TEST_CASES.md](TEST_CASES.md) for positive and negative scenarios.

Stop Kafka with:

```bash
docker compose down
```
