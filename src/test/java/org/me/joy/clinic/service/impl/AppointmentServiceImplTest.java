package org.me.joy.clinic.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.me.joy.clinic.dto.CreateAppointmentRequest;
import org.me.joy.clinic.dto.UpdateAppointmentRequest;
import org.me.joy.clinic.entity.Appointment;
import org.me.joy.clinic.entity.AvailableSlot;
import org.me.joy.clinic.exception.BusinessException;
import org.me.joy.clinic.mapper.AppointmentMapper;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * 预约服务实现类单元测试
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("预约服务测试")
class AppointmentServiceImplTest {

    @Mock
    private AppointmentMapper appointmentMapper;

    @InjectMocks
    private AppointmentServiceImpl appointmentService;

    private Appointment testAppointment;
    private CreateAppointmentRequest createRequest;
    private UpdateAppointmentRequest updateRequest;

    @BeforeEach
    void setUp() {
        testAppointment = new Appointment();
        testAppointment.setId(1L);
        testAppointment.setPatientId(1L);
        testAppointment.setDoctorId(2L);
        testAppointment.setAppointmentTime(LocalDateTime.now().plusDays(1));
        testAppointment.setAppointmentType("初诊");
        testAppointment.setStatus("已预约");
        testAppointment.setDepartment("内科");
        testAppointment.setPriority(3);

        createRequest = new CreateAppointmentRequest();
        createRequest.setPatientId(1L);
        createRequest.setDoctorId(2L);
        createRequest.setAppointmentTime(LocalDateTime.now().plusDays(1));
        createRequest.setAppointmentType("初诊");
        createRequest.setDepartment("内科");
        createRequest.setAppointmentFee(new BigDecimal("50.00"));

        updateRequest = new UpdateAppointmentRequest();
        updateRequest.setAppointmentType("复诊");
        updateRequest.setNotes("更新备注");
    }

    @Test
    @DisplayName("创建预约成功")
    void testCreateAppointmentSuccess() {
        when(appointmentMapper.checkTimeConflict(any(), any())).thenReturn(0);
        when(appointmentMapper.insert(any(Appointment.class))).thenReturn(1);

        Appointment result = appointmentService.createAppointment(createRequest);

        assertNotNull(result);
        assertEquals(createRequest.getPatientId(), result.getPatientId());
        assertEquals(createRequest.getDoctorId(), result.getDoctorId());
        assertEquals(createRequest.getAppointmentType(), result.getAppointmentType());
        assertEquals("已预约", result.getStatus());

        verify(appointmentMapper).checkTimeConflict(createRequest.getDoctorId(), createRequest.getAppointmentTime());
        verify(appointmentMapper).insert(any(Appointment.class));
    }

    @Test
    @DisplayName("创建预约失败 - 时间冲突")
    void testCreateAppointmentFailureTimeConflict() {
        when(appointmentMapper.checkTimeConflict(any(), any())).thenReturn(1);

        BusinessException exception = assertThrows(BusinessException.class, 
            () -> appointmentService.createAppointment(createRequest));

        assertTrue(exception.getMessage().contains("该时间段已有预约"));
        verify(appointmentMapper).checkTimeConflict(createRequest.getDoctorId(), createRequest.getAppointmentTime());
        verify(appointmentMapper, never()).insert(any(Appointment.class));
    }

    @Test
    @DisplayName("更新预约成功")
    void testUpdateAppointmentSuccess() {
        when(appointmentMapper.selectById(1L)).thenReturn(testAppointment);
        when(appointmentMapper.updateById(any(Appointment.class))).thenReturn(1);

        Appointment result = appointmentService.updateAppointment(1L, updateRequest);

        assertNotNull(result);
        assertEquals(updateRequest.getAppointmentType(), result.getAppointmentType());
        assertEquals(updateRequest.getNotes(), result.getNotes());

        verify(appointmentMapper).selectById(1L);
        verify(appointmentMapper).updateById(any(Appointment.class));
    }

    @Test
    @DisplayName("更新预约失败 - 状态不允许修改")
    void testUpdateAppointmentFailureInvalidStatus() {
        testAppointment.setStatus("已完成");
        when(appointmentMapper.selectById(1L)).thenReturn(testAppointment);

        BusinessException exception = assertThrows(BusinessException.class, 
            () -> appointmentService.updateAppointment(1L, updateRequest));

        assertTrue(exception.getMessage().contains("当前状态不允许修改预约"));
        verify(appointmentMapper).selectById(1L);
        verify(appointmentMapper, never()).updateById(any(Appointment.class));
    }

