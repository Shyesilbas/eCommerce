package com.serhat.security.service.order.creation;

import com.serhat.security.dto.request.OrderRequest;
import com.serhat.security.dto.response.OrderResponse;
import com.serhat.security.dto.response.PriceDetails;
import com.serhat.security.entity.*;
import com.serhat.security.component.mapper.OrderMapper;
import com.serhat.security.service.order.OrderStockService;
import com.serhat.security.service.order.price.OrderPriceCalculationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Lazy
public class OrderCreationServiceImpl implements OrderCreationService {

    private final OrderValidationService orderValidationService;
    private final OrderPriceCalculationService orderPriceCalculationService;
    private final OrderProcessingService orderProcessingService;
    private final OrderMapper orderMapper;
    private final OrderStockService orderStockService;

    @Transactional
    @Override
    @CacheEvict(value = "userInfoCache", key = "T(org.springframework.security.core.context.SecurityContextHolder).getContext().getAuthentication().getName()")
    public OrderResponse createOrder(OrderRequest orderRequest) {

        OrderValidationService.ShoppingCartValidationResult validationResult = orderValidationService.validateUserAndGetShoppingCart(orderRequest);
        User user = validationResult.user();
        List<ShoppingCard> shoppingCards = validationResult.shoppingCards();

        PriceDetails priceDetails = orderPriceCalculationService.calculateOrderPrice(shoppingCards, user, orderRequest);
        Order order = orderProcessingService.processOrder(user, orderRequest, shoppingCards, priceDetails); // initializes and converts the order items
        orderStockService.updateStock(order.getOrderItems());

        orderProcessingService.completeOrder(order, user, shoppingCards);
        return orderMapper.toOrderResponse(order);
    }

}
