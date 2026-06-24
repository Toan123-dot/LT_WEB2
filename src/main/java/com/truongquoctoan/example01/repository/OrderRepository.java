package com.truongquoctoan.example01.repository;

import com.truongquoctoan.example01.entity.Order; 
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    Optional<Order> findFirstByTable_IdAndStatusNotInOrderByIdDesc(
            Long tableId,
            List<Order.Status> statuses
    );
}