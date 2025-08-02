package org.me.joy.clinic.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 预约实体类单元测试
 */
@DisplayName("预约实体测试")
class AppointmentTest {

    private Validator validator;
    private Appointment appointment;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
        
        appointment = new Appointment();
        appointment.setPatientId(1L);
        appointment.setDoctorId(2L);
        appointment.setAppointmentTime(LocalDateTime.now().plusDays(1));
        appointment.setAppointmentType("初诊");
        appointment.setStatus("已预约");
        appointment.setDepartment("内科");
        appointment.setAppointmentFee(new BigDecimal("50.00"));
    }

    @Test
    @DisplayName("创建有效预约实体")
    void testCreateValidAppointment() {
        Set<ConstraintViolation<Appointment>> violations = validator.validate(appointment);
        assertTrue(violations.isEmpty(), "有效的预约实体不应有验证错误");
    }

    @Test
    @DisplayName("患者ID不能为空")
    void testPatientIdNotNull() {
        appointment.setPatientId(null);
        Set<ConstraintViolation<Appointment>> violations = validator.validate(appointment);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("患者ID不能为空")));
    }

    @Test
    @DisplayName("医生ID不能为空")
    void testDoctorIdNotNull() {
        appointment.setDoctorId(null);
        Set<ConstraintViolation<Appointment>> violations = validator.validate(appointment);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("医生ID不能为空")));
    }

    @Test
    @DisplayName("预约时间不能为空")
    void testAppointmentTimeNotNull() {
        appointment.setAppointmentTime(null);
        Set<ConstraintViolation<Appointment>> violations = validator.validate(appointment);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("预约时间不能为空")));
    }

    @Test
    @DisplayName("预约时间必须是未来时间")
    void testAppointmentTimeFuture() {
        appointment.setAppointmentTime(LocalDateTime.now().minusHours(1));
        Set<ConstraintViolation<Appointment>> violations = validator.validate(appointment);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("预约时间必须是未来时间")));
    }

    @Test
    @DisplayName("预约类型验证")
    void testAppointmentTypeValidation() {
        appointment.setAppointmentType("无效类型");
        Set<ConstraintViolation<Appointment>> violations = validator.validate(appointment);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("预约类型只能是")));
    }

    @Test
    @DisplayName("有效的预约类型")
    void testValidAppointmentTypes() {
        String[] validTypes = {"初诊", "复诊", "专家门诊", "急诊", "体检", "疫苗接种", "其他"};
        
        for (String type : validTypes) {
            appointment.setAppointmentType(type);
            Set<ConstraintViolation<Appointment>> violations = validator.validate(appointment);
            assertTrue(violations.isEmpty(), "预约类型 '" + type + "' 应该是有效的");
        }
    }

    @Test
    @DisplayName("预约状态验证")
    void testStatusValidation() {
        appointment.setStatus("无效状态");
        Set<ConstraintViolation<Appointment>> violations = validator.validate(appointment);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("预约状态只能是")));
    }

    @Test
    @DisplayName("有效的预约状态")
    void testValidStatuses() {
        String[] validStatuses = {"已预约", "已确认", "已到达", "进行中", "已完成", "已取消", "未到"};
        
        for (String status : validStatuses) {
            appointment.setStatus(status);
            Set<ConstraintViolation<Appointment>> violations = validator.validate(appointment);
            assertTrue(violations.isEmpty(), "预约状态 '" + status + "' 应该是有效的");
        }
    }

    @Test
    @DisplayName("优先级范围验证")
    void testPriorityRange() {
        appointment.setPriority(0);
        Set<ConstraintViolation<Appointment>> violations = validator.validate(appointment);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("优先级最小值为1")));

        appointment.setPriority(6);
        violations = validator.validate(appointment);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("优先级最大值为5")));
    }

    @Test
    @DisplayName("预约费用不能为负数")
    void testAppointmentFeeNotNegative() {
        appointment.setAppointmentFee(new BigDecimal("-10.00"));
        Set<ConstraintViolation<Appointment>> violations = validator.validate(appointment);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("预约费用不能为负数")));
    }

    @Test
    @DisplayName("检查预约是否可以取消")
    void testCanBeCancelled() {
        appointment.setStatus("已预约");
        assertTrue(appointment.canBeCancelled());

        appointment.setStatus("已确认");
        assertTrue(appointment.canBeCancelled());

        appointment.setStatus("已完成");
        assertFalse(appointment.canBeCancelled());

        appointment.setStatus("已取消");
        assertFalse(appointment.canBeCancelled());
    }

    @Test
    @DisplayName("检查预约是否已过期")
    void testIsExpired() {
        appointment.setAppointmentTime(LocalDateTime.now().minusHours(1));
        appointment.setStatus("已预约");
        assertTrue(appointment.isExpired());

        appointment.setAppointmentTime(LocalDateTime.now().plusHours(1));
        assertFalse(appointment.isExpired());

        appointment.setAppointmentTime(LocalDateTime.now().minusHours(1));
        appointment.setStatus("已完成");
        assertFalse(appointment.isExpired());
    }

    @Test
    @DisplayName("检查是否需要提醒")
    void testShouldRemind() {
        appointment.setNeedReminder(true);
        appointment.setReminderMinutes(30);
        appointment.setAppointmentTime(LocalDateTime.now().plusMinutes(15));
        assertTrue(appointment.shouldRemind());

        appointment.setAppointmentTime(LocalDateTime.now().plusMinutes(45));
        assertFalse(appointment.shouldRemind());

        appointment.setNeedReminder(false);
        appointment.setAppointmentTime(LocalDateTime.now().plusMinutes(15));
        assertFalse(appointment.shouldRemind());
    }

    @Test
    @DisplayName("确认预约")
    void testConfirm() {
        appointment.setStatus("已预约");
        appointment.confirm();
        assertEquals("已确认", appointment.getStatus());
        assertNotNull(appointment.getConfirmedAt());

        // 非已预约状态不能确认
        appointment.setStatus("已完成");
        appointment.setConfirmedAt(null);
        appointment.confirm();
        assertEquals("已完成", appointment.getStatus());
        assertNull(appointment.getConfirmedAt());
    }

    @Test
    @DisplayName("标记患者到达")
    void testMarkArrived() {
        appointment.setStatus("已确认");
        appointment.markArrived();
        assertEquals("已到达", appointment.getStatus());
        assertNotNull(appointment.getArrivedAt());
    }

    @Test
    @DisplayName("开始就诊")
    void testStart() {
        appointment.setStatus("已到达");
        appointment.start();
        assertEquals("进行中", appointment.getStatus());
        assertNotNull(appointment.getStartedAt());
    }

    @Test
    @DisplayName("完成就诊")
    void testComplete() {
        appointment.setStatus("进行中");
        appointment.complete();
        assertEquals("已完成", appointment.getStatus());
        assertNotNull(appointment.getCompletedAt());
    }

    @Test
    @DisplayName("取消预约")
    void testCancel() {
        appointment.setStatus("已预约");
        appointment.cancel("患者临时有事");
        assertEquals("已取消", appointment.getStatus());
        assertEquals("患者临时有事", appointment.getCancelReason());
        assertNotNull(appointment.getCancelledAt());
    }

    @Test
    @DisplayName("标记未到")
    void testMarkNoShow() {
        appointment.setStatus("已确认");
        appointment.setAppointmentTime(LocalDateTime.now().minusHours(1));
        appointment.markNoShow();
        assertEquals("未到", appointment.getStatus());
    }

    @Test
    @DisplayName("获取状态描述")
    void testGetStatusDescription() {
        appointment.setStatus("已预约");
        assertEquals("患者已预约，等待确认", appointment.getStatusDescription());

        appointment.setStatus("已确认");
        assertEquals("预约已确认，等待患者到达", appointment.getStatusDescription());

        appointment.setStatus("已完成");
        assertEquals("就诊已完成", appointment.getStatusDescription());
    }

    @Test
    @DisplayName("预约实体相等性测试")
    void testEquality() {
        Appointment appointment1 = new Appointment();
        appointment1.setId(1L);
        
        Appointment appointment2 = new Appointment();
        appointment2.setId(1L);
        
        Appointment appointment3 = new Appointment();
        appointment3.setId(2L);
        
        assertEquals(appointment1, appointment2, "相同ID的预约应相等");
        assertNotEquals(appointment1, appointment3, "不同ID的预约不应相等");
        assertEquals(appointment1.hashCode(), appointment2.hashCode(), "相等的预约应有相同的hashCode");
    }

    @Test
    @DisplayName("预约实体toString测试")
    void testToString() {
        appointment.setId(1L);
        String toString = appointment.toString();
        
        assertTrue(toString.contains("Appointment{"));
        assertTrue(toString.contains("id=1"));
        assertTrue(toString.contains("patientId=1"));
        assertTrue(toString.contains("doctorId=2"));
        assertTrue(toString.contains("appointmentType='初诊'"));
        assertTrue(toString.contains("status='已预约'"));
    }

    @Test
    @DisplayName("构造函数测试")
    void testConstructors() {
        Appointment defaultAppointment = new Appointment();
        assertNull(defaultAppointment.getPatientId());
        assertNull(defaultAppointment.getDoctorId());
        
        LocalDateTime appointmentTime = LocalDateTime.now().plusDays(1);
        Appointment parameterizedAppointment = new Appointment(1L, 2L, appointmentTime, "复诊");
        assertEquals(1L, parameterizedAppointment.getPatientId());
        assertEquals(2L, parameterizedAppointment.getDoctorId());
        assertEquals(appointmentTime, parameterizedAppointment.getAppointmentTime());
        assertEquals("复诊", parameterizedAppointment.getAppointmentType());
    }

    @Test
    @DisplayName("默认值测试")
    void testDefaultValues() {
        Appointment newAppointment = new Appointment();
        assertEquals("已预约", newAppointment.getStatus());
        assertEquals("现场", newAppointment.getSource());
        assertEquals(3, newAppointment.getPriority());
        assertEquals(Boolean.TRUE, newAppointment.getNeedReminder());
        assertEquals(30, newAppointment.getReminderMinutes());
    }
}