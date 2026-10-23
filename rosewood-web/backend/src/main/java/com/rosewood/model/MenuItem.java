package com.rosewood.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "menu_items")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MenuItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "item_id")
    private Integer itemId;

    @Column(name = "category_id", nullable = false)
    private Integer categoryId;

    @Column(name = "name", unique = true, nullable = false, length = 100)
    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "price", nullable = false)
    private BigDecimal price;

    @Column(name = "preparation_time", nullable = false)
    private Integer preparationTime = 15;

    @Column(name = "is_available", nullable = false)
    private boolean available = true;

    @Column(name = "image_path", length = 255)
    private String imagePath;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    // Transient: category name joined from menu_categories
    @Transient
    private String categoryName;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
