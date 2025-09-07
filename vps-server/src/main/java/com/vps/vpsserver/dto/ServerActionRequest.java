package com.vps.vpsserver.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Data
public class ServerActionRequest {
    
    @NotNull(message = "服务器ID不能为空")
    private Long serverId;
    
    @NotBlank(message = "操作类型不能为空")
    private String action; // start, restart, stop, resetPassword, reinstall
    
    // 重置密码时的新密码
    private String newPassword;
    
    // 重装系统时的参数
    private String osName;
    private String osVersion;
    private String rootPassword;
    
    // 操作备注
    private String remark;
}
