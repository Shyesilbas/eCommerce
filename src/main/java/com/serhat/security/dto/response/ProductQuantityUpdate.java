package com.serhat.security.dto.response;

public record ProductQuantityUpdate(
        String productName,
        String productCode,
        int newQuantity
) {
}
