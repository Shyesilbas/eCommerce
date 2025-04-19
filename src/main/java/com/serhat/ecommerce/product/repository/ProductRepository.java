package com.serhat.ecommerce.product.repository;

import com.serhat.ecommerce.product.enums.Category;
import com.serhat.ecommerce.product.entity.Product;
import jakarta.annotation.Nonnull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product,Long> {
    Optional<Product> findByProductCode(String productCode);
    int countByCategory(Category category);

    Page<Product> findAll(@Nonnull Pageable pageable);

    Page<Product> findAll(Specification<Product> spec, Pageable pageable);
}
