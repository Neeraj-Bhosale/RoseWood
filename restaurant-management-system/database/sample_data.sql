-- ==========================================================
-- RESTAURANT MANAGEMENT SYSTEM (RMS) SAMPLE DATA
-- Pre-populated users, tables, categories, menu items, inventory, suppliers
-- ==========================================================

USE restaurant_management;

-- 1. Users (Passwords hashed using SHA-256 with salt)
-- Default password for all seed accounts is: admin123 / manager123 / waiter123 / cashier123 / kitchen123
-- Stored password hashes computed with SHA-256 + salt
INSERT INTO users (user_id, username, password_hash, salt, full_name, email, phone, role, is_active) VALUES
(1, 'admin', '63284bfe7d252fda9d15068091437c214dc88d3796a471f078ea426cf1757fbc', 'MdlE1iMpPdE3q5xs6Bh4PQ==', 'System Administrator', 'admin@restaurant.com', '+91 9876543210', 'ADMIN', TRUE),
(2, 'manager', '1c4e241e9dd8591f6e203c9f47f3678df71c6ecbaa104f900980100fb418dcb0', 'SALT_MGR_002', 'Vikram Malhotra', 'manager@restaurant.com', '+91 9876543211', 'MANAGER', TRUE),
(3, 'waiter1', 'c21bbd68d02ed494ed9a0394801480225d705e53d30b8e2a303611c2e9690f46', 'SALT_WTR_003', 'Rahul Sharma', 'rahul@restaurant.com', '+91 9876543212', 'WAITER', TRUE),
(4, 'cashier1', '38c56ac6a64b2b53e5c581a8517ce01084ccb6583e61adb7ea6b4fe5ac4c4f02', 'SALT_CSH_004', 'Sneha Patel', 'sneha@restaurant.com', '+91 9876543213', 'CASHIER', TRUE),
(5, 'chef1', '724cd9213a71c32454feea73f4fbb15164548dcd90def3364840e6af6466792f', 'SALT_CHF_005', 'Chef Sanjeev Kumar', 'sanjeev@restaurant.com', '+91 9876543214', 'KITCHEN_STAFF', TRUE)
ON DUPLICATE KEY UPDATE password_hash = VALUES(password_hash), salt = VALUES(salt), full_name = VALUES(full_name), role = VALUES(role), is_active = VALUES(is_active);

-- 2. Employees Details
INSERT INTO employees (user_id, designation, salary, hire_date, shift_timing, emergency_contact) VALUES
(1, 'General Administrator', 85000.00, '2023-01-01', 'General', '+91 9999900001'),
(2, 'Restaurant General Manager', 60000.00, '2023-02-15', 'Evening (2 PM - 11 PM)', '+91 9999900002'),
(3, 'Head Server', 28000.00, '2023-06-10', 'Full Day', '+91 9999900003'),
(4, 'Chief Cashier & Accountant', 32000.00, '2023-04-01', 'Day Shift', '+91 9999900004'),
(5, 'Executive Head Chef', 65000.00, '2023-01-15', 'Kitchen Schedule', '+91 9999900005');

-- 3. Customers
INSERT INTO customers (customer_id, name, phone, email, address, loyalty_points, total_visits, total_spent) VALUES
(1, 'Amitabh Roy', '+91 9811122233', 'amitabh.roy@example.com', 'Flat 402, Greenfield Apts, Bengaluru', 45, 8, 4520.00),
(2, 'Pooja Hegde', '+91 9822233344', 'pooja.h@example.com', 'Plot 12, Indiranagar, Bengaluru', 120, 15, 12850.00),
(3, 'Rohan Verma', '+91 9833344455', 'rohan.v@example.com', 'Koramangala 4th Block, Bengaluru', 25, 3, 2600.00),
(4, 'Ananya Sen', '+91 9844455566', 'ananya.sen@example.com', 'HSR Layout Sector 2, Bengaluru', 80, 10, 8900.00),
(5, 'Walk-in Guest', 'N/A', 'guest@grandfeast.com', 'Restaurant Counter', 0, 1, 0.00);

