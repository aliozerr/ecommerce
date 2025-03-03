package com.example.ecommerce.service;

import com.example.ecommerce.dto.CartDTO;
import com.example.ecommerce.exception.InsufficientStockException;
import com.example.ecommerce.exception.ResourceNotFoundException;
import com.example.ecommerce.mapper.CartMapper;
import com.example.ecommerce.model.Cart;
import com.example.ecommerce.model.CartItem;
import com.example.ecommerce.model.Product;
import com.example.ecommerce.model.User;
import com.example.ecommerce.repository.CartRepository;
import com.example.ecommerce.repository.ProductRepository;
import com.example.ecommerce.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.naming.InsufficientResourcesException;
import java.util.ArrayList;
import java.util.Optional;

@Service
public class CartService {
    private  final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final CartMapper cartMapper;

    @Autowired
    public CartService(CartRepository cartRepository, ProductRepository productRepository, UserRepository userRepository, CartMapper cartMapper) {
        this.cartRepository = cartRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
        this.cartMapper = cartMapper;
    }

    public CartDTO addToCart(Long userId, Long productId,Integer quantity){
        User user = userRepository.findById(userId).
                orElseThrow(()-> new ResourceNotFoundException("User not found"));
        Product product = productRepository.findById(productId)
                .orElseThrow(()-> new ResourceNotFoundException("Product not found"));
        if(product.getQuantity()<quantity){
            throw  new InsufficientStockException("Not enough available");
        }

        Cart cart = cartRepository.findByUserId(userId)
                .orElse(new Cart(null,user, new ArrayList<>()));

        Optional<CartItem> existingCartItem = cart.getItems().stream()
                .filter(item ->item.getProduct().getId().equals(productId))
                .findFirst();

        if(existingCartItem.isPresent()){
            CartItem cartItem = existingCartItem.get();
            cartItem.setQuantity(cartItem.getQuantity()+quantity);
        }else {
            CartItem cartItem = new CartItem(null,cart,product,quantity);
            cart.getItems().add(cartItem);
        }
        Cart saved = cartRepository.save(cart);
        return cartMapper.toDTO(saved);
    }

    public  CartDTO getCart(Long userId){
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(()->new ResourceNotFoundException("Cart not found"));

        return cartMapper.toDTO(cart);
    }

    public void clearCart(Long userId){
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(()-> new ResourceNotFoundException("Cart not found"));
        cart.getItems().clear();
        cartRepository.save(cart);
    }


}
