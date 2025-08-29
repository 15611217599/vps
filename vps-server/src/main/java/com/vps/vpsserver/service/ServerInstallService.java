package com.vps.vpsserver.service;

import com.vps.vpsserver.dto.ServerInstallRequest;
import com.vps.vpsserver.entity.Server;
import com.vps.vpsserver.entity.Server.InstallStatus;
import com.vps.vpsserver.repository.ServerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Slf4j
public class ServerInstallService {
    
    private final ServerRepository serverRepository;
    private final SSHService sshService;
    
    /**
     * 开始安装系统
     */
    @Transactional
    public void startInstall(ServerInstallRequest request) {
        log.info("开始安装系统，服务器ID: {}", request.getServerId());
        
        Optional<Server> serverOpt = serverRepository.findById(request.getServerId());
        if (serverOpt.isEmpty()) {
            throw new RuntimeException("服务器不存在");
        }
        
        Server server = serverOpt.get();
        
        // 检查服务器状态
        if (server.getInstallStatus() == InstallStatus.INSTALLING) {
            throw new RuntimeException("服务器正在安装中，请勿重复操作");
        }
        
        // 更新服务器信息
        server.setOperatingSystem(request.getOsName() + 
            (request.getOsVersion() != null ? " " + request.getOsVersion() : ""));
        server.setPassword(request.getRootPassword());
        server.setPort(request.getSshPort());
        
        // 设置安装状态
        server.setInstallStatus(InstallStatus.INSTALLING);
        server.setInstallProgress(0);
        server.setInstallStep("准备开始安装...");
        server.setInstallLog("");
        server.setInstallError(null);
        server.setInstallStartedAt(LocalDateTime.now());
        
        serverRepository.save(server);
        
        // 异步执行安装
        CompletableFuture.runAsync(() -> executeInstall(server, request));
    }
    
    /**
     * 执行安装
     */
    private void executeInstall(Server server, ServerInstallRequest request) {
        try {
            String installCommand = request.buildInstallCommand();
            log.info("执行安装命令: {}", installCommand);
            
            // 更新进度：连接测试
            updateInstallProgress(server.getId(), 10, "测试SSH连接...");
            
            if (!sshService.testConnection(server.getIp(), server.getPort(), 
                    server.getUsername(), server.getPassword())) {
                throw new RuntimeException("无法连接到服务器");
            }
            
            // 更新进度：下载脚本
            updateInstallProgress(server.getId(), 20, "下载安装脚本...");
            
            String downloadCommand = "curl -fsSL https://raw.githubusercontent.com/bin456789/reinstall/main/reinstall.sh -o reinstall.sh && chmod +x reinstall.sh";
            
            SSHService.SSHResult downloadResult = sshService.executeCommand(
                server.getIp(), server.getPort(), server.getUsername(), server.getPassword(), downloadCommand
            ).get();
            
            if (!downloadResult.isSuccess()) {
                throw new RuntimeException("下载安装脚本失败: " + downloadResult.getError());
            }
            
            // 更新进度：开始安装
            updateInstallProgress(server.getId(), 30, "开始系统安装...");
            
            SSHService.SSHResult installResult = sshService.executeCommand(
                server.getIp(), server.getPort(), server.getUsername(), server.getPassword(), installCommand
            ).get();
            
            // 更新日志
            updateInstallLog(server.getId(), installResult.getOutput());
            
            if (installResult.isSuccess()) {
                // 安装成功
                updateInstallComplete(server.getId(), true, null);
            } else {
                // 安装失败
                updateInstallComplete(server.getId(), false, installResult.getError());
            }
            
        } catch (Exception e) {
            log.error("安装系统时发生异常: {}", e.getMessage(), e);
            updateInstallComplete(server.getId(), false, e.getMessage());
        }
    }
    
    /**
     * 更新安装进度
     */
    @Transactional
    public void updateInstallProgress(Long serverId, Integer progress, String step) {
        Optional<Server> serverOpt = serverRepository.findById(serverId);
        if (serverOpt.isPresent()) {
            Server server = serverOpt.get();
            server.setInstallProgress(progress);
            server.setInstallStep(step);
            serverRepository.save(server);
            
            log.info("服务器 {} 安装进度: {}% - {}", serverId, progress, step);
        }
    }
    
    /**
     * 更新安装日志
     */
    @Transactional
    public void updateInstallLog(Long serverId, String logContent) {
        Optional<Server> serverOpt = serverRepository.findById(serverId);
        if (serverOpt.isPresent()) {
            Server server = serverOpt.get();
            String existingLog = server.getInstallLog() != null ? server.getInstallLog() : "";
            server.setInstallLog(existingLog + "\n" + logContent);
            serverRepository.save(server);
        }
    }
    
    /**
     * 完成安装
     */
    @Transactional
    public void updateInstallComplete(Long serverId, boolean success, String errorMessage) {
        Optional<Server> serverOpt = serverRepository.findById(serverId);
        if (serverOpt.isPresent()) {
            Server server = serverOpt.get();
            
            if (success) {
                server.setInstallStatus(InstallStatus.COMPLETED);
                server.setInstallProgress(100);
                server.setInstallStep("安装完成");
                server.setInstallError(null);
            } else {
                server.setInstallStatus(InstallStatus.FAILED);
                server.setInstallStep("安装失败");
                server.setInstallError(errorMessage);
            }
            
            serverRepository.save(server);
            
            log.info("服务器 {} 安装完成，状态: {}", serverId, success ? "成功" : "失败");
        }
    }
    
    /**
     * 获取服务器安装状态
     */
    public Server getServerInstallStatus(Long serverId) {
        return serverRepository.findById(serverId)
            .orElseThrow(() -> new RuntimeException("服务器不存在"));
    }
}