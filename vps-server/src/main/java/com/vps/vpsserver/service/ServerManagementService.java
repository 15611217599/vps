package com.vps.vpsserver.service;

import com.vps.vpsserver.dto.*;
import com.vps.vpsserver.entity.Server;
import com.vps.vpsserver.repository.ServerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Slf4j
public class ServerManagementService {
    
    private final ServerRepository serverRepository;
    private final SSHService sshService;
    private final ServerInstallService serverInstallService;
    
    /**
     * 执行服务器操作
     */
    @Transactional
    public ApiResponse<String> executeServerAction(ServerActionRequest request) {
        try {
            Optional<Server> serverOpt = serverRepository.findById(request.getServerId());
            if (serverOpt.isEmpty()) {
                return ApiResponse.error("服务器不存在");
            }
            
            Server server = serverOpt.get();
            
            // 记录操作日志
            logServerAction(server, request.getAction(), "pending", request.getRemark());
            
            switch (request.getAction()) {
                case "start":
                    return startServer(server);
                case "restart":
                    return restartServer(server);
                case "stop":
                    return stopServer(server);
                case "resetPassword":
                    return resetServerPassword(server, request.getNewPassword());
                case "reinstall":
                    return reinstallServer(server, request);
                default:
                    return ApiResponse.error("不支持的操作类型");
            }
        } catch (Exception e) {
            log.error("执行服务器操作失败", e);
            return ApiResponse.error("操作执行失败: " + e.getMessage());
        }
    }
    
    /**
     * 启动服务器
     */
    private ApiResponse<String> startServer(Server server) {
        try {
            // 这里应该调用云服务商API启动服务器
            // 暂时模拟操作
            log.info("启动服务器: {}", server.getIp());
            
            // 异步执行启动操作
            CompletableFuture.runAsync(() -> {
                try {
                    // 模拟启动时间
                    Thread.sleep(5000);
                    
                    // 更新服务器状态
                    server.setStatus(Server.ServerStatus.ONLINE);
                    serverRepository.save(server);
                    
                    // SSH服务重启 - 需要服务器连接信息
                    // 这里应该从server对象获取连接信息并调用SSH服务
                    log.info("服务器启动完成，服务器ID: {}", server.getId());
                    
                    logServerAction(server, "start", "success", "服务器启动成功");
                } catch (Exception e) {
                    logServerAction(server, "start", "failed", "启动失败: " + e.getMessage());
                }
            });
            
            return ApiResponse.success("启动命令已发送，正在启动中...");
        } catch (Exception e) {
            logServerAction(server, "start", "failed", "启动失败: " + e.getMessage());
            return ApiResponse.error("启动服务器失败: " + e.getMessage());
        }
    }
    
    /**
     * 重启服务器
     */
    private ApiResponse<String> restartServer(Server server) {
        try {
            log.info("重启服务器: {}", server.getIp());
            
            // 使用SSH执行重启命令
            CompletableFuture.runAsync(() -> {
                try {
                    String rebootCmd = "nohup sh -c '(sleep 2; /sbin/reboot -f || reboot || shutdown -r now)' >/dev/null 2>&1 &";
                    sshService.executeCommand(
                        server.getIp(), server.getPort(), server.getUsername(), server.getPassword(), rebootCmd
                    ).get();
                    
                    // 重启命令可能会因为连接断开而返回错误，这是正常的
                    logServerAction(server, "restart", "success", "重启命令已执行");
                } catch (Exception e) {
                    log.error("重启服务器失败", e);
                    logServerAction(server, "restart", "failed", "重启失败: " + e.getMessage());
                }
            });
            
            return ApiResponse.success("重启命令已发送，服务器正在重启中...");
        } catch (Exception e) {
            logServerAction(server, "restart", "failed", "重启失败: " + e.getMessage());
            return ApiResponse.error("重启服务器失败: " + e.getMessage());
        }
    }
    
