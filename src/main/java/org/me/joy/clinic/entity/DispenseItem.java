package org.me.joy.clinic.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 调剂项目明细实体类
 * 存储每个药品的具体调剂信息
 */
@TableName("dispense_items")
public class DispenseItem extends BaseEntity {

    /**
     * 调剂记录ID
     */
    @NotNull(message = "调剂记录ID不能为空")
    private Long dispenseRecordId;

    /**
     * 处方项目ID
     */
    @NotNull(message = "处方项目ID不能为空")
    private Long prescriptionItemId;

    /**
     * 药品ID
     */
    @NotNull(message = "药品ID不能为空")
    private Long medicineId;

    /**
     * 药品名称
     */
    @NotBlank(message = "药品名称不能为空")
    @Size(max = 200, message = "药品名称长度不能超过200个字符")
    private String medicineName;

    /**
     * 药品规格
     */
    @Size(max = 100, message = "药品规格长度不能超过100个字符")
    private String specification;

    /**
     * 批号
     */
    @Size(max = 50, message = "批号长度不能超过50个字符")
    private String batchNumber;

    /**
     * 生产日期
     */
    private LocalDate productionDate;

    /**
     * 有效期至
     */
    private LocalDate expiryDate;

    /**
     * 处方数量
     */
    @NotNull(message = "处方数量不能为空")
    @Min(value = 1, message = "处方数量必须大于0")
    private Integer prescribedQuantity;

    /**
     * 实际调剂数量
     */
    @Min(value = 0, message = "实际调剂数量不能为负数")
    private Integer dispensedQuantity;

    /**
     * 单位
     */
    @NotBlank(message = "单位不能为空")
    @Size(max = 20, message = "单位长度不能超过20个字符")
    private String unit;

    /**
     * 单价
     */
    @NotNull(message = "单价不能为空")
    @DecimalMin(value = "0.0", message = "单价不能为负数")
    @Digits(integer = 8, fraction = 2, message = "单价格式不正确")
    private BigDecimal unitPrice;

    /**
     * 小计金额
     */
    @DecimalMin(value = "0.0", message = "小计金额不能为负数")
    @Digits(integer = 10, fraction = 2, message = "小计金额格式不正确")
    private BigDecimal subtotal;

    /**
     * 调剂状态
     */
    @Pattern(regexp = "^(待调剂|已调剂|缺货|替代|退回)$", 
             message = "调剂状态只能是待调剂、已调剂、缺货、替代或退回")
    private String status = "待调剂";

    /**
     * 库存检查结果
     */
    @Pattern(regexp = "^(充足|不足|无库存)$", 
             message = "库存检查结果只能是充足、不足或无库存")
    private String stockStatus;

    /**
     * 调剂前库存数量
     */
    @Min(value = 0, message = "调剂前库存数量不能为负数")
    private Integer stockBeforeDispense;

    /**
     * 调剂后库存数量
     */
    @Min(value = 0, message = "调剂后库存数量不能为负数")
    private Integer stockAfterDispense;

    /**
     * 是否为替代药品
     */
    private Boolean isSubstitute = false;

    /**
     * 原药品ID（如果是替代药品）
     */
    private Long originalMedicineId;

    /**
     * 原药品名称
     */
    @Size(max = 200, message = "原药品名称长度不能超过200个字符")
    private String originalMedicineName;

    /**
     * 替代原因
     */
    @Size(max = 200, message = "替代原因长度不能超过200个字符")
    private String substituteReason;

    /**
     * 用法
     */
    @Size(max = 200, message = "用法长度不能超过200个字符")
    private String usage;

    /**
     * 用量
     */
    @Size(max = 100, message = "用量长度不能超过100个字符")
    private String dosage;

    /**
     * 频次
     */
    @Size(max = 50, message = "频次长度不能超过50个字符")
    private String frequency;

    /**
     * 疗程（天）
     */
    @Min(value = 1, message = "疗程必须大于0")
    private Integer duration;

    /**
     * 特殊说明
     */
    @Size(max = 500, message = "特殊说明长度不能超过500个字符")
    private String specialInstructions;

    /**
     * 调剂说明
     */
    @Size(max = 500, message = "调剂说明长度不能超过500个字符")
    private String dispenseNotes;

    /**
     * 质量检查结果
     */
    @Pattern(regexp = "^(合格|不合格|需要复检)$", 
             message = "质量检查结果只能是合格、不合格或需要复检")
    private String qualityCheckResult = "合格";

    /**
     * 质量问题描述
     */
    @Size(max = 500, message = "质量问题描述长度不能超过500个字符")
    private String qualityIssues;

    /**
     * 调剂时间
     */
    private LocalDateTime dispensedTime;

    /**
     * 调剂人员ID
     */
    private Long dispensedBy;

    /**
     * 调剂人员姓名
     */
    @Size(max = 100, message = "调剂人员姓名长度不能超过100个字符")
    private String dispensedByName;

    /**
     * 复核人员ID
     */
    private Long reviewedBy;

