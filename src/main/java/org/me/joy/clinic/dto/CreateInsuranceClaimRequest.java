package org.me.joy.clinic.dto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

/**
 * 创建保险理赔请求DTO
 */
public class CreateInsuranceClaimRequest {

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
    @DecimalMin(value = "0.01", message = "理赔金额必须大于0")
    @Digits(integer = 10, fraction = 2, message = "理赔金额格式不正确")
    private BigDecimal claimAmount;

    /**
     * 备注信息
     */
    @Size(max = 1000, message = "备注信息长度不能超过1000个字符")
    private String remarks;

    // 构造函数
    public CreateInsuranceClaimRequest() {}

    public CreateInsuranceClaimRequest(Long patientId, Long insuranceProviderId, 
                                     Long billId, BigDecimal claimAmount) {
        this.patientId = patientId;
        this.insuranceProviderId = insuranceProviderId;
        this.billId = billId;
        this.claimAmount = claimAmount;
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

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    @Override
    public String toString() {
        return "CreateInsuranceClaimRequest{" +
                "patientId=" + patientId +
                ", insuranceProviderId=" + insuranceProviderId +
                ", billId=" + billId +
                ", claimAmount=" + claimAmount +
                ", remarks='" + remarks + '\'' +
                '}';
    }
}