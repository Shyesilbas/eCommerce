package com.serhat.security.interfaces;

import com.serhat.security.dto.response.WalletInfoResponse;
import com.serhat.security.entity.User;
import com.serhat.security.entity.Wallet;
import jakarta.servlet.http.HttpServletRequest;

public interface WalletInterface {
    Wallet getWalletByUser(User user);
    Wallet getUserAndTheirWallet(HttpServletRequest request);

    WalletInfoResponse walletInfo(HttpServletRequest request);
}
