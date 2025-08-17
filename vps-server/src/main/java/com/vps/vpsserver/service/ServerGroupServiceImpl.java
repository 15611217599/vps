package com.vps.vpsserver.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.vps.vpsserver.dto.ServerGroupDTO;
import com.vps.vpsserver.entity.ServerCategory;
import com.vps.vpsserver.entity.ServerGroup;
import com.vps.vpsserver.repository.ServerCategoryRepository;
import com.vps.vpsserver.repository.ServerGroupRepository;

/**
 * 服务器分组服务实现
 */
@Service
@Transactional
public class ServerGroupServiceImpl implements ServerGroupService {
    
    @Autowired
    private ServerGroupRepository groupRepository;
    
    @Autowired
    private ServerCategoryRepository categoryRepository;
    
    @Autowired
    private I18nService i18nService;
    
    @Override
    @Transactional(readOnly = true)
    public Page<ServerGroupDTO> getGroups(Pageable pageable) {
        return groupRepository.findAllByOrderBySortOrderAsc(pageable)
                .map(ServerGroupDTO::fromEntity);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<ServerGroupDTO> getAllActiveGroups() {
        return groupRepository.findByIsActiveOrderBySortOrderAsc(true).stream()
                .map(ServerGroupDTO::fromEntity)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public ServerGroupDTO getGroupById(Long id) {
        ServerGroup group = groupRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(i18nService.getMessage("group.notFound") + ": " + id));
        return ServerGroupDTO.fromEntity(group);
    }
    
    @Override
    public ServerGroupDTO createGroup(ServerGroupDTO groupDTO) {
        // 检查名称是否已存在
        String nameToCheck = groupDTO.getName();
        if (nameToCheck != null && groupRepository.existsByName(nameToCheck)) {
            throw new RuntimeException(i18nService.getMessage("group.nameExists") + ": " + nameToCheck);
        }
        
        // 查找类别
        ServerCategory category = categoryRepository.findById(groupDTO.getCategoryId())
                .orElseThrow(() -> new RuntimeException(i18nService.getMessage("category.notFound") + ": " + groupDTO.getCategoryId()));
        
        // 创建分组实体
        ServerGroup group = groupDTO.toEntity();
        group.setCategory(category);
        
        // 如果没有设置排序顺序，设置为最大值+1
        if (group.getSortOrder() == null || group.getSortOrder() == 0) {
            Integer maxOrder = groupRepository.findMaxSortOrderByCategory(groupDTO.getCategoryId());
            group.setSortOrder(maxOrder != null ? maxOrder + 1 : 1);
        }
        
        // 保存分组
        ServerGroup savedGroup = groupRepository.save(group);
        return ServerGroupDTO.fromEntity(savedGroup);
    }
    
    @Override
    public ServerGroupDTO updateGroup(Long id, ServerGroupDTO groupDTO) {
        // 查找现有分组
        ServerGroup existingGroup = groupRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(i18nService.getMessage("group.notFound") + ": " + id));
        
        // 检查名称是否被其他分组使用
        String nameToCheck = groupDTO.getName();
        if (nameToCheck != null && groupRepository.existsByNameAndIdNot(nameToCheck, id)) {
            throw new RuntimeException(i18nService.getMessage("group.nameExists") + ": " + nameToCheck);
        }
        
        // 查找类别
        ServerCategory category = categoryRepository.findById(groupDTO.getCategoryId())
                .orElseThrow(() -> new RuntimeException(i18nService.getMessage("category.notFound") + ": " + groupDTO.getCategoryId()));
        
        // 更新分组信息
        existingGroup.setName(groupDTO.getName());
        existingGroup.setDescription(groupDTO.getDescription());
        existingGroup.setRegion(groupDTO.getRegion());
        existingGroup.setCountry(groupDTO.getCountry());
        existingGroup.setCity(groupDTO.getCity());
        existingGroup.setSortOrder(groupDTO.getSortOrder());
        existingGroup.setIsActive(groupDTO.getIsActive());
        existingGroup.setCategory(category);
        
        // 保存更新
        ServerGroup updatedGroup = groupRepository.save(existingGroup);
        return ServerGroupDTO.fromEntity(updatedGroup);
    }
    
    @Override
    public void deleteGroup(Long id) {
        ServerGroup group = groupRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(i18nService.getMessage("group.notFound") + ": " + id));
        
        // 检查是否有服务器关联到此分组
        if (group.getServers() != null && !group.getServers().isEmpty()) {
            throw new RuntimeException(i18nService.getMessage("group.cannotDeleteWithServers", group.getServers().size()));
        }
        
        groupRepository.deleteById(id);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<ServerGroupDTO> searchGroups(String keyword, Pageable pageable) {
        return groupRepository.searchGroups(keyword, pageable)
                .map(ServerGroupDTO::fromEntity);
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean existsByName(String name) {
        return groupRepository.existsByName(name);
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean existsByNameAndIdNot(String name, Long excludeId) {
        return groupRepository.existsByNameAndIdNot(name, excludeId);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<ServerGroupDTO> getGroupsByCategory(Long categoryId) {
        return groupRepository.findByCategoryIdAndIsActiveOrderBySortOrderAsc(categoryId, true).stream()
                .map(ServerGroupDTO::fromEntity)
                .collect(Collectors.toList());
    }
}