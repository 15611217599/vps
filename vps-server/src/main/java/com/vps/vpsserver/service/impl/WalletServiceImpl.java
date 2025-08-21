package com.vps.vpsserver.service.impl;

import com.vps.vpsserver.dto.WalletDTO;
import com.vps.vpsserver.entity.User;
import com.vps.vpsserver.entity.Wallet;
import com.vps.vpsserver.repository.UserRepository;
import com.vps.vpsserver.repository.WalletRepository;
import com.vps.vpsserver.service.WalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Transactional
public class WalletServiceImpl implements WalletService {

    private final WalletRepository walletRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public WalletDTO getWalletByUserId(Long userId) {
        Wallet wallet = walletRepository.findByUserId(userId)
                .orElseGet(() -> createWalletEntity(userId));
        return convertToDTO(wallet);
    }

    @Override
    public WalletDTO createWallet(Long userId, String currency) {
        if (walletRepository.existsByUserId(userId)) {
            throw new RuntimeException("Wallet already exists for user: " + userId);
        }
        
        Wallet wallet = createWalletEntity(userId);
        if (currency != null && !currency.trim().isEmpty()) {
            wallet.setCurrency(currency);
        }
        wallet = walletRepository.save(wallet);
        return convertToDTO(wallet);
    }

    @Override
    public WalletDTO updateBalance(Long userId, BigDecimal amount) {
        Wallet wallet = walletRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Wallet not found for user: " + userId));
        
        wallet.setBalance(amount);
        wallet = walletRepository.save(wallet);
        return convertToDTO(wallet);
    }

    @Override
    public WalletDTO updateCurrency(Long userId, String currency) {
        Wallet wallet = walletRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Wallet not found for user: " + userId));
        
        wallet.setCurrency(currency);
        wallet = walletRepository.save(wallet);
        return convertToDTO(wallet);
    }

    private Wallet createWalletEntity(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found: " + userId));
        
        Wallet wallet = new Wallet();
        wallet.setUser(user);
        wallet.setBalance(BigDecimal.ZERO);
        wallet.setCurrency("USD");
        
        return walletRepository.save(wallet);
    }

    private WalletDTO convertToDTO(Wallet wallet) {
        WalletDTO dto = new WalletDTO();
        dto.setId(wallet.getId());
        dto.setUserId(wallet.getUser().getId());
        dto.setBalance(wallet.getBalance());
        dto.setCurrency(wallet.getCurrency());
        dto.setCreatedAt(wallet.getCreatedAt());
        dto.setUpdatedAt(wallet.getUpdatedAt());
        return dto;
    }
}
