package com.truongquoctoan.example01.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "orders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class Order {

    public enum Status {
        PENDING, CONFIRMED, PREPARING, SERVED, PAID, CANCELLED
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "table_id", nullable = false)
    @JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
    private CoffeeTable table;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "employee_id", nullable = true)
    @JsonIgnoreProperties({ "hibernateLazyInitializer", "handler", "password" })
    private User employee;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = true)
    @JsonIgnoreProperties({ "hibernateLazyInitializer", "handler", "password" })
    private User user;

    @Enumerated(EnumType.STRING)
    private Status status;

    private BigDecimal totalAmount;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "promotion_id")
    @JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
    private Promotion promotion;

    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        if (this.status == null)
            this.status = Status.PENDING;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}