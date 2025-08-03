package org.me.joy.clinic.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 保险资格实体类
 */
@TableName("insurance_eligibilities")
public class InsuranceEligibility extends BaseEntity {

    /**
     * 患者ID
     */
    @NotNull(message = "患者ID不能为空")
    private Long patientId;

    /**
     * 保险提供商ID
     */
    @NotNull(message = "保险提供商ID不能为空")
    private Long insuranceProviderId;

    /**
     * 保险号码
     */
    @NotBlank(message = "保险号码不能为空")
    @Size(max = 100, message = "保险号码长度不能超过100个字符")
    private String insuranceNumber;

    /**
     * 保险类型
     */
    @Size(max = 50, message = "保险类型长度不能超过50个字符")
    private String insuranceType;

    /**
     * 生效日期
     */
    @NotNull(message = "生效日期不能为空")
    private LocalDate effectiveDate;

    /**
     * 到期日期
     */
    private LocalDate expiryDate;

    /**
     * 覆盖范围
     */
    @Size(max = 500, message = "覆盖范围长度不能超过500个字符")
    private String coverageDetails;

    /**
     * 年度限额
     */
    @DecimalMin(value = "0.0", message = "年度限额不能为负数")
    @Digits(integer = 10, fraction = 2, message = "年度限额格式不正确")
    private BigDecimal annualLimit;

    /**
     * 已使用金额
     */
    @DecimalMin(value = "0.0", message = "已使用金额不能为负数")
    @Digits(integer = 10, fraction = 2, message = "已使用金额格式不正确")
    private BigDecimal usedAmount = BigDecimal.ZERO;

    /**
     * 自付额
     */
    @DecimalMin(value = "0.0", message = "自付额不能为负数")
    @Digits(integer = 10, fraction = 2, message = "自付额格式不正确")
    private BigDecimal deductible = BigDecimal.ZERO;

    /**
     * 共付比例
     */
    @DecimalMin(value = "0.0", message = "共付比例不能为负数")
    @DecimalMax(value = "1.0", message = "共付比例不能超过1.0")
    private BigDecimal copaymentRate = BigDecimal.ZERO;

    /**
     * 状态
     */
    @Pattern(regexp = "^(有效|无效|暂停|过期)$", 
             message = "状态只能是有效、无效、暂停或过期")
    private String status = "有效";

    /**
     * 验证状态
     */
    @Pattern(regexp = "^(未验证|已验证|验证失败)$", 
             message = "验证状态只能是未验证、已验证或验证失败")
    private String verificationStatus = "未验证";

    /**
     * 备注信息
     */
    @Size(max = 1000, message = "备注信息长度不能超过1000个字符")
    private String remarks;

    // 构造函数
    public InsuranceEligibility() {}

    public InsuranceEligibility(Long patientId, Long insuranceProviderId, String insuranceNumber, 
                               LocalDate effectiveDate) {
        this.patientId = patientId;
        this.insuranceProviderId = insuranceProviderId;
        this.insuranceNumber = insuranceNumber;
        this.effectiveDate = effectiveDate;
    }

    // Getters and Setters
    public Long getPatientId() {
        return patientId;
    }

    public void setPatientId(Long patientId) {
        this.patientId = patientId;
    }

    public Long getInsuranceProviderId() {
        return insuranceProviderId;
    }

    public void setInsuranceProviderId(Long insuranceProviderId) {
        this.insuranceProviderId = insuranceProviderId;
    }

    public String getInsuranceNumber() {
        return insuranceNumber;
    }

    public void setInsuranceNumber(String insuranceNumber) {
        this.insuranceNumber = insuranceNumber;
    }

    public String getInsuranceType() {
        return insuranceType;
    }

    public void setInsuranceType(String insuranceType) {
        this.insuranceType = insuranceType;
    }

    public LocalDate getEffectiveDate() {
        return effectiveDate;
    }

    public void setEffectiveDate(LocalDate effectiveDate) {
        this.effectiveDate = effectiveDate;
    }

    public LocalDate getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(LocalDate expiryDate) {
        this.expiryDate = expiryDate;
    }

    public String getCoverageDetails() {
        return coverageDetails;
    }

    public void setCoverageDetails(String coverageDetails) {
        this.coverageDetails = coverageDetails;
    }

    public BigDecimal getAnnualLimit() {
        return annualLimit;
    }

    public void setAnnualLimit(BigDecimal annualLimit) {
        this.annualLimit = annualLimit;
    }

    public BigDecimal getUsedAmount() {
        return usedAmount;
    }

    public void setUsedAmount(BigDecimal usedAmount) {
        this.usedAmount = usedAmount;
    }

    public BigDecimal getDeductible() {
        return deductible;
    }

    public void setDeductible(BigDecimal deductible) {
        this.deductible = deductible;
    }

    public BigDecimal getCopaymentRate() {
        return copaymentRate;
    }

    public void setCopaymentRate(BigDecimal copaymentRate) {
        this.copaymentRate = copaymentRate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getVerificationStatus() {
        return verificationStatus;
    }

    public void setVerificationStatus(String verificationStatus) {
        this.verificationStatus = verificationStatus;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    /**
     * 检查保险是否有效
     */
    public boolean isValid() {
        return "有效".equals(status) && 
               (expiryDate == null || !expiryDate.isBefore(LocalDate.now()));
    }

    /**
     * 获取剩余额度
     */
    public BigDecimal getRemainingLimit() {
        if (annualLimit == null) {
            return null;
        }
        return annualLimit.subtract(usedAmount != null ? usedAmount : BigDecimal.ZERO);
    }

    @Override
    public String toString() {
        return "InsuranceEligibility{" +
                "id=" + getId() +
                ", patientId=" + patientId +
                ", insuranceProviderId=" + insuranceProviderId +
                ", insuranceNumber='" + insuranceNumber + '\'' +
                ", insuranceType='" + insuranceType + '\'' +
                ", effectiveDate=" + effectiveDate +
                ", expiryDate=" + expiryDate +
                ", status='" + status + '\'' +
                '}';
    }
}