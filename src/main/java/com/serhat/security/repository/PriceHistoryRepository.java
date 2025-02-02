package com.serhat.security.repository;

import com.serhat.security.entity.PriceHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PriceHistoryRepository extends JpaRepository<PriceHistory,Long> {
    List<PriceHistory> findByProduct_ProductIdOrderByChangeDateDesc(Long productId);

    List<PriceHistory> findByProduct_ProductIdAndChangeDateBetween(Long productId, LocalDateTime start, LocalDateTime end);

    PriceHistory findFirstByProduct_ProductIdOrderByChangeDateAsc(Long productId);
}
