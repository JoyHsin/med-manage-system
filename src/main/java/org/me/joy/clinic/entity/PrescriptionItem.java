package org.me.joy.clinic.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;

/**
 * 处方项目实体类
 * 存储处方中的具体药品信息
 */
@TableName("prescription_items")
public class PrescriptionItem extends BaseEntity {

    /**
     * 处方ID
     */
    @NotNull(message = "处方ID不能为空")
    private Long prescriptionId;

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
     * 数量
     */
    @NotNull(message = "数量不能为空")
    @Min(value = 1, message = "数量必须大于0")
    private Integer quantity;

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
     * 是否替代药品
     */
    private Boolean isSubstitute = false;

    /**
     * 原药品ID（如果是替代药品）
     */
    private Long originalMedicineId;

    /**
     * 替代原因
     */
    @Size(max = 200, message = "替代原因长度不能超过200个字符")
    private String substituteReason;

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
    public PrescriptionItem() {}

    public PrescriptionItem(Long prescriptionId, Long medicineId, String medicineName, 
                           Integer quantity, String unit, BigDecimal unitPrice) {
        this.prescriptionId = prescriptionId;
        this.medicineId = medicineId;
        this.medicineName = medicineName;
        this.quantity = quantity;
        this.unit = unit;
        this.unitPrice = unitPrice;
        calculateSubtotal();
    }

    // Getters and Setters
    public Long getPrescriptionId() {
        return prescriptionId;
    }

    public void setPrescriptionId(Long prescriptionId) {
        this.prescriptionId = prescriptionId;
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

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
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

    public String getSubstituteReason() {
        return substituteReason;
    }

    public void setSubstituteReason(String substituteReason) {
        this.substituteReason = substituteReason;
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
        if (quantity != null && unitPrice != null) {
            this.subtotal = unitPrice.multiply(new BigDecimal(quantity));
        } else {
            this.subtotal = BigDecimal.ZERO;
        }
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

    /**
     * 检查是否为特殊药品
     */
    public boolean isSpecialMedicine() {
        return Boolean.TRUE.equals(isSubstitute) || 
               (specialInstructions != null && !specialInstructions.trim().isEmpty());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PrescriptionItem)) return false;
        PrescriptionItem that = (PrescriptionItem) o;
        return getId() != null && getId().equals(that.getId());
    }

    @Override
    public int hashCode() {
        return getId() != null ? getId().hashCode() : 0;
    }

    @Override
    public String toString() {
        return "PrescriptionItem{" +
                "id=" + getId() +
                ", prescriptionId=" + prescriptionId +
                ", medicineId=" + medicineId +
                ", medicineName='" + medicineName + '\'' +
                ", quantity=" + quantity +
                ", unit='" + unit + '\'' +
                ", unitPrice=" + unitPrice +
                ", subtotal=" + subtotal +
                '}';
    }
}