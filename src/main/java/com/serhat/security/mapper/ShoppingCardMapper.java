package com.serhat.security.mapper;

import com.serhat.security.dto.object.CardProductDto;
import com.serhat.security.entity.Product;
import com.serhat.security.entity.ShoppingCard;
import org.springframework.stereotype.Component;

@Component
public class ShoppingCardMapper {
    public CardProductDto convertToCardProductDto(ShoppingCard shoppingCard) {
        Product product = shoppingCard.getProduct();
        return CardProductDto.builder()
                .productId(product.getProductId())
                .productCode(product.getProductCode())
                .name(product.getName())
                .price(product.getPrice())
                .description(product.getDescription())
                .brand(product.getBrand())
                .category(product.getCategory())
                .quantity(shoppingCard.getQuantity())
                .build();
    }
}
