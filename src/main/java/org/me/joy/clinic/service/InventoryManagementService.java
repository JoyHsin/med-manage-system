package org.me.joy.clinic.service;

import org.me.joy.clinic.dto.*;
import org.me.joy.clinic.entity.Medicine;

import java.util.List;

/**
 * 库存管理服务接口
 */
public interface InventoryManagementService {

    /**
     * 创建药品
     */
    Medicine createMedicine(CreateMedicineRequest request);

    /**
     * 更新药品信息
     */
    Medicine updateMedicine(Long medicineId, UpdateMedicineRequest request);

    /**
     * 根据ID获取药品
     */
    Medicine getMedicineById(Long medicineId);

    /**
     * 根据药品编码获取药品
     */
    Medicine getMedicineByCode(String medicineCode);

    /**
     * 搜索药品
     */
    List<Medicine> searchMedicines(MedicineSearchCriteria criteria);

    /**
     * 获取所有启用的药品
     */
    List<Medicine> getAllActiveMedicines();

    /**
     * 根据分类获取药品
     */
    List<Medicine> getMedicinesByCategory(String category);

    /**
     * 获取处方药列表
     */
    List<Medicine> getPrescriptionMedicines();

    /**
     * 获取非处方药列表
     */
    List<Medicine> getOverTheCounterMedicines();

    /**
     * 获取特殊管制药品列表
     */
    List<Medicine> getControlledSubstances();

    /**
     * 更新库存
     */
    void updateStock(Long medicineId, StockUpdateRequest request);

    /**
     * 获取需要补货的药品列表
     */
    List<Medicine> getLowStockMedicines();

    /**
     * 获取库存过多的药品列表
     */
    List<Medicine> getOverstockedMedicines();

    /**
     * 获取即将过期的药品列表
     */
    List<Medicine> getExpiringMedicines(int daysThreshold);

    /**
     * 获取已过期的药品列表
     */
    List<Medicine> getExpiredMedicines();

    /**
     * 获取药品的当前库存总量
     */
    Integer getCurrentStock(Long medicineId);

    /**
     * 获取药品的可用库存总量
     */
    Integer getAvailableStock(Long medicineId);

    /**
     * 预留库存
     */
    boolean reserveStock(Long medicineId, Integer quantity);

    /**
     * 释放预留库存
     */
    void releaseReservedStock(Long medicineId, Integer quantity);

    /**
     * 启用药品
     */
    void enableMedicine(Long medicineId);

    /**
     * 禁用药品
     */
    void disableMedicine(Long medicineId);

    /**
     * 删除药品（软删除）
     */
    void deleteMedicine(Long medicineId);

    /**
     * 统计药品总数
     */
    Long countActiveMedicines();

    /**
     * 统计各分类药品数量
     */
    List<Object> countMedicinesByCategory();

    /**
     * 获取库存总价值
     */
    Double getTotalInventoryValue();

    /**
     * 获取指定药品的库存价值
     */
    Double getInventoryValueByMedicine(Long medicineId);
}