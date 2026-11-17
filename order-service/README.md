# Order service

Independent Java 21 / Spring Boot application for guest checkout. Owns only orders and order items in its own PostgreSQL database.

## Request flow

1. POST /restaurants/{restaurantId}/orders accepts customer contact details and item IDs/quantities, never prices.
2. RestaurantClient calls restaurant-service GET /restaurants/{restaurantId}/menu using Spring RestClient with connection/read timeouts.
3. That public menu includes only available items in active categories of an active restaurant. A missing requested item rejects the whole order.
4. Order service snapshots each item's ID, name and price, checks a common currency, computes the total and persists its own entities.
5. Responds with 201 and OrderResponse, excluding customer phone and delivery address.

The existing public menu endpoint is reused for this small catalogue. No shared entities, direct access to the restaurant database, cross-service joins or cross-service foreign keys are used.

## Configuration

The default HTTP port is 8081; restaurant-service uses 8080. Set configuration through environment variables:

| Variable | Default / purpose |
| --- | --- |
| ORDER_SERVICE_PORT | 8081 |
| ORDER_DB_HOST | localhost |
| ORDER_DB_PORT | 5432 |
| ORDER_DB_NAME | order_db, must be separate from restaurant_db |
| ORDER_DB_USER | Required database user |
| ORDER_DB_PASSWORD | Required database password |
| RESTAURANT_SERVICE_BASE_URL | http://localhost:8080 |
| RESTAURANT_SERVICE_CONNECT_TIMEOUT | 2s |
| RESTAURANT_SERVICE_READ_TIMEOUT | 5s |

Flyway V1 creates only orders/order_items. The order_items.order_id foreign key is local to this database. restaurant_id/menu_item_id are external UUID references.

Run OrderServiceApplication after configuring a separately provisioned database and starting restaurant-service. Database provisioning and live end-to-end execution were not performed as part of this refactor.

## Verification without a database

From this folder on Windows, run .\gradlew.bat compileJava and .\gradlew.bat test.

This module contains only isolated tests: Mockito service tests, WebMvc controller tests and REST client tests with in-memory HTTP responses. No PostgreSQL or Docker is needed for them.

## Error handling

All application exceptions inherit ApplicationException and provide a message and ErrorCode. GlobalExceptionHandler returns the same ApiError format as restaurant-service.

- 400: VALIDATION_FAILED for invalid requests; ORDER_INVALID for order rules.
- 404: RESTAURANT_NOT_FOUND or MENU_ITEM_NOT_FOUND.
- 503: RESTAURANT_SERVICE_UNAVAILABLE for remote errors, network failures or invalid menu responses.

Prices and availability reflect the menu read at checkout, with snapshots retained afterwards. No inventory reservation, authentication, payments, events or status-management endpoints are implemented in this slice.