    @Test
    @DisplayName("根据ID获取预约成功")
    void testGetAppointmentByIdSuccess() {
        when(appointmentMapper.selectById(1L)).thenReturn(testAppointment);

        Appointment result = appointmentService.getAppointmentById(1L);

        assertNotNull(result);
        assertEquals(testAppointment.getId(), result.getId());
        assertEquals(testAppointment.getPatientId(), result.getPatientId());

        verify(appointmentMapper).selectById(1L);
    }

    @Test
    @DisplayName("根据ID获取预约失败 - 预约不存在")
    void testGetAppointmentByIdFailureNotFound() {
        when(appointmentMapper.selectById(1L)).thenReturn(null);

        BusinessException exception = assertThrows(BusinessException.class, 
            () -> appointmentService.getAppointmentById(1L));

        assertTrue(exception.getMessage().contains("预约不存在"));
        verify(appointmentMapper).selectById(1L);
    }

    @Test
    @DisplayName("根据患者ID获取预约列表")
    void testGetAppointmentsByPatientId() {
        List<Appointment> appointments = Arrays.asList(testAppointment);
        when(appointmentMapper.findByPatientId(1L)).thenReturn(appointments);

        List<Appointment> result = appointmentService.getAppointmentsByPatientId(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testAppointment.getId(), result.get(0).getId());

        verify(appointmentMapper).findByPatientId(1L);
    }

    @Test
    @DisplayName("根据医生ID获取预约列表")
    void testGetAppointmentsByDoctorId() {
        List<Appointment> appointments = Arrays.asList(testAppointment);
        when(appointmentMapper.findByDoctorId(2L)).thenReturn(appointments);

        List<Appointment> result = appointmentService.getAppointmentsByDoctorId(2L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testAppointment.getId(), result.get(0).getId());

        verify(appointmentMapper).findByDoctorId(2L);
    }

    @Test
    @DisplayName("根据日期获取预约列表")
    void testGetAppointmentsByDate() {
        LocalDate date = LocalDate.now();
        List<Appointment> appointments = Arrays.asList(testAppointment);
        when(appointmentMapper.findByDate(date)).thenReturn(appointments);

        List<Appointment> result = appointmentService.getAppointmentsByDate(date);

        assertNotNull(result);
        assertEquals(1, result.size());

        verify(appointmentMapper).findByDate(date);
    }

    @Test
    @DisplayName("获取可用时间段")
    void testGetAvailableSlots() {
        LocalDate date = LocalDate.now().plusDays(1);
        List<LocalDateTime> bookedSlots = Arrays.asList(
            date.atTime(9, 0),
            date.atTime(10, 30)
        );
        when(appointmentMapper.findBookedTimeSlots(2L, date)).thenReturn(bookedSlots);

        List<AvailableSlot> result = appointmentService.getAvailableSlots(2L, date);

        assertNotNull(result);
        assertFalse(result.isEmpty());
        
        // 验证已预约的时间段不可用
        boolean hasUnavailableSlot = result.stream()
            .anyMatch(slot -> !slot.getAvailable() && 
                     (slot.getStartTime().equals(date.atTime(9, 0)) || 
                      slot.getStartTime().equals(date.atTime(10, 30))));
        assertTrue(hasUnavailableSlot);

        verify(appointmentMapper).findBookedTimeSlots(2L, date);
    }

    @Test
    @DisplayName("检查时间冲突")
    void testHasTimeConflict() {
        LocalDateTime appointmentTime = LocalDateTime.now().plusDays(1);
        when(appointmentMapper.checkTimeConflict(2L, appointmentTime)).thenReturn(1);

        boolean result = appointmentService.hasTimeConflict(2L, appointmentTime);

        assertTrue(result);
        verify(appointmentMapper).checkTimeConflict(2L, appointmentTime);
    }

    @Test
    @DisplayName("确认预约")
    void testConfirmAppointment() {
        when(appointmentMapper.selectById(1L)).thenReturn(testAppointment);
        when(appointmentMapper.updateById(any(Appointment.class))).thenReturn(1);

        assertDoesNotThrow(() -> appointmentService.confirmAppointment(1L));

        verify(appointmentMapper).selectById(1L);
        verify(appointmentMapper).updateById(any(Appointment.class));
    }

    @Test
    @DisplayName("取消预约")
    void testCancelAppointment() {
        when(appointmentMapper.selectById(1L)).thenReturn(testAppointment);
        when(appointmentMapper.updateById(any(Appointment.class))).thenReturn(1);

        assertDoesNotThrow(() -> appointmentService.cancelAppointment(1L, "患者临时有事"));

        verify(appointmentMapper).selectById(1L);
        verify(appointmentMapper).updateById(any(Appointment.class));
    }

