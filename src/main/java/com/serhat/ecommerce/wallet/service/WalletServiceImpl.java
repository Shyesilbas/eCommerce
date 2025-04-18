package com.serhat.ecommerce.wallet.service;

import com.serhat.ecommerce.wallet.dto.WalletRequest;
import com.serhat.ecommerce.payment.dto.TransactionResponse;
import com.serhat.ecommerce.payment.entity.Transaction;
import com.serhat.ecommerce.payment.mapper.TransactionMapper;
import com.serhat.ecommerce.payment.repository.TransactionRepository;
import com.serhat.ecommerce.wallet.dto.DepositSuccessfulResponse;
import com.serhat.ecommerce.wallet.dto.WalletCreatedResponse;
import com.serhat.ecommerce.wallet.dto.WalletInfoResponse;
import com.serhat.ecommerce.wallet.dto.WalletLimitUpdateResponse;
import com.serhat.ecommerce.wallet.repository.WalletRepository;
import com.serhat.ecommerce.wallet.entity.Wallet;
import com.serhat.ecommerce.wallet.mapper.WalletMapper;
import com.serhat.ecommerce.wallet.walletException.WalletNotFoundException;
import com.serhat.ecommerce.user.userS.entity.User;
import com.serhat.ecommerce.payment.service.TransactionService;
import com.serhat.ecommerce.user.userS.service.UserService;
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
public class WalletServiceImpl implements WalletService {
    private final WalletRepository walletRepository;
    private final TransactionService transactionService;
    private final WalletMapper walletMapper;
    private final TransactionMapper transactionMapper;
    private final TransactionRepository transactionRepository;
    private final WalletValidationInterface walletValidation;
    private final UserService userService;


    @Override
    public Wallet getWalletByUser(User user){
        return walletRepository.findByUser_UserId(user.getUserId())
                .orElseThrow(()-> new WalletNotFoundException("Wallet not found"));
    }
    public User getUser(){
       return userService.getAuthenticatedUser();
    }
    public Wallet getUserAndTheirWallet() {
        User user = getUser();
        return getWalletByUser(user);
    }

    @Override
    @Transactional
    public WalletCreatedResponse createWallet(WalletRequest walletRequest) {
        User user = getUser();
        walletValidation.hasUserHaveWallet(user);

         Wallet wallet = walletMapper.toWallet(walletRequest);
         walletRepository.save(wallet);
         return walletMapper.toWalletCreatedResponse(wallet);
    }

    @Override
    @Transactional
    public WalletLimitUpdateResponse limitUpdate(BigDecimal newLimit){
        Wallet wallet = getUserAndTheirWallet();
        walletValidation.validateLimit(wallet, newLimit);
        wallet.setWalletLimit(newLimit);
        walletRepository.save(wallet);
        return walletMapper.toWalletLimitUpdateResponse(wallet,newLimit);
    }

    @Override
    @Transactional
    public DepositSuccessfulResponse depositMoney(BigDecimal amount) {
        Wallet wallet = getUserAndTheirWallet();
        walletValidation.checkIfDepositValid(wallet,amount);
        wallet.setBalance(wallet.getBalance().add(amount));
        walletRepository.save(wallet);

        transactionService.createDepositTransaction(wallet.getUser(), amount);

        log.info("User {} deposited {} into their wallet. New balance: {}",
                wallet.getUser().getUserId(), amount, wallet.getBalance());

        return walletMapper.toDepositSuccessfulResponse(amount,wallet);
    }

    @Override
    public Page<TransactionResponse> getTransactionHistory(Pageable pageable) {
        Wallet wallet = getUserAndTheirWallet();
        Page<Transaction> transactionPage = transactionRepository.findByWallet(wallet, pageable);
        return transactionPage.map(transactionMapper::toTransactionResponse);
    }

    @Override
    public WalletInfoResponse walletInfo() {
        Wallet wallet = getUserAndTheirWallet();
        return walletMapper.toWalletInfoResponse(wallet);
    }

}
