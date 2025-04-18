package com.serhat.ecommerce.order.creation;

import com.serhat.ecommerce.order.OrderMapper;
import com.serhat.ecommerce.dto.request.OrderRequest;
import com.serhat.ecommerce.dto.response.PriceDetails;
import com.serhat.ecommerce.order.Order;
import com.serhat.ecommerce.order.OrderItem;
import com.serhat.ecommerce.order.finalize.OrderFinalizationService;
import com.serhat.ecommerce.sCard.entity.ShoppingCard;
import com.serhat.ecommerce.user.userS.entity.User;
import com.serhat.ecommerce.order.OrderRepository;
import com.serhat.ecommerce.payment.service.PaymentProcessingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderProcessingService {
    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final OrderFinalizationService finalizeOrderService;
    private final PaymentProcessingService paymentProcessingService;

    @Transactional
    public Order processOrder(User user, OrderRequest orderRequest, List<ShoppingCard> shoppingCards, PriceDetails priceDetails) {
        Order order = orderMapper.createOrderEntity(user, orderRequest, priceDetails);
        List<OrderItem> orderItems = orderMapper.convertShoppingCartToOrderItems(order, shoppingCards);
        order.setOrderItems(orderItems);
        order = orderRepository.save(order);
        return order;
    }

    public void completeOrder(Order order, User user, List<ShoppingCard> shoppingCards) {
        paymentProcessingService.processPayment(order, order.getPaymentMethod()); // transaction will be created from here
        finalizeOrderService.finalizeOrder(order, user, shoppingCards);
    }
}