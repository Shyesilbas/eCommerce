package com.serhat.security.service.payment;

import com.serhat.security.entity.*;
import com.serhat.security.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EWalletPaymentService extends PaymentService<List<Transaction>> {
     private final TransactionService transactionService;

    @Transactional
    @Override
    public List<Transaction> processPayment(Order order) {
        logPayment(order);
        return transactionService.createTransactions(
                order, order.getUser(), order.getTotalPaid(), order.getBonusWon(), order.getShippingFee());
    }
}

