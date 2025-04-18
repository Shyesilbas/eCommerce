package com.serhat.ecommerce.dto.response;

public record ProductQuantityUpdate(
        String productName,
        String productCode,
        int newQuantity
) {
}
