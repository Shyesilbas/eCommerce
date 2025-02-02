package com.serhat.security.entity;

import com.serhat.security.entity.enums.StockStatus;
import com.serhat.security.entity.enums.Category;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long productId;

    @Column(name = "name",nullable = false)
    private String name;
    @Column(name = "origin_of_country",nullable = false)
    private String originOfCountry;
    @Column(name = "product_code",nullable = false,unique = true)
    private String productCode;
    @Column(name = "description",nullable = false,unique = false)
    private String description;
    @Column(name = "price",nullable = false)
    private BigDecimal price;
    @Column(name = "brand")
    private String brand;
    @Column(name = "average_rating")
    private BigDecimal averageRating;
    @Enumerated(EnumType.STRING)
    private StockStatus stockStatus;
    @Column(name = "color")
    private String color;
    @Column(name = "quantity",nullable = false)
    private int quantity;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PriceHistory> priceHistoryList = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    private Category category;


}
