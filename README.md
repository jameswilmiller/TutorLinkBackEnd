# TutorLink Backend

## Status

The backend is under active development and currently serves as the primary implementation of the TutorLink system. Core functionality, including authentication, user management, tutor profile management, and token-based session handling, has been implemented. Additional features such as bookings, payments, and production deployment are planned for future iterations.


## Overview

The TutorLink backend is a Spring Boot application that provides authentication, user management, tutor profile management, and data persistence. It exposes a REST API consumed by the frontend.


## Technology Stack

| Component                       | Version |
|--------------------------------|---------|
| Java                           | 25      |
| Spring Boot                    | 4.0.0   |
| Spring Dependency Management   | 1.1.7   |
| Gradle                         | 9.2.1 |
| PostgreSQL Driver              | Managed by Spring Boot |
| JJWT                           | 0.13.0  |
| Lombok                         | Managed by Spring Boot |

## Key Dependencies

- `org.springframework.boot:spring-boot-starter-data-jpa`
- `org.springframework.boot:spring-boot-starter-security`
- `org.springframework.boot:spring-boot-starter-webmvc`
- `org.springframework.boot:spring-boot-starter-mail`
- `org.postgresql:postgresql`
- `io.jsonwebtoken:jjwt-api:0.13.0`
- `io.jsonwebtoken:jjwt-impl:0.13.0`
- `io.jsonwebtoken:jjwt-jackson:0.13.0`
- `org.projectlombok:lombok`

Most dependency versions are managed through Spring Boot’s dependency management to ensure compatibility across the application stack.

## Project Structure

```text
src/main/java/com/tl/
├── config/         # Application and security configuration
├── controller/     # REST controllers
├── dto/            # Request and response DTOs
├── model/          # Domain entities
├── repository/     # Data access layer
├── security/       # JWT handling, filters, authentication
├── service/        # Business logic
└── Application.java

src/main/resources/
├── application.properties
└── ...
```
## Architecture
The backend follows a layered architecture:

- Controllers handle HTTP requests and responses
- Services implement business logic
- Repositories manage persistence
- Security components handle authentication and authorisation

This structure improves maintainability and isolates concerns across the application.

## Architectural Style

The system is implemented with a monolithic architecture

This approach was selected because:

- the current domain size does not justify distributed system complexity
- development speed is prioritised during early stages
- a single deployable unit simplifies debugging and iteration

The codebase maintains logical separation between domains, allowing future migration to microservices if required.

## Authentication and Session Management
The backend uses a dual-token authentication model consisting of short-lived access tokens and longer-lived refresh tokens.

### Access Tokens
- Issued after successful authentication
- Used to authorise API requests
- Sent in the Authorization header
```Authorization: Bearer <access_token>```

### Refresh Tokens
- Used to obtain new access tokens without requiring re-authentication
- Support session continuity while limiting access token lifetime
- Form part of the backend session renewal flow

### Sessions Strategy
This approach balances security and usability:
- short-lived access tokens reduce the impact of token compromise
- refresh tokens allow sessions to persist without frequent login
- request handling remains stateless

## API Overview

### Authentication
- POST /auth/signup
- POST /auth/login
- POST /auth/verify
- POST /auth/resend
- POST /auth/refresh
### Users
- GET /users/me
- POST /users/me/become-tutor
### Tutors
- GET /tutors
- GET /tutors/{id}
- GET /tutors/me/profile
- POST /tutors/me/profile
Detailed request and response formats should be documented separately.

## Data Model Summary
The application uses a relational data model implemented with JPA entities.

### User

Represents an authenticated user of the platform.

| Field                         | Type            | Description |
|------------------------------|----------------|------------|
| id                           | long           | Primary key |
| firstname                    | String         | User first name |
| lastname                     | String         | User last name |
| username                     | String         | Public display username (unique) |
| email                        | String         | Login identifier (unique) |
| password                     | String         | Hashed password |
| enabled                      | boolean        | Indicates whether the account is verified |
| verificationCode             | String         | Email verification code |
| verificationCodeExpiresAt    | LocalDateTime  | Verification expiry timestamp |
| roles                        | Set<Role>      | Assigned roles (e.g. STUDENT, TUTOR, ADMIN) |

