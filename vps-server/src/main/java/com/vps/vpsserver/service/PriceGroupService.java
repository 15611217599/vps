package com.vps.vpsserver.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.vps.vpsserver.dto.PriceGroupDTO;

/**
 * 价格组服务接口
 */
public interface PriceGroupService {
    
    /**
     * 创建价格组
     */
    PriceGroupDTO createPriceGroup(PriceGroupDTO priceGroupDTO);
    
    /**
     * 更新价格组
     */
    PriceGroupDTO updatePriceGroup(Long id, PriceGroupDTO priceGroupDTO);
    
    /**
     * 删除价格组
     */
    void deletePriceGroup(Long id);
    
    /**
     * 根据ID获取价格组
     */
    PriceGroupDTO getPriceGroupById(Long id);
    
    /**
     * 获取所有激活的价格组
     */
    List<PriceGroupDTO> getAllActivePriceGroups();
    
    /**
     * 分页获取价格组
     */
    Page<PriceGroupDTO> getPriceGroups(Pageable pageable);
    
    /**
     * 搜索价格组
     */
    Page<PriceGroupDTO> searchPriceGroups(String keyword, Pageable pageable);
    
    /**
     * 检查价格组名称是否存在
     */
    boolean existsByName(String name);
    
    /**
     * 检查价格组名称是否存在（排除指定ID）
     */
    boolean existsByNameAndIdNot(String name, Long id);
    
    /**
     * 根据激活状态分页获取价格组
     */
    Page<PriceGroupDTO> getPriceGroupsByActive(Boolean isActive, Pageable pageable);
    
    /**
     * 应用折扣
     */
    PriceGroupDTO applyDiscount(Long id, Double discountPercentage, 
                               String startTime, String endTime);
    
    /**
     * 恢复原价
     */
    PriceGroupDTO restoreOriginalPrices(Long id);
}
