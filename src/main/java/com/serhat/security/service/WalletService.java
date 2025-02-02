package com.serhat.security.service;

import com.serhat.security.dto.request.WalletRequest;
import com.serhat.security.dto.response.*;
import com.serhat.security.entity.Transaction;
import com.serhat.security.entity.User;
import com.serhat.security.entity.Wallet;
import com.serhat.security.entity.enums.TransactionType;
import com.serhat.security.exception.*;
import com.serhat.security.interfaces.TokenInterface;
import com.serhat.security.repository.TransactionRepository;
import com.serhat.security.repository.WalletRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class WalletService {


    private final WalletRepository walletRepository;
    private final TransactionRepository transactionRepository;
    private final TokenInterface tokenInterface;

    @Transactional
    public WalletCreatedResponse createWallet(HttpServletRequest request, WalletRequest walletRequest) {
        User user = tokenInterface.getUserFromToken(request);

        boolean hasUserHaveWallet = walletRepository.findByUser_UserId(user.getUserId()).isPresent();
        if(hasUserHaveWallet){
            throw new AlreadyHasWalletException("You can only have 1 wallet currently active.");
        }

        Wallet wallet = new Wallet();
        wallet.setWalletName(walletRequest.walletName());
        wallet.setUser(user);
        wallet.setCreatedAt(LocalDateTime.now());
        wallet.setDescription(walletRequest.description());
        wallet.setBonusPoints(new BigDecimal("0.0"));
        wallet.setWalletLimit(walletRequest.limit());
        wallet.setWalletPin(walletRequest.walletPin());
        wallet.setBalance(new BigDecimal("0.0"));
         walletRepository.save(wallet);

         return new WalletCreatedResponse(
                 wallet.getWalletId(),
                 wallet.getWalletName(),
                 wallet.getBalance(),
                 wallet.getDescription()
         );
    }

    @Transactional
    public WalletLimitUpdateResponse limitUpdate(HttpServletRequest servletRequest , BigDecimal newLimit){
        User user = tokenInterface.getUserFromToken(servletRequest);
        Long userId = user.getUserId();

        Wallet wallet = walletRepository.findByUser_UserId(userId)
                .orElseThrow(() -> new WalletNotFoundException("Wallet not found for user"));

        if(newLimit.compareTo(BigDecimal.ZERO)<=0){
            throw new InvalidAmountException("Invalid limit request!");
        }

        if (wallet.getBalance().compareTo(newLimit) > 0) {
            throw new LimitExceededException("Your current balance exceeds the new limit. Please adjust your balance before updating the limit.");
        }

       wallet.setWalletLimit(newLimit);

        return new WalletLimitUpdateResponse(
                "Limit Updated Successfully",
                newLimit,
                LocalDateTime.now()
        );

    }

    @Transactional
    public DepositSuccessfulResponse depositMoney(HttpServletRequest request, BigDecimal amount) {
        User user = tokenInterface.getUserFromToken(request);
        Long userId = user.getUserId();

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidAmountException("Deposit amount must be greater than zero");
        }

        Wallet wallet = walletRepository.findByUser_UserId(userId)
                .orElseThrow(() -> new WalletNotFoundException("Wallet not found for user"));

        if(amount.compareTo(wallet.getWalletLimit())>0){
            throw new LimitExceededException("Your deposit amount request exceeds the limit you have set ");
        }

        wallet.setBalance(wallet.getBalance().add(amount));

        Transaction transaction = new Transaction();
        transaction.setWallet(wallet);
        transaction.setUser(user);
        transaction.setOrder(null);
        transaction.setAmount(amount);
        transaction.setTransactionType(TransactionType.DEPOSIT);
        transaction.setTransactionDate(LocalDateTime.now());
        transaction.setDescription("Deposit");

        transactionRepository.save(transaction);
        walletRepository.save(wallet);

        log.info("User {} deposited {} into their wallet", userId, amount);

        return new DepositSuccessfulResponse(
                amount,
                wallet.getBalance(),
                LocalDateTime.now(),
                TransactionType.DEPOSIT
        );
    }


    public List<TransactionResponse> getTransactionHistory(HttpServletRequest request) {
        User user = tokenInterface.getUserFromToken(request);
        Long userId = user.getUserId();

        Wallet wallet = walletRepository.findByUser_UserId(userId)
                .orElseThrow(() -> new WalletNotFoundException("Wallet not found for user"));

        return wallet.getTransactions().stream()
                .map(transaction -> new TransactionResponse(
                        transaction.getAmount(),
                        transaction.getTransactionDate(),
                        transaction.getTransactionType(),
                        transaction.getDescription()
                ))
                .collect(Collectors.toList());
    }

    public WalletInfoResponse walletInfo(HttpServletRequest request) {
        User user = tokenInterface.getUserFromToken(request);
        Long userId = user.getUserId();

        Wallet wallet = walletRepository.findByUser_UserId(userId)
                .orElseThrow(() -> new WalletNotFoundException("Wallet not found for user"));

        return new WalletInfoResponse(
                wallet.getDescription(),
                wallet.getWalletName(),
                wallet.getWalletLimit(),
                wallet.getBalance(),
                wallet.getBonusPoints()
        );
    }

}