    /**
     * 关机服务器
     */
    private ApiResponse<String> stopServer(Server server) {
        try {
            log.info("关机服务器: {}", server.getIp());
            
            CompletableFuture.runAsync(() -> {
                try {
                    String shutdownCmd = "nohup sh -c '(sleep 2; /sbin/shutdown -h now || halt || poweroff)' >/dev/null 2>&1 &";
                    sshService.executeCommand(
                        server.getIp(), server.getPort(), server.getUsername(), server.getPassword(), shutdownCmd
                    ).get();
                    
                    // 更新服务器状态
                    server.setStatus(Server.ServerStatus.OFFLINE);
                    serverRepository.save(server);
                    
                    logServerAction(server, "stop", "success", "服务器已关机");
                } catch (Exception e) {
                    log.error("关机服务器失败", e);
                    logServerAction(server, "stop", "failed", "关机失败: " + e.getMessage());
                }
            });
            
            return ApiResponse.success("关机命令已发送，服务器正在关机中...");
        } catch (Exception e) {
            logServerAction(server, "stop", "failed", "关机失败: " + e.getMessage());
            return ApiResponse.error("关机服务器失败: " + e.getMessage());
        }
    }
    
    /**
     * 重置服务器密码
     */
    private ApiResponse<String> resetServerPassword(Server server, String newPassword) {
        try {
            log.info("重置服务器密码: {}", server.getIp());
            
            CompletableFuture.runAsync(() -> {
                try {
                    String changePasswordCmd = String.format("echo 'root:%s' | chpasswd", newPassword);
                    SSHService.SSHResult result = sshService.executeCommand(
                        server.getIp(), server.getPort(), server.getUsername(), server.getPassword(), changePasswordCmd
                    ).get();
                    
                    if (result.isSuccess()) {
                        // 更新服务器密码
                        server.setPassword(newPassword);
                        serverRepository.save(server);
                        
                        logServerAction(server, "resetPassword", "success", "密码重置成功");
                    } else {
                        logServerAction(server, "resetPassword", "failed", "密码重置失败: " + result.getError());
                    }
                } catch (Exception e) {
                    log.error("重置密码失败", e);
                    logServerAction(server, "resetPassword", "failed", "密码重置失败: " + e.getMessage());
                }
            });
            
            return ApiResponse.success("密码重置命令已发送，正在处理中...");
        } catch (Exception e) {
            logServerAction(server, "resetPassword", "failed", "密码重置失败: " + e.getMessage());
            return ApiResponse.error("重置密码失败: " + e.getMessage());
        }
    }
    
