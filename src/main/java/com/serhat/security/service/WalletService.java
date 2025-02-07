package com.serhat.security.service;

import com.serhat.security.dto.request.WalletRequest;
import com.serhat.security.dto.response.*;
import com.serhat.security.entity.Transaction;
import com.serhat.security.entity.User;
import com.serhat.security.entity.Wallet;
import com.serhat.security.exception.*;
import com.serhat.security.interfaces.TokenInterface;
import com.serhat.security.mapper.TransactionMapper;
import com.serhat.security.mapper.WalletMapper;
import com.serhat.security.repository.TransactionRepository;
import com.serhat.security.repository.WalletRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Slf4j
public class WalletService {
    private final WalletRepository walletRepository;
    private final TransactionService transactionService;
    private final TokenInterface tokenInterface;
    private final WalletMapper walletMapper;
    private final TransactionMapper transactionMapper;
    private final TransactionRepository transactionRepository;

    private Wallet getUsersWallet(User user){
        return walletRepository.findByUser_UserId(user.getUserId())
                .orElseThrow(()-> new WalletNotFoundException("Wallet not found"));
    }

    @Transactional
    public WalletCreatedResponse createWallet(HttpServletRequest request, WalletRequest walletRequest) {
        User user = tokenInterface.getUserFromToken(request);

        boolean hasUserHaveWallet = walletRepository.findByUser_UserId(user.getUserId()).isPresent();
        if(hasUserHaveWallet){
            throw new AlreadyHasWalletException("You can only have 1 wallet currently active.");
        }

         Wallet wallet = walletMapper.toWalletAndSave(walletRequest);

         return walletMapper.toWalletCreatedResponse(wallet);
    }

    @Transactional
    public WalletLimitUpdateResponse limitUpdate(HttpServletRequest servletRequest , BigDecimal newLimit){
        User user = tokenInterface.getUserFromToken(servletRequest);
        Wallet wallet = getUsersWallet(user);

        if(newLimit.compareTo(BigDecimal.ZERO)<=0){
            throw new InvalidAmountException("Invalid limit request!");
        }

        if (wallet.getBalance().compareTo(newLimit) > 0) {
            throw new LimitExceededException("Your current balance exceeds the new limit. Please adjust your balance before updating the limit.");
        }

       wallet.setWalletLimit(newLimit);

        return walletMapper.toWalletLimitUpdateResponse(wallet,newLimit);
    }

    @Transactional
    public DepositSuccessfulResponse depositMoney(HttpServletRequest request, BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidAmountException("Deposit amount must be greater than zero");
        }

        User user = tokenInterface.getUserFromToken(request);
        Wallet wallet = getUsersWallet(user);

        checkAmountAndLimit(wallet, amount);

        wallet.setBalance(wallet.getBalance().add(amount));
        walletRepository.save(wallet);

        transactionService.createDepositTransaction(user, amount);

        log.info("User {} deposited {} into their wallet. New balance: {}",
                user.getUserId(), amount, wallet.getBalance());

        return walletMapper.toDepositSuccessfulResponse(amount,wallet);
    }

    private void checkAmountAndLimit(Wallet wallet, BigDecimal amount) {
        BigDecimal limit = wallet.getWalletLimit();
        BigDecimal balanceAfterDepositRequest = wallet.getBalance().add(amount);

        if (balanceAfterDepositRequest.compareTo(limit) > 0) {
            throw new LimitExceededException("After deposit, you will exceed your limit. Update your limit.");
        }
    }

    public Page<TransactionResponse> getTransactionHistory(HttpServletRequest request, Pageable pageable) {
        User user = tokenInterface.getUserFromToken(request);
        Wallet wallet = getUsersWallet(user);

        Page<Transaction> transactionPage = transactionRepository.findByWallet(wallet, pageable);
        return transactionPage.map(transactionMapper::toTransactionResponse);
    }


    public WalletInfoResponse walletInfo(HttpServletRequest request) {
        User user = tokenInterface.getUserFromToken(request);
        Wallet wallet = getUsersWallet(user);
        return walletMapper.toWalletInfoResponse(wallet);
    }

}
