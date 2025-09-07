package com.vps.vpsserver.controller;

import com.vps.vpsserver.dto.*;
import com.vps.vpsserver.service.ServerManagementService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/server-management")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ServerManagementController {
    
    private final ServerManagementService serverManagementService;
    
    /**
     * 执行服务器操作
     */
    @PostMapping("/action")
    public ResponseEntity<ApiResponse<String>> executeAction(@Valid @RequestBody ServerActionRequest request) {
        ApiResponse<String> response = serverManagementService.executeServerAction(request);
        return ResponseEntity.ok(response);
    }
    
    /**
     * 获取服务器监控信息
     */
    @GetMapping("/{serverId}/monitoring")
    public ResponseEntity<ApiResponse<ServerMonitoringDTO>> getMonitoring(@PathVariable Long serverId) {
        ApiResponse<ServerMonitoringDTO> response = serverManagementService.getServerMonitoring(serverId);
        return ResponseEntity.ok(response);
    }
    
    /**
     * 获取服务器操作日志
     */
    @GetMapping("/{serverId}/logs")
    public ResponseEntity<ApiResponse<List<ServerActionLogDTO>>> getActionLogs(
            @PathVariable Long serverId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        ApiResponse<List<ServerActionLogDTO>> response = serverManagementService.getServerActionLogs(serverId, page, size);
        return ResponseEntity.ok(response);
    }
    
    /**
     * 测试服务器连接
     */
    @PostMapping("/{serverId}/test-connection")
    public ResponseEntity<ApiResponse<Boolean>> testConnection(@PathVariable Long serverId) {
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