    /**
     * 重装服务器系统
     */
    private ApiResponse<String> reinstallServer(Server server, ServerActionRequest request) {
        try {
            log.info("重装服务器系统: {}, OS: {}", server.getIp(), request.getOsName());
            
            // 构建重装请求
            ServerInstallRequest installRequest = new ServerInstallRequest();
            installRequest.setServerId(server.getId());
            installRequest.setOsName(request.getOsName());
            installRequest.setOsVersion(request.getOsVersion());
            installRequest.setRootPassword(request.getRootPassword());
            
            // 调用安装服务
            serverInstallService.startInstall(installRequest);
            
            logServerAction(server, "reinstall", "running", "系统重装已开始");
            return ApiResponse.success("系统重装已开始，请耐心等待...");
        } catch (Exception e) {
            logServerAction(server, "reinstall", "failed", "系统重装失败: " + e.getMessage());
            return ApiResponse.error("系统重装失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取服务器监控信息
     */
    public ApiResponse<ServerMonitoringDTO> getServerMonitoring(Long serverId) {
        try {
            Optional<Server> serverOpt = serverRepository.findById(serverId);
            if (serverOpt.isEmpty()) {
                return ApiResponse.error("服务器不存在");
            }
            
            Server server = serverOpt.get();
            ServerMonitoringDTO monitoring = new ServerMonitoringDTO();
            monitoring.setServerId(serverId);
            monitoring.setServerIp(server.getIp());
            monitoring.setStatus(server.getStatus().name().toLowerCase());
            monitoring.setPublicIp(server.getIp());
            monitoring.setSshPort(server.getPort());
            monitoring.setLastCheckTime(LocalDateTime.now());
            
            // 同步获取实时监控数据，直接返回给前端
            try {
                // 测试SSH连接
                boolean sshConnectable = sshService.testConnection(
                    server.getIp(), server.getPort(), server.getUsername(), server.getPassword()
                );
                monitoring.setSshConnectable(sshConnectable);
                
                if (sshConnectable) {
                    // 使用与定时任务相同的监控命令
                    String monitoringCmd = buildMetricsCommand();
                    
                    SSHService.SSHResult result = sshService.executeCommand(
                        server.getIp(), server.getPort(), server.getUsername(), server.getPassword(), monitoringCmd
                    ).get();
                    
                    if (result.isSuccess()) {
                        String output = result.getOutput().trim();
                        log.debug("Raw monitoring output: {}", output);
                        
                        // 解析管道分隔的输出格式: cpu|mem|disk|rx|tx
                        parseMetricsOutput(monitoring, output);
                        
                        // 获取运行时间和进程数
                        getAdditionalMetrics(server, monitoring);
                        
                        monitoring.setStatus("online");
                    } else {
                        monitoring.setStatus("offline");
                    }
                } else {
                    monitoring.setStatus("offline");
                }
                
                // 确保所有值都有默认值
                if (monitoring.getCpuUsage() == null) monitoring.setCpuUsage(0.0);
                if (monitoring.getMemoryUsage() == null) monitoring.setMemoryUsage(0.0);
                if (monitoring.getDiskUsage() == null) monitoring.setDiskUsage(0.0);
                if (monitoring.getUptime() == null) monitoring.setUptime("未知");
                if (monitoring.getProcessCount() == null) monitoring.setProcessCount(0);
                
                monitoring.setLastUpdateTime(LocalDateTime.now());
            } catch (Exception e) {
                log.error("获取服务器监控信息失败", e);
                // 即使监控数据获取失败，也返回基础信息
                monitoring.setStatus("offline");
                monitoring.setSshConnectable(false);
                monitoring.setCpuUsage(0.0);
                monitoring.setMemoryUsage(0.0);
                monitoring.setDiskUsage(0.0);
                monitoring.setUptime("未知");
                monitoring.setProcessCount(0);
            }
        
            return ApiResponse.success(monitoring);
        } catch (Exception e) {
            log.error("获取服务器监控信息失败", e);
            return ApiResponse.error("获取监控信息失败: " + e.getMessage());
        }
    }

    
    /**
     * 生成跨发行版的指标采集脚本（与定时任务保持一致）
     */
    private String buildMetricsCommand() {
        String script =
            "set -e; " +
            // CPU first sample
            "t1=$(awk '/^cpu /{print $2+$3+$4+$5+$6+$7+$8}' /proc/stat); " +
            "i1=$(awk '/^cpu /{print $5}' /proc/stat); " +
            // NET first sample
            "rx1=$(awk -F'[: ]+' '/:/{if($1!=\"lo\"){rx+=$3}} END{print rx+0}' /proc/net/dev); " +
            "tx1=$(awk -F'[: ]+' '/:/{if($1!=\"lo\"){tx+=$11}} END{print tx+0}' /proc/net/dev); " +
            // sleep 1 second
            "sleep 1; " +
            // CPU second sample
            "t2=$(awk '/^cpu /{print $2+$3+$4+$5+$6+$7+$8}' /proc/stat); " +
            "i2=$(awk '/^cpu /{print $5}' /proc/stat); " +
            // NET second sample
            "rx2=$(awk -F'[: ]+' '/:/{if($1!=\"lo\"){rx+=$3}} END{print rx+0}' /proc/net/dev); " +
            "tx2=$(awk -F'[: ]+' '/:/{if($1!=\"lo\"){tx+=$11}} END{print tx+0}' /proc/net/dev); " +
            // CPU usage calc
            "dt=$((t2 - t1)); di=$((i2 - i1)); cpu=0; if [ $dt -gt 0 ]; then cpu=$(awk -v dt=$dt -v di=$di 'BEGIN{print (dt-di)/dt*100}'); fi; " +
            // MEM usage
            "mem=$(free -m 2>/dev/null | awk '/^Mem:/{if($2>0) print $3/$2*100; else print 0}'); if [ -z \"$mem\" ]; then mem=$(awk '/MemTotal/{mt=$2} /MemAvailable/{ma=$2} END{if(mt>0) print (mt-ma)/mt*100; else print 0}' /proc/meminfo); fi; " +
            // DISK usage for /
            "disk=$(df -P / 2>/dev/null | awk 'NR==2{gsub(\"%\",\"\",$5); print $5+0}'); if [ -z \"$disk\" ]; then disk=0; fi; " +
            // NET rates kbps
            "rxk=$(awk -v r1=$rx1 -v r2=$rx2 'BEGIN{print (r2-r1)/1024*8}'); txk=$(awk -v t1=$tx1 -v t2=$tx2 'BEGIN{print (t2-t1)/1024*8}'); " +
            // print values with 2 decimals and '|' delimiter
            "printf '%.2f|%.2f|%.2f|%.2f|%.2f\\n' \"$cpu\" \"$mem\" \"$disk\" \"$rxk\" \"$txk\"";
        return script;
    }

    /**
     * 解析管道分隔的监控数据输出
     * 格式: cpu|mem|disk|rx|tx
     */
    private void parseMetricsOutput(ServerMonitoringDTO monitoring, String output) {
        try {
            String[] parts = output.split("\\|");
            if (parts.length >= 5) {
                monitoring.setCpuUsage(parseDouble(parts[0]));
                monitoring.setMemoryUsage(parseDouble(parts[1]));
                monitoring.setDiskUsage(parseDouble(parts[2]));
                // 网络速率可以添加到DTO中，暂时记录到日志
                double rxKbps = parseDouble(parts[3]);
                double txKbps = parseDouble(parts[4]);
                log.debug("网络速率 - RX: {}kbps, TX: {}kbps", rxKbps, txKbps);
            } else {
                log.warn("监控数据格式不正确: {}", output);
            }
        } catch (Exception e) {
            log.error("解析监控数据失败", e);
        }
    }

    /**
     * 获取额外的监控指标（运行时间和进程数）
     */
    private void getAdditionalMetrics(Server server, ServerMonitoringDTO monitoring) {
        try {
            String additionalCmd = "uptime | awk -F'up ' '{print $2}' | awk -F',' '{print $1}' | sed 's/^ *//'; ps aux | wc -l";
            SSHService.SSHResult result = sshService.executeCommand(
                server.getIp(), server.getPort(), server.getUsername(), server.getPassword(), additionalCmd
            ).get();
            
            if (result.isSuccess()) {
                String[] lines = result.getOutput().trim().split("\n");
                if (lines.length >= 2) {
                    monitoring.setUptime(lines[0].trim());
                    try {
                        monitoring.setProcessCount(Integer.parseInt(lines[1].trim()));
                    } catch (NumberFormatException e) {
                        log.warn("解析进程数失败: {}", lines[1]);
                    }
                }
            }
        } catch (Exception e) {
            log.debug("获取额外监控指标失败", e);
        }
    }

    /**
     * 安全解析双精度浮点数
     */
    private double parseDouble(String s) {
        try { 
            return Double.parseDouble(s.trim()); 
        } catch (Exception e) { 
            return 0.0; 
        }
    }
    
    /**
     * 记录服务器操作日志
     */
    private void logServerAction(Server server, String action, String status, String remark) {
        try {
            // 这里应该保存到操作日志表
            log.info("服务器操作日志 - 服务器: {}, 操作: {}, 状态: {}, 备注: {}", 
                    server.getIp(), action, status, remark);
        } catch (Exception e) {
            log.error("记录操作日志失败", e);
        }
    }
    
    /**
     * 获取服务器操作日志
     */
    public ApiResponse<List<ServerActionLogDTO>> getServerActionLogs(Long serverId, int page, int size) {
        try {
            // 这里应该从操作日志表查询
            // 暂时返回空列表
            return ApiResponse.success(List.of());
        } catch (Exception e) {
            log.error("获取服务器操作日志失败", e);
            return ApiResponse.error("获取操作日志失败: " + e.getMessage());
        }
    }
}
