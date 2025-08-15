package com.vps.vpsserver.repository;

import com.vps.vpsserver.entity.Server;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ServerRepository extends JpaRepository<Server, Long> {
    
    /**
     * 根据IP地址查找服务器
     */
    Optional<Server> findByIp(String ip);
    
    /**
     * 检查IP地址是否已存在（排除指定ID）
     */
    boolean existsByIpAndIdNot(String ip, Long id);
    
    /**
     * 检查IP地址是否已存在
     */
    boolean existsByIp(String ip);
    
    /**
     * 根据状态查找服务器
     */
    List<Server> findByStatus(Server.ServerStatus status);
    
    /**
     * 统计在线服务器数量
     */
    long countByStatus(Server.ServerStatus status);
    
    /**
     * 根据类型查找服务器
     */
    List<Server> findByType(String type);
    
    /**
     * 根据位置查找服务器
     */
    List<Server> findByLocation(String location);
    
    /**
     * 根据服务商查找服务器
     */
    List<Server> findByProvider(String provider);
    
    /**
     * 模糊搜索服务器（按名称、IP、类型、位置）
     */
    @Query("SELECT s FROM Server s WHERE " +
           "LOWER(s.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(s.ip) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(s.type) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(s.location) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Server> searchServers(@Param("keyword") String keyword, Pageable pageable);
    
    /**
     * 获取所有服务器（分页）
     */
    Page<Server> findAllByOrderByCreateTimeDesc(Pageable pageable);
    
    /**
     * 根据状态获取服务器（分页）
     */
    Page<Server> findByStatusOrderByCreateTimeDesc(Server.ServerStatus status, Pageable pageable);
    
    /**
     * 根据类型获取服务器（分页）
     */
    Page<Server> findByTypeOrderByCreateTimeDesc(String type, Pageable pageable);
    
    /**
     * 获取服务器统计信息
     */
    @Query("SELECT " +
           "COUNT(s) as total, " +
           "SUM(CASE WHEN s.status = 'ONLINE' THEN 1 ELSE 0 END) as online, " +
           "SUM(CASE WHEN s.status = 'OFFLINE' THEN 1 ELSE 0 END) as offline " +
           "FROM Server s")
    Object[] getServerStats();
}
