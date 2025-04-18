package com.serhat.ecommerce.product.repository;

import com.serhat.ecommerce.product.entity.PriceHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface PriceHistoryRepository extends JpaRepository<PriceHistory,Long> {
    Page<PriceHistory> findByProduct_ProductIdOrderByChangeDateDesc(Long productId, Pageable pageable);

    Page<PriceHistory> findByProduct_ProductIdAndChangeDateBetween(Long productId, LocalDateTime start, LocalDateTime end, Pageable pageable);

    PriceHistory findFirstByProduct_ProductIdOrderByChangeDateAsc(Long productId);
}
