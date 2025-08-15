package com.vps.vpsserver.service;

import java.util.List;
import java.util.stream.Collectors;

import com.vps.vpsserver.entity.ServerGroup;
import com.vps.vpsserver.repository.ServerGroupRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.vps.vpsserver.dto.ServerGroupWithServersDTO;
import com.vps.vpsserver.dto.ServerRequestDTO;
import com.vps.vpsserver.dto.ServerResponseDTO;
import com.vps.vpsserver.dto.ServerStatsDTO;
import com.vps.vpsserver.entity.Server;
import com.vps.vpsserver.repository.ServerRepository;

@Service
@Transactional
public class ServerServiceImpl implements ServerService {
    
    @Autowired
    private ServerRepository serverRepository;
    
    @Autowired
    private ServerGroupRepository serverGroupRepository;
    
    @Autowired
    private I18nService i18nService;
    
    @Override
    @Transactional(readOnly = true)
    public List<ServerResponseDTO> getAllServers() {
        return serverRepository.findAll().stream()
                .map(ServerResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<ServerResponseDTO> getServersPage(Pageable pageable) {
        return serverRepository.findAllByOrderByCreateTimeDesc(pageable)
                .map(ServerResponseDTO::fromEntity);
    }
    
    @Override
    @Transactional(readOnly = true)
    public ServerResponseDTO getServerById(Long id) {
        Server server = serverRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(i18nService.getMessage("server.notFound") + ": " + id));
        return ServerResponseDTO.fromEntity(server);
    }
    
    @Override
    public ServerResponseDTO createServer(ServerRequestDTO serverRequestDTO) {
        // 检查IP是否已存在
        if (serverRepository.existsByIp(serverRequestDTO.getIp())) {
            throw new RuntimeException(i18nService.getMessage("server.ipExists") + ": " + serverRequestDTO.getIp());
        }
        
        // 创建服务器实体
        Server server = new Server();
        server.setName(serverRequestDTO.getName());
        server.setIp(serverRequestDTO.getIp());
        server.setType(serverRequestDTO.getType());
        server.setLocation(serverRequestDTO.getLocation());
        server.setStatus(Server.ServerStatus.fromValue(serverRequestDTO.getStatus()));
        server.setProvider(serverRequestDTO.getProvider());
        server.setPort(serverRequestDTO.getPort());
        server.setUsername(serverRequestDTO.getUsername());
        server.setPassword(serverRequestDTO.getPassword());
        server.setCpuCores(serverRequestDTO.getCpuCores());
        server.setMemory(serverRequestDTO.getMemory());
        server.setDiskSpace(serverRequestDTO.getDiskSpace());
        server.setDiskType(serverRequestDTO.getDiskType());
        server.setNetworkSpeed(serverRequestDTO.getNetworkSpeed());
        server.setOperatingSystem(serverRequestDTO.getOperatingSystem());
        
        // 设置分组关联
        if (serverRequestDTO.getGroupId() != null) {
            ServerGroup group = serverGroupRepository.findById(serverRequestDTO.getGroupId())
                    .orElseThrow(() -> new RuntimeException(i18nService.getMessage("group.notFound") + ": " + serverRequestDTO.getGroupId()));
            server.setGroup(group);
        }
        
        // 保存服务器
        Server savedServer = serverRepository.save(server);
        return ServerResponseDTO.fromEntity(savedServer);
    }
    
    @Override
    public ServerResponseDTO updateServer(Long id, ServerRequestDTO serverRequestDTO) {
        // 查找现有服务器
        Server existingServer = serverRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(i18nService.getMessage("server.notFound") + ": " + id));
        
        // 检查IP是否被其他服务器使用
        if (serverRepository.existsByIpAndIdNot(serverRequestDTO.getIp(), id)) {
            throw new RuntimeException(i18nService.getMessage("server.ipExists") + ": " + serverRequestDTO.getIp());
        }
        
        // 更新服务器字段
        existingServer.setName(serverRequestDTO.getName());
        existingServer.setIp(serverRequestDTO.getIp());
        existingServer.setType(serverRequestDTO.getType());
        existingServer.setLocation(serverRequestDTO.getLocation());
        existingServer.setStatus(Server.ServerStatus.fromValue(serverRequestDTO.getStatus()));
        existingServer.setProvider(serverRequestDTO.getProvider());
        existingServer.setPort(serverRequestDTO.getPort());
        existingServer.setUsername(serverRequestDTO.getUsername());
        existingServer.setPassword(serverRequestDTO.getPassword());
        existingServer.setCpuCores(serverRequestDTO.getCpuCores());
        existingServer.setMemory(serverRequestDTO.getMemory());
        existingServer.setDiskSpace(serverRequestDTO.getDiskSpace());
        existingServer.setDiskType(serverRequestDTO.getDiskType());
        existingServer.setNetworkSpeed(serverRequestDTO.getNetworkSpeed());
        existingServer.setOperatingSystem(serverRequestDTO.getOperatingSystem());
        
        // 更新分组关联
        if (serverRequestDTO.getGroupId() != null) {
            ServerGroup group = serverGroupRepository.findById(serverRequestDTO.getGroupId())
                    .orElseThrow(() -> new RuntimeException(i18nService.getMessage("group.notFound") + ": " + serverRequestDTO.getGroupId()));
            existingServer.setGroup(group);
        } else {
            existingServer.setGroup(null);
        }
        
        // 保存更新
        Server updatedServer = serverRepository.save(existingServer);
        return ServerResponseDTO.fromEntity(updatedServer);
    }
    
    @Override
    public void deleteServer(Long id) {
        if (!serverRepository.existsById(id)) {
            throw new RuntimeException(i18nService.getMessage("server.notFound") + ": " + id);
        }
        serverRepository.deleteById(id);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<ServerResponseDTO> searchServers(String keyword, Pageable pageable) {
        return serverRepository.searchServers(keyword, pageable)
                .map(ServerResponseDTO::fromEntity);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<ServerResponseDTO> getServersByStatus(String status) {
        Server.ServerStatus serverStatus = Server.ServerStatus.fromValue(status);
        return serverRepository.findByStatus(serverStatus).stream()
                .map(ServerResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<ServerResponseDTO> getServersByType(String type) {
        return serverRepository.findByType(type).stream()
                .map(ServerResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public ServerStatsDTO getServerStats() {
        Object[] stats = serverRepository.getServerStats();
        if (stats != null && stats.length >= 3) {
            long total = ((Number) stats[0]).longValue();
            long online = ((Number) stats[1]).longValue();
            long offline = ((Number) stats[2]).longValue();
            long warning = Math.round(total * 0.1); // 模拟警告数量
            
            return new ServerStatsDTO(total, online, offline, warning);
        }
        return new ServerStatsDTO(0, 0, 0, 0);
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean isIpExists(String ip) {
        return serverRepository.existsByIp(ip);
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean isIpExistsExcludeId(String ip, Long excludeId) {
        return serverRepository.existsByIpAndIdNot(ip, excludeId);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<ServerGroupWithServersDTO> getGroupedServers() {
        List<ServerGroup> groups = serverGroupRepository.findAllByIsActiveTrueOrderBySortOrderAsc();
        return groups.stream()
                .map(ServerGroupWithServersDTO::fromEntity)
                .collect(Collectors.toList());
    }
}
