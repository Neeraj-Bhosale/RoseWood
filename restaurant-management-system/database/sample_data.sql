-- ==========================================================
-- RESTAURANT MANAGEMENT SYSTEM (RMS) SAMPLE DATA
-- Pre-populated users, tables, categories, menu items, inventory, suppliers, customers
-- Theme: Authentic Indian Restaurant
-- ==========================================================

USE restaurant_management;

-- 1. Users (Passwords hashed using SHA-256 with salt)
-- Default passwords: admin123 / manager123 / waiter123 / cashier123 / kitchen123
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
(5, 'Executive Head Chef', 65000.00, '2023-01-15', 'Kitchen Schedule', '+91 9999900005')
ON DUPLICATE KEY UPDATE designation = VALUES(designation), salary = VALUES(salary);

-- 3. Restaurant Tables
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
(10, 'T-10', 4, 'Outdoor Terrace', 'AVAILABLE')
ON DUPLICATE KEY UPDATE capacity = VALUES(capacity), location = VALUES(location);

-- 4. Suppliers (7 records)
INSERT INTO suppliers (supplier_id, name, contact_person, phone, email, address, supplied_category) VALUES
(1, 'Shree Dairy Farm', 'Mahesh Pawar', '9876543210', 'shreedairy@example.com', 'Dairy Belt, Pune', 'Dairy products'),
(2, 'FreshFields Produce', 'Rahul Jadhav', '9876543211', 'freshfields@example.com', 'APMC Market Yard', 'Fruits & vegetables'),
(3, 'Deccan Poultry Suppliers', 'Suresh Patil', '9876543212', 'deccanpoultry@example.com', 'Bhosari Industrial Area', 'Poultry & eggs'),
(4, 'Grain House Traders', 'Amit Shah', '9876543213', 'grainhouse@example.com', 'Gultekdi Market Yard', 'Grains & rice'),
(5, 'Spice Route Traders', 'Nitin Kulkarni', '9876543214', 'spiceroute@example.com', 'Nana Peth Wholesale Market', 'Spices & masalas'),
(6, 'Coastal Foods Supply', 'Imran Shaikh', '9876543215', 'coastalfoods@example.com', 'Dockline Cold Storage', 'Meat & seafood'),
(7, 'GreenLeaf Organics', 'Prasad More', '9876543216', 'greenleaf@example.com', 'Saswad Organic Farm Valley', 'Organic vegetables')
ON DUPLICATE KEY UPDATE name = VALUES(name), contact_person = VALUES(contact_person), phone = VALUES(phone);

-- 5. Inventory (20 ingredients)
INSERT INTO inventory (inventory_id, ingredient_name, category, quantity, unit, minimum_stock, unit_cost, supplier_id) VALUES
(1, 'Paneer', 'Dairy', 18.00, 'kg', 5.00, 320.00, 1),
(2, 'Chicken', 'Meat', 25.00, 'kg', 8.00, 240.00, 3),
(3, 'Mutton', 'Meat', 12.00, 'kg', 4.00, 680.00, 6),
(4, 'Basmati Rice', 'Grains', 40.00, 'kg', 10.00, 110.00, 4),
(5, 'Butter', 'Dairy', 8.00, 'kg', 3.00, 480.00, 1),
(6, 'Fresh Cream', 'Dairy', 10.00, 'L', 3.00, 220.00, 1),
(7, 'Tomatoes', 'Vegetables', 22.00, 'kg', 8.00, 35.00, 2),
(8, 'Onions', 'Vegetables', 30.00, 'kg', 10.00, 30.00, 2),
(9, 'Potatoes', 'Vegetables', 25.00, 'kg', 8.00, 28.00, 2),
(10, 'Capsicum', 'Vegetables', 8.00, 'kg', 3.00, 70.00, 2),
(11, 'Spinach', 'Vegetables', 7.00, 'kg', 2.00, 40.00, 7),
(12, 'Ginger', 'Spices', 4.00, 'kg', 1.00, 120.00, 2),
(13, 'Garlic', 'Spices', 5.00, 'kg', 2.00, 160.00, 2),
(14, 'Green Chilies', 'Vegetables', 3.00, 'kg', 1.00, 80.00, 2),
(15, 'Coriander Leaves', 'Vegetables', 4.00, 'kg', 1.00, 60.00, 2),
(16, 'Garam Masala', 'Spices', 3.00, 'kg', 1.00, 420.00, 5),
(17, 'Turmeric Powder', 'Spices', 4.00, 'kg', 1.00, 240.00, 5),
(18, 'Cooking Oil', 'Oils', 35.00, 'L', 10.00, 150.00, 4),
(19, 'Wheat Flour', 'Flour', 28.00, 'kg', 10.00, 42.00, 4),
(20, 'Curd', 'Dairy', 12.00, 'kg', 4.00, 80.00, 1)
ON DUPLICATE KEY UPDATE quantity = VALUES(quantity), minimum_stock = VALUES(minimum_stock), unit_cost = VALUES(unit_cost);

