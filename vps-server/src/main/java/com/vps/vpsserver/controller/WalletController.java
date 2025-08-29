package com.vps.vpsserver.controller;

import java.math.BigDecimal;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.vps.vpsserver.dto.WalletDTO;
import com.vps.vpsserver.service.WalletService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/wallet")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class WalletController {

    private final WalletService walletService;

    @GetMapping
    public ResponseEntity<WalletDTO> getWallet(Authentication authentication) {
        Long userId = getUserIdFromAuthentication(authentication);
        WalletDTO wallet = walletService.getWalletByUserId(userId);
        return ResponseEntity.ok(wallet);
    }

    @PostMapping
    public ResponseEntity<WalletDTO> createWallet(Authentication authentication) {
        Long userId = getUserIdFromAuthentication(authentication);
        WalletDTO wallet = walletService.createWallet(userId, "CNY");
        return ResponseEntity.ok(wallet);
    }

    @PutMapping("/balance")
    public ResponseEntity<WalletDTO> updateBalance(
            @RequestParam BigDecimal amount,
            Authentication authentication) {
        Long userId = getUserIdFromAuthentication(authentication);
        WalletDTO wallet = walletService.updateBalance(userId, amount);
        return ResponseEntity.ok(wallet);
    }



    private Long getUserIdFromAuthentication(Authentication authentication) {
        // 这里需要根据你的用户认证实现来获取用户ID
        // 假设UserDetails中包含用户ID信息
        if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
            // 这里需要根据实际的User实体实现来获取ID
            // 暂时返回1作为示例，实际使用时需要修改
            return 1L;
        }
        throw new RuntimeException("User not authenticated");
    }
}
