package org.me.joy.clinic.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 处方实体类测试
 */
class PrescriptionTest {

    private Prescription prescription;

    @BeforeEach
    void setUp() {
        prescription = new Prescription(1L, 2L, "RX20240101001");
    }

    @Test
    void testConstructor() {
        assertNotNull(prescription);
        assertEquals(1L, prescription.getMedicalRecordId());
        assertEquals(2L, prescription.getDoctorId());
        assertEquals("RX20240101001", prescription.getPrescriptionNumber());
        assertNotNull(prescription.getPrescribedAt());
        assertEquals("普通处方", prescription.getPrescriptionType());
        assertEquals("草稿", prescription.getStatus());
        assertEquals(3, prescription.getValidityDays());
        assertFalse(prescription.getIsEmergency());
        assertFalse(prescription.getIsChildPrescription());
        assertFalse(prescription.getIsChronicDiseasePrescription());
        assertEquals(1, prescription.getRepeatTimes());
    }

    @Test
    void testCalculateTotalAmount() {
        List<PrescriptionItem> items = new ArrayList<>();
        
        // 创建处方项目
        PrescriptionItem item1 = new PrescriptionItem();
        item1.setUnitPrice(new BigDecimal("10.50"));
        item1.setQuantity(2);
        item1.calculateSubtotal();
        
        PrescriptionItem item2 = new PrescriptionItem();
        item2.setUnitPrice(new BigDecimal("25.00"));
        item2.setQuantity(1);
        item2.calculateSubtotal();
        
        items.add(item1);
        items.add(item2);
        
        prescription.setPrescriptionItems(items);
        prescription.calculateTotalAmount();
        
        // 10.50 * 2 + 25.00 * 1 = 46.00
        assertEquals(new BigDecimal("46.00"), prescription.getTotalAmount());
    }

    @Test
    void testCalculateTotalAmountWithEmptyItems() {
        prescription.setPrescriptionItems(new ArrayList<>());
        prescription.calculateTotalAmount();
        assertEquals(0, prescription.getTotalAmount().compareTo(BigDecimal.ZERO));
        
        prescription.setPrescriptionItems(null);
        prescription.calculateTotalAmount();
        assertEquals(0, prescription.getTotalAmount().compareTo(BigDecimal.ZERO));
    }

    @Test
    void testCalculateTotalAmountWithNullValues() {
        List<PrescriptionItem> items = new ArrayList<>();
        
        PrescriptionItem item1 = new PrescriptionItem();
        item1.setUnitPrice(null);
        item1.setQuantity(2);
        
        PrescriptionItem item2 = new PrescriptionItem();
        item2.setUnitPrice(new BigDecimal("25.00"));
        item2.setQuantity(null);
        
        items.add(item1);
        items.add(item2);
        
        prescription.setPrescriptionItems(items);
        prescription.calculateTotalAmount();
        
        assertEquals(0, prescription.getTotalAmount().compareTo(BigDecimal.ZERO));
    }

    @Test
    void testIssue() {
        prescription.setStatus("草稿");
        LocalDateTime beforeIssue = LocalDateTime.now();
        
        prescription.issue();
        
        assertEquals("已开具", prescription.getStatus());
        assertTrue(prescription.getPrescribedAt().isAfter(beforeIssue) || 
                  prescription.getPrescribedAt().isEqual(beforeIssue));
        
        // 非草稿状态不能开具
        prescription.setStatus("已审核");
        prescription.issue();
        assertEquals("已审核", prescription.getStatus());
    }

    @Test
    void testReview() {
        Long reviewDoctorId = 3L;
        String comments = "审核通过";
        
        prescription.setStatus("已开具");
        LocalDateTime beforeReview = LocalDateTime.now();
        
        prescription.review(reviewDoctorId, comments);
        
        assertEquals("已审核", prescription.getStatus());
        assertEquals(reviewDoctorId, prescription.getReviewDoctorId());
        assertEquals(comments, prescription.getReviewComments());
        assertTrue(prescription.getReviewedAt().isAfter(beforeReview) || 
                  prescription.getReviewedAt().isEqual(beforeReview));
        
        // 非已开具状态不能审核
        prescription.setStatus("草稿");
        prescription.review(4L, "测试");
        assertEquals("草稿", prescription.getStatus());
    }

