package com.laptophub.backend.controller;

import com.laptophub.backend.model.Order;
import com.laptophub.backend.model.OrderStatus;
import com.laptophub.backend.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/user/{userId}")
    public Order createFromCart(
            @PathVariable UUID userId,
            @RequestParam String direccionEnvio
    ) {
        return orderService.createOrderFromCart(userId, direccionEnvio);
    }

    @GetMapping("/{orderId}")
    public Order findById(@PathVariable Long orderId) {
        return orderService.findById(orderId);
    }

    @GetMapping("/user/{userId}")
    public Page<Order> findByUser(@PathVariable UUID userId, Pageable pageable) {
        return orderService.findByUserId(userId, pageable);
    }

    @GetMapping("/status/{estado}")
    public Page<Order> findByStatus(@PathVariable OrderStatus estado, Pageable pageable) {
        return orderService.findByStatus(estado, pageable);
    }

    @PutMapping("/{orderId}/status/{estado}")
    public Order updateStatus(
            @PathVariable Long orderId,
            @PathVariable OrderStatus estado
    ) {
        return orderService.updateOrderStatus(orderId, estado);
    }

    @PostMapping("/{orderId}/cancel")
    public Order cancel(@PathVariable Long orderId) {
        return orderService.cancelOrder(orderId);
    }

    @PostMapping("/expire")
    public int expirePending() {
        return orderService.expirePendingPaymentOrders();
    }

    @GetMapping
    public List<Order> findAll() {
        return orderService.findAll();
    }
}