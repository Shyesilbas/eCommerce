package com.serhat.security.service.payment;

import com.serhat.security.entity.*;
import com.serhat.security.interfaces.*;
import com.serhat.security.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentService implements PaymentServiceInterface {
     private final TransactionService transactionService;

    @Override
    @Transactional
    public List<Transaction> createOrderTransactions(Order order) {
        return transactionService.createTransactions(
                order, order.getUser(), order.getTotalPaid(), order.getBonusWon(), order.getShippingFee());
    }
}

