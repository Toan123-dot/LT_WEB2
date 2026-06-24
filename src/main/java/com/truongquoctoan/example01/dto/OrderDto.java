package com.truongquoctoan.example01.dto;

import lombok.*;
import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderDto {
    private Long id;
    private Long tableId;
    private Long employeeId;
    private Long userId;
    private String status;
    private BigDecimal totalAmount;
    private Long promotionId;
    private String notes;
}
