package com.serhat.ecommerce.product.mapper;

import com.serhat.ecommerce.product.dto.BestSellerProductDTO;
import com.serhat.ecommerce.product.dto.ProductDto;
import com.serhat.ecommerce.product.dto.ProductRequest;
import com.serhat.ecommerce.product.entity.Product;
import com.serhat.ecommerce.product.enums.Category;
import com.serhat.ecommerce.product.enums.StockStatus;
import org.springframework.stereotype.Component;

@Component
public class ProductMapper {

    public Product mapToProduct(ProductRequest productRequest) {
        return Product.builder()
                .name(productRequest.name())
                .originOfCountry(productRequest.originOfCountry())
                .productCode(productRequest.productCode())
                .description(productRequest.description())
                .price(productRequest.price())
                .brand(productRequest.brand())
                .averageRating(productRequest.averageRating())
                .stockStatus(StockStatus.valueOf(productRequest.stockStatus()))
                .color(productRequest.color())
                .quantity(productRequest.quantity())
                .category(Category.valueOf(productRequest.category()))
                .isReturnable(productRequest.isReturnable())
                .build();
    }

    public ProductDto mapToProductDto(Product product){
        return new ProductDto(
                product.getProductId(),
                product.getName(),
                product.getOriginOfCountry(),
                product.getProductCode(),
                product.getDescription(),
                product.getPrice(),
                product.getBrand(),
                product.getAverageRating(),
                product.getStockStatus(),
                product.getColor(),
                product.getQuantity(),
                product.getCategory(),
                product.isReturnable()
        );
    }

    public BestSellerProductDTO mapToBestSeller(Product product){
        return new BestSellerProductDTO(
                product.getName(),
                product.getOriginOfCountry(),
                product.getProductCode(),
                product.getDescription(),
                product.getPrice(),
                product.getBrand(),
                product.getColor(),
                product.getCategory()
        );

    }

}
