package com.serhat.security.repository;

import com.serhat.security.entity.GiftCard;
import com.serhat.security.entity.User;
import com.serhat.security.entity.enums.CouponStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GiftCardRepository extends JpaRepository<GiftCard,Long> {
    Page<GiftCard> findByUserAndStatus(User user, CouponStatus couponStatus, Pageable pageable);
}
