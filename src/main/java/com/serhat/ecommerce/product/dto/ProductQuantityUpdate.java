package com.serhat.ecommerce.product.dto;

public record ProductQuantityUpdate(
        String productName,
        String productCode,
        int newQuantity
) {
}
