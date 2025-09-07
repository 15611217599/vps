package com.vps.vpsserver.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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

import com.vps.vpsserver.dto.ServerGroupDTO;
import com.vps.vpsserver.service.ServerGroupService;

import jakarta.validation.Valid;

/**
 * 服务器分组控制器
 */
@RestController
@RequestMapping("/api/groups")
@CrossOrigin(origins = "*")
public class ServerGroupController {
    
    @Autowired
    private ServerGroupService groupService;
    
    
    
    /**
     * 获取所有分组（分页）
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllGroups(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search) {
        
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<ServerGroupDTO> groupPage;
            
            if (search != null && !search.trim().isEmpty()) {
                groupPage = groupService.searchGroups(search.trim(), pageable);
            } else {
                groupPage = groupService.getGroups(pageable);
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", groupPage.getContent());
            response.put("totalElements", groupPage.getTotalElements());
            response.put("totalPages", groupPage.getTotalPages());
            response.put("currentPage", groupPage.getNumber());
            response.put("size", groupPage.getSize());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "获取分组列表失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    /**
     * 获取所有激活的分组
     */
    @GetMapping("/active")
    public ResponseEntity<Map<String, Object>> getAllActiveGroups() {
        try {
            List<ServerGroupDTO> groups = groupService.getAllActiveGroups();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", groups);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "获取激活分组失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    /**
     * 根据ID获取分组
     */
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getGroupById(@PathVariable Long id) {
        try {
            ServerGroupDTO group = groupService.getGroupById(id);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", group);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "获取分组失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    /**
     * 创建新分组 - 仅限管理员
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> createGroup(@Valid @RequestBody ServerGroupDTO groupDTO) {
        try {
            ServerGroupDTO createdGroup = groupService.createGroup(groupDTO);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", createdGroup);
            response.put("message", "分组创建成功");
            
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "创建分组失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    /**
     * 更新分组 - 仅限管理员
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> updateGroup(
            @PathVariable Long id, 
            @Valid @RequestBody ServerGroupDTO groupDTO) {
        try {
            ServerGroupDTO updatedGroup = groupService.updateGroup(id, groupDTO);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", updatedGroup);
            response.put("message", "分组更新成功");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "更新分组失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    /**
     * 删除分组 - 仅限管理员
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> deleteGroup(@PathVariable Long id) {
        try {
            groupService.deleteGroup(id);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "分组删除成功");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "删除分组失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    /**
     * 检查分组名称是否存在
     */
    @GetMapping("/check-name")
    public ResponseEntity<Map<String, Object>> checkGroupName(
            @RequestParam String name,
            @RequestParam(required = false) Long excludeId) {
        try {
            boolean exists;
            if (excludeId != null) {
                exists = groupService.existsByNameAndIdNot(name, excludeId);
            } else {
                exists = groupService.existsByName(name);
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("exists", exists);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "检查分组名称失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
}