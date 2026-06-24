package com.truongquoctoan.example01.controller;

import com.truongquoctoan.example01.dto.OrderDto;
import com.truongquoctoan.example01.entity.Order;
import com.truongquoctoan.example01.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
public class OrderController {

    private final OrderService orderService;

    @GetMapping
    public List<Order> getAll() {
        return orderService.getAllOrders();
    }

    // ✅ NEW: lấy order đang chạy theo tableId (để FE có activeOrderId)
    @GetMapping("/active/table/{tableId}")
    public ResponseEntity<?> getActiveOrderByTable(@PathVariable Long tableId) {
        try {
            Order order = orderService.getActiveOrderByTableId(tableId);
            return ResponseEntity.ok(order);
        } catch (Exception e) {
            return ResponseEntity.status(404).body("❌ Không có order đang chạy cho bàn này");
        }
    }

    // ✅ NEW: FE dùng để lấy totalAmount mới nhất sau khi append món
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.getOrderById(id));
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody OrderDto dto, Principal principal) {
        try {
            if (principal == null) {
                return ResponseEntity.status(401).body("⚠️ Bạn chưa đăng nhập!");
            }
            String username = principal.getName();
            Order createdOrder = orderService.createOrder(dto, username);
            return ResponseEntity.ok(createdOrder);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("❌ Lỗi khi tạo đơn hàng: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public Order update(@PathVariable Long id, @RequestBody OrderDto dto) {
        return orderService.updateOrder(id, dto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        try {
            orderService.deleteOrder(id);
            return ResponseEntity.ok("✅ Đã xóa đơn hàng");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("❌ Xóa thất bại: " + e.getMessage());
        }
    }
}