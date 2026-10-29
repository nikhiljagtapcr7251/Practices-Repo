# Order Kafka API Test Cases

Run automated tests from this directory with `mvn test`. The suite uses an embedded Kafka broker for the end-to-end message test.

## Positive scenarios

| ID | Scenario | Expected result |
| --- | --- | --- |
| P01 | Submit a valid order with customer, product, positive quantity, and price | `202 Accepted`; response contains a generated order ID and `ACCEPTED` status. |
| P02 | Publish the created order event | Kafka acknowledges the event on `orders.created`, keyed by order ID. |
| P03 | Calculate order total | Quantity 2 at 49.95 produces total 99.90 in the Kafka event. |
| P04 | Consume the Kafka event | Listener stores the order with `PROCESSED` status. |
| P05 | Read processed order status | `GET /api/orders/{id}` returns `200` and `PROCESSED`. |
| P06 | Request validation boundary values | Nonblank names, quantity 1, and unit price 0.01 are accepted. |
| P07 | Application startup | Spring context and Kafka topic configuration load successfully. |

## Negative scenarios

| ID | Scenario | Expected result |
| --- | --- | --- |
| N01 | Blank or whitespace-only customer name | `400 Bad Request`; service is not called. |
| N02 | Blank product name | `400 Bad Request`. |
| N03 | Missing or null unit price | `400 Bad Request`. |
| N04 | Unit price zero or negative | `400 Bad Request`. |
| N05 | Quantity zero or negative | `400 Bad Request`. |
| N06 | Malformed JSON or wrong field type | `400 Bad Request`. |
| N07 | Malformed order UUID in status URL | `400 Bad Request`. |
| N08 | Valid UUID with no consumed event | `404 Not Found`. |
| N09 | Kafka send fails or times out | `503 Service Unavailable`; the API does not claim the order was accepted. |
| N10 | Null event or missing order ID in internal consumer/store call | Rejected with an argument/null error; no status is recorded. |

The application stores processing status only in memory. Restarting it clears previous statuses; Kafka persistence depends on the local broker's data lifecycle.
