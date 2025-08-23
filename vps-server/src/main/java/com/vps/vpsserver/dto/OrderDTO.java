package com.vps.vpsserver.dto;

import com.vps.vpsserver.entity.Order;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderDTO {
    private Long id;
    private Long userId;
    private String username;
    private Long priceGroupId;
    private String priceGroupName;
    private Long serverId;
    private String serverName;
    private String orderNumber;
    private Order.OrderStatus status;
    private BigDecimal amount;
    private String currency;
    private String billingPeriod;
    
    // 服务器配置信息
    private Integer cpuCores;
    private Integer memoryGb;
    private Integer storageGb;
    private Integer bandwidthMbps;
    private Integer ipCount;
    private String osName;
    private String osVersion;
    private String initialPassword;
    private Integer sshPort;
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime expiresAt;
}
