package org.me.joy.clinic.controller;

import jakarta.validation.Valid;
import org.me.joy.clinic.dto.CreatePatientRequest;
import org.me.joy.clinic.dto.PatientResponse;
import org.me.joy.clinic.dto.UpdatePatientRequest;
import org.me.joy.clinic.entity.AllergyHistory;
import org.me.joy.clinic.entity.MedicalHistory;
import org.me.joy.clinic.service.PatientManagementService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * 患者管理控制器
 * 提供患者档案管理的REST API接口
 */
@RestController
@RequestMapping("/api/patients")
public class PatientManagementController {

    private static final Logger logger = LoggerFactory.getLogger(PatientManagementController.class);

    @Autowired
    private PatientManagementService patientManagementService;

    /**
     * 创建新患者
     */
    @PostMapping
    @PreAuthorize("hasAuthority('PATIENT_CREATE')")
    public ResponseEntity<PatientResponse> createPatient(@Valid @RequestBody CreatePatientRequest createPatientRequest) {
        logger.info("创建患者请求: {}", createPatientRequest.getName());
        
        PatientResponse response = patientManagementService.createPatient(createPatientRequest);
        
        logger.info("患者创建成功: {}", response.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * 更新患者信息
     */
    @PutMapping("/{patientId}")
    @PreAuthorize("hasAuthority('PATIENT_UPDATE')")
    public ResponseEntity<PatientResponse> updatePatient(
            @PathVariable Long patientId,
            @Valid @RequestBody UpdatePatientRequest updatePatientRequest) {
        logger.info("更新患者请求: patientId={}", patientId);
        
        PatientResponse response = patientManagementService.updatePatient(patientId, updatePatientRequest);
        
        logger.info("患者更新成功: {}", patientId);
        return ResponseEntity.ok(response);
    }

    /**
     * 根据ID获取患者信息
     */
    @GetMapping("/{patientId}")
    @PreAuthorize("hasAuthority('PATIENT_VIEW')")
    public ResponseEntity<PatientResponse> getPatientById(@PathVariable Long patientId) {
        logger.debug("获取患者信息: patientId={}", patientId);
        
        PatientResponse response = patientManagementService.getPatientById(patientId);
        
        return ResponseEntity.ok(response);
    }

    /**
     * 根据患者编号获取患者信息
     */
    @GetMapping("/number/{patientNumber}")
    @PreAuthorize("hasAuthority('PATIENT_VIEW')")
    public ResponseEntity<PatientResponse> getPatientByNumber(@PathVariable String patientNumber) {
        logger.debug("根据患者编号获取患者信息: patientNumber={}", patientNumber);
        
        PatientResponse response = patientManagementService.getPatientByNumber(patientNumber);
        
        return ResponseEntity.ok(response);
    }

    /**
     * 根据身份证号获取患者信息
     */
    @GetMapping("/idcard/{idCard}")
    @PreAuthorize("hasAuthority('PATIENT_VIEW')")
    public ResponseEntity<PatientResponse> getPatientByIdCard(@PathVariable String idCard) {
        logger.debug("根据身份证号获取患者信息: idCard={}", idCard);
        
        PatientResponse response = patientManagementService.getPatientByIdCard(idCard);
        
        return ResponseEntity.ok(response);
    }

    /**
     * 获取所有患者列表
     */
    @GetMapping
    @PreAuthorize("hasAuthority('PATIENT_VIEW')")
    public ResponseEntity<List<PatientResponse>> getAllPatients() {
        logger.debug("获取所有患者列表");
        
        List<PatientResponse> responses = patientManagementService.getAllPatients();
        
        return ResponseEntity.ok(responses);
    }

    /**
     * 根据状态获取患者列表
     */
    @GetMapping("/status/{status}")
    @PreAuthorize("hasAuthority('PATIENT_VIEW')")
    public ResponseEntity<List<PatientResponse>> getPatientsByStatus(@PathVariable String status) {
        logger.debug("根据状态获取患者列表: status={}", status);
        
        List<PatientResponse> responses = patientManagementService.getPatientsByStatus(status);
        
        return ResponseEntity.ok(responses);
    }

    /**
     * 获取VIP患者列表
     */
    @GetMapping("/vip")
    @PreAuthorize("hasAuthority('PATIENT_VIEW')")
    public ResponseEntity<List<PatientResponse>> getVipPatients() {
        logger.debug("获取VIP患者列表");
        
        List<PatientResponse> responses = patientManagementService.getVipPatients();
        
        return ResponseEntity.ok(responses);
    }

    /**
     * 根据性别获取患者列表
     */
    @GetMapping("/gender/{gender}")
    @PreAuthorize("hasAuthority('PATIENT_VIEW')")
    public ResponseEntity<List<PatientResponse>> getPatientsByGender(@PathVariable String gender) {
        logger.debug("根据性别获取患者列表: gender={}", gender);
        
        List<PatientResponse> responses = patientManagementService.getPatientsByGender(gender);
        
        return ResponseEntity.ok(responses);
    }

    /**
     * 根据年龄范围获取患者列表
     */
    @GetMapping("/age")
    @PreAuthorize("hasAuthority('PATIENT_VIEW')")
    public ResponseEntity<List<PatientResponse>> getPatientsByAgeRange(
            @RequestParam Integer minAge,
            @RequestParam Integer maxAge) {
        logger.debug("根据年龄范围获取患者列表: minAge={}, maxAge={}", minAge, maxAge);
        
        List<PatientResponse> responses = patientManagementService.getPatientsByAgeRange(minAge, maxAge);
        
        return ResponseEntity.ok(responses);
    }

    /**
     * 搜索患者
     */
    @GetMapping("/search")
    @PreAuthorize("hasAuthority('PATIENT_VIEW')")
    public ResponseEntity<List<PatientResponse>> searchPatients(@RequestParam String keyword) {
        logger.debug("搜索患者: keyword={}", keyword);
        
        List<PatientResponse> responses = patientManagementService.searchPatients(keyword);
        
        return ResponseEntity.ok(responses);
    }

    /**
     * 获取今日就诊患者
     */
    @GetMapping("/today")
    @PreAuthorize("hasAuthority('PATIENT_VIEW')")
    public ResponseEntity<List<PatientResponse>> getTodayPatients(
            @RequestParam(required = false) LocalDate date) {
        logger.debug("获取今日就诊患者: date={}", date);
        
        List<PatientResponse> responses = patientManagementService.getTodayPatients(date);
        
        return ResponseEntity.ok(responses);
    }

    /**
     * 删除患者
     */
    @DeleteMapping("/{patientId}")
    @PreAuthorize("hasAuthority('PATIENT_DELETE')")
    public ResponseEntity<Void> deletePatient(@PathVariable Long patientId) {
        logger.info("删除患者请求: patientId={}", patientId);
        
        patientManagementService.deletePatient(patientId);
        
        logger.info("患者删除成功: {}", patientId);
        return ResponseEntity.noContent().build();
    }

    /**
     * 设置患者为VIP
     */
    @PutMapping("/{patientId}/vip")
    @PreAuthorize("hasAuthority('PATIENT_UPDATE')")
    public ResponseEntity<Void> setPatientAsVip(@PathVariable Long patientId) {
        logger.info("设置患者为VIP: patientId={}", patientId);
        
        patientManagementService.setPatientAsVip(patientId);
        
        logger.info("患者VIP设置成功: {}", patientId);
        return ResponseEntity.ok().build();
    }

    /**
     * 取消患者VIP状态
     */
    @DeleteMapping("/{patientId}/vip")
    @PreAuthorize("hasAuthority('PATIENT_UPDATE')")
    public ResponseEntity<Void> removePatientVipStatus(@PathVariable Long patientId) {
        logger.info("取消患者VIP状态: patientId={}", patientId);
        
        patientManagementService.removePatientVipStatus(patientId);
        
        logger.info("患者VIP状态取消成功: {}", patientId);
        return ResponseEntity.ok().build();
    }

    /**
     * 更新患者状态
     */
    @PutMapping("/{patientId}/status")
    @PreAuthorize("hasAuthority('PATIENT_UPDATE')")
    public ResponseEntity<Void> updatePatientStatus(
            @PathVariable Long patientId,
            @RequestBody Map<String, String> request) {
        String status = request.get("status");
        logger.info("更新患者状态: patientId={}, status={}", patientId, status);
        
        patientManagementService.updatePatientStatus(patientId, status);
        
        logger.info("患者状态更新成功: patientId={}, status={}", patientId, status);
        return ResponseEntity.ok().build();
    }

    /**
     * 记录患者就诊
     */
    @PostMapping("/{patientId}/visit")
    @PreAuthorize("hasAuthority('PATIENT_UPDATE')")
    public ResponseEntity<Void> recordPatientVisit(@PathVariable Long patientId) {
        logger.info("记录患者就诊: patientId={}", patientId);
        
        patientManagementService.recordPatientVisit(patientId);
        
        logger.info("患者就诊记录成功: {}", patientId);
        return ResponseEntity.ok().build();
    }

    /**
     * 添加患者过敏史
     */
    @PostMapping("/{patientId}/allergies")
    @PreAuthorize("hasAuthority('PATIENT_UPDATE')")
    public ResponseEntity<AllergyHistory> addAllergyHistory(
            @PathVariable Long patientId,
            @Valid @RequestBody AllergyHistory allergyHistory) {
        logger.info("添加患者过敏史: patientId={}, allergen={}", patientId, allergyHistory.getAllergen());
        
        AllergyHistory response = patientManagementService.addAllergyHistory(patientId, allergyHistory);
        
        logger.info("患者过敏史添加成功: patientId={}, allergyHistoryId={}", patientId, response.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * 更新患者过敏史
     */
    @PutMapping("/allergies/{allergyHistoryId}")
    @PreAuthorize("hasAuthority('PATIENT_UPDATE')")
    public ResponseEntity<AllergyHistory> updateAllergyHistory(
            @PathVariable Long allergyHistoryId,
            @Valid @RequestBody AllergyHistory allergyHistory) {
        logger.info("更新患者过敏史: allergyHistoryId={}", allergyHistoryId);
        
        AllergyHistory response = patientManagementService.updateAllergyHistory(allergyHistoryId, allergyHistory);
        
        logger.info("患者过敏史更新成功: {}", allergyHistoryId);
        return ResponseEntity.ok(response);
    }

    /**
     * 删除患者过敏史
     */
    @DeleteMapping("/allergies/{allergyHistoryId}")
    @PreAuthorize("hasAuthority('PATIENT_UPDATE')")
    public ResponseEntity<Void> deleteAllergyHistory(@PathVariable Long allergyHistoryId) {
        logger.info("删除患者过敏史: allergyHistoryId={}", allergyHistoryId);
        
        patientManagementService.deleteAllergyHistory(allergyHistoryId);
        
        logger.info("患者过敏史删除成功: {}", allergyHistoryId);
        return ResponseEntity.noContent().build();
    }

    /**
     * 获取患者过敏史
     */
    @GetMapping("/{patientId}/allergies")
    @PreAuthorize("hasAuthority('PATIENT_VIEW')")
    public ResponseEntity<List<AllergyHistory>> getPatientAllergyHistories(@PathVariable Long patientId) {
        logger.debug("获取患者过敏史: patientId={}", patientId);
        
        List<AllergyHistory> responses = patientManagementService.getPatientAllergyHistories(patientId);
        
        return ResponseEntity.ok(responses);
    }

    /**
     * 添加患者病史
     */
    @PostMapping("/{patientId}/medical-histories")
    @PreAuthorize("hasAuthority('PATIENT_UPDATE')")
    public ResponseEntity<MedicalHistory> addMedicalHistory(
            @PathVariable Long patientId,
            @Valid @RequestBody MedicalHistory medicalHistory) {
        logger.info("添加患者病史: patientId={}, diseaseName={}", patientId, medicalHistory.getDiseaseName());
        
        MedicalHistory response = patientManagementService.addMedicalHistory(patientId, medicalHistory);
        
        logger.info("患者病史添加成功: patientId={}, medicalHistoryId={}", patientId, response.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * 更新患者病史
     */
    @PutMapping("/medical-histories/{medicalHistoryId}")
    @PreAuthorize("hasAuthority('PATIENT_UPDATE')")
    public ResponseEntity<MedicalHistory> updateMedicalHistory(
            @PathVariable Long medicalHistoryId,
            @Valid @RequestBody MedicalHistory medicalHistory) {
        logger.info("更新患者病史: medicalHistoryId={}", medicalHistoryId);
        
        MedicalHistory response = patientManagementService.updateMedicalHistory(medicalHistoryId, medicalHistory);
        
        logger.info("患者病史更新成功: {}", medicalHistoryId);
        return ResponseEntity.ok(response);
    }

    /**
     * 删除患者病史
     */
    @DeleteMapping("/medical-histories/{medicalHistoryId}")
    @PreAuthorize("hasAuthority('PATIENT_UPDATE')")
    public ResponseEntity<Void> deleteMedicalHistory(@PathVariable Long medicalHistoryId) {
        logger.info("删除患者病史: medicalHistoryId={}", medicalHistoryId);
        
        patientManagementService.deleteMedicalHistory(medicalHistoryId);
        
        logger.info("患者病史删除成功: {}", medicalHistoryId);
        return ResponseEntity.noContent().build();
    }

    /**
     * 获取患者病史
     */
    @GetMapping("/{patientId}/medical-histories")
    @PreAuthorize("hasAuthority('PATIENT_VIEW')")
    public ResponseEntity<List<MedicalHistory>> getPatientMedicalHistories(@PathVariable Long patientId) {
        logger.debug("获取患者病史: patientId={}", patientId);
        
        List<MedicalHistory> responses = patientManagementService.getPatientMedicalHistories(patientId);
        
        return ResponseEntity.ok(responses);
    }

    /**
     * 根据病史类型获取患者病史
     */
    @GetMapping("/{patientId}/medical-histories/type/{historyType}")
    @PreAuthorize("hasAuthority('PATIENT_VIEW')")
    public ResponseEntity<List<MedicalHistory>> getPatientMedicalHistoriesByType(
            @PathVariable Long patientId,
            @PathVariable String historyType) {
        logger.debug("根据病史类型获取患者病史: patientId={}, historyType={}", patientId, historyType);
        
        List<MedicalHistory> responses = patientManagementService.getPatientMedicalHistoriesByType(patientId, historyType);
        
        return ResponseEntity.ok(responses);
    }

    /**
     * 获取患者统计信息
     */
    @GetMapping("/statistics")
    @PreAuthorize("hasAuthority('PATIENT_VIEW')")
    public ResponseEntity<Map<String, Object>> getPatientStatistics() {
        logger.debug("获取患者统计信息");
        
        Long totalPatients = patientManagementService.countAllPatients();
        Long normalPatients = patientManagementService.countPatientsByStatus("正常");
        Long vipPatients = patientManagementService.countVipPatients();
        
        Map<String, Object> statistics = Map.of(
                "totalPatients", totalPatients,
                "normalPatients", normalPatients,
                "vipPatients", vipPatients
        );
        
        return ResponseEntity.ok(statistics);
    }
}