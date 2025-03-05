package com.example.ecommerce.service;

import com.example.ecommerce.dto.CartDTO;
import com.example.ecommerce.dto.OrderDTO;
import com.example.ecommerce.exception.InsufficientStockException;
import com.example.ecommerce.exception.ResourceNotFoundException;
import com.example.ecommerce.mapper.CartMapper;
import com.example.ecommerce.mapper.OrderMapper;
import com.example.ecommerce.model.*;
import com.example.ecommerce.repository.OrderRepository;
import com.example.ecommerce.repository.ProductRepository;
import com.example.ecommerce.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.aspectj.weaver.ast.Or;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.MailException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderService {
    private final Logger logger = LoggerFactory.getLogger(OrderService.class);
    private final OrderRepository orderRepository;
    private  final CartService cartService;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;
    private final OrderMapper orderMapper;
    private final CartMapper cartMapper;

    public OrderService(OrderRepository orderRepository, CartService cartService, ProductRepository productRepository, UserRepository userRepository, EmailService emailService, OrderMapper orderMapper, CartMapper cartMapper) {
        this.orderRepository = orderRepository;
        this.cartService = cartService;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
        this.emailService = emailService;
        this.orderMapper = orderMapper;
        this.cartMapper = cartMapper;
    }

    public OrderDTO createOrder(Long userId,String address, String phoneNumber){
        User user = userRepository.findById(userId)
                .orElseThrow(()->new ResourceNotFoundException("User not found"));

        if (!user.isEmailConfirmation())
            throw new IllegalStateException("Email not confirmed. Please confirm your email before placing order.");

        CartDTO cartDTO = cartService.getCart(userId);
        Cart cart = cartMapper.toEntity(cartDTO);
        if (cart.getItems().isEmpty()){
            throw  new IllegalStateException("Cannot create an order with an empty cart");
        }

        Order order = new Order();
        order.setUser(user);
        order.setAddress(address);
        order.setPhoneNumber(phoneNumber);
        order.setStatus(Order.OrderStatus.PREPARING);
        order.setCreatedAt(LocalDateTime.now());

        List<OrderItem> orderItems = createOrderItems(cart,order);
        order.setItems(orderItems);

        Order savedOrder = orderRepository.save(order);
        cartService.clearCart(userId);

        try{
            emailService.sendOrderConfirmation(savedOrder);
        } catch (MailException e){
            logger.error("Failed to send order confirmation email for order ID "+savedOrder.getId(), e);
        }
        return orderMapper.toDto(savedOrder);
    }

    private List<OrderItem> createOrderItems(Cart cart, Order order) {
        return cart.getItems().stream().map(cartItem ->{
            Product product = productRepository.findById(cartItem.getProduct().getId())
                    .orElseThrow(()->new EntityNotFoundException("Product not found with id " + cartItem.getProduct().getId()));
            if (product.getQuantity() == null)
                throw new IllegalStateException("Product quantity not set for product "+product.getName());
            if (product.getQuantity() < cartItem.getQuantity())
                throw  new InsufficientStockException("Not enough stock for " + product.getName());
            product.setQuantity(product.getQuantity() - cartItem.getQuantity());
            productRepository.save(product);

            return new OrderItem(null,order,product, cartItem.getQuantity(),product.getPrice());
        }).collect(Collectors.toList());
    }

    public List<OrderDTO> getAllOrders(){
        return  orderMapper.toDTOs(orderRepository.findAll());
    }

    public List<OrderDTO> getUserOrders(Long userId){
        return  orderMapper.toDTOs(orderRepository.findByUserId(userId));
    }

    public OrderDTO updateOrderStatus(Long orderId,Order.OrderStatus status){
        Order order = orderRepository.findById(orderId)
                .orElseThrow(()->new ResourceNotFoundException("Order not found"));
        order.setStatus(status);
        Order updatedOrder = orderRepository.save(order);
        return orderMapper.toDto(updatedOrder);
    }
}
