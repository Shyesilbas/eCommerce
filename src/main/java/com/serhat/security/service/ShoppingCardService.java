package com.serhat.security.service;

import com.serhat.security.dto.object.CardProductDto;
import com.serhat.security.dto.response.AddedToCardResponse;
import com.serhat.security.dto.response.QuantityUpdateResponse;
import com.serhat.security.dto.response.ShoppingCardInfo;
import com.serhat.security.entity.Product;
import com.serhat.security.entity.User;
import com.serhat.security.entity.ShoppingCard;
import com.serhat.security.exception.EmptyShoppingCardException;
import com.serhat.security.exception.InvalidQuantityException;
import com.serhat.security.exception.ProductNotFoundException;
import com.serhat.security.exception.ProductNotFoundInCardException;
import com.serhat.security.interfaces.ShoppingCardInterface;
import com.serhat.security.interfaces.TokenInterface;
import com.serhat.security.mapper.ShoppingCardMapper;
import com.serhat.security.repository.ProductRepository;
import com.serhat.security.repository.ShoppingCardRepository;
import com.serhat.security.service.discountService.DiscountCodeService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ShoppingCardService implements ShoppingCardInterface {
    private final ShoppingCardRepository shoppingCardRepository;
    private final TokenInterface tokenInterface;
    private final ProductRepository productRepository;
    private final ShoppingCardMapper shoppingCardMapper;
    private final DiscountCodeService discountService;

    @Override
    public List<ShoppingCard> findShoppingCard(User user) {
        List<ShoppingCard> shoppingCards = shoppingCardRepository.findByUser(user);
        if (shoppingCards.isEmpty()) {
            throw new EmptyShoppingCardException("Shopping Card is empty!");
        }
        return shoppingCards;
    }

    @Override
    public BigDecimal cardTotal(List<ShoppingCard> shoppingCards) {
        return shoppingCards.stream()
                .map(sc -> sc.getProduct().getPrice().multiply(new BigDecimal(sc.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    public List<CardProductDto> getItems(HttpServletRequest servletRequest) {
        User user = tokenInterface.getUserFromToken(servletRequest);
        List<ShoppingCard> shoppingCards = findShoppingCard(user);
        return shoppingCards.stream()
                .map(shoppingCardMapper::convertToCardProductDto)
                .collect(Collectors.toList());
    }

    @Override
    public Product findById(Long productId) {
        return Optional.ofNullable(productId)
                .flatMap(productRepository::findById)
                .orElseThrow(() -> productId == null
                        ? new IllegalArgumentException("Product ID cannot be null")
                        : new ProductNotFoundException("Product Not Found"));
    }

    @Override
    public ShoppingCard checkProductInShoppingCard(User user, Product product) {
        return shoppingCardRepository.findByUserAndProduct(user, product)
                .orElseThrow(() -> new ProductNotFoundInCardException("Product not found in shopping card"));
    }

    @Override
    @Transactional
    public AddedToCardResponse addToCard(HttpServletRequest servletRequest, Long productId) {
        User user = tokenInterface.getUserFromToken(servletRequest);
        Product product = findById(productId);

        if (!shoppingCardRepository.existsByUserAndProduct(user, product)) {
            ShoppingCard shoppingCard = shoppingCardMapper.convertToShoppingCard(user, product);
            shoppingCardRepository.save(shoppingCard);
        }

        return shoppingCardMapper.convertToAddedToCardResponse(product);
    }

    @Override
    @Transactional
    public QuantityUpdateResponse handleQuantity(HttpServletRequest servletRequest, Long productId, int quantity) {
        if (quantity < 0) {
            throw new InvalidQuantityException("Quantity cannot be negative!");
        }
        User user = tokenInterface.getUserFromToken(servletRequest);
        Product product = findById(productId);
        ShoppingCard shoppingCard = checkProductInShoppingCard(user, product);

        if (quantity == 0) {
            shoppingCardRepository.delete(shoppingCard);
            log.info("Product {} removed from shopping card for user {}", productId, user.getUsername());
        } else {
            shoppingCard.setQuantity(quantity);
            shoppingCardRepository.save(shoppingCard);
            log.info("Product {} quantity updated to {} for user {}", productId, shoppingCard.getQuantity(), user.getUsername());
        }
        return shoppingCardMapper.quantityUpdateResponse(product, quantity);
    }

    @Override
    public BigDecimal totalPrice(HttpServletRequest request) {
        User user = tokenInterface.getUserFromToken(request);
        List<ShoppingCard> shoppingCards = findShoppingCard(user);
        return shoppingCards
                .stream()
                .map(shoppingCard -> shoppingCard.getProduct().getPrice().multiply(new BigDecimal(shoppingCard.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    public long totalProduct(HttpServletRequest request) {
        User user = tokenInterface.getUserFromToken(request);
        List<ShoppingCard> shoppingCards = findShoppingCard(user);
        return shoppingCards.size();
    }

    @Override
    public long totalItems(HttpServletRequest request) {
        User user = tokenInterface.getUserFromToken(request);
        List<ShoppingCard> shoppingCards = findShoppingCard(user);

        return shoppingCards.stream()
                .mapToLong(ShoppingCard::getQuantity)
                .sum();
    }

    @Override
    public ShoppingCardInfo getShoppingCardTotalInfo(HttpServletRequest request) {
        User user = tokenInterface.getUserFromToken(request);
        List<ShoppingCard> shoppingCards = findShoppingCard(user);

        List<CardProductDto> shoppingCardItems = shoppingCards.stream()
                .map(shoppingCardMapper::convertToCardProductDto)
                .collect(Collectors.toList());

        BigDecimal totalPrice = totalPrice(request);
        BigDecimal leftToDiscountCode = discountService.getDiscountThreshold().subtract(totalPrice);

        String message;
        if (totalPrice.compareTo(discountService.getDiscountThreshold()) < 0) {
            message = "Add " + leftToDiscountCode + " to obtain a discount code!";
        } else {
            message = "You will obtain a discount code because cart total exceeds the order threshold";
        }

        long totalItems = totalItems(request);
        long totalQuantity = shoppingCards.size();

        return new ShoppingCardInfo(totalPrice, totalItems, totalQuantity, message, shoppingCardItems);
    }

    @Override
    @Transactional
    public void removeFromCard(HttpServletRequest servletRequest, Long productId) {
        User user = tokenInterface.getUserFromToken(servletRequest);
        Product product = findById(productId);
        ShoppingCard shoppingCard = checkProductInShoppingCard(user, product);
        shoppingCardRepository.delete(shoppingCard);
        log.info("Product {} removed from shopping card for user {}", productId, user.getUsername());
    }

    @Override
    public void clearShoppingCart(List<ShoppingCard> shoppingCards) {
        shoppingCardRepository.deleteAll(shoppingCards);
    }
}
