package org.me.joy.clinic.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.me.joy.clinic.dto.*;
import org.me.joy.clinic.entity.InventoryLevel;
import org.me.joy.clinic.entity.Medicine;
import org.me.joy.clinic.entity.StockTransaction;
import org.me.joy.clinic.exception.BusinessException;
import org.me.joy.clinic.exception.ValidationException;
import org.me.joy.clinic.mapper.InventoryLevelMapper;
import org.me.joy.clinic.mapper.MedicineMapper;
import org.me.joy.clinic.mapper.StockTransactionMapper;
import org.me.joy.clinic.service.StockManagementService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 库存管理服务实现类
 */
@Service
@Transactional
public class StockManagementServiceImpl implements StockManagementService {

    private static final Logger logger = LoggerFactory.getLogger(StockManagementServiceImpl.class);

    @Autowired
    private MedicineMapper medicineMapper;

    @Autowired
    private InventoryLevelMapper inventoryLevelMapper;

    @Autowired
    private StockTransactionMapper stockTransactionMapper;

    @Override
    public void recordStockIn(StockInRequest request) {
        logger.info("开始处理入库请求: {}", request);

        // 验证药品是否存在
        Medicine medicine = medicineMapper.selectById(request.getMedicineId());
        if (medicine == null) {
            throw new ValidationException("MEDICINE_NOT_FOUND", "药品不存在，ID: " + request.getMedicineId());
        }

        // 验证过期日期
        if (request.getExpiryDate().isBefore(LocalDate.now())) {
            throw new ValidationException("EXPIRED_MEDICINE", "不能入库已过期的药品");
        }

        // 生成交易编号
        String transactionNumber = generateTransactionNumber("IN");

        // 创建库存交易记录
        StockTransaction transaction = new StockTransaction();
        transaction.setTransactionNumber(transactionNumber);
        transaction.setMedicineId(request.getMedicineId());
        transaction.setTransactionType("入库");
        transaction.setQuantity(request.getQuantity());
        transaction.setUnitPrice(request.getPurchasePrice());
        transaction.setBatchNumber(request.getBatchNumber());
        transaction.setProductionDate(request.getProductionDate());
        transaction.setExpiryDate(request.getExpiryDate());
        transaction.setSupplierId(request.getSupplierId());
        transaction.setSupplierName(request.getSupplierName());
        transaction.setRelatedDocumentNumber(request.getRelatedDocumentNumber());
        transaction.setReason(request.getReason());
        transaction.setRemarks(request.getRemarks());
        transaction.setTransactionDate(LocalDateTime.now());
        transaction.setStatus("已确认");
        transaction.calculateTotalAmount();

        // 查找或创建库存水平记录
        QueryWrapper<InventoryLevel> wrapper = new QueryWrapper<>();
        wrapper.eq("medicine_id", request.getMedicineId())
               .eq("batch_number", request.getBatchNumber());
        InventoryLevel inventoryLevel = inventoryLevelMapper.selectOne(wrapper);

        if (inventoryLevel == null) {
            // 创建新的库存水平记录
            inventoryLevel = new InventoryLevel();
            inventoryLevel.setMedicineId(request.getMedicineId());
            inventoryLevel.setBatchNumber(request.getBatchNumber());
            inventoryLevel.setCurrentStock(request.getQuantity());
            inventoryLevel.setAvailableStock(request.getQuantity());
            inventoryLevel.setPurchasePrice(request.getPurchasePrice());
            inventoryLevel.setProductionDate(request.getProductionDate());
            inventoryLevel.setExpiryDate(request.getExpiryDate());
            inventoryLevel.setSupplierId(request.getSupplierId());
            inventoryLevel.setSupplierName(request.getSupplierName());
            inventoryLevel.setStorageLocation(request.getStorageLocation());
            inventoryLevel.setStatus("正常");
            inventoryLevel.setLastInboundDate(LocalDateTime.now());

            transaction.setStockBefore(0);
            transaction.setStockAfter(request.getQuantity());

            inventoryLevelMapper.insert(inventoryLevel);
        } else {
            // 更新现有库存水平记录
            transaction.setStockBefore(inventoryLevel.getCurrentStock());
            inventoryLevel.addStock(request.getQuantity());
            transaction.setStockAfter(inventoryLevel.getCurrentStock());

            inventoryLevelMapper.updateById(inventoryLevel);
        }

        // 保存交易记录
        stockTransactionMapper.insert(transaction);

        logger.info("入库处理完成，交易编号: {}", transactionNumber);
    }

