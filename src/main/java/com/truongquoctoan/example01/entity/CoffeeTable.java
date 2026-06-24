package com.truongquoctoan.example01.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "coffee_tables") // Đã sửa từ "tables" tránh trùng từ khóa hệ thống
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CoffeeTable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer number;
    private Integer capacity;

    @Enumerated(EnumType.STRING)
    private TableStatus status = TableStatus.FREE;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public enum TableStatus {
        FREE, OCCUPIED, RESERVED
    }
}