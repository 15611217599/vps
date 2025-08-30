package com.vps.vpsserver.controller;

import com.vps.vpsserver.dto.ApiResponse;
import com.vps.vpsserver.dto.ServerInstallRequest;
import com.vps.vpsserver.entity.Server;
import com.vps.vpsserver.service.ServerInstallService;
import com.vps.vpsserver.util.JwtUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/server-install")
@RequiredArgsConstructor
@Slf4j
public class ServerInstallController {
    
    private final ServerInstallService installService;
    private final JwtUtil jwtUtil;
    
    /**
     * 开始安装系统
     */
    @PostMapping("/start")
    public ResponseEntity<ApiResponse<String>> startInstall(
            @Valid @RequestBody ServerInstallRequest request,
            @RequestHeader("Authorization") String token) {
        
        try {
            String jwt = token.replace("Bearer ", "");
            Long userId = jwtUtil.extractUserId(jwt);
            
            log.info("用户 {} 开始安装系统，服务器ID: {}", userId, request.getServerId());
            
            installService.startInstall(request);
            
            return ResponseEntity.ok(ApiResponse.success("安装任务已开始", "系统安装已开始，请查看服务器详情页面监控进度"));
            
        } catch (Exception e) {
            log.error("开始安装系统失败: {}", e.getMessage(), e);
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("开始安装系统失败: " + e.getMessage()));
        }
    }
    
    /**
     * 获取服务器安装状态
     */
    @GetMapping("/status/{serverId}")
    public ResponseEntity<ApiResponse<Server>> getInstallStatus(
            @PathVariable Long serverId,
            @RequestHeader("Authorization") String token) {
        
        try {
            Server server = installService.getServerInstallStatus(serverId);
            
            return ResponseEntity.ok(ApiResponse.success(server));
            
        } catch (Exception e) {
            log.error("获取安装状态失败: {}", e.getMessage(), e);
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("获取安装状态失败: " + e.getMessage()));
        }
    }
}