    @Override
    public void recordStockOut(StockOutRequest request) {
        logger.info("开始处理出库请求: {}", request);

        // 验证药品是否存在
        Medicine medicine = medicineMapper.selectById(request.getMedicineId());
        if (medicine == null) {
            throw new ValidationException("MEDICINE_NOT_FOUND", "药品不存在，ID: " + request.getMedicineId());
        }

        String batchNumber = request.getBatchNumber();
        if (batchNumber == null || batchNumber.trim().isEmpty()) {
            // 如果没有指定批次号，使用先进先出原则选择批次
            batchNumber = getFirstInFirstOutBatch(request.getMedicineId(), request.getQuantity());
            if (batchNumber == null) {
                throw new BusinessException("INSUFFICIENT_STOCK", "库存不足，无法完成出库操作");
            }
        }

        // 查找库存水平记录
        QueryWrapper<InventoryLevel> wrapper = new QueryWrapper<>();
        wrapper.eq("medicine_id", request.getMedicineId())
               .eq("batch_number", batchNumber);
        InventoryLevel inventoryLevel = inventoryLevelMapper.selectOne(wrapper);

        if (inventoryLevel == null) {
            throw new BusinessException("BATCH_NOT_FOUND", "指定批次的库存不存在");
        }

        // 验证库存是否充足
        if (inventoryLevel.getAvailableStock() < request.getQuantity()) {
            throw new BusinessException("INSUFFICIENT_STOCK", "库存不足，当前可用库存: " + inventoryLevel.getAvailableStock() + 
                                      "，需要出库: " + request.getQuantity());
        }

        // 生成交易编号
        String transactionNumber = generateTransactionNumber("OUT");

        // 创建库存交易记录
        StockTransaction transaction = new StockTransaction();
        transaction.setTransactionNumber(transactionNumber);
        transaction.setMedicineId(request.getMedicineId());
        transaction.setTransactionType("出库");
        transaction.setQuantity(-request.getQuantity()); // 出库为负数
        transaction.setUnitPrice(inventoryLevel.getPurchasePrice());
        transaction.setBatchNumber(batchNumber);
        transaction.setExpiryDate(inventoryLevel.getExpiryDate());
        transaction.setRelatedDocumentNumber(request.getRelatedDocumentNumber());
        transaction.setReason(request.getReason());
        transaction.setRemarks(request.getRemarks());
        transaction.setTransactionDate(LocalDateTime.now());
        transaction.setStatus("已确认");
        transaction.setStockBefore(inventoryLevel.getCurrentStock());
        transaction.calculateTotalAmount();

        // 更新库存水平
        inventoryLevel.reduceStock(request.getQuantity());
        transaction.setStockAfter(inventoryLevel.getCurrentStock());

        // 保存更新
        inventoryLevelMapper.updateById(inventoryLevel);
        stockTransactionMapper.insert(transaction);

        logger.info("出库处理完成，交易编号: {}", transactionNumber);
    }

    @Override
    public StockLevel getCurrentStockLevel(Long medicineId) {
        Medicine medicine = medicineMapper.selectById(medicineId);
        if (medicine == null) {
            throw new ValidationException("MEDICINE_NOT_FOUND", "药品不存在，ID: " + medicineId);
        }

        // 获取该药品的所有批次库存
        QueryWrapper<InventoryLevel> wrapper = new QueryWrapper<>();
        wrapper.eq("medicine_id", medicineId)
               .gt("current_stock", 0)
               .orderByAsc("expiry_date");
        List<InventoryLevel> inventoryLevels = inventoryLevelMapper.selectList(wrapper);

        if (inventoryLevels.isEmpty()) {
            return createEmptyStockLevel(medicine);
        }

        // 汇总所有批次的库存
        return aggregateStockLevels(medicine, inventoryLevels);
    }

    @Override
    public StockLevel getCurrentStockLevel(Long medicineId, String batchNumber) {
        Medicine medicine = medicineMapper.selectById(medicineId);
        if (medicine == null) {
            throw new ValidationException("MEDICINE_NOT_FOUND", "药品不存在，ID: " + medicineId);
        }

        QueryWrapper<InventoryLevel> wrapper = new QueryWrapper<>();
        wrapper.eq("medicine_id", medicineId)
               .eq("batch_number", batchNumber);
        InventoryLevel inventoryLevel = inventoryLevelMapper.selectOne(wrapper);

        if (inventoryLevel == null) {
            return createEmptyStockLevel(medicine, batchNumber);
        }

        return convertToStockLevel(medicine, inventoryLevel);
    }

