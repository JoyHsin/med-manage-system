package org.me.joy.clinic.controller;

import org.me.joy.clinic.dto.CallPatientRequest;
import org.me.joy.clinic.dto.PatientQueueResponse;
import org.me.joy.clinic.entity.PatientQueue;
import org.me.joy.clinic.security.RequiresPermission;
import org.me.joy.clinic.service.TriageService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * 分诊叫号控制器
 */
@RestController
@RequestMapping("/triage")
public class TriageController {

    private static final Logger log = Logger.getLogger(TriageController.class.getName());

    private final TriageService triageService;

    @Autowired
    public TriageController(TriageService triageService) {
        this.triageService = triageService;
    }

    /**
     * 获取当日患者队列
     */
    @GetMapping("/queue/today")
    @RequiresPermission("TRIAGE_MANAGEMENT")
    public ResponseEntity<List<PatientQueueResponse>> getTodayPatientQueue() {
        log.info("获取当日患者队列");
        List<PatientQueue> patientQueues = triageService.getTodayPatientQueue();
        List<PatientQueueResponse> responses = patientQueues.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    /**
     * 获取指定日期的患者队列
     */
    @GetMapping("/queue")
    @RequiresPermission("TRIAGE_MANAGEMENT")
    public ResponseEntity<List<PatientQueueResponse>> getPatientQueueByDate(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate queueDate) {
        log.info("获取指定日期的患者队列，日期: " + queueDate);
        List<PatientQueue> patientQueues = triageService.getPatientQueueByDate(queueDate);
        List<PatientQueueResponse> responses = patientQueues.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    /**
     * 获取指定状态的患者队列
     */
    @GetMapping("/queue/status/{status}")
    @RequiresPermission("TRIAGE_MANAGEMENT")
    public ResponseEntity<List<PatientQueueResponse>> getPatientQueueByStatus(
            @PathVariable String status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate queueDate) {
        LocalDate targetDate = queueDate != null ? queueDate : LocalDate.now();
        log.info("获取指定状态的患者队列，日期: " + targetDate + ", 状态: " + status);
        List<PatientQueue> patientQueues = triageService.getPatientQueueByStatus(targetDate, status);
        List<PatientQueueResponse> responses = patientQueues.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    /**
     * 获取下一个待叫号的患者
     */
    @GetMapping("/queue/next")
    @RequiresPermission("TRIAGE_MANAGEMENT")
    public ResponseEntity<PatientQueueResponse> getNextPatient(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate queueDate) {
        LocalDate targetDate = queueDate != null ? queueDate : LocalDate.now();
        log.info("获取下一个待叫号患者，日期: " + targetDate);
        PatientQueue nextPatient = triageService.getNextPatient(targetDate);
        if (nextPatient == null) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(convertToResponse(nextPatient));
    }

    /**
     * 叫号
     */
    @PostMapping("/call")
    @RequiresPermission("TRIAGE_MANAGEMENT")
    public ResponseEntity<Void> callPatient(@Valid @RequestBody CallPatientRequest request) {
        log.info("叫号患者，请求: " + request);
        triageService.callPatient(request.getPatientQueueId(), request.getCalledBy());
        return ResponseEntity.ok().build();
    }

    /**
     * 确认患者到达
     */
    @PostMapping("/confirm-arrival")
    @RequiresPermission("TRIAGE_MANAGEMENT")
    public ResponseEntity<Void> confirmPatientArrival(@Valid @RequestBody CallPatientRequest request) {
        log.info("确认患者到达，请求: " + request);
        triageService.confirmPatientArrival(request.getPatientQueueId(), request.getCalledBy());
        return ResponseEntity.ok().build();
    }

    /**
     * 标记患者未到
     */
    @PostMapping("/mark-absent")
    @RequiresPermission("TRIAGE_MANAGEMENT")
    public ResponseEntity<Void> markPatientAbsent(@Valid @RequestBody CallPatientRequest request) {
        log.info("标记患者未到，请求: " + request);
        triageService.markPatientAbsent(request.getPatientQueueId(), request.getCalledBy());
        return ResponseEntity.ok().build();
    }

    /**
     * 重新叫号
     */
    @PostMapping("/recall")
    @RequiresPermission("TRIAGE_MANAGEMENT")
    public ResponseEntity<Void> recallPatient(@Valid @RequestBody CallPatientRequest request) {
        log.info("重新叫号患者，请求: " + request);
        triageService.recallPatient(request.getPatientQueueId(), request.getCalledBy());
        return ResponseEntity.ok().build();
    }

    /**
     * 完成患者就诊
     */
    @PostMapping("/complete/{patientQueueId}")
    @RequiresPermission("TRIAGE_MANAGEMENT")
    public ResponseEntity<Void> completePatient(@PathVariable Long patientQueueId) {
        log.info("完成患者就诊，队列ID: " + patientQueueId);
        triageService.completePatient(patientQueueId);
        return ResponseEntity.ok().build();
    }

    /**
     * 创建患者队列记录
     */
    @PostMapping("/queue")
    @RequiresPermission("TRIAGE_MANAGEMENT")
    public ResponseEntity<PatientQueueResponse> createPatientQueue(
            @RequestParam Long registrationId,
            @RequestParam(required = false) Integer priority) {
        log.info("创建患者队列记录，挂号ID: " + registrationId + ", 优先级: " + priority);
        PatientQueue patientQueue = triageService.createPatientQueue(registrationId, priority);
        return ResponseEntity.ok(convertToResponse(patientQueue));
    }

    /**
     * 转换为响应对象
     */
    private PatientQueueResponse convertToResponse(PatientQueue patientQueue) {
        PatientQueueResponse response = new PatientQueueResponse();
        BeanUtils.copyProperties(patientQueue, response);
        return response;
    }
}