package com.vps.vpsserver.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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

import com.vps.vpsserver.dto.PriceGroupDTO;
import com.vps.vpsserver.service.PriceGroupService;

import jakarta.validation.Valid;

/**
 * 价格组控制器
 */
@RestController
@RequestMapping("/api/price-groups")
@CrossOrigin(origins = "*")
public class PriceGroupController {

    @Autowired
    private PriceGroupService priceGroupService;

    /**
     * 获取所有价格组（分页）
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllPriceGroups(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Boolean active) {

        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<PriceGroupDTO> priceGroupPage;

            if (search != null && !search.trim().isEmpty()) {
                priceGroupPage = priceGroupService.searchPriceGroups(search.trim(), pageable);
            } else if (active != null) {
                priceGroupPage = priceGroupService.getPriceGroupsByActive(active, pageable);
            } else {
                priceGroupPage = priceGroupService.getPriceGroups(pageable);
            }

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", priceGroupPage.getContent());
            response.put("totalElements", priceGroupPage.getTotalElements());
            response.put("totalPages", priceGroupPage.getTotalPages());
            response.put("currentPage", priceGroupPage.getNumber());
            response.put("size", priceGroupPage.getSize());
            response.put("hasNext", priceGroupPage.hasNext());
            response.put("hasPrevious", priceGroupPage.hasPrevious());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "获取价格组列表失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 获取所有激活的价格组
     */
    @GetMapping("/active")
    public ResponseEntity<Map<String, Object>> getActivePriceGroups() {
        try {
            List<PriceGroupDTO> priceGroups = priceGroupService.getAllActivePriceGroups();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", priceGroups);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "获取激活价格组失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 根据ID获取价格组
     */
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getPriceGroupById(@PathVariable Long id) {
        try {
            PriceGroupDTO priceGroup = priceGroupService.getPriceGroupById(id);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", priceGroup);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "获取价格组失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 创建价格组
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> createPriceGroup(@Valid @RequestBody PriceGroupDTO priceGroupDTO) {
        try {
            PriceGroupDTO createdPriceGroup = priceGroupService.createPriceGroup(priceGroupDTO);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", createdPriceGroup);
            response.put("message", "价格组创建成功");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "创建价格组失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 更新价格组
     */
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updatePriceGroup(
            @PathVariable Long id, 
            @Valid @RequestBody PriceGroupDTO priceGroupDTO) {
        try {
            PriceGroupDTO updatedPriceGroup = priceGroupService.updatePriceGroup(id, priceGroupDTO);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", updatedPriceGroup);
            response.put("message", "价格组更新成功");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "更新价格组失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 删除价格组
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deletePriceGroup(@PathVariable Long id) {
        try {
            priceGroupService.deletePriceGroup(id);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "价格组删除成功");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "删除价格组失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 检查价格组名称是否存在
     */
    @GetMapping("/check-name")
    public ResponseEntity<Map<String, Object>> checkNameExists(
            @RequestParam String name,
            @RequestParam(required = false) Long excludeId) {
        try {
            boolean exists;
            if (excludeId != null) {
                exists = priceGroupService.existsByNameAndIdNot(name, excludeId);
            } else {
                exists = priceGroupService.existsByName(name);
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("exists", exists);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "检查名称失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 应用折扣
     */
    @PostMapping("/{id}/apply-discount")
    public ResponseEntity<Map<String, Object>> applyDiscount(
            @PathVariable Long id,
            @RequestBody Map<String, Object> discountData) {
        try {
            Double discountPercentage = ((Number) discountData.get("discountPercentage")).doubleValue();
            String startTimeStr = (String) discountData.get("startTime");
            String endTimeStr = (String) discountData.get("endTime");
            
            PriceGroupDTO updatedPriceGroup = priceGroupService.applyDiscount(
                id, discountPercentage, startTimeStr, endTimeStr);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", updatedPriceGroup);
            response.put("message", "折扣应用成功");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "应用折扣失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 恢复原价
     */
    @PostMapping("/{id}/restore-original-prices")
    public ResponseEntity<Map<String, Object>> restoreOriginalPrices(@PathVariable Long id) {
        try {
            PriceGroupDTO updatedPriceGroup = priceGroupService.restoreOriginalPrices(id);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", updatedPriceGroup);
            response.put("message", "原价恢复成功");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "恢复原价失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
}
