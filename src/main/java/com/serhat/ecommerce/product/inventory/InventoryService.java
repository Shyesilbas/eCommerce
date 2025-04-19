package com.serhat.ecommerce.product.inventory;

import com.serhat.ecommerce.product.dto.ProductQuantityUpdate;
import com.serhat.ecommerce.order.entity.OrderItem;
import com.serhat.ecommerce.product.entity.Product;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

public interface InventoryService {
    ProductQuantityUpdate updateProductQuantity(Long productId, int quantity, HttpServletRequest request);
    void updateProductStockAfterOrder(Product product, int quantity);
    void updateProductStockAfterOrderCancellation(Product product, int quantity);
    void updateProductsAfterOrder(List<OrderItem> orderItems);
    void validateAndUpdateProductStock(Product product, int requestedQuantity);
    void updateStockForOrderItems(List<OrderItem> orderItems);
}
