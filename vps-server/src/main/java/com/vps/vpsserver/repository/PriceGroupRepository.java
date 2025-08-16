package com.vps.vpsserver.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.vps.vpsserver.entity.PriceGroup;

/**
 * 价格组数据访问接口
 */
@Repository
public interface PriceGroupRepository extends JpaRepository<PriceGroup, Long> {
    
    /**
     * 根据名称查找价格组
     */
    Optional<PriceGroup> findByName(String name);
    
    /**
     * 检查名称是否存在
     */
    boolean existsByName(String name);
    
    /**
     * 检查名称是否存在（排除指定ID）
     */
    boolean existsByNameAndIdNot(String name, Long id);
    
    /**
     * 根据激活状态查找价格组
     */
    List<PriceGroup> findByIsActiveOrderBySortOrderAsc(Boolean isActive);
    
    /**
     * 分页查询所有价格组（按排序顺序）
     */
    Page<PriceGroup> findAllByOrderBySortOrderAsc(Pageable pageable);
    
    /**
     * 根据名称或描述模糊搜索价格组
     */
    @Query("SELECT p FROM PriceGroup p WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(p.description) LIKE LOWER(CONCAT('%', :keyword, '%')) ORDER BY p.sortOrder ASC")
    Page<PriceGroup> searchPriceGroups(@Param("keyword") String keyword, Pageable pageable);
    
    /**
     * 获取最大排序号
     */
    @Query("SELECT MAX(p.sortOrder) FROM PriceGroup p")
    Integer findMaxSortOrder();
    
    /**
     * 根据激活状态分页查询
     */
    Page<PriceGroup> findByIsActiveOrderBySortOrderAsc(Boolean isActive, Pageable pageable);
}
