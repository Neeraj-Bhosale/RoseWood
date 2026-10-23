package com.restaurant.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.restaurant.config.DatabaseConfig;
import com.restaurant.model.Customer;
import com.restaurant.model.InventoryItem;
import com.restaurant.model.MenuItemIngredient;
import com.restaurant.model.Supplier;
import com.restaurant.repository.CustomerRepository;
import com.restaurant.repository.InventoryRepository;
import com.restaurant.repository.MenuItemIngredientRepository;
import com.restaurant.repository.MenuRepository;
import com.restaurant.repository.SupplierRepository;
import com.restaurant.repository.TableRepository;
import com.restaurant.util.ActivityLogger;

/**
 * Service to manage realistic dummy sample data and clean slate / vacant states.
 * Populates 7 Suppliers, 20 Ingredients, 32 Dishes across 8 Categories with full recipes,
 * and 25 Customers.
 */
public class DummyDataService {
    private static final Logger LOGGER = Logger.getLogger(DummyDataService.class.getName());

    private final SupplierRepository supplierRepository = new SupplierRepository();
    private final InventoryRepository inventoryRepository = new InventoryRepository();
    private final MenuRepository menuRepository = new MenuRepository();
    private final MenuItemIngredientRepository recipeRepository = new MenuItemIngredientRepository();
    private final CustomerRepository customerRepository = new CustomerRepository();
    private final TableRepository tableRepository = new TableRepository();

    // -----------------------------------------------------------------------
    // Entity Counts
    // -----------------------------------------------------------------------

    public int getSupplierCount() {
        return supplierRepository.findAll().size();
    }

    public int getInventoryCount() {
        return inventoryRepository.findAll().size();
    }

    public int getDishesCount() {
        return menuRepository.findAllItems().size();
    }

    public int getCustomerCount() {
        return customerRepository.findAll().size();
    }

    public int getTableCount() {
        return tableRepository.findAll().size();
    }

    public boolean isVacant() {
        return getSupplierCount() == 0 && getInventoryCount() == 0 &&
               getDishesCount() == 0 && getCustomerCount() == 0;
    }

    // -----------------------------------------------------------------------
    // 1. Suppliers: 7 Records
    // -----------------------------------------------------------------------

    public int importDummySuppliers() {
        Supplier[] dummySuppliers = {
            new Supplier(1, "Shree Dairy Farm", "Mahesh Pawar", "9876543210", "shreedairy@example.com", "Pune / Baramati Belt", "Dairy products"),
            new Supplier(2, "FreshFields Produce", "Rahul Jadhav", "9876543211", "freshfields@example.com", "APMC Market Yard", "Fruits & vegetables"),
            new Supplier(3, "Deccan Poultry Suppliers", "Suresh Patil", "9876543212", "deccanpoultry@example.com", "Bhosari Industrial Area", "Poultry & eggs"),
            new Supplier(4, "Grain House Traders", "Amit Shah", "9876543213", "grainhouse@example.com", "Gultekdi Market Yard", "Grains & rice"),
            new Supplier(5, "Spice Route Traders", "Nitin Kulkarni", "9876543214", "spiceroute@example.com", "Nana Peth Wholesale Market", "Spices & masalas"),
            new Supplier(6, "Coastal Foods Supply", "Imran Shaikh", "9876543215", "coastalfoods@example.com", "Dockline Cold Storage", "Meat & seafood"),
            new Supplier(7, "GreenLeaf Organics", "Prasad More", "9876543216", "greenleaf@example.com", "Saswad Organic Farm Valley", "Organic vegetables")
        };

        int count = 0;
        for (Supplier s : dummySuppliers) {
            Supplier existing = supplierRepository.findByName(s.getName());
            if (existing == null) {
                if (insertSupplierWithId(s)) {
                    count++;
                } else if (supplierRepository.create(s)) {
                    count++;
                }
            }
        }
        ActivityLogger.getInstance().logInfo("Imported " + count + " suppliers.");
        return count;
    }

