package com.vps.vpsserver.service.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vps.vpsserver.config.PaymentConfig;
import com.vps.vpsserver.dto.CreatePaymentRequest;
import com.vps.vpsserver.dto.PaymentResponse;
import com.vps.vpsserver.entity.PaymentOrder;
import com.vps.vpsserver.entity.Transaction;
import com.vps.vpsserver.entity.User;
import com.vps.vpsserver.repository.PaymentOrderRepository;
import com.vps.vpsserver.repository.UserRepository;
import com.vps.vpsserver.service.PaymentService;
import com.vps.vpsserver.service.TransactionService;
import com.vps.vpsserver.service.WalletService;
import com.vps.vpsserver.util.PaymentUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentServiceImpl implements PaymentService {

    private final PaymentOrderRepository paymentOrderRepository;
    private final UserRepository userRepository;
    private final WalletService walletService;
    private final TransactionService transactionService;
    private final PaymentConfig paymentConfig;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    @Transactional
    public PaymentResponse createPayment(CreatePaymentRequest request, Long userId, String clientIp) {
        // 获取用户信息
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));

        // 创建支付订单
        PaymentOrder paymentOrder = new PaymentOrder();
        paymentOrder.setUser(user);
        paymentOrder.setType(PaymentOrder.PaymentType.valueOf(request.getType().toUpperCase()));
        paymentOrder.setMoney(request.getMoney());
        paymentOrder.setName(request.getName());
        paymentOrder.setClientIp(clientIp);
        paymentOrder.setDevice(request.getDevice());
        paymentOrder.setParam(request.getParam());
        
        paymentOrder = paymentOrderRepository.save(paymentOrder);

        // 构建支付参数
        Map<String, String> params = new HashMap<>();
        params.put("pid", paymentConfig.getPid());
        params.put("type", request.getType());
        params.put("out_trade_no", paymentOrder.getOutTradeNo());
        params.put("notify_url", paymentConfig.getNotifyUrl());
        params.put("return_url", paymentConfig.getReturnUrl());
        params.put("name", request.getName());
        params.put("money", request.getMoney().toString());
        params.put("clientip", clientIp);
        params.put("device", request.getDevice());
        if (request.getParam() != null) {
            params.put("param", request.getParam());
        }
        params.put("sign_type", "MD5");

        // 生成签名
        String sign = PaymentUtil.generateSign(params, paymentConfig.getKey());
        params.put("sign", sign);

        try {
            // 调用易支付API
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
            params.forEach(formData::add);

            HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(formData, headers);
            ResponseEntity<String> response = restTemplate.postForEntity(paymentConfig.getApiUrl(), entity, String.class);

            if (response.getStatusCode() == HttpStatus.OK) {
                JsonNode jsonNode = objectMapper.readTree(response.getBody());
                
                PaymentResponse paymentResponse = new PaymentResponse();
                paymentResponse.setCode(jsonNode.get("code").asInt());
                paymentResponse.setMsg(jsonNode.has("msg") ? jsonNode.get("msg").asText() : null);
                paymentResponse.setOutTradeNo(paymentOrder.getOutTradeNo());

                if (paymentResponse.getCode() == 1) {
                    // 支付请求成功
                    if (jsonNode.has("trade_no")) {
                        paymentResponse.setTradeNo(jsonNode.get("trade_no").asText());
                        paymentOrder.setTradeNo(paymentResponse.getTradeNo());
                    }
                    if (jsonNode.has("payurl")) {
                        paymentResponse.setPayUrl(jsonNode.get("payurl").asText());
                        paymentOrder.setPayUrl(paymentResponse.getPayUrl());
                    }
                    if (jsonNode.has("qrcode")) {
                        paymentResponse.setQrCode(jsonNode.get("qrcode").asText());
                        paymentOrder.setQrCode(paymentResponse.getQrCode());
                    }
                    if (jsonNode.has("urlscheme")) {
                        paymentResponse.setUrlScheme(jsonNode.get("urlscheme").asText());
                        paymentOrder.setUrlScheme(paymentResponse.getUrlScheme());
                    }
                    
                    paymentOrderRepository.save(paymentOrder);
                }

                return paymentResponse;
            } else {
                throw new RuntimeException("支付接口调用失败");
            }
        } catch (Exception e) {
            log.error("创建支付订单失败", e);
            throw new RuntimeException("创建支付订单失败: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public boolean handlePaymentNotify(Map<String, String> params) {
        try {
            // 验证签名
            String sign = params.get("sign");
            if (!PaymentUtil.verifySign(params, paymentConfig.getKey(), sign)) {
                log.error("支付通知签名验证失败");
                return false;
            }

            String outTradeNo = params.get("out_trade_no");
            String tradeStatus = params.get("trade_status");

            // 查找支付订单
            Optional<PaymentOrder> optionalPaymentOrder = paymentOrderRepository.findByOutTradeNo(outTradeNo);
            if (optionalPaymentOrder.isEmpty()) {
                log.error("支付订单不存在: {}", outTradeNo);
                return false;
            }

            PaymentOrder paymentOrder = optionalPaymentOrder.get();
            
            // 检查订单状态，避免重复处理
            if (paymentOrder.getStatus() == PaymentOrder.PaymentStatus.SUCCESS) {
                log.info("订单已处理过: {}", outTradeNo);
                return true;
            }

            if ("TRADE_SUCCESS".equals(tradeStatus)) {
                // 支付成功，更新订单状态
                paymentOrder.setStatus(PaymentOrder.PaymentStatus.SUCCESS);
                paymentOrder.setTradeNo(params.get("trade_no"));
                paymentOrder.setApiTradeNo(params.get("api_trade_no"));
                paymentOrder.setBuyer(params.get("buyer"));
                paymentOrderRepository.save(paymentOrder);

                // 增加用户余额
                walletService.updateBalance(paymentOrder.getUser().getId(), paymentOrder.getMoney());

                // 创建交易记录
                Transaction transaction = new Transaction();
                transaction.setUser(paymentOrder.getUser());
                transaction.setType(Transaction.TransactionType.RECHARGE);
                transaction.setStatus(Transaction.TransactionStatus.COMPLETED);
                transaction.setAmount(paymentOrder.getMoney());
                transaction.setDescription("账户充值 - " + paymentOrder.getName());
                transaction.setPaymentMethod(paymentOrder.getType().getName());
                transaction.setExternalTransactionId(paymentOrder.getTradeNo());
                transactionService.createTransaction(transaction);

                log.info("支付成功处理完成: {}", outTradeNo);
                return true;
            } else {
                // 支付失败
                paymentOrder.setStatus(PaymentOrder.PaymentStatus.FAILED);
                paymentOrderRepository.save(paymentOrder);
                log.info("支付失败: {}", outTradeNo);
                return true;
            }
        } catch (Exception e) {
            log.error("处理支付通知失败", e);
            return false;
        }
    }

    @Override
    public PaymentOrder getPaymentOrder(String outTradeNo) {
        return paymentOrderRepository.findByOutTradeNo(outTradeNo)
                .orElseThrow(() -> new RuntimeException("支付订单不存在"));
    }

    @Override
    public Page<PaymentOrder> getUserPaymentOrders(Long userId, Pageable pageable) {
        return paymentOrderRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable);
    }
}