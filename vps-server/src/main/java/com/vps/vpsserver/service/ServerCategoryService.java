package com.vps.vpsserver.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.vps.vpsserver.dto.ServerCategoryDTO;

/**
 * 服务器类别服务接口
 */
public interface ServerCategoryService {
    
    /**
     * 创建服务器类别
     */
    ServerCategoryDTO createCategory(ServerCategoryDTO categoryDTO);
    
    /**
     * 更新服务器类别
     */
    ServerCategoryDTO updateCategory(Long id, ServerCategoryDTO categoryDTO);
    
    /**
     * 删除服务器类别
     */
    void deleteCategory(Long id);
    
    /**
     * 根据ID获取服务器类别
     */
    ServerCategoryDTO getCategoryById(Long id);
    
    /**
     * 获取所有激活的服务器类别
     */
    List<ServerCategoryDTO> getAllActiveCategories();
    
    /**
     * 分页获取服务器类别
     */
    Page<ServerCategoryDTO> getCategories(Pageable pageable);
    
    /**
     * 搜索服务器类别
     */
    Page<ServerCategoryDTO> searchCategories(String keyword, Pageable pageable);
    
    /**
     * 检查类别名称是否存在（向后兼容）
     */
    boolean existsByName(String name);
    
    /**
     * 检查类别名称是否存在（排除指定ID，向后兼容）
     */
    boolean existsByNameAndIdNot(String name, Long id);
}