    /**
     * 复核人员姓名
     */
    @Size(max = 100, message = "复核人员姓名长度不能超过100个字符")
    private String reviewedByName;

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
     * 退回原因
     */
    @Size(max = 200, message = "退回原因长度不能超过200个字符")
    private String returnReason;

    /**
     * 退回时间
     */
    private LocalDateTime returnedTime;

    /**
     * 排序序号
     */
    private Integer sortOrder = 1;

    /**
     * 备注信息
     */
    @Size(max = 500, message = "备注信息长度不能超过500个字符")
    private String remarks;

    // 构造函数
    public DispenseItem() {}

    public DispenseItem(Long dispenseRecordId, Long prescriptionItemId, Long medicineId, 
                       String medicineName, Integer prescribedQuantity, String unit, BigDecimal unitPrice) {
        this.dispenseRecordId = dispenseRecordId;
        this.prescriptionItemId = prescriptionItemId;
        this.medicineId = medicineId;
        this.medicineName = medicineName;
        this.prescribedQuantity = prescribedQuantity;
        this.dispensedQuantity = prescribedQuantity; // 默认调剂数量等于处方数量
        this.unit = unit;
        this.unitPrice = unitPrice;
        calculateSubtotal();
    }

    // Getters and Setters
    public Long getDispenseRecordId() {
        return dispenseRecordId;
    }

    public void setDispenseRecordId(Long dispenseRecordId) {
        this.dispenseRecordId = dispenseRecordId;
    }

    public Long getPrescriptionItemId() {
        return prescriptionItemId;
    }

    public void setPrescriptionItemId(Long prescriptionItemId) {
        this.prescriptionItemId = prescriptionItemId;
    }

    public Long getMedicineId() {
        return medicineId;
    }

    public void setMedicineId(Long medicineId) {
        this.medicineId = medicineId;
    }

    public String getMedicineName() {
        return medicineName;
    }

    public void setMedicineName(String medicineName) {
        this.medicineName = medicineName;
    }

    public String getSpecification() {
        return specification;
    }

    public void setSpecification(String specification) {
        this.specification = specification;
    }

    public String getBatchNumber() {
        return batchNumber;
    }

    public void setBatchNumber(String batchNumber) {
        this.batchNumber = batchNumber;
    }

    public LocalDate getProductionDate() {
        return productionDate;
    }

    public void setProductionDate(LocalDate productionDate) {
        this.productionDate = productionDate;
    }

    public LocalDate getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(LocalDate expiryDate) {
        this.expiryDate = expiryDate;
    }

    public Integer getPrescribedQuantity() {
        return prescribedQuantity;
    }

    public void setPrescribedQuantity(Integer prescribedQuantity) {
        this.prescribedQuantity = prescribedQuantity;
    }

    public Integer getDispensedQuantity() {
        return dispensedQuantity;
    }