-- 6. Menu Categories (8 categories)
INSERT INTO menu_categories (category_id, name, description, display_order) VALUES
(1, 'Starters', 'Crispy, spiced appetizers and finger foods', 1),
(2, 'Main Course', 'Traditional Indian gravies and curries', 2),
(3, 'Rice & Biryani', 'Aromatic Basmati rice specialties and layered biryanis', 3),
(4, 'Indian Breads', 'Clay oven tandoori rotis, butter naans, and breads', 4),
(5, 'Chinese', 'Wok-tossed noodles, fried rice, and Indo-Chinese favorites', 5),
(6, 'South Indian', 'Crispy dosas, steamed idlis, and sambar', 6),
(7, 'Street Food', 'Mumbai-style pav bhaji, spicy misal pav, and chaats', 7),
(8, 'Desserts & Beverages', 'Sweet indulgences, traditional lassi, and hot masala chai', 8)
ON DUPLICATE KEY UPDATE name = VALUES(name), description = VALUES(description);

-- 7. Menu Items (32 dishes)
INSERT INTO menu_items (item_id, category_id, name, description, price, preparation_time, is_available) VALUES
(1, 2, 'Paneer Butter Masala', 'Cottage cheese cubes cooked in rich buttery tomato sauce [Veg]', 280.00, 15, TRUE),
(2, 2, 'Kadai Paneer', 'Paneer tossed with bell peppers and freshly ground kadai spices [Veg]', 260.00, 15, TRUE),
(3, 2, 'Palak Paneer', 'Fresh cottage cheese simmered in smooth spiced spinach puree [Veg]', 240.00, 15, TRUE),
(4, 2, 'Dal Tadka', 'Yellow lentils tempered with cumin, garlic, and aromatic spices [Veg]', 180.00, 12, TRUE),
(5, 2, 'Dal Makhani', 'Slow-cooked black lentils finished with cream and white butter [Veg]', 220.00, 15, TRUE),
(6, 2, 'Veg Kolhapuri', 'Assorted vegetables cooked in fiery Kolhapuri red masala [Veg]', 240.00, 18, TRUE),
(7, 2, 'Chole Masala', 'Tender chickpeas simmered in authentic Amritsari spices [Veg]', 190.00, 15, TRUE),
(8, 2, 'Aloo Gobi', 'Homestyle potatoes and cauliflower tossed with turmeric and ginger [Veg]', 180.00, 12, TRUE),
(9, 2, 'Butter Chicken', 'Charcoal roasted chicken in velvety tomato cream gravy [Non-Veg]', 340.00, 20, TRUE),
(10, 2, 'Chicken Curry', 'Home-style tender chicken curry with traditional spices [Non-Veg]', 300.00, 18, TRUE),
(11, 2, 'Chicken Handi', 'Slow-cooked chicken cooked in an earthen pot with curd [Non-Veg]', 360.00, 22, TRUE),
(12, 1, 'Chicken Tikka', 'Char-grilled boneless chicken chunks marinated in yogurt & spices [Non-Veg]', 320.00, 15, TRUE),
(13, 3, 'Chicken Biryani', 'Layered fragrant basmati rice and marinated spiced chicken [Non-Veg]', 280.00, 20, TRUE),
(14, 3, 'Veg Pulao', 'Mildly spiced basmati rice tossed with garden fresh vegetables [Veg]', 180.00, 12, TRUE),
(15, 3, 'Jeera Rice', 'Fluffy basmati rice tempered with roasted cumin and ghee [Veg]', 150.00, 10, TRUE),
(16, 2, 'Mutton Rogan Josh', 'Kashmiri style braised lamb chunks in aromatic red gravy [Non-Veg]', 420.00, 25, TRUE),
(17, 2, 'Mutton Keema', 'Minced mutton cooked with green peas and caramelized onions [Non-Veg]', 380.00, 20, TRUE),
(18, 2, 'Fish Curry', 'Fresh coastal fish simmered in tangy kokum and coconut broth [Non-Veg]', 350.00, 18, TRUE),
(19, 4, 'Butter Naan', 'Tandoor-baked leavened flatbread brushed with golden butter [Veg]', 55.00, 5, TRUE),
(20, 4, 'Garlic Naan', 'Crispy clay oven bread topped with crushed garlic and coriander [Veg]', 70.00, 5, TRUE),
(21, 4, 'Tandoori Roti', 'Whole wheat bread baked crisp in traditional clay tandoor [Veg]', 30.00, 5, TRUE),
(22, 5, 'Veg Manchurian', 'Crispy vegetable dumplings tossed in tangy soya-garlic sauce [Veg]', 210.00, 15, TRUE),
(23, 5, 'Hakka Noodles', 'Wok-tossed noodles with crunchy bell peppers and spring onion [Veg]', 190.00, 12, TRUE),
(24, 6, 'Masala Dosa', 'Crispy fermented rice crepe stuffed with spiced potato mash [Veg]', 140.00, 10, TRUE),
(25, 6, 'Idli Sambar', 'Steamed soft rice cakes served with aromatic lentil sambar [Veg]', 100.00, 8, TRUE),
(26, 7, 'Pav Bhaji', 'Spiced mashed vegetable curry served with warm buttered pav [Veg]', 160.00, 12, TRUE),
(27, 7, 'Misal Pav', 'Spicy sprout curry topped with farsan, onions, and lemon [Veg]', 120.00, 10, TRUE),
(28, 8, 'Gulab Jamun', 'Warm golden milk dough dumplings soaked in saffron sugar syrup [Veg]', 90.00, 5, TRUE),
(29, 8, 'Rasmalai', 'Soft cottage cheese patties immersed in chilled cardamom milk [Veg]', 120.00, 5, TRUE),
(30, 8, 'Mango Lassi', 'Thick chilled yogurt beverage blended with sweet Alphonso mango [Veg]', 100.00, 5, TRUE),
(31, 8, 'Masala Chai', 'Freshly brewed Assam tea infused with crushed ginger and cardamom [Veg]', 40.00, 5, TRUE),
(32, 5, 'Veg Hakka Fried Rice', 'Wok-fried basmati rice tossed with fresh greens and soy sauce [Veg]', 200.00, 12, TRUE)
ON DUPLICATE KEY UPDATE price = VALUES(price), is_available = VALUES(is_available);

