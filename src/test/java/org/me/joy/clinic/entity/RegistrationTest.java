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
 * 挂号实体类单元测试
 */
@DisplayName("挂号实体测试")
class RegistrationTest {

    private Validator validator;
    private Registration registration;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
        
        registration = new Registration();
        registration.setPatientId(1L);
        registration.setRegistrationNumber("REG20240101001");
        registration.setRegistrationDate(LocalDate.now());
        registration.setRegistrationTime(LocalDateTime.now());
        registration.setDepartment("内科");
        registration.setRegistrationType("普通门诊");
        registration.setStatus("已挂号");
        registration.setRegistrationFee(new BigDecimal("10.00"));
        registration.setQueueNumber(1);
    }

    @Test
    @DisplayName("创建有效挂号实体")
    void testCreateValidRegistration() {
        Set<ConstraintViolation<Registration>> violations = validator.validate(registration);
        assertTrue(violations.isEmpty(), "有效的挂号实体不应有验证错误");
    }

    @Test
    @DisplayName("患者ID不能为空")
    void testPatientIdNotNull() {
        registration.setPatientId(null);
        Set<ConstraintViolation<Registration>> violations = validator.validate(registration);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("患者ID不能为空")));
    }

    @Test
    @DisplayName("挂号编号不能为空")
    void testRegistrationNumberNotBlank() {
        registration.setRegistrationNumber("");
        Set<ConstraintViolation<Registration>> violations = validator.validate(registration);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("挂号编号不能为空")));
    }

    @Test
    @DisplayName("挂号编号长度限制")
    void testRegistrationNumberMaxLength() {
        registration.setRegistrationNumber("A".repeat(51));
        Set<ConstraintViolation<Registration>> violations = validator.validate(registration);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("挂号编号长度不能超过50个字符")));
    }

    @Test
    @DisplayName("挂号日期不能为空")
    void testRegistrationDateNotNull() {
        registration.setRegistrationDate(null);
        Set<ConstraintViolation<Registration>> violations = validator.validate(registration);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("挂号日期不能为空")));
    }

    @Test
    @DisplayName("科室不能为空")
    void testDepartmentNotBlank() {
        registration.setDepartment("");
        Set<ConstraintViolation<Registration>> violations = validator.validate(registration);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("科室不能为空")));
    }

    @Test
    @DisplayName("挂号类型验证")
    void testRegistrationTypeValidation() {
        registration.setRegistrationType("无效类型");
        Set<ConstraintViolation<Registration>> violations = validator.validate(registration);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("挂号类型只能是")));
    }

    @Test
    @DisplayName("有效的挂号类型")
    void testValidRegistrationTypes() {
        String[] validTypes = {"普通门诊", "专家门诊", "急诊", "专科门诊", "体检", "疫苗接种", "其他"};
        
        for (String type : validTypes) {
            registration.setRegistrationType(type);
            Set<ConstraintViolation<Registration>> violations = validator.validate(registration);
            assertTrue(violations.isEmpty(), "挂号类型 '" + type + "' 应该是有效的");
        }
    }

    @Test
    @DisplayName("挂号状态验证")
    void testStatusValidation() {
        registration.setStatus("无效状态");
        Set<ConstraintViolation<Registration>> violations = validator.validate(registration);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("挂号状态只能是")));
    }

    @Test
    @DisplayName("有效的挂号状态")
    void testValidStatuses() {
        String[] validStatuses = {"已挂号", "已叫号", "已到达", "就诊中", "已完成", "已取消", "未到"};
        
        for (String status : validStatuses) {
            registration.setStatus(status);
            Set<ConstraintViolation<Registration>> violations = validator.validate(registration);
            assertTrue(violations.isEmpty(), "挂号状态 '" + status + "' 应该是有效的");
        }
    }

    @Test
    @DisplayName("队列号必须大于0")
    void testQueueNumberMinValue() {
        registration.setQueueNumber(0);
        Set<ConstraintViolation<Registration>> violations = validator.validate(registration);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("队列号必须大于0")));
    }

    @Test
    @DisplayName("优先级范围验证")
    void testPriorityRange() {
        registration.setPriority(0);
        Set<ConstraintViolation<Registration>> violations = validator.validate(registration);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("优先级最小值为1")));

        registration.setPriority(6);
        violations = validator.validate(registration);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("优先级最大值为5")));
    }

    @Test
    @DisplayName("挂号费不能为空且不能为负数")
    void testRegistrationFeeValidation() {
        registration.setRegistrationFee(null);
        Set<ConstraintViolation<Registration>> violations = validator.validate(registration);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("挂号费不能为空")));

        registration.setRegistrationFee(new BigDecimal("-10.00"));
        violations = validator.validate(registration);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("挂号费不能为负数")));
    }

    @Test
    @DisplayName("计算总费用")
    void testCalculateTotalFee() {
        registration.setRegistrationFee(new BigDecimal("10.00"));
        registration.setConsultationFee(new BigDecimal("20.00"));
        
        registration.calculateTotalFee();
        assertEquals(new BigDecimal("30.00"), registration.getTotalFee());
    }

    @Test
    @DisplayName("计算总费用 - 部分费用为空")
    void testCalculateTotalFeeWithNullValues() {
        registration.setRegistrationFee(new BigDecimal("10.00"));
        registration.setConsultationFee(null);
        
        registration.calculateTotalFee();
        assertEquals(new BigDecimal("10.00"), registration.getTotalFee());
    }

    @Test
    @DisplayName("检查是否可以取消")
    void testCanBeCancelled() {
        registration.setStatus("已挂号");
        assertTrue(registration.canBeCancelled());

        registration.setStatus("已叫号");
        assertTrue(registration.canBeCancelled());

        registration.setStatus("已完成");
        assertFalse(registration.canBeCancelled());

        registration.setStatus("已取消");
        assertFalse(registration.canBeCancelled());
    }

    @Test
    @DisplayName("检查是否已支付")
    void testIsPaid() {
        registration.setPaymentStatus("已支付");
        assertTrue(registration.isPaid());

        registration.setPaymentStatus("未支付");
        assertFalse(registration.isPaid());
    }

    @Test
    @DisplayName("检查是否为当日挂号")
    void testIsTodayRegistration() {
        registration.setRegistrationDate(LocalDate.now());
        assertTrue(registration.isTodayRegistration());

        registration.setRegistrationDate(LocalDate.now().minusDays(1));
        assertFalse(registration.isTodayRegistration());
    }

    @Test
    @DisplayName("获取等待时间")
    void testGetWaitingTimeMinutes() {
        LocalDateTime registrationTime = LocalDateTime.now().minusMinutes(30);
        LocalDateTime calledTime = LocalDateTime.now();
        
        registration.setRegistrationTime(registrationTime);
        registration.setCalledAt(calledTime);
        
        Long waitingTime = registration.getWaitingTimeMinutes();
        assertEquals(30L, waitingTime);
    }

    @Test
    @DisplayName("获取就诊时长")
    void testGetConsultationDurationMinutes() {
        LocalDateTime startTime = LocalDateTime.now().minusMinutes(20);
        LocalDateTime endTime = LocalDateTime.now();
        
        registration.setStartedAt(startTime);
        registration.setCompletedAt(endTime);
        
        Long duration = registration.getConsultationDurationMinutes();
        assertEquals(20L, duration);
    }

    @Test
    @DisplayName("叫号")
    void testCall() {
        registration.setStatus("已挂号");
        registration.call();
        assertEquals("已叫号", registration.getStatus());
        assertNotNull(registration.getCalledAt());
    }

    @Test
    @DisplayName("标记患者到达")
    void testMarkArrived() {
        registration.setStatus("已叫号");
        registration.markArrived();
        assertEquals("已到达", registration.getStatus());
        assertNotNull(registration.getArrivedAt());
    }

    @Test
    @DisplayName("开始就诊")
    void testStartConsultation() {
        registration.setStatus("已到达");
        registration.startConsultation();
        assertEquals("就诊中", registration.getStatus());
        assertNotNull(registration.getStartedAt());
    }

    @Test
    @DisplayName("完成就诊")
    void testComplete() {
        registration.setStatus("就诊中");
        registration.complete();
        assertEquals("已完成", registration.getStatus());
        assertNotNull(registration.getCompletedAt());
    }

    @Test
    @DisplayName("取消挂号")
    void testCancel() {
        registration.setStatus("已挂号");
        registration.cancel("患者临时有事");
        assertEquals("已取消", registration.getStatus());
        assertEquals("患者临时有事", registration.getCancelReason());
        assertNotNull(registration.getCancelledAt());
    }

    @Test
    @DisplayName("标记未到")
    void testMarkNoShow() {
        registration.setStatus("已叫号");
        registration.markNoShow();
        assertEquals("未到", registration.getStatus());
    }

    @Test
    @DisplayName("标记已支付")
    void testMarkPaid() {
        registration.markPaid("现金");
        assertEquals("已支付", registration.getPaymentStatus());
        assertEquals("现金", registration.getPaymentMethod());
    }

    @Test
    @DisplayName("获取状态描述")
    void testGetStatusDescription() {
        registration.setStatus("已挂号");
        assertEquals("已挂号，等待叫号", registration.getStatusDescription());

        registration.setStatus("已叫号");
        assertEquals("已叫号，等待患者到达", registration.getStatusDescription());

        registration.setStatus("已完成");
        assertEquals("就诊已完成", registration.getStatusDescription());
    }

    @Test
    @DisplayName("挂号实体相等性测试")
    void testEquality() {
        Registration registration1 = new Registration();
        registration1.setRegistrationNumber("REG001");
        
        Registration registration2 = new Registration();
        registration2.setRegistrationNumber("REG001");
        
        Registration registration3 = new Registration();
        registration3.setRegistrationNumber("REG002");
        
        assertEquals(registration1, registration2, "相同挂号编号的挂号应相等");
        assertNotEquals(registration1, registration3, "不同挂号编号的挂号不应相等");
        assertEquals(registration1.hashCode(), registration2.hashCode(), "相等的挂号应有相同的hashCode");
    }

    @Test
    @DisplayName("挂号实体toString测试")
    void testToString() {
        registration.setId(1L);
        String toString = registration.toString();
        
        assertTrue(toString.contains("Registration{"));
        assertTrue(toString.contains("id=1"));
        assertTrue(toString.contains("patientId=1"));
        assertTrue(toString.contains("registrationNumber='REG20240101001'"));
        assertTrue(toString.contains("department='内科'"));
        assertTrue(toString.contains("status='已挂号'"));
    }

    @Test
    @DisplayName("构造函数测试")
    void testConstructors() {
        Registration defaultRegistration = new Registration();
        assertNull(defaultRegistration.getPatientId());
        assertNull(defaultRegistration.getRegistrationNumber());
        
        LocalDate regDate = LocalDate.now();
        Registration parameterizedRegistration = new Registration(1L, "REG002", regDate, "外科", "专家门诊");
        assertEquals(1L, parameterizedRegistration.getPatientId());
        assertEquals("REG002", parameterizedRegistration.getRegistrationNumber());
        assertEquals(regDate, parameterizedRegistration.getRegistrationDate());
        assertEquals("外科", parameterizedRegistration.getDepartment());
        assertEquals("专家门诊", parameterizedRegistration.getRegistrationType());
        assertNotNull(parameterizedRegistration.getRegistrationTime());
    }

    @Test
    @DisplayName("默认值测试")
    void testDefaultValues() {
        Registration newRegistration = new Registration();
        assertEquals("已挂号", newRegistration.getStatus());
        assertEquals("未支付", newRegistration.getPaymentStatus());
        assertEquals("现场", newRegistration.getSource());
        assertEquals(3, newRegistration.getPriority());
        assertEquals(Boolean.TRUE, newRegistration.getIsFirstVisit());
        assertEquals(Boolean.FALSE, newRegistration.getIsEmergency());
    }
}