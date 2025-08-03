package org.me.joy.clinic.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 处方调剂记录实体类
 * 存储处方调剂和发药的详细记录
 */
@TableName("dispense_records")
public class DispenseRecord extends BaseEntity {

    /**
     * 处方ID
     */
    @NotNull(message = "处方ID不能为空")
    private Long prescriptionId;

    /**
     * 处方编号
     */
    @NotBlank(message = "处方编号不能为空")
    @Size(max = 50, message = "处方编号长度不能超过50个字符")
    private String prescriptionNumber;

    /**
     * 患者ID
     */
    @NotNull(message = "患者ID不能为空")
    private Long patientId;

    /**
     * 患者姓名
     */
    @NotBlank(message = "患者姓名不能为空")
    @Size(max = 100, message = "患者姓名长度不能超过100个字符")
    private String patientName;

    /**
     * 调剂药师ID
     */
    @NotNull(message = "调剂药师ID不能为空")
    private Long pharmacistId;

    /**
     * 调剂药师姓名
     */
    @NotBlank(message = "调剂药师姓名不能为空")
    @Size(max = 100, message = "调剂药师姓名长度不能超过100个字符")
    private String pharmacistName;

    /**
     * 发药药师ID
     */
    private Long dispensingPharmacistId;

    /**
     * 发药药师姓名
     */
    @Size(max = 100, message = "发药药师姓名长度不能超过100个字符")
    private String dispensingPharmacistName;

    /**
     * 调剂状态
     */
    @Pattern(regexp = "^(待调剂|调剂中|已调剂|已发药|部分发药|已退回|已取消)$", 
             message = "调剂状态只能是待调剂、调剂中、已调剂、已发药、部分发药、已退回或已取消")
    private String status = "待调剂";

    /**
     * 调剂开始时间
     */
    private LocalDateTime dispenseStartTime;

    /**
     * 调剂完成时间
     */
    private LocalDateTime dispenseCompletedTime;

    /**
     * 发药时间
     */
    private LocalDateTime deliveredTime;

    /**
     * 处方总金额
     */
    @DecimalMin(value = "0.0", message = "处方总金额不能为负数")
    @Digits(integer = 10, fraction = 2, message = "处方总金额格式不正确")
    private BigDecimal totalAmount;

    /**
     * 实际调剂金额
     */
    @DecimalMin(value = "0.0", message = "实际调剂金额不能为负数")
    @Digits(integer = 10, fraction = 2, message = "实际调剂金额格式不正确")
    private BigDecimal actualAmount;

    /**
     * 调剂验证结果
     */
    @Pattern(regexp = "^(通过|不通过|需要复核)$", 
             message = "调剂验证结果只能是通过、不通过或需要复核")
    private String validationResult;

    /**
     * 库存检查结果
     */
    @Pattern(regexp = "^(充足|不足|部分不足)$", 
             message = "库存检查结果只能是充足、不足或部分不足")
    private String stockCheckResult;

    /**
     * 药品相互作用检查结果
     */
    @Size(max = 1000, message = "药品相互作用检查结果长度不能超过1000个字符")
    private String drugInteractionCheck;

    /**
     * 过敏史检查结果
     */
    @Size(max = 500, message = "过敏史检查结果长度不能超过500个字符")
    private String allergyCheck;

    /**
     * 调剂说明
     */
    @Size(max = 1000, message = "调剂说明长度不能超过1000个字符")
    private String dispenseNotes;

    /**
     * 发药说明
     */
    @Size(max = 1000, message = "发药说明长度不能超过1000个字符")
    private String deliveryNotes;

    /**
     * 患者用药指导
     */
    @Size(max = 2000, message = "患者用药指导长度不能超过2000个字符")
    private String medicationGuidance;

    /**
     * 特殊注意事项
     */
    @Size(max = 1000, message = "特殊注意事项长度不能超过1000个字符")
    private String specialPrecautions;

    /**
     * 是否需要冷藏
     */
    private Boolean requiresRefrigeration = false;

    /**
     * 是否需要特殊包装
     */
    private Boolean requiresSpecialPackaging = false;

    /**
     * 包装规格说明
     */
    @Size(max = 200, message = "包装规格说明长度不能超过200个字符")
    private String packagingSpecification;

    /**
     * 退回原因
     */
    @Size(max = 500, message = "退回原因长度不能超过500个字符")
    private String returnReason;

    /**
     * 退回时间
     */
    private LocalDateTime returnedTime;

    /**
     * 取消原因
     */
    @Size(max = 500, message = "取消原因长度不能超过500个字符")
    private String cancelReason;

    /**
     * 取消时间
     */
    private LocalDateTime cancelledTime;

    /**
     * 质量检查结果
     */
    @Pattern(regexp = "^(合格|不合格|需要复检)$", 
             message = "质量检查结果只能是合格、不合格或需要复检")
    private String qualityCheckResult;

