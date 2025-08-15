package com.vps.vpsserver.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.vps.vpsserver.dto.ServerGroupDTO;

/**
 * 服务器分组服务接口
 */
public interface ServerGroupService {
    
    /**
     * 获取所有分组（分页）
     */
    Page<ServerGroupDTO> getGroups(Pageable pageable);
    
    /**
     * 获取所有激活的分组
     */
    List<ServerGroupDTO> getAllActiveGroups();
    
    /**
     * 根据ID获取分组
     */
    ServerGroupDTO getGroupById(Long id);
    
    /**
     * 创建分组
     */
    ServerGroupDTO createGroup(ServerGroupDTO groupDTO);
    
    /**
     * 更新分组
     */
    ServerGroupDTO updateGroup(Long id, ServerGroupDTO groupDTO);
    
    /**
     * 删除分组
     */
    void deleteGroup(Long id);
    
    /**
     * 搜索分组
     */
    Page<ServerGroupDTO> searchGroups(String keyword, Pageable pageable);
    
    /**
     * 检查名称是否存在
     */
    boolean existsByName(String name);
    
    /**
     * 检查名称是否存在（排除指定ID）
     */
    boolean existsByNameAndIdNot(String name, Long excludeId);
    
    /**
     * 根据类别ID获取分组
     */
    List<ServerGroupDTO> getGroupsByCategory(Long categoryId);
}