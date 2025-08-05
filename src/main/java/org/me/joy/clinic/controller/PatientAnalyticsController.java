package org.me.joy.clinic.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.me.joy.clinic.dto.CommonDiagnosis;
import org.me.joy.clinic.dto.PatientDemographics;
import org.me.joy.clinic.dto.PatientRetentionReport;
import org.me.joy.clinic.dto.PatientSatisfactionScore;
import org.me.joy.clinic.security.RequiresPermission;
import org.me.joy.clinic.service.PatientAnalyticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * 患者分析控制器
 */
@Tag(name = "患者分析", description = "患者统计分析相关API")
@RestController
@RequestMapping("/api/analytics/patient")
public class PatientAnalyticsController {
    
    @Autowired
    private PatientAnalyticsService patientAnalyticsService;
    
    /**
     * 获取患者人口统计数据
     */
    @Operation(summary = "获取患者人口统计数据", description = "获取患者的年龄、性别、地区分布等人口统计信息")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "成功获取患者统计数据"),
        @ApiResponse(responseCode = "403", description = "权限不足")
    })
    @GetMapping("/demographics")
    @RequiresPermission("PATIENT_ANALYTICS_READ")
    public ResponseEntity<PatientDemographics> getPatientDemographics() {
        PatientDemographics demographics = patientAnalyticsService.getPatientDemographics();
        return ResponseEntity.ok(demographics);
    }
    
    /**
     * 获取常见诊断统计
     */
    @Operation(summary = "获取常见诊断统计", description = "获取指定时间范围内的常见诊断统计信息")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "成功获取诊断统计数据"),
        @ApiResponse(responseCode = "400", description = "请求参数错误"),
        @ApiResponse(responseCode = "403", description = "权限不足")
    })
    @GetMapping("/common-diagnoses")
    @RequiresPermission("PATIENT_ANALYTICS_READ")
    public ResponseEntity<List<CommonDiagnosis>> getCommonDiagnoses(
            @Parameter(description = "开始日期", required = true, example = "2024-01-01")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "结束日期", required = true, example = "2024-12-31")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        List<CommonDiagnosis> diagnoses = patientAnalyticsService.getCommonDiagnoses(startDate, endDate);
        return ResponseEntity.ok(diagnoses);
    }
    
    /**
     * 获取患者留存报告
     */
    @Operation(summary = "获取患者留存报告", description = "获取患者留存分析报告，包含新患者数量、回访患者数量和留存率")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "成功获取留存报告"),
        @ApiResponse(responseCode = "403", description = "权限不足")
    })
    @GetMapping("/retention-report")
    @RequiresPermission("PATIENT_ANALYTICS_READ")
    public ResponseEntity<PatientRetentionReport> getPatientRetentionReport() {
        PatientRetentionReport report = patientAnalyticsService.getPatientRetentionReport();
        return ResponseEntity.ok(report);
    }
    
    /**
     * 获取患者满意度评分
     */
    @Operation(summary = "获取患者满意度评分", description = "获取患者满意度调查评分统计（当前未实现，需要创建满意度调查表）")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "成功获取满意度评分"),
        @ApiResponse(responseCode = "403", description = "权限不足"),
        @ApiResponse(responseCode = "501", description = "功能未实现")
    })
    @GetMapping("/satisfaction-scores")
    @RequiresPermission("PATIENT_ANALYTICS_READ")
    public ResponseEntity<List<PatientSatisfactionScore>> getPatientSatisfactionScores() {
        List<PatientSatisfactionScore> scores = patientAnalyticsService.getPatientSatisfactionScores();
        return ResponseEntity.ok(scores);
    }
}