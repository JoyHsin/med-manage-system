package org.me.joy.clinic.controller;

import jakarta.validation.Valid;
import org.me.joy.clinic.dto.VitalSignsRequest;
import org.me.joy.clinic.entity.VitalSigns;
import org.me.joy.clinic.security.CustomUserPrincipal;
import org.me.joy.clinic.security.RequiresPermission;
import org.me.joy.clinic.service.VitalSignsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 生命体征管理控制器
 * 
 * @author Kiro
 */
@RestController
@RequestMapping("/api/vital-signs")
public class VitalSignsController {

    @Autowired
    private VitalSignsService vitalSignsService;

    /**
     * 录入生命体征
     */
    @PostMapping
    @RequiresPermission("VITAL_SIGNS_RECORD")
    public ResponseEntity<Map<String, Object>> recordVitalSigns(
            @Valid @RequestBody VitalSignsRequest request,
            @AuthenticationPrincipal CustomUserPrincipal currentUser) {
        
        // 验证生命体征数据并获取警告信息
        VitalSignsService.VitalSignsValidationResult validationResult = 
            vitalSignsService.validateVitalSigns(request);
        
        // 录入生命体征
        VitalSigns vitalSigns = vitalSignsService.recordVitalSigns(request, currentUser.getUserId());
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "生命体征录入成功");
        response.put("data", vitalSigns);
        
        // 如果有警告信息，一并返回
        if (validationResult.isHasWarnings()) {
            response.put("warnings", validationResult.getWarnings());
        }
        
        return ResponseEntity.ok(response);
    }

    /**
     * 获取生命体征记录详情
     */
    @GetMapping("/{id}")
    @RequiresPermission("VITAL_SIGNS_VIEW")
    public ResponseEntity<Map<String, Object>> getVitalSigns(@PathVariable Long id) {
        VitalSigns vitalSigns = vitalSignsService.getVitalSignsById(id);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", vitalSigns);
        
        return ResponseEntity.ok(response);
    }

    /**
     * 获取患者的生命体征记录列表
     */
    @GetMapping("/patient/{patientId}")
    @RequiresPermission("VITAL_SIGNS_VIEW")
    public ResponseEntity<Map<String, Object>> getPatientVitalSigns(@PathVariable Long patientId) {
        List<VitalSigns> vitalSignsList = vitalSignsService.getPatientVitalSigns(patientId);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", vitalSignsList);
        response.put("total", vitalSignsList.size());
        
        return ResponseEntity.ok(response);
    }

    /**
     * 获取患者指定时间范围内的生命体征记录
     */
    @GetMapping("/patient/{patientId}/range")
    @RequiresPermission("VITAL_SIGNS_VIEW")
    public ResponseEntity<Map<String, Object>> getPatientVitalSignsByTimeRange(
            @PathVariable Long patientId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime) {
        
        List<VitalSigns> vitalSignsList = vitalSignsService.getPatientVitalSignsByTimeRange(
            patientId, startTime, endTime);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", vitalSignsList);
        response.put("total", vitalSignsList.size());
        
        return ResponseEntity.ok(response);
    }

    /**
     * 获取患者最新的生命体征记录
     */
    @GetMapping("/patient/{patientId}/latest")
    @RequiresPermission("VITAL_SIGNS_VIEW")
    public ResponseEntity<Map<String, Object>> getLatestVitalSigns(@PathVariable Long patientId) {
        VitalSigns vitalSigns = vitalSignsService.getLatestVitalSigns(patientId);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", vitalSigns);
        
        return ResponseEntity.ok(response);
    }

    /**
     * 获取异常生命体征记录
     */
    @GetMapping("/abnormal")
    @RequiresPermission("VITAL_SIGNS_VIEW")
    public ResponseEntity<Map<String, Object>> getAbnormalVitalSigns(
            @RequestParam(required = false) Long patientId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime) {
        
        List<VitalSigns> abnormalVitalSigns = vitalSignsService.getAbnormalVitalSigns(
            patientId, startTime, endTime);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", abnormalVitalSigns);
        response.put("total", abnormalVitalSigns.size());
        
        return ResponseEntity.ok(response);
    }

    /**
     * 更新生命体征记录
     */
    @PutMapping("/{id}")
    @RequiresPermission("VITAL_SIGNS_UPDATE")
    public ResponseEntity<Map<String, Object>> updateVitalSigns(
            @PathVariable Long id,
            @Valid @RequestBody VitalSignsRequest request,
            @AuthenticationPrincipal CustomUserPrincipal currentUser) {
        
        // 验证生命体征数据并获取警告信息
        VitalSignsService.VitalSignsValidationResult validationResult = 
            vitalSignsService.validateVitalSigns(request);
        
        // 更新生命体征
        VitalSigns vitalSigns = vitalSignsService.updateVitalSigns(id, request, currentUser.getUserId());
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "生命体征更新成功");
        response.put("data", vitalSigns);
        
        // 如果有警告信息，一并返回
        if (validationResult.isHasWarnings()) {
            response.put("warnings", validationResult.getWarnings());
        }
        
        return ResponseEntity.ok(response);
    }

    /**
     * 删除生命体征记录
     */
    @DeleteMapping("/{id}")
    @RequiresPermission("VITAL_SIGNS_UPDATE")
    public ResponseEntity<Map<String, Object>> deleteVitalSigns(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserPrincipal currentUser) {
        
        vitalSignsService.deleteVitalSigns(id, currentUser.getUserId());
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "生命体征记录删除成功");
        
        return ResponseEntity.ok(response);
    }

    /**
     * 验证生命体征数据
     */
    @PostMapping("/validate")
    @RequiresPermission("VITAL_SIGNS_RECORD")
    public ResponseEntity<Map<String, Object>> validateVitalSigns(
            @Valid @RequestBody VitalSignsRequest request) {
        
        VitalSignsService.VitalSignsValidationResult validationResult = 
            vitalSignsService.validateVitalSigns(request);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("valid", validationResult.isValid());
        response.put("hasWarnings", validationResult.isHasWarnings());
        
        if (validationResult.isHasWarnings()) {
            response.put("warnings", validationResult.getWarnings());
        }
        
        if (!validationResult.isValid()) {
            response.put("errors", validationResult.getErrors());
        }
        
        return ResponseEntity.ok(response);
    }

    /**
     * 获取记录人员的生命体征记录
     */
    @GetMapping("/recorder/{recordedBy}")
    @RequiresPermission("VITAL_SIGNS_VIEW")
    public ResponseEntity<Map<String, Object>> getVitalSignsByRecorder(
            @PathVariable Long recordedBy,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime) {
        
        List<VitalSigns> vitalSignsList = vitalSignsService.getVitalSignsByRecorder(
            recordedBy, startTime, endTime);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", vitalSignsList);
        response.put("total", vitalSignsList.size());
        
        return ResponseEntity.ok(response);
    }
}