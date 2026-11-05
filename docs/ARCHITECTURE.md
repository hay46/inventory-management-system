# IAMS Architecture

## Layers
1. Presentation (JavaFX desktop client) - FXML views, controllers, ApiClient (REST/JSON).
2. Application (Spring Boot) - REST controllers, services (business rules), repositories (JPA).
3. Data (H2 dev / PostgreSQL prod) - normalized relational schema.

## Modules
- backend: Spring Boot REST API + security (JWT) + JPA + demos.
- frontend: JavaFX desktop client.
- database: schema.sql.
- docs: architecture, UML, master prompt.

## Design Patterns
- Singleton: UserSession (frontend).
- Factory Method: StockTransaction creation by type (StockService).
- Strategy: Report/export strategies (ReportService placeholder).

## Quality Attributes
- Security: JWT auth, role-based authorization, BCrypt, input validation.
- Reliability: DB transactions for stock updates.
- Maintainability: layered architecture, SOLID.
- Testability: services injectable, JUnit + Mockito.
- Performance: background tasks in JavaFX (Task/Thread).
- Usability: JavaFX forms, validation, feedback.

## Demos (Advanced Programming)
- JDBC: backend/demo/jdbc/JdbcDemo
- TCP Sockets: backend/demo/socket/SocketServer & SocketClient
- Java RMI: backend/demo/rmi/RmiServer & RmiClient
- Servlet: /demo-servlet (DemoServlet)
- File handling & multithreading: implemented in frontend/backend services.
