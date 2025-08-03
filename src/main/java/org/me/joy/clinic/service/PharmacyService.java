package org.me.joy.clinic.service;

import org.me.joy.clinic.entity.DispenseRecord;
import org.me.joy.clinic.entity.DispenseItem;
import org.me.joy.clinic.entity.Prescription;

import java.util.List;

/**
 * 药房服务接口
 * 提供处方调剂和发药相关功能
 */
public interface PharmacyService {

    /**
     * 开始处方调剂
     * 
     * @param prescriptionId 处方ID
     * @param pharmacistId 调剂药师ID
     * @param pharmacistName 调剂药师姓名
     * @return 调剂记录
     */
    DispenseRecord startDispensing(Long prescriptionId, Long pharmacistId, String pharmacistName);

    /**
     * 调剂单个药品项目
     * 
     * @param request 调剂请求
     * @return 调剂项目
     */
    DispenseItem dispenseMedicineItem(DispenseMedicineRequest request);

    /**
     * 完成处方调剂
     * 
     * @param dispenseRecordId 调剂记录ID
     * @return 调剂记录
     */
    DispenseRecord completeDispensing(Long dispenseRecordId);

    /**
     * 发药
     * 
     * @param dispenseRecordId 调剂记录ID
     * @param dispensingPharmacistId 发药药师ID
     * @param dispensingPharmacistName 发药药师姓名
     * @param deliveryNotes 发药说明
     * @return 调剂记录
     */
    DispenseRecord deliverMedicine(Long dispenseRecordId, Long dispensingPharmacistId, 
                                  String dispensingPharmacistName, String deliveryNotes);

    /**
     * 退回处方
     * 
     * @param dispenseRecordId 调剂记录ID
     * @param reason 退回原因
     * @return 调剂记录
     */
    DispenseRecord returnPrescription(Long dispenseRecordId, String reason);

    /**
     * 取消调剂
     * 
     * @param dispenseRecordId 调剂记录ID
     * @param reason 取消原因
     * @return 调剂记录
     */
    DispenseRecord cancelDispensing(Long dispenseRecordId, String reason);

    /**
     * 替代药品
     * 
     * @param dispenseItemId 调剂项目ID
     * @param newMedicineId 新药品ID
     * @param reason 替代原因
     * @return 调剂项目
     */
    DispenseItem substituteMedicine(Long dispenseItemId, Long newMedicineId, String reason);

    /**
     * 复核调剂
     * 
     * @param dispenseRecordId 调剂记录ID
     * @param reviewPharmacistId 复核药师ID
     * @param reviewPharmacistName 复核药师姓名
     * @param comments 复核意见
     * @return 调剂记录
     */
    DispenseRecord reviewDispensing(Long dispenseRecordId, Long reviewPharmacistId, 
                                   String reviewPharmacistName, String comments);

    /**
     * 获取待调剂的处方列表
     * 
     * @return 待调剂处方列表
     */
    List<Prescription> getPendingPrescriptions();

    /**
     * 获取调剂中的记录列表
     * 
     * @return 调剂中记录列表
     */
    List<DispenseRecord> getInProgressDispenses();

    /**
     * 获取待发药的记录列表
     * 
     * @return 待发药记录列表
     */
    List<DispenseRecord> getReadyForDelivery();

    /**
     * 获取需要复核的记录列表
     * 
     * @return 需要复核记录列表
     */
    List<DispenseRecord> getNeedingReview();

    /**
     * 根据处方ID获取调剂记录
     * 
     * @param prescriptionId 处方ID
     * @return 调剂记录
     */
    DispenseRecord getDispenseRecordByPrescriptionId(Long prescriptionId);

    /**
     * 根据ID获取调剂记录详情
     * 
     * @param dispenseRecordId 调剂记录ID
     * @return 调剂记录详情
     */
    DispenseRecord getDispenseRecordWithItems(Long dispenseRecordId);

