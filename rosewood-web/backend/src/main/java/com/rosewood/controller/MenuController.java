package com.rosewood.controller;

import com.rosewood.model.MenuCategory;
import com.rosewood.model.MenuItem;
import com.rosewood.repository.MenuCategoryRepository;
import com.rosewood.repository.MenuItemRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/menu")
public class MenuController {

    private final MenuItemRepository menuItemRepository;
    private final MenuCategoryRepository menuCategoryRepository;

    public MenuController(MenuItemRepository menuItemRepository,
                          MenuCategoryRepository menuCategoryRepository) {
        this.menuItemRepository = menuItemRepository;
        this.menuCategoryRepository = menuCategoryRepository;
    }

    @GetMapping("/items")
    public List<MenuItem> getAllItems() {
        List<MenuItem> items = menuItemRepository.findAll();
        // Populate category names
        items.forEach(item -> menuCategoryRepository.findById(item.getCategoryId())
                .ifPresent(cat -> item.setCategoryName(cat.getName())));
        return items;
    }

    @GetMapping("/items/available")
    public List<MenuItem> getAvailableItems() {
        List<MenuItem> items = menuItemRepository.findByAvailableTrue();
        items.forEach(item -> menuCategoryRepository.findById(item.getCategoryId())
                .ifPresent(cat -> item.setCategoryName(cat.getName())));
        return items;
    }

    @GetMapping("/items/{id}")
    public ResponseEntity<MenuItem> getItem(@PathVariable Integer id) {
        return menuItemRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/items")
    public ResponseEntity<MenuItem> createItem(@RequestBody MenuItem item) {
        return ResponseEntity.ok(menuItemRepository.save(item));
    }

    @PutMapping("/items/{id}")
    public ResponseEntity<MenuItem> updateItem(@PathVariable Integer id, @RequestBody MenuItem item) {
        if (!menuItemRepository.existsById(id)) return ResponseEntity.notFound().build();
        item.setItemId(id);
        return ResponseEntity.ok(menuItemRepository.save(item));
    }

    @DeleteMapping("/items/{id}")
    public ResponseEntity<?> deleteItem(@PathVariable Integer id) {
        if (!menuItemRepository.existsById(id)) return ResponseEntity.notFound().build();
        menuItemRepository.deleteById(id);
        return ResponseEntity.ok(Map.of("message", "Deleted"));
    }

    @GetMapping("/categories")
    public List<MenuCategory> getAllCategories() {
        return menuCategoryRepository.findAllByOrderByDisplayOrderAsc();
    }

    @PostMapping("/categories")
    public ResponseEntity<MenuCategory> createCategory(@RequestBody MenuCategory category) {
        return ResponseEntity.ok(menuCategoryRepository.save(category));
    }
}
