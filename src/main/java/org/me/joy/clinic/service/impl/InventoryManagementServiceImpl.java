package org.me.joy.clinic.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.me.joy.clinic.dto.*;
import org.me.joy.clinic.entity.InventoryLevel;
import org.me.joy.clinic.entity.Medicine;
import org.me.joy.clinic.entity.StockTransaction;
import org.me.joy.clinic.exception.BusinessException;
import org.me.joy.clinic.mapper.InventoryLevelMapper;
import org.me.joy.clinic.mapper.MedicineMapper;
import org.me.joy.clinic.mapper.StockTransactionMapper;
import org.me.joy.clinic.service.InventoryManagementService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * 库存管理服务实现类
 */
@Service
@Transactional
public class InventoryManagementServiceImpl implements InventoryManagementService {

    @Autowired
    private MedicineMapper medicineMapper;

    @Autowired
    private StockTransactionMapper stockTransactionMapper;

    @Autowired
    private InventoryLevelMapper inventoryLevelMapper;

    @Override
    public Medicine createMedicine(CreateMedicineRequest request) {
        // 检查药品编码是否已存在
        Medicine existingMedicine = medicineMapper.findByMedicineCode(request.getMedicineCode());
        if (existingMedicine != null) {
            throw new BusinessException("MEDICINE_001", "药品编码已存在: " + request.getMedicineCode());
        }

        // 创建药品实体
        Medicine medicine = new Medicine();
        BeanUtils.copyProperties(request, medicine);
        medicine.setEnabled(true);
        medicine.setCreatedAt(LocalDateTime.now());
        medicine.setUpdatedAt(LocalDateTime.now());

        // 保存药品
        medicineMapper.insert(medicine);
        return medicine;
    }

    @Override
    public Medicine updateMedicine(Long medicineId, UpdateMedicineRequest request) {
        Medicine medicine = getMedicineById(medicineId);
        
        // 更新非空字段
        if (StringUtils.hasText(request.getName())) {
            medicine.setName(request.getName());
        }
        if (StringUtils.hasText(request.getGenericName())) {
            medicine.setGenericName(request.getGenericName());
        }
        if (StringUtils.hasText(request.getBrandName())) {
            medicine.setBrandName(request.getBrandName());
        }
        if (StringUtils.hasText(request.getCategory())) {
            medicine.setCategory(request.getCategory());
        }
        if (StringUtils.hasText(request.getType())) {
            medicine.setType(request.getType());
        }
        if (StringUtils.hasText(request.getDosageForm())) {
            medicine.setDosageForm(request.getDosageForm());
        }
        if (StringUtils.hasText(request.getSpecification())) {
            medicine.setSpecification(request.getSpecification());
        }
        if (StringUtils.hasText(request.getUnit())) {
            medicine.setUnit(request.getUnit());
        }
        if (StringUtils.hasText(request.getManufacturer())) {
            medicine.setManufacturer(request.getManufacturer());
        }
        if (StringUtils.hasText(request.getApprovalNumber())) {
            medicine.setApprovalNumber(request.getApprovalNumber());
        }
        if (StringUtils.hasText(request.getDrugLicenseNumber())) {
            medicine.setDrugLicenseNumber(request.getDrugLicenseNumber());
        }
        if (request.getPurchasePrice() != null) {
            medicine.setPurchasePrice(request.getPurchasePrice());
        }
        if (request.getSellingPrice() != null) {
            medicine.setSellingPrice(request.getSellingPrice());
        }
        if (StringUtils.hasText(request.getStorageConditions())) {
            medicine.setStorageConditions(request.getStorageConditions());
        }
        if (StringUtils.hasText(request.getIndications())) {
            medicine.setIndications(request.getIndications());
        }
        if (StringUtils.hasText(request.getContraindications())) {
            medicine.setContraindications(request.getContraindications());
        }
        if (StringUtils.hasText(request.getAdverseReactions())) {
            medicine.setAdverseReactions(request.getAdverseReactions());
        }
        if (StringUtils.hasText(request.getDosageAndUsage())) {
            medicine.setDosageAndUsage(request.getDosageAndUsage());
        }
        if (StringUtils.hasText(request.getPrecautions())) {
            medicine.setPrecautions(request.getPrecautions());
        }
        if (StringUtils.hasText(request.getDrugInteractions())) {
            medicine.setDrugInteractions(request.getDrugInteractions());
        }
        if (request.getShelfLifeMonths() != null) {
            medicine.setShelfLifeMonths(request.getShelfLifeMonths());
        }
        if (request.getMinStockLevel() != null) {
            medicine.setMinStockLevel(request.getMinStockLevel());
        }
        if (request.getMaxStockLevel() != null) {
            medicine.setMaxStockLevel(request.getMaxStockLevel());
        }
        if (request.getSafetyStockLevel() != null) {
            medicine.setSafetyStockLevel(request.getSafetyStockLevel());
        }
        if (request.getRequiresPrescription() != null) {
            medicine.setRequiresPrescription(request.getRequiresPrescription());
        }
        if (request.getIsControlledSubstance() != null) {
            medicine.setIsControlledSubstance(request.getIsControlledSubstance());
        }
        if (request.getEnabled() != null) {
            medicine.setEnabled(request.getEnabled());
        }
        if (StringUtils.hasText(request.getRemarks())) {
            medicine.setRemarks(request.getRemarks());
        }
        if (request.getSupplierId() != null) {
            medicine.setSupplierId(request.getSupplierId());
        }

        medicine.setUpdatedAt(LocalDateTime.now());
        medicineMapper.updateById(medicine);
        return medicine;
    }

