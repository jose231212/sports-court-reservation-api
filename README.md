# Sports Court Reservation API

[English](README.md) \| [Español](README.es.md)

A REST API for managing sports court reservations, users, and payments.
I built this project to practice developing a backend with Spring Boot,
including authentication, role-based access, validation, persistence,
and automated tests.

The API supports four court types: football, basketball, tennis, and
padel. It also includes reservation rules to prevent overlapping
bookings and access controls so users can work with their own
reservations and payments while administrators can manage the wider
system.

## Features

-   User registration and login with JWT authentication.
-   Role-based access for `USER` and `ADMIN` accounts.
-   CRUD operations for users, courts, reservations, and payments,
    subject to each resource's permissions.
-   Pagination and sorting for collection endpoints.
-   Reservation validation for dates, times, court availability, and
    overlapping bookings.
-   Automatic calculation of a payment amount from the reservation
    duration and the court's hourly rate.
-   Centralized error responses.
-   API documentation through Swagger UI.
-   Automated tests with JUnit and Mockito.

## Tech stack

-   Java
-   Spring Boot
-   Spring Web MVC
-   Spring Data JPA and Hibernate
-   Spring Security
-   JSON Web Tokens (JJWT)
-   MySQL
-   Maven
-   Springdoc OpenAPI / Swagger UI
-   JUnit and Mockito

## Requirements

Before running the application, make sure you have:

-   JDK 17 or newer.
-   MySQL running locally or a reachable MySQL server.
-   Git, if you want to clone the repository.

The project declares Java 17 in its Maven configuration. I developed and
tested it locally with Java 21.

## Configuration

Create a MySQL database named `apidereservas`:

``` sql
CREATE DATABASE apidereservas;
```

The application reads the database password and JWT signing secret from
environment variables. Set them before starting the app; do not commit
real credentials or secrets to the repository.

In PowerShell:

``` powershell
$env:DB_PASSWORD="your_mysql_password"
$env:JWT_SECRET="your_base64_encoded_secret"
```

`JWT_SECRET` must be a strong Base64-encoded secret suitable for signing
JWTs. The token expiration is configured as 86,400,000 milliseconds (24
hours).

The current local database configuration uses:

-   Database: `apidereservas`
-   URL: `jdbc:mysql://localhost:3306/apidereservas`
-   Username: `root`
-   Schema management: Hibernate `ddl-auto=update`

Review `src/main/resources/application.properties` if your local MySQL
setup uses a different host, port, database, or username.

## Run the application

From the project root, use the Maven Wrapper:

``` powershell
.\mvnw spring-boot:run
```

You can also run the main application class from your IDE.

Once the application has started, the default local base URL is
`http://localhost:8080`.

## API documentation

Swagger UI:

`http://localhost:8080/swagger-ui/index.html`

OpenAPI document:

`http://localhost:8080/v3/api-docs`

Use the login endpoint to obtain a JWT, then select **Authorize** in
Swagger UI and provide the token as a Bearer token to try protected
endpoints.

## Main routes

  -----------------------------------------------------------------------
  Resource                Base path               Purpose
  ----------------------- ----------------------- -----------------------
  Authentication          `/api/auth`             Login and obtain a JWT

  Users                   `/api/users`            Register and manage
                                                  user accounts

  Courts                  `/api/courts`           Browse and manage
                                                  sports courts

  Reservations            `/api/reservations`     Create and manage court
                                                  reservations

  Payments                `/api/payments`         Create and manage
                                                  reservation payments
  -----------------------------------------------------------------------

The exact operations available on each route depend on the endpoint and
the authenticated user's role.

## Reservation and payment rules

-   A reservation must use an existing, active court.
-   The end time must be later than the start time.
-   Reservations cannot be made for a past date or for a time that has
    already passed today.
-   Two active reservations cannot overlap on the same court and date.
    Cancelled reservations do not block a time slot.
-   A regular user can access their own reservations and related
    payments. Administrators can access records across users.
-   Each reservation can have at most one payment.
-   A payment cannot be created for a cancelled reservation.
-   The payment amount is calculated from the reservation duration and
    the court's hourly price. Updating a payment changes its payment
    method; it does not recalculate the amount or change its status.

## Error handling and security

The API uses centralized exception handling and returns structured error
responses. Depending on the situation, responses can include
`400 Bad Request`, `401 Unauthorized`, `403 Forbidden`, `404 Not Found`,
and `500 Internal Server Error`.

Passwords are stored using BCrypt, and protected requests use JWT-based
authentication. Authorization rules distinguish between regular users
and administrators and, for reservations and payments, check resource
ownership.

## Tests

The test suite was run successfully during project validation:

``` text
Tests run: 61, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
```

To run the tests yourself:

``` powershell
.\mvnw clean test
```

## Project status

The core API, security flow, Swagger documentation, and automated tests
have been implemented and validated. This project is part of my ongoing
practice building Java backend applications with realistic business
rules and access control.
