package com.rosewood.repository;

import com.rosewood.model.InventoryItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface InventoryRepository extends JpaRepository<InventoryItem, Integer> {
    @Query("SELECT i FROM InventoryItem i WHERE i.quantity <= i.minimumStock ORDER BY i.quantity ASC")
    List<InventoryItem> findLowStockItems();

    List<InventoryItem> findByCategory(String category);

    List<InventoryItem> findByIngredientNameContainingIgnoreCase(String name);
}
