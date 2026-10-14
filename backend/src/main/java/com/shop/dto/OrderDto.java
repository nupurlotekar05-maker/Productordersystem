package com.shop.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class OrderDto {

    public record ItemResponse(String productName, int quantity, BigDecimal price) {}

    public record Response(Long id, String status, BigDecimal totalAmount, LocalDateTime createdAt, List<ItemResponse> items) {}
}
