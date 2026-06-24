package com.truongquoctoan.example01.dto;

import lombok.*;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PromotionProductDto {
    private Long id;
    private Long promotionId;
    private Long productId;
    private BigDecimal discountPercent;
}
