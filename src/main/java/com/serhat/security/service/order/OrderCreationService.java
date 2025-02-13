package com.serhat.security.service.order;

import com.serhat.security.dto.request.OrderRequest;
import com.serhat.security.dto.response.*;
import com.serhat.security.entity.*;
import com.serhat.security.entity.enums.PaymentMethod;
import com.serhat.security.exception.*;
import com.serhat.security.interfaces.*;
import com.serhat.security.mapper.OrderMapper;
import com.serhat.security.repository.*;
import com.serhat.security.service.*;
import com.serhat.security.interfaces.OrderCreationInterface;
import com.serhat.security.interfaces.WalletInterface;
import com.serhat.security.service.payment.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@EnableScheduling
public class OrderCreationService implements OrderCreationInterface  {

    private final OrderRepository orderRepository;
    private final PaymentProcessingService paymentProcessingService;
    private final OrderMapper orderMapper;
    private final StockInterface stockInterface;
    private final ShoppingCardService shoppingCardService;
    private final OrderCreationValidation orderCreationValidation;
    private final OrderFinalizationService finalizeOrder;
    private final OrderPriceCalculationService orderPriceCalculationService;

    public User validateAndGetUser(HttpServletRequest request, OrderRequest orderRequest) {
        return orderCreationValidation.validateAndGetUser(request, orderRequest);
    }

    public void updateProductStock(Product product, int quantity) {
        stockInterface.validateAndUpdateProductStock(product, quantity);
    }

    @Override
    public PriceDetails calculatePriceDetails(List<ShoppingCard> shoppingCards , User user , OrderRequest orderRequest){
        return orderPriceCalculationService.calculateOrderPrice(shoppingCards, user, orderRequest);
    }

    @Transactional
    @Override
    @CacheEvict(value = "userInfoCache", key = "#request.userPrincipal.name")
    public OrderResponse createOrder(HttpServletRequest request, OrderRequest orderRequest) {

        User user = validateAndGetUser(request, orderRequest);
        List<ShoppingCard> shoppingCards = shoppingCardService.findShoppingCard(user);

        PriceDetails priceDetails = calculatePriceDetails(shoppingCards, user, orderRequest);
        Order order = orderMapper.createOrderEntity(user, orderRequest, priceDetails);
        order = orderRepository.save(order);
        initializeOrderItems(order, shoppingCards);
        processPayment(order, order.getPaymentMethod());

        finalizeOrder(order, user, shoppingCards, request);
        return orderMapper.toOrderResponse(order);
    }

    private void initializeOrderItems(Order order, List<ShoppingCard> shoppingCards) {
        List<OrderItem> orderItems = orderMapper.convertShoppingCartToOrderItems(order, shoppingCards);
        order.setOrderItems(orderItems);
        orderItems.forEach(item -> updateProductStock(item.getProduct(), item.getQuantity()));
    }

    private void processPayment(Order order, PaymentMethod paymentMethod) {
        paymentProcessingService.processPayment(order,paymentMethod);
    }

    private void finalizeOrder(Order order, User user, List<ShoppingCard> shoppingCards, HttpServletRequest request) {
        finalizeOrder.finalizeOrder(order,user,shoppingCards,request);
    }


}
