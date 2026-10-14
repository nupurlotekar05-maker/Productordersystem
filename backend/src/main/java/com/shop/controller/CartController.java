package com.shop.controller;

import com.shop.dto.CartDto;
import com.shop.service.CartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController @RequestMapping("/api/cart") @RequiredArgsConstructor
public class CartController {
    private final CartService service;

    @GetMapping
    public CartDto.Response get(@RequestHeader("X-User-Id") Long userId) {
        return service.get(userId);
    }

    @PostMapping("/items")
    public CartDto.Response add(@RequestHeader("X-User-Id") Long userId, @Valid @RequestBody CartDto.ItemRequest r) {
        return service.addItem(userId, r);
    }

    @PutMapping("/items/{itemId}")
    public CartDto.Response update(@RequestHeader("X-User-Id") Long userId, @PathVariable Long itemId,
                                   @Valid @RequestBody CartDto.QuantityRequest r) {
        return service.updateItem(userId, itemId, r);
    }

    @DeleteMapping("/items/{itemId}")
    public CartDto.Response remove(@RequestHeader("X-User-Id") Long userId, @PathVariable Long itemId) {
        return service.removeItem(userId, itemId);
    }
}