-- 4. Restaurant Tables
INSERT INTO restaurant_tables (table_id, table_number, capacity, location, status) VALUES
(1, 'T-01', 2, 'Window Bay A', 'AVAILABLE'),
(2, 'T-02', 2, 'Window Bay A', 'AVAILABLE'),
(3, 'T-03', 4, 'Central Hall', 'AVAILABLE'),
(4, 'T-04', 4, 'Central Hall', 'AVAILABLE'),
(5, 'T-05', 4, 'Central Hall', 'AVAILABLE'),
(6, 'T-06', 6, 'Family Section', 'AVAILABLE'),
(7, 'T-07', 6, 'Family Section', 'AVAILABLE'),
(8, 'T-08', 8, 'VIP Lounge', 'AVAILABLE'),
(9, 'T-09', 10, 'Private Dining Room', 'AVAILABLE'),
(10, 'T-10', 4, 'Outdoor Terrace', 'AVAILABLE');

-- 5. Menu Categories
INSERT INTO menu_categories (category_id, name, description, display_order) VALUES
(1, 'Starters & Appetizers', 'Crispy, flavorful finger foods and appetizers', 1),
(2, 'Main Course - Indian', 'Authentic North & South Indian curries and gravies', 2),
(3, 'Main Course - Continental & Italian', 'Artisanal Pizzas, Pastas, and Sizzlers', 3),
(4, 'Fast Food & Sandwiches', 'Gourmet burgers, wraps, and french fries', 4),
(5, 'Desserts & Sweets', 'Artisanal cakes, puddings, and ice creams', 5),
(6, 'Beverages & Mocktails', 'Fresh fruit juices, coolers, and hot brews', 6);

-- 6. Menu Items
INSERT INTO menu_items (item_id, category_id, name, description, price, preparation_time, is_available) VALUES
-- Starters
(1, 1, 'Paneer Tikka Shashlik', 'Clay oven smoked cottage cheese with bell peppers and spices', 320.00, 15, TRUE),
(2, 1, 'Crispy Corn Salt & Pepper', 'Sweet corn tossed with scallions and oriental seasonings', 240.00, 10, TRUE),
(3, 1, 'Chicken Malai Tikka', 'Tender boneless chicken marinated with cream, cardamom & cheese', 390.00, 18, TRUE),
(4, 1, 'Golden Fried Prawns', 'Crispy batter fried tiger prawns served with tartar dip', 480.00, 15, TRUE),

-- Main Course - Indian
(5, 2, 'Butter Chicken Supreme', 'Charcoal roasted chicken in rich velvety tomato gravy', 440.00, 20, TRUE),
(6, 2, 'Paneer Butter Masala', 'Fresh cottage cheese in creamy spiced tomato sauce', 360.00, 15, TRUE),
(7, 2, 'Dal Makhani Bukhara', 'Slow-cooked black lentils simmered overnight with butter & cream', 290.00, 12, TRUE),
(8, 2, 'Dum Hyderabadi Mutton Biryani', 'Fragrant basmati rice layered with spiced tender lamb', 520.00, 22, TRUE),
(9, 2, 'Garlic Butter Naan', 'Clay oven leavened bread brushed with garlic & fresh coriander', 60.00, 5, TRUE),

-- Main Course - Italian / Continental
(10, 3, 'Margherita Pizza 11-inch', 'Fresh mozzarella, San Marzano tomato basil sauce', 450.00, 15, TRUE),
(11, 3, 'Penne Alfredo Pollo', 'Penne pasta tossed in rich parmesan cream with grilled chicken', 420.00, 15, TRUE),
(12, 3, 'Vegetable Lasagna Classico', 'Layered pasta sheets with baked garden vegetables & ricotta', 390.00, 18, TRUE),

