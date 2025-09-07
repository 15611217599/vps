package com.vps.vpsserver.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ServerMonitoringDTO {
    
    private Long serverId;
    private String serverIp;
    private String status; // online, offline, maintenance
    
    // 系统资源使用情况
    private Double cpuUsage;
    private Double memoryUsage;
    private Double diskUsage;
    private Double networkInbound;
    private Double networkOutbound;
    
    // 系统信息
    private String uptime;
    private Integer processCount;
    private Double loadAverage;
    
    // 网络信息
    private String publicIp;
    private String privateIp;
    private Integer sshPort;
    private Boolean sshConnectable;
    
    // 时间戳
    private LocalDateTime lastUpdateTime;
    private LocalDateTime lastCheckTime;
}