    @Override
    public List<StockLevel> getAllStockLevels() {
        List<Medicine> medicines = medicineMapper.selectList(
            new QueryWrapper<Medicine>().eq("enabled", true)
        );

        return medicines.stream()
                       .map(medicine -> getCurrentStockLevel(medicine.getId()))
                       .collect(Collectors.toList());
    }

    @Override
    public List<StockLevel> getLowStockItems() {
        List<StockLevel> allStockLevels = getAllStockLevels();
        return allStockLevels.stream()
                            .filter(StockLevel::isLowStock)
                            .collect(Collectors.toList());
    }

    @Override
    public List<StockLevel> getOverstockedItems() {
        List<StockLevel> allStockLevels = getAllStockLevels();
        return allStockLevels.stream()
                            .filter(StockLevel::isOverstocked)
                            .collect(Collectors.toList());
    }

    @Override
    public List<StockLevel> getExpiringSoonItems(int daysThreshold) {
        QueryWrapper<InventoryLevel> wrapper = new QueryWrapper<>();
        wrapper.gt("current_stock", 0)
               .le("expiry_date", LocalDate.now().plusDays(daysThreshold))
               .gt("expiry_date", LocalDate.now())
               .orderByAsc("expiry_date");
        
        List<InventoryLevel> inventoryLevels = inventoryLevelMapper.selectList(wrapper);
        
        return inventoryLevels.stream()
                             .map(this::convertToStockLevel)
                             .collect(Collectors.toList());
    }

    @Override
    public List<StockLevel> getExpiredItems() {
        QueryWrapper<InventoryLevel> wrapper = new QueryWrapper<>();
        wrapper.gt("current_stock", 0)
               .lt("expiry_date", LocalDate.now())
               .orderByAsc("expiry_date");
        
        List<InventoryLevel> inventoryLevels = inventoryLevelMapper.selectList(wrapper);
        
        return inventoryLevels.stream()
                             .map(this::convertToStockLevel)
                             .collect(Collectors.toList());
    }

    @Override
    public List<StockTransaction> getStockTransactions(Long medicineId) {
        QueryWrapper<StockTransaction> wrapper = new QueryWrapper<>();
        wrapper.eq("medicine_id", medicineId)
               .orderByDesc("transaction_date");
        return stockTransactionMapper.selectList(wrapper);
    }

    @Override
    public List<StockTransaction> getStockTransactions(Long medicineId, LocalDate startDate, LocalDate endDate) {
        QueryWrapper<StockTransaction> wrapper = new QueryWrapper<>();
        wrapper.eq("medicine_id", medicineId)
               .ge("transaction_date", startDate.atStartOfDay())
               .le("transaction_date", endDate.atTime(23, 59, 59))
               .orderByDesc("transaction_date");
        return stockTransactionMapper.selectList(wrapper);
    }

    @Override
    public List<StockTransaction> getAllStockTransactions(LocalDate startDate, LocalDate endDate) {
        QueryWrapper<StockTransaction> wrapper = new QueryWrapper<>();
        wrapper.ge("transaction_date", startDate.atStartOfDay())
               .le("transaction_date", endDate.atTime(23, 59, 59))
               .orderByDesc("transaction_date");
        return stockTransactionMapper.selectList(wrapper);
    }

