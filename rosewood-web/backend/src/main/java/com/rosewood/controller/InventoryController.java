package com.rosewood.controller;

import com.rosewood.model.InventoryItem;
import com.rosewood.repository.InventoryRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/inventory")
public class InventoryController {

    private final InventoryRepository inventoryRepository;

    public InventoryController(InventoryRepository inventoryRepository) {
        this.inventoryRepository = inventoryRepository;
    }

    @GetMapping
    public List<InventoryItem> getAll() {
        return inventoryRepository.findAll();
    }

    @GetMapping("/low-stock")
    public List<InventoryItem> getLowStock() {
        return inventoryRepository.findLowStockItems();
    }

    @GetMapping("/{id}")
    public ResponseEntity<InventoryItem> getById(@PathVariable Integer id) {
        return inventoryRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<InventoryItem> create(@RequestBody InventoryItem item) {
        InventoryItem saved = inventoryRepository.save(item);
        return ResponseEntity.ok(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<InventoryItem> update(@PathVariable Integer id, @RequestBody InventoryItem item) {
        if (!inventoryRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        item.setInventoryId(id);
        return ResponseEntity.ok(inventoryRepository.save(item));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Integer id) {
        if (!inventoryRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        inventoryRepository.deleteById(id);
        return ResponseEntity.ok(Map.of("message", "Deleted successfully"));
    }
}