-- Fast Food
(13, 4, 'Grand BBQ Beef/Lamb Burger', 'Juicy grilled patty with cheddar cheese, caramelized onions & fries', 350.00, 12, TRUE),
(14, 4, 'Crispy Peri-Peri Fries', 'Double fried potato chips seasoned with spicy African peri-peri', 160.00, 8, TRUE),

-- Desserts
(15, 5, 'Warm Chocolate Brownie Sundae', 'Gooey dark fudge brownie with vanilla bean gelato & hot chocolate fudge', 220.00, 5, TRUE),
(16, 5, 'Gulab Jamun with Rabri', 'Classic fried milk dumplings topped with saffron-infused condensed milk', 180.00, 5, TRUE),

-- Beverages
(17, 6, 'Classic Virgin Mojito', 'Crushed fresh mint, lime chunks, sparkling soda & cane sugar', 160.00, 5, TRUE),
(18, 6, 'Cold Coffee with Ice Cream', 'Chilled espresso blended with full cream milk and dark roast scoops', 180.00, 5, TRUE),
(19, 6, 'Masala Chai Pot', 'Traditional ginger and cardamom infused Assam tea', 90.00, 5, TRUE);

-- 7. Suppliers
INSERT INTO suppliers (supplier_id, name, contact_person, phone, email, address, supplied_category) VALUES
(1, 'FarmFresh Dairy & Organics', 'Suresh Reddy', '+91 9711100001', 'orders@farmfreshdairy.in', 'Dairy Circle, Bengaluru', 'Dairy, Milk, Paneer & Cheese'),
(2, 'GreenHarvest Fresh Produce', 'Ramesh Rao', '+91 9722200002', 'contact@greenharvest.com', 'APMC Market Yard, Bengaluru', 'Vegetables, Fruits, Herbs'),
(3, 'Prime Poultry & Meats', 'Kabeer Khan', '+91 9733300003', 'sales@primepoultry.in', 'Frazer Town, Bengaluru', 'Chicken, Mutton, Seafood'),
(4, 'Bakers Pride Flour & Spices', 'Sunil Goyal', '+91 9744400004', 'bakerspride@trade.com', 'Rajajinagar Industrial Area', 'Flour, Grains, Spices & Oils');

-- 8. Inventory Ingredients
INSERT INTO inventory (inventory_id, ingredient_name, category, quantity, unit, minimum_stock, unit_cost, supplier_id) VALUES
(1, 'Fresh Cottage Cheese (Paneer)', 'Dairy', 18.50, 'kg', 5.00, 320.00, 1),
(2, 'Mozzarella Cheese', 'Dairy', 12.00, 'kg', 4.00, 480.00, 1),
(3, 'Fresh Chicken Breast', 'Meat', 25.00, 'kg', 10.00, 240.00, 3),
(4, 'Mutton Cuts', 'Meat', 15.00, 'kg', 8.00, 680.00, 3),
(5, 'Tiger Prawns', 'Seafood', 6.00, 'kg', 3.00, 750.00, 3),
(6, 'Tomatoes', 'Vegetables', 30.00, 'kg', 10.00, 35.00, 2),
(7, 'Onions', 'Vegetables', 45.00, 'kg', 15.00, 40.00, 2),
(8, 'Bell Peppers (Capsicum)', 'Vegetables', 14.00, 'kg', 5.00, 60.00, 2),
(9, 'Basmati Rice Premium', 'Grains', 60.00, 'kg', 20.00, 110.00, 4),
(10, 'All-Purpose Flour (Maida)', 'Flour', 50.00, 'kg', 15.00, 45.00, 4),
(11, 'Cold Brew Coffee Beans', 'Beverage Dry', 3.50, 'kg', 5.00, 950.00, 4),
(12, 'Cooking Butter', 'Dairy', 20.00, 'kg', 6.00, 460.00, 1);

