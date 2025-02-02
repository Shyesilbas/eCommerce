package com.serhat.security.service;

import com.serhat.security.dto.object.CardProductDto;
import com.serhat.security.dto.response.AddedToCardResponse;
import com.serhat.security.dto.response.QuantityUpdateResponse;
import com.serhat.security.dto.response.TotalInfo;
import com.serhat.security.entity.Product;
import com.serhat.security.entity.User;
import com.serhat.security.entity.ShoppingCard;
import com.serhat.security.exception.EmptyShoppingCardException;
import com.serhat.security.exception.InvalidQuantityException;
import com.serhat.security.exception.ProductNotFoundException;
import com.serhat.security.exception.ProductNotFoundInCardException;
import com.serhat.security.interfaces.TokenInterface;
import com.serhat.security.repository.ProductRepository;
import com.serhat.security.repository.ShoppingCardRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ShoppingCardService {
    private final ShoppingCardRepository shoppingCardRepository;
    private final TokenInterface tokenInterface;
    private final ProductRepository productRepository;

    public List<CardProductDto> getShoppingCardByUser(HttpServletRequest servletRequest) {
        User user = tokenInterface.getUserFromToken(servletRequest);

        List<ShoppingCard> shoppingCards = shoppingCardRepository.findByUser(user);

        if (shoppingCards.isEmpty()) {
            throw new EmptyShoppingCardException("Shopping Card is empty!");
        }

        return shoppingCards.stream()
                .map(this::convertToCardProductDto)
                .collect(Collectors.toList());
    }

    private CardProductDto convertToCardProductDto(ShoppingCard shoppingCard) {
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

    @Transactional
    public AddedToCardResponse addToCard(HttpServletRequest servletRequest, Long productId) {
        User user = tokenInterface.getUserFromToken(servletRequest);
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException("Product not found"));

        if (!shoppingCardRepository.existsByUserAndProduct(user, product)) {
            ShoppingCard shoppingCard = ShoppingCard.builder()
                    .user(user)
                    .product(product)
                    .addedAt(LocalDate.now())
                    .quantity(1)
                    .build();
            shoppingCardRepository.save(shoppingCard);
            log.info("Product {} added to shopping card for user {}", productId, user.getUsername());
        }

        return new AddedToCardResponse(
                product.getName(),
                product.getBrand(),
                product.getProductCode(),
                product.getPrice()
        );
    }

    @Transactional
    public QuantityUpdateResponse handleQuantity(HttpServletRequest servletRequest, Long productId, int quantity) {
        if (quantity < 0) {
            throw new InvalidQuantityException("Quantity cannot be negative!");
        }

        User user = tokenInterface.getUserFromToken(servletRequest);
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException("Product not found"));

        ShoppingCard shoppingCard = shoppingCardRepository.findByUserAndProduct(user, product)
                .orElseThrow(() -> new ProductNotFoundInCardException("Product not found in shopping card"));

        if (quantity == 0) {
            shoppingCardRepository.delete(shoppingCard);
            log.info("Product {} removed from shopping card for user {}", productId, user.getUsername());
        } else {
            shoppingCard.setQuantity(quantity);
            shoppingCardRepository.save(shoppingCard);
            log.info("Product {} quantity updated to {} for user {}", productId, shoppingCard.getQuantity(), user.getUsername());
        }

        return new QuantityUpdateResponse(
                product.getName(),
                product.getProductCode(),
                quantity
        );
    }


    public BigDecimal totalPrice(HttpServletRequest request) {
        User user = tokenInterface.getUserFromToken(request);

        boolean isCardEmpty = shoppingCardRepository.count() == 0;
        if (isCardEmpty) {
            throw new EmptyShoppingCardException("No product in the card!");
        }

        return shoppingCardRepository.findByUser(user)
                .stream()
                .map(shoppingCard -> shoppingCard.getProduct().getPrice().multiply(new BigDecimal(shoppingCard.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }


    public long totalProduct(HttpServletRequest request) {
        User user = tokenInterface.getUserFromToken(request);

        List<ShoppingCard> userCards = shoppingCardRepository.findByUser(user);
        if (userCards.isEmpty()) {
            throw new EmptyShoppingCardException("No products in the card!");
        }

        return userCards.size();
    }

    public TotalInfo calculateTotalInfo(HttpServletRequest request) {
        User user = tokenInterface.getUserFromToken(request);

        List<ShoppingCard> shoppingCards = shoppingCardRepository.findByUser(user);

        BigDecimal totalPrice = shoppingCards.stream()
                .map(shoppingCard -> shoppingCard.getProduct().getPrice().multiply(new BigDecimal(shoppingCard.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        long totalItems = shoppingCards.stream()
                .mapToLong(ShoppingCard::getQuantity)
                .sum();

        long totalQuantity = shoppingCards.size();

        return new TotalInfo(totalPrice, totalItems, totalQuantity);
    }

    @Transactional
    public void removeFromCard(HttpServletRequest servletRequest, Long productId) {
        User user = tokenInterface.getUserFromToken(servletRequest);
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException("Product not found"));

        shoppingCardRepository.deleteByUserAndProduct(user, product);
        log.info("Product {} removed from shopping card for user {}", productId, user.getUsername());
    }
}