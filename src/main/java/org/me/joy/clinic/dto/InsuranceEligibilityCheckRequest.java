package org.me.joy.clinic.dto;

import jakarta.validation.constraints.NotNull;

/**
 * 医保资格验证请求
 */
public class InsuranceEligibilityCheckRequest {
    
    @NotNull(message = "患者ID不能为空")
    private Long patientId;
    
    @NotNull(message = "医保卡号不能为空")
    private String insuranceCardNumber;
    
    @NotNull(message = "医保提供商ID不能为空")
    private Long insuranceProviderId;

    // Constructors
    public InsuranceEligibilityCheckRequest() {}

    // Getters and Setters
    public Long getPatientId() {
        return patientId;
    }

    public void setPatientId(Long patientId) {
        this.patientId = patientId;
    }

    public String getInsuranceCardNumber() {
        return insuranceCardNumber;
    }

    public void setInsuranceCardNumber(String insuranceCardNumber) {
        this.insuranceCardNumber = insuranceCardNumber;
    }

    public Long getInsuranceProviderId() {
        return insuranceProviderId;
    }

    public void setInsuranceProviderId(Long insuranceProviderId) {
        this.insuranceProviderId = insuranceProviderId;
    }
}