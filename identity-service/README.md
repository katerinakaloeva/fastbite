# Identity service

## Responsibility

Owns the user and role persistence model and authentication configuration in identity_db, separately from the restaurant and order databases.

## Current status

The registration foundation includes:

- An independent Java 21 / Spring Boot Gradle project and IdentityServiceApplication.
- User and Role entities, with multiple roles per user through user_roles.
- Flyway V1 for users, roles and user_roles, including unique email/role names, foreign keys and initial role records.
- UserRepository and RoleRepository for database access.
- RegisterRequest validation and UserResponse, which excludes passwords and password hashes.
- UserMapper for entity/response conversion.
- A PBKDF2-HMAC-SHA256 PasswordEncoder with a random 16-byte salt and 600,000 iterations.
- ApplicationException, ErrorCode, ApiError and GlobalExceptionHandler for consistent application errors.

Registration service/controller and login/JWT are the next implementation steps.

## Endpoints

Planned endpoints, added with the registration and login features:

| Method | Path | Purpose |
| --- | --- | --- |
| POST | /auth/register | Register a customer account |
| POST | /auth/login | Authenticate and receive a JWT access token |

## Configuration

Set values through the process environment or IDE run configuration:

| Variable | Default / purpose |
| --- | --- |
| IDENTITY_SERVICE_PORT | 8082 |
| IDENTITY_DB_HOST | localhost |
| IDENTITY_DB_PORT | 5432 |
| IDENTITY_DB_NAME | identity_db; must be separate from restaurant_db and order_db |
| IDENTITY_DB_USER | Required database user |
| IDENTITY_DB_PASSWORD | Required database password |

Hibernate uses ddl-auto=validate and open-in-view=false. Flyway V1 defines users, roles and user_roles and inserts CUSTOMER, RESTAURANT_OWNER, DELIVERY_DRIVER and ADMIN.

Provide database settings through the process environment or IDE run configuration. Spring Boot does not automatically load a private .env file.

## Build and tests

From this folder:

    ./gradlew test          # Windows: .\gradlew.bat test

The tests use MockMvc to check the error responses: 409 for an existing email, 500 for a missing default role, field-level validation errors and malformed JSON.

Flyway owns the schema definition and Hibernate validates entity mappings when the application starts.

## Package structure

The application entry point is in com.fastbite.identityservice. Password encoder configuration lives in config. The identity feature groups model, repository, dto, mapper and exception packages, following the same convention as restaurant-service and order-service.

Controller and service packages are introduced with their corresponding features.

## Next scope

Complete the registration service and controller. Assign CUSTOMER through the backend, hash passwords before persistence and return only the public UserResponse fields. Follow with login and JWT.

See the [project README](../README.md) and [architecture](../ARCHITECTURE.md) for the system context.
