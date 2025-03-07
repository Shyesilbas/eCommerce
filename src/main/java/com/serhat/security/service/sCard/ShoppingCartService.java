package com.serhat.security.service.sCard;

import com.serhat.security.dto.object.CardProductDto;
import com.serhat.security.dto.response.AddedToCardResponse;
import com.serhat.security.dto.response.QuantityUpdateResponse;
import com.serhat.security.dto.response.ShoppingCardInfo;
import com.serhat.security.entity.Product;
import com.serhat.security.entity.ShoppingCard;
import com.serhat.security.entity.User;

import java.math.BigDecimal;
import java.util.List;

public interface ShoppingCartService {
    List<ShoppingCard> findShoppingCard(User user);

    BigDecimal cardTotal(List<ShoppingCard> shoppingCards);

    List<CardProductDto> getItems();

    Product findById(Long productId);

    ShoppingCard checkProductInShoppingCard(User user, Product product);

    AddedToCardResponse addToCard(Long productId);

    QuantityUpdateResponse handleQuantity(Long productId, int quantity);

    BigDecimal totalPrice();

    long totalProduct();

    long totalItems();

    ShoppingCardInfo getShoppingCardTotalInfo();

    void removeFromCard(Long productId);
    void clearShoppingCart(List<ShoppingCard> shoppingCards);
}
