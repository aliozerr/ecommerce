package com.example.ecommerce.controller;

import com.example.ecommerce.dto.CartDTO;
import com.example.ecommerce.model.Cart;
import com.example.ecommerce.model.User;
import com.example.ecommerce.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/api/cart")
public class CartController {
    private final CartService cartService;
    @Autowired
    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @PostMapping("/add")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<CartDTO> addToChart(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam Long productId,
            @RequestParam Integer quantity){
       Long userId = ((User)userDetails).getId();
       return  ResponseEntity.ok(cartService.addToCart(userId,productId,quantity));
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<CartDTO> getCart(@AuthenticationPrincipal UserDetails userDetails){
        Long userId = ((User) userDetails).getId();
        return ResponseEntity.ok(cartService.getCart(userId));
    }

    @DeleteMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> clearCart(@AuthenticationPrincipal UserDetails userDetails){
        Long userId = ((User) userDetails).getId();
        cartService.clearCart(userId);
        return ResponseEntity.noContent().build();
    }
}
