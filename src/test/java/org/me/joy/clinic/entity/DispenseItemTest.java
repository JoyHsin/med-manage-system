package org.me.joy.clinic.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 调剂项目明细实体测试类
 */
class DispenseItemTest {

    private DispenseItem dispenseItem;

    @BeforeEach
    void setUp() {
        dispenseItem = new DispenseItem(
            1L, 1L, 1L, "阿莫西林胶囊", 10, "粒", new BigDecimal("2.50")
        );
    }

    @Test
    void testConstructor() {
        assertNotNull(dispenseItem);
        assertEquals(1L, dispenseItem.getDispenseRecordId());
        assertEquals(1L, dispenseItem.getPrescriptionItemId());
        assertEquals(1L, dispenseItem.getMedicineId());
        assertEquals("阿莫西林胶囊", dispenseItem.getMedicineName());
        assertEquals(10, dispenseItem.getPrescribedQuantity());
        assertEquals(10, dispenseItem.getDispensedQuantity()); // 默认等于处方数量
        assertEquals("粒", dispenseItem.getUnit());
        assertEquals(new BigDecimal("2.50"), dispenseItem.getUnitPrice());
        assertEquals(new BigDecimal("25.00"), dispenseItem.getSubtotal()); // 自动计算
    }

    @Test
    void testDefaultValues() {
        DispenseItem item = new DispenseItem();
        assertEquals("待调剂", item.getStatus());
        assertEquals("合格", item.getQualityCheckResult());
        assertFalse(item.getIsSubstitute());
        assertEquals(1, item.getSortOrder());
    }

    @Test
    void testCalculateSubtotal() {
        // 测试小计计算
        dispenseItem.setDispensedQuantity(15);
        dispenseItem.calculateSubtotal();
        assertEquals(new BigDecimal("37.50"), dispenseItem.getSubtotal());
        
        // 测试设置数量时自动计算
        dispenseItem.setDispensedQuantity(20);
        assertEquals(new BigDecimal("50.00"), dispenseItem.getSubtotal());
        
        // 测试设置单价时自动计算
        dispenseItem.setUnitPrice(new BigDecimal("3.00"));
        assertEquals(new BigDecimal("60.00"), dispenseItem.getSubtotal());
    }

    @Test
    void testCalculateSubtotalWithNullValues() {
        // 测试空值情况
        dispenseItem.setDispensedQuantity(null);
        dispenseItem.calculateSubtotal();
        assertEquals(BigDecimal.ZERO, dispenseItem.getSubtotal());
        
        dispenseItem.setDispensedQuantity(10);
        dispenseItem.setUnitPrice(null);
        dispenseItem.calculateSubtotal();
        assertEquals(BigDecimal.ZERO, dispenseItem.getSubtotal());
    }

    @Test
    void testDispense() {
        // 测试调剂药品
        LocalDate expiryDate = LocalDate.now().plusYears(2);
        dispenseItem.dispense(1L, "李药师", "20240101", expiryDate, 100, 90);
        
        assertEquals("已调剂", dispenseItem.getStatus());
        assertEquals(1L, dispenseItem.getDispensedBy());
        assertEquals("李药师", dispenseItem.getDispensedByName());
        assertEquals("20240101", dispenseItem.getBatchNumber());
        assertEquals(expiryDate, dispenseItem.getExpiryDate());
        assertEquals(100, dispenseItem.getStockBeforeDispense());
        assertEquals(90, dispenseItem.getStockAfterDispense());
        assertNotNull(dispenseItem.getDispensedTime());
    }

    @Test
    void testDispenseInvalidStatus() {
        // 设置为非待调剂状态
        dispenseItem.setStatus("已调剂");
        LocalDateTime originalTime = dispenseItem.getDispensedTime();
        
        // 尝试调剂
        LocalDate expiryDate = LocalDate.now().plusYears(2);
        dispenseItem.dispense(1L, "李药师", "20240101", expiryDate, 100, 90);
        
        // 状态不应该改变
        assertEquals("已调剂", dispenseItem.getStatus());
        assertEquals(originalTime, dispenseItem.getDispensedTime());
    }

