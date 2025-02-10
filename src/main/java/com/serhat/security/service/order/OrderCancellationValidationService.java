package com.serhat.security.service.order;

import com.serhat.security.entity.Order;
import com.serhat.security.entity.User;
import com.serhat.security.entity.enums.OrderStatus;
import com.serhat.security.exception.OrderAlreadyCanceledException;
import com.serhat.security.exception.OrderCancellationException;
import com.serhat.security.exception.WrongOrderIdException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderCancellationValidationService {
    public void isItemsReturnable(Order order){
        boolean isItemsReturnable = order.getOrderItems().stream()
                .allMatch(orderItem -> orderItem.getProduct().isReturnable());

        if (!isItemsReturnable) {
            throw new OrderCancellationException("All the items must be returnable to cancel the order!");
        }
    }

    private void checkOrderStatus(Order order){
        if (order.getStatus() == OrderStatus.SHIPPED || order.getStatus() == OrderStatus.DELIVERED) {
            throw new OrderCancellationException("Order cannot be canceled as it is already shipped or delivered!");
        } if (order.getStatus() == OrderStatus.REFUNDED) {
            throw new OrderAlreadyCanceledException("Order was already canceled!");
        }
    }

    private void checkOrderBelongsToUser(Order order , User user){
        if (!order.getUser().equals(user)) {
            throw new WrongOrderIdException("You do not have any order with id : "+order.getOrderId());
        }
    }

    public void checkIsOrderCancellable(Order order , User user){
        isItemsReturnable(order);
        checkOrderStatus(order);
        checkOrderBelongsToUser(order,user);
    }
}
