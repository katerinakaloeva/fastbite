# Order service

## Responsibility

Owns orders and order items in order_db. Implements guest checkout, including item name/price snapshots and total calculation.

## Current status

The order controller, request/response DTOs, mapper, service, entities, repository and Flyway V1 migration are implemented.

During checkout:

1. The customer supplies contact details and product IDs/quantities.
2. RestaurantClient calls restaurant-service GET /restaurants/{restaurantId}/menu using Spring RestClient with connection/read timeouts.
3. The public menu contains available items from active categories of an active restaurant. Missing requested products reject the whole order.
4. Order-service checks a common currency, snapshots product IDs/names/prices and calculates line totals and the order total.
5. It stores the order and returns OrderResponse, excluding customer phone and delivery address.

There are no shared entities, direct restaurant-database queries or cross-service foreign keys. Order.restaurantId and OrderItem.menuItemId are external UUID references. Only the OrderItem-to-Order foreign key is local to order_db.

## Endpoints

| Method | Path | Purpose |
| --- | --- | --- |
| POST | /restaurants/{restaurantId}/orders | Create a guest order and return 201 with OrderResponse |

Prices come from the restaurant API, not from the customer. Errors use ApplicationException/ErrorCode/ApiError:

- 400: VALIDATION_FAILED for request validation; ORDER_INVALID for order rules.
- 404: RESTAURANT_NOT_FOUND or MENU_ITEM_NOT_FOUND.
- 503: RESTAURANT_SERVICE_UNAVAILABLE for remote errors, network failures or invalid menu responses.

When the application is running, Swagger UI is available at /swagger-ui/index.html and the OpenAPI document at /v3/api-docs.

## Configuration

The default HTTP port is 8081; restaurant-service uses 8080. Set values through environment variables:

| Variable | Default / purpose |
| --- | --- |
| ORDER_SERVICE_PORT | 8081 |
| ORDER_DB_HOST | localhost |
| ORDER_DB_PORT | 5432 |
| ORDER_DB_NAME | order_db; must be separate from restaurant_db |
| ORDER_DB_USER | Required database user |
| ORDER_DB_PASSWORD | Required database password |
| RESTAURANT_SERVICE_BASE_URL | http://localhost:8080 |
| RESTAURANT_SERVICE_CONNECT_TIMEOUT | 2s |
| RESTAURANT_SERVICE_READ_TIMEOUT | 5s |

Flyway V1 creates orders/order_items, and Hibernate validates the schema. Run OrderServiceApplication after provisioning its database, providing the environment variables and starting restaurant-service. Spring Boot does not automatically load a private .env file.

## Build and tests

From this folder:

    ./gradlew test          # Windows: .\gradlew.bat test

Service tests use Mockito, controller tests use WebMvc, and REST client tests use in-memory HTTP responses.

For end-to-end checkout, configure the two service databases and run restaurant-service alongside order-service.

## Package structure

Under com.fastbite.orderservice, Spring configuration is in config and the application entry point is at the package root. Business code is grouped inside order/controller, dto, exception, mapper, model, repository and service. REST calls live in order/client; the consumed menu response contract lives in order/dto. Tests mirror these packages. Flyway files are in src/main/resources/db/migration.

## Next scope

Prices/availability reflect the menu read at checkout. No inventory reservation, authentication, payment, event publishing or status-management endpoints are implemented yet.

See the [project README](../README.md) and [architecture](../ARCHITECTURE.md) for the system context.
