# Database Setup Guide — Restaurant Management System

This system uses MySQL 8.0+ as its relational database back-end.

## Prerequisites
- MySQL Server 8.0+ running on port `3306`
- MySQL Workbench or MySQL Command Line Client

## Step-by-Step Installation

### Option 1: Via MySQL CLI
```bash
# Connect to MySQL
mysql -u root -p

# Execute Schema Creation
source /path/to/restaurant-management-system/database/schema.sql

# Execute Sample Seed Data
source /path/to/restaurant-management-system/database/sample_data.sql
```

### Option 2: Via MySQL Workbench
1. Open MySQL Workbench and connect to your local instance.
2. Go to **File -> Open SQL Script...** and select `database/schema.sql`.
3. Click the **Execute (lightning bolt)** icon to build the tables and constraints.
4. Open `database/sample_data.sql` and execute it to populate sample roles, tables, menu items, and inventory.

## Default Database Credentials
Configure in `src/main/resources/config/application.properties`:
- **URL**: `jdbc:mysql://localhost:3306/restaurant_management`
- **Username**: `root`
- **Password**: `root` (change to match your local MySQL root password)

## Pre-seeded User Accounts
| Role | Username | Default Password | Access Scope |
|---|---|---|---|
| ADMIN | `admin` | `admin123` | Full System Access, Configuration, Staff, Reports |
| MANAGER | `manager` | `manager123` | Menu, Tables, Inventory, Reservations, Analytics |
| WAITER | `waiter1` | `waiter123` | Table Allocation, Order Creation, Kitchen Dispatch |
| CASHIER | `cashier1` | `cashier123` | Billing, Payments, Receipts, Transactions |
| KITCHEN_STAFF | `chef1` | `kitchen123` | Live Kitchen Display (KDS), Order Status Flow |