-- 8. Recipe Ingredients (Mapped for all 32 dishes)
INSERT INTO menu_item_ingredients (menu_item_id, inventory_id, quantity_required, unit) VALUES
-- D001: Paneer Butter Masala
(1, 1, 0.25, 'kg'), (1, 5, 0.05, 'kg'), (1, 7, 0.20, 'kg'), (1, 8, 0.15, 'kg'), (1, 6, 0.05, 'L'), (1, 16, 0.01, 'kg'),
-- D002: Kadai Paneer
(2, 1, 0.25, 'kg'), (2, 10, 0.15, 'kg'), (2, 8, 0.15, 'kg'), (2, 7, 0.15, 'kg'), (2, 18, 0.03, 'L'), (2, 16, 0.01, 'kg'),
-- D003: Palak Paneer
(3, 1, 0.25, 'kg'), (3, 11, 0.30, 'kg'), (3, 8, 0.10, 'kg'), (3, 7, 0.10, 'kg'), (3, 6, 0.03, 'L'), (3, 13, 0.01, 'kg'),
-- D004: Dal Tadka
(4, 7, 0.15, 'kg'), (4, 8, 0.15, 'kg'), (4, 5, 0.03, 'kg'), (4, 13, 0.01, 'kg'), (4, 17, 0.005, 'kg'), (4, 14, 0.01, 'kg'),
-- D005: Dal Makhani
(5, 5, 0.08, 'kg'), (5, 6, 0.05, 'L'), (5, 7, 0.20, 'kg'), (5, 8, 0.10, 'kg'), (5, 16, 0.01, 'kg'),
-- D006: Veg Kolhapuri
(6, 10, 0.10, 'kg'), (6, 9, 0.15, 'kg'), (6, 7, 0.15, 'kg'), (6, 8, 0.15, 'kg'), (6, 14, 0.02, 'kg'), (6, 16, 0.01, 'kg'),
-- D007: Chole Masala
(7, 8, 0.20, 'kg'), (7, 7, 0.20, 'kg'), (7, 12, 0.02, 'kg'), (7, 13, 0.02, 'kg'), (7, 16, 0.01, 'kg'), (7, 18, 0.03, 'L'),
-- D008: Aloo Gobi
(8, 9, 0.30, 'kg'), (8, 8, 0.15, 'kg'), (8, 7, 0.15, 'kg'), (8, 17, 0.01, 'kg'), (8, 18, 0.03, 'L'), (8, 15, 0.02, 'kg'),
-- D009: Butter Chicken
(9, 2, 0.35, 'kg'), (9, 5, 0.06, 'kg'), (9, 7, 0.25, 'kg'), (9, 6, 0.05, 'L'), (9, 16, 0.01, 'kg'), (9, 12, 0.01, 'kg'),
-- D010: Chicken Curry
(10, 2, 0.35, 'kg'), (10, 8, 0.20, 'kg'), (10, 7, 0.15, 'kg'), (10, 13, 0.02, 'kg'), (10, 12, 0.02, 'kg'), (10, 18, 0.04, 'L'),
-- D011: Chicken Handi
(11, 2, 0.40, 'kg'), (11, 20, 0.10, 'kg'), (11, 8, 0.20, 'kg'), (11, 5, 0.04, 'kg'), (11, 16, 0.01, 'kg'),
-- D012: Chicken Tikka
(12, 2, 0.35, 'kg'), (12, 20, 0.10, 'kg'), (12, 12, 0.02, 'kg'), (12, 13, 0.02, 'kg'), (12, 16, 0.01, 'kg'), (12, 5, 0.03, 'kg'),
-- D013: Chicken Biryani
(13, 2, 0.35, 'kg'), (13, 4, 0.30, 'kg'), (13, 8, 0.20, 'kg'), (13, 20, 0.08, 'kg'), (13, 16, 0.01, 'kg'), (13, 18, 0.03, 'L'),
-- D014: Veg Pulao
(14, 4, 0.25, 'kg'), (14, 10, 0.08, 'kg'), (14, 9, 0.10, 'kg'), (14, 8, 0.10, 'kg'), (14, 18, 0.03, 'L'),
-- D015: Jeera Rice
(15, 4, 0.25, 'kg'), (15, 5, 0.03, 'kg'), (15, 15, 0.02, 'kg'),
-- D016: Mutton Rogan Josh
(16, 3, 0.35, 'kg'), (16, 20, 0.10, 'kg'), (16, 8, 0.20, 'kg'), (16, 12, 0.02, 'kg'), (16, 13, 0.02, 'kg'), (16, 16, 0.015, 'kg'),
-- D017: Mutton Keema
(17, 3, 0.30, 'kg'), (17, 8, 0.15, 'kg'), (17, 7, 0.15, 'kg'), (17, 14, 0.02, 'kg'), (17, 18, 0.04, 'L'),
-- D018: Fish Curry
(18, 8, 0.15, 'kg'), (18, 7, 0.20, 'kg'), (18, 12, 0.02, 'kg'), (18, 13, 0.02, 'kg'), (18, 17, 0.01, 'kg'), (18, 18, 0.04, 'L'),
-- D019: Butter Naan
(19, 19, 0.15, 'kg'), (19, 5, 0.03, 'kg'), (19, 20, 0.03, 'kg'),
-- D020: Garlic Naan
(20, 19, 0.15, 'kg'), (20, 13, 0.03, 'kg'), (20, 5, 0.03, 'kg'), (20, 15, 0.01, 'kg'),
-- D021: Tandoori Roti
(21, 19, 0.12, 'kg'),
-- D022: Veg Manchurian
(22, 10, 0.10, 'kg'), (22, 8, 0.10, 'kg'), (22, 13, 0.02, 'kg'), (22, 12, 0.02, 'kg'), (22, 18, 0.04, 'L'),
-- D023: Hakka Noodles
(23, 10, 0.10, 'kg'), (23, 8, 0.10, 'kg'), (23, 13, 0.02, 'kg'), (23, 18, 0.03, 'L'),
-- D024: Masala Dosa
(24, 9, 0.18, 'kg'), (24, 8, 0.08, 'kg'), (24, 5, 0.02, 'kg'), (24, 14, 0.01, 'kg'),
-- D025: Idli Sambar
(25, 7, 0.10, 'kg'), (25, 8, 0.08, 'kg'), (25, 17, 0.005, 'kg'), (25, 15, 0.01, 'kg'),
-- D026: Pav Bhaji
(26, 9, 0.20, 'kg'), (26, 7, 0.15, 'kg'), (26, 8, 0.15, 'kg'), (26, 10, 0.08, 'kg'), (26, 5, 0.05, 'kg'),
-- D027: Misal Pav
(27, 8, 0.15, 'kg'), (27, 7, 0.10, 'kg'), (27, 12, 0.01, 'kg'), (27, 13, 0.01, 'kg'), (27, 18, 0.03, 'L'),
-- D028: Gulab Jamun
(28, 1, 0.10, 'kg'), (28, 19, 0.05, 'kg'), (28, 5, 0.03, 'kg'),
-- D029: Rasmalai
(29, 1, 0.15, 'kg'), (29, 6, 0.05, 'L'),
-- D030: Mango Lassi
(30, 20, 0.20, 'kg'), (30, 6, 0.03, 'L'),
-- D031: Masala Chai
(31, 6, 0.05, 'L'), (31, 12, 0.01, 'kg'),
-- D032: Veg Hakka Fried Rice
(32, 4, 0.25, 'kg'), (32, 10, 0.08, 'kg'), (32, 8, 0.08, 'kg'), (32, 13, 0.02, 'kg'), (32, 18, 0.03, 'L')
ON DUPLICATE KEY UPDATE quantity_required = VALUES(quantity_required);