    @Override
    public void performStockTaking(StockTakingRequest request) {
        logger.info("开始执行库存盘点: {}", request);

        for (StockTakingRequest.StockTakingItem item : request.getItems()) {
            // 查找库存水平记录
            QueryWrapper<InventoryLevel> wrapper = new QueryWrapper<>();
            wrapper.eq("medicine_id", item.getMedicineId())
                   .eq("batch_number", item.getBatchNumber());
            InventoryLevel inventoryLevel = inventoryLevelMapper.selectOne(wrapper);

            if (inventoryLevel == null) {
                logger.warn("盘点时未找到库存记录: 药品ID={}, 批次号={}", 
                           item.getMedicineId(), item.getBatchNumber());
                continue;
            }

            // 检查是否有差异
            if (item.hasDifference()) {
                // 生成盘点交易记录
                String transactionNumber = generateTransactionNumber("INV");
                
                StockTransaction transaction = new StockTransaction();
                transaction.setTransactionNumber(transactionNumber);
                transaction.setMedicineId(item.getMedicineId());
                transaction.setTransactionType("盘点");
                transaction.setQuantity(item.getDifference());
                transaction.setBatchNumber(item.getBatchNumber());
                transaction.setStockBefore(item.getBookStock());
                transaction.setStockAfter(item.getActualStock());
                transaction.setReason("库存盘点差异：" + item.getDifferenceReason());
                transaction.setRemarks(request.getDescription());
                transaction.setTransactionDate(LocalDateTime.now());
                transaction.setOperatorName(request.getInventoryStaff());
                transaction.setReviewerName(request.getSupervisor());
                transaction.setStatus("已确认");

                // 更新库存水平
                inventoryLevel.setCurrentStock(item.getActualStock());
                inventoryLevel.setLastInventoryDate(request.getInventoryDate());
                inventoryLevel.updateAvailableStock();

                // 保存更新
                stockTransactionMapper.insert(transaction);
                inventoryLevelMapper.updateById(inventoryLevel);

                logger.info("盘点差异处理完成: 药品ID={}, 批次号={}, 差异={}", 
                           item.getMedicineId(), item.getBatchNumber(), item.getDifference());
            } else {
                // 即使没有差异也要更新盘点日期
                inventoryLevel.setLastInventoryDate(request.getInventoryDate());
                inventoryLevelMapper.updateById(inventoryLevel);
            }
        }

        logger.info("库存盘点完成");
    }

    @Override
    public List<StockTransaction> getStockTakingRecords(LocalDate startDate, LocalDate endDate) {
        QueryWrapper<StockTransaction> wrapper = new QueryWrapper<>();
        wrapper.eq("transaction_type", "盘点")
               .ge("transaction_date", startDate.atStartOfDay())
               .le("transaction_date", endDate.atTime(23, 59, 59))
               .orderByDesc("transaction_date");
        return stockTransactionMapper.selectList(wrapper);
    }

    @Override
    public boolean reserveStock(Long medicineId, String batchNumber, Integer quantity) {
        QueryWrapper<InventoryLevel> wrapper = new QueryWrapper<>();
        wrapper.eq("medicine_id", medicineId)
               .eq("batch_number", batchNumber);
        InventoryLevel inventoryLevel = inventoryLevelMapper.selectOne(wrapper);

        if (inventoryLevel != null && inventoryLevel.reserveStock(quantity)) {
            inventoryLevelMapper.updateById(inventoryLevel);
            return true;
        }
        return false;
    }

    @Override
    public void releaseReservedStock(Long medicineId, String batchNumber, Integer quantity) {
        QueryWrapper<InventoryLevel> wrapper = new QueryWrapper<>();
        wrapper.eq("medicine_id", medicineId)
               .eq("batch_number", batchNumber);
        InventoryLevel inventoryLevel = inventoryLevelMapper.selectOne(wrapper);

        if (inventoryLevel != null) {
            inventoryLevel.releaseReservedStock(quantity);
            inventoryLevelMapper.updateById(inventoryLevel);
        }
    }

    @Override
    public boolean lockStock(Long medicineId, String batchNumber, Integer quantity) {
        QueryWrapper<InventoryLevel> wrapper = new QueryWrapper<>();
        wrapper.eq("medicine_id", medicineId)
               .eq("batch_number", batchNumber);
        InventoryLevel inventoryLevel = inventoryLevelMapper.selectOne(wrapper);

        if (inventoryLevel != null && 
            inventoryLevel.getAvailableStock() >= quantity) {
            inventoryLevel.setLockedStock(
                (inventoryLevel.getLockedStock() != null ? inventoryLevel.getLockedStock() : 0) + quantity
            );
            inventoryLevelMapper.updateById(inventoryLevel);
            return true;
        }
        return false;
    }

    @Override
    public void unlockStock(Long medicineId, String batchNumber, Integer quantity) {
        QueryWrapper<InventoryLevel> wrapper = new QueryWrapper<>();
        wrapper.eq("medicine_id", medicineId)
               .eq("batch_number", batchNumber);
        InventoryLevel inventoryLevel = inventoryLevelMapper.selectOne(wrapper);

        if (inventoryLevel != null) {
            int currentLocked = inventoryLevel.getLockedStock() != null ? inventoryLevel.getLockedStock() : 0;
            inventoryLevel.setLockedStock(Math.max(0, currentLocked - quantity));
            inventoryLevelMapper.updateById(inventoryLevel);
        }
    }

