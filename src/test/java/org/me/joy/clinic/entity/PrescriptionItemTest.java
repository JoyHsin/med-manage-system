package org.me.joy.clinic.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 处方项目实体类测试
 */
class PrescriptionItemTest {

    private PrescriptionItem prescriptionItem;

    @BeforeEach
    void setUp() {
        prescriptionItem = new PrescriptionItem(1L, 2L, "阿莫西林胶囊", 
                                               10, "粒", new BigDecimal("2.50"));
    }

    @Test
    void testConstructor() {
        assertNotNull(prescriptionItem);
        assertEquals(1L, prescriptionItem.getPrescriptionId());
        assertEquals(2L, prescriptionItem.getMedicineId());
        assertEquals("阿莫西林胶囊", prescriptionItem.getMedicineName());
        assertEquals(10, prescriptionItem.getQuantity());
        assertEquals("粒", prescriptionItem.getUnit());
        assertEquals(new BigDecimal("2.50"), prescriptionItem.getUnitPrice());
        assertEquals(new BigDecimal("25.00"), prescriptionItem.getSubtotal());
        assertFalse(prescriptionItem.getIsSubstitute());
        assertEquals(1, prescriptionItem.getSortOrder());
    }

    @Test
    void testCalculateSubtotal() {
        prescriptionItem.setQuantity(5);
        prescriptionItem.setUnitPrice(new BigDecimal("10.00"));
        prescriptionItem.calculateSubtotal();
        
        assertEquals(new BigDecimal("50.00"), prescriptionItem.getSubtotal());
    }

    @Test
    void testCalculateSubtotalWithNullValues() {
        prescriptionItem.setQuantity(null);
        prescriptionItem.setUnitPrice(new BigDecimal("10.00"));
        prescriptionItem.calculateSubtotal();
        assertEquals(BigDecimal.ZERO, prescriptionItem.getSubtotal());
        
        prescriptionItem.setQuantity(5);
        prescriptionItem.setUnitPrice(null);
        prescriptionItem.calculateSubtotal();
        assertEquals(BigDecimal.ZERO, prescriptionItem.getSubtotal());
        
        prescriptionItem.setQuantity(null);
        prescriptionItem.setUnitPrice(null);
        prescriptionItem.calculateSubtotal();
        assertEquals(BigDecimal.ZERO, prescriptionItem.getSubtotal());
    }

    @Test
    void testSetQuantity() {
        prescriptionItem.setQuantity(20);
        assertEquals(20, prescriptionItem.getQuantity());
        // 应该自动重新计算小计
        assertEquals(new BigDecimal("50.00"), prescriptionItem.getSubtotal());
    }

    @Test
    void testSetUnitPrice() {
        prescriptionItem.setUnitPrice(new BigDecimal("5.00"));
        assertEquals(new BigDecimal("5.00"), prescriptionItem.getUnitPrice());
        // 应该自动重新计算小计
        assertEquals(new BigDecimal("50.00"), prescriptionItem.getSubtotal());
    }

    @Test
    void testGetFullDosageDescription() {
        prescriptionItem.setUsage("口服");
        prescriptionItem.setDosage("2粒");
        prescriptionItem.setFrequency("每日3次");
        prescriptionItem.setDuration(7);
        
        String description = prescriptionItem.getFullDosageDescription();
        assertEquals("口服，每次2粒，每日3次，连用7天", description);
    }

    @Test
    void testGetFullDosageDescriptionPartial() {
        prescriptionItem.setUsage("口服");
        prescriptionItem.setFrequency("每日3次");
        
        String description = prescriptionItem.getFullDosageDescription();
        assertEquals("口服，每日3次", description);
    }

    @Test
    void testGetFullDosageDescriptionEmpty() {
        String description = prescriptionItem.getFullDosageDescription();
        assertEquals("", description);
    }

    @Test
    void testGetFullDosageDescriptionWithEmptyStrings() {
        prescriptionItem.setUsage("");
        prescriptionItem.setDosage("   ");
        prescriptionItem.setFrequency("每日3次");
        
        String description = prescriptionItem.getFullDosageDescription();
        assertEquals("每日3次", description);
    }