-- 9. Customers (25 records)
INSERT INTO customers (customer_id, name, phone, email, address, loyalty_points, total_visits, total_spent) VALUES
(1, 'Aarav Kulkarni', '9000000001', 'aarav.k@example.com', 'Model Colony, Pune', 240, 12, 5400.00),
(2, 'Ananya Deshmukh', '9000000002', 'ananya.d@example.com', 'Kothrud, Pune', 160, 8, 3600.00),
(3, 'Rohan Patil', '9000000003', 'rohan.p@example.com', 'Baner Road, Pune', 300, 15, 6800.00),
(4, 'Isha Joshi', '9000000004', 'isha.j@example.com', 'Aundh, Pune', 120, 6, 2700.00),
(5, 'Siddharth Shah', '9000000005', 'siddharth.s@example.com', 'Koregaon Park, Pune', 400, 20, 9200.00),
(6, 'Meera Nair', '9000000006', 'meera.n@example.com', 'Kalyani Nagar, Pune', 180, 9, 4100.00),
(7, 'Aditya More', '9000000007', 'aditya.m@example.com', 'Viman Nagar, Pune', 80, 4, 1850.00),
(8, 'Kavya Rao', '9000000008', 'kavya.r@example.com', 'Magarpatta City, Pune', 220, 11, 4900.00),
(9, 'Omkar Shinde', '9000000009', 'omkar.s@example.com', 'Shivaji Nagar, Pune', 60, 3, 1400.00),
(10, 'Sneha Kulkarni', '9000000010', 'sneha.k@example.com', 'Erandwane, Pune', 140, 7, 3200.00),
(11, 'Vedant Joshi', '9000000011', 'vedant.j@example.com', 'Prabhat Road, Pune', 280, 14, 6300.00),
(12, 'Pooja Desai', '9000000012', 'pooja.d@example.com', 'Bavdhan, Pune', 100, 5, 2300.00),
(13, 'Atharva Pawar', '9000000013', 'atharva.p@example.com', 'Wakad, Pune', 40, 2, 950.00),
(14, 'Sanika Patil', '9000000014', 'sanika.p@example.com', 'Hinjawadi Phase 1, Pune', 200, 10, 4600.00),
(15, 'Rahul Mehta', '9000000015', 'rahul.m@example.com', 'Camp Cantonment, Pune', 360, 18, 8100.00),
(16, 'Neha Sharma', '9000000016', 'neha.s@example.com', 'Khadki, Pune', 120, 6, 2750.00),
(17, 'Kunal Bhosale', '9000000017', 'kunal.b@example.com', 'Pimple Saudagar, Pune', 260, 13, 5900.00),
(18, 'Riya Agarwal', '9000000018', 'riya.a@example.com', 'FC Road, Pune', 80, 4, 1800.00),
(19, 'Yash Chavan', '9000000019', 'yash.c@example.com', 'Karve Nagar, Pune', 160, 8, 3700.00),
(20, 'Tanvi Kulkarni', '9000000020', 'tanvi.k@example.com', 'Deccan Gymkhana, Pune', 320, 16, 7400.00),
(21, 'Akash Verma', '9000000021', 'akash.v@example.com', 'Sinhagad Road, Pune', 20, 1, 500.00),
(22, 'Priya Nair', '9000000022', 'priya.n@example.com', 'Senapati Bapat Road, Pune', 140, 7, 3150.00),
(23, 'Saurabh Jadhav', '9000000023', 'saurabh.j@example.com', 'Hadapsar, Pune', 100, 5, 2250.00),
(24, 'Aniket Joshi', '9000000024', 'aniket.j@example.com', 'Bibwewadi, Pune', 180, 9, 4050.00),
(25, 'Shruti Shah', '9000000025', 'shruti.s@example.com', 'Salunke Vihar, Pune', 240, 12, 5500.00)
ON DUPLICATE KEY UPDATE name = VALUES(name), email = VALUES(email), loyalty_points = VALUES(loyalty_points), total_visits = VALUES(total_visits);
