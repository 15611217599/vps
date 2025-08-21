package com.vps.vpsserver.service;

import com.vps.vpsserver.dto.WalletDTO;

import java.math.BigDecimal;

public interface WalletService {
    WalletDTO getWalletByUserId(Long userId);
    WalletDTO createWallet(Long userId, String currency);
    WalletDTO updateBalance(Long userId, BigDecimal amount);
    WalletDTO updateCurrency(Long userId, String currency);
}