-- 9. Menu Item Ingredient Recipes
INSERT INTO menu_item_ingredients (menu_item_id, inventory_id, quantity_required, unit) VALUES
(1, 1, 0.35, 'kg'),
(1, 8, 0.20, 'kg'),
(1, 7, 0.10, 'kg'),
(5, 3, 0.45, 'kg'),
(5, 6, 0.30, 'kg'),
(5, 7, 0.20, 'kg'),
(5, 12, 0.10, 'kg'),
(6, 1, 0.40, 'kg'),
(6, 6, 0.28, 'kg'),
(6, 7, 0.18, 'kg'),
(6, 12, 0.08, 'kg'),
(8, 4, 0.60, 'kg'),
(8, 9, 0.45, 'kg'),
(8, 6, 0.30, 'kg'),
(8, 7, 0.20, 'kg'),
(10, 2, 0.25, 'kg'),
(10, 6, 0.30, 'kg'),
(10, 7, 0.10, 'kg'),
(11, 2, 0.18, 'kg'),
(11, 12, 0.10, 'kg'),
(11, 7, 0.12, 'kg'),
(12, 2, 0.22, 'kg'),
(12, 6, 0.25, 'kg'),
(12, 7, 0.15, 'kg'),
(12, 10, 0.15, 'kg'),
(13, 3, 0.25, 'kg'),
(13, 10, 0.10, 'kg'),
(13, 7, 0.12, 'kg');

-- 10. Reservations
INSERT INTO reservations (reservation_id, customer_id, table_id, reservation_date, reservation_time, guest_count, special_request, status) VALUES
(1, 1, 8, CURDATE(), '19:30:00', 6, 'Anniversary decoration requested', 'CONFIRMED'),
(2, 2, 3, CURDATE(), '20:00:00', 4, 'Window seat preference', 'CONFIRMED'),
(3, 4, 1, DATE_ADD(CURDATE(), INTERVAL 1 DAY), '13:00:00', 2, 'Quiet corner', 'PENDING');

-- 11. Sample Completed Orders & Items for Instant Reporting
INSERT INTO orders (order_id, order_number, customer_id, table_id, employee_id, order_date_time, status, subtotal, discount_amount, tax_amount, service_charge, total_amount, special_notes) VALUES
(1, 'ORD-20261001-001', 1, 3, 3, DATE_SUB(NOW(), INTERVAL 2 HOUR), 'COMPLETED', 1020.00, 50.00, 48.50, 25.50, 1044.00, 'Regular order served well'),
(2, 'ORD-20261001-002', 2, 7, 3, DATE_SUB(NOW(), INTERVAL 1 HOUR), 'COMPLETED', 1450.00, 0.00, 72.50, 36.25, 1558.75, 'Quick service');

INSERT INTO order_items (order_item_id, order_id, item_id, quantity, unit_price, special_instructions, subtotal, status) VALUES
(1, 1, 1, 1, 320.00, 'Medium spicy', 320.00, 'SERVED'),
(2, 1, 6, 1, 360.00, 'Less oil', 360.00, 'SERVED'),
(3, 1, 9, 4, 60.00, 'Hot & crisp', 240.00, 'SERVED'),
(4, 1, 17, 2, 160.00, 'Extra mint', 320.00, 'SERVED'),
(5, 2, 5, 2, 440.00, 'Rich gravy', 880.00, 'SERVED'),
(6, 2, 8, 1, 520.00, 'Traditional raita', 520.00, 'SERVED'),
(7, 2, 18, 1, 180.00, 'Strong coffee', 180.00, 'SERVED');

INSERT INTO payments (payment_id, order_id, invoice_number, amount, payment_method, payment_date_time, transaction_reference, status, processed_by) VALUES
(1, 1, 'INV-20261001-001', 1044.00, 'UPI', DATE_SUB(NOW(), INTERVAL 1 HOUR), 'UPI-TXN-9841847192', 'PAID', 4),
(2, 2, 'INV-20261001-002', 1558.75, 'CREDIT_CARD', DATE_SUB(NOW(), INTERVAL 30 MINUTE), 'CC-VISA-4491028', 'PAID', 4);
