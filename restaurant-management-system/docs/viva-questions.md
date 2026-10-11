# Comprehensive Viva & Technical Defense Guide

## 1. Object-Oriented Programming (OOP) in this Project

### Q1: How did you implement OOP principles in your Restaurant Management System?
- **Encapsulation**: Every model entity (`User`, `Customer`, `Order`, `MenuItem`, `Table`) has strictly `private` attributes, accessed solely through validated getters and setters.
- **Inheritance**: We created an abstract base class `User` with concrete specialized subclasses `Admin`, `Manager`, `Waiter`, `Cashier`, and `KitchenStaff`. Likewise, the `Payment` base class is inherited by `CashPayment`, `UPIPayment`, and `CardPayment`.
- **Polymorphism**: The `Payment` class declares an abstract `processPayment()` and `generateReceipt()` method. At runtime, the cashier triggers `payment.processPayment()`, executing distinct logic depending on whether cash change calculation, UPI VPA validation, or card authorization is needed.
- **Abstraction**: We defined repository interfaces (`IRepository<T, ID>`) and service abstractions so that UI controllers never interact directly with low-level SQL or database drivers.

### Q2: Why did you use Layered / MVC Architecture?
- It guarantees **Separation of Concerns**. JavaFX Controllers only deal with user events; Services execute business logic, tax calculations, and validations; Repositories handle JDBC queries; and Models encapsulate domain state. This makes the system clean, testable, and maintainable.

---

## 2. Data Structures & Algorithms (DSA) Questions

### Q3: Which Java Collections and Data Structures are used and why?
- **`PriorityQueue<KitchenOrder>`**: Used in `OrderQueue.java` for the Kitchen Display System so higher priority or older pending orders are prioritized before newer ones.
- **`HashMap<Integer, MenuItem>`**: Used for O(1) in-memory lookups when assembling order lines and calculating bill subtotals.
- **`ArrayList<OrderItem>`**: Dynamic storage for variable line-items within a customer's order.
- **`HashSet<String>`**: Used for instantaneous uniqueness checks (e.g. verifying unique table numbers or active sessions).
- **`SearchSortUtil`**: Custom algorithms for binary searching sorted menu items and quick-sorting inventory items by stock levels.

---

## 3. Database & JDBC Questions

### Q4: How is SQL Injection prevented?
- We strictly utilize `PreparedStatement` with parameterized placeholders (`?`) for all database operations across all repositories. No dynamic string concatenation is used for SQL queries.

### Q5: How are database transactions handled?
- For multi-step operations like checkout (which requires creating a payment record, marking the order completed, updating table status to cleaning, and deducting stock), we use JDBC transactions via `connection.setAutoCommit(false)` and `connection.commit()` with `connection.rollback()` in catch blocks.

---

## 4. Exception Handling & Security

### Q6: What custom exceptions exist in your project?
- `InvalidLoginException`, `TableNotAvailableException`, `MenuItemNotAvailableException`, `InsufficientStockException`, `PaymentFailedException`, `InvalidReservationException`, `DatabaseOperationException`, `InvalidOrderException`.

### Q7: How are user passwords stored securely?
- Passwords are never stored in plain text. We utilize salted SHA-256 cryptographic hashing (`PasswordUtil.java`), generating a unique cryptographic salt per user.
