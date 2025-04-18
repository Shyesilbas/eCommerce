package com.serhat.ecommerce.order;

import com.serhat.ecommerce.product.inventory.InventoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderStockService {
    private final InventoryService inventoryService;

    public void updateStockAfterCancellation(Order order) {
        order.getOrderItems().forEach(orderItem ->
                inventoryService.updateProductStockAfterOrderCancellation(orderItem.getProduct(), orderItem.getQuantity()));
    }

    public void updateStock(List<OrderItem> orderItems) {
        inventoryService.updateStockForOrderItems(orderItems);
    }
}
