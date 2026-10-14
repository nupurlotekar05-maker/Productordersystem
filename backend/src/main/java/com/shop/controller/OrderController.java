package com.shop.controller;

import com.shop.dto.OrderDto;
import com.shop.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController @RequestMapping("/api/orders") @RequiredArgsConstructor
public class OrderController {
    private final OrderService service;

    @PostMapping @ResponseStatus(HttpStatus.CREATED)
    public OrderDto.Response place(@RequestHeader("X-User-Id") Long userId) {
        return service.placeOrder(userId);
    }

    @GetMapping
    public List<OrderDto.Response> myOrders(@RequestHeader("X-User-Id") Long userId) {
        return service.myOrders(userId);
    }
}
