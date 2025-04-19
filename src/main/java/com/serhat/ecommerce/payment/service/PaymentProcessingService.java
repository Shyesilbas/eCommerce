package com.serhat.ecommerce.payment.service;

import com.serhat.ecommerce.order.entity.Order;
import com.serhat.ecommerce.payment.enums.PaymentMethod;
import com.serhat.ecommerce.payment.entity.PaymentService;
import com.serhat.ecommerce.payment.entity.Transaction;
import com.serhat.ecommerce.wallet.service.WalletService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentProcessingService {

    private final PaymentFactory paymentFactory;
    private final WalletService walletService;

    public void processPayment(Order order, PaymentMethod paymentMethod) {
        PaymentService<?> paymentService = paymentFactory.getPaymentService(paymentMethod);
        if(paymentMethod == null){
            throw new NullPointerException("NULL PAYMENT ERROR");
        }

        if (paymentService instanceof EWalletPaymentService eWalletService) {
            walletService.getWalletByUser(order.getUser());
            List<Transaction> transactions = eWalletService.processPayment(order);
            order.setTransactions(transactions);
        } else if (paymentService instanceof OnlinePaymentService onlinePaymentService) {
            String message = onlinePaymentService.processPayment(order);
            log.info("Payment Message: {}", message);
        } else {
            throw new UnsupportedOperationException("Unsupported payment service type");
        }
    }

}
