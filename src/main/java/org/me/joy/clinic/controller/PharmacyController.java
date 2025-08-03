package org.me.joy.clinic.controller;

import org.me.joy.clinic.entity.DispenseRecord;
import org.me.joy.clinic.entity.DispenseItem;
import org.me.joy.clinic.entity.Prescription;
import org.me.joy.clinic.security.RequiresPermission;
import org.me.joy.clinic.service.PharmacyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * 药房管理控制器
 * 提供处方调剂和发药相关的REST API
 */
@RestController
@RequestMapping("/api/pharmacy")
public class PharmacyController {

    @Autowired
    private PharmacyService pharmacyService;

    /**
     * 获取待调剂的处方列表
     */
    @GetMapping("/prescriptions/pending")
    @RequiresPermission("PHARMACY_MANAGEMENT")
    public ResponseEntity<List<Prescription>> getPendingPrescriptions() {
        List<Prescription> prescriptions = pharmacyService.getPendingPrescriptions();
        return ResponseEntity.ok(prescriptions);
    }

    /**
     * 检查处方调剂资格
     */
    @GetMapping("/prescriptions/{prescriptionId}/eligibility")
    @RequiresPermission("PHARMACY_MANAGEMENT")
    public ResponseEntity<PharmacyService.DispenseEligibilityResult> checkDispenseEligibility(
            @PathVariable Long prescriptionId) {
        PharmacyService.DispenseEligibilityResult result = 
            pharmacyService.checkDispenseEligibility(prescriptionId);
        return ResponseEntity.ok(result);
    }

    /**
     * 开始处方调剂
     */
    @PostMapping("/dispense/start")
    @RequiresPermission("PRESCRIPTION_DISPENSE")
    public ResponseEntity<DispenseRecord> startDispensing(@RequestBody @Valid StartDispensingRequest request) {
        DispenseRecord dispenseRecord = pharmacyService.startDispensing(
            request.getPrescriptionId(),
            request.getPharmacistId(),
            request.getPharmacistName()
        );
        return ResponseEntity.ok(dispenseRecord);
    }

    /**
     * 调剂单个药品项目
     */
    @PostMapping("/dispense/medicine")
    @RequiresPermission("PRESCRIPTION_DISPENSE")
    public ResponseEntity<DispenseItem> dispenseMedicineItem(
            @RequestBody @Valid PharmacyService.DispenseMedicineRequest request) {
        DispenseItem dispenseItem = pharmacyService.dispenseMedicineItem(request);
        return ResponseEntity.ok(dispenseItem);
    }

    /**
     * 完成处方调剂
     */
    @PostMapping("/dispense/{dispenseRecordId}/complete")
    @RequiresPermission("PRESCRIPTION_DISPENSE")
    public ResponseEntity<DispenseRecord> completeDispensing(@PathVariable Long dispenseRecordId) {
        DispenseRecord dispenseRecord = pharmacyService.completeDispensing(dispenseRecordId);
        return ResponseEntity.ok(dispenseRecord);
    }

    /**
     * 发药
     */
    @PostMapping("/dispense/{dispenseRecordId}/deliver")
    @RequiresPermission("PRESCRIPTION_DISPENSE")
    public ResponseEntity<DispenseRecord> deliverMedicine(
            @PathVariable Long dispenseRecordId,
            @RequestBody @Valid DeliverMedicineRequest request) {
        DispenseRecord dispenseRecord = pharmacyService.deliverMedicine(
            dispenseRecordId,
            request.getDispensingPharmacistId(),
            request.getDispensingPharmacistName(),
            request.getDeliveryNotes()
        );
        return ResponseEntity.ok(dispenseRecord);
    }

    /**
     * 替代药品
     */
    @PostMapping("/dispense/items/{dispenseItemId}/substitute")
    @RequiresPermission("PRESCRIPTION_DISPENSE")
    public ResponseEntity<DispenseItem> substituteMedicine(
            @PathVariable Long dispenseItemId,
            @RequestBody @Valid SubstituteMedicineRequest request) {
        DispenseItem dispenseItem = pharmacyService.substituteMedicine(
            dispenseItemId,
            request.getNewMedicineId(),
            request.getReason()
        );
        return ResponseEntity.ok(dispenseItem);
    }

