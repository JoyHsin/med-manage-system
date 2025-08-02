package org.me.joy.clinic.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.me.joy.clinic.dto.*;
import org.me.joy.clinic.entity.InventoryLevel;
import org.me.joy.clinic.entity.Medicine;
import org.me.joy.clinic.entity.StockTransaction;
import org.me.joy.clinic.exception.BusinessException;
import org.me.joy.clinic.mapper.InventoryLevelMapper;
import org.me.joy.clinic.mapper.MedicineMapper;
import org.me.joy.clinic.mapper.StockTransactionMapper;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * 库存管理服务实现类单元测试
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("库存管理服务测试")
class InventoryManagementServiceImplTest {

    @Mock
    private MedicineMapper medicineMapper;

    @Mock
    private StockTransactionMapper stockTransactionMapper;

    @Mock
    private InventoryLevelMapper inventoryLevelMapper;

    @InjectMocks
    private InventoryManagementServiceImpl inventoryManagementService;

    private Medicine testMedicine;
    private CreateMedicineRequest createRequest;
    private UpdateMedicineRequest updateRequest;
    private StockUpdateRequest stockUpdateRequest;

    @BeforeEach
    void setUp() {
        testMedicine = new Medicine();
        testMedicine.setId(1L);
        testMedicine.setMedicineCode("MED001");
        testMedicine.setName("阿莫西林胶囊");
        testMedicine.setCategory("处方药");
        testMedicine.setUnit("盒");
        testMedicine.setPurchasePrice(new BigDecimal("15.50"));
        testMedicine.setSellingPrice(new BigDecimal("18.80"));
        testMedicine.setMinStockLevel(10);
        testMedicine.setMaxStockLevel(100);
        testMedicine.setEnabled(true);

        createRequest = new CreateMedicineRequest();
        createRequest.setMedicineCode("MED002");
        createRequest.setName("布洛芬片");
        createRequest.setCategory("非处方药");
        createRequest.setUnit("盒");
        createRequest.setPurchasePrice(new BigDecimal("12.00"));
        createRequest.setSellingPrice(new BigDecimal("15.00"));
        createRequest.setMinStockLevel(20);
        createRequest.setMaxStockLevel(200);

        updateRequest = new UpdateMedicineRequest();
        updateRequest.setName("阿莫西林胶囊（更新）");
        updateRequest.setPurchasePrice(new BigDecimal("16.00"));

        stockUpdateRequest = new StockUpdateRequest();
        stockUpdateRequest.setTransactionType("入库");
        stockUpdateRequest.setQuantity(50);
        stockUpdateRequest.setUnitPrice(new BigDecimal("15.50"));
        stockUpdateRequest.setBatchNumber("BATCH001");
        stockUpdateRequest.setExpiryDate(LocalDate.now().plusMonths(12));
    }

    @Test
    @DisplayName("创建药品成功")
    void testCreateMedicineSuccess() {
        when(medicineMapper.findByMedicineCode(createRequest.getMedicineCode())).thenReturn(null);
        when(medicineMapper.insert(any(Medicine.class))).thenReturn(1);

        Medicine result = inventoryManagementService.createMedicine(createRequest);

        assertNotNull(result);
        assertEquals(createRequest.getMedicineCode(), result.getMedicineCode());
        assertEquals(createRequest.getName(), result.getName());
        assertEquals(createRequest.getCategory(), result.getCategory());
        assertTrue(result.getEnabled());

        verify(medicineMapper).findByMedicineCode(createRequest.getMedicineCode());
        verify(medicineMapper).insert(any(Medicine.class));
    }

    @Test
    @DisplayName("创建药品失败 - 药品编码已存在")
    void testCreateMedicineFailureDuplicateCode() {
        when(medicineMapper.findByMedicineCode(createRequest.getMedicineCode())).thenReturn(testMedicine);

        BusinessException exception = assertThrows(BusinessException.class, 
            () -> inventoryManagementService.createMedicine(createRequest));

        assertTrue(exception.getMessage().contains("药品编码已存在"));
        verify(medicineMapper).findByMedicineCode(createRequest.getMedicineCode());
        verify(medicineMapper, never()).insert(any(Medicine.class));
    }

