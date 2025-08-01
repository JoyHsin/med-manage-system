package org.me.joy.clinic.controller;

import jakarta.validation.Valid;
import org.me.joy.clinic.dto.CreateRegistrationRequest;
import org.me.joy.clinic.entity.Registration;
import org.me.joy.clinic.service.RegistrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 挂号管理控制器
 */
@RestController
@RequestMapping("/api/registrations")
public class RegistrationController {

    @Autowired
    private RegistrationService registrationService;

    /**
     * 创建挂号
     */
    @PostMapping
    public ResponseEntity<Registration> createRegistration(@Valid @RequestBody CreateRegistrationRequest request) {
        Registration registration = registrationService.createRegistration(request);
        return ResponseEntity.ok(registration);
    }

    /**
     * 根据ID获取挂号记录
     */
    @GetMapping("/{id}")
    public ResponseEntity<Registration> getRegistrationById(@PathVariable Long id) {
        Registration registration = registrationService.getRegistrationById(id);
        return ResponseEntity.ok(registration);
    }

    /**
     * 根据挂号编号获取挂号记录
     */
    @GetMapping("/number/{number}")
    public ResponseEntity<Registration> getRegistrationByNumber(@PathVariable String number) {
        Registration registration = registrationService.getRegistrationByNumber(number);
        return ResponseEntity.ok(registration);
    }

    /**
     * 根据患者ID获取挂号记录
     */
    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<Registration>> getRegistrationsByPatientId(@PathVariable Long patientId) {
        List<Registration> registrations = registrationService.getRegistrationsByPatientId(patientId);
        return ResponseEntity.ok(registrations);
    }

    /**
     * 根据日期获取挂号记录
     */
    @GetMapping("/date/{date}")
    public ResponseEntity<List<Registration>> getRegistrationsByDate(
            @PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) {
        List<Registration> registrations = registrationService.getRegistrationsByDate(date);
        return ResponseEntity.ok(registrations);
    }

    /**
     * 根据科室获取挂号记录
     */
    @GetMapping("/department/{department}")
    public ResponseEntity<List<Registration>> getRegistrationsByDepartment(@PathVariable String department) {
        List<Registration> registrations = registrationService.getRegistrationsByDepartment(department);
        return ResponseEntity.ok(registrations);
    }

    /**
     * 根据医生ID获取挂号记录
     */
    @GetMapping("/doctor/{doctorId}")
    public ResponseEntity<List<Registration>> getRegistrationsByDoctorId(@PathVariable Long doctorId) {
        List<Registration> registrations = registrationService.getRegistrationsByDoctorId(doctorId);
        return ResponseEntity.ok(registrations);
    }

    /**
     * 获取今日挂号记录
     */
    @GetMapping("/today")
    public ResponseEntity<List<Registration>> getTodayRegistrations() {
        List<Registration> registrations = registrationService.getTodayRegistrations();
        return ResponseEntity.ok(registrations);
    }

    /**
     * 获取今日指定科室的挂号记录
     */
    @GetMapping("/today/department/{department}")
    public ResponseEntity<List<Registration>> getTodayRegistrationsByDepartment(@PathVariable String department) {
        List<Registration> registrations = registrationService.getTodayRegistrationsByDepartment(department);
        return ResponseEntity.ok(registrations);
    }

    /**
     * 获取今日指定医生的挂号记录
     */
    @GetMapping("/today/doctor/{doctorId}")
    public ResponseEntity<List<Registration>> getTodayRegistrationsByDoctor(@PathVariable Long doctorId) {
        List<Registration> registrations = registrationService.getTodayRegistrationsByDoctor(doctorId);
        return ResponseEntity.ok(registrations);
    }

    /**
     * 获取待叫号的挂号记录
     */
    @GetMapping("/pending")
    public ResponseEntity<List<Registration>> getPendingRegistrations() {
        List<Registration> registrations = registrationService.getPendingRegistrations();
        return ResponseEntity.ok(registrations);
    }

    /**
     * 获取指定科室待叫号的挂号记录
     */
    @GetMapping("/pending/department/{department}")
    public ResponseEntity<List<Registration>> getPendingRegistrationsByDepartment(@PathVariable String department) {
        List<Registration> registrations = registrationService.getPendingRegistrationsByDepartment(department);
        return ResponseEntity.ok(registrations);
    }

