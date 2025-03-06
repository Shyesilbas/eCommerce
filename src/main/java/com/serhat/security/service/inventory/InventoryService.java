package com.serhat.security.service.inventory;

import com.serhat.security.dto.response.ProductQuantityUpdate;
import com.serhat.security.entity.OrderItem;
import com.serhat.security.entity.Product;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

public interface InventoryService {
    ProductQuantityUpdate updateProductQuantity(Long productId, int quantity, HttpServletRequest request);
    void updateProductStockAfterOrder(Product product, int quantity);
    void updateProductStockAfterOrderCancellation(Product product, int quantity);
    void updateProductsAfterOrder(List<OrderItem> orderItems);
    void validateAndUpdateProductStock(Product product, int requestedQuantity);;
}
