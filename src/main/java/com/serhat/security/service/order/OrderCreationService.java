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
import com.serhat.security.service.discountService.DiscountCodeService;
import com.serhat.security.interfaces.OrderCreationInterface;
import com.serhat.security.interfaces.WalletInterface;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@EnableScheduling
public class OrderCreationService implements OrderCreationInterface {

    private final OrderRepository orderRepository;
    private final PaymentServiceInterface paymentServiceInterface;
    private final OrderMapper orderMapper;
    private final StockInterface stockInterface;
    private final ShoppingCardService shoppingCardService;
    private final OrderCreationValidation orderCreationValidation;
    private final WalletInterface walletInterface;
    private final OrderFinalizationService finalizeOrder;
    private final OrderPriceCalculationService orderPriceCalculationService;

    public User validateAndGetUser(HttpServletRequest request, OrderRequest orderRequest) {
        return orderCreationValidation.validateAndGetUser(request, orderRequest);
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
        return orderPriceCalculationService.calculateOrderPrice(shoppingCards, user, orderRequest);
    }

    public void getUsersWallet(User user){
         walletInterface.getWalletByUser(user);
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
        initializeOrderItems(order, shoppingCards);
        initializeTransactions(order);

        finalizeOrder(order, user, shoppingCards, request);
        return orderMapper.toOrderResponse(order);
    }

    private void initializeOrderItems(Order order, List<ShoppingCard> shoppingCards) {
        List<OrderItem> orderItems = orderMapper.convertShoppingCartToOrderItems(order, shoppingCards);
        order.setOrderItems(orderItems);
        orderItems.forEach(item -> updateProductStock(item.getProduct(), item.getQuantity()));
    }

    private void initializeTransactions(Order order) {
        List<Transaction> transactions = paymentServiceInterface.createOrderTransactions(order);
        order.setTransactions(transactions);
    }

    private void finalizeOrder(Order order, User user, List<ShoppingCard> shoppingCards, HttpServletRequest request) {
        finalizeOrder.finalizeOrder(order,user,shoppingCards,request);
    }


}