    /**
     * 获取指定医生待叫号的挂号记录
     */
    @GetMapping("/pending/doctor/{doctorId}")
    public ResponseEntity<List<Registration>> getPendingRegistrationsByDoctor(@PathVariable Long doctorId) {
        List<Registration> registrations = registrationService.getPendingRegistrationsByDoctor(doctorId);
        return ResponseEntity.ok(registrations);
    }

    /**
     * 获取已叫号但未到达的挂号记录
     */
    @GetMapping("/called-not-arrived")
    public ResponseEntity<List<Registration>> getCalledButNotArrivedRegistrations() {
        List<Registration> registrations = registrationService.getCalledButNotArrivedRegistrations();
        return ResponseEntity.ok(registrations);
    }

    /**
     * 获取正在就诊的挂号记录
     */
    @GetMapping("/active-consultations")
    public ResponseEntity<List<Registration>> getActiveConsultations() {
        List<Registration> registrations = registrationService.getActiveConsultations();
        return ResponseEntity.ok(registrations);
    }

    /**
     * 叫号
     */
    @PostMapping("/{id}/call")
    public ResponseEntity<Map<String, String>> callPatient(@PathVariable Long id) {
        registrationService.callPatient(id);
        Map<String, String> response = new HashMap<>();
        response.put("message", "叫号成功");
        return ResponseEntity.ok(response);
    }

    /**
     * 标记患者到达
     */
    @PostMapping("/{id}/arrive")
    public ResponseEntity<Map<String, String>> markPatientArrived(@PathVariable Long id) {
        registrationService.markPatientArrived(id);
        Map<String, String> response = new HashMap<>();
        response.put("message", "患者到达标记成功");
        return ResponseEntity.ok(response);
    }

    /**
     * 开始就诊
     */
    @PostMapping("/{id}/start")
    public ResponseEntity<Map<String, String>> startConsultation(@PathVariable Long id) {
        registrationService.startConsultation(id);
        Map<String, String> response = new HashMap<>();
        response.put("message", "就诊开始");
        return ResponseEntity.ok(response);
    }

    /**
     * 完成就诊
     */
    @PostMapping("/{id}/complete")
    public ResponseEntity<Map<String, String>> completeConsultation(@PathVariable Long id) {
        registrationService.completeConsultation(id);
        Map<String, String> response = new HashMap<>();
        response.put("message", "就诊完成");
        return ResponseEntity.ok(response);
    }

    /**
     * 取消挂号
     */
    @PostMapping("/{id}/cancel")
    public ResponseEntity<Map<String, String>> cancelRegistration(@PathVariable Long id, 
                                                                @RequestParam String reason) {
        registrationService.cancelRegistration(id, reason);
        Map<String, String> response = new HashMap<>();
        response.put("message", "挂号取消成功");
        return ResponseEntity.ok(response);
    }

    /**
     * 标记未到
     */
    @PostMapping("/{id}/no-show")
    public ResponseEntity<Map<String, String>> markNoShow(@PathVariable Long id) {
        registrationService.markNoShow(id);
        Map<String, String> response = new HashMap<>();
        response.put("message", "已标记为未到");
        return ResponseEntity.ok(response);
    }

    /**
     * 标记已支付
     */
    @PostMapping("/{id}/pay")
    public ResponseEntity<Map<String, String>> markAsPaid(@PathVariable Long id, 
                                                        @RequestParam String paymentMethod) {
        registrationService.markAsPaid(id, paymentMethod);
        Map<String, String> response = new HashMap<>();
        response.put("message", "支付标记成功");
        return ResponseEntity.ok(response);
    }

    /**
     * 更新挂号状态
     */
    @PutMapping("/{id}/status")
    public ResponseEntity<Map<String, String>> updateRegistrationStatus(@PathVariable Long id, 
                                                                       @RequestParam String status) {
        registrationService.updateRegistrationStatus(id, status);
        Map<String, String> response = new HashMap<>();
        response.put("message", "状态更新成功");
        return ResponseEntity.ok(response);
    }

    /**
     * 根据状态获取挂号记录
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<List<Registration>> getRegistrationsByStatus(@PathVariable String status) {
        List<Registration> registrations = registrationService.getRegistrationsByStatus(status);
        return ResponseEntity.ok(registrations);
    }

    /**
     * 根据支付状态获取挂号记录
     */
    @GetMapping("/payment-status/{paymentStatus}")
    public ResponseEntity<List<Registration>> getRegistrationsByPaymentStatus(@PathVariable String paymentStatus) {
        List<Registration> registrations = registrationService.getRegistrationsByPaymentStatus(paymentStatus);
        return ResponseEntity.ok(registrations);
    }

