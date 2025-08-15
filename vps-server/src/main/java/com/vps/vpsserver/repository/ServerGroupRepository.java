package com.vps.vpsserver.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.vps.vpsserver.entity.ServerGroup;

/**
 * 服务器分组数据访问接口
 */
@Repository
public interface ServerGroupRepository extends JpaRepository<ServerGroup, Long> {
    
    /**
     * 根据名称查找分组
     */
    Optional<ServerGroup> findByName(String name);
    
    /**
     * 检查名称是否存在（排除指定ID）
     */
    boolean existsByNameAndIdNot(String name, Long id);
    
    /**
     * 检查名称是否存在
     */
    boolean existsByName(String name);
    
    /**
     * 根据类别ID查找分组
     */
    List<ServerGroup> findByCategoryIdOrderBySortOrderAsc(Long categoryId);
    
    /**
     * 根据类别ID和激活状态查找分组
     */
    List<ServerGroup> findByCategoryIdAndIsActiveOrderBySortOrderAsc(Long categoryId, Boolean isActive);
    
    /**
     * 根据激活状态查找分组
     */
    List<ServerGroup> findByIsActiveOrderBySortOrderAsc(Boolean isActive);
    
    /**
     * 获取所有激活的分组（按排序顺序）
     */
    List<ServerGroup> findAllByIsActiveTrueOrderBySortOrderAsc();
    
    /**
     * 分页查询所有分组（按排序顺序）
     */
    Page<ServerGroup> findAllByOrderBySortOrderAsc(Pageable pageable);
    
    /**
     * 根据类别ID分页查询分组
     */
    Page<ServerGroup> findByCategoryIdOrderBySortOrderAsc(Long categoryId, Pageable pageable);
    
    /**
     * 根据名称模糊搜索分组
     */
    @Query("SELECT g FROM ServerGroup g WHERE LOWER(g.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(g.description) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(g.region) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(g.country) LIKE LOWER(CONCAT('%', :keyword, '%')) ORDER BY g.sortOrder ASC")
    Page<ServerGroup> searchGroups(@Param("keyword") String keyword, Pageable pageable);
    
    /**
     * 根据类别ID和关键词搜索分组
     */
    @Query("SELECT g FROM ServerGroup g WHERE g.category.id = :categoryId AND (LOWER(g.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(g.description) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(g.region) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(g.country) LIKE LOWER(CONCAT('%', :keyword, '%'))) ORDER BY g.sortOrder ASC")
    Page<ServerGroup> searchGroupsByCategory(@Param("categoryId") Long categoryId, @Param("keyword") String keyword, Pageable pageable);
    
    /**
     * 根据地区查找分组
     */
    List<ServerGroup> findByRegionOrderBySortOrderAsc(String region);
    
    /**
     * 根据国家查找分组
     */
    List<ServerGroup> findByCountryOrderBySortOrderAsc(String country);
    
    /**
     * 获取指定类别下的最大排序号
     */
    @Query("SELECT MAX(g.sortOrder) FROM ServerGroup g WHERE g.category.id = :categoryId")
    Integer findMaxSortOrderByCategory(@Param("categoryId") Long categoryId);
    
    /**
     * 统计指定类别下的分组数量
     */
    long countByCategoryId(Long categoryId);
    
    /**
     * 统计指定类别下激活的分组数量
     */
    long countByCategoryIdAndIsActive(Long categoryId, Boolean isActive);
}
