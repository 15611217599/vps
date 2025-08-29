package com.vps.vpsserver.service;

import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.vps.vpsserver.dto.CreatePaymentRequest;
import com.vps.vpsserver.dto.PaymentResponse;
import com.vps.vpsserver.entity.PaymentOrder;

public interface PaymentService {
    
    /**
     * 创建支付订单
     * @param request 支付请求
     * @param userId 用户ID
     * @param clientIp 客户端IP
     * @return 支付响应
     */
    PaymentResponse createPayment(CreatePaymentRequest request, Long userId, String clientIp);
    
    /**
     * 处理支付通知
     * @param params 通知参数
     * @return 处理结果
     */
    boolean handlePaymentNotify(Map<String, String> params);
    
    /**
     * 查询支付订单
     * @param outTradeNo 商户订单号
     * @return 支付订单
     */
    PaymentOrder getPaymentOrder(String outTradeNo);
    
    /**
     * 获取用户支付订单列表
     * @param userId 用户ID
     * @param pageable 分页参数
     * @return 支付订单列表
     */
    Page<PaymentOrder> getUserPaymentOrders(Long userId, Pageable pageable);
}