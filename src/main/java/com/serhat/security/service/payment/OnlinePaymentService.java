package com.serhat.security.service.payment;

import com.serhat.security.entity.Order;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class OnlinePaymentService extends PaymentService<String>{

    @Override
    public String processPayment(Order order) {
        return "Payment processed online. Total Paid: "+order.getTotalPaid();
    }
}
