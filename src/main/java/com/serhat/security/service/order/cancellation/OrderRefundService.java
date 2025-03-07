package com.serhat.security.service.order.cancellation;

import com.serhat.security.entity.Order;
import com.serhat.security.entity.User;
import com.serhat.security.service.payment.TransactionService;
import com.serhat.security.service.user.UserService;
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