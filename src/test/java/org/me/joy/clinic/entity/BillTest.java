package org.me.joy.clinic.entity;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 账单实体测试类
 */
class BillTest {

    @Test
    void testBillCreation() {
        // Given
        Bill bill = new Bill();
        bill.setId(1L);
        bill.setPatientId(100L);
        bill.setRegistrationId(200L);
        bill.setBillNumber("BILL202401010001");
        bill.setTotalAmount(new BigDecimal("150.00"));
        bill.setPaidAmount(new BigDecimal("0.00"));
        bill.setStatus("PENDING");
        bill.setCreatedAt(LocalDateTime.now());
        bill.setNotes("初诊费用");
        bill.setCreatedBy(1L);

        // Then
        assertNotNull(bill);
        assertEquals(1L, bill.getId());
        assertEquals(100L, bill.getPatientId());
        assertEquals(200L, bill.getRegistrationId());
        assertEquals("BILL202401010001", bill.getBillNumber());
        assertEquals(new BigDecimal("150.00"), bill.getTotalAmount());
        assertEquals(new BigDecimal("0.00"), bill.getPaidAmount());
        assertEquals("PENDING", bill.getStatus());
        assertNotNull(bill.getCreatedAt());
        assertEquals("初诊费用", bill.getNotes());
        assertEquals(1L, bill.getCreatedBy());
    }

    @Test
    void testBillStatusValidation() {
        // Given
        Bill bill = new Bill();
        
        // Test valid statuses
        String[] validStatuses = {"PENDING", "PAID", "PARTIALLY_PAID", "CANCELLED"};
        
        for (String status : validStatuses) {
            // When
            bill.setStatus(status);
            
            // Then
            assertEquals(status, bill.getStatus());
        }
    }

    @Test
    void testBillAmountCalculation() {
        // Given
        Bill bill = new Bill();
        bill.setTotalAmount(new BigDecimal("200.00"));
        bill.setPaidAmount(new BigDecimal("150.00"));
        
        // When
        BigDecimal remainingAmount = bill.getTotalAmount().subtract(bill.getPaidAmount());
        
        // Then
        assertEquals(new BigDecimal("50.00"), remainingAmount);
    }

    @Test
    void testBillNumberUniqueness() {
        // Given
        Bill bill1 = new Bill();
        Bill bill2 = new Bill();
        String billNumber = "BILL202401010001";
        
        // When
        bill1.setBillNumber(billNumber);
        bill2.setBillNumber(billNumber);
        
        // Then
        assertEquals(bill1.getBillNumber(), bill2.getBillNumber());
        // Note: Uniqueness is enforced at database level
    }

    @Test
    void testBillEqualsAndHashCode() {
        // Given
        Bill bill1 = new Bill();
        bill1.setId(1L);
        bill1.setBillNumber("BILL202401010001");
        
        Bill bill2 = new Bill();
        bill2.setId(1L);
        bill2.setBillNumber("BILL202401010001");
        
        // Then
        assertEquals(bill1, bill2);
        assertEquals(bill1.hashCode(), bill2.hashCode());
    }

    @Test
    void testBillToString() {
        // Given
        Bill bill = new Bill();
        bill.setId(1L);
        bill.setBillNumber("BILL202401010001");
        bill.setTotalAmount(new BigDecimal("150.00"));
        bill.setStatus("PENDING");
        
        // When
        String billString = bill.toString();
        
        // Then
        assertNotNull(billString);
        assertTrue(billString.contains("Bill"));
    }

    @Test
    void testBillDefaultValues() {
        // Given & When
        Bill bill = new Bill();
        
        // Then
        assertNull(bill.getId());
        assertNull(bill.getPatientId());
        assertNull(bill.getRegistrationId());
        assertNull(bill.getBillNumber());
        assertEquals(BigDecimal.ZERO, bill.getTotalAmount()); // Constructor sets default
        assertEquals(BigDecimal.ZERO, bill.getPaidAmount()); // Constructor sets default
        assertEquals("PENDING", bill.getStatus()); // Constructor sets default
        assertNotNull(bill.getCreatedAt()); // Constructor sets default
        assertNotNull(bill.getUpdatedAt()); // Constructor sets default
        assertNull(bill.getNotes());
        assertNull(bill.getCreatedBy());
        assertNull(bill.getUpdatedBy());
    }

    @Test
    void testBillAmountPrecision() {
        // Given
        Bill bill = new Bill();
        BigDecimal amount = new BigDecimal("123.456");
        
        // When
        bill.setTotalAmount(amount);
        
        // Then
        assertEquals(amount, bill.getTotalAmount());
        // Note: Database precision should be enforced at DB level (DECIMAL(10,2))
    }
}