    @Test
    @DisplayName("标记患者到达")
    void testMarkPatientArrived() {
        testAppointment.setStatus("已确认");
        when(appointmentMapper.selectById(1L)).thenReturn(testAppointment);
        when(appointmentMapper.updateById(any(Appointment.class))).thenReturn(1);

        assertDoesNotThrow(() -> appointmentService.markPatientArrived(1L));

        verify(appointmentMapper).selectById(1L);
        verify(appointmentMapper).updateById(any(Appointment.class));
    }

    @Test
    @DisplayName("开始就诊")
    void testStartConsultation() {
        testAppointment.setStatus("已到达");
        when(appointmentMapper.selectById(1L)).thenReturn(testAppointment);
        when(appointmentMapper.updateById(any(Appointment.class))).thenReturn(1);

        assertDoesNotThrow(() -> appointmentService.startConsultation(1L));

        verify(appointmentMapper).selectById(1L);
        verify(appointmentMapper).updateById(any(Appointment.class));
    }

    @Test
    @DisplayName("完成就诊")
    void testCompleteConsultation() {
        testAppointment.setStatus("进行中");
        when(appointmentMapper.selectById(1L)).thenReturn(testAppointment);
        when(appointmentMapper.updateById(any(Appointment.class))).thenReturn(1);

        assertDoesNotThrow(() -> appointmentService.completeConsultation(1L));

        verify(appointmentMapper).selectById(1L);
        verify(appointmentMapper).updateById(any(Appointment.class));
    }

    @Test
    @DisplayName("标记未到")
    void testMarkNoShow() {
        testAppointment.setStatus("已确认");
        testAppointment.setAppointmentTime(LocalDateTime.now().minusHours(1));
        when(appointmentMapper.selectById(1L)).thenReturn(testAppointment);
        when(appointmentMapper.updateById(any(Appointment.class))).thenReturn(1);

        assertDoesNotThrow(() -> appointmentService.markNoShow(1L));

        verify(appointmentMapper).selectById(1L);
        verify(appointmentMapper).updateById(any(Appointment.class));
    }

    @Test
    @DisplayName("获取今日预约")
    void testGetTodayAppointments() {
        List<Appointment> appointments = Arrays.asList(testAppointment);
        when(appointmentMapper.findTodayAppointments()).thenReturn(appointments);

        List<Appointment> result = appointmentService.getTodayAppointments();

        assertNotNull(result);
        assertEquals(1, result.size());

        verify(appointmentMapper).findTodayAppointments();
    }

    @Test
    @DisplayName("获取需要提醒的预约")
    void testGetAppointmentsNeedingReminder() {
        List<Appointment> appointments = Arrays.asList(testAppointment);
        when(appointmentMapper.findAppointmentsNeedingReminder()).thenReturn(appointments);

        List<Appointment> result = appointmentService.getAppointmentsNeedingReminder();

        assertNotNull(result);
        assertEquals(1, result.size());

        verify(appointmentMapper).findAppointmentsNeedingReminder();
    }

    @Test
    @DisplayName("统计指定日期的预约数量")
    void testCountAppointmentsByDate() {
        LocalDate date = LocalDate.now();
        when(appointmentMapper.countAppointmentsByDate(date)).thenReturn(5L);

        Long result = appointmentService.countAppointmentsByDate(date);

        assertEquals(5L, result);
        verify(appointmentMapper).countAppointmentsByDate(date);
    }

    @Test
    @DisplayName("批量处理过期预约")
    void testProcessExpiredAppointments() {
        List<Appointment> expiredAppointments = Arrays.asList(testAppointment);
        when(appointmentMapper.findExpiredAppointments()).thenReturn(expiredAppointments);
        when(appointmentMapper.updateById(any(Appointment.class))).thenReturn(1);

        assertDoesNotThrow(() -> appointmentService.processExpiredAppointments());

        verify(appointmentMapper).findExpiredAppointments();
        verify(appointmentMapper).updateById(any(Appointment.class));
    }

    @Test
    @DisplayName("发送预约提醒")
    void testSendAppointmentReminders() {
        List<Appointment> appointments = Arrays.asList(testAppointment);
        when(appointmentMapper.findAppointmentsNeedingReminder()).thenReturn(appointments);

        assertDoesNotThrow(() -> appointmentService.sendAppointmentReminders());

        verify(appointmentMapper).findAppointmentsNeedingReminder();
    }
}