package org.me.joy.clinic.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.me.joy.clinic.dto.*;
import org.me.joy.clinic.entity.InventoryLevel;
import org.me.joy.clinic.entity.Medicine;
import org.me.joy.clinic.entity.StockTransaction;
import org.me.joy.clinic.exception.BusinessException;
import org.me.joy.clinic.exception.ValidationException;
import org.me.joy.clinic.mapper.InventoryLevelMapper;
import org.me.joy.clinic.mapper.MedicineMapper;
import org.me.joy.clinic.mapper.StockTransactionMapper;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * 库存管理服务测试类
 */
@ExtendWith(MockitoExtension.class)
class StockManagementServiceImplTest {

    @Mock
    private MedicineMapper medicineMapper;

    @Mock
    private InventoryLevelMapper inventoryLevelMapper;

    @Mock
    private StockTransactionMapper stockTransactionMapper;

    @InjectMocks
    private StockManagementServiceImpl stockManagementService;

    private Medicine testMedicine;
    private InventoryLevel testInventoryLevel;
    private StockInRequest stockInRequest;
    private StockOutRequest stockOutRequest;

    @BeforeEach
    void setUp() {
        // 创建测试药品
        testMedicine = new Medicine();
        testMedicine.setId(1L);
        testMedicine.setMedicineCode("MED001");
        testMedicine.setName("测试药品");
        testMedicine.setCategory("处方药");
        testMedicine.setUnit("盒");
        testMedicine.setMinStockLevel(10);
        testMedicine.setMaxStockLevel(100);
        testMedicine.setSafetyStockLevel(20);
        testMedicine.setEnabled(true);

        // 创建测试库存水平
        testInventoryLevel = new InventoryLevel();
        testInventoryLevel.setId(1L);
        testInventoryLevel.setMedicineId(1L);
        testInventoryLevel.setBatchNumber("BATCH001");
        testInventoryLevel.setCurrentStock(50);
        testInventoryLevel.setAvailableStock(50);
        testInventoryLevel.setPurchasePrice(new BigDecimal("10.00"));
        testInventoryLevel.setExpiryDate(LocalDate.now().plusMonths(6));
        testInventoryLevel.setStatus("正常");

        // 创建入库请求
        stockInRequest = new StockInRequest();
        stockInRequest.setMedicineId(1L);
        stockInRequest.setQuantity(20);
        stockInRequest.setBatchNumber("BATCH002");
        stockInRequest.setPurchasePrice(new BigDecimal("12.00"));
        stockInRequest.setExpiryDate(LocalDate.now().plusMonths(12));
        stockInRequest.setSupplierName("测试供应商");

        // 创建出库请求
        stockOutRequest = new StockOutRequest();
        stockOutRequest.setMedicineId(1L);
        stockOutRequest.setQuantity(10);
        stockOutRequest.setBatchNumber("BATCH001");
        stockOutRequest.setOutboundType("销售");
    }

    @Test
    void testRecordStockIn_Success() {
        // 准备数据
        when(medicineMapper.selectById(1L)).thenReturn(testMedicine);
        when(inventoryLevelMapper.selectOne(any(QueryWrapper.class))).thenReturn(null);
        when(inventoryLevelMapper.insert(any(InventoryLevel.class))).thenReturn(1);
        when(stockTransactionMapper.insert(any(StockTransaction.class))).thenReturn(1);

        // 执行测试
        assertDoesNotThrow(() -> stockManagementService.recordStockIn(stockInRequest));

        // 验证调用
        verify(medicineMapper).selectById(1L);
        verify(inventoryLevelMapper).selectOne(any(QueryWrapper.class));
        verify(inventoryLevelMapper).insert(any(InventoryLevel.class));
        verify(stockTransactionMapper).insert(any(StockTransaction.class));
    }

    @Test
    void testRecordStockIn_MedicineNotFound() {
        // 准备数据
        when(medicineMapper.selectById(1L)).thenReturn(null);

        // 执行测试并验证异常
        ValidationException exception = assertThrows(ValidationException.class, 
            () -> stockManagementService.recordStockIn(stockInRequest));
        
        assertEquals("药品不存在，ID: 1", exception.getMessage());
    }

    @Test
    void testRecordStockIn_ExpiredMedicine() {
        // 准备数据
        stockInRequest.setExpiryDate(LocalDate.now().minusDays(1)); // 设置为已过期
        when(medicineMapper.selectById(1L)).thenReturn(testMedicine);

        // 执行测试并验证异常
        ValidationException exception = assertThrows(ValidationException.class, 
            () -> stockManagementService.recordStockIn(stockInRequest));
        
        assertEquals("不能入库已过期的药品", exception.getMessage());
    }

