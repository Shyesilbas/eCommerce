package com.serhat.security.controller;

import com.serhat.security.dto.request.WalletRequest;
import com.serhat.security.dto.response.*;
import com.serhat.security.interfaces.WalletService;
import jakarta.servlet.http.HttpServletRequest;
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
    public ResponseEntity<WalletCreatedResponse> createWallet(HttpServletRequest request, @RequestBody WalletRequest walletRequest) {
        WalletCreatedResponse response = walletService.createWallet(request, walletRequest);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/deposit")
    public ResponseEntity<DepositSuccessfulResponse> depositMoney(HttpServletRequest request, @RequestParam BigDecimal amount) {
        DepositSuccessfulResponse response = walletService.depositMoney(request, amount);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping("/update-limit")
    public ResponseEntity<WalletLimitUpdateResponse> walletLimitUpdate(HttpServletRequest request , @RequestParam BigDecimal newLimit){
        return ResponseEntity.ok(walletService.limitUpdate(request, newLimit));
    }

    @GetMapping("/info")
    public ResponseEntity<WalletInfoResponse> getWalletInfo(HttpServletRequest request) {
        WalletInfoResponse walletInfo = walletService.walletInfo(request);
        return ResponseEntity.ok(walletInfo);
    }

    @GetMapping("/transactions")
    public ResponseEntity<Page<TransactionResponse>> getTransactionHistory(
            HttpServletRequest request,
             Pageable pageable) {

        Page<TransactionResponse> transactions = walletService.getTransactionHistory(request, pageable);
        return ResponseEntity.ok(transactions);
    }

}
