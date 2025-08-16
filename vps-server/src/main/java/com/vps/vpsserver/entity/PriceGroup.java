package com.vps.vpsserver.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.DecimalMin;

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

    @Column(name = "sales_page_html", columnDefinition = "TEXT")
    private String salesPageHtml;

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