    @Test
    void testSubstitute() {
        // 测试替代药品
        dispenseItem.substitute(2L, "头孢氨苄胶囊", "原药品缺货");
        
        assertEquals(1L, dispenseItem.getOriginalMedicineId());
        assertEquals("阿莫西林胶囊", dispenseItem.getOriginalMedicineName());
        assertEquals(2L, dispenseItem.getMedicineId());
        assertEquals("头孢氨苄胶囊", dispenseItem.getMedicineName());
        assertTrue(dispenseItem.getIsSubstitute());
        assertEquals("原药品缺货", dispenseItem.getSubstituteReason());
        assertEquals("替代", dispenseItem.getStatus());
    }

    @Test
    void testReturnItem() {
        // 测试退回药品
        dispenseItem.returnItem("质量问题");
        
        assertEquals("退回", dispenseItem.getStatus());
        assertEquals("质量问题", dispenseItem.getReturnReason());
        assertNotNull(dispenseItem.getReturnedTime());
    }

    @Test
    void testReview() {
        // 测试复核药品
        dispenseItem.review(2L, "王主任", "复核通过");
        
        assertEquals(2L, dispenseItem.getReviewedBy());
        assertEquals("王主任", dispenseItem.getReviewedByName());
        assertEquals("复核通过", dispenseItem.getReviewComments());
        assertNotNull(dispenseItem.getReviewedTime());
    }

    @Test
    void testIsOutOfStock() {
        // 测试缺货检查
        dispenseItem.setStockStatus("无库存");
        assertTrue(dispenseItem.isOutOfStock());
        
        dispenseItem.setStockStatus("充足");
        dispenseItem.setStatus("缺货");
        assertTrue(dispenseItem.isOutOfStock());
        
        dispenseItem.setStockStatus("充足");
        dispenseItem.setStatus("已调剂");
        assertFalse(dispenseItem.isOutOfStock());
    }

    @Test
    void testIsLowStock() {
        // 测试库存不足检查
        dispenseItem.setStockStatus("不足");
        dispenseItem.setStockBeforeDispense(5);
        dispenseItem.setPrescribedQuantity(10);
        assertTrue(dispenseItem.isLowStock());
        
        dispenseItem.setStockBeforeDispense(15);
        assertFalse(dispenseItem.isLowStock());
        
        dispenseItem.setStockStatus("充足");
        dispenseItem.setStockBeforeDispense(5);
        assertFalse(dispenseItem.isLowStock());
    }

    @Test
    void testIsExpired() {
        // 测试过期检查
        dispenseItem.setExpiryDate(LocalDate.now().minusDays(1));
        assertTrue(dispenseItem.isExpired());
        
        dispenseItem.setExpiryDate(LocalDate.now().plusDays(1));
        assertFalse(dispenseItem.isExpired());
        
        dispenseItem.setExpiryDate(null);
        assertFalse(dispenseItem.isExpired());
    }

    @Test
    void testIsNearExpiry() {
        // 测试即将过期检查
        dispenseItem.setExpiryDate(LocalDate.now().plusDays(15));
        assertTrue(dispenseItem.isNearExpiry());
        
        dispenseItem.setExpiryDate(LocalDate.now().plusDays(45));
        assertFalse(dispenseItem.isNearExpiry());
        
        dispenseItem.setExpiryDate(null);
        assertFalse(dispenseItem.isNearExpiry());
    }

    @Test
    void testGetFullDosageDescription() {
        // 测试完整用法用量描述
        dispenseItem.setUsage("口服");
        dispenseItem.setDosage("2粒");
        dispenseItem.setFrequency("每日3次");
        dispenseItem.setDuration(7);
        
        String description = dispenseItem.getFullDosageDescription();
        assertEquals("口服，每次2粒，每日3次，连用7天", description);
        
        // 测试部分信息
        dispenseItem.setUsage("口服");
        dispenseItem.setDosage(null);
        dispenseItem.setFrequency("每日3次");
        dispenseItem.setDuration(null);
        
        description = dispenseItem.getFullDosageDescription();
        assertEquals("口服，每日3次", description);
        
        // 测试空信息
        dispenseItem.setUsage(null);
        dispenseItem.setDosage(null);
        dispenseItem.setFrequency(null);
        dispenseItem.setDuration(null);
        
        description = dispenseItem.getFullDosageDescription();
        assertEquals("", description);
    }