    /**
     * 获取急诊挂号记录
     */
    @GetMapping("/emergency")
    public ResponseEntity<List<Registration>> getEmergencyRegistrations() {
        List<Registration> registrations = registrationService.getEmergencyRegistrations();
        return ResponseEntity.ok(registrations);
    }

    /**
     * 获取初诊挂号记录
     */
    @GetMapping("/first-visit")
    public ResponseEntity<List<Registration>> getFirstVisitRegistrations() {
        List<Registration> registrations = registrationService.getFirstVisitRegistrations();
        return ResponseEntity.ok(registrations);
    }

    /**
     * 获取未支付的挂号记录
     */
    @GetMapping("/unpaid")
    public ResponseEntity<List<Registration>> getUnpaidRegistrations() {
        List<Registration> registrations = registrationService.getUnpaidRegistrations();
        return ResponseEntity.ok(registrations);
    }

    /**
     * 获取已完成的挂号记录
     */
    @GetMapping("/completed")
    public ResponseEntity<List<Registration>> getCompletedRegistrations() {
        List<Registration> registrations = registrationService.getCompletedRegistrations();
        return ResponseEntity.ok(registrations);
    }

    /**
     * 获取挂号统计信息
     */
    @GetMapping("/statistics")
    public ResponseEntity<Map<String, Object>> getRegistrationStatistics() {
        Map<String, Object> statistics = new HashMap<>();
        
        // 今日挂号数量
        Long todayCount = registrationService.countRegistrationsByDate(LocalDate.now());
        statistics.put("todayCount", todayCount);
        
        // 各状态挂号数量
        List<Object> statusStats = registrationService.countRegistrationsByStatus();
        statistics.put("registrationsByStatus", statusStats);
        
        // 各科室挂号数量
        List<Object> departmentStats = registrationService.countRegistrationsByDepartment();
        statistics.put("registrationsByDepartment", departmentStats);
        
        // 各挂号类型数量
        List<Object> typeStats = registrationService.countRegistrationsByType();
        statistics.put("registrationsByType", typeStats);
        
        // 各支付状态数量
        List<Object> paymentStats = registrationService.countRegistrationsByPaymentStatus();
        statistics.put("registrationsByPaymentStatus", paymentStats);
        
        // 待叫号数量
        List<Registration> pending = registrationService.getPendingRegistrations();
        statistics.put("pendingCount", pending.size());
        
        // 已叫号未到达数量
        List<Registration> calledNotArrived = registrationService.getCalledButNotArrivedRegistrations();
        statistics.put("calledNotArrivedCount", calledNotArrived.size());
        
        // 正在就诊数量
        List<Registration> activeConsultations = registrationService.getActiveConsultations();
        statistics.put("activeConsultationCount", activeConsultations.size());
        
        // 未支付数量
        List<Registration> unpaid = registrationService.getUnpaidRegistrations();
        statistics.put("unpaidCount", unpaid.size());
        
        return ResponseEntity.ok(statistics);
    }

    /**
     * 获取患者最近的挂号记录
     */
    @GetMapping("/patient/{patientId}/latest")
    public ResponseEntity<Registration> getLatestRegistrationByPatient(@PathVariable Long patientId) {
        Registration registration = registrationService.getLatestRegistrationByPatient(patientId);
        return ResponseEntity.ok(registration);
    }

    /**
     * 从预约创建挂号
     */
    @PostMapping("/from-appointment/{appointmentId}")
    public ResponseEntity<Registration> createRegistrationFromAppointment(@PathVariable Long appointmentId) {
        Registration registration = registrationService.createRegistrationFromAppointment(appointmentId);
        return ResponseEntity.ok(registration);
    }

    /**
     * 生成挂号编号
     */
    @GetMapping("/generate-number")
    public ResponseEntity<Map<String, String>> generateRegistrationNumber() {
        String number = registrationService.generateRegistrationNumber();
        Map<String, String> response = new HashMap<>();
        response.put("registrationNumber", number);
        return ResponseEntity.ok(response);
    }
}