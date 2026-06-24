package com.truongquoctoan.example01.service;

import com.truongquoctoan.example01.dto.OrderItemDto;
import com.truongquoctoan.example01.entity.OrderItem;
import java.util.List;

public interface OrderItemService {

    List<OrderItem> getItemsByOrder(Long orderId);

    OrderItem createItem(OrderItemDto dto);

    void deleteItem(Long id);
}