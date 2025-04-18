package com.serhat.ecommerce.discount.giftCard.repository;

import com.serhat.ecommerce.discount.giftCard.entity.GiftCard;
import com.serhat.ecommerce.user.userS.entity.User;
import com.serhat.ecommerce.enums.CouponStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GiftCardRepository extends JpaRepository<GiftCard,Long> {
    Page<GiftCard> findByUserAndStatus(User user, CouponStatus couponStatus, Pageable pageable);
}
