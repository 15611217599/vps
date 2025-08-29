package com.vps.vpsserver.service;

import java.math.BigDecimal;

import com.vps.vpsserver.dto.WalletDTO;

public interface WalletService {
    WalletDTO getWalletByUserId(Long userId);
    WalletDTO createWallet(Long userId, String currency);
    WalletDTO updateBalance(Long userId, BigDecimal amount);
}
