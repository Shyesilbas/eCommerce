package com.serhat.security.entity;

import com.serhat.security.entity.enums.CouponStatus;
import com.serhat.security.entity.enums.DiscountRate;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "discount_code")
public class DiscountCode {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String code;

    @Enumerated(EnumType.STRING)
    private DiscountRate discountRate;


    private LocalDateTime createdAt;

    private LocalDateTime expiresAt;

    @Enumerated(EnumType.STRING)
    private CouponStatus status = CouponStatus.NOT_USED;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public void markAsExpired() {
        this.status = CouponStatus.EXPIRED;
    }



    @PrePersist
    public void prePersist() {
        this.code = UUID.randomUUID().toString();
        this.createdAt = LocalDateTime.now();
        this.expiresAt = this.createdAt.plusMonths(1);
    }

}
