package com.vps.vpsserver.service.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.vps.vpsserver.dto.CreateOrderRequest;
import com.vps.vpsserver.dto.OrderDTO;
import com.vps.vpsserver.dto.ServerInstallRequest;
import com.vps.vpsserver.entity.Order;
import com.vps.vpsserver.entity.PriceGroup;
import com.vps.vpsserver.entity.Server;
import com.vps.vpsserver.entity.ServerGroup;
import com.vps.vpsserver.entity.User;
import com.vps.vpsserver.entity.Wallet;
import com.vps.vpsserver.repository.OrderRepository;
import com.vps.vpsserver.repository.PriceGroupRepository;
import com.vps.vpsserver.repository.ServerRepository;
import com.vps.vpsserver.repository.WalletRepository;
import com.vps.vpsserver.service.OrderService;
import com.vps.vpsserver.service.ServerInstallService;
import com.vps.vpsserver.service.TransactionService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final PriceGroupRepository priceGroupRepository;
    private final ServerRepository serverRepository;
    private final WalletRepository walletRepository;
    private final TransactionService transactionService;
    private final ServerInstallService serverInstallService;
    

    @Override
    @Transactional
    public OrderDTO createOrder(CreateOrderRequest request, User user) {
        // 获取价格组
        PriceGroup priceGroup = priceGroupRepository.findById(request.getPriceGroupId())
                .orElseThrow(() -> new RuntimeException("未找到价格组"));

        // 计算订单金额
        BigDecimal amount = calculateOrderAmount(priceGroup, request.getBillingPeriod());

        // 检查用户余额
        Wallet wallet = walletRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("未找到钱包账户"));

        if (wallet.getBalance().compareTo(amount) < 0) {
            throw new RuntimeException("钱包余额不足，当前余额: " + wallet.getBalance() + ", 需要: " + amount);
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
        order.setAutoRenewal(request.getAutoRenewal() != null ? request.getAutoRenewal() : false);
        order.setStatus(Order.OrderStatus.PAID);

        // 计算过期时间
        order.setExpiresAt(calculateExpirationDate(request.getBillingPeriod()));

        // 记录余额变化前的金额
        BigDecimal balanceBefore = wallet.getBalance();
        
        // 扣除余额
        wallet.setBalance(wallet.getBalance().subtract(amount));
        walletRepository.save(wallet);
        
        BigDecimal balanceAfter = wallet.getBalance();

        // 保存订单
        order = orderRepository.save(order);
        
        // 创建交易流水记录
        transactionService.createOrderPaymentTransaction(
            user, 
            order.getId(), 
            order.getOrderNumber(), 
            amount, 
            balanceBefore, 
            balanceAfter
        );

        // 标记服务器为已售（保持在线状态）
        if (assignedServer != null) {
            assignedServer.setIsSold(true);
            serverRepository.save(assignedServer);
            
            // 自动触发操作系统安装
            try {
                log.info("订单创建成功，开始自动安装操作系统。订单号: {}, 服务器ID: {}", order.getOrderNumber(), assignedServer.getId());
                
                ServerInstallRequest installRequest = new ServerInstallRequest();
                installRequest.setServerId(assignedServer.getId());
                installRequest.setOsName(request.getOsName() != null ? request.getOsName() : "ubuntu");
                installRequest.setOsVersion(request.getOsVersion() != null ? request.getOsVersion() : "22.04");
                installRequest.setRootPassword(request.getInitialPassword() != null ? request.getInitialPassword() : "123@@@");
                installRequest.setSshPort(request.getSshPort() != null ? request.getSshPort() : 22);
                installRequest.setInstallType("REINSTALL");
                installRequest.setMinimal(false);
                
                // 异步启动安装过程
                serverInstallService.startInstall(installRequest);
                
                log.info("操作系统安装任务已启动。订单号: {}, 服务器ID: {}", order.getOrderNumber(), assignedServer.getId());
                
            } catch (Exception e) {
                log.error("自动安装操作系统失败。订单号: {}, 服务器ID: {}, 错误: {}", 
                    order.getOrderNumber(), assignedServer.getId(), e.getMessage(), e);
                // 不抛出异常，避免影响订单创建流程
            }
        }

        return convertToDTO(order);
    }

    @Override
    public OrderDTO getOrderById(Long id, User user) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("未找到订单"));

        if (!order.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("无权访问该订单");
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
                .orElseThrow(() -> new RuntimeException("未找到订单"));

        if (!order.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("无权访问该订单");
        }

        return convertToDTO(order);
    }

    @Override
    @Transactional
    public OrderDTO updateOrderStatus(Long orderId, Order.OrderStatus status, User user) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("未找到订单"));

        if (!order.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("无权访问该订单");
        }

        order.setStatus(status);
        order = orderRepository.save(order);

        return convertToDTO(order);
    }

    @Override
    @Transactional
    public OrderDTO cancelOrder(Long orderId, User user) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("未找到订单"));

        if (!order.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("无权访问该订单");
        }

        if (order.getStatus() != Order.OrderStatus.PENDING) {
            throw new RuntimeException("该订单当前状态不可取消");
        }

        order.setStatus(Order.OrderStatus.CANCELLED);
        order = orderRepository.save(order);

        return convertToDTO(order);
    }

    @Override
    @Transactional
    public OrderDTO processOrderPayment(Long orderId, User user) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("未找到订单"));

        if (!order.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("无权访问该订单");
        }

        if (order.getStatus() != Order.OrderStatus.PENDING) {
            throw new RuntimeException("订单状态无效，无法支付");
        }

        // 检查用户余额
        Wallet wallet = walletRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("未找到钱包账户"));

        if (wallet.getBalance().compareTo(order.getAmount()) < 0) {
            throw new RuntimeException("钱包余额不足，当前余额: " + wallet.getBalance() + ", 需要: " + order.getAmount());
        }

        // 记录余额变化前的金额
        BigDecimal balanceBefore = wallet.getBalance();
        
        // 扣除余额
        wallet.setBalance(wallet.getBalance().subtract(order.getAmount()));
        walletRepository.save(wallet);
        
        BigDecimal balanceAfter = wallet.getBalance();

        // 更新订单状态
        order.setStatus(Order.OrderStatus.PAID);
        order = orderRepository.save(order);
        
        // 创建交易流水记录
        transactionService.createOrderPaymentTransaction(
            user, 
            order.getId(), 
            order.getOrderNumber(), 
            order.getAmount(), 
            balanceBefore, 
            balanceAfter
        );

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
        // 查找该分组下在线且未售出的服务器
        List<Server> availableServers = serverRepository.findByGroupAndStatusAndIsSold(
            serverGroup, Server.ServerStatus.ONLINE, false);
        
        if (availableServers.isEmpty()) {
            throw new RuntimeException("当前分组没有可用服务器");
        }

        // 返回第一个可用的服务器
        return availableServers.getFirst();
    }

    @Override
    @Transactional
    public OrderDTO updateAutoRenewal(Long orderId, Boolean autoRenewal, User user) {
        // 查找订单
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("订单不存在"));
        
        // 验证订单所有者
        if (!order.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("无权限操作此订单");
        }
        
        // 只有活跃状态的订单才能修改自动续费设置
        if (order.getStatus() != Order.OrderStatus.ACTIVE && order.getStatus() != Order.OrderStatus.PAID) {
            throw new RuntimeException("只有活跃状态的订单才能修改自动续费设置");
        }

        // 更新自动续费设置
        order.setAutoRenewal(autoRenewal);
        order.setUpdatedAt(LocalDateTime.now());

        Order savedOrder = orderRepository.save(order);

        log.info("订单 {} 的自动续费设置已更新为: {}", orderId, autoRenewal);

        return convertToDTO(savedOrder);
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
        dto.setAutoRenewal(order.getAutoRenewal());
        
        return dto;
    }
}