    @Override
    public void transferStock(Long medicineId, String fromBatch, String toBatch, Integer quantity, String reason) {
        // 从源批次出库
        StockOutRequest outRequest = new StockOutRequest(medicineId, quantity, fromBatch, "调拨");
        outRequest.setReason(reason);
        recordStockOut(outRequest);

        // 向目标批次入库（这里简化处理，实际应该有更复杂的逻辑）
        logger.info("库存调拨完成: 药品ID={}, 从批次={} 到批次={}, 数量={}", 
                   medicineId, fromBatch, toBatch, quantity);
    }

    @Override
    public void recordStockLoss(Long medicineId, String batchNumber, Integer quantity, String reason) {
        StockOutRequest request = new StockOutRequest(medicineId, quantity, batchNumber, "报损");
        request.setReason(reason);
        recordStockOut(request);
    }

    @Override
    public void handleExpiredMedicines() {
        List<StockLevel> expiredItems = getExpiredItems();
        
        for (StockLevel item : expiredItems) {
            if (item.getCurrentStock() > 0) {
                recordStockLoss(item.getMedicineId(), item.getBatchNumber(), 
                               item.getCurrentStock(), "药品过期处理");
                logger.info("处理过期药品: 药品ID={}, 批次号={}, 数量={}", 
                           item.getMedicineId(), item.getBatchNumber(), item.getCurrentStock());
            }
        }
    }

    @Override
    public List<StockLevel> getRestockSuggestions() {
        return getLowStockItems();
    }

    @Override
    public Double getTotalInventoryValue() {
        QueryWrapper<InventoryLevel> wrapper = new QueryWrapper<>();
        wrapper.gt("current_stock", 0);
        List<InventoryLevel> inventoryLevels = inventoryLevelMapper.selectList(wrapper);

        return inventoryLevels.stream()
                             .filter(level -> level.getPurchasePrice() != null && level.getCurrentStock() != null)
                             .mapToDouble(level -> level.getPurchasePrice().doubleValue() * level.getCurrentStock())
                             .sum();
    }

    @Override
    public Double getInventoryValueByMedicine(Long medicineId) {
        QueryWrapper<InventoryLevel> wrapper = new QueryWrapper<>();
        wrapper.eq("medicine_id", medicineId)
               .gt("current_stock", 0);
        List<InventoryLevel> inventoryLevels = inventoryLevelMapper.selectList(wrapper);

        return inventoryLevels.stream()
                             .filter(level -> level.getPurchasePrice() != null && level.getCurrentStock() != null)
                             .mapToDouble(level -> level.getPurchasePrice().doubleValue() * level.getCurrentStock())
                             .sum();
    }

    @Override
    public Double getInventoryTurnoverRate(Long medicineId, LocalDate startDate, LocalDate endDate) {
        // 获取期间内的出库总量
        List<StockTransaction> outboundTransactions = getStockTransactions(medicineId, startDate, endDate)
            .stream()
            .filter(t -> "出库".equals(t.getTransactionType()))
            .collect(Collectors.toList());

        int totalOutbound = outboundTransactions.stream()
                                               .mapToInt(t -> Math.abs(t.getQuantity()))
                                               .sum();

        // 获取平均库存
        StockLevel currentStock = getCurrentStockLevel(medicineId);
        int averageStock = currentStock.getCurrentStock() != null ? currentStock.getCurrentStock() : 0;

        if (averageStock == 0) {
            return 0.0;
        }

        return (double) totalOutbound / averageStock;
    }

    @Override
    public List<String> getInventoryAlerts() {
        List<String> alerts = new ArrayList<>();

        // 库存不足警告
        List<StockLevel> lowStockItems = getLowStockItems();
        for (StockLevel item : lowStockItems) {
            alerts.add(String.format("库存不足警告：%s (编码：%s) 当前库存：%d，最小库存：%d", 
                                   item.getMedicineName(), item.getMedicineCode(), 
                                   item.getCurrentStock(), item.getMinStockLevel()));
        }

        // 即将过期警告
        List<StockLevel> expiringSoonItems = getExpiringSoonItems(30);
        for (StockLevel item : expiringSoonItems) {
            alerts.add(String.format("即将过期警告：%s (批次：%s) 将在%d天后过期", 
                                   item.getMedicineName(), item.getBatchNumber(), 
                                   item.getRemainingShelfLifeDays()));
        }

        // 已过期警告
        List<StockLevel> expiredItems = getExpiredItems();
        for (StockLevel item : expiredItems) {
            alerts.add(String.format("已过期警告：%s (批次：%s) 已过期%d天", 
                                   item.getMedicineName(), item.getBatchNumber(), 
                                   Math.abs(item.getRemainingShelfLifeDays())));
        }

        return alerts;
    }

