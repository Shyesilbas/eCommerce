package com.serhat.ecommerce.order.finalize;

import com.serhat.ecommerce.order.entity.Order;
import com.serhat.ecommerce.discount.discountService.service.DiscountCodeService;
import com.serhat.ecommerce.notification.service.NotificationService;
import com.serhat.ecommerce.product.inventory.InventoryService;
import com.serhat.ecommerce.order.repo.OrderRepository;
import com.serhat.ecommerce.sCard.entity.ShoppingCard;
import com.serhat.ecommerce.sCard.service.ShoppingCartService;
import com.serhat.ecommerce.user.userS.entity.User;
import com.serhat.ecommerce.user.userS.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderFinalizationService {
    private final OrderRepository orderRepository;
    private final ShoppingCartService shoppingCardService;
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