    @Override
    public Medicine getMedicineById(Long medicineId) {
        Medicine medicine = medicineMapper.selectById(medicineId);
        if (medicine == null) {
            throw new BusinessException("MEDICINE_002", "药品不存在，ID: " + medicineId);
        }
        return medicine;
    }

    @Override
    public Medicine getMedicineByCode(String medicineCode) {
        Medicine medicine = medicineMapper.findByMedicineCode(medicineCode);
        if (medicine == null) {
            throw new BusinessException("MEDICINE_003", "药品不存在，编码: " + medicineCode);
        }
        return medicine;
    }

    @Override
    public List<Medicine> searchMedicines(MedicineSearchCriteria criteria) {
        QueryWrapper<Medicine> queryWrapper = new QueryWrapper<>();
        
        // 关键词搜索
        if (StringUtils.hasText(criteria.getKeyword())) {
            queryWrapper.and(wrapper -> wrapper
                .like("name", criteria.getKeyword())
                .or().like("generic_name", criteria.getKeyword())
                .or().like("brand_name", criteria.getKeyword())
            );
        }
        
        // 分类筛选
        if (StringUtils.hasText(criteria.getCategory())) {
            queryWrapper.eq("category", criteria.getCategory());
        }
        
        // 生产厂家筛选
        if (StringUtils.hasText(criteria.getManufacturer())) {
            queryWrapper.like("manufacturer", criteria.getManufacturer());
        }
        
        // 启用状态筛选
        if (criteria.getEnabled() != null) {
            queryWrapper.eq("enabled", criteria.getEnabled());
        }
        
        // 处方药筛选
        if (criteria.getRequiresPrescription() != null) {
            if (criteria.getRequiresPrescription()) {
                queryWrapper.and(wrapper -> wrapper
                    .eq("category", "处方药")
                    .or().eq("requires_prescription", true)
                );
            } else {
                queryWrapper.eq("category", "非处方药")
                    .eq("requires_prescription", false);
            }
        }
        
        // 特殊管制药品筛选
        if (criteria.getIsControlledSubstance() != null) {
            queryWrapper.eq("is_controlled_substance", criteria.getIsControlledSubstance());
        }
        
        // 价格范围筛选
        if (criteria.getMinPrice() != null) {
            queryWrapper.ge("selling_price", criteria.getMinPrice());
        }
        if (criteria.getMaxPrice() != null) {
            queryWrapper.le("selling_price", criteria.getMaxPrice());
        }
        
        // 供应商筛选
        if (criteria.getSupplierId() != null) {
            queryWrapper.eq("supplier_id", criteria.getSupplierId());
        }
        
        queryWrapper.eq("deleted", 0);
        queryWrapper.orderByAsc("name");
        
        List<Medicine> medicines = medicineMapper.selectList(queryWrapper);
        
        // 根据库存状态进一步筛选
        if (criteria.getNeedsRestock() != null && criteria.getNeedsRestock()) {
            medicines = medicines.stream()
                .filter(medicine -> {
                    Integer currentStock = getCurrentStock(medicine.getId());
                    return medicine.needsRestock(currentStock);
                })
                .toList();
        }
        
        if (criteria.getIsOverstocked() != null && criteria.getIsOverstocked()) {
            medicines = medicines.stream()
                .filter(medicine -> {
                    Integer currentStock = getCurrentStock(medicine.getId());
                    return medicine.isOverstocked(currentStock);
                })
                .toList();
        }
        
        return medicines;
    }

