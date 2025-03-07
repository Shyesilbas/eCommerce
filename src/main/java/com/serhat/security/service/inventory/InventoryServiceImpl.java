package com.serhat.security.service.inventory;

import com.serhat.security.dto.response.ProductQuantityUpdate;
import com.serhat.security.entity.OrderItem;
import com.serhat.security.entity.Product;
import com.serhat.security.entity.enums.Role;
import com.serhat.security.entity.enums.StockStatus;
import com.serhat.security.exception.InsufficientStockException;
import com.serhat.security.exception.InvalidAmountException;
import com.serhat.security.exception.InvalidQuantityException;
import com.serhat.security.jwt.TokenInterface;
import com.serhat.security.repository.ProductRepository;
import com.serhat.security.service.product.ProductService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class InventoryServiceImpl implements InventoryService{
    private final ProductRepository productRepository;
    private final StockAlertService stockAlertService;
    private final ProductService productService;
    private final TokenInterface tokenInterface;


    @Override
    public ProductQuantityUpdate updateProductQuantity(Long productId, int quantity, HttpServletRequest request) {
        tokenInterface.validateRole(request, Role.ADMIN, Role.MANAGER);
        Product product = productService.getProductById(productId);

        if (quantity < 0) {
            throw new InvalidAmountException("Product quantity cannot be negative");
        }
        if(quantity == 0){
            product.setStockStatus(StockStatus.OUT_OF_STOCKS);
        }
        if(quantity>0){
            product.setStockStatus(StockStatus.AVAILABLE);
        }

        product.setQuantity(quantity);
        productRepository.save(product);
        log.info("Product quantity updated: id {} -> {}", productId, quantity);
        manageStockAlerts(product.getProductId());
        return new ProductQuantityUpdate(product.getName(), product.getProductCode(), quantity);
    }

    public void manageStockAlerts(Long productId) {
        stockAlertService.handleStockAlert(productId);
    }

    @Override
    public void updateProductStockAfterOrder(Product product, int quantity) {
        int newQuantity = product.getQuantity() - quantity;
        if (newQuantity < 0) {
            throw new InvalidQuantityException("Quantity cannot be negative");
        }
        product.setQuantity(newQuantity);
        product.setStockStatus(newQuantity == 0 ? StockStatus.OUT_OF_STOCKS : StockStatus.AVAILABLE);
    }
    @Override
    public void updateProductStockAfterOrderCancellation(Product product, int quantity) {
        int newQuantity = product.getQuantity() + quantity;
        if (newQuantity < 0) {
            throw new InvalidQuantityException("Quantity cannot be negative");
        }
        product.setQuantity(newQuantity);
        product.setStockStatus(newQuantity == 0 ? StockStatus.OUT_OF_STOCKS : StockStatus.AVAILABLE);
    }

    @Override
    public void updateProductsAfterOrder(List<OrderItem> orderItems) {
        productRepository.saveAll(orderItems.stream()
                .map(OrderItem::getProduct)
                .collect(Collectors.toList()));
    }

    @Override
    public void validateAndUpdateProductStock(Product product, int requestedQuantity) {
        if (product.getQuantity() < requestedQuantity) {
            throw new InsufficientStockException("Insufficient stock for product: " + product.getName());
        }
        updateProductStockAfterOrder(product, requestedQuantity);
        stockAlertService.handleStockAlert(product.getProductId());
    }

    @Override
    @Transactional
    public void updateStockForOrderItems(List<OrderItem> orderItems) {
        if (orderItems == null || orderItems.isEmpty()) {
            log.warn("No order items provided for stock update");
            return;
        }

        for (OrderItem item : orderItems) {
            Product product = item.getProduct();
            int requestedQuantity = item.getQuantity();
            validateAndUpdateProductStock(product, requestedQuantity);
            log.info("Stock updated for product {}: requested quantity {}, new quantity {}",
                    product.getName(), requestedQuantity, product.getQuantity());
        }
        productRepository.saveAll(orderItems.stream()
                .map(OrderItem::getProduct)
                .collect(Collectors.toList()));
    }
}
