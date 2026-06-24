package com.truongquoctoan.example01.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import com.truongquoctoan.example01.controller.OrderWebSocketController;
import com.truongquoctoan.example01.dto.OrderDto;
import com.truongquoctoan.example01.entity.*;
import com.truongquoctoan.example01.repository.*;
import com.truongquoctoan.example01.service.OrderService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final CoffeeTableRepository coffeeTableRepository;
    private final PromotionRepository promotionRepository;
    private final UserRepository userRepository;
    private final OrderWebSocketController webSocketController;

    @Override
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    @Override
    public Order getOrderById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("❌ Order not found with id: " + id));
    }

    // ✅ IMPLEMENT ĐÚNG METHOD CỦA REPOSITORY: StatusNotIn (PAID, CANCELLED)
    @Override
    public Order getActiveOrderByTableId(Long tableId) {
        List<Order.Status> closedStatuses = List.of(Order.Status.PAID, Order.Status.CANCELLED);
        return orderRepository.findFirstByTable_IdAndStatusNotInOrderByIdDesc(tableId, closedStatuses)
                .orElse(null);
    }

    @Override
    public Order createOrder(OrderDto dto, String username) {
        User creator = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("❌ Không tìm thấy user: " + username));

        CoffeeTable table = coffeeTableRepository.findById(dto.getTableId())
                .orElseThrow(() -> new RuntimeException("❌ Table not found with id: " + dto.getTableId()));

        Promotion promotion = null;
        if (dto.getPromotionId() != null) {
            promotion = promotionRepository.findById(dto.getPromotionId())
                    .orElseThrow(() -> new RuntimeException("❌ Promotion not found with id: " + dto.getPromotionId()));
        }

        User employee = null;
        User customer = null;

        if (creator.getRole() == User.Role.ADMIN || creator.getRole() == User.Role.EMPLOYEE) {
            employee = creator;
        } else if (creator.getRole() == User.Role.USER) {
            customer = creator;
        }

        Order order = Order.builder()
                .table(table)
                .employee(employee)
                .user(customer)
                .promotion(promotion)
                .status(Order.Status.PENDING)
                .notes(dto.getNotes())
                .totalAmount(dto.getTotalAmount())
                .build();

        Order savedOrder = orderRepository.save(order);

        try {
            webSocketController.notifyNewOrder(savedOrder);
        } catch (Exception e) {
            System.out.println("⚠️ Không gửi được WebSocket: " + e.getMessage());
        }

        return savedOrder;
    }

    @Override
    public Order updateOrder(Long id, OrderDto dto) {
        Order order = getOrderById(id);

        if (dto.getNotes() != null) {
            order.setNotes(dto.getNotes());
        }

        // ✅ an toàn: pending/PENDING/ Pending đều OK
        if (dto.getStatus() != null) {
            try {
                order.setStatus(Order.Status.valueOf(dto.getStatus().trim().toUpperCase()));
            } catch (IllegalArgumentException ex) {
                throw new RuntimeException("❌ Status không hợp lệ: " + dto.getStatus());
            }
        }

        if (dto.getTotalAmount() != null) {
            order.setTotalAmount(dto.getTotalAmount());
        }

        if (dto.getPromotionId() != null) {
            Promotion promo = promotionRepository.findById(dto.getPromotionId())
                    .orElseThrow(() -> new RuntimeException("❌ Promotion not found"));
            order.setPromotion(promo);
        }

        Order updatedOrder = orderRepository.save(order);

        try {
            webSocketController.notifyOrderUpdate(updatedOrder);
        } catch (Exception e) {
            System.out.println("⚠️ Không gửi được WebSocket update: " + e.getMessage());
        }

        return updatedOrder;
    }

    @Override
    public void deleteOrder(Long id) {
        if (!orderRepository.existsById(id)) {
            throw new RuntimeException("❌ Cannot delete: Order not found with id " + id);
        }
        orderRepository.deleteById(id);
    }
}