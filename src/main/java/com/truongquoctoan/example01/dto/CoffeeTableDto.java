package com.truongquoctoan.example01.dto;

import com.truongquoctoan.example01.entity.CoffeeTable;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CoffeeTableDto {
    private Long id;
    private Integer number;
    private Integer capacity;
    private CoffeeTable.TableStatus status;
}
