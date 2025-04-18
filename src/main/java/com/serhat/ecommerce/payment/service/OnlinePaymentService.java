package com.serhat.ecommerce.payment.service;

import com.serhat.ecommerce.order.Order;
import com.serhat.ecommerce.payment.entity.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OnlinePaymentService extends PaymentService<String> {

    @Override
    public String processPayment(Order order) {
        return "Payment processed online. Total Paid: "+order.getTotalPaid();
    }
}
