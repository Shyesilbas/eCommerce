package com.serhat.ecommerce.payment.service;

import com.serhat.ecommerce.order.Order;
import com.serhat.ecommerce.payment.entity.Transaction;
import com.serhat.ecommerce.payment.mapper.TransactionMapper;
import com.serhat.ecommerce.payment.repository.TransactionRepository;
import com.serhat.ecommerce.user.userS.entity.User;
import com.serhat.ecommerce.wallet.entity.Wallet;
import com.serhat.ecommerce.enums.PaymentMethod;
import com.serhat.ecommerce.enums.TransactionType;
import com.serhat.ecommerce.payment.paymentException.InsufficientFundsException;
import com.serhat.ecommerce.wallet.walletException.WalletNotFoundException;
import com.serhat.ecommerce.wallet.repository.WalletRepository;
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

        if (order.getBonusWon().compareTo(BigDecimal.ZERO) > 0) {
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


        List<Transaction> transactions = new ArrayList<>();

        transactions.add(transactionMapper.toTransaction(wallet, user, order, order.getTotalPaid(),
                TransactionType.CANCEL_REFUND, String.format("Refund for canceled order, including shipping fee: %s", shippingFee)));

        wallet.setBalance(wallet.getBalance().add(order.getTotalPaid()));

        if (order.getBonusWon().compareTo(BigDecimal.ZERO) > 0) {
            transactions.add(transactionMapper.toTransaction(wallet, user, order, order.getBonusWon(),
                    TransactionType.BONUS_DEDUCT, "Bonus points retake for canceled order"));
        }

        if (order.getBonusPointsUsed().compareTo(BigDecimal.ZERO) > 0) {
            transactions.add(transactionMapper.toTransaction(wallet, user, order, order.getBonusPointsUsed(),
                    TransactionType.BONUS_GRANTED, "Bonus refund for canceled order"));
        }

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
    public void createGiftCardTransaction(User user, BigDecimal amount) {
        Wallet wallet = getWalletByUser(user);

        if (wallet.getBalance().compareTo(amount) < 0) {
            throw new InsufficientFundsException("Insufficient funds in wallet");
        }

        wallet.setBalance(wallet.getBalance().subtract(amount));

        Transaction transaction = transactionMapper.toTransaction(wallet, user, null, amount, TransactionType.PAYMENT, "Gift card payment via E-Wallet");

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
