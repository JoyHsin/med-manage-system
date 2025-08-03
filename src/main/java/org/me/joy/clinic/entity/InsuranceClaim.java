package org.me.joy.clinic.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 保险理赔实体类
 */
@TableName("insurance_claims")
public class InsuranceClaim extends BaseEntity {

    /**
     * 理赔编号
     */
    @NotBlank(message = "理赔编号不能为空")
    @Size(max = 50, message = "理赔编号长度不能超过50个字符")
    private String claimNumber;

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
     * 账单ID
     */
    @NotNull(message = "账单ID不能为空")
    private Long billId;

    /**
     * 理赔金额
     */
    @NotNull(message = "理赔金额不能为空")
    @DecimalMin(value = "0.0", message = "理赔金额不能为负数")
    @Digits(integer = 10, fraction = 2, message = "理赔金额格式不正确")
    private BigDecimal claimAmount;

    /**
     * 批准金额
     */
    @DecimalMin(value = "0.0", message = "批准金额不能为负数")
    @Digits(integer = 10, fraction = 2, message = "批准金额格式不正确")
    private BigDecimal approvedAmount;

    /**
     * 理赔状态
     */
    @Pattern(regexp = "^(待提交|已提交|审核中|已批准|已拒绝|已支付)$", 
             message = "理赔状态只能是待提交、已提交、审核中、已批准、已拒绝或已支付")
    private String status = "待提交";

    /**
     * 提交时间
     */
    private LocalDateTime submittedAt;

    /**
     * 处理时间
     */
    private LocalDateTime processedAt;

    /**
     * 支付时间
     */
    private LocalDateTime paidAt;

    /**
     * 拒绝原因
     */
    @Size(max = 500, message = "拒绝原因长度不能超过500个字符")
    private String rejectionReason;

    /**
     * 备注信息
     */
    @Size(max = 1000, message = "备注信息长度不能超过1000个字符")
    private String remarks;

    // 构造函数
    public InsuranceClaim() {}

    public InsuranceClaim(String claimNumber, Long patientId, Long insuranceProviderId, 
                         Long billId, BigDecimal claimAmount) {
        this.claimNumber = claimNumber;
        this.patientId = patientId;
        this.insuranceProviderId = insuranceProviderId;
        this.billId = billId;
        this.claimAmount = claimAmount;
    }

    // Getters and Setters
    public String getClaimNumber() {
        return claimNumber;
    }

    public void setClaimNumber(String claimNumber) {
        this.claimNumber = claimNumber;
    }

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

    public Long getBillId() {
        return billId;
    }

    public void setBillId(Long billId) {
        this.billId = billId;
    }

    public BigDecimal getClaimAmount() {
        return claimAmount;
    }

    public void setClaimAmount(BigDecimal claimAmount) {
        this.claimAmount = claimAmount;
    }

    public BigDecimal getApprovedAmount() {
        return approvedAmount;
    }

    public void setApprovedAmount(BigDecimal approvedAmount) {
        this.approvedAmount = approvedAmount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getSubmittedAt() {
        return submittedAt;
    }

    public void setSubmittedAt(LocalDateTime submittedAt) {
        this.submittedAt = submittedAt;
    }

    public LocalDateTime getProcessedAt() {
        return processedAt;
    }

    public void setProcessedAt(LocalDateTime processedAt) {
        this.processedAt = processedAt;
    }

    public LocalDateTime getPaidAt() {
        return paidAt;
    }

    public void setPaidAt(LocalDateTime paidAt) {
        this.paidAt = paidAt;
    }

    public String getRejectionReason() {
        return rejectionReason;
    }

    public void setRejectionReason(String rejectionReason) {
        this.rejectionReason = rejectionReason;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    @Override
    public String toString() {
        return "InsuranceClaim{" +
                "id=" + getId() +
                ", claimNumber='" + claimNumber + '\'' +
                ", patientId=" + patientId +
                ", insuranceProviderId=" + insuranceProviderId +
                ", billId=" + billId +
                ", claimAmount=" + claimAmount +
                ", approvedAmount=" + approvedAmount +
                ", status='" + status + '\'' +
                '}';
    }
}