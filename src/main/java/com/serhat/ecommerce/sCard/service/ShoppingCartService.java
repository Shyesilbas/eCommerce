package com.serhat.ecommerce.sCard.service;

import com.serhat.ecommerce.dto.object.CardProductDto;
import com.serhat.ecommerce.dto.response.AddedToCardResponse;
import com.serhat.ecommerce.dto.response.QuantityUpdateResponse;
import com.serhat.ecommerce.dto.response.ShoppingCardInfo;
import com.serhat.ecommerce.product.entity.Product;
import com.serhat.ecommerce.sCard.entity.ShoppingCard;
import com.serhat.ecommerce.user.userS.entity.User;

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
