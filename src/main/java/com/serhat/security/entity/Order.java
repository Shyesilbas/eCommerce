package com.serhat.security.entity;

import com.serhat.security.entity.enums.DiscountRate;
import com.serhat.security.entity.enums.OrderStatus;
import com.serhat.security.entity.enums.PaymentMethod;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;


    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<OrderItem> orderItems;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Transaction> transactions;

    @OneToOne
    @JoinColumn(name = "discount_id")
    private DiscountCode discountCode;


    @OneToOne
    @JoinColumn(name = "gift_card_id")
    private GiftCard giftCard;


    private LocalDateTime orderDate;

    private BigDecimal totalPrice;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;

    private String notes;

    private Long shippingAddressId;

    private LocalDateTime updatedAt;

    private BigDecimal shippingFee;
    private BigDecimal bonusWon;
    private BigDecimal totalDiscount;
    private BigDecimal totalPaid;
    private BigDecimal totalSaved;


    @Enumerated(EnumType.STRING)
    private DiscountRate discountRate;

    private Boolean isBonusPointUsed;
    private BigDecimal bonusPointsUsed;

    private boolean isOrderReturnable;
    @PreUpdate
    public void setUpdatedAt() {
        this.updatedAt = LocalDateTime.now();
    }

}
