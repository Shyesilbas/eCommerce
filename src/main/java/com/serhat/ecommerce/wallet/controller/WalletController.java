package com.serhat.ecommerce.wallet.controller;

import com.serhat.ecommerce.dto.request.WalletRequest;
import com.serhat.ecommerce.dto.response.*;
import com.serhat.ecommerce.wallet.service.WalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequiredArgsConstructor
@RequestMapping("/wallet")
public class WalletController {

    private final WalletService walletService;

    @PostMapping("/create")
    public ResponseEntity<WalletCreatedResponse> createWallet(@RequestBody WalletRequest walletRequest) {
        WalletCreatedResponse response = walletService.createWallet(walletRequest);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/deposit")
    public ResponseEntity<DepositSuccessfulResponse> depositMoney(@RequestParam BigDecimal amount) {
        DepositSuccessfulResponse response = walletService.depositMoney(amount);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping("/update-limit")
    public ResponseEntity<WalletLimitUpdateResponse> walletLimitUpdate(@RequestParam BigDecimal newLimit){
        return ResponseEntity.ok(walletService.limitUpdate(newLimit));
    }

    @GetMapping("/info")
    public ResponseEntity<WalletInfoResponse> getWalletInfo() {
        WalletInfoResponse walletInfo = walletService.walletInfo();
        return ResponseEntity.ok(walletInfo);
    }

    @GetMapping("/transactions")
    public ResponseEntity<Page<TransactionResponse>> getTransactionHistory(Pageable pageable) {
        Page<TransactionResponse> transactions = walletService.getTransactionHistory(pageable);
        return ResponseEntity.ok(transactions);
    }

}
