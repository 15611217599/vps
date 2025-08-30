package com.vps.vpsserver.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.vps.vpsserver.dto.ServerGroupWithServersDTO;
import com.vps.vpsserver.dto.ServerRequestDTO;
import com.vps.vpsserver.dto.ServerResponseDTO;
import com.vps.vpsserver.dto.ServerStatsDTO;
import com.vps.vpsserver.service.ServerService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/servers")
@CrossOrigin(origins = "*")
public class ServerController {

    @Autowired
    private ServerService serverService;

    /**
     * 获取所有服务器
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllServers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createTime") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Long groupId) {

        try {
            Sort sort = sortDir.equalsIgnoreCase("desc")
                    ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
            Pageable pageable = PageRequest.of(page, size, sort);

            Page<ServerResponseDTO> serverPage;
            if (search != null && !search.trim().isEmpty()) {
                serverPage = serverService.searchServers(search.trim(), pageable);
            } else if (status != null || groupId != null) {
                serverPage = serverService.getServersByFilters(status, groupId, pageable);
            } else {
                serverPage = serverService.getServersPage(pageable);
            }

            Map<String, Object> pageData = new HashMap<>();
            pageData.put("content", serverPage.getContent());
            pageData.put("totalElements", serverPage.getTotalElements());
            pageData.put("totalPages", serverPage.getTotalPages());
            pageData.put("size", serverPage.getSize());
            pageData.put("number", serverPage.getNumber());
            pageData.put("hasNext", serverPage.hasNext());
            pageData.put("hasPrevious", serverPage.hasPrevious());

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", pageData);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "获取服务器列表失败" + ": " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * 获取服务器统计信息
     */
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getServerStats() {
        try {
            ServerStatsDTO stats = serverService.getServerStats();
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", stats);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "获取服务器统计失败" + ": " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * 根据ID获取服务器
     */
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getServerById(@PathVariable Long id) {
        try {
            ServerResponseDTO server = serverService.getServerById(id);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", server);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "根据ID获取服务器失败" + ": " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * 创建服务器
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> createServer(@Valid @RequestBody ServerRequestDTO serverRequestDTO) {
        try {
            ServerResponseDTO createdServer = serverService.createServer(serverRequestDTO);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "服务器创建成功");
            response.put("data", createdServer);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (RuntimeException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "创建服务器失败" + ": " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * 更新服务器
     */
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateServer(@PathVariable Long id,
            @Valid @RequestBody ServerRequestDTO serverRequestDTO) {
        try {
            ServerResponseDTO updatedServer = serverService.updateServer(id, serverRequestDTO);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "服务器更新成功");
            response.put("data", updatedServer);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "更新服务器失败" + ": " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * 删除服务器
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteServer(@PathVariable Long id) {
        try {
            serverService.deleteServer(id);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "服务器删除成功");
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "删除服务器失败" + ": " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * 根据状态获取服务器
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<Map<String, Object>> getServersByStatus(@PathVariable String status) {
        try {
            List<ServerResponseDTO> servers = serverService.getServersByStatus(status);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", servers);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "获取服务器列表失败" + ": " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * 根据分组获取服务器
     */
    @GetMapping("/group/{groupId}")
    public ResponseEntity<Map<String, Object>> getServersByGroupId(@PathVariable Long groupId) {
        try {
            List<ServerResponseDTO> servers = serverService.getServersByGroupId(groupId);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", servers);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "获取服务器列表失败" + ": " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * 检查IP是否存在
     */
    @GetMapping("/check-ip")
    public ResponseEntity<Map<String, Object>> checkIpExists(@RequestParam String ip,
            @RequestParam(required = false) Long excludeId) {
        try {
            boolean exists;
            if (excludeId != null) {
                exists = serverService.isIpExistsExcludeId(ip, excludeId);
            } else {
                exists = serverService.isIpExists(ip);
            }

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("exists", exists);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "检查IP失败" + ": " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * 获取按分组组织的服务器数据
     */
    @GetMapping("/grouped")
    public ResponseEntity<Map<String, Object>> getGroupedServers() {
        try {
            List<ServerGroupWithServersDTO> groupedServers = serverService.getGroupedServers();

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", groupedServers);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "获取按分组组织的数据失败" + ": " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * 创建服务器（带默认值）
     */
    @PostMapping("/with-defaults")
    public ResponseEntity<Map<String, Object>> createServerWithDefaults(@Valid @RequestBody Map<String, Object> requestData) {
        try {
            // 创建带默认值的服务器请求DTO
            ServerRequestDTO serverRequestDTO = new ServerRequestDTO();

            // 必填字段
            serverRequestDTO.setIp((String) requestData.get("ip"));
            serverRequestDTO.setPort((Integer) requestData.get("port"));
            serverRequestDTO.setUsername((String) requestData.get("username"));
            serverRequestDTO.setPassword((String) requestData.get("password"));

            // 设置默认值
            serverRequestDTO.setStatus("OFFLINE");
            serverRequestDTO.setOperatingSystem("CentOS - 7.9.2111 - x64");
            serverRequestDTO.setCpuCores("32 Core");
            serverRequestDTO.setMemory("32G");
            serverRequestDTO.setDiskSpace("200G SSD");
            serverRequestDTO.setDiskType("FR - SSD");
            serverRequestDTO.setNetworkSpeed("25M");

            // 如果提供了分组ID，设置分组
            if (requestData.containsKey("groupId")) {
                serverRequestDTO.setGroupId(((Number) requestData.get("groupId")).longValue());
            }

            ServerResponseDTO createdServer = serverService.createServer(serverRequestDTO);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "服务器创建成功");
            response.put("data", createdServer);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (RuntimeException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "创建服务器失败" + ": " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * 获取所有分组选项
     */
    @GetMapping("/groups")
    public ResponseEntity<Map<String, Object>> getAllGroups() {
        try {
            List<ServerGroupWithServersDTO> groups = serverService.getGroupedServers();

            // 转换为简单的选项格式
            List<Map<String, Object>> groupOptions = groups.stream()
                    .map(group -> {
                        Map<String, Object> option = new HashMap<>();
                        option.put("value", group.getId());
                        option.put("title", group.getName());
                        option.put("description", group.getDescription());
                        return option;
                    })
                    .collect(Collectors.toList());

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", groupOptions);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "获取分组列表失败" + ": " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
}
