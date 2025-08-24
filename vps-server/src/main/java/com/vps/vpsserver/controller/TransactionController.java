package com.vps.vpsserver.controller;

import com.vps.vpsserver.dto.CreateRechargeRequest;
import com.vps.vpsserver.dto.TransactionDTO;
import com.vps.vpsserver.entity.Transaction;
import com.vps.vpsserver.entity.User;
import com.vps.vpsserver.service.TransactionService;
import com.vps.vpsserver.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;
    

    /**
     * 获取用户交易记录
     */
    @GetMapping("/user")
    public ResponseEntity<ApiResponse<List<TransactionDTO>>> getUserTransactions(
            @AuthenticationPrincipal User user) {
        try {
            List<TransactionDTO> transactions = transactionService.getUserTransactions(user);
            return ResponseEntity.ok(ApiResponse.success(transactions));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * 根据类型获取用户交易记录
     */
    @GetMapping("/user/type/{type}")
    public ResponseEntity<ApiResponse<List<TransactionDTO>>> getUserTransactionsByType(
            @AuthenticationPrincipal User user,
            @PathVariable Transaction.TransactionType type) {
        try {
            List<TransactionDTO> transactions = transactionService.getUserTransactionsByType(user, type);
            return ResponseEntity.ok(ApiResponse.success(transactions));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * 根据状态获取用户交易记录
     */
    @GetMapping("/user/status/{status}")
    public ResponseEntity<ApiResponse<List<TransactionDTO>>> getUserTransactionsByStatus(
            @AuthenticationPrincipal User user,
            @PathVariable Transaction.TransactionStatus status) {
        try {
            List<TransactionDTO> transactions = transactionService.getUserTransactionsByStatus(user, status);
            return ResponseEntity.ok(ApiResponse.success(transactions));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * 根据日期范围获取用户交易记录
     */
    @GetMapping("/user/date-range")
    public ResponseEntity<ApiResponse<List<TransactionDTO>>> getUserTransactionsByDateRange(
            @AuthenticationPrincipal User user,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        try {
            List<TransactionDTO> transactions = transactionService.getUserTransactionsByDateRange(user, startDate, endDate);
            return ResponseEntity.ok(ApiResponse.success(transactions));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * 根据交易号获取交易记录
     */
    @GetMapping("/number/{transactionNumber}")
    public ResponseEntity<ApiResponse<TransactionDTO>> getTransactionByNumber(
            @PathVariable String transactionNumber) {
        try {
            TransactionDTO transaction = transactionService.getTransactionByNumber(transactionNumber);
            return ResponseEntity.ok(ApiResponse.success(transaction));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * 根据订单获取交易记录
     */
    @GetMapping("/order/{orderId}")
    public ResponseEntity<ApiResponse<List<TransactionDTO>>> getTransactionsByOrder(
            @PathVariable Long orderId) {
        try {
            List<TransactionDTO> transactions = transactionService.getTransactionsByOrder(orderId);
            return ResponseEntity.ok(ApiResponse.success(transactions));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * 创建充值交易
     */
    @PostMapping("/recharge")
    public ResponseEntity<ApiResponse<TransactionDTO>> createRecharge(
            @RequestBody CreateRechargeRequest request,
            @AuthenticationPrincipal User user) {
        try {
            TransactionDTO transaction = transactionService.createRechargeTransaction(request, user);
            return ResponseEntity.ok(ApiResponse.success(transaction, "充值交易已创建"));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
}
