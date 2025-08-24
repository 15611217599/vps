package com.vps.vpsserver.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.vps.vpsserver.dto.ServerCategoryDTO;
import com.vps.vpsserver.entity.ServerCategory;
import com.vps.vpsserver.repository.ServerCategoryRepository;

/**
 * 服务器类别服务实现类
 */
@Service
@Transactional
public class ServerCategoryServiceImpl implements ServerCategoryService {
    
    @Autowired
    private ServerCategoryRepository categoryRepository;
    
    
    
    @Override
    public ServerCategoryDTO createCategory(ServerCategoryDTO categoryDTO) {
        // 检查名称是否已存在
        String nameToCheck = categoryDTO.getName();
        if (nameToCheck != null && categoryRepository.existsByName(nameToCheck)) {
            throw new RuntimeException("类别名称已存在: " + nameToCheck);
        }
        
        ServerCategory category = categoryDTO.toEntity();
        
        // 设置排序号
        if (category.getSortOrder() == null) {
            Integer maxSortOrder = categoryRepository.findMaxSortOrder();
            category.setSortOrder(maxSortOrder != null ? maxSortOrder + 1 : 1);
        }
        
        // 设置默认激活状态
        if (category.getIsActive() == null) {
            category.setIsActive(true);
        }
        
        ServerCategory savedCategory = categoryRepository.save(category);
        return ServerCategoryDTO.fromEntity(savedCategory);
    }
    
    @Override
    public ServerCategoryDTO updateCategory(Long id, ServerCategoryDTO categoryDTO) {
        ServerCategory existingCategory = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("未找到类别: " + id));
        
        // 检查名称是否已被其他类别使用
        String nameToCheck = categoryDTO.getName();
        if (nameToCheck != null && categoryRepository.existsByNameAndIdNot(nameToCheck, id)) {
            throw new RuntimeException("类别名称已存在: " + nameToCheck);
        }
        
        // 更新字段
        if (categoryDTO.getName() != null) {
            existingCategory.setName(categoryDTO.getName());
        }
        if (categoryDTO.getDescription() != null) {
            existingCategory.setDescription(categoryDTO.getDescription());
        }
        
        if (categoryDTO.getSortOrder() != null) {
            existingCategory.setSortOrder(categoryDTO.getSortOrder());
        }
        
        if (categoryDTO.getIsActive() != null) {
            existingCategory.setIsActive(categoryDTO.getIsActive());
        }
        
        ServerCategory updatedCategory = categoryRepository.save(existingCategory);
        return ServerCategoryDTO.fromEntity(updatedCategory);
    }
    
    @Override
    public void deleteCategory(Long id) {
        ServerCategory category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("未找到类别: " + id));
        
        // 检查是否有关联的分组
        if (category.getGroups() != null && !category.getGroups().isEmpty()) {
            throw new RuntimeException("该类别下存在分组，无法删除");
        }
        
        categoryRepository.delete(category);
    }
    
    @Override
    @Transactional(readOnly = true)
    public ServerCategoryDTO getCategoryById(Long id) {
        ServerCategory category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("未找到类别: " + id));
        return ServerCategoryDTO.fromEntity(category);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<ServerCategoryDTO> getAllActiveCategories() {
        List<ServerCategory> categories = categoryRepository.findByIsActiveOrderBySortOrderAsc(true);
        return categories.stream()
                .map(ServerCategoryDTO::fromEntity)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<ServerCategoryDTO> getCategories(Pageable pageable) {
        Page<ServerCategory> categoryPage = categoryRepository.findAllByOrderBySortOrderAsc(pageable);
        return categoryPage.map(ServerCategoryDTO::fromEntity);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<ServerCategoryDTO> searchCategories(String keyword, Pageable pageable) {
        Page<ServerCategory> categoryPage = categoryRepository.searchCategories(keyword, pageable);
        return categoryPage.map(ServerCategoryDTO::fromEntity);
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean existsByName(String name) {
        return categoryRepository.existsByName(name);
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean existsByNameAndIdNot(String name, Long id) {
        return categoryRepository.existsByNameAndIdNot(name, id);
    }
}
