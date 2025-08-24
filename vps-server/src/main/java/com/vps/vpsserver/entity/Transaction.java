package com.vps.vpsserver.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "transactions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "transaction_number", unique = true, nullable = false)
    private String transactionNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionStatus status = TransactionStatus.PENDING;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    @Column(nullable = false, length = 3)
    private String currency = "CNY";

    @Column(name = "balance_before", precision = 19, scale = 2)
    private BigDecimal balanceBefore;

    @Column(name = "balance_after", precision = 19, scale = 2)
    private BigDecimal balanceAfter;

    @Column(name = "description")
    private String description;

    // 关联的订单ID（如果是订单支付）
    @Column(name = "order_id")
    private Long orderId;

    // 关联的订单号（如果是订单支付）
    @Column(name = "order_number")
    private String orderNumber;

    // 支付方式
    @Column(name = "payment_method")
    private String paymentMethod;

    // 第三方交易号
    @Column(name = "external_transaction_id")
    private String externalTransactionId;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (transactionNumber == null) {
            transactionNumber = generateTransactionNumber();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
        if (status == TransactionStatus.COMPLETED && completedAt == null) {
            completedAt = LocalDateTime.now();
        }
    }

    private String generateTransactionNumber() {
        return "TXN" + System.currentTimeMillis() + String.format("%04d", (int)(Math.random() * 10000));
    }

    public enum TransactionType {
        RECHARGE,        // 充值
        ORDER_PAYMENT,   // 订单支付
        REFUND,          // 退款
        WITHDRAWAL,      // 提现
        ADJUSTMENT       // 余额调整
    }

    public enum TransactionStatus {
        PENDING,         // 待处理
        PROCESSING,      // 处理中
        COMPLETED,       // 已完成
        FAILED,          // 失败
        CANCELLED        // 已取消
    }
}
