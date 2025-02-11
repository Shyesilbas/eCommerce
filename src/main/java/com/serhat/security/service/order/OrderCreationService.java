package com.serhat.security.service.order;

import com.serhat.security.dto.request.OrderRequest;
import com.serhat.security.dto.response.*;
import com.serhat.security.entity.*;
import com.serhat.security.exception.*;
import com.serhat.security.interfaces.*;
import com.serhat.security.mapper.OrderMapper;
import com.serhat.security.repository.*;
import com.serhat.security.service.*;
import com.serhat.security.service.ProductService;
import com.serhat.security.service.DiscountCodeService;
import com.serhat.security.interfaces.OrderCreationInterface;
import com.serhat.security.interfaces.WalletInterface;
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
    private final PaymentServiceInterface paymentServiceInterface;
    private final OrderMapper orderMapper;
    private final DiscountCodeService discountService;
    private final NotificationInterface notificationInterface;
    private final StockInterface stockInterface;
    private final ShoppingCardService shoppingCardService;
    private final OrderCreationValidation orderCreationValidation;
    private final WalletInterface walletInterface;

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
        stockInterface.validateAndUpdateProductStock(product, quantity);
    }

    @Override
    public PriceDetails calculatePriceDetails(List<ShoppingCard> shoppingCards , User user , OrderRequest orderRequest){
        return paymentServiceInterface.calculatePriceDetails(shoppingCards, user, orderRequest);
    }

    public Wallet getUsersWallet(User user){
        return walletInterface.getWalletByUser(user);
    }

    @Transactional
    @Override
    public OrderResponse createOrder(HttpServletRequest request, OrderRequest orderRequest) {
        User user = validateAndGetUser(request, orderRequest);
        getUsersWallet(user);
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
        List<Transaction> transactions = paymentServiceInterface.createOrderTransactions(order);

        order.setOrderItems(orderItems);
        order.setTransactions(transactions);
        orderItems.forEach(item -> updateProductStock(item.getProduct(), item.getQuantity()));
    }

    private void finalizeOrder(Order order, User user, List<ShoppingCard> shoppingCards, HttpServletRequest request) {
        saveOrderAndUpdateUserSendNotification(order, user);
        clearShoppingCart(shoppingCards);
        saveUpdatedProducts(order.getOrderItems());
        handleDiscountCode(order,request,order.getDiscountCode());
    }

    public void addOrderNotification(User user , Order order ){
        notificationInterface.addOrderCreationNotification(user, order);
    }

    private void handleDiscountCode(Order order, HttpServletRequest request,DiscountCode discountCode) {
        discountService.handleDiscountCode(order,discountCode,request);
    }

    public void saveOrderAndUpdateUserSendNotification(Order order, User user) {
        orderRepository.save(order);
        updateUserTotalFees(user);
        user.setTotalOrders(user.getTotalOrders() + 1);
        addOrderNotification(user,order);
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
