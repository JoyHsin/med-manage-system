package org.me.joy.clinic.service;

import org.me.joy.clinic.dto.CreateAppointmentRequest;
import org.me.joy.clinic.dto.UpdateAppointmentRequest;
import org.me.joy.clinic.entity.Appointment;
import org.me.joy.clinic.entity.AvailableSlot;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 预约服务接口
 */
public interface AppointmentService {

    /**
     * 创建预约
     */
    Appointment createAppointment(CreateAppointmentRequest request);

    /**
     * 更新预约
     */
    Appointment updateAppointment(Long appointmentId, UpdateAppointmentRequest request);

    /**
     * 根据ID获取预约
     */
    Appointment getAppointmentById(Long appointmentId);

    /**
     * 根据患者ID获取预约列表
     */
    List<Appointment> getAppointmentsByPatientId(Long patientId);

    /**
     * 根据医生ID获取预约列表
     */
    List<Appointment> getAppointmentsByDoctorId(Long doctorId);

    /**
     * 根据日期获取预约列表
     */
    List<Appointment> getAppointmentsByDate(LocalDate date);

    /**
     * 根据医生ID和日期获取预约列表
     */
    List<Appointment> getAppointmentsByDoctorAndDate(Long doctorId, LocalDate date);

    /**
     * 获取医生的可用时间段
     */
    List<AvailableSlot> getAvailableSlots(Long doctorId, LocalDate date);

    /**
     * 检查时间冲突
     */
    boolean hasTimeConflict(Long doctorId, LocalDateTime appointmentTime);

    /**
     * 确认预约
     */
    void confirmAppointment(Long appointmentId);

    /**
     * 取消预约
     */
    void cancelAppointment(Long appointmentId, String reason);

    /**
     * 标记患者到达
     */
    void markPatientArrived(Long appointmentId);

    /**
     * 开始就诊
     */
    void startConsultation(Long appointmentId);

    /**
     * 完成就诊
     */
    void completeConsultation(Long appointmentId);

    /**
     * 标记未到
     */
    void markNoShow(Long appointmentId);

    /**
     * 获取今日预约
     */
    List<Appointment> getTodayAppointments();

    /**
     * 获取明日预约
     */
    List<Appointment> getTomorrowAppointments();

    /**
     * 获取需要提醒的预约
     */
    List<Appointment> getAppointmentsNeedingReminder();

    /**
     * 获取过期预约
     */
    List<Appointment> getExpiredAppointments();

    /**
     * 根据状态获取预约
     */
    List<Appointment> getAppointmentsByStatus(String status);

    /**
     * 根据预约类型获取预约
     */
    List<Appointment> getAppointmentsByType(String appointmentType);

    /**
     * 根据科室获取预约
     */
    List<Appointment> getAppointmentsByDepartment(String department);

    /**
     * 获取高优先级预约
     */
    List<Appointment> getHighPriorityAppointments();

    /**
     * 统计指定日期的预约数量
     */
    Long countAppointmentsByDate(LocalDate date);

    /**
     * 统计指定医生指定日期的预约数量
     */
    Long countAppointmentsByDoctorAndDate(Long doctorId, LocalDate date);

    /**
     * 统计各状态预约数量
     */
    List<Object> countAppointmentsByStatus();

    /**
     * 统计各预约类型数量
     */
    List<Object> countAppointmentsByType();

    /**
     * 获取患者最近的预约
     */
    Appointment getLatestAppointmentByPatient(Long patientId);

    /**
     * 获取医生的下一个预约
     */
    Appointment getNextAppointmentByDoctor(Long doctorId);

    /**
     * 批量处理过期预约
     */
    void processExpiredAppointments();

    /**
     * 发送预约提醒
     */
    void sendAppointmentReminders();
}