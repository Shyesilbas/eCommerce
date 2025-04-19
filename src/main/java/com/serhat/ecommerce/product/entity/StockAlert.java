package com.serhat.ecommerce.product.entity;

import com.serhat.ecommerce.product.enums.StockAlertType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "stock_alert")
public class StockAlert {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String notes;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    private int currentQuantity;

    private LocalDateTime alertDate;


    @Enumerated(EnumType.STRING)
    private StockAlertType alertType;

    @PrePersist
    public void prePersist() {
        this.alertDate = LocalDateTime.now();
    }

}