    /**
     * 质量检查说明
     */
    @Size(max = 500, message = "质量检查说明长度不能超过500个字符")
    private String qualityCheckNotes;

    /**
     * 复核药师ID
     */
    private Long reviewPharmacistId;

    /**
     * 复核药师姓名
     */
    @Size(max = 100, message = "复核药师姓名长度不能超过100个字符")
    private String reviewPharmacistName;

    /**
     * 复核时间
     */
    private LocalDateTime reviewedTime;

    /**
     * 复核意见
     */
    @Size(max = 500, message = "复核意见长度不能超过500个字符")
    private String reviewComments;

    /**
     * 备注信息
     */
    @Size(max = 1000, message = "备注信息长度不能超过1000个字符")
    private String remarks;

    /**
     * 调剂项目明细列表（不存储在数据库中，通过关联查询获取）
     */
    @TableField(exist = false)
    private List<DispenseItem> dispenseItems;

    // 构造函数
    public DispenseRecord() {}

    public DispenseRecord(Long prescriptionId, String prescriptionNumber, Long patientId, 
                         String patientName, Long pharmacistId, String pharmacistName) {
        this.prescriptionId = prescriptionId;
        this.prescriptionNumber = prescriptionNumber;
        this.patientId = patientId;
        this.patientName = patientName;
        this.pharmacistId = pharmacistId;
        this.pharmacistName = pharmacistName;
        this.dispenseStartTime = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getPrescriptionId() {
        return prescriptionId;
    }

    public void setPrescriptionId(Long prescriptionId) {
        this.prescriptionId = prescriptionId;
    }

    public String getPrescriptionNumber() {
        return prescriptionNumber;
    }

    public void setPrescriptionNumber(String prescriptionNumber) {
        this.prescriptionNumber = prescriptionNumber;
    }

    public Long getPatientId() {
        return patientId;
    }

    public void setPatientId(Long patientId) {
        this.patientId = patientId;
    }

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getDispenseStartTime() {
        return dispenseStartTime;
    }

    public void setDispenseStartTime(LocalDateTime dispenseStartTime) {
        this.dispenseStartTime = dispenseStartTime;
    }

    public LocalDateTime getDispenseCompletedTime() {
        return dispenseCompletedTime;
    }

    public void setDispenseCompletedTime(LocalDateTime dispenseCompletedTime) {
        this.dispenseCompletedTime = dispenseCompletedTime;
    }

    public LocalDateTime getDeliveredTime() {
        return deliveredTime;
    }

    public void setDeliveredTime(LocalDateTime deliveredTime) {
        this.deliveredTime = deliveredTime;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public BigDecimal getActualAmount() {
        return actualAmount;
    }

    public void setActualAmount(BigDecimal actualAmount) {
        this.actualAmount = actualAmount;
    }

    public String getValidationResult() {
        return validationResult;
    }

    public void setValidationResult(String validationResult) {
        this.validationResult = validationResult;
    }

    public String getStockCheckResult() {
        return stockCheckResult;
    }

    public void setStockCheckResult(String stockCheckResult) {
        this.stockCheckResult = stockCheckResult;
    }

    public String getDrugInteractionCheck() {
        return drugInteractionCheck;
    }

    public void setDrugInteractionCheck(String drugInteractionCheck) {
        this.drugInteractionCheck = drugInteractionCheck;
    }

    public String getAllergyCheck() {
        return allergyCheck;
    }

    public void setAllergyCheck(String allergyCheck) {
        this.allergyCheck = allergyCheck;
    }

    public String getDispenseNotes() {
        return dispenseNotes;
    }

    public void setDispenseNotes(String dispenseNotes) {
        this.dispenseNotes = dispenseNotes;
    }

    public String getDeliveryNotes() {
        return deliveryNotes;
    }

    public void setDeliveryNotes(String deliveryNotes) {
        this.deliveryNotes = deliveryNotes;
    }

    public String getMedicationGuidance() {
        return medicationGuidance;
    }

    public void setMedicationGuidance(String medicationGuidance) {
        this.medicationGuidance = medicationGuidance;
    }

    public String getSpecialPrecautions() {
        return specialPrecautions;
    }

    public void setSpecialPrecautions(String specialPrecautions) {
        this.specialPrecautions = specialPrecautions;
    }

    public Boolean getRequiresRefrigeration() {
        return requiresRefrigeration;
    }

    public void setRequiresRefrigeration(Boolean requiresRefrigeration) {
        this.requiresRefrigeration = requiresRefrigeration;
    }

    public Boolean getRequiresSpecialPackaging() {
        return requiresSpecialPackaging;
    }

    public void setRequiresSpecialPackaging(Boolean requiresSpecialPackaging) {
        this.requiresSpecialPackaging = requiresSpecialPackaging;
    }

    public String getPackagingSpecification() {
        return packagingSpecification;
    }

    public void setPackagingSpecification(String packagingSpecification) {
        this.packagingSpecification = packagingSpecification;
    }

    public String getReturnReason() {
        return returnReason;
    }

    public void setReturnReason(String returnReason) {
        this.returnReason = returnReason;
    }

    public LocalDateTime getReturnedTime() {
        return returnedTime;
    }

    public void setReturnedTime(LocalDateTime returnedTime) {
        this.returnedTime = returnedTime;
    }

    public String getCancelReason() {
        return cancelReason;
    }

    public void setCancelReason(String cancelReason) {
        this.cancelReason = cancelReason;
    }

    public LocalDateTime getCancelledTime() {
        return cancelledTime;
    }

    public void setCancelledTime(LocalDateTime cancelledTime) {
        this.cancelledTime = cancelledTime;
    }

    public String getQualityCheckResult() {
        return qualityCheckResult;
    }

    public void setQualityCheckResult(String qualityCheckResult) {
        this.qualityCheckResult = qualityCheckResult;
    }

    public String getQualityCheckNotes() {
        return qualityCheckNotes;
    }

    public void setQualityCheckNotes(String qualityCheckNotes) {
        this.qualityCheckNotes = qualityCheckNotes;
    }

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

    public LocalDateTime getReviewedTime() {
        return reviewedTime;
    }

    public void setReviewedTime(LocalDateTime reviewedTime) {
        this.reviewedTime = reviewedTime;
    }

    public String getReviewComments() {
        return reviewComments;
    }

    public void setReviewComments(String reviewComments) {
        this.reviewComments = reviewComments;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public List<DispenseItem> getDispenseItems() {
        return dispenseItems;
    }

    public void setDispenseItems(List<DispenseItem> dispenseItems) {
        this.dispenseItems = dispenseItems;
    }

    /**
     * 开始调剂
     */
    public void startDispensing() {
        if ("待调剂".equals(status)) {
            this.status = "调剂中";
            this.dispenseStartTime = LocalDateTime.now();
        }
    }

    /**
     * 完成调剂
     */
    public void completeDispensing() {
        if ("调剂中".equals(status)) {
            this.status = "已调剂";
            this.dispenseCompletedTime = LocalDateTime.now();
        }
    }

    /**
     * 发药
     */
    public void deliver(Long dispensingPharmacistId, String dispensingPharmacistName) {
        if ("已调剂".equals(status)) {
            this.status = "已发药";
            this.dispensingPharmacistId = dispensingPharmacistId;
            this.dispensingPharmacistName = dispensingPharmacistName;
            this.deliveredTime = LocalDateTime.now();
        }
    }

    /**
     * 退回处方
     */
    public void returnPrescription(String reason) {
        if (!"已发药".equals(status)) {
            this.status = "已退回";
            this.returnReason = reason;
            this.returnedTime = LocalDateTime.now();
        }
    }

    /**
     * 取消调剂
     */
    public void cancel(String reason) {
        if (!"已发药".equals(status)) {
            this.status = "已取消";
            this.cancelReason = reason;
            this.cancelledTime = LocalDateTime.now();
        }
    }

    /**
     * 复核调剂
     */
    public void review(Long reviewPharmacistId, String reviewPharmacistName, String comments) {
        this.reviewPharmacistId = reviewPharmacistId;
        this.reviewPharmacistName = reviewPharmacistName;
        this.reviewedTime = LocalDateTime.now();
        this.reviewComments = comments;
    }

    /**
     * 计算调剂耗时（分钟）
     */
    public Long getDispenseTimeInMinutes() {
        if (dispenseStartTime != null && dispenseCompletedTime != null) {
            return java.time.Duration.between(dispenseStartTime, dispenseCompletedTime).toMinutes();
        }
        return null;
    }

    /**
     * 检查是否需要复核
     */
    public boolean needsReview() {
        return "需要复核".equals(validationResult) || 
               (drugInteractionCheck != null && drugInteractionCheck.contains("警告")) ||
               (allergyCheck != null && allergyCheck.contains("过敏") && !allergyCheck.contains("无过敏"));
    }

    /**
     * 检查是否可以发药
     */
    public boolean canBeDelivered() {
        return "已调剂".equals(status) && 
               "通过".equals(validationResult) && 
               "合格".equals(qualityCheckResult);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DispenseRecord)) return false;
        DispenseRecord that = (DispenseRecord) o;
        return getId() != null && getId().equals(that.getId());
    }

    @Override
    public int hashCode() {
        return getId() != null ? getId().hashCode() : 0;
    }

    @Override
    public String toString() {
        return "DispenseRecord{" +
                "id=" + getId() +
                ", prescriptionId=" + prescriptionId +
                ", prescriptionNumber='" + prescriptionNumber + '\'' +
                ", patientName='" + patientName + '\'' +
                ", pharmacistName='" + pharmacistName + '\'' +
                ", status='" + status + '\'' +
                ", totalAmount=" + totalAmount +
                '}';
    }
}