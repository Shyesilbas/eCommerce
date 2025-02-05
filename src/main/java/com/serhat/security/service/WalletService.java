package com.serhat.security.service;

import com.serhat.security.dto.request.WalletRequest;
import com.serhat.security.dto.response.*;
import com.serhat.security.entity.Transaction;
import com.serhat.security.entity.User;
import com.serhat.security.entity.Wallet;
import com.serhat.security.entity.enums.TransactionType;
import com.serhat.security.exception.*;
import com.serhat.security.interfaces.TokenInterface;
import com.serhat.security.mapper.TransactionMapper;
import com.serhat.security.mapper.WalletMapper;
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
    private final TransactionService transactionService;
    private final TokenInterface tokenInterface;
    private final WalletMapper walletMapper;
    private final TransactionMapper transactionMapper;

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
        Wallet wallet = getUsersWallet(user);

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

        Wallet wallet =getUsersWallet(user);

        if(amount.compareTo(wallet.getWalletLimit())>0){
            throw new LimitExceededException("Your deposit amount request exceeds the limit you have set ");
        }

        wallet.setBalance(wallet.getBalance().add(amount));

        transactionService.createDepositTransaction(user,amount);
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
        Wallet wallet = getUsersWallet(user);

        return wallet.getTransactions().stream()
                .map(transactionMapper::toTransactionResponse)
                .collect(Collectors.toList());
    }

    public WalletInfoResponse walletInfo(HttpServletRequest request) {
        User user = tokenInterface.getUserFromToken(request);
        Wallet wallet = getUsersWallet(user);
        return walletMapper.toWalletInfoResponse(wallet);
    }

}
