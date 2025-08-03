package org.me.joy.clinic.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 处方调剂记录实体测试类
 */
class DispenseRecordTest {

    private DispenseRecord dispenseRecord;

    @BeforeEach
    void setUp() {
        dispenseRecord = new DispenseRecord(
            1L, "P20240101001", 1L, "张三", 1L, "李药师"
        );
    }

    @Test
    void testConstructor() {
        assertNotNull(dispenseRecord);
        assertEquals(1L, dispenseRecord.getPrescriptionId());
        assertEquals("P20240101001", dispenseRecord.getPrescriptionNumber());
        assertEquals(1L, dispenseRecord.getPatientId());
        assertEquals("张三", dispenseRecord.getPatientName());
        assertEquals(1L, dispenseRecord.getPharmacistId());
        assertEquals("李药师", dispenseRecord.getPharmacistName());
        assertEquals("待调剂", dispenseRecord.getStatus());
        assertNotNull(dispenseRecord.getDispenseStartTime());
    }

    @Test
    void testDefaultValues() {
        DispenseRecord record = new DispenseRecord();
        assertEquals("待调剂", record.getStatus());
        assertFalse(record.getRequiresRefrigeration());
        assertFalse(record.getRequiresSpecialPackaging());
    }

    @Test
    void testStartDispensing() {
        // 测试开始调剂
        dispenseRecord.startDispensing();
        assertEquals("调剂中", dispenseRecord.getStatus());
        assertNotNull(dispenseRecord.getDispenseStartTime());
    }

    @Test
    void testStartDispensingInvalidStatus() {
        // 设置为非待调剂状态
        dispenseRecord.setStatus("已调剂");
        LocalDateTime originalTime = dispenseRecord.getDispenseStartTime();
        
        // 尝试开始调剂
        dispenseRecord.startDispensing();
        
        // 状态不应该改变
        assertEquals("已调剂", dispenseRecord.getStatus());
        assertEquals(originalTime, dispenseRecord.getDispenseStartTime());
    }

    @Test
    void testCompleteDispensing() {
        // 先开始调剂
        dispenseRecord.startDispensing();
        
        // 完成调剂
        dispenseRecord.completeDispensing();
        assertEquals("已调剂", dispenseRecord.getStatus());
        assertNotNull(dispenseRecord.getDispenseCompletedTime());
    }

    @Test
    void testCompleteDispensingInvalidStatus() {
        // 设置为非调剂中状态
        dispenseRecord.setStatus("待调剂");
        
        // 尝试完成调剂
        dispenseRecord.completeDispensing();
        
        // 状态不应该改变
        assertEquals("待调剂", dispenseRecord.getStatus());
        assertNull(dispenseRecord.getDispenseCompletedTime());
    }

    @Test
    void testDeliver() {
        // 设置为已调剂状态
        dispenseRecord.setStatus("已调剂");
        
        // 发药
        dispenseRecord.deliver(2L, "王药师");
        assertEquals("已发药", dispenseRecord.getStatus());
        assertEquals(2L, dispenseRecord.getDispensingPharmacistId());
        assertEquals("王药师", dispenseRecord.getDispensingPharmacistName());
        assertNotNull(dispenseRecord.getDeliveredTime());
    }

    @Test
    void testDeliverInvalidStatus() {
        // 设置为非已调剂状态
        dispenseRecord.setStatus("调剂中");
        
        // 尝试发药
        dispenseRecord.deliver(2L, "王药师");
        
        // 状态不应该改变
        assertEquals("调剂中", dispenseRecord.getStatus());
        assertNull(dispenseRecord.getDispensingPharmacistId());
        assertNull(dispenseRecord.getDispensingPharmacistName());
        assertNull(dispenseRecord.getDeliveredTime());
    }

    @Test
    void testReturnPrescription() {
        // 设置为调剂中状态
        dispenseRecord.setStatus("调剂中");
        
        // 退回处方
        dispenseRecord.returnPrescription("库存不足");
        assertEquals("已退回", dispenseRecord.getStatus());
        assertEquals("库存不足", dispenseRecord.getReturnReason());
        assertNotNull(dispenseRecord.getReturnedTime());
    }

    @Test
    void testReturnPrescriptionAfterDelivery() {
        // 设置为已发药状态
        dispenseRecord.setStatus("已发药");
        
        // 尝试退回处方
        dispenseRecord.returnPrescription("测试原因");
        
        // 状态不应该改变（已发药不能退回）
        assertEquals("已发药", dispenseRecord.getStatus());
        assertNull(dispenseRecord.getReturnReason());
        assertNull(dispenseRecord.getReturnedTime());
    }

    @Test
    void testCancel() {
        // 设置为待调剂状态
        dispenseRecord.setStatus("待调剂");
        
        // 取消调剂
        dispenseRecord.cancel("患者取消");
        assertEquals("已取消", dispenseRecord.getStatus());
        assertEquals("患者取消", dispenseRecord.getCancelReason());
        assertNotNull(dispenseRecord.getCancelledTime());
    }

    @Test
    void testCancelAfterDelivery() {
        // 设置为已发药状态
        dispenseRecord.setStatus("已发药");
        
        // 尝试取消调剂
        dispenseRecord.cancel("测试原因");
        
        // 状态不应该改变（已发药不能取消）
        assertEquals("已发药", dispenseRecord.getStatus());
        assertNull(dispenseRecord.getCancelReason());
        assertNull(dispenseRecord.getCancelledTime());
    }

