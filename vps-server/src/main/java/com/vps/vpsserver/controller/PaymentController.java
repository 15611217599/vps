package com.vps.vpsserver.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.vps.vpsserver.dto.ApiResponse;
import com.vps.vpsserver.dto.CreatePaymentRequest;
import com.vps.vpsserver.dto.PaymentResponse;
import com.vps.vpsserver.entity.PaymentOrder;
import com.vps.vpsserver.service.PaymentService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/payment")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Slf4j
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/create")
    public ResponseEntity<ApiResponse<PaymentResponse>> createPayment(
            @RequestBody CreatePaymentRequest request,
            HttpServletRequest httpRequest,
            Authentication authentication) {
        
        Long userId = getUserIdFromAuthentication(authentication);
        String clientIp = getClientIp(httpRequest);
        
        PaymentResponse response = paymentService.createPayment(request, userId, clientIp);
        
        if (response.getCode() == 1) {
            return ResponseEntity.ok(ApiResponse.success(response));
        } else {
            return ResponseEntity.ok(ApiResponse.error(response.getMsg()));
        }
    }

    @PostMapping("/notify")
    public ResponseEntity<String> handleNotify(HttpServletRequest request) {
        Map<String, String> params = new HashMap<>();
        request.getParameterMap().forEach((key, values) -> {
            if (values.length > 0) {
                params.put(key, values[0]);
            }
        });

        log.info("收到支付通知: {}", params);

        boolean success = paymentService.handlePaymentNotify(params);
        return ResponseEntity.ok(success ? "success" : "fail");
    }

    @GetMapping("/return")
    public ResponseEntity<String> handleReturn(HttpServletRequest request) {
        Map<String, String> params = new HashMap<>();
        request.getParameterMap().forEach((key, values) -> {
            if (values.length > 0) {
                params.put(key, values[0]);
            }
        });

        log.info("收到支付返回: {}", params);

        // 页面跳转通知，通常用于前端页面跳转
        // 这里可以返回一个重定向到前端支付结果页面的响应
        String outTradeNo = params.get("out_trade_no");
        String tradeStatus = params.get("trade_status");
        
        if ("TRADE_SUCCESS".equals(tradeStatus)) {
            return ResponseEntity.ok("<!DOCTYPE html><html><head><title>支付成功</title></head><body><script>window.location.href='http://localhost:3000/payment/success?order=" + outTradeNo + "';</script></body></html>");
        } else {
            return ResponseEntity.ok("<!DOCTYPE html><html><head><title>支付失败</title></head><body><script>window.location.href='http://localhost:3000/payment/failed?order=" + outTradeNo + "';</script></body></html>");
        }
    }

    @GetMapping("/order/{outTradeNo}")
    public ResponseEntity<ApiResponse<PaymentOrder>> getPaymentOrder(
            @PathVariable String outTradeNo,
            Authentication authentication) {
        
        PaymentOrder paymentOrder = paymentService.getPaymentOrder(outTradeNo);
        return ResponseEntity.ok(ApiResponse.success(paymentOrder));
    }

    @GetMapping("/orders")
    public ResponseEntity<ApiResponse<Page<PaymentOrder>>> getUserPaymentOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Authentication authentication) {
        
        Long userId = getUserIdFromAuthentication(authentication);
        Pageable pageable = PageRequest.of(page, size);
        Page<PaymentOrder> orders = paymentService.getUserPaymentOrders(userId, pageable);
        
        return ResponseEntity.ok(ApiResponse.success(orders));
    }

    private Long getUserIdFromAuthentication(Authentication authentication) {
        // 这里需要根据你的用户认证实现来获取用户ID
        // 暂时返回1作为示例，实际使用时需要修改
        return 1L;
    }

    private String getClientIp(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty() && !"unknown".equalsIgnoreCase(xForwardedFor)) {
            return xForwardedFor.split(",")[0];
        }
        
        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty() && !"unknown".equalsIgnoreCase(xRealIp)) {
            return xRealIp;
        }
        
        return request.getRemoteAddr();
    }
}