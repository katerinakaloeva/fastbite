# Restaurant service

## Responsibility

Owns restaurants, menu categories and menu items in restaurant_db. It provides catalogue and management APIs. Order-service reads its public menu through REST; it does not access the restaurant database.

## Current status

Restaurant/category/item creation and updates, public restaurant search/details and filtered public menus are implemented. Available menu products come from active categories of an active restaurant.

Request validation, DTO mapping, ApplicationException/ErrorCode/ApiError handling and OpenAPI configuration are implemented. Flyway V1-V5 creates and evolves the catalogue schema. Authentication and owner authorization are planned.

## Endpoints

Current controllers use unversioned paths:

| Method | Path | Purpose |
| --- | --- | --- |
| GET | /restaurants | Search active restaurants with filters and pagination |
| GET | /restaurants/{restaurantId} | Retrieve restaurant details |
| GET | /public/restaurants/{restaurantId} | Retrieve public details for an active restaurant |
| POST | /restaurants | Create a restaurant |
| PATCH | /restaurants/{restaurantId} | Update a restaurant |
| GET | /restaurants/{restaurantId}/menu | Get active categories and available items for an active restaurant |
| POST | /restaurants/{restaurantId}/menu-categories | Create a category |
| GET | /restaurants/{restaurantId}/menu-categories | List categories in display order |
| PATCH | /restaurants/{restaurantId}/menu-categories/{categoryId} | Update a category |
| POST | /menu-categories/{categoryId}/menu-items | Create a menu item |
| GET | /menu-categories/{categoryId}/menu-items | List available items in a category |
| PATCH | /menu-categories/{categoryId}/menu-items/{itemId} | Update a menu item |

When the application is running, Swagger UI is available at /swagger-ui/index.html and the OpenAPI document at /v3/api-docs. Management endpoints currently have no authentication and are intended for local development.

## Configuration

The default HTTP port is 8080. Pass database settings through the process environment or IDE run configuration:

| Variable | Default / purpose |
| --- | --- |
| DB_HOST | localhost |
| POSTGRES_PORT | Required PostgreSQL port |
| POSTGRES_DB | Required database name; use the restaurant-owned database, normally restaurant_db |
| POSTGRES_USER | Required database user |
| POSTGRES_PASSWORD | Required database password |

Hibernate uses ddl-auto=validate; Flyway owns schema creation and changes. This service has no HTTP client for calling another service.

Follow the [root database setup instructions](../README.md#1-start-the-restaurant-database) to start restaurant-db. A private .env is ignored by Git and must be loaded into the application process environment explicitly; Spring Boot does not load it automatically.

Run RestaurantServiceApplication after providing the database and environment settings.

## Build and tests

From this folder, with restaurant-db running and its environment variables set:

    ./gradlew test          # Windows: .\gradlew.bat test

The suite contains entity tests for domain rules, Mockito service tests, WebMvc controller tests, mapper tests and repository tests that run against PostgreSQL.

## Package structure

Under com.fastbite.restaurantservice, Spring configuration is in config and the application entry point is at the package root. Business code is grouped inside restaurant/controller, dto, exception, mapper, model, repository and service. Tests mirror these packages. Flyway files are in src/main/resources/db/migration.

## Next scope

Future authentication/authorization will be introduced after identity-service. Opening hours and other catalogue extensions remain roadmap work.

See the [project README](../README.md) and [architecture](../ARCHITECTURE.md) for the system context.