    @Test
    void testReview() {
        // 复核调剂
        dispenseRecord.review(3L, "赵主任", "复核通过");
        assertEquals(3L, dispenseRecord.getReviewPharmacistId());
        assertEquals("赵主任", dispenseRecord.getReviewPharmacistName());
        assertEquals("复核通过", dispenseRecord.getReviewComments());
        assertNotNull(dispenseRecord.getReviewedTime());
    }

    @Test
    void testGetDispenseTimeInMinutes() {
        // 设置开始和完成时间
        LocalDateTime startTime = LocalDateTime.now().minusMinutes(30);
        LocalDateTime completedTime = LocalDateTime.now();
        
        dispenseRecord.setDispenseStartTime(startTime);
        dispenseRecord.setDispenseCompletedTime(completedTime);
        
        Long timeInMinutes = dispenseRecord.getDispenseTimeInMinutes();
        assertNotNull(timeInMinutes);
        assertTrue(timeInMinutes >= 29 && timeInMinutes <= 31); // 允许1分钟误差
    }

    @Test
    void testGetDispenseTimeInMinutesWithNullTimes() {
        // 测试时间为空的情况
        dispenseRecord.setDispenseStartTime(null);
        dispenseRecord.setDispenseCompletedTime(null);
        
        Long timeInMinutes = dispenseRecord.getDispenseTimeInMinutes();
        assertNull(timeInMinutes);
    }

    @Test
    void testNeedsReview() {
        // 测试需要复核的情况
        dispenseRecord.setValidationResult("需要复核");
        assertTrue(dispenseRecord.needsReview());
        
        dispenseRecord.setValidationResult("通过");
        dispenseRecord.setDrugInteractionCheck("发现药物相互作用警告");
        assertTrue(dispenseRecord.needsReview());
        
        dispenseRecord.setDrugInteractionCheck("无相互作用");
        dispenseRecord.setAllergyCheck("患者对青霉素过敏");
        assertTrue(dispenseRecord.needsReview());
        
        // 测试不需要复核的情况
        dispenseRecord.setValidationResult("通过");
        dispenseRecord.setDrugInteractionCheck("无相互作用");
        dispenseRecord.setAllergyCheck("无过敏史");
        assertFalse(dispenseRecord.needsReview());
    }

    @Test
    void testCanBeDelivered() {
        // 测试可以发药的情况
        dispenseRecord.setStatus("已调剂");
        dispenseRecord.setValidationResult("通过");
        dispenseRecord.setQualityCheckResult("合格");
        assertTrue(dispenseRecord.canBeDelivered());
        
        // 测试不能发药的情况
        dispenseRecord.setStatus("调剂中");
        assertFalse(dispenseRecord.canBeDelivered());
        
        dispenseRecord.setStatus("已调剂");
        dispenseRecord.setValidationResult("不通过");
        assertFalse(dispenseRecord.canBeDelivered());
        
        dispenseRecord.setValidationResult("通过");
        dispenseRecord.setQualityCheckResult("不合格");
        assertFalse(dispenseRecord.canBeDelivered());
    }

    @Test
    void testValidationConstraints() {
        // 测试验证约束
        DispenseRecord record = new DispenseRecord();
        
        // 测试状态约束
        record.setStatus("无效状态");
        // 在实际应用中，这里应该触发验证异常
        
        // 测试验证结果约束
        record.setValidationResult("无效结果");
        // 在实际应用中，这里应该触发验证异常
        
        // 测试库存检查结果约束
        record.setStockCheckResult("无效结果");
        // 在实际应用中，这里应该触发验证异常
    }

    @Test
    void testEqualsAndHashCode() {
        DispenseRecord record1 = new DispenseRecord();
        record1.setId(1L);
        
        DispenseRecord record2 = new DispenseRecord();
        record2.setId(1L);
        
        DispenseRecord record3 = new DispenseRecord();
        record3.setId(2L);
        
        assertEquals(record1, record2);
        assertNotEquals(record1, record3);
        assertEquals(record1.hashCode(), record2.hashCode());
    }

    @Test
    void testToString() {
        dispenseRecord.setId(1L);
        dispenseRecord.setTotalAmount(new BigDecimal("100.00"));
        
        String toString = dispenseRecord.toString();
        assertNotNull(toString);
        assertTrue(toString.contains("DispenseRecord"));
        assertTrue(toString.contains("id=1"));
        assertTrue(toString.contains("prescriptionId=1"));
        assertTrue(toString.contains("prescriptionNumber=P20240101001"));
        assertTrue(toString.contains("patientName=张三"));
        assertTrue(toString.contains("pharmacistName=李药师"));
        assertTrue(toString.contains("status=待调剂"));
        // Debug: print actual toString to see what's wrong
        System.out.println("Actual toString: " + toString);
        assertTrue(toString.contains("totalAmount="));
    }

    @Test
    void testAmountFields() {
        // 测试金额字段
        BigDecimal totalAmount = new BigDecimal("150.50");
        BigDecimal actualAmount = new BigDecimal("145.00");
        
        dispenseRecord.setTotalAmount(totalAmount);
        dispenseRecord.setActualAmount(actualAmount);
        
        assertEquals(totalAmount, dispenseRecord.getTotalAmount());
        assertEquals(actualAmount, dispenseRecord.getActualAmount());
    }

    @Test
    void testSpecialRequirements() {
        // 测试特殊要求
        dispenseRecord.setRequiresRefrigeration(true);
        dispenseRecord.setRequiresSpecialPackaging(true);
        dispenseRecord.setPackagingSpecification("避光密封包装");
        
        assertTrue(dispenseRecord.getRequiresRefrigeration());
        assertTrue(dispenseRecord.getRequiresSpecialPackaging());
        assertEquals("避光密封包装", dispenseRecord.getPackagingSpecification());
    }
}