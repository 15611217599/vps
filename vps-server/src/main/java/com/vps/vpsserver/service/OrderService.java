package com.vps.vpsserver.service;

import com.vps.vpsserver.dto.CreateOrderRequest;
import com.vps.vpsserver.dto.OrderDTO;
import com.vps.vpsserver.entity.Order;
import com.vps.vpsserver.entity.User;

import java.util.List;

public interface OrderService {
    
    /**
     * 创建订单
     */
    OrderDTO createOrder(CreateOrderRequest request, User user);
    
    /**
     * 根据ID获取订单
     */
    OrderDTO getOrderById(Long id, User user);
    
    /**
     * 获取用户的所有订单
     */
    List<OrderDTO> getUserOrders(User user);
    
    /**
     * 根据订单号获取订单
     */
    OrderDTO getOrderByNumber(String orderNumber, User user);
    
    /**
     * 更新订单状态
     */
    OrderDTO updateOrderStatus(Long orderId, Order.OrderStatus status, User user);
    
    /**
     * 取消订单
     */
    OrderDTO cancelOrder(Long orderId, User user);
    
    /**
     * 处理订单支付
     */
    OrderDTO processOrderPayment(Long orderId, User user);
}
