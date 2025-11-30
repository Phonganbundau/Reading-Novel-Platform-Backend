package com.truyenchu.demo.service;

import com.truyenchu.demo.entity.Order;

import java.util.List;

public interface OrderService {
    Order createOrder(Long userId, Long orderCode, int price, int coins);
    List<Order> getUserOrders(Long userId);
    Order getOrderByOrderCode(Long orderCode);
    Order updateOrderStatus(Long orderId, Order.OrderStatus status);
} 