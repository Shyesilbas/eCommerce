package com.serhat.ecommerce.order.creation;

import com.serhat.ecommerce.order.dto.request.OrderRequest;
import com.serhat.ecommerce.sCard.entity.ShoppingCard;
import com.serhat.ecommerce.user.userS.entity.User;
import com.serhat.ecommerce.sCard.service.ShoppingCartService;
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