    private boolean insertSupplierWithId(Supplier s) {
        String sql = "INSERT INTO suppliers (supplier_id, name, contact_person, phone, email, address, supplied_category) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?) ON DUPLICATE KEY UPDATE name=VALUES(name), contact_person=VALUES(contact_person), phone=VALUES(phone)";
        try (Connection conn = DatabaseConfig.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, s.getSupplierId());
            stmt.setString(2, s.getName());
            stmt.setString(3, s.getContactPerson());
            stmt.setString(4, s.getPhone());
            stmt.setString(5, s.getEmail());
            stmt.setString(6, s.getAddress());
            stmt.setString(7, s.getSuppliedCategory());
            return stmt.executeUpdate() > 0;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean vacateSuppliers() {
        try (Connection conn = DatabaseConfig.getInstance().getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute("SET FOREIGN_KEY_CHECKS = 0;");
            stmt.execute("UPDATE inventory SET supplier_id = NULL;");
            stmt.execute("DELETE FROM suppliers;");
            stmt.execute("SET FOREIGN_KEY_CHECKS = 1;");
            ActivityLogger.getInstance().logInfo("All suppliers vacated.");
            return true;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to vacate suppliers", e);
            return false;
        }
    }

    // -----------------------------------------------------------------------
    // 2. Inventory: 20 Ingredients
    // -----------------------------------------------------------------------

    public int importDummyInventory() {
        if (getSupplierCount() == 0) {
            importDummySuppliers();
        }

        Object[][] dummyInventory = {
            // ID, Name, Category, Current Stock, Unit, Reorder Level, Cost Per Unit, Supplier ID
            {1, "Paneer", "Dairy", 18.00, "kg", 5.00, 320.00, 1},
            {2, "Chicken", "Meat", 25.00, "kg", 8.00, 240.00, 3},
            {3, "Mutton", "Meat", 12.00, "kg", 4.00, 680.00, 6},
            {4, "Basmati Rice", "Grains", 40.00, "kg", 10.00, 110.00, 4},
            {5, "Butter", "Dairy", 8.00, "kg", 3.00, 480.00, 1},
            {6, "Fresh Cream", "Dairy", 10.00, "L", 3.00, 220.00, 1},
            {7, "Tomatoes", "Vegetables", 22.00, "kg", 8.00, 35.00, 2},
            {8, "Onions", "Vegetables", 30.00, "kg", 10.00, 30.00, 2},
            {9, "Potatoes", "Vegetables", 25.00, "kg", 8.00, 28.00, 2},
            {10, "Capsicum", "Vegetables", 8.00, "kg", 3.00, 70.00, 2},
            {11, "Spinach", "Vegetables", 7.00, "kg", 2.00, 40.00, 7},
            {12, "Ginger", "Spices", 4.00, "kg", 1.00, 120.00, 2},
            {13, "Garlic", "Spices", 5.00, "kg", 2.00, 160.00, 2},
            {14, "Green Chilies", "Vegetables", 3.00, "kg", 1.00, 80.00, 2},
            {15, "Coriander Leaves", "Vegetables", 4.00, "kg", 1.00, 60.00, 2},
            {16, "Garam Masala", "Spices", 3.00, "kg", 1.00, 420.00, 5},
            {17, "Turmeric Powder", "Spices", 4.00, "kg", 1.00, 240.00, 5},
            {18, "Cooking Oil", "Oils", 35.00, "L", 10.00, 150.00, 4},
            {19, "Wheat Flour", "Flour", 28.00, "kg", 10.00, 42.00, 4},
            {20, "Curd", "Dairy", 12.00, "kg", 4.00, 80.00, 1}
        };

        int count = 0;
        for (Object[] row : dummyInventory) {
            int id = (int) row[0];
            String name = (String) row[1];
            String cat = (String) row[2];
            double qty = (double) row[3];
            String unit = (String) row[4];
            double min = (double) row[5];
            double cost = (double) row[6];
            Integer supId = (Integer) row[7];

            InventoryItem existing = inventoryRepository.findByName(name);
            if (existing == null) {
                if (insertInventoryWithId(id, name, cat, qty, unit, min, cost, supId)) {
                    count++;
                }
            }
        }
        ActivityLogger.getInstance().logInfo("Imported " + count + " inventory items.");
        return count;
    }

    private boolean insertInventoryWithId(int id, String name, String category, double qty, String unit, double min, double cost, Integer supId) {
        String sql = "INSERT INTO inventory (inventory_id, ingredient_name, category, quantity, unit, minimum_stock, unit_cost, supplier_id) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?) " +
                     "ON DUPLICATE KEY UPDATE quantity=VALUES(quantity), minimum_stock=VALUES(minimum_stock), unit_cost=VALUES(unit_cost)";
        try (Connection conn = DatabaseConfig.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.setString(2, name);
            stmt.setString(3, category);
            stmt.setDouble(4, qty);
            stmt.setString(5, unit);
            stmt.setDouble(6, min);
            stmt.setDouble(7, cost);
            if (supId != null) stmt.setInt(8, supId);
            else stmt.setNull(8, java.sql.Types.INTEGER);
            return stmt.executeUpdate() > 0;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean vacateInventory() {
        try (Connection conn = DatabaseConfig.getInstance().getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute("SET FOREIGN_KEY_CHECKS = 0;");
            stmt.execute("DELETE FROM menu_item_ingredients;");
            stmt.execute("DELETE FROM inventory_logs;");
            stmt.execute("DELETE FROM purchase_items;");
            stmt.execute("DELETE FROM inventory;");
            stmt.execute("SET FOREIGN_KEY_CHECKS = 1;");
            ActivityLogger.getInstance().logInfo("All inventory and linked recipes vacated.");
            return true;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to vacate inventory", e);
            return false;
        }
    }

    // -----------------------------------------------------------------------
    // 3. Menu Categories & 32 Dishes with Recipes
    // -----------------------------------------------------------------------

    public int importDummyDishes() {
        if (getInventoryCount() == 0) {
            importDummyInventory();
        }

        // Categories (8 Main categories as specified)
        Object[][] categories = {
            {1, "Starters", "Crispy, spiced appetizers and finger foods", 1},
            {2, "Main Course", "Traditional Indian gravies and curries", 2},
            {3, "Rice & Biryani", "Aromatic Basmati rice specialties and layered biryanis", 3},
            {4, "Indian Breads", "Clay oven tandoori rotis, butter naans, and breads", 4},
            {5, "Chinese", "Wok-tossed noodles, fried rice, and Indo-Chinese favorites", 5},
            {6, "South Indian", "Crispy dosas, steamed idlis, and sambar", 6},
            {7, "Street Food", "Mumbai-style pav bhaji, spicy misal pav, and chaats", 7},
            {8, "Desserts & Beverages", "Sweet indulgences, traditional lassi, and hot masala chai", 8}
        };

        for (Object[] cat : categories) {
            insertCategoryWithId((int) cat[0], (String) cat[1], (String) cat[2], (int) cat[3]);
        }

        // 32 Dishes
        Object[][] dishes = {
            // ID, CategoryId, Name, Description, Price, PrepTime, Available
            {1, 2, "Paneer Butter Masala", "Cottage cheese cubes cooked in rich buttery tomato sauce [Veg]", 280.00, 15, true},
            {2, 2, "Kadai Paneer", "Paneer tossed with bell peppers and freshly ground kadai spices [Veg]", 260.00, 15, true},
            {3, 2, "Palak Paneer", "Fresh cottage cheese simmered in smooth spiced spinach puree [Veg]", 240.00, 15, true},
            {4, 2, "Dal Tadka", "Yellow lentils tempered with cumin, garlic, and aromatic spices [Veg]", 180.00, 12, true},
            {5, 2, "Dal Makhani", "Slow-cooked black lentils finished with cream and white butter [Veg]", 220.00, 15, true},
            {6, 2, "Veg Kolhapuri", "Assorted vegetables cooked in fiery Kolhapuri red masala [Veg]", 240.00, 18, true},
            {7, 2, "Chole Masala", "Tender chickpeas simmered in authentic Amritsari spices [Veg]", 190.00, 15, true},
            {8, 2, "Aloo Gobi", "Homestyle potatoes and cauliflower tossed with turmeric and ginger [Veg]", 180.00, 12, true},
            {9, 2, "Butter Chicken", "Charcoal roasted chicken in velvety tomato cream gravy [Non-Veg]", 340.00, 20, true},
            {10, 2, "Chicken Curry", "Home-style tender chicken curry with traditional spices [Non-Veg]", 300.00, 18, true},
            {11, 2, "Chicken Handi", "Slow-cooked chicken cooked in an earthen pot with curd [Non-Veg]", 360.00, 22, true},
            {12, 1, "Chicken Tikka", "Char-grilled boneless chicken chunks marinated in yogurt & spices [Non-Veg]", 320.00, 15, true},
            {13, 3, "Chicken Biryani", "Layered fragrant basmati rice and marinated spiced chicken [Non-Veg]", 280.00, 20, true},
            {14, 3, "Veg Pulao", "Mildly spiced basmati rice tossed with garden fresh vegetables [Veg]", 180.00, 12, true},
            {15, 3, "Jeera Rice", "Fluffy basmati rice tempered with roasted cumin and ghee [Veg]", 150.00, 10, true},
            {16, 2, "Mutton Rogan Josh", "Kashmiri style braised lamb chunks in aromatic red gravy [Non-Veg]", 420.00, 25, true},
            {17, 2, "Mutton Keema", "Minced mutton cooked with green peas and caramelized onions [Non-Veg]", 380.00, 20, true},
            {18, 2, "Fish Curry", "Fresh coastal fish simmered in tangy kokum and coconut broth [Non-Veg]", 350.00, 18, true},
            {19, 4, "Butter Naan", "Tandoor-baked leavened flatbread brushed with golden butter [Veg]", 55.00, 5, true},
            {20, 4, "Garlic Naan", "Crispy clay oven bread topped with crushed garlic and coriander [Veg]", 70.00, 5, true},
            {21, 4, "Tandoori Roti", "Whole wheat bread baked crisp in traditional clay tandoor [Veg]", 30.00, 5, true},
            {22, 5, "Veg Manchurian", "Crispy vegetable dumplings tossed in tangy soya-garlic sauce [Veg]", 210.00, 15, true},
            {23, 5, "Hakka Noodles", "Wok-tossed noodles with crunchy bell peppers and spring onion [Veg]", 190.00, 12, true},
            {24, 6, "Masala Dosa", "Crispy fermented rice crepe stuffed with spiced potato mash [Veg]", 140.00, 10, true},
            {25, 6, "Idli Sambar", "Steamed soft rice cakes served with aromatic lentil sambar [Veg]", 100.00, 8, true},
            {26, 7, "Pav Bhaji", "Spiced mashed vegetable curry served with warm buttered pav [Veg]", 160.00, 12, true},
            {27, 7, "Misal Pav", "Spicy sprout curry topped with farsan, onions, and lemon [Veg]", 120.00, 10, true},
            {28, 8, "Gulab Jamun", "Warm golden milk dough dumplings soaked in saffron sugar syrup [Veg]", 90.00, 5, true},
            {29, 8, "Rasmalai", "Soft cottage cheese patties immersed in chilled cardamom milk [Veg]", 120.00, 5, true},
            {30, 8, "Mango Lassi", "Thick chilled yogurt beverage blended with sweet Alphonso mango [Veg]", 100.00, 5, true},
            {31, 8, "Masala Chai", "Freshly brewed Assam tea infused with crushed ginger and cardamom [Veg]", 40.00, 5, true},
            {32, 5, "Veg Hakka Fried Rice", "Wok-fried basmati rice tossed with fresh greens and soy sauce [Veg]", 200.00, 12, true}
        };

        int dishCount = 0;
        for (Object[] d : dishes) {
            int id = (int) d[0];
            int catId = (int) d[1];
            String name = (String) d[2];
            String desc = (String) d[3];
            double price = (double) d[4];
            int prep = (int) d[5];
            boolean avail = (boolean) d[6];

            if (insertDishWithId(id, catId, name, desc, price, prep, avail)) {
                dishCount++;
            }
        }

        // 3. Import complete Recipes into `menu_item_ingredients`
        importAllDishRecipes();

        ActivityLogger.getInstance().logInfo("Imported " + dishCount + " dishes with complete ingredient recipes.");
        return dishCount;
    }

    private void importAllDishRecipes() {
        Map<Integer, List<MenuItemIngredient>> recipes = MenuRecipeService.getDefaultRecipes();
        for (Map.Entry<Integer, List<MenuItemIngredient>> entry : recipes.entrySet()) {
            int dishId = entry.getKey();
            for (MenuItemIngredient ing : entry.getValue()) {
                insertRecipeRow(dishId, ing.getInventoryId(), ing.getQuantityRequired(), ing.getUnit());
            }
        }
    }

    private void insertRecipeRow(int dishId, int inventoryId, double qty, String unit) {
        String sql = "INSERT INTO menu_item_ingredients (menu_item_id, inventory_id, quantity_required, unit) " +
                     "VALUES (?, ?, ?, ?) ON DUPLICATE KEY UPDATE quantity_required=VALUES(quantity_required), unit=VALUES(unit)";
        try (Connection conn = DatabaseConfig.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, dishId);
            stmt.setInt(2, inventoryId);
            stmt.setDouble(3, qty);
            stmt.setString(4, unit != null ? unit : "kg");
            stmt.executeUpdate();
        } catch (Exception ignored) {}
    }

    private boolean insertCategoryWithId(int id, String name, String desc, int order) {
        String sql = "INSERT INTO menu_categories (category_id, name, description, display_order) " +
                     "VALUES (?, ?, ?, ?) ON DUPLICATE KEY UPDATE name=VALUES(name), description=VALUES(description)";
        try (Connection conn = DatabaseConfig.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.setString(2, name);
            stmt.setString(3, desc);
            stmt.setInt(4, order);
            return stmt.executeUpdate() > 0;
        } catch (Exception e) {
            return false;
        }
    }

    private boolean insertDishWithId(int id, int catId, String name, String desc, double price, int prep, boolean avail) {
        String sql = "INSERT INTO menu_items (item_id, category_id, name, description, price, preparation_time, is_available) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?) " +
                     "ON DUPLICATE KEY UPDATE category_id=VALUES(category_id), name=VALUES(name), description=VALUES(description), price=VALUES(price), is_available=VALUES(is_available)";
        try (Connection conn = DatabaseConfig.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.setInt(2, catId);
            stmt.setString(3, name);
            stmt.setString(4, desc);
            stmt.setDouble(5, price);
            stmt.setInt(6, prep);
            stmt.setBoolean(7, avail);
            return stmt.executeUpdate() > 0;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean vacateDishes() {
        try (Connection conn = DatabaseConfig.getInstance().getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute("SET FOREIGN_KEY_CHECKS = 0;");
            stmt.execute("DELETE FROM menu_item_ingredients;");
            stmt.execute("DELETE FROM order_items;");
            stmt.execute("DELETE FROM menu_items;");
            stmt.execute("DELETE FROM menu_categories;");
            stmt.execute("SET FOREIGN_KEY_CHECKS = 1;");
            ActivityLogger.getInstance().logInfo("All menu dishes, categories and recipes vacated.");
            return true;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to vacate dishes", e);
            return false;
        }
    }

    // -----------------------------------------------------------------------
    // 4. Customers: 25 Records
    // -----------------------------------------------------------------------

    public int importDummyCustomers() {
        Object[][] dummyCustomers = {
            {1, "Aarav Kulkarni", "9000000001", "aarav.k@example.com", "Model Colony, Pune", 240, 12, 5400.00},
            {2, "Ananya Deshmukh", "9000000002", "ananya.d@example.com", "Kothrud, Pune", 160, 8, 3600.00},
            {3, "Rohan Patil", "9000000003", "rohan.p@example.com", "Baner Road, Pune", 300, 15, 6800.00},
            {4, "Isha Joshi", "9000000004", "isha.j@example.com", "Aundh, Pune", 120, 6, 2700.00},
            {5, "Siddharth Shah", "9000000005", "siddharth.s@example.com", "Koregaon Park, Pune", 400, 20, 9200.00},
            {6, "Meera Nair", "9000000006", "meera.n@example.com", "Kalyani Nagar, Pune", 180, 9, 4100.00},
            {7, "Aditya More", "9000000007", "aditya.m@example.com", "Viman Nagar, Pune", 80, 4, 1850.00},
            {8, "Kavya Rao", "9000000008", "kavya.r@example.com", "Magarpatta City, Pune", 220, 11, 4900.00},
            {9, "Omkar Shinde", "9000000009", "omkar.s@example.com", "Shivaji Nagar, Pune", 60, 3, 1400.00},
            {10, "Sneha Kulkarni", "9000000010", "sneha.k@example.com", "Erandwane, Pune", 140, 7, 3200.00},
            {11, "Vedant Joshi", "9000000011", "vedant.j@example.com", "Prabhat Road, Pune", 280, 14, 6300.00},
            {12, "Pooja Desai", "9000000012", "pooja.d@example.com", "Bavdhan, Pune", 100, 5, 2300.00},
            {13, "Atharva Pawar", "9000000013", "atharva.p@example.com", "Wakad, Pune", 40, 2, 950.00},
            {14, "Sanika Patil", "9000000014", "sanika.p@example.com", "Hinjawadi Phase 1, Pune", 200, 10, 4600.00},
            {15, "Rahul Mehta", "9000000015", "rahul.m@example.com", "Camp Cantonment, Pune", 360, 18, 8100.00},
            {16, "Neha Sharma", "9000000016", "neha.s@example.com", "Khadki, Pune", 120, 6, 2750.00},
            {17, "Kunal Bhosale", "9000000017", "kunal.b@example.com", "Pimple Saudagar, Pune", 260, 13, 5900.00},
            {18, "Riya Agarwal", "9000000018", "riya.a@example.com", "FC Road, Pune", 80, 4, 1800.00},
            {19, "Yash Chavan", "9000000019", "yash.c@example.com", "Karve Nagar, Pune", 160, 8, 3700.00},
            {20, "Tanvi Kulkarni", "9000000020", "tanvi.k@example.com", "Deccan Gymkhana, Pune", 320, 16, 7400.00},
            {21, "Akash Verma", "9000000021", "akash.v@example.com", "Sinhagad Road, Pune", 20, 1, 500.00},
            {22, "Priya Nair", "9000000022", "priya.n@example.com", "Senapati Bapat Road, Pune", 140, 7, 3150.00},
            {23, "Saurabh Jadhav", "9000000023", "saurabh.j@example.com", "Hadapsar, Pune", 100, 5, 2250.00},
            {24, "Aniket Joshi", "9000000024", "aniket.j@example.com", "Bibwewadi, Pune", 180, 9, 4050.00},
            {25, "Shruti Shah", "9000000025", "shruti.s@example.com", "Salunke Vihar, Pune", 240, 12, 5500.00}
        };

        int count = 0;
        for (Object[] row : dummyCustomers) {
            int id = (int) row[0];
            String name = (String) row[1];
            String phone = (String) row[2];
            String email = (String) row[3];
            String address = (String) row[4];
            int points = (int) row[5];
            int visits = (int) row[6];
            double spent = (double) row[7];

            Customer existing = customerRepository.findByPhone(phone);
            if (existing == null) {
                if (insertCustomerWithId(id, name, phone, email, address, points, visits, spent)) {
                    count++;
                }
            }
        }
        ActivityLogger.getInstance().logInfo("Imported " + count + " customers.");
        return count;
    }

    private boolean insertCustomerWithId(int id, String name, String phone, String email, String address, int points, int visits, double spent) {
        String sql = "INSERT INTO customers (customer_id, name, phone, email, address, loyalty_points, total_visits, total_spent) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?) ON DUPLICATE KEY UPDATE name=VALUES(name), email=VALUES(email), loyalty_points=VALUES(loyalty_points), total_visits=VALUES(total_visits)";
        try (Connection conn = DatabaseConfig.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.setString(2, name);
            stmt.setString(3, phone);
            stmt.setString(4, email);
            stmt.setString(5, address);
            stmt.setInt(6, points);
            stmt.setInt(7, visits);
            stmt.setDouble(8, spent);
            return stmt.executeUpdate() > 0;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean vacateCustomers() {
        try (Connection conn = DatabaseConfig.getInstance().getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute("SET FOREIGN_KEY_CHECKS = 0;");
            stmt.execute("DELETE FROM reservations;");
            stmt.execute("UPDATE orders SET customer_id = NULL;");
            stmt.execute("DELETE FROM customers;");
            stmt.execute("SET FOREIGN_KEY_CHECKS = 1;");
            ActivityLogger.getInstance().logInfo("All customers vacated.");
            return true;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to vacate customers", e);
            return false;
        }
    }

    // -----------------------------------------------------------------------
    // 5. Tables: Standard Dining Tables
    // -----------------------------------------------------------------------

    public int importDummyTables() {
        Object[][] tables = {
            {1, "T-01", 2, "Window Bay A", "AVAILABLE"},
            {2, "T-02", 2, "Window Bay A", "AVAILABLE"},
            {3, "T-03", 4, "Central Hall", "AVAILABLE"},
            {4, "T-04", 4, "Central Hall", "AVAILABLE"},
            {5, "T-05", 4, "Central Hall", "AVAILABLE"},
            {6, "T-06", 6, "Family Section", "AVAILABLE"},
            {7, "T-07", 6, "Family Section", "AVAILABLE"},
            {8, "T-08", 8, "VIP Lounge", "AVAILABLE"},
            {9, "T-09", 10, "Private Dining Room", "AVAILABLE"},
            {10, "T-10", 4, "Outdoor Terrace", "AVAILABLE"}
        };

        int count = 0;
        for (Object[] t : tables) {
            String sql = "INSERT INTO restaurant_tables (table_id, table_number, capacity, location, status) " +
                         "VALUES (?, ?, ?, ?, ?) ON DUPLICATE KEY UPDATE capacity=VALUES(capacity)";
            try (Connection conn = DatabaseConfig.getInstance().getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, (int) t[0]);
                stmt.setString(2, (String) t[1]);
                stmt.setInt(3, (int) t[2]);
                stmt.setString(4, (String) t[3]);
                stmt.setString(5, (String) t[4]);
                if (stmt.executeUpdate() > 0) count++;
            } catch (Exception ignored) {}
        }
        return count;
    }

    // -----------------------------------------------------------------------
    // Master Actions: Import All / Vacate All
    // -----------------------------------------------------------------------

    public void importAllDummyData() {
        importDummyTables();
        importDummySuppliers();
        importDummyInventory();
        importDummyDishes();
        importDummyCustomers();
        ActivityLogger.getInstance().logInfo("All Indian restaurant dummy sample data imported successfully.");
    }

    public boolean vacateAllData() {
        try (Connection conn = DatabaseConfig.getInstance().getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute("SET FOREIGN_KEY_CHECKS = 0;");
            stmt.execute("DELETE FROM payments;");
            stmt.execute("DELETE FROM order_items;");
            stmt.execute("DELETE FROM orders;");
            stmt.execute("DELETE FROM reservations;");
            stmt.execute("DELETE FROM menu_item_ingredients;");
            stmt.execute("DELETE FROM menu_items;");
            stmt.execute("DELETE FROM menu_categories;");
            stmt.execute("DELETE FROM inventory_logs;");
            stmt.execute("DELETE FROM purchase_items;");
            stmt.execute("DELETE FROM purchases;");
            stmt.execute("DELETE FROM inventory;");
            stmt.execute("DELETE FROM suppliers;");
            stmt.execute("DELETE FROM customers;");
            stmt.execute("UPDATE restaurant_tables SET status = 'AVAILABLE';");
            stmt.execute("SET FOREIGN_KEY_CHECKS = 1;");
            ActivityLogger.getInstance().logInfo("System database vacated to clean slate (users & tables preserved).");
            return true;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to vacate all data", e);
            return false;
        }
    }
}
