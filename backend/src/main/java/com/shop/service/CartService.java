package com.shop.service;

import com.shop.dto.CartDto;
import com.shop.entity.Cart;
import com.shop.entity.CartItem;
import com.shop.entity.Product;
import com.shop.exception.BadRequestException;
import com.shop.exception.InsufficientStockException;
import com.shop.exception.ResourceNotFoundException;
import com.shop.repository.CartRepository;
import com.shop.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.List;

@Service @RequiredArgsConstructor @Transactional
public class CartService {
    private final CartRepository cartRepo;
    private final ProductRepository productRepo;
    private final UserService userService;

    public CartDto.Response get(Long userId) {
        return toDto(getOrCreateCart(userId));
    }
    private Cart getOrCreateCart(Long userId) {//cart get list or create it
        return cartRepo.findByUserId(userId).orElseGet(() -> {
            Cart c = new Cart();
            c.setUser(userService.getUser(userId));
            return cartRepo.save(c);
        });
    }

    public CartDto.Response addItem(Long userId, CartDto.ItemRequest r) {
        Cart cart = getOrCreateCart(userId);
        Product p = productRepo.findById(r.productId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + r.productId()));
        if (!p.isEnabled()) throw new BadRequestException("Product is disabled: " + p.getName());

        // if product is already in the cart, increase its quantity
        CartItem item = cart.getItems().stream()
                .filter(i -> i.getProduct().getId().equals(p.getId()))
                .findFirst().orElse(null);//if product not in cart then item==null
        int newQty = r.quantity() + (item == null ? 0 : item.getQuantity());//if product -> inc quantity !product -> 0 
        checkStock(p, newQty);//check again
       //create cart item entity
        if (item == null) { 
            item = new CartItem();
            item.setCart(cart);
            item.setProduct(p);
            cart.getItems().add(item);//add into cart item collection
        }
        item.setQuantity(newQty);
        cartRepo.saveAndFlush(cart);           // so the new item gets its id
        return toDto(cart);
    }
    
    private void checkStock(Product p, int qty) {//quantity must be less than stock
        if (p.getQuantity() < qty)
            throw new InsufficientStockException("Only " + p.getQuantity() + " units of " + p.getName() + " available");
    }

    public CartDto.Response updateItem(Long userId, Long itemId, CartDto.QuantityRequest r) {
        Cart cart = getOrCreateCart(userId);
        CartItem item = findItem(cart, itemId);
        checkStock(item.getProduct(), r.quantity());
        item.setQuantity(r.quantity());
        return toDto(cart);
    }
    private CartItem findItem(Cart cart, Long itemId) {
        return cart.getItems().stream().filter(i -> i.getId().equals(itemId)).findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Cart item not found: " + itemId));
    }

    public CartDto.Response removeItem(Long userId, Long itemId) {
        Cart cart = getOrCreateCart(userId);
        cart.getItems().remove(findItem(cart, itemId));    // orphanRemoval deletes the row
        return toDto(cart);
    }


    private CartDto.Response toDto(Cart cart) {
        List<CartDto.ItemResponse> items = cart.getItems().stream() //item collec -> dto
                .map(i -> new CartDto.ItemResponse(i.getId(), i.getProduct().getId(),
                        i.getProduct().getName(), i.getProduct().getPrice(), i.getQuantity()))
                .toList();
        BigDecimal total = items.stream()
                .map(i -> i.price().multiply(BigDecimal.valueOf(i.quantity())))//integer quantity into a BigDecimal.
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return new CartDto.Response(items, total);
    }
}