    @Test
    @DisplayName("更新药品成功")
    void testUpdateMedicineSuccess() {
        when(medicineMapper.selectById(1L)).thenReturn(testMedicine);
        when(medicineMapper.updateById(any(Medicine.class))).thenReturn(1);

        Medicine result = inventoryManagementService.updateMedicine(1L, updateRequest);

        assertNotNull(result);
        assertEquals(updateRequest.getName(), result.getName());
        assertEquals(updateRequest.getPurchasePrice(), result.getPurchasePrice());

        verify(medicineMapper).selectById(1L);
        verify(medicineMapper).updateById(any(Medicine.class));
    }

    @Test
    @DisplayName("根据ID获取药品成功")
    void testGetMedicineByIdSuccess() {
        when(medicineMapper.selectById(1L)).thenReturn(testMedicine);

        Medicine result = inventoryManagementService.getMedicineById(1L);

        assertNotNull(result);
        assertEquals(testMedicine.getId(), result.getId());
        assertEquals(testMedicine.getName(), result.getName());

        verify(medicineMapper).selectById(1L);
    }

    @Test
    @DisplayName("根据ID获取药品失败 - 药品不存在")
    void testGetMedicineByIdFailureNotFound() {
        when(medicineMapper.selectById(1L)).thenReturn(null);

        BusinessException exception = assertThrows(BusinessException.class, 
            () -> inventoryManagementService.getMedicineById(1L));

        assertTrue(exception.getMessage().contains("药品不存在"));
        verify(medicineMapper).selectById(1L);
    }

    @Test
    @DisplayName("根据药品编码获取药品成功")
    void testGetMedicineByCodeSuccess() {
        when(medicineMapper.findByMedicineCode("MED001")).thenReturn(testMedicine);

        Medicine result = inventoryManagementService.getMedicineByCode("MED001");

        assertNotNull(result);
        assertEquals(testMedicine.getMedicineCode(), result.getMedicineCode());

        verify(medicineMapper).findByMedicineCode("MED001");
    }

    @Test
    @DisplayName("获取所有启用的药品")
    void testGetAllActiveMedicines() {
        List<Medicine> medicines = Arrays.asList(testMedicine);
        when(medicineMapper.findByEnabled(true)).thenReturn(medicines);

        List<Medicine> result = inventoryManagementService.getAllActiveMedicines();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testMedicine.getId(), result.get(0).getId());

