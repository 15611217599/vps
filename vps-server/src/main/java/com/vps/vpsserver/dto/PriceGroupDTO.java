package com.vps.vpsserver.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.vps.vpsserver.entity.PriceGroup;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.DecimalMin;

/**
 * 价格组DTO
 */
public class PriceGroupDTO {
    
    private Long id;
    
    @NotNull(message = "名称不能为空")
    private String name;
    
    private String description;
    
    @NotNull(message = "小时价格不能为空")
    @DecimalMin(value = "0.0", inclusive = false, message = "小时价格必须大于0")
    private BigDecimal hourlyPrice;
    
    @NotNull(message = "日价格不能为空")
    @DecimalMin(value = "0.0", inclusive = false, message = "日价格必须大于0")
    private BigDecimal dailyPrice;
    
    @NotNull(message = "月价格不能为空")
    @DecimalMin(value = "0.0", inclusive = false, message = "月价格必须大于0")
    private BigDecimal monthlyPrice;
    
    @NotNull(message = "季度价格不能为空")
    @DecimalMin(value = "0.0", inclusive = false, message = "季度价格必须大于0")
    private BigDecimal quarterlyPrice;
    
    @NotNull(message = "半年价格不能为空")
    @DecimalMin(value = "0.0", inclusive = false, message = "半年价格必须大于0")
    private BigDecimal semiAnnualPrice;
    
    @NotNull(message = "年价格不能为空")
    @DecimalMin(value = "0.0", inclusive = false, message = "年价格必须大于0")
    private BigDecimal annualPrice;
    
    private Boolean isActive;
    private Integer sortOrder;
    private Long serverGroupId;
    private String salesPageHtml;
    private LocalDateTime createTime;
    private LocalDateTime lastUpdate;
    
    // 构造函数
    public PriceGroupDTO() {}
    
    public PriceGroupDTO(String name, BigDecimal hourlyPrice, BigDecimal dailyPrice, 
                        BigDecimal monthlyPrice, BigDecimal quarterlyPrice, 
                        BigDecimal semiAnnualPrice, BigDecimal annualPrice) {
        this.name = name;
        this.hourlyPrice = hourlyPrice;
        this.dailyPrice = dailyPrice;
        this.monthlyPrice = monthlyPrice;
        this.quarterlyPrice = quarterlyPrice;
        this.semiAnnualPrice = semiAnnualPrice;
        this.annualPrice = annualPrice;
    }
    
    // 从实体转换为DTO
    public static PriceGroupDTO fromEntity(PriceGroup entity) {
        PriceGroupDTO dto = new PriceGroupDTO();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setDescription(entity.getDescription());
        dto.setHourlyPrice(entity.getHourlyPrice());
        dto.setDailyPrice(entity.getDailyPrice());
        dto.setMonthlyPrice(entity.getMonthlyPrice());
        dto.setQuarterlyPrice(entity.getQuarterlyPrice());
        dto.setSemiAnnualPrice(entity.getSemiAnnualPrice());
        dto.setAnnualPrice(entity.getAnnualPrice());
        dto.setIsActive(entity.getIsActive());
        dto.setSortOrder(entity.getSortOrder());
        dto.setServerGroupId(entity.getServerGroupId());
        dto.setSalesPageHtml(entity.getSalesPageHtml());
        dto.setCreateTime(entity.getCreateTime());
        dto.setLastUpdate(entity.getLastUpdate());
        
        return dto;
    }
    
    // 转换为实体
    public PriceGroup toEntity() {
        PriceGroup entity = new PriceGroup();
        entity.setId(this.id);
        entity.setName(this.name);
        entity.setDescription(this.description);
        entity.setHourlyPrice(this.hourlyPrice);
        entity.setDailyPrice(this.dailyPrice);
        entity.setMonthlyPrice(this.monthlyPrice);
        entity.setQuarterlyPrice(this.quarterlyPrice);
        entity.setSemiAnnualPrice(this.semiAnnualPrice);
        entity.setAnnualPrice(this.annualPrice);
        entity.setIsActive(this.isActive);
        entity.setSortOrder(this.sortOrder);
        entity.setServerGroupId(this.serverGroupId);
        entity.setSalesPageHtml(this.salesPageHtml);
        
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
    
    public BigDecimal getHourlyPrice() {
        return hourlyPrice;
    }
    
    public void setHourlyPrice(BigDecimal hourlyPrice) {
        this.hourlyPrice = hourlyPrice;
    }
    
    public BigDecimal getDailyPrice() {
        return dailyPrice;
    }
    
    public void setDailyPrice(BigDecimal dailyPrice) {
        this.dailyPrice = dailyPrice;
    }
    
    public BigDecimal getMonthlyPrice() {
        return monthlyPrice;
    }
    
    public void setMonthlyPrice(BigDecimal monthlyPrice) {
        this.monthlyPrice = monthlyPrice;
    }
    
    public BigDecimal getQuarterlyPrice() {
        return quarterlyPrice;
    }
    
    public void setQuarterlyPrice(BigDecimal quarterlyPrice) {
        this.quarterlyPrice = quarterlyPrice;
    }
    
    public BigDecimal getSemiAnnualPrice() {
        return semiAnnualPrice;
    }
    
    public void setSemiAnnualPrice(BigDecimal semiAnnualPrice) {
        this.semiAnnualPrice = semiAnnualPrice;
    }
    
    public BigDecimal getAnnualPrice() {
        return annualPrice;
    }
    
    public void setAnnualPrice(BigDecimal annualPrice) {
        this.annualPrice = annualPrice;
    }
    
    public Boolean getIsActive() {
        return isActive;
    }
    
    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }
    
    public Integer getSortOrder() {
        return sortOrder;
    }
    
    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }
    
    public LocalDateTime getCreateTime() {
        return createTime;
    }
    
    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }
    
    public LocalDateTime getLastUpdate() {
        return lastUpdate;
    }
    
    public void setLastUpdate(LocalDateTime lastUpdate) {
        this.lastUpdate = lastUpdate;
    }
    
    public Long getServerGroupId() {
        return serverGroupId;
    }
    
    public void setServerGroupId(Long serverGroupId) {
        this.serverGroupId = serverGroupId;
    }
    
    public String getSalesPageHtml() {
        return salesPageHtml;
    }
    
    public void setSalesPageHtml(String salesPageHtml) {
        this.salesPageHtml = salesPageHtml;
    }
}