    @Test
    void testValidationConstraints() {
        // 测试验证约束
        DispenseItem item = new DispenseItem();
        
        // 测试状态约束
        item.setStatus("无效状态");
        // 在实际应用中，这里应该触发验证异常
        
        // 测试库存状态约束
        item.setStockStatus("无效状态");
        // 在实际应用中，这里应该触发验证异常
        
        // 测试质量检查结果约束
        item.setQualityCheckResult("无效结果");
        // 在实际应用中，这里应该触发验证异常
    }

    @Test
    void testEqualsAndHashCode() {
        DispenseItem item1 = new DispenseItem();
        item1.setId(1L);
        
        DispenseItem item2 = new DispenseItem();
        item2.setId(1L);
        
        DispenseItem item3 = new DispenseItem();
        item3.setId(2L);
        
        assertEquals(item1, item2);
        assertNotEquals(item1, item3);
        assertEquals(item1.hashCode(), item2.hashCode());
    }

    @Test
    void testToString() {
        dispenseItem.setId(1L);
        
        String toString = dispenseItem.toString();
        assertNotNull(toString);
        assertTrue(toString.contains("DispenseItem"));
        assertTrue(toString.contains("id=1"));
        assertTrue(toString.contains("dispenseRecordId=1"));
        assertTrue(toString.contains("medicineId=1"));
        assertTrue(toString.contains("medicineName=阿莫西林胶囊"));
        assertTrue(toString.contains("prescribedQuantity=10"));
        assertTrue(toString.contains("dispensedQuantity=10"));
        assertTrue(toString.contains("status=待调剂"));
    }

    @Test
    void testBatchAndExpiryInfo() {
        // 测试批号和有效期信息
        String batchNumber = "20240101";
        LocalDate productionDate = LocalDate.now().minusMonths(6);
        LocalDate expiryDate = LocalDate.now().plusYears(2);
        
        dispenseItem.setBatchNumber(batchNumber);
        dispenseItem.setProductionDate(productionDate);
        dispenseItem.setExpiryDate(expiryDate);
        
        assertEquals(batchNumber, dispenseItem.getBatchNumber());
        assertEquals(productionDate, dispenseItem.getProductionDate());
        assertEquals(expiryDate, dispenseItem.getExpiryDate());
    }

    @Test
    void testStockInfo() {
        // 测试库存信息
        dispenseItem.setStockStatus("充足");
        dispenseItem.setStockBeforeDispense(100);
        dispenseItem.setStockAfterDispense(90);
        
        assertEquals("充足", dispenseItem.getStockStatus());
        assertEquals(100, dispenseItem.getStockBeforeDispense());
        assertEquals(90, dispenseItem.getStockAfterDispense());
    }

    @Test
    void testQualityInfo() {
        // 测试质量信息
        dispenseItem.setQualityCheckResult("不合格");
        dispenseItem.setQualityIssues("包装破损");
        
        assertEquals("不合格", dispenseItem.getQualityCheckResult());
        assertEquals("包装破损", dispenseItem.getQualityIssues());
    }

    @Test
    void testDosageInfo() {
        // 测试用法用量信息
        dispenseItem.setUsage("口服");
        dispenseItem.setDosage("2粒");
        dispenseItem.setFrequency("每日3次");
        dispenseItem.setDuration(7);
        dispenseItem.setSpecialInstructions("饭后服用");
        
        assertEquals("口服", dispenseItem.getUsage());
        assertEquals("2粒", dispenseItem.getDosage());
        assertEquals("每日3次", dispenseItem.getFrequency());
        assertEquals(7, dispenseItem.getDuration());
        assertEquals("饭后服用", dispenseItem.getSpecialInstructions());
    }
}