    /**
     * 退回处方
     */
    @PostMapping("/dispense/{dispenseRecordId}/return")
    @RequiresPermission("PRESCRIPTION_DISPENSE")
    public ResponseEntity<DispenseRecord> returnPrescription(
            @PathVariable Long dispenseRecordId,
            @RequestBody @Valid ReturnPrescriptionRequest request) {
        DispenseRecord dispenseRecord = pharmacyService.returnPrescription(
            dispenseRecordId,
            request.getReason()
        );
        return ResponseEntity.ok(dispenseRecord);
    }

    /**
     * 取消调剂
     */
    @PostMapping("/dispense/{dispenseRecordId}/cancel")
    @RequiresPermission("PRESCRIPTION_DISPENSE")
    public ResponseEntity<DispenseRecord> cancelDispensing(
            @PathVariable Long dispenseRecordId,
            @RequestBody @Valid CancelDispensingRequest request) {
        DispenseRecord dispenseRecord = pharmacyService.cancelDispensing(
            dispenseRecordId,
            request.getReason()
        );
        return ResponseEntity.ok(dispenseRecord);
    }

    /**
     * 复核调剂
     */
    @PostMapping("/dispense/{dispenseRecordId}/review")
    @RequiresPermission("PRESCRIPTION_DISPENSE")
    public ResponseEntity<DispenseRecord> reviewDispensing(
            @PathVariable Long dispenseRecordId,
            @RequestBody @Valid ReviewDispensingRequest request) {
        DispenseRecord dispenseRecord = pharmacyService.reviewDispensing(
            dispenseRecordId,
            request.getReviewPharmacistId(),
            request.getReviewPharmacistName(),
            request.getComments()
        );
        return ResponseEntity.ok(dispenseRecord);
    }

    /**
     * 获取调剂中的记录列表
     */
    @GetMapping("/dispense/in-progress")
    @RequiresPermission("PHARMACY_MANAGEMENT")
    public ResponseEntity<List<DispenseRecord>> getInProgressDispenses() {
        List<DispenseRecord> records = pharmacyService.getInProgressDispenses();
        return ResponseEntity.ok(records);
    }

    /**
     * 获取待发药的记录列表
     */
    @GetMapping("/dispense/ready-for-delivery")
    @RequiresPermission("PHARMACY_MANAGEMENT")
    public ResponseEntity<List<DispenseRecord>> getReadyForDelivery() {
        List<DispenseRecord> records = pharmacyService.getReadyForDelivery();
        return ResponseEntity.ok(records);
    }

    /**
     * 获取需要复核的记录列表
     */
    @GetMapping("/dispense/needing-review")
    @RequiresPermission("PHARMACY_MANAGEMENT")
    public ResponseEntity<List<DispenseRecord>> getNeedingReview() {
        List<DispenseRecord> records = pharmacyService.getNeedingReview();
        return ResponseEntity.ok(records);
    }