    @Override
    public List<Medicine> getAllActiveMedicines() {
        return medicineMapper.findByEnabled(true);
    }

    @Override
    public List<Medicine> getMedicinesByCategory(String category) {
        return medicineMapper.findByCategory(category);
    }

    @Override
    public List<Medicine> getPrescriptionMedicines() {
        return medicineMapper.findPrescriptionMedicines();
    }

    @Override
    public List<Medicine> getOverTheCounterMedicines() {
        return medicineMapper.findOverTheCounterMedicines();
    }

    @Override
    public List<Medicine> getControlledSubstances() {
        return medicineMapper.findControlledSubstances();
    }

    @Override
    public void updateStock(Long medicineId, StockUpdateRequest request) {
        Medicine medicine = getMedicineById(medicineId);
        
        // 生成交易编号
        String transactionNumber = generateTransactionNumber();
        
        // 创建库存交易记录
        StockTransaction transaction = new StockTransaction();
        transaction.setTransactionNumber(transactionNumber);
        transaction.setMedicineId(medicineId);
        transaction.setTransactionType(request.getTransactionType());
        transaction.setQuantity(request.getQuantity());
        transaction.setUnitPrice(request.getUnitPrice());
        transaction.setBatchNumber(request.getBatchNumber());
        transaction.setProductionDate(request.getProductionDate());
        transaction.setExpiryDate(request.getExpiryDate());
        transaction.setSupplierId(request.getSupplierId());
        transaction.setSupplierName(request.getSupplierName());
        transaction.setReason(request.getReason());
        transaction.setRelatedDocumentNumber(request.getRelatedDocumentNumber());
        transaction.setTransactionDate(LocalDateTime.now());
        transaction.setRemarks(request.getRemarks());
        
        // 计算总金额
        transaction.calculateTotalAmount();
        
        // 获取当前库存
        Integer currentStock = getCurrentStock(medicineId);
        transaction.setStockBefore(currentStock != null ? currentStock : 0);
        
        // 计算交易后库存
        int newStock = transaction.getStockBefore() + request.getQuantity();
        if (newStock < 0) {
            throw new BusinessException("STOCK_001", "库存不足，无法执行出库操作");
        }
        transaction.setStockAfter(newStock);
        
        // 保存交易记录
        stockTransactionMapper.insert(transaction);
        
        // 更新库存水平
        updateInventoryLevel(medicineId, request, transaction);
    }

    private void updateInventoryLevel(Long medicineId, StockUpdateRequest request, StockTransaction transaction) {
        String batchNumber = request.getBatchNumber();
        if (!StringUtils.hasText(batchNumber)) {
            batchNumber = "DEFAULT";
        }
        
        // 查找或创建库存水平记录
        InventoryLevel inventoryLevel = inventoryLevelMapper.findByMedicineIdAndBatchNumber(medicineId, batchNumber);
        
        if (inventoryLevel == null) {
            // 创建新的库存水平记录
            inventoryLevel = new InventoryLevel();
            inventoryLevel.setMedicineId(medicineId);
            inventoryLevel.setBatchNumber(batchNumber);
            inventoryLevel.setCurrentStock(0);
            inventoryLevel.setAvailableStock(0);
            inventoryLevel.setReservedStock(0);
            inventoryLevel.setLockedStock(0);
            inventoryLevel.setPurchasePrice(request.getUnitPrice());
            inventoryLevel.setProductionDate(request.getProductionDate());
            inventoryLevel.setExpiryDate(request.getExpiryDate());
            inventoryLevel.setSupplierId(request.getSupplierId());
            inventoryLevel.setSupplierName(request.getSupplierName());
            inventoryLevel.setStorageLocation(request.getStorageLocation());
            inventoryLevel.setStatus("正常");
            inventoryLevel.setCreatedAt(LocalDateTime.now());
            inventoryLevel.setUpdatedAt(LocalDateTime.now());
        }
        
        // 更新库存数量
        if (request.getQuantity() > 0) {
            inventoryLevel.addStock(request.getQuantity());
        } else {
            inventoryLevel.reduceStock(Math.abs(request.getQuantity()));
        }
        
        inventoryLevel.setUpdatedAt(LocalDateTime.now());
        
        // 保存或更新库存水平
        if (inventoryLevel.getId() == null) {
            inventoryLevelMapper.insert(inventoryLevel);
        } else {
            inventoryLevelMapper.updateById(inventoryLevel);
        }
    }

