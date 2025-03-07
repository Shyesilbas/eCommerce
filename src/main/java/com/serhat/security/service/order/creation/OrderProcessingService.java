package com.serhat.security.service.order.creation;

import com.serhat.security.component.mapper.OrderMapper;
import com.serhat.security.dto.request.OrderRequest;
import com.serhat.security.dto.response.PriceDetails;
import com.serhat.security.entity.Order;
import com.serhat.security.entity.OrderItem;
import com.serhat.security.entity.ShoppingCard;
import com.serhat.security.entity.User;
import com.serhat.security.repository.OrderRepository;
import com.serhat.security.service.order.finalize.OrderFinalizationService;
import com.serhat.security.service.payment.PaymentProcessingService;
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