    @Override
    public Object generateInventoryReport(LocalDate startDate, LocalDate endDate, String reportType) {
        // 这里可以根据reportType生成不同类型的报表
        Map<String, Object> report = new HashMap<>();
        report.put("reportType", reportType);
        report.put("startDate", startDate);
        report.put("endDate", endDate);
        report.put("totalValue", getTotalInventoryValue());
        report.put("lowStockCount", getLowStockItems().size());
        report.put("expiredCount", getExpiredItems().size());
        report.put("transactionCount", getAllStockTransactions(startDate, endDate).size());
        
        return report;
    }

    @Override
    public boolean validateStockTransaction(Long medicineId, String batchNumber, Integer quantity, String transactionType) {
        if ("出库".equals(transactionType) || "调拨".equals(transactionType) || "报损".equals(transactionType)) {
            StockLevel stockLevel = getCurrentStockLevel(medicineId, batchNumber);
            return stockLevel.getAvailableStock() >= quantity;
        }
        return true; // 入库等操作默认有效
    }

    @Override
    public String getFirstInFirstOutBatch(Long medicineId, Integer requiredQuantity) {
        QueryWrapper<InventoryLevel> wrapper = new QueryWrapper<>();
        wrapper.eq("medicine_id", medicineId)
               .gt("available_stock", 0)
               .eq("status", "正常")
               .orderByAsc("expiry_date");
        
        List<InventoryLevel> inventoryLevels = inventoryLevelMapper.selectList(wrapper);
        
        int remainingQuantity = requiredQuantity;
        for (InventoryLevel level : inventoryLevels) {
            if (level.getAvailableStock() >= remainingQuantity) {
                return level.getBatchNumber();
            }
            remainingQuantity -= level.getAvailableStock();
        }
        
        return null; // 库存不足
    }

    @Override
    public void batchUpdateStockStatus() {
        // 更新过期状态
        QueryWrapper<InventoryLevel> expiredWrapper = new QueryWrapper<>();
        expiredWrapper.lt("expiry_date", LocalDate.now())
                     .ne("status", "过期");
        
        List<InventoryLevel> expiredLevels = inventoryLevelMapper.selectList(expiredWrapper);
        for (InventoryLevel level : expiredLevels) {
            level.setStatus("过期");
            inventoryLevelMapper.updateById(level);
        }

        // 更新预警状态
        List<Medicine> medicines = medicineMapper.selectList(new QueryWrapper<Medicine>().eq("enabled", true));
        for (Medicine medicine : medicines) {
            StockLevel stockLevel = getCurrentStockLevel(medicine.getId());
            if (stockLevel.isLowStock()) {
                // 可以在这里添加预警状态更新逻辑
                logger.info("药品库存预警: {}", medicine.getName());
            }
        }
    }

    @Override
    public void synchronizeInventoryData() {
        // 同步库存数据的逻辑
        logger.info("开始同步库存数据");
        batchUpdateStockStatus();
        logger.info("库存数据同步完成");
    }

    // 私有辅助方法

    private String generateTransactionNumber(String prefix) {
        return prefix + System.currentTimeMillis() + String.format("%03d", new Random().nextInt(1000));
    }

    private StockLevel createEmptyStockLevel(Medicine medicine) {
        return createEmptyStockLevel(medicine, null);
    }

    private StockLevel createEmptyStockLevel(Medicine medicine, String batchNumber) {
        StockLevel stockLevel = new StockLevel();
        stockLevel.setMedicineId(medicine.getId());
        stockLevel.setMedicineCode(medicine.getMedicineCode());
        stockLevel.setMedicineName(medicine.getName());
        stockLevel.setBatchNumber(batchNumber);
        stockLevel.setCurrentStock(0);
        stockLevel.setAvailableStock(0);
        stockLevel.setReservedStock(0);
        stockLevel.setLockedStock(0);
        stockLevel.setMinStockLevel(medicine.getMinStockLevel());
        stockLevel.setMaxStockLevel(medicine.getMaxStockLevel());
        stockLevel.setSafetyStockLevel(medicine.getSafetyStockLevel());
        stockLevel.setStatus("正常");
        return stockLevel;
    }

