package com.vps.vpsserver.service;

import com.vps.vpsserver.dto.CreateRechargeRequest;
import com.vps.vpsserver.dto.TransactionDTO;
import com.vps.vpsserver.entity.Transaction;
import com.vps.vpsserver.entity.User;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface TransactionService {
    
    /**
     * 创建订单支付交易记录
     */
    TransactionDTO createOrderPaymentTransaction(User user, Long orderId, String orderNumber, 
                                               BigDecimal amount, BigDecimal balanceBefore, 
                                               BigDecimal balanceAfter);
    
    /**
     * 创建充值交易记录
     */
    TransactionDTO createRechargeTransaction(CreateRechargeRequest request, User user);
    
    /**
     * 创建退款交易记录
     */
    TransactionDTO createRefundTransaction(User user, Long orderId, String orderNumber, 
                                         BigDecimal amount, String reason);
    
    /**
     * 创建余额调整交易记录
     */
    TransactionDTO createAdjustmentTransaction(User user, BigDecimal amount, String reason, 
                                             BigDecimal balanceBefore, BigDecimal balanceAfter);
    
    /**
     * 完成交易
     */
    TransactionDTO completeTransaction(Long transactionId);
    
    /**
     * 取消交易
     */
    TransactionDTO cancelTransaction(Long transactionId, String reason);
    
    /**
     * 获取用户交易记录
     */
    List<TransactionDTO> getUserTransactions(User user);
    
    /**
     * 根据类型获取用户交易记录
     */
    List<TransactionDTO> getUserTransactionsByType(User user, Transaction.TransactionType type);
    
    /**
     * 根据状态获取用户交易记录
     */
    List<TransactionDTO> getUserTransactionsByStatus(User user, Transaction.TransactionStatus status);
    
    /**
     * 根据日期范围获取用户交易记录
     */
    List<TransactionDTO> getUserTransactionsByDateRange(User user, LocalDateTime startDate, LocalDateTime endDate);
    
    /**
     * 根据交易号获取交易记录
     */
    TransactionDTO getTransactionByNumber(String transactionNumber);
    
    /**
     * 根据订单获取交易记录
     */
    List<TransactionDTO> getTransactionsByOrder(Long orderId);
}
