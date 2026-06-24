package com.truongquoctoan.example01.repository;

import com.truongquoctoan.example01.entity.Order;       
import com.truongquoctoan.example01.entity.OrderItem;   
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    List<OrderItem> findByOrder(Order order);

    List<OrderItem> findByOrder_Id(Long orderId);

    Optional<OrderItem> findByOrder_IdAndProduct_Id(Long orderId, Long productId);

    void deleteByOrder_Id(Long orderId);
}