    @Test
    void testRecordStockIn_ExistingBatch() {
        // 准备数据
        stockInRequest.setBatchNumber("BATCH001"); // 使用已存在的批次号
        when(medicineMapper.selectById(1L)).thenReturn(testMedicine);
        when(inventoryLevelMapper.selectOne(any(QueryWrapper.class))).thenReturn(testInventoryLevel);
        when(inventoryLevelMapper.updateById(any(InventoryLevel.class))).thenReturn(1);
        when(stockTransactionMapper.insert(any(StockTransaction.class))).thenReturn(1);

        // 执行测试
        assertDoesNotThrow(() -> stockManagementService.recordStockIn(stockInRequest));

        // 验证库存增加
        verify(inventoryLevelMapper).updateById(any(InventoryLevel.class));
    }

    @Test
    void testRecordStockOut_Success() {
        // 准备数据
        when(medicineMapper.selectById(1L)).thenReturn(testMedicine);
        when(inventoryLevelMapper.selectOne(any(QueryWrapper.class))).thenReturn(testInventoryLevel);
        when(inventoryLevelMapper.updateById(any(InventoryLevel.class))).thenReturn(1);
        when(stockTransactionMapper.insert(any(StockTransaction.class))).thenReturn(1);

        // 执行测试
        assertDoesNotThrow(() -> stockManagementService.recordStockOut(stockOutRequest));

        // 验证调用
        verify(medicineMapper).selectById(1L);
        verify(inventoryLevelMapper).selectOne(any(QueryWrapper.class));
        verify(inventoryLevelMapper).updateById(any(InventoryLevel.class));
        verify(stockTransactionMapper).insert(any(StockTransaction.class));
    }

    @Test
    void testRecordStockOut_InsufficientStock() {
        // 准备数据
        stockOutRequest.setQuantity(100); // 超过可用库存
        when(medicineMapper.selectById(1L)).thenReturn(testMedicine);
        when(inventoryLevelMapper.selectOne(any(QueryWrapper.class))).thenReturn(testInventoryLevel);

        // 执行测试并验证异常
        BusinessException exception = assertThrows(BusinessException.class, 
            () -> stockManagementService.recordStockOut(stockOutRequest));
        
        assertTrue(exception.getMessage().contains("库存不足"));
    }

    @Test
    void testRecordStockOut_BatchNotFound() {
        // 准备数据
        when(medicineMapper.selectById(1L)).thenReturn(testMedicine);
        when(inventoryLevelMapper.selectOne(any(QueryWrapper.class))).thenReturn(null);

        // 执行测试并验证异常
        BusinessException exception = assertThrows(BusinessException.class, 
            () -> stockManagementService.recordStockOut(stockOutRequest));
        
        assertEquals("指定批次的库存不存在", exception.getMessage());
    }

    @Test
    void testGetCurrentStockLevel_Success() {
        // 准备数据
        when(medicineMapper.selectById(1L)).thenReturn(testMedicine);
        when(inventoryLevelMapper.selectList(any(QueryWrapper.class)))
            .thenReturn(Collections.singletonList(testInventoryLevel));

        // 执行测试
        StockLevel result = stockManagementService.getCurrentStockLevel(1L);

        // 验证结果
        assertNotNull(result);
        assertEquals(1L, result.getMedicineId());
        assertEquals("MED001", result.getMedicineCode());
        assertEquals("测试药品", result.getMedicineName());
        assertEquals(50, result.getCurrentStock());
        assertEquals(50, result.getAvailableStock());
    }

    @Test
    void testGetCurrentStockLevel_MedicineNotFound() {
        // 准备数据
        when(medicineMapper.selectById(1L)).thenReturn(null);

        // 执行测试并验证异常
        ValidationException exception = assertThrows(ValidationException.class, 
            () -> stockManagementService.getCurrentStockLevel(1L));
        
        assertEquals("药品不存在，ID: 1", exception.getMessage());
    }

    @Test
    void testGetCurrentStockLevel_EmptyStock() {
        // 准备数据
        when(medicineMapper.selectById(1L)).thenReturn(testMedicine);
        when(inventoryLevelMapper.selectList(any(QueryWrapper.class)))
            .thenReturn(Collections.emptyList());

        // 执行测试
        StockLevel result = stockManagementService.getCurrentStockLevel(1L);

        // 验证结果
        assertNotNull(result);
        assertEquals(1L, result.getMedicineId());
        assertEquals(0, result.getCurrentStock());
        assertEquals(0, result.getAvailableStock());
    }

    @Test
    void testGetCurrentStockLevelWithBatch_Success() {
        // 准备数据
        when(medicineMapper.selectById(1L)).thenReturn(testMedicine);
        when(inventoryLevelMapper.selectOne(any(QueryWrapper.class))).thenReturn(testInventoryLevel);

        // 执行测试
        StockLevel result = stockManagementService.getCurrentStockLevel(1L, "BATCH001");

        // 验证结果
        assertNotNull(result);
        assertEquals(1L, result.getMedicineId());
        assertEquals("BATCH001", result.getBatchNumber());
        assertEquals(50, result.getCurrentStock());
    }

