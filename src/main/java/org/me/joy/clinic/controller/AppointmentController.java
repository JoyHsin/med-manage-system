package org.me.joy.clinic.controller;

import jakarta.validation.Valid;
import org.me.joy.clinic.dto.CreateAppointmentRequest;
import org.me.joy.clinic.dto.UpdateAppointmentRequest;
import org.me.joy.clinic.entity.Appointment;
import org.me.joy.clinic.entity.AvailableSlot;
import org.me.joy.clinic.security.RequiresPermission;
import org.me.joy.clinic.service.AppointmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 预约管理控制器
 */
@RestController
@RequestMapping("/appointments")
public class AppointmentController {

    @Autowired
    private AppointmentService appointmentService;

    /**
     * 创建预约
     */
    @PostMapping
    @RequiresPermission("APPOINTMENT_CREATE")
    public ResponseEntity<Appointment> createAppointment(@Valid @RequestBody CreateAppointmentRequest request) {
        Appointment appointment = appointmentService.createAppointment(request);
        return ResponseEntity.ok(appointment);
    }

    /**
     * 更新预约
     */
    @PutMapping("/{id}")
    @RequiresPermission("APPOINTMENT_UPDATE")
    public ResponseEntity<Appointment> updateAppointment(@PathVariable Long id, 
                                                       @Valid @RequestBody UpdateAppointmentRequest request) {
        Appointment appointment = appointmentService.updateAppointment(id, request);
        return ResponseEntity.ok(appointment);
    }

    /**
     * 根据ID获取预约
     */
    @GetMapping("/{id}")
    @RequiresPermission("APPOINTMENT_VIEW")
    public ResponseEntity<Appointment> getAppointmentById(@PathVariable Long id) {
        Appointment appointment = appointmentService.getAppointmentById(id);
        return ResponseEntity.ok(appointment);
    }

    /**
     * 根据患者ID获取预约列表
     */
    @GetMapping("/patient/{patientId}")
    @RequiresPermission("APPOINTMENT_VIEW")
    public ResponseEntity<List<Appointment>> getAppointmentsByPatientId(@PathVariable Long patientId) {
        List<Appointment> appointments = appointmentService.getAppointmentsByPatientId(patientId);
        return ResponseEntity.ok(appointments);
    }

    /**
     * 根据医生ID获取预约列表
     */
    @GetMapping("/doctor/{doctorId}")
    @RequiresPermission("APPOINTMENT_VIEW")
    public ResponseEntity<List<Appointment>> getAppointmentsByDoctorId(@PathVariable Long doctorId) {
        List<Appointment> appointments = appointmentService.getAppointmentsByDoctorId(doctorId);
        return ResponseEntity.ok(appointments);
    }

    /**
     * 根据日期获取预约列表
     */
    @GetMapping("/date/{date}")
    @RequiresPermission("APPOINTMENT_VIEW")
    public ResponseEntity<List<Appointment>> getAppointmentsByDate(
            @PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) {
        List<Appointment> appointments = appointmentService.getAppointmentsByDate(date);
        return ResponseEntity.ok(appointments);
    }

    /**
     * 根据医生ID和日期获取预约列表
     */
    @GetMapping("/doctor/{doctorId}/date/{date}")
    public ResponseEntity<List<Appointment>> getAppointmentsByDoctorAndDate(
            @PathVariable Long doctorId,
            @PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) {
        List<Appointment> appointments = appointmentService.getAppointmentsByDoctorAndDate(doctorId, date);
        return ResponseEntity.ok(appointments);
    }

    /**
     * 获取医生的可用时间段
     */
    @GetMapping("/doctor/{doctorId}/available-slots")
    @RequiresPermission("APPOINTMENT_VIEW")
    public ResponseEntity<List<AvailableSlot>> getAvailableSlots(
            @PathVariable Long doctorId,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) {
        List<AvailableSlot> slots = appointmentService.getAvailableSlots(doctorId, date);
        return ResponseEntity.ok(slots);
    }

