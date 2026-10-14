package com.shop.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public class ProductDto {

    public record Request(
            @NotBlank(message = "Name is required") String name,
            @NotNull(message = "Price is required") @Positive(message = "Price must be greater than 0") BigDecimal price,
            @NotNull(message = "Quantity is required") @Min(value = 0, message = "Quantity cannot be negative") Integer quantity) {}

    public record Update(
            @NotNull(message = "Price is required") @Positive(message = "Price must be greater than 0") BigDecimal price,
            @NotNull(message = "Quantity is required") @Min(value = 0, message = "Quantity cannot be negative") Integer quantity) {}

    public record Response(Long id, String name, BigDecimal price, int quantity, boolean enabled) {}
}
