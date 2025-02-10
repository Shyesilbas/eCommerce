package com.serhat.security.interfaces;

import com.serhat.security.dto.object.CardProductDto;
import com.serhat.security.dto.response.AddedToCardResponse;
import com.serhat.security.dto.response.QuantityUpdateResponse;
import com.serhat.security.dto.response.ShoppingCardInfo;
import com.serhat.security.entity.Product;
import com.serhat.security.entity.ShoppingCard;
import com.serhat.security.entity.User;
import jakarta.servlet.http.HttpServletRequest;

import java.math.BigDecimal;
import java.util.List;

public interface ShoppingCardInterface {
    List<ShoppingCard> findShoppingCard(User user);

    BigDecimal calculateTotalPrice(List<ShoppingCard> shoppingCards);

    List<CardProductDto> getItems(HttpServletRequest servletRequest);

    Product findById(Long productId);

    ShoppingCard checkProductInShoppingCard(User user, Product product);

    AddedToCardResponse addToCard(HttpServletRequest servletRequest, Long productId);

    QuantityUpdateResponse handleQuantity(HttpServletRequest servletRequest, Long productId, int quantity);

    BigDecimal totalPrice(HttpServletRequest request);

    long totalProduct(HttpServletRequest request);

    long totalItems(HttpServletRequest request);

    ShoppingCardInfo getShoppingCardTotalInfo(HttpServletRequest request);

    void removeFromCard(HttpServletRequest servletRequest, Long productId);
    void clearShoppingCart(List<ShoppingCard> shoppingCards);
}
