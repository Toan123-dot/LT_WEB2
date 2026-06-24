package com.truongquoctoan.example01.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class BillDTO {
    private Long id;
    private Long orderId;
    private BigDecimal totalAmount;
    private String paymentMethod;
    private String paymentStatus;
    private LocalDateTime issuedAt;
    private String notes;
}