        verify(medicineMapper).findByEnabled(true);
    }

    @Test
    @DisplayName("根据分类获取药品")
    void testGetMedicinesByCategory() {
        List<Medicine> medicines = Arrays.asList(testMedicine);
        when(medicineMapper.findByCategory("处方药")).thenReturn(medicines);

        List<Medicine> result = inventoryManagementService.getMedicinesByCategory("处方药");

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("处方药", result.get(0).getCategory());

        verify(medicineMapper).findByCategory("处方药");
    }

    @Test
    @DisplayName("获取处方药列表")
    void testGetPrescriptionMedicines() {
        List<Medicine> medicines = Arrays.asList(testMedicine);
        when(medicineMapper.findPrescriptionMedicines()).thenReturn(medicines);

        List<Medicine> result = inventoryManagementService.getPrescriptionMedicines();

        assertNotNull(result);
        assertEquals(1, result.size());

        verify(medicineMapper).findPrescriptionMedicines();
    }

    @Test
    @DisplayName("获取非处方药列表")
    void testGetOverTheCounterMedicines() {
        List<Medicine> medicines = Arrays.asList(testMedicine);
        when(medicineMapper.findOverTheCounterMedicines()).thenReturn(medicines);

        List<Medicine> result = inventoryManagementService.getOverTheCounterMedicines();

        assertNotNull(result);
        assertEquals(1, result.size());

        verify(medicineMapper).findOverTheCounterMedicines();
    }

    @Test
    @DisplayName("获取特殊管制药品列表")
    void testGetControlledSubstances() {
        List<Medicine> medicines = Arrays.asList(testMedicine);
        when(medicineMapper.findControlledSubstances()).thenReturn(medicines);

        List<Medicine> result = inventoryManagementService.getControlledSubstances();

        assertNotNull(result);
        assertEquals(1, result.size());

        verify(medicineMapper).findControlledSubstances();
    }

    @Test
    @DisplayName("更新库存成功 - 入库")
    void testUpdateStockSuccessInbound() {
        when(medicineMapper.selectById(1L)).thenReturn(testMedicine);
        when(inventoryLevelMapper.getTotalStockByMedicine(1L)).thenReturn(50);
        when(inventoryLevelMapper.findByMedicineIdAndBatchNumber(1L, "BATCH001")).thenReturn(null);
        when(stockTransactionMapper.insert(any(StockTransaction.class))).thenReturn(1);
        when(inventoryLevelMapper.insert(any(InventoryLevel.class))).thenReturn(1);

        assertDoesNotThrow(() -> inventoryManagementService.updateStock(1L, stockUpdateRequest));

        verify(medicineMapper).selectById(1L);
        verify(stockTransactionMapper).insert(any(StockTransaction.class));
        verify(inventoryLevelMapper).insert(any(InventoryLevel.class));
    }

    @Test
    @DisplayName("更新库存失败 - 库存不足")
    void testUpdateStockFailureInsufficientStock() {
        stockUpdateRequest.setTransactionType("出库");
        stockUpdateRequest.setQuantity(-100);

        when(medicineMapper.selectById(1L)).thenReturn(testMedicine);
        when(inventoryLevelMapper.getTotalStockByMedicine(1L)).thenReturn(50);

        BusinessException exception = assertThrows(BusinessException.class, 
            () -> inventoryManagementService.updateStock(1L, stockUpdateRequest));

        assertTrue(exception.getMessage().contains("库存不足"));
        verify(medicineMapper).selectById(1L);
        verify(stockTransactionMapper, never()).insert(any(StockTransaction.class));
    }

    @Test
    @DisplayName("获取需要补货的药品列表")
    void testGetLowStockMedicines() {
        List<Medicine> medicines = Arrays.asList(testMedicine);
        when(medicineMapper.findMedicinesNeedingRestock()).thenReturn(medicines);

        List<Medicine> result = inventoryManagementService.getLowStockMedicines();

        assertNotNull(result);
        assertEquals(1, result.size());

        verify(medicineMapper).findMedicinesNeedingRestock();
    }

    @Test
    @DisplayName("获取库存过多的药品列表")
    void testGetOverstockedMedicines() {
        List<Medicine> medicines = Arrays.asList(testMedicine);
        when(medicineMapper.findOverstockedMedicines()).thenReturn(medicines);

        List<Medicine> result = inventoryManagementService.getOverstockedMedicines();

        assertNotNull(result);
        assertEquals(1, result.size());

        verify(medicineMapper).findOverstockedMedicines();
    }

    @Test
    @DisplayName("获取即将过期的药品列表")
    void testGetExpiringMedicines() {
        InventoryLevel inventoryLevel = new InventoryLevel();
        inventoryLevel.setMedicineId(1L);
        List<InventoryLevel> levels = Arrays.asList(inventoryLevel);
        
        when(inventoryLevelMapper.findExpiringSoon(30)).thenReturn(levels);
        when(medicineMapper.selectById(1L)).thenReturn(testMedicine);

        List<Medicine> result = inventoryManagementService.getExpiringMedicines(30);

        assertNotNull(result);
        assertEquals(1, result.size());

        verify(inventoryLevelMapper).findExpiringSoon(30);
        verify(medicineMapper).selectById(1L);
    }

    @Test
    @DisplayName("获取已过期的药品列表")
    void testGetExpiredMedicines() {
        InventoryLevel inventoryLevel = new InventoryLevel();
        inventoryLevel.setMedicineId(1L);
        List<InventoryLevel> levels = Arrays.asList(inventoryLevel);
        
        when(inventoryLevelMapper.findExpiredStock()).thenReturn(levels);
        when(medicineMapper.selectById(1L)).thenReturn(testMedicine);

        List<Medicine> result = inventoryManagementService.getExpiredMedicines();

        assertNotNull(result);
        assertEquals(1, result.size());

        verify(inventoryLevelMapper).findExpiredStock();
        verify(medicineMapper).selectById(1L);
    }

    @Test
    @DisplayName("获取当前库存总量")
    void testGetCurrentStock() {
        when(inventoryLevelMapper.getTotalStockByMedicine(1L)).thenReturn(100);

        Integer result = inventoryManagementService.getCurrentStock(1L);

        assertEquals(100, result);
        verify(inventoryLevelMapper).getTotalStockByMedicine(1L);
    }

    @Test
    @DisplayName("获取可用库存总量")
    void testGetAvailableStock() {
        when(inventoryLevelMapper.getAvailableStockByMedicine(1L)).thenReturn(80);

        Integer result = inventoryManagementService.getAvailableStock(1L);

        assertEquals(80, result);
        verify(inventoryLevelMapper).getAvailableStockByMedicine(1L);
    }

    @Test
    @DisplayName("预留库存成功")
    void testReserveStockSuccess() {
        InventoryLevel inventoryLevel = new InventoryLevel();
        inventoryLevel.setId(1L);
        inventoryLevel.setMedicineId(1L);
        inventoryLevel.setCurrentStock(100);
        inventoryLevel.setAvailableStock(100);
        inventoryLevel.setReservedStock(0);
        
        List<InventoryLevel> levels = Arrays.asList(inventoryLevel);
        when(inventoryLevelMapper.findAvailableStockByMedicine(1L)).thenReturn(levels);
        when(inventoryLevelMapper.updateById(any(InventoryLevel.class))).thenReturn(1);

        boolean result = inventoryManagementService.reserveStock(1L, 30);

        assertTrue(result);
        verify(inventoryLevelMapper).findAvailableStockByMedicine(1L);
        verify(inventoryLevelMapper).updateById(any(InventoryLevel.class));
    }

    @Test
    @DisplayName("预留库存失败 - 可用库存不足")
    void testReserveStockFailureInsufficientStock() {
        InventoryLevel inventoryLevel = new InventoryLevel();
        inventoryLevel.setId(1L);
        inventoryLevel.setMedicineId(1L);
        inventoryLevel.setCurrentStock(100);
        inventoryLevel.setAvailableStock(20);
        inventoryLevel.setReservedStock(80);
        
        List<InventoryLevel> levels = Arrays.asList(inventoryLevel);
        when(inventoryLevelMapper.findAvailableStockByMedicine(1L)).thenReturn(levels);
        when(inventoryLevelMapper.updateById(any(InventoryLevel.class))).thenReturn(1);

        boolean result = inventoryManagementService.reserveStock(1L, 30);

        assertFalse(result);
        verify(inventoryLevelMapper).findAvailableStockByMedicine(1L);
        // The method will still call updateById for the partial reservation (20 out of 30)
        verify(inventoryLevelMapper).updateById(any(InventoryLevel.class));
    }

    @Test
    @DisplayName("释放预留库存")
    void testReleaseReservedStock() {
        InventoryLevel inventoryLevel = new InventoryLevel();
        inventoryLevel.setId(1L);
        inventoryLevel.setMedicineId(1L);
        inventoryLevel.setCurrentStock(100);
        inventoryLevel.setAvailableStock(70);
        inventoryLevel.setReservedStock(30);
        
        List<InventoryLevel> levels = Arrays.asList(inventoryLevel);
        when(inventoryLevelMapper.findByMedicineId(1L)).thenReturn(levels);
        when(inventoryLevelMapper.updateById(any(InventoryLevel.class))).thenReturn(1);

        assertDoesNotThrow(() -> inventoryManagementService.releaseReservedStock(1L, 20));

        verify(inventoryLevelMapper).findByMedicineId(1L);
        verify(inventoryLevelMapper).updateById(any(InventoryLevel.class));
    }

    @Test
    @DisplayName("启用药品")
    void testEnableMedicine() {
        when(medicineMapper.selectById(1L)).thenReturn(testMedicine);
        when(medicineMapper.updateById(any(Medicine.class))).thenReturn(1);

        assertDoesNotThrow(() -> inventoryManagementService.enableMedicine(1L));

        verify(medicineMapper).selectById(1L);
        verify(medicineMapper).updateById(any(Medicine.class));
    }

    @Test
    @DisplayName("禁用药品")
    void testDisableMedicine() {
        when(medicineMapper.selectById(1L)).thenReturn(testMedicine);
        when(medicineMapper.updateById(any(Medicine.class))).thenReturn(1);

        assertDoesNotThrow(() -> inventoryManagementService.disableMedicine(1L));

        verify(medicineMapper).selectById(1L);
        verify(medicineMapper).updateById(any(Medicine.class));
    }

    @Test
    @DisplayName("删除药品成功")
    void testDeleteMedicineSuccess() {
        when(medicineMapper.selectById(1L)).thenReturn(testMedicine);
        when(inventoryLevelMapper.getTotalStockByMedicine(1L)).thenReturn(0);
        when(medicineMapper.updateById(any(Medicine.class))).thenReturn(1);

        assertDoesNotThrow(() -> inventoryManagementService.deleteMedicine(1L));

        verify(medicineMapper).selectById(1L);
        verify(inventoryLevelMapper).getTotalStockByMedicine(1L);
        verify(medicineMapper).updateById(any(Medicine.class));
    }

    @Test
    @DisplayName("删除药品失败 - 仍有库存")
    void testDeleteMedicineFailureHasStock() {
        when(medicineMapper.selectById(1L)).thenReturn(testMedicine);
        when(inventoryLevelMapper.getTotalStockByMedicine(1L)).thenReturn(50);

        BusinessException exception = assertThrows(BusinessException.class, 
            () -> inventoryManagementService.deleteMedicine(1L));

        assertTrue(exception.getMessage().contains("药品仍有库存"));
        verify(medicineMapper).selectById(1L);
        verify(inventoryLevelMapper).getTotalStockByMedicine(1L);
        verify(medicineMapper, never()).updateById(any(Medicine.class));
    }

    @Test
    @DisplayName("统计药品总数")
    void testCountActiveMedicines() {
        when(medicineMapper.countActiveMedicines()).thenReturn(100L);

        Long result = inventoryManagementService.countActiveMedicines();

        assertEquals(100L, result);
        verify(medicineMapper).countActiveMedicines();
    }

    @Test
    @DisplayName("获取库存总价值")
    void testGetTotalInventoryValue() {
        when(inventoryLevelMapper.getTotalInventoryValue()).thenReturn(50000.0);

        Double result = inventoryManagementService.getTotalInventoryValue();

        assertEquals(50000.0, result);
        verify(inventoryLevelMapper).getTotalInventoryValue();
    }

    @Test
    @DisplayName("获取指定药品的库存价值")
    void testGetInventoryValueByMedicine() {
        when(inventoryLevelMapper.getInventoryValueByMedicine(1L)).thenReturn(1500.0);

        Double result = inventoryManagementService.getInventoryValueByMedicine(1L);

        assertEquals(1500.0, result);
        verify(inventoryLevelMapper).getInventoryValueByMedicine(1L);
    }
}