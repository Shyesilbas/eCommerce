package com.serhat.security.service.order.creation;

import com.serhat.security.dto.request.OrderRequest;
import com.serhat.security.dto.response.OrderResponse;
import com.serhat.security.dto.response.PriceDetails;
import com.serhat.security.entity.*;
import com.serhat.security.entity.enums.PaymentMethod;
import com.serhat.security.component.mapper.OrderMapper;
import com.serhat.security.repository.OrderRepository;
import com.serhat.security.service.inventory.InventoryService;
import com.serhat.security.service.order.finalize.OrderFinalizationService;
import com.serhat.security.service.order.price.OrderPriceCalculationService;
import com.serhat.security.service.payment.PaymentProcessingService;
import com.serhat.security.service.sCard.ShoppingCardService;
import com.serhat.security.service.user.UserService; // Yeni bağımlılık
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderCreationServiceImpl implements OrderCreationService {

    private final OrderRepository orderRepository;
    private final PaymentProcessingService paymentProcessingService;
    private final OrderMapper orderMapper;
    private final InventoryService inventoryService;
    private final ShoppingCardService shoppingCardService;
    private final OrderCreationValidation orderCreationValidation;
    private final OrderFinalizationService finalizeOrder;
    private final OrderPriceCalculationService orderPriceCalculationService;

    @Transactional
    @Override
    @CacheEvict(value = "userInfoCache", key = "#request.userPrincipal.name")
    public OrderResponse createOrder(HttpServletRequest request, OrderRequest orderRequest) {
        User user = orderCreationValidation.validateAndGetUser(request, orderRequest);
        List<ShoppingCard> shoppingCards = shoppingCardService.findShoppingCard(user);

        PriceDetails priceDetails = orderPriceCalculationService.calculateOrderPrice(shoppingCards, user, orderRequest);
        Order order = orderMapper.createOrderEntity(user, orderRequest, priceDetails);
        order = orderRepository.save(order);
        initializeOrderItems(order, shoppingCards);
        processPayment(order, order.getPaymentMethod());

        finalizeOrder.finalizeOrder(order, user, shoppingCards, request);
        return orderMapper.toOrderResponse(order);
    }

    private void initializeOrderItems(Order order, List<ShoppingCard> shoppingCards) {
        List<OrderItem> orderItems = orderMapper.convertShoppingCartToOrderItems(order, shoppingCards);
        order.setOrderItems(orderItems);
        orderItems.forEach(item -> inventoryService.validateAndUpdateProductStock(item.getProduct(), item.getQuantity()));
    }

    private void processPayment(Order order, PaymentMethod paymentMethod) {
        paymentProcessingService.processPayment(order, paymentMethod);
    }
}