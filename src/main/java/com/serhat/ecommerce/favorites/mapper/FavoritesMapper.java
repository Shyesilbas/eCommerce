package com.serhat.ecommerce.favorites.mapper;

import com.serhat.ecommerce.dto.object.FavoriteProductDto;
import com.serhat.ecommerce.favorites.entity.Favorites;
import com.serhat.ecommerce.product.entity.Product;
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
