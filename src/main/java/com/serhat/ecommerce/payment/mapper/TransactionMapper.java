package com.serhat.ecommerce.payment.mapper;

import com.serhat.ecommerce.payment.dto.TransactionResponse;
import com.serhat.ecommerce.order.entity.Order;
import com.serhat.ecommerce.payment.entity.Transaction;
import com.serhat.ecommerce.user.userS.entity.User;
import com.serhat.ecommerce.wallet.entity.Wallet;
import com.serhat.ecommerce.payment.enums.TransactionType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class TransactionMapper {

    public TransactionResponse toTransactionResponse(Transaction transaction) {
        return new TransactionResponse(
                transaction.getAmount(),
                transaction.getTransactionDate(),
                transaction.getTransactionType(),
                transaction.getDescription()
        );
    }
    public Transaction toTransaction(Wallet wallet, User user, Order order, BigDecimal amount, TransactionType type, String description) {
        return Transaction.builder()
                .wallet(wallet)
                .user(user)
                .order(order)
                .amount(amount)
                .transactionType(type)
                .transactionDate(LocalDateTime.now())
                .description(description)
                .build();
    }

}
