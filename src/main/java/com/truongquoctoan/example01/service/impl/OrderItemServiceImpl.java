package com.truongquoctoan.example01.service.impl;

import com.truongquoctoan.example01.dto.OrderItemDto;
import com.truongquoctoan.example01.entity.Order;
import com.truongquoctoan.example01.entity.OrderItem;
import com.truongquoctoan.example01.entity.Product;
import com.truongquoctoan.example01.repository.OrderItemRepository;
import com.truongquoctoan.example01.repository.OrderRepository;
import com.truongquoctoan.example01.repository.ProductRepository;
import com.truongquoctoan.example01.service.OrderItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderItemServiceImpl implements OrderItemService {

    private final OrderItemRepository orderItemRepository;
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    @Override
    public List<OrderItem> getItemsByOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("❌ Order not found with id: " + orderId));
        return orderItemRepository.findByOrder(order);
    }

    @Override
    @Transactional
    public OrderItem createItem(OrderItemDto dto) {
        if (dto.getOrderId() == null) throw new RuntimeException("❌ orderId is required");
        if (dto.getProductId() == null) throw new RuntimeException("❌ productId is required");

        if (dto.getQuantity() == null || dto.getQuantity() <= 0)
            throw new RuntimeException("❌ Quantity must be greater than 0");

        if (dto.getPrice() == null || dto.getPrice().compareTo(BigDecimal.ZERO) <= 0)
            throw new RuntimeException("❌ Price must be greater than 0");

        Order order = orderRepository.findById(dto.getOrderId())
                .orElseThrow(() -> new RuntimeException("❌ Order not found with id: " + dto.getOrderId()));

        Product product = productRepository.findById(dto.getProductId())
                .orElseThrow(() -> new RuntimeException("❌ Product not found with id: " + dto.getProductId()));

        // ✅ nếu món đã có trong order -> cộng dồn
        OrderItem item = orderItemRepository
                .findByOrder_IdAndProduct_Id(dto.getOrderId(), dto.getProductId())
                .orElse(null);

        if (item == null) {
            item = OrderItem.builder()
                    .order(order)
                    .product(product)
                    .price(dto.getPrice())
                    .quantity(dto.getQuantity())
                    .subtotal(dto.getPrice().multiply(BigDecimal.valueOf(dto.getQuantity())))
                    .build();
        } else {
            int oldQty = item.getQuantity() == null ? 0 : item.getQuantity();
            int newQty = oldQty + dto.getQuantity();
            item.setQuantity(newQty);

            // nếu muốn luôn lấy giá FE gửi lên thì update price
            item.setPrice(dto.getPrice());
            item.setSubtotal(dto.getPrice().multiply(BigDecimal.valueOf(newQty)));
        }

        OrderItem saved = orderItemRepository.save(item);

        // ✅ cập nhật tổng tiền order
        updateOrderTotalAmount(order.getId());

        return saved;
    }

    @Override
    @Transactional
    public void deleteItem(Long id) {
        OrderItem item = orderItemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("❌ Cannot delete: OrderItem not found with id " + id));

        Long orderId = item.getOrder().getId();

        orderItemRepository.deleteById(id);

        // ✅ cập nhật tổng tiền order sau khi xóa
        updateOrderTotalAmount(orderId);
    }

    // ===================== HELPER: UPDATE ORDER TOTAL =====================
    private void updateOrderTotalAmount(Long orderId) {
        List<OrderItem> items = orderItemRepository.findByOrder_Id(orderId);

        BigDecimal total = BigDecimal.ZERO;
        for (OrderItem oi : items) {
            BigDecimal sub = oi.getSubtotal();
            if (sub == null) {
                BigDecimal price = oi.getPrice() == null ? BigDecimal.ZERO : oi.getPrice();
                int qty = oi.getQuantity() == null ? 0 : oi.getQuantity();
                sub = price.multiply(BigDecimal.valueOf(qty));
            }
            total = total.add(sub);
        }

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("❌ Order not found with id: " + orderId));

        order.setTotalAmount(total);
        orderRepository.save(order);
    }
}