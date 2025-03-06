package com.serhat.security.service.order.finalize;

import com.serhat.security.entity.*;
import com.serhat.security.service.discountService.DiscountCodeService;
import com.serhat.security.service.notification.NotificationService;
import com.serhat.security.service.inventory.InventoryService;
import com.serhat.security.service.sCard.ShoppingCardService;
import com.serhat.security.repository.OrderRepository;
import com.serhat.security.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderFinalizationService {
    private final OrderRepository orderRepository;
    private final ShoppingCardService shoppingCardService;
    private final NotificationService notificationService;
    private final DiscountCodeService discountCodeService;
    private final UserService userService;
    private final InventoryService inventoryService;

    @Transactional
    public void finalizeOrder(Order order, User user, List<ShoppingCard> shoppingCards) {
        orderRepository.save(order);
        userService.updateUserAfterOrder(order, user);
        shoppingCardService.clearShoppingCart(shoppingCards);
        inventoryService.updateProductsAfterOrder(order.getOrderItems());
        discountCodeService.handleDiscountCode(order, order.getDiscountCode());
        notificationService.addOrderCreationNotification(user, order);
    }
}