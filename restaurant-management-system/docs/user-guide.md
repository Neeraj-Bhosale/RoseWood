# User Guide — Restaurant Management System (RMS)

## 1. Getting Started

### Launching the Application
Run through Maven:
```bash
mvn clean javafx:run
```

### Logging In
Use any of the following credentials depending on the testing scenario:
- **Administrator**: `admin` / `admin123`
- **Restaurant Manager**: `manager` / `manager123`
- **Floor Waiter**: `waiter1` / `waiter123`
- **Cashier**: `cashier1` / `cashier123`
- **Kitchen Chef**: `chef1` / `kitchen123`

---

## 2. Core Workflow Walkthrough

### Step 1: Dashboard Overview
- View live metric cards: Daily Revenue, Total Orders, Occupied vs. Available Tables, Kitchen queue depth, and Low-stock alerts.

### Step 2: Customer Registration & Table Selection
- Navigate to **Customers** to register new guests or award loyalty points.
- Go to **Tables** to view the floor map and toggle table statuses (`AVAILABLE`, `OCCUPIED`, `RESERVED`, `CLEANING`).

### Step 3: Taking & Submitting Orders
- Go to **Orders** -> **Create Order**.
- Select Table, Customer, and add items from categorized menu lists.
- Set special cooking instructions and quantities.
- Click **Send to Kitchen** — this transitions the order to `CONFIRMED` and inserts it into the Kitchen Priority Queue.

### Step 4: Kitchen Order Display (KDS)
- Log in as kitchen staff (or view the **Kitchen** screen).
- The order appears in real-time.
- Chef clicks **Start Preparing** (`PREPARING`) then **Mark as Ready** (`READY`).

### Step 5: Service & Billing
- Waiter delivers food and marks order as `SERVED`.
- Cashier navigates to **Billing**, selects the table/order, applies loyalty discounts or tax rates, and reviews the auto-calculated breakdown.

### Step 6: Payment & Settlement
- Cashier navigates to **Payments**, selects payment method (Cash, UPI, Credit/Debit Card), validates payment reference, and clicks **Complete Transaction**.
- System prints/displays formatted receipt, sets order to `COMPLETED`, deducts inventory ingredients, and updates the sales ledger.

### Step 7: Reports & Analytics
- Manager/Admin navigates to **Reports** to view interactive JavaFX Bar, Pie, and Line charts covering daily revenue, popular items, and stock health.
