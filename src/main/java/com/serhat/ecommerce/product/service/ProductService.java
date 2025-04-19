package com.serhat.ecommerce.product.service;

import com.serhat.ecommerce.product.dto.BestSellerProductDTO;
import com.serhat.ecommerce.product.dto.ProductDto;
import com.serhat.ecommerce.product.dto.ProductRequest;
import com.serhat.ecommerce.product.dto.ProductPriceUpdate;
import com.serhat.ecommerce.product.dto.ProductResponse;
import com.serhat.ecommerce.product.enums.Category;
import com.serhat.ecommerce.product.entity.Product;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.domain.Page;

import java.math.BigDecimal;
import java.util.List;

public interface ProductService {
     ProductPriceUpdate updateProductPrice(Long productId, BigDecimal price, HttpServletRequest request);
     long totalProductCountByCategory(Category category);
     List<BestSellerProductDTO> bestSellersByCategory(Category category, int size);
     List<BestSellerProductDTO> bestSellers(int size);
     long totalProductCount();
     Product getProductById(Long productId);
     ProductDto productInfo(String productCode);
     ProductResponse addProduct(ProductRequest productRequest, HttpServletRequest request);
     Page<ProductDto> getFilteredProducts(BigDecimal minPrice, BigDecimal maxPrice, Category category, String brand, int page, int size);

}
