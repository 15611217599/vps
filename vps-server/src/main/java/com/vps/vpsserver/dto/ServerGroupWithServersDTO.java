package com.vps.vpsserver.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import com.vps.vpsserver.entity.ServerGroup;

/**
 * 带服务器列表的服务器分组DTO
 */
public class ServerGroupWithServersDTO {
    
    private Long id;
    private String name;
    private String description;
    private Integer sortOrder;
    private Boolean isActive;
    private String region;
    private String country;
    private String city;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private Long categoryId;
    private String categoryName;
    private List<ServerResponseDTO> servers;
    
    // 构造函数
    public ServerGroupWithServersDTO() {}
    
    // 从实体转换为DTO
    public static ServerGroupWithServersDTO fromEntity(ServerGroup entity) {
        ServerGroupWithServersDTO dto = new ServerGroupWithServersDTO();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setDescription(entity.getDescription());
        dto.setSortOrder(entity.getSortOrder());
        dto.setIsActive(entity.getIsActive());
        dto.setRegion(entity.getRegion());
        dto.setCountry(entity.getCountry());
        dto.setCity(entity.getCity());
        dto.setCreateTime(entity.getCreateTime());
        dto.setUpdateTime(entity.getUpdateTime());
        
        if (entity.getCategory() != null) {
            dto.setCategoryId(entity.getCategory().getId());
            dto.setCategoryName(entity.getCategory().getName());
        }
        
        // 转换服务器列表
        if (entity.getServers() != null) {
            dto.setServers(entity.getServers().stream()
                    .map(ServerResponseDTO::fromEntity)
                    .collect(Collectors.toList()));
        }
        
        return dto;
    }
    
    // Getter和Setter方法
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public Integer getSortOrder() {
        return sortOrder;
    }
    
    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }
    
    public Boolean getIsActive() {
        return isActive;
    }
    
    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }
    
    public String getRegion() {
        return region;
    }
    
    public void setRegion(String region) {
        this.region = region;
    }
    
    public String getCountry() {
        return country;
    }
    
    public void setCountry(String country) {
        this.country = country;
    }
    
    public String getCity() {
        return city;
    }
    
    public void setCity(String city) {
        this.city = city;
    }
    
    public LocalDateTime getCreateTime() {
        return createTime;
    }
    
    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }
    
    public LocalDateTime getUpdateTime() {
        return updateTime;
    }
    
    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }
    
    public Long getCategoryId() {
        return categoryId;
    }
    
    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }
    
    public String getCategoryName() {
        return categoryName;
    }
    
    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }
    
    public List<ServerResponseDTO> getServers() {
        return servers;
    }
    
    public void setServers(List<ServerResponseDTO> servers) {
        this.servers = servers;
    }
}