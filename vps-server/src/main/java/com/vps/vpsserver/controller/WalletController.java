package com.vps.vpsserver.controller;

import java.math.BigDecimal;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.vps.vpsserver.dto.WalletDTO;
import com.vps.vpsserver.entity.User;
import com.vps.vpsserver.service.WalletService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/wallet")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class WalletController {

    private final WalletService walletService;

    @GetMapping
    public ResponseEntity<WalletDTO> getWallet(@AuthenticationPrincipal User user) {
        WalletDTO wallet = walletService.getWalletByUserId(user.getId());
        return ResponseEntity.ok(wallet);
    }

    @PostMapping
    public ResponseEntity<WalletDTO> createWallet(@AuthenticationPrincipal User user) {
        WalletDTO wallet = walletService.createWallet(user.getId(), "CNY");
        return ResponseEntity.ok(wallet);
    }

    @PutMapping("/balance")
    public ResponseEntity<WalletDTO> updateBalance(
            @RequestParam BigDecimal amount,
            @AuthenticationPrincipal User user) {
        WalletDTO wallet = walletService.updateBalance(user.getId(), amount);
        return ResponseEntity.ok(wallet);
    }
}
