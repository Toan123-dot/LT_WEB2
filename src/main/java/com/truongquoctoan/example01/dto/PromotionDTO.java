package com.truongquoctoan.example01.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class PromotionDTO {
    private Long id;
    private String name;
    private BigDecimal discountPercentage;
    private BigDecimal discountAmount;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Boolean isActive;
}
