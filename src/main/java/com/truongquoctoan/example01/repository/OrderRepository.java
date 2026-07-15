package com.truongquoctoan.example01.repository;

import com.truongquoctoan.example01.entity.Order;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository
        extends JpaRepository<Order, Long> {

    // Lấy đơn đang mở mới nhất của một bàn
    Optional<Order>
            findFirstByTable_IdAndStatusNotInOrderByIdDesc(
                    Long tableId,
                    List<Order.Status> statuses
            );

    // Đếm số đơn đã thanh toán của một khách hàng
    long countByUser_IdAndStatus(
            Long userId,
            Order.Status status
    );

    // Tính tổng tiền khách hàng đã chi tiêu
    @Query("""
            SELECT COALESCE(SUM(o.totalAmount), 0)
            FROM Order o
            WHERE o.user.id = :userId
              AND o.status = :status
            """)
    BigDecimal sumTotalAmountByUserIdAndStatus(
            @Param("userId") Long userId,
            @Param("status") Order.Status status
    );

    // Lấy danh sách đơn của một khách hàng
    List<Order> findByUser_IdOrderByIdDesc(
            Long userId
    );

    // Lấy danh sách đơn đã thanh toán của khách hàng
    List<Order> findByUser_IdAndStatusOrderByIdDesc(
            Long userId,
            Order.Status status
    );
}