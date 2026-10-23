package com.rosewood.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class CreateOrderRequest {
    private Integer customerId;
    private Integer tableId;
    private String specialNotes;
    private List<OrderItemRequest> items;

    @Data
    public static class OrderItemRequest {
        private Integer itemId;
        private Integer quantity;
        private String specialInstructions;
    }
}
