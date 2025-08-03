package org.me.joy.clinic.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 医保理赔响应
 */
public class InsuranceClaimResponse {
    
    private Long id;
    private String claimNumber;
    private Long patientId;
    private String patientName;
    private Long billId;
    private String billNumber;
    private Long insuranceProviderId;
    private String insuranceProviderName;
    private String insuranceCardNumber;
    private BigDecimal totalAmount;
    private BigDecimal reimbursableAmount;
    private BigDecimal actualReimbursement;
    private BigDecimal personalPayment;
    private String claimStatus;
    private LocalDateTime claimDate;
    private LocalDateTime reviewDate;
    private LocalDateTime completedDate;
    private String reviewedBy;
    private String rejectionReason;
    private String externalClaimId;
    private String remarks;

    // Constructors
    public InsuranceClaimResponse() {}

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public Long getBillId() {
        return billId;
    }

    public void setBillId(Long billId) {
        this.billId = billId;
    }

    public String getBillNumber() {
        return billNumber;
    }

    public void setBillNumber(String billNumber) {
        this.billNumber = billNumber;
    }

    public Long getInsuranceProviderId() {
        return insuranceProviderId;
    }

    public void setInsuranceProviderId(Long insuranceProviderId) {
        this.insuranceProviderId = insuranceProviderId;
    }

    public String getInsuranceProviderName() {
        return insuranceProviderName;
    }

    public void setInsuranceProviderName(String insuranceProviderName) {
        this.insuranceProviderName = insuranceProviderName;
    }

    public String getInsuranceCardNumber() {
        return insuranceCardNumber;
    }

    public void setInsuranceCardNumber(String insuranceCardNumber) {
        this.insuranceCardNumber = insuranceCardNumber;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public BigDecimal getReimbursableAmount() {
        return reimbursableAmount;
    }

    public void setReimbursableAmount(BigDecimal reimbursableAmount) {
        this.reimbursableAmount = reimbursableAmount;
    }

    public BigDecimal getActualReimbursement() {
        return actualReimbursement;
    }

    public void setActualReimbursement(BigDecimal actualReimbursement) {
        this.actualReimbursement = actualReimbursement;
    }

    public BigDecimal getPersonalPayment() {
        return personalPayment;
    }

    public void setPersonalPayment(BigDecimal personalPayment) {
        this.personalPayment = personalPayment;
    }

    public String getClaimStatus() {
        return claimStatus;
    }

    public void setClaimStatus(String claimStatus) {
        this.claimStatus = claimStatus;
    }

    public LocalDateTime getClaimDate() {
        return claimDate;
    }

    public void setClaimDate(LocalDateTime claimDate) {
        this.claimDate = claimDate;
    }

    public LocalDateTime getReviewDate() {
        return reviewDate;
    }

    public void setReviewDate(LocalDateTime reviewDate) {
        this.reviewDate = reviewDate;
    }

    public LocalDateTime getCompletedDate() {
        return completedDate;
    }

    public void setCompletedDate(LocalDateTime completedDate) {
        this.completedDate = completedDate;
    }

    public String getReviewedBy() {
        return reviewedBy;
    }

    public void setReviewedBy(String reviewedBy) {
        this.reviewedBy = reviewedBy;
    }

    public String getRejectionReason() {
        return rejectionReason;
    }

    public void setRejectionReason(String rejectionReason) {
        this.rejectionReason = rejectionReason;
    }

    public String getExternalClaimId() {
        return externalClaimId;
    }

    public void setExternalClaimId(String externalClaimId) {
        this.externalClaimId = externalClaimId;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
}