#### Notes

- The system authenticates users using their **email address**, not their username  
- Roles are stored as an element collection and eagerly loaded  
- The `enabled` flag is used to enforce email verification before access  

### Tutor

Represents tutor-specific profile information associated with a user.

| Field            | Type     | Description |
|------------------|----------|------------|
| id               | Long     | Primary key |
| user             | User     | Associated user (one-to-one) |
| bio              | String   | Tutor description |
| subjects         | String   | Subjects offered |
| location         | String   | Human-readable location |
| remote           | boolean  | Indicates remote availability |
| hourlyRate       | Integer  | Hourly rate |
| profileImageKey  | String   | Reference to stored profile image |
| latitude         | Double   | Geographic latitude |
| longitude        | Double   | Geographic longitude |

#### Notes

- Each tutor is linked to exactly one user via a **one-to-one relationship**  
- The `user_id` column is unique, ensuring a user can only have one tutor profile  
- Location is stored both as a string (display) and coordinates (map integration)  

### RefreshToken

Represents a persisted refresh token used for session renewal.

| Field      | Type          | Description |
|------------|---------------|-------------|
| id         | Long          | Primary key |
| token      | String        | Refresh token value |
| user       | User          | Associated user |
| revoked    | boolean       | Indicates whether the token has been invalidated |
| expiresAt  | LocalDateTime | Refresh token expiry timestamp |

#### Notes

- Refresh tokens are persisted to support session renewal and token revocation  
- Multiple refresh tokens may exist for a single user, allowing multiple active sessions across devices or browsers  
- Access tokens are not persisted and remain stateless
  
### Relationships

- User -> Tutor: One-to-one  
- User -> roles: One-to-many (via element collection)  
- User -> RefreshToken = one-to-many

## Configuration
Application configuration is managed via application.properties

Configuration areas include:
- database connectivity
- JWT configuration
- email service configuration
- CORS settings
Sensitive values should be externalised i.e in an env file and not committed to source control

## Local Development
### pre-requisites
ensure the following are installed

- Java 25
- Gradle
- PostgreSQL

Clone the repository:

```bash
git clone https://github.com/jameswilmiller/TutorLinkBackEnd.git
cd TutorLinkBackEnd
```
### Database Configuration
A PostgreSQL database must be available and reachable by the application before startup. Local and cloud-hosted deployments are both supported.

update the following properties in  ```application.properties```:

```
spring.datasource.url=jdbc:postgresql://<host>:<port>/<database_name> 
spring.datasource.username=your_username
spring.datasource.password=your_password
spring.jpa.hibernate.ddl-auto=update
```

### JWT Configuration
JWT properties are required for authentication and token generation
```
security.jwt.secret-key=your_secret_key
security.jwt.expiration-time=3600000
security.jwt.refresh-token-expiration=604800000
```
#### generating a secret key
generate a secure random key:
Linux / macOS:
```bash
openssl rand -base64 32
```
Windows
```bash
$bytes = New-Object byte[] 32
[System.Security.Cryptography.RandomNumberGenerator]::Create().GetBytes($bytes)
[Convert]::ToBase64String($bytes)
```
this key must remain private and should not be committed to source control

### Email Service (SMTP)
The backend uses SMTP to send account verification emails

GMail SMTP can be used via an App password
```
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=your_email@gmail.com
spring.mail.password=your_app_password
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
```
#### GMail setup
To use GMail as the SMTP provider
- enable 2-step verification on the google account
- generate an App password
- use the app password as spring.mail.password
A standard Gmail account password will not work

### CORS Configuration
The backend must allow requests from the frontend origin.

CORS configuration is defined within the security configuration and controlled via application properties.

Example:
```properties
app.cors.allowed-origins=https://frontendorigin.com or http://localhost:5173 for local development
```

### Running the Application
start the backend using:
```
./gradlew bootRun
```
the API will be available at:

```http://localhost:8080```

To test full functionality of TutorLink navigate to https://github.com/jameswilmiller/TutorLinkFrontEnd/blob/main/README.md and follow setup instructions 

## Author 
James Miller

For questions or collaboration, contact: jameswil.miller@gmail.com
