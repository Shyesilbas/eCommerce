package com.serhat.ecommerce.payment.service;

import com.serhat.ecommerce.order.entity.Order;
import com.serhat.ecommerce.payment.entity.PaymentService;
import com.serhat.ecommerce.payment.entity.Transaction;
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

