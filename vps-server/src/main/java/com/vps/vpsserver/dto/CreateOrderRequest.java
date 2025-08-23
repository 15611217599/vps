package com.vps.vpsserver.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateOrderRequest {
    private Long priceGroupId;
    private String billingPeriod; // hourly, daily, monthly, quarterly, semiAnnual, annual
    
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
}
