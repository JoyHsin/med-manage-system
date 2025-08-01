package org.me.joy.clinic.controller;

import jakarta.validation.Valid;
import org.me.joy.clinic.dto.CreateStaffRequest;
import org.me.joy.clinic.dto.StaffResponse;
import org.me.joy.clinic.dto.UpdateStaffRequest;
import org.me.joy.clinic.entity.Schedule;
import org.me.joy.clinic.service.StaffManagementService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * 医护人员管理控制器
 * 提供医护人员管理的REST API接口
 */
@RestController
@RequestMapping("/api/staff")
public class StaffManagementController {

    private static final Logger logger = LoggerFactory.getLogger(StaffManagementController.class);

    @Autowired
    private StaffManagementService staffManagementService;

    /**
     * 创建新员工
     */
    @PostMapping
    @PreAuthorize("hasAuthority('STAFF_CREATE')")
    public ResponseEntity<StaffResponse> createStaff(@Valid @RequestBody CreateStaffRequest createStaffRequest) {
        logger.info("创建员工请求: {}", createStaffRequest.getName());
        
        StaffResponse response = staffManagementService.createStaff(createStaffRequest);
        
        logger.info("员工创建成功: {}", response.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * 更新员工信息
     */
    @PutMapping("/{staffId}")
    @PreAuthorize("hasAuthority('STAFF_UPDATE')")
    public ResponseEntity<StaffResponse> updateStaff(
            @PathVariable Long staffId,
            @Valid @RequestBody UpdateStaffRequest updateStaffRequest) {
        logger.info("更新员工请求: staffId={}", staffId);
        
        StaffResponse response = staffManagementService.updateStaff(staffId, updateStaffRequest);
        
        logger.info("员工更新成功: {}", staffId);
        return ResponseEntity.ok(response);
    }

    /**
     * 根据ID获取员工信息
     */
    @GetMapping("/{staffId}")
    @PreAuthorize("hasAuthority('STAFF_VIEW')")
    public ResponseEntity<StaffResponse> getStaffById(@PathVariable Long staffId) {
        logger.debug("获取员工信息: staffId={}", staffId);
        
        StaffResponse response = staffManagementService.getStaffById(staffId);
        
        return ResponseEntity.ok(response);
    }

    /**
     * 根据员工编号获取员工信息
     */
    @GetMapping("/number/{staffNumber}")
    @PreAuthorize("hasAuthority('STAFF_VIEW')")
    public ResponseEntity<StaffResponse> getStaffByNumber(@PathVariable String staffNumber) {
        logger.debug("根据员工编号获取员工信息: staffNumber={}", staffNumber);
        
        StaffResponse response = staffManagementService.getStaffByNumber(staffNumber);
        
        return ResponseEntity.ok(response);
    }

    /**
     * 根据身份证号获取员工信息
     */
    @GetMapping("/idcard/{idCard}")
    @PreAuthorize("hasAuthority('STAFF_VIEW')")
    public ResponseEntity<StaffResponse> getStaffByIdCard(@PathVariable String idCard) {
        logger.debug("根据身份证号获取员工信息: idCard={}", idCard);
        
        StaffResponse response = staffManagementService.getStaffByIdCard(idCard);
        
        return ResponseEntity.ok(response);
    }

    /**
     * 根据用户ID获取员工信息
     */
    @GetMapping("/user/{userId}")
    @PreAuthorize("hasAuthority('STAFF_VIEW')")
    public ResponseEntity<StaffResponse> getStaffByUserId(@PathVariable Long userId) {
        logger.debug("根据用户ID获取员工信息: userId={}", userId);
        
        StaffResponse response = staffManagementService.getStaffByUserId(userId);
        
        return ResponseEntity.ok(response);
    }

    /**
     * 获取所有员工列表
     */
    @GetMapping
    @PreAuthorize("hasAuthority('STAFF_VIEW')")
    public ResponseEntity<List<StaffResponse>> getAllStaff() {
        logger.debug("获取所有员工列表");
        
        List<StaffResponse> responses = staffManagementService.getAllStaff();
        
        return ResponseEntity.ok(responses);
    }

    /**
     * 根据职位获取员工列表
     */
    @GetMapping("/position/{position}")
    @PreAuthorize("hasAuthority('STAFF_VIEW')")
    public ResponseEntity<List<StaffResponse>> getStaffByPosition(@PathVariable String position) {
        logger.debug("根据职位获取员工列表: position={}", position);
        
        List<StaffResponse> responses = staffManagementService.getStaffByPosition(position);
        
        return ResponseEntity.ok(responses);
    }

    /**
     * 根据科室获取员工列表
     */
    @GetMapping("/department/{department}")
    @PreAuthorize("hasAuthority('STAFF_VIEW')")
    public ResponseEntity<List<StaffResponse>> getStaffByDepartment(@PathVariable String department) {
        logger.debug("根据科室获取员工列表: department={}", department);
        
        List<StaffResponse> responses = staffManagementService.getStaffByDepartment(department);
        
        return ResponseEntity.ok(responses);
    }

    /**
     * 根据工作状态获取员工列表
     */
    @GetMapping("/status/{workStatus}")
    @PreAuthorize("hasAuthority('STAFF_VIEW')")
    public ResponseEntity<List<StaffResponse>> getStaffByWorkStatus(@PathVariable String workStatus) {
        logger.debug("根据工作状态获取员工列表: workStatus={}", workStatus);
        
        List<StaffResponse> responses = staffManagementService.getStaffByWorkStatus(workStatus);
        
        return ResponseEntity.ok(responses);
    }

    /**
     * 获取在职员工列表
     */
    @GetMapping("/active")
    @PreAuthorize("hasAuthority('STAFF_VIEW')")
    public ResponseEntity<List<StaffResponse>> getActiveStaff() {
        logger.debug("获取在职员工列表");
        
        List<StaffResponse> responses = staffManagementService.getActiveStaff();
        
        return ResponseEntity.ok(responses);
    }

    /**
     * 获取可排班员工列表
     */
    @GetMapping("/schedulable")
    @PreAuthorize("hasAuthority('STAFF_VIEW')")
    public ResponseEntity<List<StaffResponse>> getSchedulableStaff() {
        logger.debug("获取可排班员工列表");
        
        List<StaffResponse> responses = staffManagementService.getSchedulableStaff();
        
        return ResponseEntity.ok(responses);
    }

    /**
     * 搜索员工
     */
    @GetMapping("/search")
    @PreAuthorize("hasAuthority('STAFF_VIEW')")
    public ResponseEntity<List<StaffResponse>> searchStaff(@RequestParam String keyword) {
        logger.debug("搜索员工: keyword={}", keyword);
        
        List<StaffResponse> responses = staffManagementService.searchStaff(keyword);
        
        return ResponseEntity.ok(responses);
    }

    /**
     * 获取执业证书即将过期的员工
     */
    @GetMapping("/license-expiring")
    @PreAuthorize("hasAuthority('STAFF_VIEW')")
    public ResponseEntity<List<StaffResponse>> getStaffWithExpiringLicense(
            @RequestParam(defaultValue = "30") Integer days) {
        logger.debug("获取执业证书即将过期的员工: days={}", days);
        
        List<StaffResponse> responses = staffManagementService.getStaffWithExpiringLicense(days);
        
        return ResponseEntity.ok(responses);
    }

    /**
     * 删除员工
     */
    @DeleteMapping("/{staffId}")
    @PreAuthorize("hasAuthority('STAFF_DELETE')")
    public ResponseEntity<Void> deleteStaff(@PathVariable Long staffId) {
        logger.info("删除员工请求: staffId={}", staffId);
        
        staffManagementService.deleteStaff(staffId);
        
        logger.info("员工删除成功: {}", staffId);
        return ResponseEntity.noContent().build();
    }

    /**
     * 更新员工工作状态
     */
    @PutMapping("/{staffId}/status")
    @PreAuthorize("hasAuthority('STAFF_UPDATE')")
    public ResponseEntity<Void> updateStaffWorkStatus(
            @PathVariable Long staffId,
            @RequestBody Map<String, String> request) {
        String workStatus = request.get("workStatus");
        logger.info("更新员工工作状态: staffId={}, workStatus={}", staffId, workStatus);
        
        staffManagementService.updateStaffWorkStatus(staffId, workStatus);
        
        logger.info("员工工作状态更新成功: staffId={}, workStatus={}", staffId, workStatus);
        return ResponseEntity.ok().build();
    }

    /**
     * 员工离职处理
     */
    @PostMapping("/{staffId}/resign")
    @PreAuthorize("hasAuthority('STAFF_UPDATE')")
    public ResponseEntity<Void> resignStaff(
            @PathVariable Long staffId,
            @RequestBody(required = false) Map<String, String> request) {
        LocalDate resignationDate = null;
        if (request != null && request.containsKey("resignationDate")) {
            resignationDate = LocalDate.parse(request.get("resignationDate"));
        }
        
        logger.info("员工离职处理: staffId={}, resignationDate={}", staffId, resignationDate);
        
        staffManagementService.resignStaff(staffId, resignationDate);
        
        logger.info("员工离职处理完成: {}", staffId);
        return ResponseEntity.ok().build();
    }

    /**
     * 员工复职处理
     */
    @PostMapping("/{staffId}/reinstate")
    @PreAuthorize("hasAuthority('STAFF_UPDATE')")
    public ResponseEntity<Void> reinstateStaff(@PathVariable Long staffId) {
        logger.info("员工复职处理: staffId={}", staffId);
        
        staffManagementService.reinstateStaff(staffId);
        
        logger.info("员工复职处理完成: {}", staffId);
        return ResponseEntity.ok().build();
    }

    /**
     * 更新员工排班状态
     */
    @PutMapping("/{staffId}/schedulable")
    @PreAuthorize("hasAuthority('STAFF_UPDATE')")
    public ResponseEntity<Void> updateStaffSchedulableStatus(
            @PathVariable Long staffId,
            @RequestBody Map<String, Boolean> request) {
        Boolean isSchedulable = request.get("isSchedulable");
        logger.info("更新员工排班状态: staffId={}, isSchedulable={}", staffId, isSchedulable);
        
        staffManagementService.updateStaffSchedulableStatus(staffId, isSchedulable);
        
        logger.info("员工排班状态更新成功: staffId={}, isSchedulable={}", staffId, isSchedulable);
        return ResponseEntity.ok().build();
    }

    /**
     * 创建员工排班
     */
    @PostMapping("/{staffId}/schedules")
    @PreAuthorize("hasAuthority('SCHEDULE_CREATE')")
    public ResponseEntity<Schedule> createSchedule(
            @PathVariable Long staffId,
            @Valid @RequestBody Schedule schedule) {
        logger.info("创建员工排班: staffId={}, date={}", staffId, schedule.getScheduleDate());
        
        Schedule response = staffManagementService.createSchedule(staffId, schedule);
        
        logger.info("员工排班创建成功: staffId={}, scheduleId={}", staffId, response.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * 更新员工排班
     */
    @PutMapping("/schedules/{scheduleId}")
    @PreAuthorize("hasAuthority('SCHEDULE_UPDATE')")
    public ResponseEntity<Schedule> updateSchedule(
            @PathVariable Long scheduleId,
            @Valid @RequestBody Schedule schedule) {
        logger.info("更新员工排班: scheduleId={}", scheduleId);
        
        Schedule response = staffManagementService.updateSchedule(scheduleId, schedule);
        
        logger.info("员工排班更新成功: {}", scheduleId);
        return ResponseEntity.ok(response);
    }

    /**
     * 删除员工排班
     */
    @DeleteMapping("/schedules/{scheduleId}")
    @PreAuthorize("hasAuthority('SCHEDULE_DELETE')")
    public ResponseEntity<Void> deleteSchedule(@PathVariable Long scheduleId) {
        logger.info("删除员工排班: scheduleId={}", scheduleId);
        
        staffManagementService.deleteSchedule(scheduleId);
        
        logger.info("员工排班删除成功: {}", scheduleId);
        return ResponseEntity.noContent().build();
    }

    /**
     * 获取员工排班
     */
    @GetMapping("/{staffId}/schedules")
    @PreAuthorize("hasAuthority('SCHEDULE_VIEW')")
    public ResponseEntity<List<Schedule>> getStaffSchedules(@PathVariable Long staffId) {
        logger.debug("获取员工排班: staffId={}", staffId);
        
        List<Schedule> responses = staffManagementService.getStaffSchedules(staffId);
        
        return ResponseEntity.ok(responses);
    }

    /**
     * 获取员工指定日期范围的排班
     */
    @GetMapping("/{staffId}/schedules/range")
    @PreAuthorize("hasAuthority('SCHEDULE_VIEW')")
    public ResponseEntity<List<Schedule>> getStaffSchedulesByDateRange(
            @PathVariable Long staffId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        logger.debug("获取员工指定日期范围的排班: staffId={}, startDate={}, endDate={}", 
                    staffId, startDate, endDate);
        
        List<Schedule> responses = staffManagementService.getStaffSchedulesByDateRange(
                staffId, startDate, endDate);
        
        return ResponseEntity.ok(responses);
    }

    /**
     * 获取指定日期的所有排班
     */
    @GetMapping("/schedules/date/{date}")
    @PreAuthorize("hasAuthority('SCHEDULE_VIEW')")
    public ResponseEntity<List<Schedule>> getSchedulesByDate(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        logger.debug("获取指定日期的所有排班: date={}", date);
        
        List<Schedule> responses = staffManagementService.getSchedulesByDate(date);
        
        return ResponseEntity.ok(responses);
    }

    /**
     * 获取今日排班
     */
    @GetMapping("/schedules/today")
    @PreAuthorize("hasAuthority('SCHEDULE_VIEW')")
    public ResponseEntity<List<Schedule>> getTodaySchedules() {
        logger.debug("获取今日排班");
        
        List<Schedule> responses = staffManagementService.getTodaySchedules();
        
        return ResponseEntity.ok(responses);
    }

    /**
     * 获取本周排班
     */
    @GetMapping("/schedules/weekly")
    @PreAuthorize("hasAuthority('SCHEDULE_VIEW')")
    public ResponseEntity<List<Schedule>> getWeeklySchedules() {
        logger.debug("获取本周排班");
        
        List<Schedule> responses = staffManagementService.getWeeklySchedules();
        
        return ResponseEntity.ok(responses);
    }

    /**
     * 获取本月排班
     */
    @GetMapping("/schedules/monthly")
    @PreAuthorize("hasAuthority('SCHEDULE_VIEW')")
    public ResponseEntity<List<Schedule>> getMonthlySchedules() {
        logger.debug("获取本月排班");
        
        List<Schedule> responses = staffManagementService.getMonthlySchedules();
        
        return ResponseEntity.ok(responses);
    }

    /**
     * 批量创建排班
     */
    @PostMapping("/schedules/batch")
    @PreAuthorize("hasAuthority('SCHEDULE_CREATE')")
    public ResponseEntity<List<Schedule>> batchCreateSchedules(
            @Valid @RequestBody List<Schedule> schedules) {
        logger.info("批量创建排班: count={}", schedules.size());
        
        List<Schedule> responses = staffManagementService.batchCreateSchedules(schedules);
        
        logger.info("批量创建排班成功: count={}", responses.size());
        return ResponseEntity.status(HttpStatus.CREATED).body(responses);
    }

    /**
     * 获取员工统计信息
     */
    @GetMapping("/statistics")
    @PreAuthorize("hasAuthority('STAFF_VIEW')")
    public ResponseEntity<Map<String, Object>> getStaffStatistics() {
        logger.debug("获取员工统计信息");
        
        Long totalStaff = staffManagementService.countAllStaff();
        Long activeStaff = staffManagementService.countActiveStaff();
        Long doctorCount = staffManagementService.countStaffByPosition("医生");
        Long nurseCount = staffManagementService.countStaffByPosition("护士");
        
        Map<String, Object> statistics = Map.of(
                "totalStaff", totalStaff,
                "activeStaff", activeStaff,
                "doctorCount", doctorCount,
                "nurseCount", nurseCount
        );
        
        return ResponseEntity.ok(statistics);
    }
}