package com.serhat.security.service;

import com.serhat.security.entity.Order;
import com.serhat.security.entity.Transaction;
import com.serhat.security.entity.User;
import com.serhat.security.entity.Wallet;
import com.serhat.security.entity.enums.PaymentMethod;
import com.serhat.security.entity.enums.TransactionType;
import com.serhat.security.exception.InsufficientFundsException;
import com.serhat.security.exception.WalletNotFoundException;
import com.serhat.security.repository.TransactionRepository;
import com.serhat.security.repository.WalletRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TransactionService {
    private final TransactionRepository transactionRepository;
    private final WalletRepository walletRepository;

    @Transactional
    public List<Transaction> createTransactions(Order order, User user, BigDecimal finalPrice, BigDecimal bonusPoints, BigDecimal shippingFee) {
        List<Transaction> transactions = new ArrayList<>();

        if (order.getPaymentMethod() == PaymentMethod.E_WALLET) {
            Wallet wallet = walletRepository.findByUser_UserId(user.getUserId())
                    .orElseThrow(() -> new WalletNotFoundException("Wallet not found for user"));

            if (wallet.getBalance().compareTo(finalPrice) < 0) {
                throw new InsufficientFundsException("Insufficient funds in wallet");
            }

            wallet.setBalance(wallet.getBalance().subtract(finalPrice));
            wallet.setBonusPoints(wallet.getBonusPoints().add(bonusPoints));

            String paymentDescription = shippingFee.compareTo(BigDecimal.ZERO) > 0
                    ? String.format("Payment for order (including shipping fee: %s)", shippingFee)
                    : "Payment for order";

            Transaction paymentTransaction = new Transaction();
            paymentTransaction.setWallet(wallet);
            paymentTransaction.setUser(user);
            paymentTransaction.setOrder(order);
            paymentTransaction.setAmount(finalPrice);
            paymentTransaction.setTransactionType(TransactionType.PAYMENT);
            paymentTransaction.setTransactionDate(LocalDateTime.now());
            paymentTransaction.setDescription(paymentDescription);

            transactions.add(paymentTransaction);

            Transaction bonusTransaction = new Transaction();
            bonusTransaction.setWallet(wallet);
            bonusTransaction.setUser(user);
            bonusTransaction.setOrder(order);
            bonusTransaction.setAmount(bonusPoints);
            bonusTransaction.setTransactionType(TransactionType.BONUS_GRANTED);
            bonusTransaction.setTransactionDate(LocalDateTime.now());
            bonusTransaction.setDescription("Bonus points granted for order");



            transactionRepository.saveAll(transactions);
            walletRepository.save(wallet);
        }

        return transactions;
    }

    @Transactional
    public Transaction createRefundTransaction(Order order, User user, BigDecimal refundAmount, BigDecimal shippingFee) {
        Wallet wallet = walletRepository.findByUser_UserId(user.getUserId())
                .orElseThrow(() -> new WalletNotFoundException("Wallet not found for user"));

        wallet.setBalance(wallet.getBalance().add(refundAmount));
        walletRepository.save(wallet);

        Transaction refundTransaction = new Transaction();
        refundTransaction.setWallet(wallet);
        refundTransaction.setUser(user);
        refundTransaction.setOrder(order);
        refundTransaction.setAmount(refundAmount);
        refundTransaction.setTransactionType(TransactionType.CANCEL_REFUND);
        refundTransaction.setTransactionDate(LocalDateTime.now());
        refundTransaction.setDescription(String.format("Refund for canceled order, including shipping fee: %s", shippingFee));

        return transactionRepository.save(refundTransaction);
    }
}
