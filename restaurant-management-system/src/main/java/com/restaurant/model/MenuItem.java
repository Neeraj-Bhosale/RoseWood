package com.restaurant.model;

import com.restaurant.util.CurrencyUtil;

public class MenuItem {
    private int itemId;
    private int categoryId;
    private String categoryName;
    private String name;
    private String description;
    private double price;
    private int preparationTime; // Minutes
    private boolean available;
    private String imagePath;

    public MenuItem() {
        this.available = true;
        this.preparationTime = 15;
    }

    public MenuItem(int itemId, int categoryId, String name, String description, double price, int preparationTime, boolean available) {
        this.itemId = itemId;
        this.categoryId = categoryId;
        this.name = name;
        this.description = description;
        this.price = price;
        this.preparationTime = preparationTime;
        this.available = available;
    }

    public int getItemId() {
        return itemId;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
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

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getPreparationTime() {
        return preparationTime;
    }

    public void setPreparationTime(int preparationTime) {
        this.preparationTime = preparationTime;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    @Override
    public String toString() {
        return name + " (" + CurrencyUtil.format(price) + ")";
    }
}
