package com.truongquoctoan.example01.service;

import com.truongquoctoan.example01.dto.OrderDto;
import com.truongquoctoan.example01.entity.Order;

import java.util.List;

public interface OrderService {
    List<Order> getAllOrders();
    Order getOrderById(Long id);
    Order createOrder(OrderDto dto, String username);
    Order updateOrder(Long id, OrderDto dto);
    void deleteOrder(Long id);

    // ✅ NEW: lấy order đang hoạt động theo bàn
    Order getActiveOrderByTableId(Long tableId);
}