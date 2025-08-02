package org.me.joy.clinic.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 处方实体类
 * 存储医生开具的处方信息
 */
@TableName("prescriptions")
public class Prescription extends BaseEntity {

    /**
     * 病历ID
     */
    @NotNull(message = "病历ID不能为空")
    private Long medicalRecordId;

    /**
     * 医生ID
     */
    @NotNull(message = "医生ID不能为空")
    private Long doctorId;

    /**
     * 处方编号
     */
    @NotBlank(message = "处方编号不能为空")
    @Size(max = 50, message = "处方编号长度不能超过50个字符")
    private String prescriptionNumber;

    /**
     * 处方类型
     */
    @Pattern(regexp = "^(普通处方|急诊处方|儿科处方|麻醉处方|精神药品处方|毒性药品处方)$", 
             message = "处方类型只能是普通处方、急诊处方、儿科处方、麻醉处方、精神药品处方或毒性药品处方")
    private String prescriptionType = "普通处方";

    /**
     * 处方状态
     */
    @Pattern(regexp = "^(草稿|已开具|已审核|已调配|已发药|已取消)$", 
             message = "处方状态只能是草稿、已开具、已审核、已调配、已发药或已取消")
    private String status = "草稿";

    /**
     * 开具时间
     */
    @NotNull(message = "开具时间不能为空")
    private LocalDateTime prescribedAt;

    /**
     * 审核医生ID
     */
    private Long reviewDoctorId;

    /**
     * 审核时间
     */
    private LocalDateTime reviewedAt;

    /**
     * 调配药师ID
     */
    private Long pharmacistId;

    /**
     * 调配时间
     */
    private LocalDateTime dispensedAt;

    /**
     * 发药时间
     */
    private LocalDateTime deliveredAt;

    /**
     * 处方总金额
     */
    @DecimalMin(value = "0.0", message = "处方总金额不能为负数")
    @Digits(integer = 10, fraction = 2, message = "处方总金额格式不正确")
    private BigDecimal totalAmount;

    /**
     * 用法用量说明
     */
    @Size(max = 1000, message = "用法用量说明长度不能超过1000个字符")
    private String dosageInstructions;

    /**
     * 注意事项
     */
    @Size(max = 1000, message = "注意事项长度不能超过1000个字符")
    private String precautions;

    /**
     * 临床诊断
     */
    @Size(max = 500, message = "临床诊断长度不能超过500个字符")
    private String clinicalDiagnosis;

    /**
     * 处方有效期（天）
     */
    @Min(value = 1, message = "处方有效期必须大于0")
    private Integer validityDays = 3;

    /**
     * 是否急诊处方
     */
    private Boolean isEmergency = false;

    /**
     * 是否儿童处方
     */
    private Boolean isChildPrescription = false;

    /**
     * 是否慢性病处方
     */
    private Boolean isChronicDiseasePrescription = false;

    /**
     * 重复次数
     */
    @Min(value = 1, message = "重复次数必须大于0")
    private Integer repeatTimes = 1;

    /**
     * 审核意见
     */
    @Size(max = 500, message = "审核意见长度不能超过500个字符")
    private String reviewComments;

    /**
     * 取消原因
     */
    @Size(max = 500, message = "取消原因长度不能超过500个字符")
    private String cancelReason;

    /**
     * 取消时间
     */
    private LocalDateTime cancelledAt;

    /**
     * 备注信息
     */
    @Size(max = 1000, message = "备注信息长度不能超过1000个字符")
    private String remarks;

    /**
     * 处方项目列表（不存储在数据库中，通过关联查询获取）
     */
    @TableField(exist = false)
    private List<PrescriptionItem> prescriptionItems;

    // 构造函数
    public Prescription() {}

