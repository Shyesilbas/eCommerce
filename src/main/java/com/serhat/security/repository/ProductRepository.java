package com.serhat.security.repository;

import com.serhat.security.entity.Product;
import com.serhat.security.entity.enums.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product,Long> {
    Optional<Product> findByProductCode(String productCode);
    int countByCategory(Category category);

    Page<Product> findAll(Pageable pageable);

    Page<Product> findByPriceBetween(BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable);

    Page<Product> findByPriceBetweenAndCategory(BigDecimal minPrice, BigDecimal maxPrice, Category category, Pageable pageable);

    Page<Product> findByPriceBetweenAndCategoryAndBrandIgnoreCase(BigDecimal minPrice, BigDecimal maxPrice, Category category, String brand, Pageable pageable);

    Page<Product> findByPriceBetweenAndBrandIgnoreCase(BigDecimal minPrice, BigDecimal maxPrice, String brand, Pageable pageable);

    Page<Product> findByCategory(Category category, Pageable pageable);

    Page<Product> findByBrandIgnoreCase(String brand, Pageable pageable);

    Page<Product> findByCategoryAndBrandIgnoreCase(Category category, String brand, Pageable pageable);

    Page<Product> findAll(Specification<Product> spec, Pageable pageable);
}
