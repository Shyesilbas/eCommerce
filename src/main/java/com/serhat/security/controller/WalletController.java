package com.serhat.security.controller;

import com.serhat.security.dto.request.WalletRequest;
import com.serhat.security.dto.response.*;
import com.serhat.security.entity.Transaction;
import com.serhat.security.service.WalletService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

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

    @GetMapping("/info")
    public ResponseEntity<WalletInfoResponse> getWalletInfo(HttpServletRequest request) {
        WalletInfoResponse walletInfo = walletService.walletInfo(request);
        return ResponseEntity.ok(walletInfo);
    }

    @GetMapping("/transactions")
    public ResponseEntity<List<TransactionResponse>> getTransactionHistory(HttpServletRequest request) {
        return  ResponseEntity.ok(walletService.getTransactionHistory(request));
    }
}