    @Test
    void testDispense() {
        Long pharmacistId = 4L;
        
        prescription.setStatus("已审核");
        LocalDateTime beforeDispense = LocalDateTime.now();
        
        prescription.dispense(pharmacistId);
        
        assertEquals("已调配", prescription.getStatus());
        assertEquals(pharmacistId, prescription.getPharmacistId());
        assertTrue(prescription.getDispensedAt().isAfter(beforeDispense) || 
                  prescription.getDispensedAt().isEqual(beforeDispense));
        
        // 非已审核状态不能调配
        prescription.setStatus("草稿");
        prescription.dispense(5L);
        assertEquals("草稿", prescription.getStatus());
    }

    @Test
    void testDeliver() {
        prescription.setStatus("已调配");
        LocalDateTime beforeDeliver = LocalDateTime.now();
        
        prescription.deliver();
        
        assertEquals("已发药", prescription.getStatus());
        assertTrue(prescription.getDeliveredAt().isAfter(beforeDeliver) || 
                  prescription.getDeliveredAt().isEqual(beforeDeliver));
        
        // 非已调配状态不能发药
        prescription.setStatus("草稿");
        prescription.deliver();
        assertEquals("草稿", prescription.getStatus());
    }

    @Test
    void testCancel() {
        String reason = "患者要求取消";
        
        prescription.setStatus("已开具");
        LocalDateTime beforeCancel = LocalDateTime.now();
        
        prescription.cancel(reason);
        
        assertEquals("已取消", prescription.getStatus());
        assertEquals(reason, prescription.getCancelReason());
        assertTrue(prescription.getCancelledAt().isAfter(beforeCancel) || 
                  prescription.getCancelledAt().isEqual(beforeCancel));
        
        // 已发药状态不能取消
        prescription.setStatus("已发药");
        prescription.cancel("测试取消");
        assertEquals("已发药", prescription.getStatus());
    }

    @Test
    void testIsExpired() {
        // 未设置开具时间或有效期，不过期
        prescription.setPrescribedAt(null);
        assertFalse(prescription.isExpired());
        
        prescription.setValidityDays(null);
        prescription.setPrescribedAt(LocalDateTime.now());
        assertFalse(prescription.isExpired());
        
        // 未过期
        prescription.setPrescribedAt(LocalDateTime.now().minusDays(1));
        prescription.setValidityDays(3);
        assertFalse(prescription.isExpired());
        
        // 已过期
        prescription.setPrescribedAt(LocalDateTime.now().minusDays(5));
        prescription.setValidityDays(3);
        assertTrue(prescription.isExpired());
    }

    @Test
    void testCanBeDispensed() {
        // 已审核且未过期可以调配
        prescription.setStatus("已审核");
        prescription.setPrescribedAt(LocalDateTime.now().minusDays(1));
        prescription.setValidityDays(3);
        assertTrue(prescription.canBeDispensed());
        
        // 非已审核状态不能调配
        prescription.setStatus("草稿");
        assertFalse(prescription.canBeDispensed());
        
        // 已过期不能调配
        prescription.setStatus("已审核");
        prescription.setPrescribedAt(LocalDateTime.now().minusDays(5));
        prescription.setValidityDays(3);
        assertFalse(prescription.canBeDispensed());
    }

    @Test
    void testNeedsSpecialReview() {
        // 普通处方不需要特殊审核
        prescription.setPrescriptionType("普通处方");
        assertFalse(prescription.needsSpecialReview());
        
        // 麻醉处方需要特殊审核
        prescription.setPrescriptionType("麻醉处方");
        assertTrue(prescription.needsSpecialReview());
        
        // 精神药品处方需要特殊审核
        prescription.setPrescriptionType("精神药品处方");
        assertTrue(prescription.needsSpecialReview());
        
        // 毒性药品处方需要特殊审核
        prescription.setPrescriptionType("毒性药品处方");
        assertTrue(prescription.needsSpecialReview());
    }

