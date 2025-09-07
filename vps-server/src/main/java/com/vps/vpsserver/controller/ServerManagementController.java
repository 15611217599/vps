package com.vps.vpsserver.controller;

import com.vps.vpsserver.dto.*;
import com.vps.vpsserver.entity.Order;
import com.vps.vpsserver.entity.Server;
import com.vps.vpsserver.entity.Server.ServerStatus;
import com.vps.vpsserver.entity.User;
import com.vps.vpsserver.repository.OrderRepository;
import com.vps.vpsserver.repository.ServerRepository;
import com.vps.vpsserver.service.ServerManagementService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/server-management")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Slf4j
public class ServerManagementController {
    
    private final ServerManagementService serverManagementService;
    private final ServerRepository serverRepository;
    private final OrderRepository orderRepository;
    
    /**
     * 执行服务器操作 - 仅限管理员
     */
    @PostMapping("/action")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<String>> executeAction(@Valid @RequestBody ServerActionRequest request, Authentication authentication) {
        User currentUser = (User) authentication.getPrincipal();
        log.info("管理员 {} 执行服务器操作: {}", currentUser.getUsername(), request.getAction());
        // 检查服务器状态，只有在ONLINE状态下才执行操作
        Optional<Server> serverOpt = serverRepository.findById(request.getServerId());
        if (serverOpt.isEmpty()) {
            return ResponseEntity.ok(ApiResponse.error("服务器不存在"));
        }
        
        Server server = serverOpt.get();
        // 特殊情况：start操作允许在OFFLINE状态下执行
        if (!"start".equals(request.getAction()) && server.getStatus() != ServerStatus.ONLINE) {
            return ResponseEntity.ok(ApiResponse.error("服务器当前不可用，无法执行操作"));
        }
        
        // 检查服务器是否即将到期（前一小时内）
        Optional<Order> activeOrderOpt = orderRepository.findActiveOrderByServerId(request.getServerId());
        if (activeOrderOpt.isPresent()) {
            Order activeOrder = activeOrderOpt.get();
            LocalDateTime expiresAt = activeOrder.getExpiresAt();
            LocalDateTime oneHourBeforeExpiry = expiresAt.minus(1, ChronoUnit.HOURS);
            
            if (LocalDateTime.now().isAfter(oneHourBeforeExpiry)) {
                log.warn("服务器 {} 即将到期，拒绝执行操作 {}", request.getServerId(), request.getAction());
                return ResponseEntity.ok(ApiResponse.error("服务器即将到期（剩余时间不足1小时），为确保数据安全，暂时无法执行操作"));
            }
        }

        server.setStatus(ServerStatus.OFFLINE);
        serverRepository.save(server);
        ApiResponse<String> response = serverManagementService.executeServerAction(request);
        return ResponseEntity.ok(response);
    }
    
    /**
     * 获取服务器监控信息 - 仅限管理员
     */
    @GetMapping("/{serverId}/monitoring")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<ServerMonitoringDTO>> getMonitoring(@PathVariable Long serverId, Authentication authentication) {
        User currentUser = (User) authentication.getPrincipal();
        log.debug("管理员 {} 获取服务器监控信息: {}", currentUser.getUsername(), serverId);
        ApiResponse<ServerMonitoringDTO> response = serverManagementService.getServerMonitoring(serverId);
        return ResponseEntity.ok(response);
    }
    
    /**
     * 获取服务器操作日志 - 仅限管理员
     */
    @GetMapping("/{serverId}/logs")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<ServerActionLogDTO>>> getActionLogs(
            @PathVariable Long serverId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            Authentication authentication) {
        User currentUser = (User) authentication.getPrincipal();
        log.debug("管理员 {} 获取服务器操作日志: {}", currentUser.getUsername(), serverId);
        ApiResponse<List<ServerActionLogDTO>> response = serverManagementService.getServerActionLogs(serverId, page, size);
        return ResponseEntity.ok(response);
    }
    
    /**
     * 测试服务器连接 - 仅限管理员
     */
    @PostMapping("/{serverId}/test-connection")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Boolean>> testConnection(@PathVariable Long serverId, Authentication authentication) {
        User currentUser = (User) authentication.getPrincipal();
        log.debug("管理员 {} 测试服务器连接: {}", currentUser.getUsername(), serverId);
        // 检查服务器状态，只有在ONLINE状态下才测试连接
        Optional<Server> serverOpt = serverRepository.findById(serverId);
        if (serverOpt.isEmpty()) {
            return ResponseEntity.ok(ApiResponse.error("服务器不存在"));
        }
        
        Server server = serverOpt.get();
        if (server.getStatus() != ServerStatus.ONLINE) {
            return ResponseEntity.ok(ApiResponse.error("服务器当前不可用，无法测试连接"));
        }
        
        // 检查服务器是否即将到期（前一小时内）
        Optional<Order> activeOrderOpt = orderRepository.findActiveOrderByServerId(serverId);
        if (activeOrderOpt.isPresent()) {
            Order activeOrder = activeOrderOpt.get();
            LocalDateTime expiresAt = activeOrder.getExpiresAt();
            LocalDateTime oneHourBeforeExpiry = expiresAt.minus(1, ChronoUnit.HOURS);
            
            if (LocalDateTime.now().isAfter(oneHourBeforeExpiry)) {
                log.warn("服务器 {} 即将到期，拒绝测试连接", serverId);
                return ResponseEntity.ok(ApiResponse.error("服务器即将到期（剩余时间不足1小时），为确保数据安全，暂时无法执行操作"));
            }
        }
        
        // 这里可以调用SSH服务测试连接
        return ResponseEntity.ok(ApiResponse.success(true));
    }
    
    /**
     * 获取可用的操作系统列表
     */
    @GetMapping("/available-os")
    public ResponseEntity<ApiResponse<List<OsOptionDTO>>> getAvailableOs() {
        List<OsOptionDTO> osList = List.of(
            new OsOptionDTO("ubuntu", "Ubuntu", List.of("20.04", "22.04", "24.04")),
            new OsOptionDTO("debian", "Debian", List.of("11", "12")),
            new OsOptionDTO("centos", "CentOS", List.of("7", "8")),
            new OsOptionDTO("rocky", "Rocky Linux", List.of("8", "9")),
            new OsOptionDTO("almalinux", "AlmaLinux", List.of("8", "9")),
            new OsOptionDTO("fedora", "Fedora", List.of("38", "39")),
            new OsOptionDTO("opensuse", "openSUSE", List.of("15.4", "15.5")),
            new OsOptionDTO("alpine", "Alpine Linux", List.of("3.18", "3.19"))
        );
        return ResponseEntity.ok(ApiResponse.success(osList));
    }
}
