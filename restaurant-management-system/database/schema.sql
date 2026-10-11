-- ==========================================================
-- RESTAURANT MANAGEMENT SYSTEM (RMS) DATABASE SCHEMA
-- Target Engine: MySQL 8.0+
-- Database: restaurant_management
-- ==========================================================

DROP DATABASE IF EXISTS restaurant_management;
CREATE DATABASE restaurant_management CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE restaurant_management;

-- 1. Users Table (Role-based authentication)
CREATE TABLE users (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    salt VARCHAR(64) NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    phone VARCHAR(20) NOT NULL,
    role ENUM('ADMIN', 'MANAGER', 'WAITER', 'CASHIER', 'KITCHEN_STAFF') NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_user_username (username),
    INDEX idx_user_role (role)
) ENGINE=InnoDB;

-- 2. Employees Details Table (Inheritance/Detail mapping)
CREATE TABLE employees (
    employee_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL UNIQUE,
    designation VARCHAR(50) NOT NULL,
    salary DECIMAL(10, 2) NOT NULL CHECK (salary >= 0),
    hire_date DATE NOT NULL,
    shift_timing VARCHAR(50) DEFAULT 'General (9 AM - 6 PM)',
    emergency_contact VARCHAR(20),
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- 3. Customers Table (with loyalty management)
CREATE TABLE customers (
    customer_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    phone VARCHAR(20) NOT NULL UNIQUE,
    email VARCHAR(100),
    address TEXT,
    loyalty_points INT NOT NULL DEFAULT 0 CHECK (loyalty_points >= 0),
    total_visits INT NOT NULL DEFAULT 0 CHECK (total_visits >= 0),
    total_spent DECIMAL(12, 2) NOT NULL DEFAULT 0.00 CHECK (total_spent >= 0),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_cust_phone (phone),
    INDEX idx_cust_name (name)
) ENGINE=InnoDB;

-- 4. Restaurant Tables
CREATE TABLE restaurant_tables (
    table_id INT AUTO_INCREMENT PRIMARY KEY,
    table_number VARCHAR(10) NOT NULL UNIQUE,
    capacity INT NOT NULL CHECK (capacity > 0),
    location VARCHAR(50) DEFAULT 'Main Dining Hall',
    status ENUM('AVAILABLE', 'OCCUPIED', 'RESERVED', 'CLEANING') NOT NULL DEFAULT 'AVAILABLE',
    INDEX idx_table_status (status)
) ENGINE=InnoDB;

-- 5. Menu Categories
CREATE TABLE menu_categories (
    category_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE,
    description VARCHAR(255),
    display_order INT DEFAULT 0
) ENGINE=InnoDB;

-- 6. Menu Items
CREATE TABLE menu_items (
    item_id INT AUTO_INCREMENT PRIMARY KEY,
    category_id INT NOT NULL,
    name VARCHAR(100) NOT NULL UNIQUE,
    description TEXT,
    price DECIMAL(10, 2) NOT NULL CHECK (price >= 0),
    preparation_time INT NOT NULL DEFAULT 15 COMMENT 'Minutes',
    is_available BOOLEAN NOT NULL DEFAULT TRUE,
    image_path VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (category_id) REFERENCES menu_categories(category_id) ON DELETE RESTRICT,
    INDEX idx_item_category (category_id),
    INDEX idx_item_avail (is_available)
) ENGINE=InnoDB;

-- 7. Table Reservations
CREATE TABLE reservations (
    reservation_id INT AUTO_INCREMENT PRIMARY KEY,
    customer_id INT NOT NULL,
    table_id INT NOT NULL,
    reservation_date DATE NOT NULL,
    reservation_time TIME NOT NULL,
    guest_count INT NOT NULL CHECK (guest_count > 0),
    special_request TEXT,
    status ENUM('PENDING', 'CONFIRMED', 'CANCELLED', 'COMPLETED') NOT NULL DEFAULT 'PENDING',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (customer_id) REFERENCES customers(customer_id) ON DELETE CASCADE,
    FOREIGN KEY (table_id) REFERENCES restaurant_tables(table_id) ON DELETE RESTRICT,
    INDEX idx_res_datetime (reservation_date, reservation_time),
    INDEX idx_res_status (status)
) ENGINE=InnoDB;

-- 8. Suppliers
CREATE TABLE suppliers (
    supplier_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    contact_person VARCHAR(100),
    phone VARCHAR(20) NOT NULL,
    email VARCHAR(100),
    address TEXT,
    supplied_category VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- 9. Inventory Ingredients
CREATE TABLE inventory (
    inventory_id INT AUTO_INCREMENT PRIMARY KEY,
    ingredient_name VARCHAR(100) NOT NULL UNIQUE,
    category VARCHAR(50) NOT NULL,
    quantity DECIMAL(10, 2) NOT NULL DEFAULT 0 CHECK (quantity >= 0),
    unit VARCHAR(20) NOT NULL COMMENT 'kg, litres, units, grams',
    minimum_stock DECIMAL(10, 2) NOT NULL DEFAULT 5 CHECK (minimum_stock >= 0),
    unit_cost DECIMAL(10, 2) NOT NULL DEFAULT 0.00,
    supplier_id INT,
    last_updated TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (supplier_id) REFERENCES suppliers(supplier_id) ON DELETE SET NULL,
    INDEX idx_inv_stock (quantity, minimum_stock)
) ENGINE=InnoDB;

-- 10. Menu Item Ingredient Recipes
CREATE TABLE menu_item_ingredients (
    recipe_id INT AUTO_INCREMENT PRIMARY KEY,
    menu_item_id INT NOT NULL,
    inventory_id INT NOT NULL,
    quantity_required DECIMAL(10, 2) NOT NULL CHECK (quantity_required > 0),
    unit VARCHAR(20) NOT NULL DEFAULT 'kg',
    FOREIGN KEY (menu_item_id) REFERENCES menu_items(item_id) ON DELETE CASCADE,
    FOREIGN KEY (inventory_id) REFERENCES inventory(inventory_id) ON DELETE RESTRICT,
    UNIQUE (menu_item_id, inventory_id),
    INDEX idx_menu_recipe_item (menu_item_id)
) ENGINE=InnoDB;

-- 11. Orders Master
CREATE TABLE orders (
    order_id INT AUTO_INCREMENT PRIMARY KEY,
    order_number VARCHAR(30) NOT NULL UNIQUE,
    customer_id INT,
    table_id INT,
    employee_id INT NOT NULL,
    order_date_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    status ENUM('CREATED', 'CONFIRMED', 'PREPARING', 'READY', 'SERVED', 'COMPLETED', 'CANCELLED') NOT NULL DEFAULT 'CREATED',
    subtotal DECIMAL(10, 2) NOT NULL DEFAULT 0.00 CHECK (subtotal >= 0),
    discount_amount DECIMAL(10, 2) NOT NULL DEFAULT 0.00 CHECK (discount_amount >= 0),
    tax_amount DECIMAL(10, 2) NOT NULL DEFAULT 0.00 CHECK (tax_amount >= 0),
    service_charge DECIMAL(10, 2) NOT NULL DEFAULT 0.00 CHECK (service_charge >= 0),
    total_amount DECIMAL(10, 2) NOT NULL DEFAULT 0.00 CHECK (total_amount >= 0),
    special_notes TEXT,
    FOREIGN KEY (customer_id) REFERENCES customers(customer_id) ON DELETE SET NULL,
    FOREIGN KEY (table_id) REFERENCES restaurant_tables(table_id) ON DELETE RESTRICT,
    FOREIGN KEY (employee_id) REFERENCES users(user_id) ON DELETE RESTRICT,
    INDEX idx_order_date (order_date_time),
    INDEX idx_order_status (status)
) ENGINE=InnoDB;

-- 12. Order Line Items
CREATE TABLE order_items (
    order_item_id INT AUTO_INCREMENT PRIMARY KEY,
    order_id INT NOT NULL,
    item_id INT NOT NULL,
    quantity INT NOT NULL CHECK (quantity > 0),
    unit_price DECIMAL(10, 2) NOT NULL CHECK (unit_price >= 0),
    special_instructions VARCHAR(255),
    subtotal DECIMAL(10, 2) NOT NULL CHECK (subtotal >= 0),
    status ENUM('PENDING', 'PREPARING', 'READY', 'SERVED', 'CANCELLED') DEFAULT 'PENDING',
    FOREIGN KEY (order_id) REFERENCES orders(order_id) ON DELETE CASCADE,
    FOREIGN KEY (item_id) REFERENCES menu_items(item_id) ON DELETE RESTRICT,
    INDEX idx_orderitem_order (order_id)
) ENGINE=InnoDB;

-- 13. Payments Transactions
CREATE TABLE payments (
    payment_id INT AUTO_INCREMENT PRIMARY KEY,
    order_id INT NOT NULL UNIQUE,
    invoice_number VARCHAR(50) NOT NULL UNIQUE,
    amount DECIMAL(10, 2) NOT NULL CHECK (amount >= 0),
    payment_method ENUM('CASH', 'UPI', 'CREDIT_CARD', 'DEBIT_CARD') NOT NULL,
    payment_date_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    transaction_reference VARCHAR(100),
    status ENUM('PENDING', 'PAID', 'FAILED', 'REFUNDED') NOT NULL DEFAULT 'PENDING',
    processed_by INT NOT NULL,
    FOREIGN KEY (order_id) REFERENCES orders(order_id) ON DELETE RESTRICT,
    FOREIGN KEY (processed_by) REFERENCES users(user_id) ON DELETE RESTRICT,
    INDEX idx_payment_date (payment_date_time),
    INDEX idx_payment_method (payment_method)
) ENGINE=InnoDB;

-- 14. Purchases (Inventory Stock Refill)
CREATE TABLE purchases (
    purchase_id INT AUTO_INCREMENT PRIMARY KEY,
    supplier_id INT NOT NULL,
    purchase_date DATE NOT NULL,
    total_cost DECIMAL(10, 2) NOT NULL DEFAULT 0.00,
    invoice_ref VARCHAR(50),
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (supplier_id) REFERENCES suppliers(supplier_id) ON DELETE RESTRICT
) ENGINE=InnoDB;

-- 15. Purchase Items
CREATE TABLE purchase_items (
    purchase_item_id INT AUTO_INCREMENT PRIMARY KEY,
    purchase_id INT NOT NULL,
    inventory_id INT NOT NULL,
    quantity DECIMAL(10, 2) NOT NULL CHECK (quantity > 0),
    unit_cost DECIMAL(10, 2) NOT NULL CHECK (unit_cost >= 0),
    subtotal DECIMAL(10, 2) NOT NULL CHECK (subtotal >= 0),
    FOREIGN KEY (purchase_id) REFERENCES purchases(purchase_id) ON DELETE CASCADE,
    FOREIGN KEY (inventory_id) REFERENCES inventory(inventory_id) ON DELETE RESTRICT
) ENGINE=InnoDB;