    public Prescription(Long medicalRecordId, Long doctorId, String prescriptionNumber) {
        this.medicalRecordId = medicalRecordId;
        this.doctorId = doctorId;
        this.prescriptionNumber = prescriptionNumber;
        this.prescribedAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getMedicalRecordId() {
        return medicalRecordId;
    }

    public void setMedicalRecordId(Long medicalRecordId) {
        this.medicalRecordId = medicalRecordId;
    }

    public Long getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(Long doctorId) {
        this.doctorId = doctorId;
    }

    public String getPrescriptionNumber() {
        return prescriptionNumber;
    }

    public void setPrescriptionNumber(String prescriptionNumber) {
        this.prescriptionNumber = prescriptionNumber;
    }

    public String getPrescriptionType() {
        return prescriptionType;
    }

    public void setPrescriptionType(String prescriptionType) {
        this.prescriptionType = prescriptionType;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getPrescribedAt() {
        return prescribedAt;
    }

    public void setPrescribedAt(LocalDateTime prescribedAt) {
        this.prescribedAt = prescribedAt;
    }

    public Long getReviewDoctorId() {
        return reviewDoctorId;
    }

    public void setReviewDoctorId(Long reviewDoctorId) {
        this.reviewDoctorId = reviewDoctorId;
    }

    public LocalDateTime getReviewedAt() {
        return reviewedAt;
    }

    public void setReviewedAt(LocalDateTime reviewedAt) {
        this.reviewedAt = reviewedAt;
    }

    public Long getPharmacistId() {
        return pharmacistId;
    }

    public void setPharmacistId(Long pharmacistId) {
        this.pharmacistId = pharmacistId;
    }

    public LocalDateTime getDispensedAt() {
        return dispensedAt;
    }

    public void setDispensedAt(LocalDateTime dispensedAt) {
        this.dispensedAt = dispensedAt;
    }

    public LocalDateTime getDeliveredAt() {
        return deliveredAt;
    }

    public void setDeliveredAt(LocalDateTime deliveredAt) {
        this.deliveredAt = deliveredAt;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getDosageInstructions() {
        return dosageInstructions;
    }

    public void setDosageInstructions(String dosageInstructions) {
        this.dosageInstructions = dosageInstructions;
    }

    public String getPrecautions() {
        return precautions;
    }

    public void setPrecautions(String precautions) {
        this.precautions = precautions;
    }

    public String getClinicalDiagnosis() {
        return clinicalDiagnosis;
    }

    public void setClinicalDiagnosis(String clinicalDiagnosis) {
        this.clinicalDiagnosis = clinicalDiagnosis;
    }

    public Integer getValidityDays() {
        return validityDays;
    }

    public void setValidityDays(Integer validityDays) {
        this.validityDays = validityDays;
    }

    public Boolean getIsEmergency() {
        return isEmergency;
    }

    public void setIsEmergency(Boolean isEmergency) {
        this.isEmergency = isEmergency;
    }

    public Boolean getIsChildPrescription() {
        return isChildPrescription;
    }

    public void setIsChildPrescription(Boolean isChildPrescription) {
        this.isChildPrescription = isChildPrescription;
    }

    public Boolean getIsChronicDiseasePrescription() {
        return isChronicDiseasePrescription;
    }

    public void setIsChronicDiseasePrescription(Boolean isChronicDiseasePrescription) {
        this.isChronicDiseasePrescription = isChronicDiseasePrescription;
    }

    public Integer getRepeatTimes() {
        return repeatTimes;
    }

    public void setRepeatTimes(Integer repeatTimes) {
        this.repeatTimes = repeatTimes;
    }

    public String getReviewComments() {
        return reviewComments;
    }

    public void setReviewComments(String reviewComments) {
        this.reviewComments = reviewComments;
    }

    public String getCancelReason() {
        return cancelReason;
    }

    public void setCancelReason(String cancelReason) {
        this.cancelReason = cancelReason;
    }

    public LocalDateTime getCancelledAt() {
        return cancelledAt;
    }

    public void setCancelledAt(LocalDateTime cancelledAt) {
        this.cancelledAt = cancelledAt;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public List<PrescriptionItem> getPrescriptionItems() {
        return prescriptionItems;
    }

    public void setPrescriptionItems(List<PrescriptionItem> prescriptionItems) {
        this.prescriptionItems = prescriptionItems;
    }

    /**
     * 计算处方总金额
     */
    public void calculateTotalAmount() {
        if (prescriptionItems == null || prescriptionItems.isEmpty()) {
            this.totalAmount = BigDecimal.ZERO;
            return;
        }
        
        BigDecimal total = prescriptionItems.stream()
            .map(item -> {
                BigDecimal unitPrice = item.getUnitPrice() != null ? item.getUnitPrice() : BigDecimal.ZERO;
                Integer quantity = item.getQuantity() != null ? item.getQuantity() : 0;
                return unitPrice.multiply(new BigDecimal(quantity));
            })
            .reduce(BigDecimal.ZERO, BigDecimal::add);
            
        this.totalAmount = total;
    }

    /**
     * 开具处方
     */
    public void issue() {
        if ("草稿".equals(status)) {
            this.status = "已开具";
            this.prescribedAt = LocalDateTime.now();
        }
    }

    /**
     * 审核处方
     */
    public void review(Long reviewDoctorId, String comments) {
        if ("已开具".equals(status)) {
            this.status = "已审核";
            this.reviewDoctorId = reviewDoctorId;
            this.reviewedAt = LocalDateTime.now();
            this.reviewComments = comments;
        }
    }

    /**
     * 调配处方
     */
    public void dispense(Long pharmacistId) {
        if ("已审核".equals(status)) {
            this.status = "已调配";
            this.pharmacistId = pharmacistId;
            this.dispensedAt = LocalDateTime.now();
        }
    }

    /**
     * 发药
     */
    public void deliver() {
        if ("已调配".equals(status)) {
            this.status = "已发药";
            this.deliveredAt = LocalDateTime.now();
        }
    }

    /**
     * 取消处方
     */
    public void cancel(String reason) {
        if (!"已发药".equals(status)) {
            this.status = "已取消";
            this.cancelReason = reason;
            this.cancelledAt = LocalDateTime.now();
        }
    }

    /**
     * 检查处方是否过期
     */
    public boolean isExpired() {
        if (prescribedAt == null || validityDays == null) {
            return false;
        }
        LocalDateTime expiryTime = prescribedAt.plusDays(validityDays);
        return LocalDateTime.now().isAfter(expiryTime);
    }

    /**
     * 检查是否可以调配
     */
    public boolean canBeDispensed() {
        return "已审核".equals(status) && !isExpired();
    }

    /**
     * 检查是否需要特殊审核
     */
    public boolean needsSpecialReview() {
        return "麻醉处方".equals(prescriptionType) || 
               "精神药品处方".equals(prescriptionType) || 
               "毒性药品处方".equals(prescriptionType);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Prescription)) return false;
        Prescription that = (Prescription) o;
        return prescriptionNumber != null && prescriptionNumber.equals(that.prescriptionNumber);
    }

    @Override
    public int hashCode() {
        return prescriptionNumber != null ? prescriptionNumber.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "Prescription{" +
                "id=" + getId() +
                ", medicalRecordId=" + medicalRecordId +
                ", doctorId=" + doctorId +
                ", prescriptionNumber='" + prescriptionNumber + '\'' +
                ", prescriptionType='" + prescriptionType + '\'' +
                ", status='" + status + '\'' +
                ", totalAmount=" + totalAmount +
                ", prescribedAt=" + prescribedAt +
                '}';
    }
}