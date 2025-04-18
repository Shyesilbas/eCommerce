package com.serhat.ecommerce.order.cancellation;

import com.serhat.ecommerce.order.Order;
import com.serhat.ecommerce.user.userS.entity.User;
import com.serhat.ecommerce.payment.service.TransactionService;
import com.serhat.ecommerce.user.userS.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
class OrderRefundService {
    private final TransactionService transactionService;
    private final UserService userService;

    public void refundOrder(Order order, User user) {
        transactionService.createRefundTransaction(order, user, order.getTotalPaid(), order.getShippingFee());
        userService.updateUserAfterOrderCancel(user, order);
    }
}