    @Test
    void testEquals() {
        Prescription prescription1 = new Prescription(1L, 2L, "RX001");
        Prescription prescription2 = new Prescription(2L, 3L, "RX001");
        Prescription prescription3 = new Prescription(1L, 2L, "RX002");

        // 相同处方编号应该相等
        assertEquals(prescription1, prescription2);
        
        // 不同处方编号不相等
        assertNotEquals(prescription1, prescription3);
        
        // 与null不相等
        assertNotEquals(prescription1, null);
        
        // 与其他类型不相等
        assertNotEquals(prescription1, "string");
    }

    @Test
    void testHashCode() {
        Prescription prescription1 = new Prescription(1L, 2L, "RX001");
        Prescription prescription2 = new Prescription(2L, 3L, "RX001");

        assertEquals(prescription1.hashCode(), prescription2.hashCode());
    }

    @Test
    void testToString() {
        prescription.setId(1L);
        prescription.setPrescriptionType("急诊处方");
        prescription.setTotalAmount(new BigDecimal("100.00"));

        String toString = prescription.toString();
        assertNotNull(toString);
        assertTrue(toString.contains("Prescription"));
        assertTrue(toString.contains("RX20240101001"));
        assertTrue(toString.contains("急诊处方"));
        assertTrue(toString.contains("100.00"));
    }

    @Test
    void testSettersAndGetters() {
        prescription.setPrescriptionType("急诊处方");
        prescription.setStatus("已开具");
        prescription.setReviewDoctorId(3L);
        prescription.setReviewedAt(LocalDateTime.now());
        prescription.setPharmacistId(4L);
        prescription.setDispensedAt(LocalDateTime.now());
        prescription.setDeliveredAt(LocalDateTime.now());
        prescription.setTotalAmount(new BigDecimal("150.50"));
        prescription.setDosageInstructions("按医嘱服用");
        prescription.setPrecautions("饭后服用");
        prescription.setClinicalDiagnosis("上呼吸道感染");
        prescription.setValidityDays(7);
        prescription.setIsEmergency(true);
        prescription.setIsChildPrescription(false);
        prescription.setIsChronicDiseasePrescription(true);
        prescription.setRepeatTimes(2);
        prescription.setReviewComments("审核通过");
        prescription.setCancelReason("患者要求");
        prescription.setCancelledAt(LocalDateTime.now());
        prescription.setRemarks("特殊说明");

        assertEquals("急诊处方", prescription.getPrescriptionType());
        assertEquals("已开具", prescription.getStatus());
        assertEquals(3L, prescription.getReviewDoctorId());
        assertNotNull(prescription.getReviewedAt());
        assertEquals(4L, prescription.getPharmacistId());
        assertNotNull(prescription.getDispensedAt());
        assertNotNull(prescription.getDeliveredAt());
        assertEquals(new BigDecimal("150.50"), prescription.getTotalAmount());
        assertEquals("按医嘱服用", prescription.getDosageInstructions());
        assertEquals("饭后服用", prescription.getPrecautions());
        assertEquals("上呼吸道感染", prescription.getClinicalDiagnosis());
        assertEquals(7, prescription.getValidityDays());
        assertTrue(prescription.getIsEmergency());
        assertFalse(prescription.getIsChildPrescription());
        assertTrue(prescription.getIsChronicDiseasePrescription());
        assertEquals(2, prescription.getRepeatTimes());
        assertEquals("审核通过", prescription.getReviewComments());
        assertEquals("患者要求", prescription.getCancelReason());
        assertNotNull(prescription.getCancelledAt());
        assertEquals("特殊说明", prescription.getRemarks());
    }

    @Test
    void testDefaultConstructor() {
        Prescription emptyPrescription = new Prescription();
        assertNotNull(emptyPrescription);
        assertEquals("普通处方", emptyPrescription.getPrescriptionType());
        assertEquals("草稿", emptyPrescription.getStatus());
        assertEquals(3, emptyPrescription.getValidityDays());
        assertFalse(emptyPrescription.getIsEmergency());
        assertFalse(emptyPrescription.getIsChildPrescription());
        assertFalse(emptyPrescription.getIsChronicDiseasePrescription());
        assertEquals(1, emptyPrescription.getRepeatTimes());
    }
}