package com.shop.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.util.List;

public class CartDto {

    public record ItemRequest( //add
            @NotNull(message = "productId is required") Long productId,
            @NotNull(message = "Quantity is required") @Min(value = 1, message = "Quantity must be at least 1") Integer quantity) {}

    public record QuantityRequest( //update
            @NotNull(message = "Quantity is required") @Min(value = 1, message = "Quantity must be at least 1") Integer quantity) {}

    public record ItemResponse(Long itemId, Long productId, String productName, BigDecimal price, int quantity) {}

    public record Response(List<ItemResponse> items, BigDecimal total) {}
}
