package org.me.joy.clinic.service;

import org.me.joy.clinic.dto.*;
import org.me.joy.clinic.entity.StockTransaction;

import java.time.LocalDate;
import java.util.List;

/**
 * 库存管理服务接口
 */
public interface StockManagementService {

    /**
     * 药品入库
     */
    void recordStockIn(StockInRequest request);

    /**
     * 药品出库
     */
    void recordStockOut(StockOutRequest request);

    /**
     * 获取药品当前库存水平
     */
    StockLevel getCurrentStockLevel(Long medicineId);

    /**
     * 获取药品指定批次的库存水平
     */
    StockLevel getCurrentStockLevel(Long medicineId, String batchNumber);

    /**
     * 获取所有药品的库存水平
     */
    List<StockLevel> getAllStockLevels();

    /**
     * 获取库存不足的药品列表
     */
    List<StockLevel> getLowStockItems();

    /**
     * 获取库存过多的药品列表
     */
    List<StockLevel> getOverstockedItems();

    /**
     * 获取即将过期的药品列表
     */
    List<StockLevel> getExpiringSoonItems(int daysThreshold);

    /**
     * 获取已过期的药品列表
     */
    List<StockLevel> getExpiredItems();

    /**
     * 获取药品的库存交易记录
     */
    List<StockTransaction> getStockTransactions(Long medicineId);

    /**
     * 获取指定日期范围内的库存交易记录
     */
    List<StockTransaction> getStockTransactions(Long medicineId, LocalDate startDate, LocalDate endDate);

    /**
     * 获取所有库存交易记录
     */
    List<StockTransaction> getAllStockTransactions(LocalDate startDate, LocalDate endDate);

    /**
     * 执行库存盘点
     */
    void performStockTaking(StockTakingRequest request);

    /**
     * 获取库存盘点记录
     */
    List<StockTransaction> getStockTakingRecords(LocalDate startDate, LocalDate endDate);

    /**
     * 预留库存
     */
    boolean reserveStock(Long medicineId, String batchNumber, Integer quantity);

    /**
     * 释放预留库存
     */
    void releaseReservedStock(Long medicineId, String batchNumber, Integer quantity);

    /**
     * 锁定库存
     */
    boolean lockStock(Long medicineId, String batchNumber, Integer quantity);

    /**
     * 解锁库存
     */
    void unlockStock(Long medicineId, String batchNumber, Integer quantity);

    /**
     * 调拨库存
     */
    void transferStock(Long medicineId, String fromBatch, String toBatch, Integer quantity, String reason);

    /**
     * 报损处理
     */
    void recordStockLoss(Long medicineId, String batchNumber, Integer quantity, String reason);

    /**
     * 过期药品处理
     */
    void handleExpiredMedicines();

    /**
     * 自动补货建议
     */
    List<StockLevel> getRestockSuggestions();

    /**
     * 获取库存总价值
     */
    Double getTotalInventoryValue();

    /**
     * 获取指定药品的库存价值
     */
    Double getInventoryValueByMedicine(Long medicineId);

    /**
     * 获取库存周转率
     */
    Double getInventoryTurnoverRate(Long medicineId, LocalDate startDate, LocalDate endDate);

    /**
     * 获取库存预警信息
     */
    List<String> getInventoryAlerts();

    /**
     * 生成库存报表
     */
    Object generateInventoryReport(LocalDate startDate, LocalDate endDate, String reportType);

    /**
     * 验证库存交易
     */
    boolean validateStockTransaction(Long medicineId, String batchNumber, Integer quantity, String transactionType);

    /**
     * 获取药品的先进先出批次
     */
    String getFirstInFirstOutBatch(Long medicineId, Integer requiredQuantity);

    /**
     * 批量更新库存状态
     */
    void batchUpdateStockStatus();

    /**
     * 同步库存数据
     */
    void synchronizeInventoryData();
}