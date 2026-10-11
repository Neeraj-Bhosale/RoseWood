package com.restaurant.service;

import com.restaurant.data.DataStructureManager;
import com.restaurant.data.SearchSortUtil;
import com.restaurant.exception.MenuItemNotAvailableException;
import com.restaurant.model.MenuCategory;
import com.restaurant.model.MenuItem;
import com.restaurant.repository.MenuRepository;
import com.restaurant.util.ValidationUtil;

import java.util.ArrayList;
import java.util.List;

public class MenuService {
    private final MenuRepository menuRepository;

    public MenuService() {
        this.menuRepository = new MenuRepository();
    }

    public MenuService(MenuRepository menuRepository) {
        this.menuRepository = menuRepository;
    }

    public List<MenuCategory> getAllCategories() {
        return menuRepository.findAllCategories();
    }

    public List<MenuItem> getAllMenuItems() {
        List<MenuItem> items = menuRepository.findAllItems();
        DataStructureManager.getInstance().cacheMenuItems(items);
        return items;
    }

    public MenuItem getMenuItemById(int itemId) {
        MenuItem cached = DataStructureManager.getInstance().getMenuItemById(itemId);
        if (cached != null) return cached;
        return menuRepository.findItemById(itemId);
    }

    public void verifyItemAvailable(int itemId) throws MenuItemNotAvailableException {
        MenuItem item = getMenuItemById(itemId);
        if (item == null) {
            throw new MenuItemNotAvailableException("Menu item #" + itemId + " was not found.");
        }
        if (!item.isAvailable()) {
            throw new MenuItemNotAvailableException("Menu item '" + item.getName() + "' is currently marked as OUT OF STOCK.");
        }
    }

    public boolean addMenuItem(int categoryId, String name, String description, double price, int prepTime, boolean isAvailable) {
        if (!ValidationUtil.isNotEmpty(name)) {
            throw new IllegalArgumentException("Menu item name is required.");
        }
        if (!ValidationUtil.isValidPrice(price)) {
            throw new IllegalArgumentException("Price cannot be negative.");
        }
        MenuItem item = new MenuItem(0, categoryId, name.trim(), description, price, prepTime, isAvailable);
        return menuRepository.createItem(item);
    }

    public boolean updateMenuItem(MenuItem item) {
        if (item == null || item.getItemId() <= 0) return false;
        if (!ValidationUtil.isValidPrice(item.getPrice())) {
            throw new IllegalArgumentException("Price cannot be negative.");
        }
        return menuRepository.updateItem(item);
    }

    public boolean deleteMenuItem(int itemId) {
        return menuRepository.deleteItem(itemId);
    }

    public List<MenuItem> searchAndFilter(String query, Integer categoryId, boolean sortPriceAsc) {
        List<MenuItem> all = getAllMenuItems();
        List<MenuItem> filtered = new ArrayList<>();

        for (MenuItem item : all) {
            boolean matchesCat = (categoryId == null || categoryId == 0 || item.getCategoryId() == categoryId);
            boolean matchesQuery = (query == null || query.trim().isEmpty() ||
                    item.getName().toLowerCase().contains(query.toLowerCase().trim()) ||
                    (item.getDescription() != null && item.getDescription().toLowerCase().contains(query.toLowerCase().trim())));

            if (matchesCat && matchesQuery) {
                filtered.add(item);
            }
        }

        if (sortPriceAsc) {
            SearchSortUtil.quickSortMenuItemsByPrice(filtered);
        }

        return filtered;
    }
}