    /**
     * 获取药师的调剂记录列表
     * 
     * @param pharmacistId 药师ID
     * @return 调剂记录列表
     */
    List<DispenseRecord> getDispenseRecordsByPharmacist(Long pharmacistId);

    /**
     * 获取患者的调剂记录列表
     * 
     * @param patientId 患者ID
     * @return 调剂记录列表
     */
    List<DispenseRecord> getDispenseRecordsByPatient(Long patientId);

    /**
     * 检查处方是否可以调剂
     * 
     * @param prescriptionId 处方ID
     * @return 检查结果
     */
    DispenseEligibilityResult checkDispenseEligibility(Long prescriptionId);

    /**
     * 调剂药品请求
     */
    class DispenseMedicineRequest {
        private Long dispenseItemId;
        private Long medicineId;
        private Integer dispensedQuantity;
        private String batchNumber;
        private java.time.LocalDate expiryDate;
        private String dispenseNotes;
        private Long dispensedBy;
        private String dispensedByName;

        // 构造函数
        public DispenseMedicineRequest() {}

        public DispenseMedicineRequest(Long dispenseItemId, Long medicineId, Integer dispensedQuantity,
                                     String batchNumber, java.time.LocalDate expiryDate, 
                                     Long dispensedBy, String dispensedByName) {
            this.dispenseItemId = dispenseItemId;
            this.medicineId = medicineId;
            this.dispensedQuantity = dispensedQuantity;
            this.batchNumber = batchNumber;
            this.expiryDate = expiryDate;
            this.dispensedBy = dispensedBy;
            this.dispensedByName = dispensedByName;
        }

        // Getters and Setters
        public Long getDispenseItemId() {
            return dispenseItemId;
        }

        public void setDispenseItemId(Long dispenseItemId) {
            this.dispenseItemId = dispenseItemId;
        }

        public Long getMedicineId() {
            return medicineId;
        }

        public void setMedicineId(Long medicineId) {
            this.medicineId = medicineId;
        }

        public Integer getDispensedQuantity() {
            return dispensedQuantity;
        }

        public void setDispensedQuantity(Integer dispensedQuantity) {
            this.dispensedQuantity = dispensedQuantity;
        }

        public String getBatchNumber() {
            return batchNumber;
        }

        public void setBatchNumber(String batchNumber) {
            this.batchNumber = batchNumber;
        }

        public java.time.LocalDate getExpiryDate() {
            return expiryDate;
        }

        public void setExpiryDate(java.time.LocalDate expiryDate) {
            this.expiryDate = expiryDate;
        }

        public String getDispenseNotes() {
            return dispenseNotes;
        }

        public void setDispenseNotes(String dispenseNotes) {
            this.dispenseNotes = dispenseNotes;
        }

        public Long getDispensedBy() {
            return dispensedBy;
        }

        public void setDispensedBy(Long dispensedBy) {
            this.dispensedBy = dispensedBy;
        }

        public String getDispensedByName() {
            return dispensedByName;
        }

        public void setDispensedByName(String dispensedByName) {
            this.dispensedByName = dispensedByName;
        }
    }

    /**
     * 调剂资格检查结果
     */
    class DispenseEligibilityResult {
        private boolean eligible;
        private String reason;
        private List<String> issues;
        private List<String> warnings;

        public DispenseEligibilityResult() {}

        public DispenseEligibilityResult(boolean eligible, String reason) {
            this.eligible = eligible;
            this.reason = reason;
        }

        // Getters and Setters
        public boolean isEligible() {
            return eligible;
        }

        public void setEligible(boolean eligible) {
            this.eligible = eligible;
        }

        public String getReason() {
            return reason;
        }

        public void setReason(String reason) {
            this.reason = reason;
        }

        public List<String> getIssues() {
            return issues;
        }

        public void setIssues(List<String> issues) {
            this.issues = issues;
        }

        public List<String> getWarnings() {
            return warnings;
        }

        public void setWarnings(List<String> warnings) {
            this.warnings = warnings;
        }
    }
}