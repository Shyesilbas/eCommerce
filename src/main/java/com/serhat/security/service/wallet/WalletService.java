package com.serhat.security.service.wallet;

import com.serhat.security.dto.request.WalletRequest;
import com.serhat.security.dto.response.*;
import com.serhat.security.entity.Transaction;
import com.serhat.security.entity.User;
import com.serhat.security.entity.Wallet;
import com.serhat.security.exception.*;
import com.serhat.security.interfaces.UserInterface;
import com.serhat.security.interfaces.WalletInterface;
import com.serhat.security.interfaces.WalletValidationInterface;
import com.serhat.security.mapper.TransactionMapper;
import com.serhat.security.mapper.WalletMapper;
import com.serhat.security.repository.TransactionRepository;
import com.serhat.security.repository.WalletRepository;
import com.serhat.security.service.TransactionService;
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
public class WalletService implements WalletInterface {
    private final WalletRepository walletRepository;
    private final TransactionService transactionService;
    private final WalletMapper walletMapper;
    private final TransactionMapper transactionMapper;
    private final TransactionRepository transactionRepository;
    private final UserInterface userInterface;
    private final WalletValidationInterface walletValidation;
    @Override
    public Wallet getWalletByUser(User user){
        return walletRepository.findByUser_UserId(user.getUserId())
                .orElseThrow(()-> new WalletNotFoundException("Wallet not found"));
    }
    public User getUser(HttpServletRequest request){
       return userInterface.getUserFromToken(request);
    }
    public Wallet getUserAndTheirWallet(HttpServletRequest request) {
        User user = getUser(request);
        return getWalletByUser(user);
    }

    @Transactional
    public WalletCreatedResponse createWallet(HttpServletRequest request, WalletRequest walletRequest) {
        User user = getUser(request);
        walletValidation.hasUserHaveWallet(user);

         Wallet wallet = walletMapper.toWallet(walletRequest);
         walletRepository.save(wallet);
         return walletMapper.toWalletCreatedResponse(wallet);
    }

    @Transactional
    public WalletLimitUpdateResponse limitUpdate(HttpServletRequest servletRequest , BigDecimal newLimit){
        Wallet wallet = getUserAndTheirWallet(servletRequest);
        walletValidation.validateLimit(wallet, newLimit);
        wallet.setWalletLimit(newLimit);
        walletRepository.save(wallet);
        return walletMapper.toWalletLimitUpdateResponse(wallet,newLimit);
    }

    @Transactional
    public DepositSuccessfulResponse depositMoney(HttpServletRequest request, BigDecimal amount) {
        Wallet wallet = getUserAndTheirWallet(request);
        walletValidation.checkIfDepositValid(wallet,amount);
        wallet.setBalance(wallet.getBalance().add(amount));
        walletRepository.save(wallet);

        transactionService.createDepositTransaction(wallet.getUser(), amount);

        log.info("User {} deposited {} into their wallet. New balance: {}",
                wallet.getUser().getUserId(), amount, wallet.getBalance());

        return walletMapper.toDepositSuccessfulResponse(amount,wallet);
    }

    public Page<TransactionResponse> getTransactionHistory(HttpServletRequest request, Pageable pageable) {
        Wallet wallet = getUserAndTheirWallet(request);
        Page<Transaction> transactionPage = transactionRepository.findByWallet(wallet, pageable);
        return transactionPage.map(transactionMapper::toTransactionResponse);
    }

    @Override
    public WalletInfoResponse walletInfo(HttpServletRequest request) {
        Wallet wallet = getUserAndTheirWallet(request);
        return walletMapper.toWalletInfoResponse(wallet);
    }

}
