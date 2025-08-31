package com.vps.vpsserver.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "price_groups")
public class PriceGroup {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "价格组名称不能为空")
    @Column(nullable = false, length = 100)
    private String name;

    @Column(length = 500)
    private String description;

    @NotNull(message = "小时价格不能为空")
    @DecimalMin(value = "0.0", inclusive = false, message = "小时价格必须大于0")
    @Column(name = "hourly_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal hourlyPrice;

    @NotNull(message = "日价格不能为空")
    @DecimalMin(value = "0.0", inclusive = false, message = "日价格必须大于0")
    @Column(name = "daily_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal dailyPrice;

    @NotNull(message = "月价格不能为空")
    @DecimalMin(value = "0.0", inclusive = false, message = "月价格必须大于0")
    @Column(name = "monthly_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal monthlyPrice;

    @NotNull(message = "季度价格不能为空")
    @DecimalMin(value = "0.0", inclusive = false, message = "季度价格必须大于0")
    @Column(name = "quarterly_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal quarterlyPrice;

    @NotNull(message = "半年价格不能为空")
    @DecimalMin(value = "0.0", inclusive = false, message = "半年价格必须大于0")
    @Column(name = "semi_annual_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal semiAnnualPrice;

    @NotNull(message = "年价格不能为空")
    @DecimalMin(value = "0.0", inclusive = false, message = "年价格必须大于0")
    @Column(name = "annual_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal annualPrice;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @Column(name = "sort_order")
    private Integer sortOrder = 0;

    @Column(name = "server_group_id")
    private Long serverGroupId;

    // 多对一关系：多个价格组属于一个服务器分组
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "server_group_id", insertable = false, updatable = false)
    private ServerGroup serverGroup;

    @Column(name = "sales_page_html", columnDefinition = "TEXT")
    private String salesPageHtml;

    // 折扣相关字段
    @Column(name = "has_discount", nullable = false)
    private Boolean hasDiscount = false;

    @Column(name = "discount_percentage", precision = 5, scale = 2)
    private BigDecimal discountPercentage;

    @Column(name = "discount_start_time")
    private LocalDateTime discountStartTime;

    @Column(name = "discount_end_time")
    private LocalDateTime discountEndTime;



    @CreationTimestamp
    @Column(name = "create_time", nullable = false, updatable = false)
    private LocalDateTime createTime;

    @UpdateTimestamp
    @Column(name = "last_update")
    private LocalDateTime lastUpdate;

    // 构造函数
    public PriceGroup() {}

    public PriceGroup(String name, BigDecimal hourlyPrice, BigDecimal dailyPrice, 
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

    // Getters and Setters
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

    public ServerGroup getServerGroup() {
        return serverGroup;
    }

    public void setServerGroup(ServerGroup serverGroup) {
        this.serverGroup = serverGroup;
    }

    // 折扣相关的getter和setter
    public Boolean getHasDiscount() {
        return hasDiscount;
    }

    public void setHasDiscount(Boolean hasDiscount) {
        this.hasDiscount = hasDiscount;
    }

    public BigDecimal getDiscountPercentage() {
        return discountPercentage;
    }

    public void setDiscountPercentage(BigDecimal discountPercentage) {
        this.discountPercentage = discountPercentage;
    }

    public LocalDateTime getDiscountStartTime() {
        return discountStartTime;
    }

    public void setDiscountStartTime(LocalDateTime discountStartTime) {
        this.discountStartTime = discountStartTime;
    }

    public LocalDateTime getDiscountEndTime() {
        return discountEndTime;
    }

    public void setDiscountEndTime(LocalDateTime discountEndTime) {
        this.discountEndTime = discountEndTime;
    }



    // 便利方法：应用折扣
    public void applyDiscount(BigDecimal discountPercentage, LocalDateTime startTime, LocalDateTime endTime) {
        // 如果已经有折扣，先恢复原价再应用新折扣
        if (this.hasDiscount) {
            restoreOriginalPrices();
        }

        // 设置折扣信息
        this.hasDiscount = true;
        this.discountPercentage = discountPercentage;
        this.discountStartTime = startTime;
        this.discountEndTime = endTime;

        // 计算折扣价格
        this.hourlyPrice = calculateDiscountedPrice(this.hourlyPrice, discountPercentage);
        this.dailyPrice = calculateDiscountedPrice(this.dailyPrice, discountPercentage);
        this.monthlyPrice = calculateDiscountedPrice(this.monthlyPrice, discountPercentage);
        this.quarterlyPrice = calculateDiscountedPrice(this.quarterlyPrice, discountPercentage);
        this.semiAnnualPrice = calculateDiscountedPrice(this.semiAnnualPrice, discountPercentage);
        this.annualPrice = calculateDiscountedPrice(this.annualPrice, discountPercentage);
    }

    // 便利方法：恢复原价
    public void restoreOriginalPrices() {
        if (this.hasDiscount && this.discountPercentage != null) {
            // 通过折扣比例反推原价
            this.hourlyPrice = calculateOriginalPrice(this.hourlyPrice, this.discountPercentage);
            this.dailyPrice = calculateOriginalPrice(this.dailyPrice, this.discountPercentage);
            this.monthlyPrice = calculateOriginalPrice(this.monthlyPrice, this.discountPercentage);
            this.quarterlyPrice = calculateOriginalPrice(this.quarterlyPrice, this.discountPercentage);
            this.semiAnnualPrice = calculateOriginalPrice(this.semiAnnualPrice, this.discountPercentage);
            this.annualPrice = calculateOriginalPrice(this.annualPrice, this.discountPercentage);

            // 清除折扣信息
            this.hasDiscount = false;
            this.discountPercentage = null;
            this.discountStartTime = null;
            this.discountEndTime = null;
        }
    }

    // 计算折扣价格的私有方法
    private BigDecimal calculateDiscountedPrice(BigDecimal originalPrice, BigDecimal discountPercentage) {
        if (originalPrice == null || discountPercentage == null) {
            return originalPrice;
        }

        // 百分比折扣
        BigDecimal discountAmount = originalPrice.multiply(discountPercentage).divide(BigDecimal.valueOf(100));
        return originalPrice.subtract(discountAmount);
    }

    // 通过折扣价格反推原价的私有方法
    private BigDecimal calculateOriginalPrice(BigDecimal discountedPrice, BigDecimal discountPercentage) {
        if (discountedPrice == null || discountPercentage == null) {
            return discountedPrice;
        }

        // 原价 = 折扣价 / (1 - 折扣比例/100)
        BigDecimal discountRate = discountPercentage.divide(BigDecimal.valueOf(100));
        BigDecimal multiplier = BigDecimal.ONE.subtract(discountRate);
        return discountedPrice.divide(multiplier, 2, BigDecimal.ROUND_HALF_UP);
    }

    // 检查折扣是否有效（在时间范围内）
    public boolean isDiscountActive() {
        if (!hasDiscount || discountPercentage == null) {
            return false;
        }

        LocalDateTime now = LocalDateTime.now();
        
        if (discountStartTime != null && now.isBefore(discountStartTime)) {
            return false;
        }
        
        if (discountEndTime != null && now.isAfter(discountEndTime)) {
            return false;
        }
        
        return true;
    }

    // 获取原价的便利方法
    public BigDecimal getOriginalHourlyPrice() {
        return hasDiscount ? calculateOriginalPrice(hourlyPrice, discountPercentage) : hourlyPrice;
    }

    public BigDecimal getOriginalDailyPrice() {
        return hasDiscount ? calculateOriginalPrice(dailyPrice, discountPercentage) : dailyPrice;
    }

    public BigDecimal getOriginalMonthlyPrice() {
        return hasDiscount ? calculateOriginalPrice(monthlyPrice, discountPercentage) : monthlyPrice;
    }

    public BigDecimal getOriginalQuarterlyPrice() {
        return hasDiscount ? calculateOriginalPrice(quarterlyPrice, discountPercentage) : quarterlyPrice;
    }

    public BigDecimal getOriginalSemiAnnualPrice() {
        return hasDiscount ? calculateOriginalPrice(semiAnnualPrice, discountPercentage) : semiAnnualPrice;
    }

    public BigDecimal getOriginalAnnualPrice() {
        return hasDiscount ? calculateOriginalPrice(annualPrice, discountPercentage) : annualPrice;
    }

    @Override
    public String toString() {
        return "PriceGroup{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", hourlyPrice=" + hourlyPrice +
                ", dailyPrice=" + dailyPrice +
                ", monthlyPrice=" + monthlyPrice +
                ", quarterlyPrice=" + quarterlyPrice +
                ", semiAnnualPrice=" + semiAnnualPrice +
                ", annualPrice=" + annualPrice +
                ", isActive=" + isActive +
                ", sortOrder=" + sortOrder +
                ", serverGroupId=" + serverGroupId +
                ", salesPageHtml='" + salesPageHtml + '\'' +
                ", createTime=" + createTime +
                ", lastUpdate=" + lastUpdate +
                '}';
    }
}
