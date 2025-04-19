package com.serhat.ecommerce.product.inventory;

import com.serhat.ecommerce.product.entity.Product;
import com.serhat.ecommerce.product.entity.StockAlert;
import com.serhat.ecommerce.product.enums.StockAlertType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StockAlertRepository extends JpaRepository<StockAlert,Long> {
    List<StockAlert> findByProductAndAlertType(Product product, StockAlertType alertType);

    StockAlert findByProduct(Product updatedProduct);

    List<StockAlert> findByAlertType(StockAlertType alertType);
}
