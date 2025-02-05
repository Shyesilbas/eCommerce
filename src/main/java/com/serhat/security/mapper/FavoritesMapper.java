package com.serhat.security.mapper;

import com.serhat.security.dto.object.FavoriteProductDto;
import com.serhat.security.entity.Favorites;
import com.serhat.security.entity.Product;
import org.springframework.stereotype.Component;

@Component
public class FavoritesMapper {

    public FavoriteProductDto mapToFavoriteProductDto(Favorites favorite){
        Product product = favorite.getProduct();
        return FavoriteProductDto.builder()
                .productId(product.getProductId())
                .productCode(product.getProductCode())
                .name(product.getName())
                .price(product.getPrice())
                .description(product.getDescription())
                .brand(product.getBrand())
                .color(product.getColor())
                .category(product.getCategory())
                .averageRating(product.getAverageRating())
                .favorite_since(favorite.getAddedAt())
                .isFavorite(favorite.isFavorite())
                .build();



    }
}
