package com.restaurant.data;

import com.restaurant.model.InventoryItem;
import com.restaurant.model.MenuItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Utility algorithms demonstrating Binary Search and QuickSort implementations.
 */
public class SearchSortUtil {

    /**
     * Binary search on a list of menu items sorted by price.
     * @return index of found item, or -1 if not found.
     */
    public static int binarySearchMenuByPrice(List<MenuItem> sortedList, double targetPrice) {
        if (sortedList == null || sortedList.isEmpty()) return -1;
        int low = 0;
        int high = sortedList.size() - 1;

        while (low <= high) {
            int mid = low + (high - low) / 2;
            double midVal = sortedList.get(mid).getPrice();

            if (Math.abs(midVal - targetPrice) < 0.001) {
                return mid;
            }
            if (midVal < targetPrice) {
                low = mid + 1;
            } else {
                high = mid - 1;
            }
        }
        return -1;
    }

    /**
     * In-place QuickSort algorithm for MenuItem list by price ascending.
     */
    public static void quickSortMenuItemsByPrice(List<MenuItem> items) {
        if (items == null || items.size() <= 1) return;
        quickSortMenuHelper(items, 0, items.size() - 1);
    }

    private static void quickSortMenuHelper(List<MenuItem> items, int low, int high) {
        if (low < high) {
            int pivotIndex = partitionMenu(items, low, high);
            quickSortMenuHelper(items, low, pivotIndex - 1);
            quickSortMenuHelper(items, pivotIndex + 1, high);
        }
    }

    private static int partitionMenu(List<MenuItem> items, int low, int high) {
        double pivot = items.get(high).getPrice();
        int i = low - 1;
        for (int j = low; j < high; j++) {
            if (items.get(j).getPrice() <= pivot) {
                i++;
                MenuItem temp = items.get(i);
                items.set(i, items.get(j));
                items.set(j, temp);
            }
        }
        MenuItem temp = items.get(i + 1);
        items.set(i + 1, items.get(high));
        items.set(high, temp);
        return i + 1;
    }

    /**
     * QuickSort for Inventory items by stock quantity ascending (to bring low-stock to top).
     */
    public static void quickSortInventoryByQuantity(List<InventoryItem> items) {
        if (items == null || items.size() <= 1) return;
        quickSortInventoryHelper(items, 0, items.size() - 1);
    }

    private static void quickSortInventoryHelper(List<InventoryItem> items, int low, int high) {
        if (low < high) {
            int pivotIndex = partitionInventory(items, low, high);
            quickSortInventoryHelper(items, low, pivotIndex - 1);
            quickSortInventoryHelper(items, pivotIndex + 1, high);
        }
    }

    private static int partitionInventory(List<InventoryItem> items, int low, int high) {
        double pivot = items.get(high).getQuantity();
        int i = low - 1;
        for (int j = low; j < high; j++) {
            if (items.get(j).getQuantity() <= pivot) {
                i++;
                InventoryItem temp = items.get(i);
                items.set(i, items.get(j));
                items.set(j, temp);
            }
        }
        InventoryItem temp = items.get(i + 1);
        items.set(i + 1, items.get(high));
        items.set(high, temp);
        return i + 1;
    }

    /**
     * Linear search filter for names containing query (case-insensitive).
     */
    public static List<MenuItem> filterMenuByName(List<MenuItem> items, String query) {
        List<MenuItem> results = new ArrayList<>();
        if (items == null) return results;
        if (query == null || query.trim().isEmpty()) return new ArrayList<>(items);

        String lowerQuery = query.toLowerCase().trim();
        for (MenuItem item : items) {
            if (item.getName().toLowerCase().contains(lowerQuery) ||
                (item.getDescription() != null && item.getDescription().toLowerCase().contains(lowerQuery))) {
                results.add(item);
            }
        }
        return results;
    }
}
