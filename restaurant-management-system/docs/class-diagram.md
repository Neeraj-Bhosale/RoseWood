# Object-Oriented Class Hierarchy & Design Patterns

## 1. Class Diagram

```mermaid
classDiagram
    class User {
        <<abstract>>
        #int userId
        #String username
        #String fullName
        #String email
        #UserRole role
        +getPermissions() List~String~*
        +hasPermission(String permission) boolean
    }
    class Admin {
        +getPermissions() List~String~
    }
    class Manager {
        +getPermissions() List~String~
    }
    class Waiter {
        +getPermissions() List~String~
    }
    class Cashier {
        +getPermissions() List~String~
    }
    class KitchenStaff {
        +getPermissions() List~String~
    }

    User <|-- Admin
    User <|-- Manager
    User <|-- Waiter
    User <|-- Cashier
    User <|-- KitchenStaff

    class Payment {
        <<abstract>>
        #int paymentId
        #int orderId
        #String invoiceNumber
        #double amount
        #PaymentMethod method
        #PaymentStatus status
        +processPayment()* boolean
        +generateReceipt()* String
    }
    class CashPayment {
        -double cashTendered
        -double changeReturned
        +processPayment() boolean
        +generateReceipt() String
    }
    class UPIPayment {
        -String vpaId
        -String transactionRef
        +processPayment() boolean
        +generateReceipt() String
    }
    class CardPayment {
        -String cardLastFour
        -String approvalCode
        +processPayment() boolean
        +generateReceipt() String
    }

    Payment <|-- CashPayment
    Payment <|-- UPIPayment
    Payment <|-- CardPayment
```

## 2. Core OOP Principles Demonstrated

1. **Encapsulation**:
   - All properties in domain models are private/protected with explicit, validated getters and setters.
2. **Inheritance**:
   - `User` abstract parent class extended by `Admin`, `Manager`, `Waiter`, `Cashier`, `KitchenStaff`.
   - `Payment` abstract class extended by `CashPayment`, `UPIPayment`, `CardPayment`.
3. **Polymorphism**:
   - `Payment.processPayment()` executes specialized validation and receipt structuring dynamically at runtime depending on the payment mode selected.
4. **Abstraction & Interfaces**:
   - Distinct generic Repository interfaces (`IRepository<T, ID>`) and Service contracts.
5. **Data Structures in Action**:
   - `PriorityQueue<KitchenOrder>` orders incoming kitchen tickets prioritized by waiting duration & cooking urgency.
   - `HashMap<Integer, MenuItem>` provides O(1) in-memory lookups for rapid bill aggregation.
   - `SearchSortUtil` implements binary search and quicksort on menu collections.
