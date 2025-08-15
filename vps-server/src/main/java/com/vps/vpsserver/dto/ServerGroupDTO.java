package com.vps.vpsserver.dto;

import java.time.LocalDateTime;
import com.vps.vpsserver.entity.ServerGroup;
import jakarta.validation.constraints.NotNull;

/**
 * 服务器分组DTO
 */
public class ServerGroupDTO {
    
    private Long id;
    
    @NotNull(message = "名称不能为空")
    private String name;
    
    private String description;
    private String region;
    private String country;
    private String city;
    
    private Integer sortOrder;
    private Boolean isActive;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    
    @NotNull(message = "类别ID不能为空")
    private Long categoryId;
    private String categoryName;
    private Integer serverCount; // 服务器数量
    
    // 构造函数
    public ServerGroupDTO() {}
    
    public ServerGroupDTO(String name, String description, Long categoryId) {
        this.name = name;
        this.description = description;
        this.categoryId = categoryId;
    }
    
    // 从实体转换为DTO
    public static ServerGroupDTO fromEntity(ServerGroup entity) {
        ServerGroupDTO dto = new ServerGroupDTO();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setDescription(entity.getDescription());
        dto.setRegion(entity.getRegion());
        dto.setCountry(entity.getCountry());
        dto.setCity(entity.getCity());
        dto.setSortOrder(entity.getSortOrder());
        dto.setIsActive(entity.getIsActive());
        dto.setCreateTime(entity.getCreateTime());
        dto.setUpdateTime(entity.getUpdateTime());
        
        if (entity.getCategory() != null) {
            dto.setCategoryId(entity.getCategory().getId());
            dto.setCategoryName(entity.getCategory().getName());
        }
        
        // 计算服务器数量
        if (entity.getServers() != null) {
            dto.setServerCount(entity.getServers().size());
        } else {
            dto.setServerCount(0);
        }
        
        return dto;
    }
    
    // 转换为实体（不包含关联关系）
    public ServerGroup toEntity() {
        ServerGroup entity = new ServerGroup();
        entity.setId(this.id);
        entity.setName(this.name);
        entity.setDescription(this.description);
        entity.setRegion(this.region);
        entity.setCountry(this.country);
        entity.setCity(this.city);
        entity.setSortOrder(this.sortOrder);
        entity.setIsActive(this.isActive);
        
        return entity;
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
    
    public Integer getServerCount() {
        return serverCount;
    }
    
    public void setServerCount(Integer serverCount) {
        this.serverCount = serverCount;
    }
}
