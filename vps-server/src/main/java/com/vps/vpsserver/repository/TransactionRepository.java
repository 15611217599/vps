package com.vps.vpsserver.repository;

import com.vps.vpsserver.entity.Transaction;
import com.vps.vpsserver.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    
    Optional<Transaction> findByTransactionNumber(String transactionNumber);
    
    List<Transaction> findByUserOrderByCreatedAtDesc(User user);
    
    List<Transaction> findByUserAndTypeOrderByCreatedAtDesc(User user, Transaction.TransactionType type);
    
    List<Transaction> findByUserAndStatusOrderByCreatedAtDesc(User user, Transaction.TransactionStatus status);
    
    List<Transaction> findByOrderId(Long orderId);
    
    List<Transaction> findByOrderNumber(String orderNumber);
    
    @Query("SELECT t FROM Transaction t WHERE t.user = :user AND t.createdAt BETWEEN :startDate AND :endDate ORDER BY t.createdAt DESC")
    List<Transaction> findByUserAndDateRange(@Param("user") User user, 
                                           @Param("startDate") LocalDateTime startDate, 
                                           @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT t FROM Transaction t WHERE t.createdAt BETWEEN :startDate AND :endDate ORDER BY t.createdAt DESC")
    List<Transaction> findByDateRange(@Param("startDate") LocalDateTime startDate, 
                                    @Param("endDate") LocalDateTime endDate);
}
