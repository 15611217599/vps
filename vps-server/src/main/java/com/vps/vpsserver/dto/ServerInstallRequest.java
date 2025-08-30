package com.vps.vpsserver.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ServerInstallRequest {
    
    @NotNull(message = "服务器ID不能为空")
    private Long serverId;
    
    @NotBlank(message = "操作系统名称不能为空")
    private String osName;
    
    private String osVersion;
    private String rootPassword = "123@@@";
    private String sshKey;
    private Integer sshPort = 22;
    private Integer webPort = 80;
    private String customImageUrl;
    private String installType = "REINSTALL"; // REINSTALL, DD, ALPINE_LIVE
    private Boolean minimal = false;
    
    // 构建安装命令
    public String buildInstallCommand() {
        StringBuilder cmd = new StringBuilder();
        
        switch (installType.toUpperCase()) {
            case "REINSTALL":
                cmd.append("bash reinstall.sh ").append(shQuote(osName));
                if (osVersion != null && !osVersion.isEmpty()) {
                    cmd.append(" ").append(shQuote(osVersion));
                }
                if (minimal != null && minimal) {
                    cmd.append(" --minimal");
                }
                break;
                
            case "DD":
                cmd.append("bash reinstall.sh dd");
                if (customImageUrl != null && !customImageUrl.isEmpty()) {
                    cmd.append(" --img ").append(shQuote(customImageUrl));
                }
                break;
                
            case "ALPINE_LIVE":
                cmd.append("bash reinstall.sh alpine --hold=1");
                break;
        }
        
        // 添加通用参数
        if (rootPassword != null && !rootPassword.isEmpty()) {
            cmd.append(" --password ").append(shQuote(rootPassword));
        }
        
        if (sshKey != null && !sshKey.isEmpty()) {
            cmd.append(" --ssh-key ").append(shQuote(sshKey));
        }
        
        if (sshPort != null && !sshPort.equals(22)) {
            cmd.append(" --ssh-port ").append(sshPort);
        }
        
        if (webPort != null && !webPort.equals(80)) {
            cmd.append(" --web-port ").append(webPort);
        }
        
        return cmd.toString();
    }

    // 使用单引号安全包装shell参数，同时转义内部的单引号
    private String shQuote(String raw) {
        // 将 ' 替换为 '\'' 的标准POSIX写法
        String escaped = raw.replace("'", "'\"'\"'");
        return "'" + escaped + "'";
    }
}