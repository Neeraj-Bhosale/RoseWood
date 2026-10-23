package com.rosewood.controller;

import com.rosewood.model.RestaurantTable;
import com.rosewood.repository.TableRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/tables")
public class TableController {

    private final TableRepository tableRepository;

    public TableController(TableRepository tableRepository) {
        this.tableRepository = tableRepository;
    }

    @GetMapping
    public List<RestaurantTable> getAll() {
        return tableRepository.findAll();
    }

    @GetMapping("/available")
    public List<RestaurantTable> getAvailable() {
        return tableRepository.findByStatusOrderByTableNumberAsc("AVAILABLE");
    }

    @PostMapping
    public ResponseEntity<RestaurantTable> create(@RequestBody RestaurantTable table) {
        return ResponseEntity.ok(tableRepository.save(table));
    }

    @PutMapping("/{id}")
    public ResponseEntity<RestaurantTable> update(@PathVariable Integer id, @RequestBody RestaurantTable table) {
        if (!tableRepository.existsById(id)) return ResponseEntity.notFound().build();
        table.setTableId(id);
        return ResponseEntity.ok(tableRepository.save(table));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<?> updateStatus(@PathVariable Integer id, @RequestBody Map<String, String> body) {
        return tableRepository.findById(id).map(table -> {
            table.setStatus(body.get("status"));
            return ResponseEntity.ok(tableRepository.save(table));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Integer id) {
        if (!tableRepository.existsById(id)) return ResponseEntity.notFound().build();
        tableRepository.deleteById(id);
        return ResponseEntity.ok(Map.of("message", "Deleted"));
    }
}
