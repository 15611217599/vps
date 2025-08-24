package com.vps.vpsserver.service.impl;

import com.vps.vpsserver.dto.CreateRechargeRequest;
import com.vps.vpsserver.dto.TransactionDTO;
import com.vps.vpsserver.entity.Transaction;
import com.vps.vpsserver.entity.User;
import com.vps.vpsserver.repository.TransactionRepository;
import com.vps.vpsserver.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    

    @Override
    @Transactional
    public TransactionDTO createOrderPaymentTransaction(User user, Long orderId, String orderNumber,
                                                       BigDecimal amount, BigDecimal balanceBefore,
                                                       BigDecimal balanceAfter) {
        Transaction transaction = new Transaction();
        transaction.setUser(user);
        transaction.setType(Transaction.TransactionType.ORDER_PAYMENT);
        transaction.setStatus(Transaction.TransactionStatus.COMPLETED);
        transaction.setAmount(amount);
        transaction.setBalanceBefore(balanceBefore);
        transaction.setBalanceAfter(balanceAfter);
        transaction.setOrderId(orderId);
        transaction.setOrderNumber(orderNumber);
        transaction.setPaymentMethod("WALLET");
        transaction.setDescription("订单支付: " + orderNumber);
        transaction.setCompletedAt(LocalDateTime.now());

        transaction = transactionRepository.save(transaction);
        return convertToDTO(transaction);
    }

    @Override
    @Transactional
    public TransactionDTO createRechargeTransaction(CreateRechargeRequest request, User user) {
        Transaction transaction = new Transaction();
        transaction.setUser(user);
        transaction.setType(Transaction.TransactionType.RECHARGE);
        transaction.setStatus(Transaction.TransactionStatus.PENDING);
        transaction.setAmount(request.getAmount());
        transaction.setPaymentMethod(request.getPaymentMethod());
        transaction.setDescription(request.getDescription() != null ? request.getDescription() : "账户充值");

        transaction = transactionRepository.save(transaction);
        return convertToDTO(transaction);
    }

    @Override
    @Transactional
    public TransactionDTO createRefundTransaction(User user, Long orderId, String orderNumber,
                                                BigDecimal amount, String reason) {
        Transaction transaction = new Transaction();
        transaction.setUser(user);
        transaction.setType(Transaction.TransactionType.REFUND);
        transaction.setStatus(Transaction.TransactionStatus.PENDING);
        transaction.setAmount(amount);
        transaction.setOrderId(orderId);
        transaction.setOrderNumber(orderNumber);
        transaction.setDescription("订单退款: " + orderNumber + (reason != null ? " - " + reason : ""));

        transaction = transactionRepository.save(transaction);
        return convertToDTO(transaction);
    }

    @Override
    @Transactional
    public TransactionDTO createAdjustmentTransaction(User user, BigDecimal amount, String reason,
                                                    BigDecimal balanceBefore, BigDecimal balanceAfter) {
        Transaction transaction = new Transaction();
        transaction.setUser(user);
        transaction.setType(Transaction.TransactionType.ADJUSTMENT);
        transaction.setStatus(Transaction.TransactionStatus.COMPLETED);
        transaction.setAmount(amount);
        transaction.setBalanceBefore(balanceBefore);
        transaction.setBalanceAfter(balanceAfter);
        transaction.setDescription("余额调整: " + (reason != null ? reason : "管理员调整"));
        transaction.setCompletedAt(LocalDateTime.now());

        transaction = transactionRepository.save(transaction);
        return convertToDTO(transaction);
    }

    @Override
    @Transactional
    public TransactionDTO completeTransaction(Long transactionId) {
        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new RuntimeException("未找到交易记录"));

        if (transaction.getStatus() != Transaction.TransactionStatus.PENDING &&
            transaction.getStatus() != Transaction.TransactionStatus.PROCESSING) {
            throw new RuntimeException("该交易状态无法标记为完成");
        }

        transaction.setStatus(Transaction.TransactionStatus.COMPLETED);
        transaction.setCompletedAt(LocalDateTime.now());

        transaction = transactionRepository.save(transaction);
        return convertToDTO(transaction);
    }

    @Override
    @Transactional
    public TransactionDTO cancelTransaction(Long transactionId, String reason) {
        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new RuntimeException("未找到交易记录"));

        if (transaction.getStatus() == Transaction.TransactionStatus.COMPLETED) {
            throw new RuntimeException("已完成的交易不能取消");
        }

        transaction.setStatus(Transaction.TransactionStatus.CANCELLED);
        if (reason != null) {
            transaction.setDescription(transaction.getDescription() + " - 取消原因: " + reason);
        }

        transaction = transactionRepository.save(transaction);
        return convertToDTO(transaction);
    }

    @Override
    public List<TransactionDTO> getUserTransactions(User user) {
        List<Transaction> transactions = transactionRepository.findByUserOrderByCreatedAtDesc(user);
        return transactions.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    @Override
    public List<TransactionDTO> getUserTransactionsByType(User user, Transaction.TransactionType type) {
        List<Transaction> transactions = transactionRepository.findByUserAndTypeOrderByCreatedAtDesc(user, type);
        return transactions.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    @Override
    public List<TransactionDTO> getUserTransactionsByStatus(User user, Transaction.TransactionStatus status) {
        List<Transaction> transactions = transactionRepository.findByUserAndStatusOrderByCreatedAtDesc(user, status);
        return transactions.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    @Override
    public List<TransactionDTO> getUserTransactionsByDateRange(User user, LocalDateTime startDate, LocalDateTime endDate) {
        List<Transaction> transactions = transactionRepository.findByUserAndDateRange(user, startDate, endDate);
        return transactions.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    @Override
    public TransactionDTO getTransactionByNumber(String transactionNumber) {
        Transaction transaction = transactionRepository.findByTransactionNumber(transactionNumber)
                .orElseThrow(() -> new RuntimeException("未找到交易记录"));
        return convertToDTO(transaction);
    }

    @Override
    public List<TransactionDTO> getTransactionsByOrder(Long orderId) {
        List<Transaction> transactions = transactionRepository.findByOrderId(orderId);
        return transactions.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    private TransactionDTO convertToDTO(Transaction transaction) {
        TransactionDTO dto = new TransactionDTO();
        dto.setId(transaction.getId());
        dto.setUserId(transaction.getUser().getId());
        dto.setUsername(transaction.getUser().getUsername());
        dto.setTransactionNumber(transaction.getTransactionNumber());
        dto.setType(transaction.getType());
        dto.setStatus(transaction.getStatus());
        dto.setAmount(transaction.getAmount());
        dto.setCurrency(transaction.getCurrency());
        dto.setBalanceBefore(transaction.getBalanceBefore());
        dto.setBalanceAfter(transaction.getBalanceAfter());
        dto.setDescription(transaction.getDescription());
        dto.setOrderId(transaction.getOrderId());
        dto.setOrderNumber(transaction.getOrderNumber());
        dto.setPaymentMethod(transaction.getPaymentMethod());
        dto.setExternalTransactionId(transaction.getExternalTransactionId());
        dto.setCreatedAt(transaction.getCreatedAt());
        dto.setUpdatedAt(transaction.getUpdatedAt());
        dto.setCompletedAt(transaction.getCompletedAt());
        return dto;
    }
}
