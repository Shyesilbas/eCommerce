package com.serhat.ecommerce.discount.discountService.entity;

import com.serhat.ecommerce.discount.enums.CouponStatus;
import com.serhat.ecommerce.discount.enums.DiscountRate;
import com.serhat.ecommerce.user.userS.entity.User;
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

    @PrePersist
    public void prePersist() {
        this.code = UUID.randomUUID().toString();
        this.createdAt = LocalDateTime.now();
        this.expiresAt = this.createdAt.plusMonths(1);
    }

}
