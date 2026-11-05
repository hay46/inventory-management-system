# Inventory and Asset Management System (IAMS)

Injibara University - College of Engineering and Technology - Department of Software Engineering
Advanced Programming and Software Architecture and Design - Integrated Semester Project

## Overview
A complete desktop + backend inventory and asset management system built with JavaFX (frontend) and Spring Boot (backend), using a relational database.

## Modules
- `backend/` - Spring Boot REST API, JPA persistence, Spring Security + JWT, demos (JDBC, sockets, RMI, servlet).
- `frontend/` - JavaFX desktop client consuming the REST API.
- `database/` - SQL schema and setup scripts.
- `docs/` - Architecture and UML diagrams.

## Prerequisites
- JDK 17 or later
- Maven 3.8+
- PostgreSQL (or use H2 in-memory for dev; default profile uses H2)
- IntelliJ IDEA (recommended)

## Run Backend
```
mvn -pl backend spring-boot:run
```
Backend starts on http://localhost:8080. Default admin: `admin` / `admin123`.
H2 console: http://localhost:8080/h2-console

## Run Frontend
```
mvn -pl frontend javafx:run
```

## Run Demos
- JDBC: `com.injibara.iams.backend.demo.jdbc.JdbcDemo`
- Socket server: `...demo.socket.SocketServer` then `SocketClient`
- RMI server: `...demo.rmi.RmiServer` then `RmiClient`
- Servlet: http://localhost:8080/demo-servlet

## Tests
```
mvn -pl backend test
```
(Note: Mockito-based tests require JDK 17-23; on newer JDKs they may be skipped.)

## API Documentation
Swagger UI: http://localhost:8080/swagger-ui.html

## Repository
https://github.com/hay46/inventory-management-system.git