    /**
     * 检查时间冲突
     */
    @GetMapping("/doctor/{doctorId}/time-conflict")
    public ResponseEntity<Map<String, Boolean>> checkTimeConflict(
            @PathVariable Long doctorId,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime appointmentTime) {
        boolean hasConflict = appointmentService.hasTimeConflict(doctorId, appointmentTime);
        Map<String, Boolean> response = new HashMap<>();
        response.put("hasConflict", hasConflict);
        return ResponseEntity.ok(response);
    }

    /**
     * 确认预约
     */
    @PostMapping("/{id}/confirm")
    @RequiresPermission("APPOINTMENT_CONFIRM")
    public ResponseEntity<Map<String, String>> confirmAppointment(@PathVariable Long id) {
        appointmentService.confirmAppointment(id);
        Map<String, String> response = new HashMap<>();
        response.put("message", "预约确认成功");
        return ResponseEntity.ok(response);
    }

    /**
     * 取消预约
     */
    @PostMapping("/{id}/cancel")
    @RequiresPermission("APPOINTMENT_CANCEL")
    public ResponseEntity<Map<String, String>> cancelAppointment(@PathVariable Long id, 
                                                               @RequestParam String reason) {
        appointmentService.cancelAppointment(id, reason);
        Map<String, String> response = new HashMap<>();
        response.put("message", "预约取消成功");
        return ResponseEntity.ok(response);
    }

    /**
     * 标记患者到达
     */
    @PostMapping("/{id}/arrive")
    @RequiresPermission("APPOINTMENT_UPDATE")
    public ResponseEntity<Map<String, String>> markPatientArrived(@PathVariable Long id) {
        appointmentService.markPatientArrived(id);
        Map<String, String> response = new HashMap<>();
        response.put("message", "患者到达标记成功");
        return ResponseEntity.ok(response);
    }

    /**
     * 开始就诊
     */
    @PostMapping("/{id}/start")
    @RequiresPermission("APPOINTMENT_UPDATE")
    public ResponseEntity<Map<String, String>> startConsultation(@PathVariable Long id) {
        appointmentService.startConsultation(id);
        Map<String, String> response = new HashMap<>();
        response.put("message", "就诊开始");
        return ResponseEntity.ok(response);
    }

    /**
     * 完成就诊
     */
    @PostMapping("/{id}/complete")
    @RequiresPermission("APPOINTMENT_UPDATE")
    public ResponseEntity<Map<String, String>> completeConsultation(@PathVariable Long id) {
        appointmentService.completeConsultation(id);
        Map<String, String> response = new HashMap<>();
        response.put("message", "就诊完成");
        return ResponseEntity.ok(response);
    }

    /**
     * 标记未到
     */
    @PostMapping("/{id}/no-show")
    public ResponseEntity<Map<String, String>> markNoShow(@PathVariable Long id) {
        appointmentService.markNoShow(id);
        Map<String, String> response = new HashMap<>();
        response.put("message", "已标记为未到");
        return ResponseEntity.ok(response);
    }

    /**
     * 获取今日预约
     */
    @GetMapping("/today")
    @RequiresPermission("APPOINTMENT_VIEW")
    public ResponseEntity<List<Appointment>> getTodayAppointments() {
        List<Appointment> appointments = appointmentService.getTodayAppointments();
        return ResponseEntity.ok(appointments);
    }

    /**
     * 获取明日预约
     */
    @GetMapping("/tomorrow")
    public ResponseEntity<List<Appointment>> getTomorrowAppointments() {
        List<Appointment> appointments = appointmentService.getTomorrowAppointments();
        return ResponseEntity.ok(appointments);
    }

    /**
     * 根据状态获取预约
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<List<Appointment>> getAppointmentsByStatus(@PathVariable String status) {
        List<Appointment> appointments = appointmentService.getAppointmentsByStatus(status);
        return ResponseEntity.ok(appointments);
    }

    /**
     * 根据预约类型获取预约
     */
    @GetMapping("/type/{type}")
    public ResponseEntity<List<Appointment>> getAppointmentsByType(@PathVariable String type) {
        List<Appointment> appointments = appointmentService.getAppointmentsByType(type);
        return ResponseEntity.ok(appointments);
    }

