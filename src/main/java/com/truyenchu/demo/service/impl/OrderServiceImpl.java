package com.truyenchu.demo.service.impl;

import com.truyenchu.demo.entity.Order;
import com.truyenchu.demo.entity.User;
import com.truyenchu.demo.repository.OrderRepository;
import com.truyenchu.demo.repository.UserRepository;
import com.truyenchu.demo.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public Order createOrder(Long userId, Long orderCode, int price, int coins) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        Order order = new Order();
        order.setUser(user);
        order.setOrderCode(orderCode);
        order.setStatus(Order.OrderStatus.PENDING);
        order.setPrice(price);
        order.setCoins(coins);
        
        return orderRepository.save(order);
    }

    @Override
    public List<Order> getUserOrders(Long userId) {
        return orderRepository.findByUserId(userId);
    }

    @Override
    public Order getOrderByOrderCode(Long orderCode) {
        return orderRepository.findByOrderCode(orderCode)
                .orElseThrow(() -> new RuntimeException("Order not found"));
    }

    @Override
    public Order updateOrderStatus(Long orderId, Order.OrderStatus status) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        order.setStatus(status);
        return orderRepository.save(order);
    }
} 