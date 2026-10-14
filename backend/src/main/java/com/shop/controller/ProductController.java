package com.shop.controller;

import com.shop.dto.ProductDto;
import com.shop.service.ProductService;
import com.shop.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController @RequestMapping("/api") @RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;
    private final UserService userService;

    //  USER: view products  
    @GetMapping("/products")
    public List<ProductDto.Response> list() {
        return productService.listEnabled();
    }

    //  ADMIN only 
    @PostMapping("/admin/products") @ResponseStatus(HttpStatus.CREATED)//201
    public ProductDto.Response add(@RequestHeader("X-User-Id") Long userId,
                                   @Valid @RequestBody ProductDto.Request r) {
        userService.requireAdmin(userId);
        return productService.add(r);
    }

    @PutMapping("/admin/products/{id}")
    public ProductDto.Response update(@RequestHeader("X-User-Id") Long userId, @PathVariable Long id,
                                      @Valid @RequestBody ProductDto.Update r) {
        userService.requireAdmin(userId);
        return productService.update(id, r);
    }

    @PatchMapping("/admin/products/{id}/status")
    public ProductDto.Response status(@RequestHeader("X-User-Id") Long userId, @PathVariable Long id,
                                      @RequestParam boolean enabled) {
        userService.requireAdmin(userId);
        return productService.setEnabled(id, enabled);//enable or if disable we cannot add that product..
    }
}
