package com.serhat.security.component.mapper;

import com.serhat.security.dto.object.CardProductDto;
import com.serhat.security.dto.response.AddedToCardResponse;
import com.serhat.security.dto.response.QuantityUpdateResponse;
import com.serhat.security.entity.Product;
import com.serhat.security.entity.ShoppingCard;
import com.serhat.security.entity.User;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

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

    public AddedToCardResponse convertToAddedToCardResponse(Product product) {
        return AddedToCardResponse.builder()
                .productName(product.getName())
                .brand(product.getBrand())
                .productCode(product.getProductCode())
                .price(product.getPrice())
                .isReturnable(product.isReturnable())
                .message("Orders with non-returnable items cannot be canceled. Please order returnable items separately.")
                .build();
    }

    public ShoppingCard convertToShoppingCard(User user, Product product) {
        return ShoppingCard.builder()
                .user(user)
                .product(product)
                .addedAt(LocalDate.now())
                .quantity(1)
                .build();

    }

    public QuantityUpdateResponse quantityUpdateResponse(Product product, int quantity){
        return new QuantityUpdateResponse(
                product.getName(),
                product.getProductCode(),
                quantity
        );
    }

}
