package com.serhat.security.service;

import com.serhat.security.entity.Order;
import com.serhat.security.entity.Transaction;
import com.serhat.security.entity.User;
import com.serhat.security.entity.Wallet;
import com.serhat.security.entity.enums.PaymentMethod;
import com.serhat.security.entity.enums.TransactionType;
import com.serhat.security.exception.InsufficientFundsException;
import com.serhat.security.exception.WalletNotFoundException;
import com.serhat.security.mapper.TransactionMapper;
import com.serhat.security.repository.TransactionRepository;
import com.serhat.security.repository.WalletRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TransactionService {
    private final TransactionRepository transactionRepository;
    private final WalletRepository walletRepository;
    private final TransactionMapper transactionMapper;

    private Wallet getWalletByUser(User user) {
        return walletRepository.findByUser_UserId(user.getUserId())
                .orElseThrow(() -> new WalletNotFoundException("Wallet not found for user"));
    }

    @Transactional
    public List<Transaction> createTransactions(Order order, User user, BigDecimal finalPrice, BigDecimal bonusPoints, BigDecimal shippingFee) {
        if (order.getPaymentMethod() != PaymentMethod.E_WALLET) {
            return new ArrayList<>();
        }

        Wallet wallet = getWalletByUser(user);

        if (wallet.getBalance().compareTo(finalPrice) < 0) {
            throw new InsufficientFundsException("Insufficient funds in wallet");
        }

        wallet.setBalance(wallet.getBalance().subtract(finalPrice));
        wallet.setBonusPoints(wallet.getBonusPoints().add(bonusPoints));

        List<Transaction> transactions = new ArrayList<>();
        transactions.add(transactionMapper.toTransaction(
                wallet,
                user,
                order,
                finalPrice,
                TransactionType.PAYMENT,
                shippingFee.compareTo(BigDecimal.ZERO) > 0
                        ? String.format("Payment for order (including shipping fee: %s)", shippingFee)
                        : "Payment for order"
        ));

        // Create bonus points transaction if any
        if (bonusPoints.compareTo(BigDecimal.ZERO) > 0) {
            transactions.add(transactionMapper.toTransaction(
                    wallet,
                    user,
                    order,
                    bonusPoints,
                    TransactionType.BONUS_GRANTED,
                    "Bonus points granted for order"
            ));
        }

        transactionRepository.saveAll(transactions);
        walletRepository.save(wallet);

        return transactions;
    }

    @Transactional
    public void createRefundTransaction(Order order, User user, BigDecimal totalPaid, BigDecimal shippingFee) {
        Wallet wallet = getWalletByUser(user);
        wallet.setBalance(wallet.getBalance().add(totalPaid));

        List<Transaction> transactions = List.of(
                transactionMapper.toTransaction(wallet, user, order, totalPaid, TransactionType.CANCEL_REFUND,
                        String.format("Refund for canceled order, including shipping fee: %s", shippingFee)),
                transactionMapper.toTransaction(wallet, user, order, order.getBonusWon(), TransactionType.CANCEL_REFUND, "Refund Bonus for canceled order")
        );

        transactionRepository.saveAll(transactions);
        walletRepository.save(wallet);
    }

    @Transactional
    public void createMembershipTransaction(User user, BigDecimal fee) {
        Wallet wallet = getWalletByUser(user);

        if (wallet.getBalance().compareTo(fee) < 0) {
            throw new InsufficientFundsException("Insufficient funds in wallet");
        }

        wallet.setBalance(wallet.getBalance().subtract(fee));

        Transaction transaction = transactionMapper.toTransaction(wallet, user, null, fee, TransactionType.PAYMENT, "Membership plan payment via E-Wallet");

        transactionRepository.save(transaction);
        walletRepository.save(wallet);
    }

    @Transactional
    public void createDepositTransaction(User user, BigDecimal amount) {
        Wallet wallet = getWalletByUser(user);

        if (amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new InsufficientFundsException("Invalid amount");
        }

        wallet.setBalance(wallet.getBalance().add(amount));

        Transaction transaction = transactionMapper.toTransaction(wallet, user, null, amount, TransactionType.DEPOSIT, "Deposit to Wallet");

        transactionRepository.save(transaction);
        walletRepository.save(wallet);
    }
}
