package com.vps.vpsserver.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.vps.vpsserver.dto.ServerGroupWithServersDTO;
import com.vps.vpsserver.dto.ServerRequestDTO;
import com.vps.vpsserver.dto.ServerResponseDTO;
import com.vps.vpsserver.dto.ServerStatsDTO;

public interface ServerService {
    
    /**
     * 获取所有服务器
     */
    List<ServerResponseDTO> getAllServers();
    
    /**
     * 分页获取服务器
     */
    Page<ServerResponseDTO> getServersPage(Pageable pageable);
    
    /**
     * 根据ID获取服务器
     */
    ServerResponseDTO getServerById(Long id);
    
    /**
     * 创建服务器
     */
    ServerResponseDTO createServer(ServerRequestDTO serverRequestDTO);
    
    /**
     * 更新服务器
     */
    ServerResponseDTO updateServer(Long id, ServerRequestDTO serverRequestDTO);
    
    /**
     * 删除服务器
     */
    void deleteServer(Long id);
    
    /**
     * 搜索服务器
     */
    Page<ServerResponseDTO> searchServers(String keyword, Pageable pageable);
    
    /**
     * 根据状态获取服务器
     */
    List<ServerResponseDTO> getServersByStatus(String status);
    
    /**
     * 根据分组获取服务器
     */
    List<ServerResponseDTO> getServersByGroupId(Long groupId);
    
    /**
     * 获取服务器统计信息
     */
    ServerStatsDTO getServerStats();
    
    /**
     * 检查IP是否已存在
     */
    boolean isIpExists(String ip);
    
    /**
     * 检查IP是否已存在（排除指定ID）
     */
    boolean isIpExistsExcludeId(String ip, Long excludeId);
    
    /**
     * 获取按分组组织的服务器数据
     */
    List<ServerGroupWithServersDTO> getGroupedServers();
}