    @Override
    public List<Medicine> getLowStockMedicines() {
        return medicineMapper.findMedicinesNeedingRestock();
    }

    @Override
    public List<Medicine> getOverstockedMedicines() {
        return medicineMapper.findOverstockedMedicines();
    }

    @Override
    public List<Medicine> getExpiringMedicines(int daysThreshold) {
        List<InventoryLevel> expiringLevels = inventoryLevelMapper.findExpiringSoon(daysThreshold);
        return expiringLevels.stream()
            .map(level -> getMedicineById(level.getMedicineId()))
            .distinct()
            .toList();
    }

    @Override
    public List<Medicine> getExpiredMedicines() {
        List<InventoryLevel> expiredLevels = inventoryLevelMapper.findExpiredStock();
        return expiredLevels.stream()
            .map(level -> getMedicineById(level.getMedicineId()))
            .distinct()
            .toList();
    }

    @Override
    public Integer getCurrentStock(Long medicineId) {
        Integer stock = inventoryLevelMapper.getTotalStockByMedicine(medicineId);
        return stock != null ? stock : 0;
    }

    @Override
    public Integer getAvailableStock(Long medicineId) {
        Integer stock = inventoryLevelMapper.getAvailableStockByMedicine(medicineId);
        return stock != null ? stock : 0;
    }

    @Override
    public boolean reserveStock(Long medicineId, Integer quantity) {
        if (quantity == null || quantity <= 0) {
            return false;
        }
        
        // 获取可用库存最多的批次
        List<InventoryLevel> availableLevels = inventoryLevelMapper.findAvailableStockByMedicine(medicineId);
        
        int remainingQuantity = quantity;
        for (InventoryLevel level : availableLevels) {
            if (remainingQuantity <= 0) {
                break;
            }
            
            int reserveQuantity = Math.min(remainingQuantity, level.getAvailableStock());
            if (level.reserveStock(reserveQuantity)) {
                inventoryLevelMapper.updateById(level);
                remainingQuantity -= reserveQuantity;
            }
        }
        
        return remainingQuantity == 0;
    }

    @Override
    public void releaseReservedStock(Long medicineId, Integer quantity) {
        if (quantity == null || quantity <= 0) {
            return;
        }
        
        List<InventoryLevel> levels = inventoryLevelMapper.findByMedicineId(medicineId);
        
        int remainingQuantity = quantity;
        for (InventoryLevel level : levels) {
            if (remainingQuantity <= 0 || level.getReservedStock() <= 0) {
                continue;
            }
            
            int releaseQuantity = Math.min(remainingQuantity, level.getReservedStock());
            level.releaseReservedStock(releaseQuantity);
            inventoryLevelMapper.updateById(level);
            remainingQuantity -= releaseQuantity;
        }
    }

    @Override
    public void enableMedicine(Long medicineId) {
        Medicine medicine = getMedicineById(medicineId);
        medicine.setEnabled(true);
        medicine.setUpdatedAt(LocalDateTime.now());
        medicineMapper.updateById(medicine);
    }

    @Override
    public void disableMedicine(Long medicineId) {
        Medicine medicine = getMedicineById(medicineId);
        medicine.setEnabled(false);
        medicine.setUpdatedAt(LocalDateTime.now());
        medicineMapper.updateById(medicine);
    }

    @Override
    public void deleteMedicine(Long medicineId) {
        Medicine medicine = getMedicineById(medicineId);
        
        // 检查是否有库存
        Integer currentStock = getCurrentStock(medicineId);
        if (currentStock > 0) {
            throw new BusinessException("MEDICINE_004", "药品仍有库存，无法删除");
        }
        
        medicine.setDeleted(true);
        medicine.setUpdatedAt(LocalDateTime.now());
        medicineMapper.updateById(medicine);
    }

    @Override
    public Long countActiveMedicines() {
        return medicineMapper.countActiveMedicines();
    }

    @Override
    public List<Object> countMedicinesByCategory() {
        return medicineMapper.countMedicinesByCategory();
    }

    @Override
    public Double getTotalInventoryValue() {
        Double value = inventoryLevelMapper.getTotalInventoryValue();
        return value != null ? value : 0.0;
    }

    @Override
    public Double getInventoryValueByMedicine(Long medicineId) {
        Double value = inventoryLevelMapper.getInventoryValueByMedicine(medicineId);
        return value != null ? value : 0.0;
    }

    /**
     * 生成交易编号
     */
    private String generateTransactionNumber() {
        return "TXN" + System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}