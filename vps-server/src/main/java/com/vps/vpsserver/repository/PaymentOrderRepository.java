package com.vps.vpsserver.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.vps.vpsserver.entity.PaymentOrder;

@Repository
public interface PaymentOrderRepository extends JpaRepository<PaymentOrder, Long> {
    
    Optional<PaymentOrder> findByOutTradeNo(String outTradeNo);
    
    Optional<PaymentOrder> findByTradeNo(String tradeNo);
    
    Page<PaymentOrder> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);
}