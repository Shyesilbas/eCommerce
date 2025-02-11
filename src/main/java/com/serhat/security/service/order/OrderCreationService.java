package com.serhat.security.service.order;

import com.serhat.security.dto.request.OrderRequest;
import com.serhat.security.dto.response.*;
import com.serhat.security.entity.*;
import com.serhat.security.entity.enums.*;
import com.serhat.security.exception.*;
import com.serhat.security.interfaces.*;
import com.serhat.security.mapper.OrderMapper;
import com.serhat.security.repository.*;
import com.serhat.security.service.*;
import com.serhat.security.service.ProductService;
import com.serhat.security.service.DiscountCodeService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@EnableScheduling
public class OrderCreationService implements OrderCreationInterface {

    private final OrderRepository orderRepository;
    private final ProductService productService;
    private final PaymentService paymentService;
    private final OrderMapper orderMapper;
    private final DiscountCodeService discountService;
    private final NotificationService notificationService;
    private final StockService stockService;
    private final ShoppingCardService shoppingCardService;
    private final OrderCreationValidation orderCreationValidation;

    public User validateAndGetUser(HttpServletRequest request, OrderRequest orderRequest) {
        return orderCreationValidation.validateAndGetUser(request, orderRequest);
    }

    @Override
    public void updateUserBonusPoints(User user, BigDecimal bonusPoints) {
        OrderCreationInterface.super.updateUserBonusPoints(user, bonusPoints);
    }
    @Override
    public Order findOrderById(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(()-> new OrderNotFoundException("Order not found"));
    }

    public void updateProductStock(Product product, int quantity) {
        stockService.validateAndUpdateProductStock(product, quantity);
    }

    @Override
    public PriceDetails calculatePriceDetails(List<ShoppingCard> shoppingCards , User user , OrderRequest orderRequest){
        return paymentService.calculatePriceDetails(shoppingCards, user, orderRequest);
    }

    @Transactional
    @Override
    public OrderResponse createOrder(HttpServletRequest request, OrderRequest orderRequest) {
        User user = validateAndGetUser(request, orderRequest);
        paymentService.findWalletForUser(user);
        List<ShoppingCard> shoppingCards = shoppingCardService.findShoppingCard(user);

        PriceDetails priceDetails = calculatePriceDetails(shoppingCards, user, orderRequest);
        Order order = orderMapper.createOrderEntity(user, orderRequest, priceDetails);
        order = orderRepository.save(order);
        initializeOrderItemsAndTransactions(order, shoppingCards);

        finalizeOrder(order, user, shoppingCards, request);
        return orderMapper.toOrderResponse(order);
    }

    private void initializeOrderItemsAndTransactions(Order order, List<ShoppingCard> shoppingCards) {
        List<OrderItem> orderItems = orderMapper.convertShoppingCartToOrderItems(order, shoppingCards);
        List<Transaction> transactions = paymentService.createOrderTransactions(order);

        order.setOrderItems(orderItems);
        order.setTransactions(transactions);
        orderItems.forEach(item -> updateProductStock(item.getProduct(), item.getQuantity()));
    }

    private void finalizeOrder(Order order, User user, List<ShoppingCard> shoppingCards, HttpServletRequest request) {
        saveOrderAndUpdateUser(order, user);
        clearShoppingCart(shoppingCards);
        saveUpdatedProducts(order.getOrderItems());
        handleDiscountCode(order,request,order.getDiscountCode());
        notificationService.addOrderNotification(user, order, NotificationTopic.ORDER_PLACED);
    }

    private void handleDiscountCode(Order order, HttpServletRequest request,DiscountCode discountCode) {
        discountService.handleDiscountCode(order,discountCode,request);
    }

    public void saveOrderAndUpdateUser(Order order, User user) {
        orderRepository.save(order);
        updateUserTotalFees(user);
        user.setTotalOrders(user.getTotalOrders() + 1);
    }
    @Override
    public void updateUserTotalFees(User user) {
        OrderCreationInterface.super.updateUserTotalFees(user);
    }

    private void clearShoppingCart(List<ShoppingCard> shoppingCards) {
        shoppingCardService.clearShoppingCart(shoppingCards);
    }

    private void saveUpdatedProducts(List<OrderItem> orderItems) {
        productService.updateProductsAfterOrder(orderItems);
    }

}
