package com.shop.service;

import com.shop.dto.OrderDto;
import com.shop.entity.*;
import com.shop.exception.BadRequestException;
import com.shop.exception.InsufficientStockException;
import com.shop.repository.CartRepository;
import com.shop.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.List;

@Service @RequiredArgsConstructor @Transactional
public class OrderService {
    private final OrderRepository orderRepo;
    private final CartRepository cartRepo;
    private final UserService userService;

    // ONE transaction: if anything throws, every stock change below is rolled back
    public OrderDto.Response placeOrder(Long userId) {
        User user = userService.getUser(userId);
        Cart cart = cartRepo.findByUserId(userId).orElseThrow(() -> new BadRequestException("Cart is empty"));
        if (cart.getItems().isEmpty()) throw new BadRequestException("Cart is empty");

        Order order = new Order();
        order.setUser(user);
        BigDecimal total = BigDecimal.ZERO;

        for (CartItem ci : cart.getItems()) {
            Product p = ci.getProduct();//req product
            if (!p.isEnabled())
                throw new BadRequestException("Product is disabled: " + p.getName());
            if (p.getQuantity() < ci.getQuantity())  //req quantity                     // rule: fail if stock is not enough
                throw new InsufficientStockException("Insufficient stock for " + p.getName()
                        + ": available " + p.getQuantity() + ", requested " + ci.getQuantity());

            p.setQuantity(p.getQuantity() - ci.getQuantity());            // rule: reduce inventory

            OrderItem oi = new OrderItem();
            oi.setOrder(order);
            oi.setProduct(p);
            oi.setQuantity(ci.getQuantity());
            oi.setPrice(p.getPrice());
            order.getItems().add(oi);
            total = total.add(p.getPrice().multiply(BigDecimal.valueOf(ci.getQuantity())));
        }

        order.setTotalAmount(total);
        orderRepo.save(order);
        cart.getItems().clear();                                          // empty the cart
        return toDto(order);
    }

    @Transactional(readOnly = true)
    public List<OrderDto.Response> myOrders(Long userId) {
        userService.getUser(userId);
        return orderRepo.findByUserIdOrderByCreatedAtDesc(userId).stream().map(this::toDto).toList();
    }

    private OrderDto.Response toDto(Order o) {
        List<OrderDto.ItemResponse> items = o.getItems().stream()
                .map(i -> new OrderDto.ItemResponse(i.getProduct().getName(), i.getQuantity(), i.getPrice()))
                .toList();
        return new OrderDto.Response(o.getId(), o.getStatus(), o.getTotalAmount(), o.getCreatedAt(), items);
    }
}