    private StockLevel aggregateStockLevels(Medicine medicine, List<InventoryLevel> inventoryLevels) {
        StockLevel stockLevel = new StockLevel();
        stockLevel.setMedicineId(medicine.getId());
        stockLevel.setMedicineCode(medicine.getMedicineCode());
        stockLevel.setMedicineName(medicine.getName());
        stockLevel.setMinStockLevel(medicine.getMinStockLevel());
        stockLevel.setMaxStockLevel(medicine.getMaxStockLevel());
        stockLevel.setSafetyStockLevel(medicine.getSafetyStockLevel());

        // 汇总各批次数据
        int totalCurrentStock = 0;
        int totalAvailableStock = 0;
        int totalReservedStock = 0;
        int totalLockedStock = 0;
        BigDecimal totalInventoryCost = BigDecimal.ZERO;
        LocalDate earliestExpiryDate = null;

        for (InventoryLevel level : inventoryLevels) {
            totalCurrentStock += level.getCurrentStock() != null ? level.getCurrentStock() : 0;
            totalAvailableStock += level.getAvailableStock() != null ? level.getAvailableStock() : 0;
            totalReservedStock += level.getReservedStock() != null ? level.getReservedStock() : 0;
            totalLockedStock += level.getLockedStock() != null ? level.getLockedStock() : 0;
            
            if (level.getInventoryCost() != null) {
                totalInventoryCost = totalInventoryCost.add(level.getInventoryCost());
            }
            
            if (level.getExpiryDate() != null && 
                (earliestExpiryDate == null || level.getExpiryDate().isBefore(earliestExpiryDate))) {
                earliestExpiryDate = level.getExpiryDate();
            }
        }

        stockLevel.setCurrentStock(totalCurrentStock);
        stockLevel.setAvailableStock(totalAvailableStock);
        stockLevel.setReservedStock(totalReservedStock);
        stockLevel.setLockedStock(totalLockedStock);
        stockLevel.setInventoryCost(totalInventoryCost);
        stockLevel.setExpiryDate(earliestExpiryDate);
        
        if (earliestExpiryDate != null) {
            stockLevel.setRemainingShelfLifeDays((long) LocalDate.now().until(earliestExpiryDate).getDays());
        }

        stockLevel.setStatus("正常");
        return stockLevel;
    }

    private StockLevel convertToStockLevel(InventoryLevel inventoryLevel) {
        Medicine medicine = medicineMapper.selectById(inventoryLevel.getMedicineId());
        return convertToStockLevel(medicine, inventoryLevel);
    }

    private StockLevel convertToStockLevel(Medicine medicine, InventoryLevel inventoryLevel) {
        StockLevel stockLevel = new StockLevel();
        stockLevel.setMedicineId(medicine.getId());
        stockLevel.setMedicineCode(medicine.getMedicineCode());
        stockLevel.setMedicineName(medicine.getName());
        stockLevel.setBatchNumber(inventoryLevel.getBatchNumber());
        stockLevel.setCurrentStock(inventoryLevel.getCurrentStock());
        stockLevel.setAvailableStock(inventoryLevel.getAvailableStock());
        stockLevel.setReservedStock(inventoryLevel.getReservedStock());
        stockLevel.setLockedStock(inventoryLevel.getLockedStock());
        stockLevel.setMinStockLevel(medicine.getMinStockLevel());
        stockLevel.setMaxStockLevel(medicine.getMaxStockLevel());
        stockLevel.setSafetyStockLevel(medicine.getSafetyStockLevel());
        stockLevel.setPurchasePrice(inventoryLevel.getPurchasePrice());
        stockLevel.setInventoryCost(inventoryLevel.getInventoryCost());
        stockLevel.setExpiryDate(inventoryLevel.getExpiryDate());
        stockLevel.setRemainingShelfLifeDays(inventoryLevel.getRemainingShelfLifeDays());
        stockLevel.setStatus(inventoryLevel.getStatus());
        stockLevel.setStorageLocation(inventoryLevel.getStorageLocation());
        stockLevel.setSupplierName(inventoryLevel.getSupplierName());
        return stockLevel;
    }
}