    /**
     * 根据处方ID获取调剂记录
     */
    @GetMapping("/dispense/prescription/{prescriptionId}")
    @RequiresPermission("PHARMACY_MANAGEMENT")
    public ResponseEntity<DispenseRecord> getDispenseRecordByPrescriptionId(@PathVariable Long prescriptionId) {
        DispenseRecord dispenseRecord = pharmacyService.getDispenseRecordByPrescriptionId(prescriptionId);
        if (dispenseRecord == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(dispenseRecord);
    }

    /**
     * 获取调剂记录详情（包含调剂项目）
     */
    @GetMapping("/dispense/{dispenseRecordId}")
    @RequiresPermission("PHARMACY_MANAGEMENT")
    public ResponseEntity<DispenseRecord> getDispenseRecordWithItems(@PathVariable Long dispenseRecordId) {
        DispenseRecord dispenseRecord = pharmacyService.getDispenseRecordWithItems(dispenseRecordId);
        if (dispenseRecord == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(dispenseRecord);
    }

    /**
     * 获取药师的调剂记录列表
     */
    @GetMapping("/dispense/pharmacist/{pharmacistId}")
    @RequiresPermission("PHARMACY_MANAGEMENT")
    public ResponseEntity<List<DispenseRecord>> getDispenseRecordsByPharmacist(@PathVariable Long pharmacistId) {
        List<DispenseRecord> records = pharmacyService.getDispenseRecordsByPharmacist(pharmacistId);
        return ResponseEntity.ok(records);
    }

    /**
     * 获取患者的调剂记录列表
     */
    @GetMapping("/dispense/patient/{patientId}")
    @RequiresPermission("PHARMACY_MANAGEMENT")
    public ResponseEntity<List<DispenseRecord>> getDispenseRecordsByPatient(@PathVariable Long patientId) {
        List<DispenseRecord> records = pharmacyService.getDispenseRecordsByPatient(patientId);
        return ResponseEntity.ok(records);
    }

    // DTO类定义

    /**
     * 开始调剂请求
     */
    public static class StartDispensingRequest {
        private Long prescriptionId;
        private Long pharmacistId;
        private String pharmacistName;

        // Getters and Setters
        public Long getPrescriptionId() {
            return prescriptionId;
        }

        public void setPrescriptionId(Long prescriptionId) {
            this.prescriptionId = prescriptionId;
        }

        public Long getPharmacistId() {
            return pharmacistId;
        }

        public void setPharmacistId(Long pharmacistId) {
            this.pharmacistId = pharmacistId;
        }

        public String getPharmacistName() {
            return pharmacistName;
        }

        public void setPharmacistName(String pharmacistName) {
            this.pharmacistName = pharmacistName;
        }
    }

    /**
     * 发药请求
     */
    public static class DeliverMedicineRequest {
        private Long dispensingPharmacistId;
        private String dispensingPharmacistName;
        private String deliveryNotes;

        // Getters and Setters
        public Long getDispensingPharmacistId() {
            return dispensingPharmacistId;
        }

        public void setDispensingPharmacistId(Long dispensingPharmacistId) {
            this.dispensingPharmacistId = dispensingPharmacistId;
        }

        public String getDispensingPharmacistName() {
            return dispensingPharmacistName;
        }

        public void setDispensingPharmacistName(String dispensingPharmacistName) {
            this.dispensingPharmacistName = dispensingPharmacistName;
        }

        public String getDeliveryNotes() {
            return deliveryNotes;
        }

        public void setDeliveryNotes(String deliveryNotes) {
            this.deliveryNotes = deliveryNotes;
        }
    }

    /**
     * 替代药品请求
     */
    public static class SubstituteMedicineRequest {
        private Long newMedicineId;
        private String reason;

        // Getters and Setters
        public Long getNewMedicineId() {
            return newMedicineId;
        }

        public void setNewMedicineId(Long newMedicineId) {
            this.newMedicineId = newMedicineId;
        }

        public String getReason() {
            return reason;
        }

        public void setReason(String reason) {
            this.reason = reason;
        }
    }

    /**
     * 退回处方请求
     */
    public static class ReturnPrescriptionRequest {
        private String reason;

        // Getters and Setters
        public String getReason() {
            return reason;
        }

        public void setReason(String reason) {
            this.reason = reason;
        }
    }

    /**
     * 取消调剂请求
     */
    public static class CancelDispensingRequest {
        private String reason;

        // Getters and Setters
        public String getReason() {
            return reason;
        }

        public void setReason(String reason) {
            this.reason = reason;
        }
    }

    /**
     * 复核调剂请求
     */
    public static class ReviewDispensingRequest {
        private Long reviewPharmacistId;
        private String reviewPharmacistName;
        private String comments;

        // Getters and Setters
        public Long getReviewPharmacistId() {
            return reviewPharmacistId;
        }

        public void setReviewPharmacistId(Long reviewPharmacistId) {
            this.reviewPharmacistId = reviewPharmacistId;
        }

        public String getReviewPharmacistName() {
            return reviewPharmacistName;
        }

        public void setReviewPharmacistName(String reviewPharmacistName) {
            this.reviewPharmacistName = reviewPharmacistName;
        }

        public String getComments() {
            return comments;
        }

        public void setComments(String comments) {
            this.comments = comments;
        }
    }
}