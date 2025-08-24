package com.vps.vpsserver.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.vps.vpsserver.dto.PriceGroupDTO;
import com.vps.vpsserver.entity.PriceGroup;
import com.vps.vpsserver.repository.PriceGroupRepository;

/**
 * 价格组服务实现类
 */
@Service
@Transactional
public class PriceGroupServiceImpl implements PriceGroupService {
    
    @Autowired
    private PriceGroupRepository priceGroupRepository;
    
    @Override
    public PriceGroupDTO createPriceGroup(PriceGroupDTO priceGroupDTO) {
        // 检查名称是否已存在
        String nameToCheck = priceGroupDTO.getName();
        if (nameToCheck != null && priceGroupRepository.existsByName(nameToCheck)) {
            throw new RuntimeException("价格组名称已存在: " + nameToCheck);
        }
        
        PriceGroup priceGroup = priceGroupDTO.toEntity();
        
        // 设置排序号
        if (priceGroup.getSortOrder() == null) {
            Integer maxSortOrder = priceGroupRepository.findMaxSortOrder();
            priceGroup.setSortOrder(maxSortOrder != null ? maxSortOrder + 1 : 1);
        }
        
        // 设置默认激活状态
        if (priceGroup.getIsActive() == null) {
            priceGroup.setIsActive(true);
        }
        
        PriceGroup savedPriceGroup = priceGroupRepository.save(priceGroup);
        return PriceGroupDTO.fromEntity(savedPriceGroup);
    }
    
    @Override
    public PriceGroupDTO updatePriceGroup(Long id, PriceGroupDTO priceGroupDTO) {
        PriceGroup existingPriceGroup = priceGroupRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("未找到价格组: " + id));
        
        // 检查名称是否已被其他价格组使用
        String nameToCheck = priceGroupDTO.getName();
        if (nameToCheck != null && priceGroupRepository.existsByNameAndIdNot(nameToCheck, id)) {
            throw new RuntimeException("价格组名称已存在: " + nameToCheck);
        }
        
        // 更新字段
        if (priceGroupDTO.getName() != null) {
            existingPriceGroup.setName(priceGroupDTO.getName());
        }
        if (priceGroupDTO.getDescription() != null) {
            existingPriceGroup.setDescription(priceGroupDTO.getDescription());
        }
        if (priceGroupDTO.getHourlyPrice() != null) {
            existingPriceGroup.setHourlyPrice(priceGroupDTO.getHourlyPrice());
        }
        if (priceGroupDTO.getDailyPrice() != null) {
            existingPriceGroup.setDailyPrice(priceGroupDTO.getDailyPrice());
        }
        if (priceGroupDTO.getMonthlyPrice() != null) {
            existingPriceGroup.setMonthlyPrice(priceGroupDTO.getMonthlyPrice());
        }
        if (priceGroupDTO.getQuarterlyPrice() != null) {
            existingPriceGroup.setQuarterlyPrice(priceGroupDTO.getQuarterlyPrice());
        }
        if (priceGroupDTO.getSemiAnnualPrice() != null) {
            existingPriceGroup.setSemiAnnualPrice(priceGroupDTO.getSemiAnnualPrice());
        }
        if (priceGroupDTO.getAnnualPrice() != null) {
            existingPriceGroup.setAnnualPrice(priceGroupDTO.getAnnualPrice());
        }
        if (priceGroupDTO.getSortOrder() != null) {
            existingPriceGroup.setSortOrder(priceGroupDTO.getSortOrder());
        }
        if (priceGroupDTO.getIsActive() != null) {
            existingPriceGroup.setIsActive(priceGroupDTO.getIsActive());
        }
        if (priceGroupDTO.getServerGroupId() != null) {
            existingPriceGroup.setServerGroupId(priceGroupDTO.getServerGroupId());
        }
        if (priceGroupDTO.getSalesPageHtml() != null) {
            existingPriceGroup.setSalesPageHtml(priceGroupDTO.getSalesPageHtml());
        }
        
        PriceGroup savedPriceGroup = priceGroupRepository.save(existingPriceGroup);
        return PriceGroupDTO.fromEntity(savedPriceGroup);
    }
    
    @Override
    public void deletePriceGroup(Long id) {
        PriceGroup priceGroup = priceGroupRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("未找到价格组: " + id));
        
        priceGroupRepository.delete(priceGroup);
    }
    
    @Override
    @Transactional(readOnly = true)
    public PriceGroupDTO getPriceGroupById(Long id) {
        PriceGroup priceGroup = priceGroupRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("未找到价格组: " + id));
        
        return PriceGroupDTO.fromEntity(priceGroup);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<PriceGroupDTO> getAllActivePriceGroups() {
        List<PriceGroup> priceGroups = priceGroupRepository.findByIsActiveOrderBySortOrderAsc(true);
        return priceGroups.stream()
                .map(PriceGroupDTO::fromEntity)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<PriceGroupDTO> getPriceGroups(Pageable pageable) {
        Page<PriceGroup> priceGroups = priceGroupRepository.findAllByOrderBySortOrderAsc(pageable);
        return priceGroups.map(PriceGroupDTO::fromEntity);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<PriceGroupDTO> searchPriceGroups(String keyword, Pageable pageable) {
        Page<PriceGroup> priceGroups = priceGroupRepository.searchPriceGroups(keyword, pageable);
        return priceGroups.map(PriceGroupDTO::fromEntity);
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean existsByName(String name) {
        return priceGroupRepository.existsByName(name);
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean existsByNameAndIdNot(String name, Long id) {
        return priceGroupRepository.existsByNameAndIdNot(name, id);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<PriceGroupDTO> getPriceGroupsByActive(Boolean isActive, Pageable pageable) {
        Page<PriceGroup> priceGroups = priceGroupRepository.findByIsActiveOrderBySortOrderAsc(isActive, pageable);
        return priceGroups.map(PriceGroupDTO::fromEntity);
    }
}
