package com.truongquoctoan.example01.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "order_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ✅ EAGER + JsonIgnoreProperties để tránh circular reference
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "order_id", nullable = false)
    @JsonIgnoreProperties({ "hibernateLazyInitializer", "handler", "items", "employee", "user" })
    private Order order;

    // ✅ EAGER để load Product + Category
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "product_id", nullable = false)
    @JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
    private Product product;

    private Integer quantity;
    private BigDecimal price;
    private BigDecimal subtotal;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        if (subtotal == null && price != null && quantity != null)
            this.subtotal = price.multiply(BigDecimal.valueOf(quantity));
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
        if (price != null && quantity != null)
            this.subtotal = price.multiply(BigDecimal.valueOf(quantity));
    }
}
