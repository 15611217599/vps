package com.vps.vpsserver.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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

import com.vps.vpsserver.dto.ServerCategoryDTO;
import com.vps.vpsserver.service.ServerCategoryService;

import jakarta.validation.Valid;

/**
 * 服务器类别控制器
 */
@RestController
@RequestMapping("/api/categories")
@CrossOrigin(origins = "*")
public class ServerCategoryController {

    @Autowired
    private ServerCategoryService categoryService;


    /**
     * 获取所有类别（分页）
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllCategories(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search) {

        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<ServerCategoryDTO> categoryPage;

            if (search != null && !search.trim().isEmpty()) {
                categoryPage = categoryService.searchCategories(search.trim(), pageable);
            } else {
                categoryPage = categoryService.getCategories(pageable);
            }

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", categoryPage.getContent());
            response.put("totalElements", categoryPage.getTotalElements());
            response.put("totalPages", categoryPage.getTotalPages());
            response.put("currentPage", categoryPage.getNumber());
            response.put("size", categoryPage.getSize());
            response.put("hasNext", categoryPage.hasNext());
            response.put("hasPrevious", categoryPage.hasPrevious());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "获取类别列表失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 获取所有激活的类别
     */
    @GetMapping("/active")
    public ResponseEntity<Map<String, Object>> getAllActiveCategories() {
        try {
            List<ServerCategoryDTO> categories = categoryService.getAllActiveCategories();

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", categories);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "获取激活类别失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }



    /**
     * 根据ID获取类别
     */
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getCategoryById(@PathVariable Long id) {
        try {
            ServerCategoryDTO category = categoryService.getCategoryById(id);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", category);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "获取类别失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 创建新类别 - 仅限管理员
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> createCategory(@Valid @RequestBody ServerCategoryDTO categoryDTO) {
        try {
            ServerCategoryDTO createdCategory = categoryService.createCategory(categoryDTO);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", createdCategory);
            response.put("message", "类别创建成功");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "创建类别失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 更新类别 - 仅限管理员
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> updateCategory(
            @PathVariable Long id,
            @Valid @RequestBody ServerCategoryDTO categoryDTO) {
        try {
            ServerCategoryDTO updatedCategory = categoryService.updateCategory(id, categoryDTO);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", updatedCategory);
            response.put("message", "类别更新成功");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "更新类别失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 删除类别 - 仅限管理员
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> deleteCategory(@PathVariable Long id) {
        try {
            categoryService.deleteCategory(id);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "类别删除成功");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "删除类别失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 检查类别名称是否存在
     */
    @GetMapping("/check-name")
    public ResponseEntity<Map<String, Object>> checkCategoryName(
            @RequestParam String name,
            @RequestParam(required = false) Long excludeId) {
        try {
            boolean exists;
            if (excludeId != null) {
                exists = categoryService.existsByNameAndIdNot(name, excludeId);
            } else {
                exists = categoryService.existsByName(name);
            }

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("exists", exists);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "检查类别名称失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
}
