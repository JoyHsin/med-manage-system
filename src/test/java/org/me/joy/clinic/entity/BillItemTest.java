package org.me.joy.clinic.entity;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 账单项目实体测试类
 */
class BillItemTest {

    @Test
    void testBillItemCreation() {
        // Given
        BillItem billItem = new BillItem();
        billItem.setId(1L);
        billItem.setBillId(100L);
        billItem.setItemType("CONSULTATION");
        billItem.setItemName("专家门诊费");
        billItem.setItemCode("CONS001");
        billItem.setUnitPrice(new BigDecimal("50.00"));
        billItem.setQuantity(1);
        billItem.setSubtotal(new BigDecimal("50.00"));
        billItem.setDiscount(new BigDecimal("0.00"));
        billItem.setActualAmount(new BigDecimal("50.00"));
        billItem.setSpecification("次");
        billItem.setNotes("专家门诊诊疗费");

        // Then
        assertNotNull(billItem);
        assertEquals(1L, billItem.getId());
        assertEquals(100L, billItem.getBillId());
        assertEquals("CONSULTATION", billItem.getItemType());
        assertEquals("专家门诊费", billItem.getItemName());
        assertEquals("CONS001", billItem.getItemCode());
        assertEquals(new BigDecimal("50.00"), billItem.getUnitPrice());
        assertEquals(1, billItem.getQuantity());
        assertEquals(new BigDecimal("50.00"), billItem.getSubtotal());
        assertEquals(new BigDecimal("0.00"), billItem.getDiscount());
        assertEquals(new BigDecimal("50.00"), billItem.getActualAmount());
        assertEquals("次", billItem.getSpecification());
        assertEquals("专家门诊诊疗费", billItem.getNotes());
    }

    @Test
    void testBillItemTypeValidation() {
        // Given
        BillItem billItem = new BillItem();
        
        // Test valid item types
        String[] validTypes = {"REGISTRATION", "CONSULTATION", "MEDICINE", "EXAMINATION", "TREATMENT", "OTHER"};
        
        for (String type : validTypes) {
            // When
            billItem.setItemType(type);
            
            // Then
            assertEquals(type, billItem.getItemType());
        }
    }

    @Test
    void testBillItemAmountCalculation() {
        // Given
        BillItem billItem = new BillItem();
        billItem.setUnitPrice(new BigDecimal("25.50"));
        billItem.setQuantity(3);
        
        // When
        BigDecimal subtotal = billItem.getUnitPrice().multiply(new BigDecimal(billItem.getQuantity()));
        billItem.setSubtotal(subtotal);
        
        BigDecimal discount = new BigDecimal("5.00");
        billItem.setDiscount(discount);
        
        BigDecimal actualAmount = subtotal.subtract(discount);
        billItem.setActualAmount(actualAmount);
        
        // Then
        assertEquals(new BigDecimal("76.50"), billItem.getSubtotal());
        assertEquals(new BigDecimal("5.00"), billItem.getDiscount());
        assertEquals(new BigDecimal("71.50"), billItem.getActualAmount());
    }

    @Test
    void testBillItemWithPrescription() {
        // Given
        BillItem billItem = new BillItem();
        billItem.setItemType("MEDICINE");
        billItem.setItemName("阿莫西林胶囊");
        billItem.setPrescriptionItemId(500L);
        billItem.setMedicalRecordId(300L);
        billItem.setUnitPrice(new BigDecimal("12.50"));
        billItem.setQuantity(2);
        billItem.setSpecification("盒");
        
        // When
        BigDecimal subtotal = billItem.getUnitPrice().multiply(new BigDecimal(billItem.getQuantity()));
        billItem.setSubtotal(subtotal);
        billItem.setDiscount(new BigDecimal("0.00"));
        billItem.setActualAmount(subtotal);
        
        // Then
        assertEquals("MEDICINE", billItem.getItemType());
        assertEquals("阿莫西林胶囊", billItem.getItemName());
        assertEquals(500L, billItem.getPrescriptionItemId());
        assertEquals(300L, billItem.getMedicalRecordId());
        assertEquals(new BigDecimal("25.00"), billItem.getSubtotal());
        assertEquals(new BigDecimal("25.00"), billItem.getActualAmount());
        assertEquals("盒", billItem.getSpecification());
    }

    @Test
    void testBillItemEqualsAndHashCode() {
        // Given
        BillItem item1 = new BillItem();
        item1.setId(1L);
        item1.setBillId(100L);
        item1.setItemName("测试项目");
        
        BillItem item2 = new BillItem();
        item2.setId(1L);
        item2.setBillId(100L);
        item2.setItemName("测试项目");
        
        // Then
        assertEquals(item1, item2);
        assertEquals(item1.hashCode(), item2.hashCode());
    }

    @Test
    void testBillItemToString() {
        // Given
        BillItem billItem = new BillItem();
        billItem.setId(1L);
        billItem.setItemName("测试项目");
        billItem.setItemType("CONSULTATION");
        billItem.setActualAmount(new BigDecimal("50.00"));
        
        // When
        String itemString = billItem.toString();
        
        // Then
        assertNotNull(itemString);
        assertTrue(itemString.contains("BillItem"));
    }

    @Test
    void testBillItemDefaultValues() {
        // Given & When
        BillItem billItem = new BillItem();
        
        // Then
        assertNull(billItem.getId());
        assertNull(billItem.getBillId());
        assertNull(billItem.getItemType());
        assertNull(billItem.getItemName());
        assertNull(billItem.getItemCode());
        assertNull(billItem.getUnitPrice());
        assertEquals(Integer.valueOf(1), billItem.getQuantity()); // Constructor sets default
        assertNull(billItem.getSubtotal());
        assertEquals(BigDecimal.ZERO, billItem.getDiscount()); // Constructor sets default
        assertNull(billItem.getActualAmount());
        assertNull(billItem.getSpecification());
        assertNull(billItem.getNotes());
        assertNull(billItem.getPrescriptionItemId());
        assertNull(billItem.getMedicalRecordId());
    }

    @Test
    void testBillItemDiscountCalculation() {
        // Given
        BillItem billItem = new BillItem();
        billItem.setUnitPrice(new BigDecimal("100.00"));
        billItem.setQuantity(1);
        billItem.setSubtotal(new BigDecimal("100.00"));
        
        // Test percentage discount (10%)
        BigDecimal discountRate = new BigDecimal("0.10");
        BigDecimal discount = billItem.getSubtotal().multiply(discountRate);
        billItem.setDiscount(discount);
        billItem.setActualAmount(billItem.getSubtotal().subtract(discount));
        
        // Then
        assertEquals(0, new BigDecimal("10.00").compareTo(billItem.getDiscount()));
        assertEquals(0, new BigDecimal("90.00").compareTo(billItem.getActualAmount()));
    }

    @Test
    void testBillItemZeroQuantity() {
        // Given
        BillItem billItem = new BillItem();
        billItem.setUnitPrice(new BigDecimal("50.00"));
        billItem.setQuantity(0);
        
        // When
        BigDecimal subtotal = billItem.getUnitPrice().multiply(new BigDecimal(billItem.getQuantity()));
        billItem.setSubtotal(subtotal);
        billItem.setDiscount(new BigDecimal("0.00"));
        billItem.setActualAmount(subtotal);
        
        // Then
        assertEquals(new BigDecimal("0.00"), billItem.getSubtotal());
        assertEquals(new BigDecimal("0.00"), billItem.getActualAmount());
    }
}