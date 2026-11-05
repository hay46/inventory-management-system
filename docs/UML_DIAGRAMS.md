# UML Diagrams - Inventory and Asset Management System (IAMS)

This document collects the UML diagrams for the project (PlantUML source).

## 1. Use Case Diagram
```plantuml
@startuml
left to right direction
actor Administrator as Admin
actor "Inventory Manager" as Manager
actor Storekeeper
actor Viewer
rectangle "IAMS" {
  usecase "Login/Logout" as UC1
  usecase "Manage Users" as UC2
  usecase "Register Item" as UC3
  usecase "Receive Stock" as UC4
  usecase "Issue Stock" as UC5
  usecase "Adjust Stock" as UC6
  usecase "Search Inventory" as UC7
  usecase "Low-Stock Alerts" as UC8
  usecase "Transaction History" as UC9
  usecase "Generate Reports" as UC10
  usecase "Import/Export" as UC11
}
Admin --> UC1
Admin --> UC2
Admin --> UC3
Admin --> UC7
Admin --> UC9
Admin --> UC10
Admin --> UC11
Manager --> UC1
Manager --> UC3
Manager --> UC4
Manager --> UC5
Manager --> UC6
Manager --> UC7
Manager --> UC8
Manager --> UC9
Manager --> UC10
Manager --> UC11
Storekeeper --> UC1
Storekeeper --> UC4
Storekeeper --> UC5
Storekeeper --> UC7
Storekeeper --> UC9
Viewer --> UC1
Viewer --> UC7
Viewer --> UC8
Viewer --> UC9
Viewer --> UC10
@enduml
```

## 2. Class Diagram
```plantuml
@startuml
class User { -id:Long -username:String -passwordHash:String -role:Role }
class Role { -id:Long -name:String }
class Item { -id:Long -itemCode:String -itemName:String -quantity:int -minimumStock:int }
class Category { -id:Long -name:String }
class Location { -id:Long -name:String }
class StockTransaction { -id:Long -type:TransactionType -quantity:int }
enum TransactionType { RECEIVE ISSUE ADJUSTMENT }
Role "1" -- "0..*" User
Category "1" -- "0..*" Item
Location "1" -- "0..*" Item
Item "1" -- "0..*" StockTransaction
User "1" -- "0..*" StockTransaction
@enduml
```

## 3. Sequence Diagram - Login
```plantuml
@startuml
actor User
participant "JavaFX" as FX
participant "Spring Boot" as API
participant "AuthService" as S
participant "UserRepository" as R
participant "Database" as DB
User -> FX: enter credentials
FX -> API: POST /api/auth/login
API -> S: login(req)
S -> R: findByUsername
R -> DB: SELECT user
DB --> R: user record
R --> S: User
S -> S: verify password hash
S --> API: LoginResponse(token, role)
API --> FX: 200 OK + token
FX --> User: open dashboard
@enduml
```

## 4. Sequence Diagram - Receive Stock
```plantuml
@startuml
actor Storekeeper
participant "JavaFX" as FX
participant "Spring Boot" as API
participant "StockService" as S
participant "ItemRepository" as IR
participant "TxRepository" as TR
participant "Database" as DB
Storekeeper -> FX: enter item + qty
FX -> API: POST /api/stock/receive
API -> S: receive(req, user)
S -> IR: findById(item)
IR -> DB: SELECT item
DB --> IR: item
IR --> S: item
S -> S: validate qty>0
S -> IR: save(item with increased qty)
S -> TR: save(RECEIVE tx)
TR -> DB: INSERT transaction
S --> API: StockTransaction
API --> FX: 200 OK
FX --> Storekeeper: success message
@enduml
```

## 5. Activity Diagram - Issue Stock
```plantuml
@startuml
start
:User opens Issue Stock screen;
:Enter item code and quantity;
if (quantity <= 0?) then (yes)
  :Show "Quantity must be > 0";
  stop
else (no)
endif
:Backend checks available stock;
if (available >= requested?) then (yes)
  :Decrease stock quantity;
  :Record ISSUE transaction;
  :Commit DB transaction;
  :Show "Issued successfully";
else (no)
  :Show "Insufficient stock";
endif
stop
@enduml
```

## 6. Database ER Diagram (summary)
- roles 1..* users
- categories 1..* items
- locations 1..* items
- suppliers 0..1 stock_transactions (source for receives)
- items 1..* stock_transactions
- users 1..* stock_transactions (performed_by)
- users 0..* audit_logs

## 7. Component Diagram
```plantuml
@startuml
component "JavaFX Client" as FX
component "Spring Boot API" as API
component "PostgreSQL/H2" as DB
FX --> API : REST/JSON
API --> DB : JPA/JDBC
component "JDBC Demo" as J
component "Socket Demo" as S
component "RMI Demo" as R
component "Servlet Demo" as SV
J ..> DB
@enduml
```
