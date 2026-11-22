# Identity service

## Responsibility

Owns the user and role persistence model and authentication configuration in identity_db, separately from the restaurant and order databases.

## Current status

Customer registration and login are implemented:

- POST /auth/register creates a customer account. The email is trimmed and lower-cased, so the same address cannot be registered twice with different casing.
- The CUSTOMER role is always assigned by the backend; clients cannot choose their role.
- Passwords are hashed before they are stored, and responses never contain passwords or password hashes.
- POST /auth/login checks the email and password and returns a signed JWT access token (HS256) that expires after 15 minutes by default. The token carries the user id, email and roles.
- Spring Security runs without sessions (stateless). Register, login and Swagger are public; every other endpoint requires authentication.

The service is built on:

- An independent Java 21 / Spring Boot Gradle project and IdentityServiceApplication.
- User and Role entities, with multiple roles per user through user_roles.
- Flyway V1 for users, roles and user_roles, including unique email/role names, foreign keys and initial role records.
- UserRepository and RoleRepository for database access.
- RegisterRequest validation and UserResponse, which excludes passwords and password hashes.
- UserMapper for entity/response conversion.
- A PBKDF2-HMAC-SHA256 PasswordEncoder with a random 16-byte salt and 600,000 iterations.
- ApplicationException, ErrorCode, ApiError and GlobalExceptionHandler for consistent application errors.

Validating these tokens in restaurant-service and order-service is the next step.

## Endpoints

| Method | Path | Purpose | Status |
| --- | --- | --- | --- |
| POST | /auth/register | Register a customer account and return 201 with UserResponse | Implemented |
| POST | /auth/login | Authenticate and return 200 with a JWT access token | Implemented |

Errors use the common ApiError format:

- 400: VALIDATION_FAILED for missing or invalid fields, or malformed JSON.
- 401: INVALID_CREDENTIALS when the email or password is wrong. The same message is returned in both cases, so the API does not reveal which emails are registered.
- 409: EMAIL_ALREADY_EXISTS when the email is already registered.
- 500: DEFAULT_ROLE_NOT_FOUND when the CUSTOMER role is missing from the database.

When the application is running, Swagger UI is available at /swagger-ui/index.html.

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
| JWT_SECRET | Required; at least 32 characters. Signs the access tokens. Never commit it. |
| JWT_EXPIRATION | 15m; access token lifetime |

Hibernate uses ddl-auto=validate and open-in-view=false. Flyway V1 defines users, roles and user_roles and inserts CUSTOMER, RESTAURANT_OWNER, DELIVERY_DRIVER and ADMIN.

Provide database settings and JWT_SECRET through the process environment or IDE run configuration. Spring Boot does not automatically load a private .env file. The application refuses to start without a valid JWT_SECRET.

## Build and tests

From this folder:

    ./gradlew test          # Windows: .\gradlew.bat test

Mockito service tests cover registration (email normalization, password hashing, default role, duplicate emails) and login (valid credentials, wrong password, unknown email). JwtService tests check the token claims and expiry and that a token signed with another key is rejected. WebMvc controller tests cover the 200, 201, 400, 401 and 409 responses with the security configuration applied. MockMvc tests for GlobalExceptionHandler cover the error format.

Flyway owns the schema definition and Hibernate validates entity mappings when the application starts.

## Package structure

The application entry point is in com.fastbite.identityservice. Password encoder, security and JWT configuration live in config. The identity feature groups controller, dto, exception, mapper, model, repository and service packages, following the same convention as restaurant-service and order-service.

## Next scope

Validate the JWT in restaurant-service and order-service, then add role- and ownership-based authorization.

See the [project README](../README.md) and [architecture](../ARCHITECTURE.md) for the system context.
