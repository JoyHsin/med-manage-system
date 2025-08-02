package org.me.joy.clinic.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 库存水平实体类单元测试
 */
@DisplayName("库存水平实体测试")
class InventoryLevelTest {

    private Validator validator;
    private InventoryLevel inventoryLevel;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
        
        inventoryLevel = new InventoryLevel();
        inventoryLevel.setMedicineId(1L);
        inventoryLevel.setBatchNumber("BATCH001");
        inventoryLevel.setCurrentStock(100);
        inventoryLevel.setAvailableStock(100);
        inventoryLevel.setPurchasePrice(new BigDecimal("15.50"));
        inventoryLevel.setExpiryDate(LocalDate.now().plusMonths(12));
        inventoryLevel.setStatus("正常");
    }

    @Test
    @DisplayName("创建有效库存水平实体")
    void testCreateValidInventoryLevel() {
        Set<ConstraintViolation<InventoryLevel>> violations = validator.validate(inventoryLevel);
        assertTrue(violations.isEmpty(), "有效的库存水平实体不应有验证错误");
    }

    @Test
    @DisplayName("药品ID不能为空")
    void testMedicineIdNotNull() {
        inventoryLevel.setMedicineId(null);
        Set<ConstraintViolation<InventoryLevel>> violations = validator.validate(inventoryLevel);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("药品ID不能为空")));
    }

    @Test
    @DisplayName("批次号不能为空")
    void testBatchNumberNotBlank() {
        inventoryLevel.setBatchNumber("");
        Set<ConstraintViolation<InventoryLevel>> violations = validator.validate(inventoryLevel);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("批次号不能为空")));
    }

    @Test
    @DisplayName("批次号长度限制")
    void testBatchNumberMaxLength() {
        inventoryLevel.setBatchNumber("A".repeat(101));
        Set<ConstraintViolation<InventoryLevel>> violations = validator.validate(inventoryLevel);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("批次号长度不能超过100个字符")));
    }

    @Test
    @DisplayName("当前库存数量不能为负数")
    void testCurrentStockNotNegative() {
        inventoryLevel.setCurrentStock(-1);
        Set<ConstraintViolation<InventoryLevel>> violations = validator.validate(inventoryLevel);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("当前库存数量不能为负数")));
    }

    @Test
    @DisplayName("可用库存数量不能为负数")
    void testAvailableStockNotNegative() {
        inventoryLevel.setAvailableStock(-1);
        Set<ConstraintViolation<InventoryLevel>> violations = validator.validate(inventoryLevel);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("可用库存数量不能为负数")));
    }

    @Test
    @DisplayName("进价不能为负数")
    void testPurchasePriceNotNegative() {
        inventoryLevel.setPurchasePrice(new BigDecimal("-1.00"));
        Set<ConstraintViolation<InventoryLevel>> violations = validator.validate(inventoryLevel);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("进价不能为负数")));
    }

    @Test
    @DisplayName("库存状态验证")
    void testStatusValidation() {
        inventoryLevel.setStatus("无效状态");
        Set<ConstraintViolation<InventoryLevel>> violations = validator.validate(inventoryLevel);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("库存状态只能是")));
    }

    @Test
    @DisplayName("有效的库存状态")
    void testValidStatuses() {
        String[] validStatuses = {"正常", "预警", "过期", "损坏", "冻结"};
        
        for (String status : validStatuses) {
            inventoryLevel.setStatus(status);
            Set<ConstraintViolation<InventoryLevel>> violations = validator.validate(inventoryLevel);
            assertTrue(violations.isEmpty(), "库存状态 '" + status + "' 应该是有效的");
        }
    }

    @Test
    @DisplayName("设置当前库存时自动更新可用库存")
    void testSetCurrentStockUpdatesAvailableStock() {
        inventoryLevel.setReservedStock(10);
        inventoryLevel.setLockedStock(5);
        inventoryLevel.setCurrentStock(100);
        
        assertEquals(85, inventoryLevel.getAvailableStock());
    }

    @Test
    @DisplayName("设置预留库存时自动更新可用库存")
    void testSetReservedStockUpdatesAvailableStock() {
        inventoryLevel.setCurrentStock(100);
        inventoryLevel.setLockedStock(5);
        inventoryLevel.setReservedStock(20);
        
        assertEquals(75, inventoryLevel.getAvailableStock());
    }

    @Test
    @DisplayName("设置锁定库存时自动更新可用库存")
    void testSetLockedStockUpdatesAvailableStock() {
        inventoryLevel.setCurrentStock(100);
        inventoryLevel.setReservedStock(10);
        inventoryLevel.setLockedStock(15);
        
        assertEquals(75, inventoryLevel.getAvailableStock());
    }

    @Test
    @DisplayName("设置进价时自动计算库存成本")
    void testSetPurchasePriceCalculatesInventoryCost() {
        inventoryLevel.setCurrentStock(50);
        inventoryLevel.setPurchasePrice(new BigDecimal("10.00"));
        
        assertEquals(new BigDecimal("500.00"), inventoryLevel.getInventoryCost());
    }

    @Test
    @DisplayName("增加库存")
    void testAddStock() {
        inventoryLevel.setCurrentStock(50);
        inventoryLevel.setPurchasePrice(new BigDecimal("10.00"));
        
        inventoryLevel.addStock(30);
        
        assertEquals(80, inventoryLevel.getCurrentStock());
        assertEquals(80, inventoryLevel.getAvailableStock());
        assertEquals(new BigDecimal("800.00"), inventoryLevel.getInventoryCost());
        assertNotNull(inventoryLevel.getLastInboundDate());
    }

    @Test
    @DisplayName("减少库存")
    void testReduceStock() {
        inventoryLevel.setCurrentStock(50);
        inventoryLevel.setPurchasePrice(new BigDecimal("10.00"));
        
        inventoryLevel.reduceStock(20);
        
        assertEquals(30, inventoryLevel.getCurrentStock());
        assertEquals(30, inventoryLevel.getAvailableStock());
        assertEquals(new BigDecimal("300.00"), inventoryLevel.getInventoryCost());
        assertNotNull(inventoryLevel.getLastOutboundDate());
    }

    @Test
    @DisplayName("减少库存不能低于零")
    void testReduceStockNotBelowZero() {
        inventoryLevel.setCurrentStock(20);
        
        inventoryLevel.reduceStock(30);
        
        assertEquals(0, inventoryLevel.getCurrentStock());
        assertEquals(0, inventoryLevel.getAvailableStock());
    }

    @Test
    @DisplayName("预留库存成功")
    void testReserveStockSuccess() {
        inventoryLevel.setCurrentStock(100);
        inventoryLevel.setAvailableStock(100);
        
        boolean result = inventoryLevel.reserveStock(30);
        
        assertTrue(result);
        assertEquals(30, inventoryLevel.getReservedStock());
        assertEquals(70, inventoryLevel.getAvailableStock());
    }

    @Test
    @DisplayName("预留库存失败 - 可用库存不足")
    void testReserveStockFailure() {
        inventoryLevel.setCurrentStock(100);
        inventoryLevel.setAvailableStock(20);
        
        boolean result = inventoryLevel.reserveStock(30);
        
        assertFalse(result);
        assertEquals(0, inventoryLevel.getReservedStock());
        assertEquals(20, inventoryLevel.getAvailableStock());
    }

    @Test
    @DisplayName("释放预留库存")
    void testReleaseReservedStock() {
        inventoryLevel.setCurrentStock(100);
        inventoryLevel.setReservedStock(30);
        inventoryLevel.setAvailableStock(70);
        
        inventoryLevel.releaseReservedStock(20);
        
        assertEquals(10, inventoryLevel.getReservedStock());
        assertEquals(90, inventoryLevel.getAvailableStock());
    }

    @Test
    @DisplayName("检查是否库存不足")
    void testIsLowStock() {
        assertTrue(inventoryLevel.isLowStock(150), "当前库存低于最小水平时应返回true");
        assertFalse(inventoryLevel.isLowStock(50), "当前库存高于最小水平时应返回false");
        assertFalse(inventoryLevel.isLowStock(null), "最小水平为null时应返回false");
        
        inventoryLevel.setCurrentStock(null);
        assertFalse(inventoryLevel.isLowStock(50), "当前库存为null时应返回false");
    }

    @Test
    @DisplayName("检查是否即将过期")
    void testIsExpiringSoon() {
        inventoryLevel.setExpiryDate(LocalDate.now().plusDays(15));
        assertTrue(inventoryLevel.isExpiringSoon());
        
        inventoryLevel.setExpiryDate(LocalDate.now().plusDays(45));
        assertFalse(inventoryLevel.isExpiringSoon());
        
        inventoryLevel.setExpiryDate(null);
        assertFalse(inventoryLevel.isExpiringSoon());
    }

    @Test
    @DisplayName("检查是否已过期")
    void testIsExpired() {
        inventoryLevel.setExpiryDate(LocalDate.now().minusDays(1));
        assertTrue(inventoryLevel.isExpired());
        
        inventoryLevel.setExpiryDate(LocalDate.now().plusDays(1));
        assertFalse(inventoryLevel.isExpired());
        
        inventoryLevel.setExpiryDate(null);
        assertFalse(inventoryLevel.isExpired());
    }

    @Test
    @DisplayName("检查是否可用")
    void testIsAvailable() {
        inventoryLevel.setStatus("正常");
        inventoryLevel.setExpiryDate(LocalDate.now().plusDays(30));
        inventoryLevel.setAvailableStock(50);
        assertTrue(inventoryLevel.isAvailable());
        
        inventoryLevel.setStatus("损坏");
        assertFalse(inventoryLevel.isAvailable());
        
        inventoryLevel.setStatus("正常");
        inventoryLevel.setExpiryDate(LocalDate.now().minusDays(1));
        assertFalse(inventoryLevel.isAvailable());
        
        inventoryLevel.setExpiryDate(LocalDate.now().plusDays(30));
        inventoryLevel.setAvailableStock(0);
        assertFalse(inventoryLevel.isAvailable());
    }

    @Test
    @DisplayName("计算剩余保质期天数")
    void testGetRemainingShelfLifeDays() {
        inventoryLevel.setExpiryDate(LocalDate.now().plusDays(30));
        assertEquals(30L, inventoryLevel.getRemainingShelfLifeDays());
        
        inventoryLevel.setExpiryDate(LocalDate.now().minusDays(5));
        assertEquals(-5L, inventoryLevel.getRemainingShelfLifeDays());
        
        inventoryLevel.setExpiryDate(null);
        assertNull(inventoryLevel.getRemainingShelfLifeDays());
    }

    @Test
    @DisplayName("库存水平实体相等性测试")
    void testEquality() {
        InventoryLevel level1 = new InventoryLevel();
        level1.setMedicineId(1L);
        level1.setBatchNumber("BATCH001");
        
        InventoryLevel level2 = new InventoryLevel();
        level2.setMedicineId(1L);
        level2.setBatchNumber("BATCH001");
        
        InventoryLevel level3 = new InventoryLevel();
        level3.setMedicineId(2L);
        level3.setBatchNumber("BATCH001");
        
        assertEquals(level1, level2, "相同药品ID和批次号的库存水平应相等");
        assertNotEquals(level1, level3, "不同药品ID的库存水平不应相等");
        assertEquals(level1.hashCode(), level2.hashCode(), "相等的库存水平应有相同的hashCode");
    }

    @Test
    @DisplayName("库存水平实体toString测试")
    void testToString() {
        inventoryLevel.setId(1L);
        String toString = inventoryLevel.toString();
        
        assertTrue(toString.contains("InventoryLevel{"));
        assertTrue(toString.contains("id=1"));
        assertTrue(toString.contains("medicineId=1"));
        assertTrue(toString.contains("batchNumber='BATCH001'"));
        assertTrue(toString.contains("currentStock=100"));
        assertTrue(toString.contains("availableStock=100"));
        assertTrue(toString.contains("status='正常'"));
    }

    @Test
    @DisplayName("构造函数测试")
    void testConstructors() {
        InventoryLevel defaultLevel = new InventoryLevel();
        assertNull(defaultLevel.getMedicineId());
        assertNull(defaultLevel.getBatchNumber());
        assertNull(defaultLevel.getCurrentStock());
        
        InventoryLevel parameterizedLevel = new InventoryLevel(2L, "BATCH002", 150);
        assertEquals(2L, parameterizedLevel.getMedicineId());
        assertEquals("BATCH002", parameterizedLevel.getBatchNumber());
        assertEquals(150, parameterizedLevel.getCurrentStock());
        assertEquals(150, parameterizedLevel.getAvailableStock());
        assertNotNull(parameterizedLevel.getLastInboundDate());
    }

    @Test
    @DisplayName("默认值测试")
    void testDefaultValues() {
        InventoryLevel newLevel = new InventoryLevel();
        assertEquals(0, newLevel.getReservedStock());
        assertEquals(0, newLevel.getLockedStock());
        assertEquals("正常", newLevel.getStatus());
    }
}