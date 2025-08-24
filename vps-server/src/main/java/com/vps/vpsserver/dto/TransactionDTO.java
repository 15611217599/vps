package com.vps.vpsserver.dto;

import com.vps.vpsserver.entity.Transaction;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionDTO {
    private Long id;
    private Long userId;
    private String username;
    private String transactionNumber;
    private Transaction.TransactionType type;
    private Transaction.TransactionStatus status;
    private BigDecimal amount;
    private String currency;
    private BigDecimal balanceBefore;
    private BigDecimal balanceAfter;
    private String description;
    private Long orderId;
    private String orderNumber;
    private String paymentMethod;
    private String externalTransactionId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime completedAt;
}
