package org.me.joy.clinic.controller;

import org.me.joy.clinic.dto.DoctorPerformanceReport;
import org.me.joy.clinic.dto.PatientVisitAnalytics;
import org.me.joy.clinic.dto.PopularService;
import org.me.joy.clinic.dto.WaitTimeAnalytics;
import org.me.joy.clinic.security.RequiresPermission;
import org.me.joy.clinic.service.OperationalAnalyticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * 运营分析控制器
 */
@RestController
@RequestMapping("/api/analytics/operational")
public class OperationalAnalyticsController {

    @Autowired
    private OperationalAnalyticsService operationalAnalyticsService;

    /**
     * 获取患者就诊量分析
     */
    @GetMapping("/patient-visits")
    @RequiresPermission("ANALYTICS_READ")
    public ResponseEntity<PatientVisitAnalytics> getPatientVisitAnalytics(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        PatientVisitAnalytics analytics = operationalAnalyticsService.getPatientVisitAnalytics(startDate, endDate);
        return ResponseEntity.ok(analytics);
    }

    /**
     * 获取热门服务列表
     */
    @GetMapping("/popular-services")
    @RequiresPermission("ANALYTICS_READ")
    public ResponseEntity<List<PopularService>> getPopularServices(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        List<PopularService> services = operationalAnalyticsService.getPopularServices(startDate, endDate);
        return ResponseEntity.ok(services);
    }

    /**
     * 获取医生绩效报表
     */
    @GetMapping("/doctor-performance/{doctorId}")
    @RequiresPermission("ANALYTICS_READ")
    public ResponseEntity<DoctorPerformanceReport> getDoctorPerformanceReport(
            @PathVariable Long doctorId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        DoctorPerformanceReport report = operationalAnalyticsService.getDoctorPerformanceReport(doctorId, startDate, endDate);
        if (report == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(report);
    }

    /**
     * 获取所有医生绩效报表
     */
    @GetMapping("/doctor-performance")
    @RequiresPermission("ANALYTICS_READ")
    public ResponseEntity<List<DoctorPerformanceReport>> getAllDoctorsPerformanceReport(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        List<DoctorPerformanceReport> reports = operationalAnalyticsService.getAllDoctorsPerformanceReport(startDate, endDate);
        return ResponseEntity.ok(reports);
    }

    /**
     * 获取等待时间分析
     */
    @GetMapping("/wait-time")
    @RequiresPermission("ANALYTICS_READ")
    public ResponseEntity<WaitTimeAnalytics> getWaitTimeAnalytics(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        WaitTimeAnalytics analytics = operationalAnalyticsService.getWaitTimeAnalytics(startDate, endDate);
        return ResponseEntity.ok(analytics);
    }

    /**
     * 获取部门就诊量统计
     */
    @GetMapping("/department-visits")
    @RequiresPermission("ANALYTICS_READ")
    public ResponseEntity<List<PopularService>> getDepartmentVisitStatistics(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        List<PopularService> statistics = operationalAnalyticsService.getDepartmentVisitStatistics(startDate, endDate);
        return ResponseEntity.ok(statistics);
    }
}