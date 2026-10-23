package com.rosewood.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "inventory")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class InventoryItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "inventory_id")
    private Integer inventoryId;

    @Column(name = "ingredient_name", unique = true, nullable = false, length = 100)
    private String ingredientName;

    @Column(name = "category", nullable = false, length = 50)
    private String category;

    @Column(name = "quantity", nullable = false)
    private BigDecimal quantity = BigDecimal.ZERO;

    @Column(name = "unit", nullable = false, length = 20)
    private String unit;

    @Column(name = "minimum_stock", nullable = false)
    private BigDecimal minimumStock = BigDecimal.valueOf(5);

    @Column(name = "unit_cost", nullable = false)
    private BigDecimal unitCost = BigDecimal.ZERO;

    @Column(name = "supplier_id")
    private Integer supplierId;

    @Column(name = "last_updated")
    private LocalDateTime lastUpdated;

    public boolean isLowStock() {
        return quantity.compareTo(minimumStock) <= 0;
    }
}
