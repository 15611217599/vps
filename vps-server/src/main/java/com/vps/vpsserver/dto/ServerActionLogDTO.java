package com.vps.vpsserver.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ServerActionLogDTO {
    
    private Long id;
    private Long serverId;
    private String serverIp;
    private String action;
    private String status; // pending, running, success, failed
    private String remark;
    private String errorMessage;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String operatorName;
    
    // 进度信息（用于长时间运行的操作如系统重装）
    private Integer progress; // 0-100
    private String currentStep;
}