    /**
     * 根据科室获取预约
     */
    @GetMapping("/department/{department}")
    public ResponseEntity<List<Appointment>> getAppointmentsByDepartment(@PathVariable String department) {
        List<Appointment> appointments = appointmentService.getAppointmentsByDepartment(department);
        return ResponseEntity.ok(appointments);
    }

    /**
     * 获取高优先级预约
     */
    @GetMapping("/high-priority")
    public ResponseEntity<List<Appointment>> getHighPriorityAppointments() {
        List<Appointment> appointments = appointmentService.getHighPriorityAppointments();
        return ResponseEntity.ok(appointments);
    }

    /**
     * 获取需要提醒的预约
     */
    @GetMapping("/reminders")
    public ResponseEntity<List<Appointment>> getAppointmentsNeedingReminder() {
        List<Appointment> appointments = appointmentService.getAppointmentsNeedingReminder();
        return ResponseEntity.ok(appointments);
    }

    /**
     * 获取过期预约
     */
    @GetMapping("/expired")
    public ResponseEntity<List<Appointment>> getExpiredAppointments() {
        List<Appointment> appointments = appointmentService.getExpiredAppointments();
        return ResponseEntity.ok(appointments);
    }

    /**
     * 获取预约统计信息
     */
    @GetMapping("/statistics")
    @RequiresPermission("APPOINTMENT_VIEW")
    public ResponseEntity<Map<String, Object>> getAppointmentStatistics() {
        Map<String, Object> statistics = new HashMap<>();
        
        // 今日预约数量
        Long todayCount = appointmentService.countAppointmentsByDate(LocalDate.now());
        statistics.put("todayCount", todayCount);
        
        // 明日预约数量
        Long tomorrowCount = appointmentService.countAppointmentsByDate(LocalDate.now().plusDays(1));
        statistics.put("tomorrowCount", tomorrowCount);
        
        // 各状态预约数量
        List<Object> statusStats = appointmentService.countAppointmentsByStatus();
        statistics.put("appointmentsByStatus", statusStats);
        
        // 各预约类型数量
        List<Object> typeStats = appointmentService.countAppointmentsByType();
        statistics.put("appointmentsByType", typeStats);
        
        // 需要提醒的预约数量
        List<Appointment> reminders = appointmentService.getAppointmentsNeedingReminder();
        statistics.put("reminderCount", reminders.size());
        
        // 过期预约数量
        List<Appointment> expired = appointmentService.getExpiredAppointments();
        statistics.put("expiredCount", expired.size());
        
        return ResponseEntity.ok(statistics);
    }

    /**
     * 获取患者最近的预约
     */
    @GetMapping("/patient/{patientId}/latest")
    public ResponseEntity<Appointment> getLatestAppointmentByPatient(@PathVariable Long patientId) {
        Appointment appointment = appointmentService.getLatestAppointmentByPatient(patientId);
        return ResponseEntity.ok(appointment);
    }

    /**
     * 获取医生的下一个预约
     */
    @GetMapping("/doctor/{doctorId}/next")
    public ResponseEntity<Appointment> getNextAppointmentByDoctor(@PathVariable Long doctorId) {
        Appointment appointment = appointmentService.getNextAppointmentByDoctor(doctorId);
        return ResponseEntity.ok(appointment);
    }

    /**
     * 批量处理过期预约
     */
    @PostMapping("/process-expired")
    public ResponseEntity<Map<String, String>> processExpiredAppointments() {
        appointmentService.processExpiredAppointments();
        Map<String, String> response = new HashMap<>();
        response.put("message", "过期预约处理完成");
        return ResponseEntity.ok(response);
    }

    /**
     * 发送预约提醒
     */
    @PostMapping("/send-reminders")
    public ResponseEntity<Map<String, String>> sendAppointmentReminders() {
        appointmentService.sendAppointmentReminders();
        Map<String, String> response = new HashMap<>();
        response.put("message", "预约提醒发送完成");
        return ResponseEntity.ok(response);
    }
}