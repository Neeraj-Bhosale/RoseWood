package com.rosewood.repository;

import com.rosewood.model.RestaurantTable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TableRepository extends JpaRepository<RestaurantTable, Integer> {
    List<RestaurantTable> findByStatus(String status);
    List<RestaurantTable> findByStatusOrderByTableNumberAsc(String status);
}
