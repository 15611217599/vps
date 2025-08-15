package com.vps.vpsserver.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.vps.vpsserver.entity.ServerCategory;

/**
 * 服务器类别数据访问接口
 */
@Repository
public interface ServerCategoryRepository extends JpaRepository<ServerCategory, Long> {
    
    /**
     * 根据名称查找类别
     */
    Optional<ServerCategory> findByName(String name);
    
    /**
     * 检查名称是否存在
     */
    boolean existsByName(String name);
    
    /**
     * 检查名称是否存在（排除指定ID）
     */
    boolean existsByNameAndIdNot(String name, Long id);

    
    /**
     * 根据激活状态查找类别
     */
    List<ServerCategory> findByIsActiveOrderBySortOrderAsc(Boolean isActive);
    
    /**
     * 分页查询所有类别（按排序顺序）
     */
    Page<ServerCategory> findAllByOrderBySortOrderAsc(Pageable pageable);
    
    /**
     * 根据名称模糊搜索类别
     */
    @Query("SELECT c FROM ServerCategory c WHERE LOWER(c.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(c.description) LIKE LOWER(CONCAT('%', :keyword, '%')) ORDER BY c.sortOrder ASC")
    Page<ServerCategory> searchCategories(@Param("keyword") String keyword, Pageable pageable);
    
    /**
     * 获取最大排序号
     */
    @Query("SELECT MAX(c.sortOrder) FROM ServerCategory c")
    Integer findMaxSortOrder();

}
