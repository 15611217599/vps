package com.vps.vpsserver.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.vps.vpsserver.dto.ApiResponse;
import com.vps.vpsserver.dto.CreateOrderRequest;
import com.vps.vpsserver.dto.OrderDTO;
import com.vps.vpsserver.entity.Order;
import com.vps.vpsserver.entity.User;
import com.vps.vpsserver.service.OrderService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<ApiResponse<OrderDTO>> createOrder(
            @RequestBody CreateOrderRequest request,
            @AuthenticationPrincipal User user) {
        try {
            OrderDTO order = orderService.createOrder(request, user);
            return ResponseEntity.ok(ApiResponse.success(order, "订单创建成功"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<OrderDTO>>> getUserOrders(
            @AuthenticationPrincipal User user) {
        try {
            List<OrderDTO> orders = orderService.getUserOrders(user);
            return ResponseEntity.ok(ApiResponse.success(orders));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<OrderDTO>> getOrderById(
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {
        try {
            OrderDTO order = orderService.getOrderById(id, user);
            return ResponseEntity.ok(ApiResponse.success(order));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/number/{orderNumber}")
    public ResponseEntity<ApiResponse<OrderDTO>> getOrderByNumber(
            @PathVariable String orderNumber,
            @AuthenticationPrincipal User user) {
        try {
            OrderDTO order = orderService.getOrderByNumber(orderNumber, user);
            return ResponseEntity.ok(ApiResponse.success(order));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<ApiResponse<OrderDTO>> updateOrderStatus(
            @PathVariable Long id,
            @RequestParam Order.OrderStatus status,
            @AuthenticationPrincipal User user) {
        try {
            OrderDTO order = orderService.updateOrderStatus(id, status, user);
            return ResponseEntity.ok(ApiResponse.success(order, "订单状态更新成功"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<ApiResponse<OrderDTO>> cancelOrder(
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {
        try {
            OrderDTO order = orderService.cancelOrder(id, user);
            return ResponseEntity.ok(ApiResponse.success(order, "订单取消成功"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @PostMapping("/{id}/pay")
    public ResponseEntity<ApiResponse<OrderDTO>> processOrderPayment(
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {
        try {
            OrderDTO order = orderService.processOrderPayment(id, user);
            return ResponseEntity.ok(ApiResponse.success(order, "订单支付成功"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @PutMapping("/{id}/auto-renewal")
    public ResponseEntity<ApiResponse<OrderDTO>> updateAutoRenewal(
            @PathVariable Long id,
            @RequestBody UpdateAutoRenewalRequest request,
            @AuthenticationPrincipal User user) {
        try {
            OrderDTO order = orderService.updateAutoRenewal(id, request.getAutoRenewal(), user);
            return ResponseEntity.ok(ApiResponse.success(order, "自动续费设置更新成功"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    // 内部类用于接收自动续费请求
    public static class UpdateAutoRenewalRequest {
        private Boolean autoRenewal;

        public Boolean getAutoRenewal() {
            return autoRenewal;
        }

        public void setAutoRenewal(Boolean autoRenewal) {
            this.autoRenewal = autoRenewal;
        }
    }
}
