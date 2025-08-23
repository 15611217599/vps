package com.vps.vpsserver.service.impl;

import com.vps.vpsserver.dto.CreateOrderRequest;
import com.vps.vpsserver.dto.OrderDTO;
import com.vps.vpsserver.entity.*;
import com.vps.vpsserver.repository.OrderRepository;
import com.vps.vpsserver.repository.PriceGroupRepository;
import com.vps.vpsserver.repository.ServerRepository;
import com.vps.vpsserver.repository.WalletRepository;
import com.vps.vpsserver.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final PriceGroupRepository priceGroupRepository;
    private final ServerRepository serverRepository;
    private final WalletRepository walletRepository;

    @Override
    @Transactional
    public OrderDTO createOrder(CreateOrderRequest request, User user) {
        // 获取价格组
        PriceGroup priceGroup = priceGroupRepository.findById(request.getPriceGroupId())
                .orElseThrow(() -> new RuntimeException("价格组不存在"));

        // 计算订单金额
        BigDecimal amount = calculateOrderAmount(priceGroup, request.getBillingPeriod());

        // 检查用户余额
        Wallet wallet = walletRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("用户钱包不存在"));

        if (wallet.getBalance().compareTo(amount) < 0) {
            throw new RuntimeException("余额不足，当前余额: ¥" + wallet.getBalance() + "，需要: ¥" + amount);
        }

        // 分配服务器
        Server assignedServer = assignServerFromGroup(priceGroup.getServerGroup());

        // 创建订单
        Order order = new Order();
        order.setUser(user);
        order.setPriceGroup(priceGroup);
        order.setServer(assignedServer);
        order.setAmount(amount);
        order.setBillingPeriod(request.getBillingPeriod());
        order.setCpuCores(request.getCpuCores());
        order.setMemoryGb(request.getMemoryGb());
        order.setStorageGb(request.getStorageGb());
        order.setBandwidthMbps(request.getBandwidthMbps());
        order.setIpCount(request.getIpCount());
        order.setOsName(request.getOsName());
        order.setOsVersion(request.getOsVersion());
        order.setInitialPassword(request.getInitialPassword());
        order.setSshPort(request.getSshPort());
        order.setStatus(Order.OrderStatus.PAID);

        // 计算过期时间
        order.setExpiresAt(calculateExpirationDate(request.getBillingPeriod()));

        // 扣除余额
        wallet.setBalance(wallet.getBalance().subtract(amount));
        walletRepository.save(wallet);

        // 保存订单
        order = orderRepository.save(order);

        // 更新服务器状态为已分配
        if (assignedServer != null) {
            assignedServer.setStatus(Server.ServerStatus.ALLOCATED);
            serverRepository.save(assignedServer);
        }

        return convertToDTO(order);
    }

    @Override
    public OrderDTO getOrderById(Long id, User user) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("订单不存在"));

        if (!order.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("无权限访问此订单");
        }

        return convertToDTO(order);
    }

    @Override
    public List<OrderDTO> getUserOrders(User user) {
        List<Order> orders = orderRepository.findByUserOrderByCreatedAtDesc(user);
        return orders.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    @Override
    public OrderDTO getOrderByNumber(String orderNumber, User user) {
        Order order = orderRepository.findByOrderNumber(orderNumber)
                .orElseThrow(() -> new RuntimeException("订单不存在"));

        if (!order.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("无权限访问此订单");
        }

        return convertToDTO(order);
    }

    @Override
    @Transactional
    public OrderDTO updateOrderStatus(Long orderId, Order.OrderStatus status, User user) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("订单不存在"));

        if (!order.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("无权限访问此订单");
        }

        order.setStatus(status);
        order = orderRepository.save(order);

        return convertToDTO(order);
    }

    @Override
    @Transactional
    public OrderDTO cancelOrder(Long orderId, User user) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("订单不存在"));

        if (!order.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("无权限访问此订单");
        }

        if (order.getStatus() != Order.OrderStatus.PENDING) {
            throw new RuntimeException("只能取消待处理的订单");
        }

        order.setStatus(Order.OrderStatus.CANCELLED);
        order = orderRepository.save(order);

        return convertToDTO(order);
    }

    @Override
    @Transactional
    public OrderDTO processOrderPayment(Long orderId, User user) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("订单不存在"));

        if (!order.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("无权限访问此订单");
        }

        if (order.getStatus() != Order.OrderStatus.PENDING) {
            throw new RuntimeException("订单状态不正确");
        }

        // 检查用户余额
        Wallet wallet = walletRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("用户钱包不存在"));

        if (wallet.getBalance().compareTo(order.getAmount()) < 0) {
            throw new RuntimeException("余额不足");
        }

        // 扣除余额
        wallet.setBalance(wallet.getBalance().subtract(order.getAmount()));
        walletRepository.save(wallet);

        // 更新订单状态
        order.setStatus(Order.OrderStatus.PAID);
        order = orderRepository.save(order);

        return convertToDTO(order);
    }

    private BigDecimal calculateOrderAmount(PriceGroup priceGroup, String billingPeriod) {
        switch (billingPeriod) {
            case "hourly":
                return priceGroup.getHourlyPrice() != null ? priceGroup.getHourlyPrice() : BigDecimal.ZERO;
            case "daily":
                return priceGroup.getDailyPrice() != null ? priceGroup.getDailyPrice() : BigDecimal.ZERO;
            case "monthly":
                return priceGroup.getMonthlyPrice() != null ? priceGroup.getMonthlyPrice() : BigDecimal.ZERO;
            case "quarterly":
                return priceGroup.getQuarterlyPrice() != null ? priceGroup.getQuarterlyPrice() : BigDecimal.ZERO;
            case "semiAnnual":
                return priceGroup.getSemiAnnualPrice() != null ? priceGroup.getSemiAnnualPrice() : BigDecimal.ZERO;
            case "annual":
                return priceGroup.getAnnualPrice() != null ? priceGroup.getAnnualPrice() : BigDecimal.ZERO;
            default:
                throw new RuntimeException("不支持的计费周期: " + billingPeriod);
        }
    }

    private LocalDateTime calculateExpirationDate(String billingPeriod) {
        LocalDateTime now = LocalDateTime.now();
        switch (billingPeriod) {
            case "hourly":
                return now.plusHours(1);
            case "daily":
                return now.plusDays(1);
            case "monthly":
                return now.plusMonths(1);
            case "quarterly":
                return now.plusMonths(3);
            case "semiAnnual":
                return now.plusMonths(6);
            case "annual":
                return now.plusYears(1);
            default:
                return now.plusMonths(1);
        }
    }

    private Server assignServerFromGroup(ServerGroup serverGroup) {
        // 查找该分组下可用的服务器
        List<Server> availableServers = serverRepository.findByGroupAndStatus(serverGroup, Server.ServerStatus.AVAILABLE);
        
        if (availableServers.isEmpty()) {
            throw new RuntimeException("该分组下没有可用的服务器");
        }

        // 返回第一个可用的服务器
        return availableServers.getFirst();
    }

    private OrderDTO convertToDTO(Order order) {
        OrderDTO dto = new OrderDTO();
        dto.setId(order.getId());
        dto.setUserId(order.getUser().getId());
        dto.setUsername(order.getUser().getUsername());
        dto.setPriceGroupId(order.getPriceGroup().getId());
        dto.setPriceGroupName(order.getPriceGroup().getName());
        
        if (order.getServer() != null) {
            dto.setServerId(order.getServer().getId());
            dto.setServerName(order.getServer().getName());
        }
        
        dto.setOrderNumber(order.getOrderNumber());
        dto.setStatus(order.getStatus());
        dto.setAmount(order.getAmount());
        dto.setCurrency(order.getCurrency());
        dto.setBillingPeriod(order.getBillingPeriod());
        dto.setCpuCores(order.getCpuCores());
        dto.setMemoryGb(order.getMemoryGb());
        dto.setStorageGb(order.getStorageGb());
        dto.setBandwidthMbps(order.getBandwidthMbps());
        dto.setIpCount(order.getIpCount());
        dto.setOsName(order.getOsName());
        dto.setOsVersion(order.getOsVersion());
        dto.setInitialPassword(order.getInitialPassword());
        dto.setSshPort(order.getSshPort());
        dto.setCreatedAt(order.getCreatedAt());
        dto.setUpdatedAt(order.getUpdatedAt());
        dto.setExpiresAt(order.getExpiresAt());
        
        return dto;
    }
}
