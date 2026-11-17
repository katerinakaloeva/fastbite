# FastBite

FastBite is a backend-focused portfolio and learning project for a simplified food ordering and delivery platform, inspired by services such as efood and Wolt.

The project is being developed incrementally to practise Java backend engineering: domain modelling, database migrations, REST APIs, security, testing, asynchronous events and clean service boundaries.

> **Project status:** in progress. The repository contains independent restaurant-service and order-service applications. This repository does not contain a frontend yet.

## Why this project

The aim is not to create many services quickly. The aim is to build a small system where each service has a clear business responsibility, owns its own data and can be explained in an interview.

The project uses a hybrid communication model:

- **REST** for requests that need an immediate answer.
- **Apache Kafka** for business events that other services can process asynchronously.

See [ARCHITECTURE.md](ARCHITECTURE.md) for the complete plan, service boundaries and development roadmap.

## Current implementation

### Restaurant service foundation

- Java 21, Spring Boot and Gradle project setup.
- PostgreSQL 17 through Docker Compose.
- Environment-variable based database configuration; `.env` is ignored by Git.
- Flyway migration for the initial `restaurants` table.
- JPA `Restaurant` entity and Spring Data repository.
- JUnit integration-test foundation for repository persistence.

Restaurant and menu REST endpoints, public menu filtering and Swagger documentation are implemented.

### Order service

Guest checkout is implemented in the independent [order-service](order-service/README.md), with its own Gradle build and database configuration. It calls the restaurant public menu API through REST to validate products and read prices, then stores order snapshots in its own database.

Isolated Mockito, WebMvc and HTTP-client tests can run without Docker or PostgreSQL. Live database migration and end-to-end execution are not yet verified.

## Services and roadmap

| Service | Responsibility | Database |
| --- | --- | --- |
| `restaurant-service` | Restaurants, menus and item availability | `restaurant_db` |
| `identity-service` | Users, roles, login and JWT tokens | `identity_db` |
| `order-service` | Orders, order items, totals and state transitions | `order_db` |
| `delivery-service` | Driver assignments and delivery status | `delivery_db` |
| `payment-service` | Mock payments and refunds | `payment_db` |
| `notification-service` | Notification records and event-driven notifications | `notification_db` when persistence is needed |

Each service owns its data. Services must not query another service's database directly.

## Technology stack

- Java 21
- Spring Boot and Gradle
- PostgreSQL
- Spring Data JPA / Hibernate
- Flyway
- Docker Compose
- REST/JSON and Spring `RestClient`
- Apache Kafka and Spring Kafka (from Phase 3)
- Spring Security and JWT (from Phase 2)
- OpenAPI / Swagger (during the restaurant API phase)
- JUnit 5, Mockito and Testcontainers
- GitHub Actions (later quality and CI phase)

## Local setup

### Prerequisites

- Java 21
- Docker Desktop, only when you want to run the local PostgreSQL database

### Start PostgreSQL

1. Create your private local environment file from the example:

   ```powershell
   Copy-Item .env.example .env
   ```

2. Choose a local value for `POSTGRES_PASSWORD` in `.env`. Never commit this file.

3. Start only the restaurant database:

   ```powershell
   docker compose up -d restaurant-db
   ```

4. Stop it when you are finished to save memory:

   ```powershell
   docker compose stop restaurant-db
   ```

### Run the Spring Boot service locally

The service reads database settings from environment variables. Set the values from your local `.env` file in your IDE run configuration, then run `RestaurantServiceApplication`.

The same variables are needed for database-backed integration tests:

```text
POSTGRES_DB
POSTGRES_USER
POSTGRES_PASSWORD
POSTGRES_PORT
```

## Development principles

- Build one small vertical slice at a time.
- Use Flyway migrations; do not use Hibernate schema auto-update.
- Keep credentials and signing keys out of Git.
- Do not expose JPA entities directly from public APIs; use DTOs.
- Validate requests and return consistent API errors.
- Enforce both role-based permissions and resource ownership.
- Use small feature branches and Pull Requests.
- Add infrastructure only when a real use case exists.

## Current limitations

- Restaurant/menu and guest-order REST endpoints are implemented; live cross-service/database execution remains to be verified.
- No authentication or authorization yet.
- Kafka, gateway, delivery, payment and notifications are planned phases.
- Payments will be simulated; no real card data will be stored.

## License

This project is for learning and portfolio purposes.