    public void setDispensedQuantity(Integer dispensedQuantity) {
        this.dispensedQuantity = dispensedQuantity;
        calculateSubtotal();
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
        calculateSubtotal();
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStockStatus() {
        return stockStatus;
    }

    public void setStockStatus(String stockStatus) {
        this.stockStatus = stockStatus;
    }

    public Integer getStockBeforeDispense() {
        return stockBeforeDispense;
    }

    public void setStockBeforeDispense(Integer stockBeforeDispense) {
        this.stockBeforeDispense = stockBeforeDispense;
    }

    public Integer getStockAfterDispense() {
        return stockAfterDispense;
    }

    public void setStockAfterDispense(Integer stockAfterDispense) {
        this.stockAfterDispense = stockAfterDispense;
    }

    public Boolean getIsSubstitute() {
        return isSubstitute;
    }

    public void setIsSubstitute(Boolean isSubstitute) {
        this.isSubstitute = isSubstitute;
    }

    public Long getOriginalMedicineId() {
        return originalMedicineId;
    }

    public void setOriginalMedicineId(Long originalMedicineId) {
        this.originalMedicineId = originalMedicineId;
    }

    public String getOriginalMedicineName() {
        return originalMedicineName;
    }

    public void setOriginalMedicineName(String originalMedicineName) {
        this.originalMedicineName = originalMedicineName;
    }

    public String getSubstituteReason() {
        return substituteReason;
    }

    public void setSubstituteReason(String substituteReason) {
        this.substituteReason = substituteReason;
    }

    public String getUsage() {
        return usage;
    }

    public void setUsage(String usage) {
        this.usage = usage;
    }

    public String getDosage() {
        return dosage;
    }

    public void setDosage(String dosage) {
        this.dosage = dosage;
    }

    public String getFrequency() {
        return frequency;
    }

    public void setFrequency(String frequency) {
        this.frequency = frequency;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public String getSpecialInstructions() {
        return specialInstructions;
    }

    public void setSpecialInstructions(String specialInstructions) {
        this.specialInstructions = specialInstructions;
    }

    public String getDispenseNotes() {
        return dispenseNotes;
    }

    public void setDispenseNotes(String dispenseNotes) {
        this.dispenseNotes = dispenseNotes;
    }

    public String getQualityCheckResult() {
        return qualityCheckResult;
    }

    public void setQualityCheckResult(String qualityCheckResult) {
        this.qualityCheckResult = qualityCheckResult;
    }

    public String getQualityIssues() {
        return qualityIssues;
    }

    public void setQualityIssues(String qualityIssues) {
        this.qualityIssues = qualityIssues;
    }

    public LocalDateTime getDispensedTime() {
        return dispensedTime;
    }

    public void setDispensedTime(LocalDateTime dispensedTime) {
        this.dispensedTime = dispensedTime;
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

    public Long getReviewedBy() {
        return reviewedBy;
    }

    public void setReviewedBy(Long reviewedBy) {
        this.reviewedBy = reviewedBy;
    }

    public String getReviewedByName() {
        return reviewedByName;
    }

    public void setReviewedByName(String reviewedByName) {
        this.reviewedByName = reviewedByName;
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

    public Integer getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    /**
     * 计算小计金额
     */
    public void calculateSubtotal() {
        if (dispensedQuantity != null && unitPrice != null) {
            this.subtotal = unitPrice.multiply(new BigDecimal(dispensedQuantity));
        } else {
            this.subtotal = BigDecimal.ZERO;
        }
    }

    /**
     * 调剂药品
     */
    public void dispense(Long dispensedBy, String dispensedByName, String batchNumber, 
                        LocalDate expiryDate, Integer stockBefore, Integer stockAfter) {
        if ("待调剂".equals(status)) {
            this.status = "已调剂";
            this.dispensedBy = dispensedBy;
            this.dispensedByName = dispensedByName;
            this.dispensedTime = LocalDateTime.now();
            this.batchNumber = batchNumber;
            this.expiryDate = expiryDate;
            this.stockBeforeDispense = stockBefore;
            this.stockAfterDispense = stockAfter;
        }
    }

    /**
     * 替代药品
     */
    public void substitute(Long newMedicineId, String newMedicineName, String reason) {
        this.originalMedicineId = this.medicineId;
        this.originalMedicineName = this.medicineName;
        this.medicineId = newMedicineId;
        this.medicineName = newMedicineName;
        this.isSubstitute = true;
        this.substituteReason = reason;
        this.status = "替代";
    }

    /**
     * 退回药品
     */
    public void returnItem(String reason) {
        this.status = "退回";
        this.returnReason = reason;
        this.returnedTime = LocalDateTime.now();
    }

    /**
     * 复核药品
     */
    public void review(Long reviewedBy, String reviewedByName, String comments) {
        this.reviewedBy = reviewedBy;
        this.reviewedByName = reviewedByName;
        this.reviewedTime = LocalDateTime.now();
        this.reviewComments = comments;
    }

    /**
     * 检查是否缺货
     */
    public boolean isOutOfStock() {
        return "无库存".equals(stockStatus) || "缺货".equals(status);
    }

    /**
     * 检查是否库存不足
     */
    public boolean isLowStock() {
        return "不足".equals(stockStatus) && stockBeforeDispense != null && 
               prescribedQuantity != null && stockBeforeDispense < prescribedQuantity;
    }

    /**
     * 检查是否已过期
     */
    public boolean isExpired() {
        return expiryDate != null && expiryDate.isBefore(LocalDate.now());
    }

    /**
     * 检查是否即将过期（30天内）
     */
    public boolean isNearExpiry() {
        return expiryDate != null && expiryDate.isBefore(LocalDate.now().plusDays(30));
    }

    /**
     * 获取完整的用法用量描述
     */
    public String getFullDosageDescription() {
        StringBuilder description = new StringBuilder();
        
        if (usage != null && !usage.trim().isEmpty()) {
            description.append(usage);
        }
        
        if (dosage != null && !dosage.trim().isEmpty()) {
            if (description.length() > 0) description.append("，");
            description.append("每次").append(dosage);
        }
        
        if (frequency != null && !frequency.trim().isEmpty()) {
            if (description.length() > 0) description.append("，");
            description.append(frequency);
        }
        
        if (duration != null && duration > 0) {
            if (description.length() > 0) description.append("，");
            description.append("连用").append(duration).append("天");
        }
        
        return description.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DispenseItem)) return false;
        DispenseItem that = (DispenseItem) o;
        return getId() != null && getId().equals(that.getId());
    }

    @Override
    public int hashCode() {
        return getId() != null ? getId().hashCode() : 0;
    }

    @Override
    public String toString() {
        return "DispenseItem{" +
                "id=" + getId() +
                ", dispenseRecordId=" + dispenseRecordId +
                ", medicineId=" + medicineId +
                ", medicineName='" + medicineName + '\'' +
                ", prescribedQuantity=" + prescribedQuantity +
                ", dispensedQuantity=" + dispensedQuantity +
                ", status='" + status + '\'' +
                '}';
    }
}