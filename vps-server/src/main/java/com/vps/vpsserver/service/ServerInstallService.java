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
        // 注意：此处不更新操作系统/端口/密码等最终字段，待安装成功后再更新
        
        // 设置安装状态
        server.setInstallStatus(InstallStatus.INSTALLING);
        server.setInstallProgress(0);
        server.setInstallStep("准备开始安装...");
        server.setInstallLog("");
        server.setInstallError(null);
        server.setInstallStartedAt(LocalDateTime.now());
        
        serverRepository.save(server);
        
        // 异步执行安装
        CompletableFuture.runAsync(() -> executeInstall(server.getId(), request));
    }
    
    /**
     * 执行安装
     */
    private void executeInstall(Long serverId, ServerInstallRequest request) {
        try {
            // 获取最新的服务器信息，避免跨线程持久化实体问题
            Server server = serverRepository.findById(serverId)
                .orElseThrow(() -> new RuntimeException("服务器不存在"));
            String installCommand = request.buildInstallCommand();
            log.info("执行安装命令: {}", installCommand);
            
            // 更新进度：连接测试
            updateInstallProgress(server.getId(), 10, "测试SSH连接...");
            
            if (!sshService.testConnection(server.getIp(), server.getPort(), 
                    server.getUsername(), server.getPassword())) {
                throw new RuntimeException("无法连接到服务器");
            }
            
            // 更新进度：安装依赖
            updateInstallProgress(server.getId(), 15, "安装必要依赖...");
            String prereqCmd = buildPrerequisitesCommand();
            SSHService.SSHResult prereqResult = sshService.executeCommand(
                server.getIp(), server.getPort(), server.getUsername(), server.getPassword(), prereqCmd
            ).get();
            if (!prereqResult.isSuccess()) {
                throw new RuntimeException("安装依赖失败: " + prereqResult.getError());
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
                // 安装成功：尝试触发重启
                updateInstallProgress(server.getId(), 80, "安装完成，准备重启...");
                String rebootCmd = "nohup sh -c '(sleep 2; /sbin/reboot -f || reboot || shutdown -r now)' >/dev/null 2>&1 & echo REBOOT_SCHEDULED";
                try {
                    SSHService.SSHResult rebootResult = sshService.executeCommand(
                        server.getIp(), server.getPort(), server.getUsername(), server.getPassword(), rebootCmd
                    ).get();
                    // 即使返回非0也可能因连接关闭导致，尽量以输出标识判断
                    if (!rebootResult.isSuccess()) {
                        log.warn("触发重启命令返回非成功状态，可能是SSH断开所致: {}", rebootResult.getError());
                    }
                } catch (Exception re) {
                    log.warn("触发重启时发生异常（可能是预期的连接中断）: {}", re.getMessage());
                }
                updateInstallProgress(server.getId(), 90, "已触发重启，等待服务器重启...");
                // 安装成功：最终更新服务器字段（操作系统/密码/端口等）
                updateInstallComplete(server.getId(), true, null, request);
            } else {
                // 安装失败：仅更新安装状态，不覆盖最终字段
                updateInstallComplete(server.getId(), false, installResult.getError(), request);
            }
            
        } catch (Exception e) {
            log.error("安装系统时发生异常: {}", e.getMessage(), e);
            updateInstallComplete(serverId, false, e.getMessage(), request);
        }
    }

    /**
     * 构建安装必要依赖的通用命令，覆盖常见发行版包管理器
     */
    private String buildPrerequisitesCommand() {
        // 尽量做到幂等：已经存在则跳过安装
        StringBuilder sb = new StringBuilder();
        sb.append("set -e; ");
        sb.append("missing=''; ");
        sb.append("for c in curl wget bash; do command -v $c >/dev/null 2>&1 || missing=\"$missing $c\"; done; ");
        // ca-certificates在部分系统里不是命令，因此仅尝试安装
        sb.append("if [ -z \"$missing\" ]; then echo 'dependencies already satisfied'; exit 0; fi; ");
        sb.append("if command -v apt-get >/dev/null 2>&1; then ");
        sb.append("export DEBIAN_FRONTEND=noninteractive; apt-get update -y && apt-get install -y curl wget bash ca-certificates && update-ca-certificates || true; ");
        sb.append("elif command -v dnf >/dev/null 2>&1; then ");
        sb.append("dnf install -y curl wget bash ca-certificates; ");
        sb.append("elif command -v yum >/dev/null 2>&1; then ");
        sb.append("yum install -y curl wget bash ca-certificates; ");
        sb.append("elif command -v zypper >/dev/null 2>&1; then ");
        sb.append("zypper --non-interactive install -y curl wget bash ca-certificates; ");
        sb.append("elif command -v pacman >/dev/null 2>&1; then ");
        sb.append("pacman -Sy --noconfirm curl wget bash ca-certificates; ");
        sb.append("elif command -v apk >/dev/null 2>&1; then ");
        sb.append("apk add --no-cache curl wget bash ca-certificates; ");
        sb.append("else echo 'unsupported package manager'; exit 1; fi");
        return sb.toString();
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
    public void updateInstallComplete(Long serverId, boolean success, String errorMessage, ServerInstallRequest request) {
        Optional<Server> serverOpt = serverRepository.findById(serverId);
        if (serverOpt.isPresent()) {
            Server server = serverOpt.get();
            
            if (success) {
                // 安装成功后，才更新这些最终字段
                server.setOperatingSystem(request.getOsName() +
                    (request.getOsVersion() != null ? " " + request.getOsVersion() : ""));
                if (request.getRootPassword() != null) {
                    server.setPassword(request.getRootPassword());
                }
                if (request.getSshPort() != null) {
                    server.setPort(request.getSshPort());
                }
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