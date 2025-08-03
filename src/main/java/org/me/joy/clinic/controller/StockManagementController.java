package org.me.joy.clinic.controller;

import org.me.joy.clinic.dto.*;
import org.me.joy.clinic.entity.StockTransaction;
import org.me.joy.clinic.security.RequiresPermission;
import org.me.joy.clinic.service.StockManagementService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 库存管理控制器
 */
@RestController
@RequestMapping("/stock-management")
@Validated
public class StockManagementController {

    private static final Logger logger = LoggerFactory.getLogger(StockManagementController.class);

    @Autowired
    private StockManagementService stockManagementService;

    /**
     * 药品入库
     */
    @PostMapping("/stock-in")
    @RequiresPermission("PHARMACY_MANAGEMENT")
    public ResponseEntity<Map<String, Object>> recordStockIn(@Valid @RequestBody StockInRequest request) {
        logger.info("处理药品入库请求: {}", request);
        
        stockManagementService.recordStockIn(request);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "药品入库成功");
        
        return ResponseEntity.ok(response);
    }

    /**
     * 药品出库
     */
    @PostMapping("/stock-out")
    @RequiresPermission("PHARMACY_MANAGEMENT")
    public ResponseEntity<Map<String, Object>> recordStockOut(@Valid @RequestBody StockOutRequest request) {
        logger.info("处理药品出库请求: {}", request);
        
        stockManagementService.recordStockOut(request);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "药品出库成功");
        
        return ResponseEntity.ok(response);
    }

    /**
     * 获取药品库存水平
     */
    @GetMapping("/stock-level/{medicineId}")
    @RequiresPermission("PHARMACY_MANAGEMENT")
    public ResponseEntity<StockLevel> getCurrentStockLevel(@PathVariable @NotNull Long medicineId) {
        logger.info("查询药品库存水平，药品ID: {}", medicineId);
        
        StockLevel stockLevel = stockManagementService.getCurrentStockLevel(medicineId);
        return ResponseEntity.ok(stockLevel);
    }

    /**
     * 获取药品指定批次的库存水平
     */
    @GetMapping("/stock-level/{medicineId}/batch/{batchNumber}")
    @RequiresPermission("PHARMACY_MANAGEMENT")
    public ResponseEntity<StockLevel> getCurrentStockLevel(
            @PathVariable @NotNull Long medicineId,
            @PathVariable @NotNull String batchNumber) {
        logger.info("查询药品批次库存水平，药品ID: {}, 批次号: {}", medicineId, batchNumber);
        
        StockLevel stockLevel = stockManagementService.getCurrentStockLevel(medicineId, batchNumber);
        return ResponseEntity.ok(stockLevel);
    }

    /**
     * 获取所有药品的库存水平
     */
    @GetMapping("/stock-levels")
    @RequiresPermission("PHARMACY_MANAGEMENT")
    public ResponseEntity<List<StockLevel>> getAllStockLevels() {
        logger.info("查询所有药品库存水平");
        
        List<StockLevel> stockLevels = stockManagementService.getAllStockLevels();
        return ResponseEntity.ok(stockLevels);
    }

    /**
     * 获取库存不足的药品列表
     */
    @GetMapping("/low-stock")
    @RequiresPermission("PHARMACY_MANAGEMENT")
    public ResponseEntity<List<StockLevel>> getLowStockItems() {
        logger.info("查询库存不足的药品列表");
        
        List<StockLevel> lowStockItems = stockManagementService.getLowStockItems();
        return ResponseEntity.ok(lowStockItems);
    }

    /**
     * 获取库存过多的药品列表
     */
    @GetMapping("/overstocked")
    @RequiresPermission("PHARMACY_MANAGEMENT")
    public ResponseEntity<List<StockLevel>> getOverstockedItems() {
        logger.info("查询库存过多的药品列表");
        
        List<StockLevel> overstockedItems = stockManagementService.getOverstockedItems();
        return ResponseEntity.ok(overstockedItems);
    }

    /**
     * 获取即将过期的药品列表
     */
    @GetMapping("/expiring-soon")
    @RequiresPermission("PHARMACY_MANAGEMENT")
    public ResponseEntity<List<StockLevel>> getExpiringSoonItems(
            @RequestParam(defaultValue = "30") @Min(1) int daysThreshold) {
        logger.info("查询即将过期的药品列表，天数阈值: {}", daysThreshold);
        
        List<StockLevel> expiringSoonItems = stockManagementService.getExpiringSoonItems(daysThreshold);
        return ResponseEntity.ok(expiringSoonItems);
    }

    /**
     * 获取已过期的药品列表
     */
    @GetMapping("/expired")
    @RequiresPermission("PHARMACY_MANAGEMENT")
    public ResponseEntity<List<StockLevel>> getExpiredItems() {
        logger.info("查询已过期的药品列表");
        
        List<StockLevel> expiredItems = stockManagementService.getExpiredItems();
        return ResponseEntity.ok(expiredItems);
    }

    /**
     * 获取药品的库存交易记录
     */
    @GetMapping("/transactions/{medicineId}")
    @RequiresPermission("PHARMACY_MANAGEMENT")
    public ResponseEntity<List<StockTransaction>> getStockTransactions(@PathVariable @NotNull Long medicineId) {
        logger.info("查询药品库存交易记录，药品ID: {}", medicineId);
        
        List<StockTransaction> transactions = stockManagementService.getStockTransactions(medicineId);
        return ResponseEntity.ok(transactions);
    }

    /**
     * 获取指定日期范围内的库存交易记录
     */
    @GetMapping("/transactions/{medicineId}/date-range")
    @RequiresPermission("PHARMACY_MANAGEMENT")
    public ResponseEntity<List<StockTransaction>> getStockTransactions(
            @PathVariable @NotNull Long medicineId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        logger.info("查询药品指定日期范围库存交易记录，药品ID: {}, 开始日期: {}, 结束日期: {}", 
                   medicineId, startDate, endDate);
        
        List<StockTransaction> transactions = stockManagementService.getStockTransactions(medicineId, startDate, endDate);
        return ResponseEntity.ok(transactions);
    }

    /**
     * 获取所有库存交易记录
     */
    @GetMapping("/transactions")
    @RequiresPermission("PHARMACY_MANAGEMENT")
    public ResponseEntity<List<StockTransaction>> getAllStockTransactions(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        logger.info("查询所有库存交易记录，开始日期: {}, 结束日期: {}", startDate, endDate);
        
        List<StockTransaction> transactions = stockManagementService.getAllStockTransactions(startDate, endDate);
        return ResponseEntity.ok(transactions);
    }

    /**
     * 执行库存盘点
     */
    @PostMapping("/stock-taking")
    @RequiresPermission("PHARMACY_MANAGEMENT")
    public ResponseEntity<Map<String, Object>> performStockTaking(@Valid @RequestBody StockTakingRequest request) {
        logger.info("执行库存盘点: {}", request);
        
        stockManagementService.performStockTaking(request);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "库存盘点完成");
        
        return ResponseEntity.ok(response);
    }

    /**
     * 获取库存盘点记录
     */
    @GetMapping("/stock-taking/records")
    @RequiresPermission("PHARMACY_MANAGEMENT")
    public ResponseEntity<List<StockTransaction>> getStockTakingRecords(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        logger.info("查询库存盘点记录，开始日期: {}, 结束日期: {}", startDate, endDate);
        
        List<StockTransaction> records = stockManagementService.getStockTakingRecords(startDate, endDate);
        return ResponseEntity.ok(records);
    }

    /**
     * 预留库存
     */
    @PostMapping("/reserve")
    @RequiresPermission("PHARMACY_MANAGEMENT")
    public ResponseEntity<Map<String, Object>> reserveStock(
            @RequestParam @NotNull Long medicineId,
            @RequestParam @NotNull String batchNumber,
            @RequestParam @NotNull @Min(1) Integer quantity) {
        logger.info("预留库存，药品ID: {}, 批次号: {}, 数量: {}", medicineId, batchNumber, quantity);
        
        boolean success = stockManagementService.reserveStock(medicineId, batchNumber, quantity);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", success);
        response.put("message", success ? "库存预留成功" : "库存预留失败，可能库存不足");
        
        return ResponseEntity.ok(response);
    }

    /**
     * 释放预留库存
     */
    @PostMapping("/release-reserved")
    @RequiresPermission("PHARMACY_MANAGEMENT")
    public ResponseEntity<Map<String, Object>> releaseReservedStock(
            @RequestParam @NotNull Long medicineId,
            @RequestParam @NotNull String batchNumber,
            @RequestParam @NotNull @Min(1) Integer quantity) {
        logger.info("释放预留库存，药品ID: {}, 批次号: {}, 数量: {}", medicineId, batchNumber, quantity);
        
        stockManagementService.releaseReservedStock(medicineId, batchNumber, quantity);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "预留库存释放成功");
        
        return ResponseEntity.ok(response);
    }

    /**
     * 锁定库存
     */
    @PostMapping("/lock")
    @RequiresPermission("PHARMACY_MANAGEMENT")
    public ResponseEntity<Map<String, Object>> lockStock(
            @RequestParam @NotNull Long medicineId,
            @RequestParam @NotNull String batchNumber,
            @RequestParam @NotNull @Min(1) Integer quantity) {
        logger.info("锁定库存，药品ID: {}, 批次号: {}, 数量: {}", medicineId, batchNumber, quantity);
        
        boolean success = stockManagementService.lockStock(medicineId, batchNumber, quantity);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", success);
        response.put("message", success ? "库存锁定成功" : "库存锁定失败，可能库存不足");
        
        return ResponseEntity.ok(response);
    }

    /**
     * 解锁库存
     */
    @PostMapping("/unlock")
    @RequiresPermission("PHARMACY_MANAGEMENT")
    public ResponseEntity<Map<String, Object>> unlockStock(
            @RequestParam @NotNull Long medicineId,
            @RequestParam @NotNull String batchNumber,
            @RequestParam @NotNull @Min(1) Integer quantity) {
        logger.info("解锁库存，药品ID: {}, 批次号: {}, 数量: {}", medicineId, batchNumber, quantity);
        
        stockManagementService.unlockStock(medicineId, batchNumber, quantity);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "库存解锁成功");
        
        return ResponseEntity.ok(response);
    }

    /**
     * 库存调拨
     */
    @PostMapping("/transfer")
    @RequiresPermission("PHARMACY_MANAGEMENT")
    public ResponseEntity<Map<String, Object>> transferStock(
            @RequestParam @NotNull Long medicineId,
            @RequestParam @NotNull String fromBatch,
            @RequestParam @NotNull String toBatch,
            @RequestParam @NotNull @Min(1) Integer quantity,
            @RequestParam String reason) {
        logger.info("库存调拨，药品ID: {}, 从批次: {}, 到批次: {}, 数量: {}", 
                   medicineId, fromBatch, toBatch, quantity);
        
        stockManagementService.transferStock(medicineId, fromBatch, toBatch, quantity, reason);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "库存调拨成功");
        
        return ResponseEntity.ok(response);
    }

    /**
     * 报损处理
     */
    @PostMapping("/loss")
    @RequiresPermission("PHARMACY_MANAGEMENT")
    public ResponseEntity<Map<String, Object>> recordStockLoss(
            @RequestParam @NotNull Long medicineId,
            @RequestParam @NotNull String batchNumber,
            @RequestParam @NotNull @Min(1) Integer quantity,
            @RequestParam @NotNull String reason) {
        logger.info("库存报损，药品ID: {}, 批次号: {}, 数量: {}, 原因: {}", 
                   medicineId, batchNumber, quantity, reason);
        
        stockManagementService.recordStockLoss(medicineId, batchNumber, quantity, reason);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "库存报损处理成功");
        
        return ResponseEntity.ok(response);
    }

    /**
     * 处理过期药品
     */
    @PostMapping("/handle-expired")
    @RequiresPermission("PHARMACY_MANAGEMENT")
    public ResponseEntity<Map<String, Object>> handleExpiredMedicines() {
        logger.info("处理过期药品");
        
        stockManagementService.handleExpiredMedicines();
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "过期药品处理完成");
        
        return ResponseEntity.ok(response);
    }

    /**
     * 获取补货建议
     */
    @GetMapping("/restock-suggestions")
    @RequiresPermission("PHARMACY_MANAGEMENT")
    public ResponseEntity<List<StockLevel>> getRestockSuggestions() {
        logger.info("获取补货建议");
        
        List<StockLevel> suggestions = stockManagementService.getRestockSuggestions();
        return ResponseEntity.ok(suggestions);
    }

    /**
     * 获取库存总价值
     */
    @GetMapping("/total-value")
    @RequiresPermission("PHARMACY_MANAGEMENT")
    public ResponseEntity<Map<String, Object>> getTotalInventoryValue() {
        logger.info("获取库存总价值");
        
        Double totalValue = stockManagementService.getTotalInventoryValue();
        
        Map<String, Object> response = new HashMap<>();
        response.put("totalValue", totalValue);
        
        return ResponseEntity.ok(response);
    }

    /**
     * 获取指定药品的库存价值
     */
    @GetMapping("/value/{medicineId}")
    @RequiresPermission("PHARMACY_MANAGEMENT")
    public ResponseEntity<Map<String, Object>> getInventoryValueByMedicine(@PathVariable @NotNull Long medicineId) {
        logger.info("获取药品库存价值，药品ID: {}", medicineId);
        
        Double value = stockManagementService.getInventoryValueByMedicine(medicineId);
        
        Map<String, Object> response = new HashMap<>();
        response.put("medicineId", medicineId);
        response.put("inventoryValue", value);
        
        return ResponseEntity.ok(response);
    }

    /**
     * 获取库存周转率
     */
    @GetMapping("/turnover-rate/{medicineId}")
    @RequiresPermission("PHARMACY_MANAGEMENT")
    public ResponseEntity<Map<String, Object>> getInventoryTurnoverRate(
            @PathVariable @NotNull Long medicineId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        logger.info("获取库存周转率，药品ID: {}, 开始日期: {}, 结束日期: {}", medicineId, startDate, endDate);
        
        Double turnoverRate = stockManagementService.getInventoryTurnoverRate(medicineId, startDate, endDate);
        
        Map<String, Object> response = new HashMap<>();
        response.put("medicineId", medicineId);
        response.put("turnoverRate", turnoverRate);
        response.put("startDate", startDate);
        response.put("endDate", endDate);
        
        return ResponseEntity.ok(response);
    }

    /**
     * 获取库存预警信息
     */
    @GetMapping("/alerts")
    @RequiresPermission("PHARMACY_MANAGEMENT")
    public ResponseEntity<List<String>> getInventoryAlerts() {
        logger.info("获取库存预警信息");
        
        List<String> alerts = stockManagementService.getInventoryAlerts();
        return ResponseEntity.ok(alerts);
    }

    /**
     * 生成库存报表
     */
    @GetMapping("/report")
    @RequiresPermission("PHARMACY_MANAGEMENT")
    public ResponseEntity<Object> generateInventoryReport(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(defaultValue = "summary") String reportType) {
        logger.info("生成库存报表，开始日期: {}, 结束日期: {}, 报表类型: {}", startDate, endDate, reportType);
        
        Object report = stockManagementService.generateInventoryReport(startDate, endDate, reportType);
        return ResponseEntity.ok(report);
    }

    /**
     * 批量更新库存状态
     */
    @PostMapping("/batch-update-status")
    @RequiresPermission("PHARMACY_MANAGEMENT")
    public ResponseEntity<Map<String, Object>> batchUpdateStockStatus() {
        logger.info("批量更新库存状态");
        
        stockManagementService.batchUpdateStockStatus();
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "库存状态更新完成");
        
        return ResponseEntity.ok(response);
    }

    /**
     * 同步库存数据
     */
    @PostMapping("/synchronize")
    @RequiresPermission("PHARMACY_MANAGEMENT")
    public ResponseEntity<Map<String, Object>> synchronizeInventoryData() {
        logger.info("同步库存数据");
        
        stockManagementService.synchronizeInventoryData();
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "库存数据同步完成");
        
        return ResponseEntity.ok(response);
    }
}