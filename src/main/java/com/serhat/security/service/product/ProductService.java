package com.serhat.security.service.product;

import com.serhat.security.dto.object.BestSellerProductDTO;
import com.serhat.security.dto.object.ProductDto;
import com.serhat.security.dto.request.ProductRequest;
import com.serhat.security.dto.response.ProductPriceUpdate;
import com.serhat.security.dto.response.ProductQuantityUpdate;
import com.serhat.security.dto.response.ProductResponse;
import com.serhat.security.entity.OrderItem;
import com.serhat.security.entity.Product;
import com.serhat.security.entity.enums.Category;
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
