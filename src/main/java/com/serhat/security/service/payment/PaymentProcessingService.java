package com.serhat.security.service.payment;

import com.serhat.security.entity.Order;
import com.serhat.security.entity.Transaction;
import com.serhat.security.entity.enums.PaymentMethod;
import com.serhat.security.service.wallet.WalletService;
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
