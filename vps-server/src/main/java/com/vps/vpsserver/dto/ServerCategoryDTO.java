package com.vps.vpsserver.dto;

import java.time.LocalDateTime;

import com.vps.vpsserver.entity.ServerCategory;

import jakarta.validation.constraints.NotNull;

/**
 * 服务器类别DTO
 */
public class ServerCategoryDTO {
    
    private Long id;
    
    @NotNull(message = "名称不能为空")
    private String name;
    
    private String description;
    
    private Integer sortOrder;
    private Boolean isActive;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private Integer groupCount; // 分组数量
    private Integer serverCount; // 服务器总数量
    
    // 构造函数
    public ServerCategoryDTO() {}
    
    public ServerCategoryDTO(String name, String description) {
        this.name = name;
        this.description = description;
    }
    
    // 从实体转换为DTO
    public static ServerCategoryDTO fromEntity(ServerCategory entity) {
        ServerCategoryDTO dto = new ServerCategoryDTO();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setDescription(entity.getDescription());
        dto.setSortOrder(entity.getSortOrder());
        dto.setIsActive(entity.getIsActive());
        dto.setCreateTime(entity.getCreateTime());
        dto.setUpdateTime(entity.getUpdateTime());
        
        // 计算分组和服务器数量
        if (entity.getGroups() != null) {
            dto.setGroupCount(entity.getGroups().size());
            dto.setServerCount(entity.getGroups().stream()
                    .mapToInt(group -> group.getServers() != null ? group.getServers().size() : 0)
                    .sum());
        } else {
            dto.setGroupCount(0);
            dto.setServerCount(0);
        }
        
        return dto;
    }
    
    // 转换为实体
    public ServerCategory toEntity() {
        ServerCategory entity = new ServerCategory();
        entity.setId(this.id);
        entity.setName(this.name);
        entity.setDescription(this.description);
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
    
    public Integer getGroupCount() {
        return groupCount;
    }
    
    public void setGroupCount(Integer groupCount) {
        this.groupCount = groupCount;
    }
    
    public Integer getServerCount() {
        return serverCount;
    }
    
    public void setServerCount(Integer serverCount) {
        this.serverCount = serverCount;
    }
}
