package com.laptophub.backend.controller;

import com.laptophub.backend.model.Cart;
import com.laptophub.backend.model.CartItem;
import com.laptophub.backend.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.UUID;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @GetMapping("/user/{userId}")
    public Cart getCartByUser(@PathVariable UUID userId) {
        return cartService.getCartByUserId(userId);
    }

    @PostMapping("/user/{userId}/items")
    public Cart addToCart(
            @PathVariable UUID userId,
            @RequestParam Long productId,
            @RequestParam Integer cantidad
    ) {
        return cartService.addToCart(userId, productId, cantidad);
    }

    @PutMapping("/items/{cartItemId}")
    public CartItem updateQuantity(
            @PathVariable Long cartItemId,
            @RequestParam Integer cantidad
    ) {
        return cartService.updateQuantity(cartItemId, cantidad);
    }

    @DeleteMapping("/items/{cartItemId}")
    public void removeFromCart(@PathVariable Long cartItemId) {
        cartService.removeFromCart(cartItemId);
    }

    @DeleteMapping("/user/{userId}/clear")
    public void clearCart(@PathVariable UUID userId) {
        cartService.clearCart(userId);
    }

    @GetMapping("/{cartId}/total")
    public BigDecimal calculateTotal(@PathVariable Long cartId) {
        return cartService.calculateTotal(cartId);
    }
}