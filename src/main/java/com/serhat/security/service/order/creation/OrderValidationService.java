package com.serhat.security.service.order.creation;

import com.serhat.security.dto.request.OrderRequest;
import com.serhat.security.entity.ShoppingCard;
import com.serhat.security.entity.User;
import com.serhat.security.service.sCard.ShoppingCartService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderValidationService {
    private final OrderCreationValidation orderCreationValidation;
    private final ShoppingCartService shoppingCartService;
    public record ShoppingCartValidationResult(User user, List<ShoppingCard> shoppingCards) {}

    public ShoppingCartValidationResult validateUserAndGetShoppingCart(OrderRequest orderRequest) {
        User user = orderCreationValidation.validateAndGetUser(orderRequest);
        List<ShoppingCard> shoppingCards = shoppingCartService.findShoppingCard(user);
        return new ShoppingCartValidationResult(user, shoppingCards);
    }
}