    @Test
    void testIsSpecialMedicine() {
        // 普通药品不是特殊药品
        assertFalse(prescriptionItem.isSpecialMedicine());
        
        // 替代药品是特殊药品
        prescriptionItem.setIsSubstitute(true);
        assertTrue(prescriptionItem.isSpecialMedicine());
        
        // 有特殊说明的是特殊药品
        prescriptionItem.setIsSubstitute(false);
        prescriptionItem.setSpecialInstructions("需要冷藏保存");
        assertTrue(prescriptionItem.isSpecialMedicine());
        
        // 空的特殊说明不算特殊药品
        prescriptionItem.setSpecialInstructions("");
        assertFalse(prescriptionItem.isSpecialMedicine());
        
        prescriptionItem.setSpecialInstructions("   ");
        assertFalse(prescriptionItem.isSpecialMedicine());
    }

    @Test
    void testEquals() {
        prescriptionItem.setId(1L);
        
        PrescriptionItem item2 = new PrescriptionItem();
        item2.setId(1L);
        
        PrescriptionItem item3 = new PrescriptionItem();
        item3.setId(2L);

        // 相同ID应该相等
        assertEquals(prescriptionItem, item2);
        
        // 不同ID不相等
        assertNotEquals(prescriptionItem, item3);
        
        // 与null不相等
        assertNotEquals(prescriptionItem, null);
        
        // 与其他类型不相等
        assertNotEquals(prescriptionItem, "string");
    }

    @Test
    void testHashCode() {
        prescriptionItem.setId(1L);
        
        PrescriptionItem item2 = new PrescriptionItem();
        item2.setId(1L);

        assertEquals(prescriptionItem.hashCode(), item2.hashCode());
    }

    @Test
    void testToString() {
        prescriptionItem.setId(1L);
        prescriptionItem.setSubtotal(new BigDecimal("25.00"));

        String toString = prescriptionItem.toString();
        assertNotNull(toString);
        assertTrue(toString.contains("PrescriptionItem"));
        assertTrue(toString.contains("阿莫西林胶囊"));
        assertTrue(toString.contains("25.00"));
        assertTrue(toString.contains("10"));
        assertTrue(toString.contains("粒"));
    }

    @Test
    void testSettersAndGetters() {
        prescriptionItem.setSpecification("0.25g*24粒");
        prescriptionItem.setUsage("口服");
        prescriptionItem.setDosage("2粒");
        prescriptionItem.setFrequency("每日3次");
        prescriptionItem.setDuration(7);
        prescriptionItem.setSpecialInstructions("饭后服用");
        prescriptionItem.setIsSubstitute(true);
        prescriptionItem.setOriginalMedicineId(3L);
        prescriptionItem.setSubstituteReason("原药品缺货");
        prescriptionItem.setSortOrder(2);
        prescriptionItem.setRemarks("患者过敏史需注意");

        assertEquals("0.25g*24粒", prescriptionItem.getSpecification());
        assertEquals("口服", prescriptionItem.getUsage());
        assertEquals("2粒", prescriptionItem.getDosage());
        assertEquals("每日3次", prescriptionItem.getFrequency());
        assertEquals(7, prescriptionItem.getDuration());
        assertEquals("饭后服用", prescriptionItem.getSpecialInstructions());
        assertTrue(prescriptionItem.getIsSubstitute());
        assertEquals(3L, prescriptionItem.getOriginalMedicineId());
        assertEquals("原药品缺货", prescriptionItem.getSubstituteReason());
        assertEquals(2, prescriptionItem.getSortOrder());
        assertEquals("患者过敏史需注意", prescriptionItem.getRemarks());
    }

    @Test
    void testDefaultConstructor() {
        PrescriptionItem emptyItem = new PrescriptionItem();
        assertNotNull(emptyItem);
        assertFalse(emptyItem.getIsSubstitute());
        assertEquals(1, emptyItem.getSortOrder());
    }

    @Test
    void testSubtotalCalculationInConstructor() {
        PrescriptionItem item = new PrescriptionItem(1L, 2L, "测试药品", 
                                                    5, "片", new BigDecimal("3.20"));
        assertEquals(new BigDecimal("16.00"), item.getSubtotal());
    }
}