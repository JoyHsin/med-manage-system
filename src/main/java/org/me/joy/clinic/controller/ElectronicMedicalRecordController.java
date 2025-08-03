package org.me.joy.clinic.controller;

import jakarta.validation.Valid;
import org.me.joy.clinic.dto.CreateMedicalRecordRequest;
import org.me.joy.clinic.dto.UpdateMedicalRecordRequest;
import org.me.joy.clinic.entity.Diagnosis;
import org.me.joy.clinic.entity.MedicalRecord;
import org.me.joy.clinic.entity.Prescription;
import org.me.joy.clinic.security.RequiresPermission;
import org.me.joy.clinic.service.ElectronicMedicalRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 电子病历管理控制器
 */
@RestController
@RequestMapping("/medical-records")
public class ElectronicMedicalRecordController {

    @Autowired
    private ElectronicMedicalRecordService medicalRecordService;

    @PostMapping
    @RequiresPermission("MEDICAL_RECORD_WRITE")
    public ResponseEntity<MedicalRecord> createMedicalRecord(
            @Valid @RequestBody CreateMedicalRecordRequest request) {
        MedicalRecord medicalRecord = medicalRecordService.createMedicalRecord(request);
        return ResponseEntity.ok(medicalRecord);
    }

    @PutMapping("/{recordId}")
    @RequiresPermission("MEDICAL_RECORD_WRITE")
    public ResponseEntity<MedicalRecord> updateMedicalRecord(
            @PathVariable Long recordId,
            @Valid @RequestBody UpdateMedicalRecordRequest request) {
        MedicalRecord medicalRecord = medicalRecordService.updateMedicalRecord(recordId, request);
        return ResponseEntity.ok(medicalRecord);
    }

    @GetMapping("/{recordId}")
    @RequiresPermission("MEDICAL_RECORD_READ")
    public ResponseEntity<MedicalRecord> getMedicalRecord(
            @PathVariable Long recordId) {
        MedicalRecord medicalRecord = medicalRecordService.getMedicalRecordById(recordId);
        return ResponseEntity.ok(medicalRecord);
    }

    @GetMapping("/patient/{patientId}")
    @RequiresPermission("MEDICAL_RECORD_READ")
    public ResponseEntity<List<MedicalRecord>> getPatientMedicalRecords(
            @PathVariable Long patientId) {
        List<MedicalRecord> records = medicalRecordService.getPatientMedicalRecords(patientId);
        return ResponseEntity.ok(records);
    }

    @PostMapping("/{recordId}/diagnoses")
    @RequiresPermission("MEDICAL_RECORD_WRITE")
    public ResponseEntity<Diagnosis> addDiagnosis(
            @PathVariable Long recordId,
            @Valid @RequestBody Diagnosis diagnosis) {
        Diagnosis addedDiagnosis = medicalRecordService.addDiagnosis(recordId, diagnosis);
        return ResponseEntity.ok(addedDiagnosis);
    }

    @PostMapping("/{recordId}/prescriptions")
    @RequiresPermission("PRESCRIPTION_CREATE")
    public ResponseEntity<Prescription> addPrescription(
            @PathVariable Long recordId,
            @Valid @RequestBody Prescription prescription) {
        Prescription addedPrescription = medicalRecordService.addPrescription(recordId, prescription);
        return ResponseEntity.ok(addedPrescription);
    }

    @PostMapping("/{recordId}/submit-review")
    @RequiresPermission("MEDICAL_RECORD_WRITE")
    public ResponseEntity<Void> submitForReview(
            @PathVariable Long recordId) {
        medicalRecordService.submitForReview(recordId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{recordId}/review")
    @RequiresPermission("MEDICAL_RECORD_WRITE")
    public ResponseEntity<Void> reviewMedicalRecord(
            @PathVariable Long recordId,
            @RequestBody Map<String, Object> reviewRequest) {
        Long reviewDoctorId = Long.valueOf(reviewRequest.get("reviewDoctorId").toString());
        Boolean approved = (Boolean) reviewRequest.get("approved");
        String comments = (String) reviewRequest.get("comments");
        
        medicalRecordService.reviewMedicalRecord(recordId, reviewDoctorId, approved, comments);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{recordId}")
    @RequiresPermission("MEDICAL_RECORD_WRITE")
    public ResponseEntity<Void> deleteMedicalRecord(
            @PathVariable Long recordId) {
        medicalRecordService.deleteMedicalRecord(recordId);
        return ResponseEntity.ok().build();
    }
}