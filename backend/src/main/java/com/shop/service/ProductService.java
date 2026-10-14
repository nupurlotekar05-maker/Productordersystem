package com.shop.service;

import com.shop.dto.ProductDto;
import com.shop.entity.Product;
import com.shop.exception.ResourceNotFoundException;
import com.shop.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service @RequiredArgsConstructor @Transactional
public class ProductService {
    private final ProductRepository repo;

    public ProductDto.Response add(ProductDto.Request r) {
        Product p = new Product();
        p.setName(r.name());
        p.setPrice(r.price());
        p.setQuantity(r.quantity());
        return toDto(repo.save(p));
    }

    public ProductDto.Response update(Long id, ProductDto.Update r) {
        Product p = find(id);
        p.setPrice(r.price());                 // changes are saved automatically (dirty checking)
        p.setQuantity(r.quantity());
        return toDto(p);
    }

    public ProductDto.Response setEnabled(Long id, boolean enabled) {
        Product p = find(id);
        p.setEnabled(enabled);
        return toDto(p);
    }
     // for user
    @Transactional(readOnly = true)
    public List<ProductDto.Response> listEnabled() {
        return repo.findByEnabledTrue().stream().map(this::toDto).toList();
    }

    private Product find(Long id) {
        return repo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Product not found: " + id));
    }

    private ProductDto.Response toDto(Product p) {
        return new ProductDto.Response(p.getId(), p.getName(), p.getPrice(), p.getQuantity(), p.isEnabled());
    }
}
