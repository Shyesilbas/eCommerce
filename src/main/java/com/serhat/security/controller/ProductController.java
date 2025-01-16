package com.serhat.security.controller;

import com.serhat.security.dto.object.ProductDto;
import com.serhat.security.dto.request.ProductRequest;
import com.serhat.security.dto.response.ProductResponse;
import com.serhat.security.service.ProductService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/products")
public class ProductController {
    private final ProductService productService;


    @GetMapping("/info/{productCode}")
    public ResponseEntity<ProductDto> productInfo(@PathVariable String productCode){
        return ResponseEntity.ok(productService.productInfo(productCode));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/addProduct")
    public ResponseEntity<ProductResponse> addProduct(@RequestBody ProductRequest request , HttpServletRequest servletRequest){
        return ResponseEntity.ok(productService.addProduct(request, servletRequest));
    }
}
