# Restaurant Management System (RMS) — Enterprise Java Application

![Java](https://img.shields.io/badge/Java-17%2B-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![JavaFX](https://img.shields.io/badge/JavaFX-19-blue?style=for-the-badge)
![MySQL](https://img.shields.io/badge/MySQL-8.0-4479A1?style=for-the-badge&logo=mysql&logoColor=white)
![Maven](https://img.shields.io/badge/Maven-3.8%2B-C71A36?style=for-the-badge&logo=apachemaven&logoColor=white)
![JUnit5](https://img.shields.io/badge/JUnit5-Testing-25A162?style=for-the-badge)

A complete, production-grade Restaurant Management System built entirely in Java using **JavaFX**, **JDBC**, and **MySQL**. Designed for academic rigor and real-world restaurant operation, demonstrating advanced OOP principles, robust Exception Handling, Data Structures, Layered MVC Architecture, and Automated Unit Testing.

---

## 🌟 Key Features

- 🔐 **Role-Based Authentication**: Secure salted SHA-256 password hashing supporting `ADMIN`, `MANAGER`, `WAITER`, `CASHIER`, and `KITCHEN_STAFF`.
- 📊 **Executive Dashboard**: Real-time metrics on daily gross revenue, active orders, floor table occupancy, kitchen queue backlog, and low inventory warnings.
- 🍽️ **Visual Table Management**: Real-time table status tracking (`AVAILABLE`, `OCCUPIED`, `RESERVED`, `CLEANING`) and capacity allocation.
- 📖 **Menu Engineering**: Categorized item catalog with price constraints, prep duration, fast filtering, and instant search.
- 📝 **Full Order Lifecycle**: Intuitive order placement with dynamic line item addition, customizations, subtotal, automatic tax/service charge breakdown.
- 👨‍🍳 **Kitchen Display System (KDS)**: Priority queue ordering workflow (`CONFIRMED` ➔ `PREPARING` ➔ `READY` ➔ `SERVED`).
- 💳 **Polymorphic Billing & Payments**: Multi-channel payment engine supporting `CASH`, `UPI`, and `CREDIT/DEBIT CARDS` with automatic invoice & receipt generation.
- 📦 **Inventory & Supplier Management**: Real-time ingredient tracking, minimum stock thresholds, auto-deduction on orders, and supplier directory.
- 📅 **Reservation Management**: Double-booking prevention algorithm and guest capacity verification.
- 📈 **Interactive Analytics & Reporting**: JavaFX Bar, Pie, and Line charts illustrating sales performance, top-selling dishes, and financial metrics.

---

## 🏗️ Architecture & Technology Stack

- **Language**: Java 17+
- **User Interface**: JavaFX 19 (FXML + CSS Design System)
- **Database**: MySQL 8.0+
- **Persistence / Data Access**: JDBC with `PreparedStatement` & Connection Management
- **Build Tool**: Apache Maven
- **Architecture**: Layered MVC (Model-View-Controller) / DAO Pattern
- **Testing Framework**: JUnit 5

---

## 📁 Project Directory Structure

```text
restaurant-management-system/
├── pom.xml
├── README.md
├── .gitignore
├── LICENSE
│
├── database/
│   ├── schema.sql
│   ├── sample_data.sql
│   └── README.md
│
├── docs/
│   ├── system-architecture.md
│   ├── database-design.md
│   ├── class-diagram.md
│   ├── user-guide.md
│   └── viva-questions.md
│
├── src/
│   ├── main/
│   │   ├── java/com/restaurant/
│   │   │   ├── Main.java
│   │   │   ├── config/ (DatabaseConfig, AppConfig)
│   │   │   ├── model/ (User, Customer, Employee, MenuItem, Order, Payment, etc.)
│   │   │   ├── enums/ (UserRole, TableStatus, OrderStatus, PaymentMethod, etc.)
│   │   │   ├── repository/ (DAOs for all entities)
│   │   │   ├── service/ (Business logic, calculations, workflow)
│   │   │   ├── controller/ (JavaFX UI Controllers)
│   │   │   ├── exception/ (Custom domain exceptions)
│   │   │   ├── util/ (Password, Validation, Currency, Alert utilities)
│   │   │   └── data/ (OrderQueue, SearchSortUtil, DataStructureManager)
│   │   └── resources/
│   │       ├── fxml/ (Login, Dashboard, Menu, Orders, Kitchen, Billing, etc.)
│   │       ├── css/ (Modern UI styles)
│   │       └── config/ (application.properties)
│   └── test/
│       └── java/com/restaurant/
│           ├── service/ (BillingServiceTest, OrderServiceTest, InventoryServiceTest)
│           ├── util/ (ValidationUtilTest)
│           └── repository/ (DatabaseConnectionTest)
└── logs/
```

---

## 🚀 Getting Started

### 1. Database Setup
Ensure MySQL Server is active on port 3306. Create and seed the database:
```bash
mysql -u root -p < database/schema.sql
mysql -u root -p < database/sample_data.sql
```

Update your database credentials in `src/main/resources/config/application.properties` if different from `root`/`root`.

### 2. Build & Run Application
```bash
cd restaurant-management-system
mvn clean compile
mvn javafx:run
```

### 3. Run Unit Tests
```bash
mvn test
```

---

## 🔑 Default Login Credentials

| Username | Password | Role | Permissions |
|---|---|---|---|
| `admin` | `admin123` | **ADMIN** | Full administrative control, user & inventory management, analytics |
| `manager` | `manager123` | **MANAGER** | Menu editing, table management, reservations, stock monitoring |
| `waiter1` | `waiter123` | **WAITER** | Order taking, table allocation, kitchen dispatch |
| `cashier1` | `cashier123` | **CASHIER** | Bill calculation, receipt printing, payment processing |
| `chef1` | `kitchen123` | **KITCHEN_STAFF** | Kitchen ticket display, preparation updates |
