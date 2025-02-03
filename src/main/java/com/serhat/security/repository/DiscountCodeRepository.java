package com.serhat.security.repository;

import com.serhat.security.entity.DiscountCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DiscountCodeRepository extends JpaRepository<DiscountCode,Long> {
    Optional<DiscountCode> findByCode(String code);

    List<DiscountCode> findByUser_UserId(Long userId);
}
