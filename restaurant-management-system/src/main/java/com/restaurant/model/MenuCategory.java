package com.restaurant.model;

public class MenuCategory {
    private int categoryId;
    private String name;
    private String description;
    private int displayOrder;

    public MenuCategory() {}

    public MenuCategory(int categoryId, String name, String description, int displayOrder) {
        this.categoryId = categoryId;
        this.name = name;
        this.description = description;
        this.displayOrder = displayOrder;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(int displayOrder) {
        this.displayOrder = displayOrder;
    }

    @Override
    public String toString() {
        return name;
    }
}