    @Test
    void testGetLowStockItems() {
        // 准备数据
        testMedicine.setMinStockLevel(60); // 设置最小库存为60，当前库存50，应该被识别为库存不足
        when(medicineMapper.selectList(any(QueryWrapper.class)))
            .thenReturn(Collections.singletonList(testMedicine));
        when(medicineMapper.selectById(1L)).thenReturn(testMedicine); // 添加这个mock
        when(inventoryLevelMapper.selectList(any(QueryWrapper.class)))
            .thenReturn(Collections.singletonList(testInventoryLevel));

        // 执行测试
        List<StockLevel> result = stockManagementService.getLowStockItems();

        // 验证结果
        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.get(0).isLowStock());
    }

    @Test
    void testGetExpiringSoonItems() {
        // 准备数据
        testInventoryLevel.setExpiryDate(LocalDate.now().plusDays(15)); // 15天后过期
        when(inventoryLevelMapper.selectList(any(QueryWrapper.class)))
            .thenReturn(Collections.singletonList(testInventoryLevel));
        when(medicineMapper.selectById(1L)).thenReturn(testMedicine);

        // 执行测试
        List<StockLevel> result = stockManagementService.getExpiringSoonItems(30);

        // 验证结果
        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void testGetExpiredItems() {
        // 准备数据
        testInventoryLevel.setExpiryDate(LocalDate.now().minusDays(1)); // 已过期
        when(inventoryLevelMapper.selectList(any(QueryWrapper.class)))
            .thenReturn(Collections.singletonList(testInventoryLevel));
        when(medicineMapper.selectById(1L)).thenReturn(testMedicine);

        // 执行测试
        List<StockLevel> result = stockManagementService.getExpiredItems();

        // 验证结果
        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void testPerformStockTaking_WithDifference() {
        // 准备数据
        StockTakingRequest.StockTakingItem item = new StockTakingRequest.StockTakingItem();
        item.setMedicineId(1L);
        item.setBatchNumber("BATCH001");
        item.setBookStock(50);
        item.setActualStock(45); // 实际库存少于账面库存
        item.setDifferenceReason("盘点发现差异");

        StockTakingRequest request = new StockTakingRequest();
        request.setInventoryDate(LocalDate.now());
        request.setInventoryType("全盘");
        request.setInventoryStaff("张三");
        request.setItems(Collections.singletonList(item));

        when(inventoryLevelMapper.selectOne(any(QueryWrapper.class))).thenReturn(testInventoryLevel);
        when(stockTransactionMapper.insert(any(StockTransaction.class))).thenReturn(1);
        when(inventoryLevelMapper.updateById(any(InventoryLevel.class))).thenReturn(1);

        // 执行测试
        assertDoesNotThrow(() -> stockManagementService.performStockTaking(request));

        // 验证调用
        verify(stockTransactionMapper).insert(any(StockTransaction.class));
        verify(inventoryLevelMapper).updateById(any(InventoryLevel.class));
    }

    @Test
    void testPerformStockTaking_NoDifference() {
        // 准备数据
        StockTakingRequest.StockTakingItem item = new StockTakingRequest.StockTakingItem();
        item.setMedicineId(1L);
        item.setBatchNumber("BATCH001");
        item.setBookStock(50);
        item.setActualStock(50); // 实际库存等于账面库存

        StockTakingRequest request = new StockTakingRequest();
        request.setInventoryDate(LocalDate.now());
        request.setInventoryType("全盘");
        request.setInventoryStaff("张三");
        request.setItems(Collections.singletonList(item));

        when(inventoryLevelMapper.selectOne(any(QueryWrapper.class))).thenReturn(testInventoryLevel);
        when(inventoryLevelMapper.updateById(any(InventoryLevel.class))).thenReturn(1);

        // 执行测试
        assertDoesNotThrow(() -> stockManagementService.performStockTaking(request));

        // 验证只更新盘点日期，不创建交易记录
        verify(stockTransactionMapper, never()).insert(any(StockTransaction.class));
        verify(inventoryLevelMapper).updateById(any(InventoryLevel.class));
    }

    @Test
    void testReserveStock_Success() {
        // 准备数据
        when(inventoryLevelMapper.selectOne(any(QueryWrapper.class))).thenReturn(testInventoryLevel);
        when(inventoryLevelMapper.updateById(any(InventoryLevel.class))).thenReturn(1);

        // 执行测试
        boolean result = stockManagementService.reserveStock(1L, "BATCH001", 10);

        // 验证结果
        assertTrue(result);
        verify(inventoryLevelMapper).updateById(any(InventoryLevel.class));
    }

    @Test
    void testReserveStock_InsufficientStock() {
        // 准备数据
        when(inventoryLevelMapper.selectOne(any(QueryWrapper.class))).thenReturn(testInventoryLevel);

        // 执行测试（尝试预留超过可用库存的数量）
        boolean result = stockManagementService.reserveStock(1L, "BATCH001", 100);

        // 验证结果
        assertFalse(result);
        verify(inventoryLevelMapper, never()).updateById(any(InventoryLevel.class));
    }

    @Test
    void testGetTotalInventoryValue() {
        // 准备数据
        InventoryLevel level1 = new InventoryLevel();
        level1.setCurrentStock(50);
        level1.setPurchasePrice(new BigDecimal("10.00"));

        InventoryLevel level2 = new InventoryLevel();
        level2.setCurrentStock(30);
        level2.setPurchasePrice(new BigDecimal("20.00"));

        when(inventoryLevelMapper.selectList(any(QueryWrapper.class)))
            .thenReturn(Arrays.asList(level1, level2));

        // 执行测试
        Double result = stockManagementService.getTotalInventoryValue();

        // 验证结果（50*10 + 30*20 = 1100）
        assertEquals(1100.0, result, 0.01);
    }

    @Test
    void testGetInventoryAlerts() {
        // 准备数据
        testMedicine.setMinStockLevel(60); // 设置最小库存为60，当前库存50
        testInventoryLevel.setExpiryDate(LocalDate.now().plusDays(15)); // 15天后过期

        when(medicineMapper.selectList(any(QueryWrapper.class)))
            .thenReturn(Collections.singletonList(testMedicine));
        when(inventoryLevelMapper.selectList(any(QueryWrapper.class)))
            .thenReturn(Collections.singletonList(testInventoryLevel));
        when(medicineMapper.selectById(1L)).thenReturn(testMedicine);

        // 执行测试
        List<String> alerts = stockManagementService.getInventoryAlerts();

        // 验证结果
        assertNotNull(alerts);
        assertFalse(alerts.isEmpty());
        assertTrue(alerts.stream().anyMatch(alert -> alert.contains("库存不足警告")));
        assertTrue(alerts.stream().anyMatch(alert -> alert.contains("即将过期警告")));
    }

    @Test
    void testGetFirstInFirstOutBatch() {
        // 准备数据
        InventoryLevel level1 = new InventoryLevel();
        level1.setBatchNumber("BATCH001");
        level1.setAvailableStock(30);
        level1.setExpiryDate(LocalDate.now().plusMonths(3));
        level1.setStatus("正常");

        InventoryLevel level2 = new InventoryLevel();
        level2.setBatchNumber("BATCH002");
        level2.setAvailableStock(40);
        level2.setExpiryDate(LocalDate.now().plusMonths(6));
        level2.setStatus("正常");

        when(inventoryLevelMapper.selectList(any(QueryWrapper.class)))
            .thenReturn(Arrays.asList(level1, level2)); // level1先过期，应该先出库

        // 执行测试
        String result = stockManagementService.getFirstInFirstOutBatch(1L, 25);

        // 验证结果
        assertEquals("BATCH001", result); // 应该选择先过期的批次
    }

    @Test
    void testGetFirstInFirstOutBatch_InsufficientStock() {
        // 准备数据
        InventoryLevel level1 = new InventoryLevel();
        level1.setBatchNumber("BATCH001");
        level1.setAvailableStock(10);
        level1.setExpiryDate(LocalDate.now().plusMonths(3));
        level1.setStatus("正常");

        when(inventoryLevelMapper.selectList(any(QueryWrapper.class)))
            .thenReturn(Collections.singletonList(level1));

        // 执行测试（需要20，但只有10）
        String result = stockManagementService.getFirstInFirstOutBatch(1L, 20);

        // 验证结果
        assertNull(result); // 库存不足，返回null
    }

    @Test
    void testValidateStockTransaction() {
        // 准备数据
        StockLevel stockLevel = new StockLevel();
        stockLevel.setAvailableStock(50);
        
        when(medicineMapper.selectById(1L)).thenReturn(testMedicine);
        when(inventoryLevelMapper.selectOne(any(QueryWrapper.class))).thenReturn(testInventoryLevel);

        // 测试出库验证
        boolean result1 = stockManagementService.validateStockTransaction(1L, "BATCH001", 30, "出库");
        assertTrue(result1); // 库存充足，应该返回true

        boolean result2 = stockManagementService.validateStockTransaction(1L, "BATCH001", 100, "出库");
        assertFalse(result2); // 库存不足，应该返回false

        // 测试入库验证
        boolean result3 = stockManagementService.validateStockTransaction(1L, "BATCH001", 100, "入库");
        assertTrue(result3); // 入库操作默认有效
    }
}