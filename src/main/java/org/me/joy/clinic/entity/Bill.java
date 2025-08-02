package org.me.joy.clinic.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 账单实体
 */
@TableName("bills")
public class Bill extends BaseEntity {
    
    /**
     * 患者ID
     */
    @NotNull(message = "患者ID不能为空")
    private Long patientId;
    
    /**
     * 挂号ID
     */
    private Long registrationId;
    
    /**
     * 账单编号
     */
    @NotBlank(message = "账单编号不能为空")
    private String billNumber;
    
    /**
     * 总金额
     */
    @NotNull(message = "总金额不能为空")
    @DecimalMin(value = "0.00", message = "总金额不能为负数")
    private BigDecimal totalAmount;
    
    /**
     * 已付金额
     */
    @NotNull(message = "已付金额不能为空")
    @DecimalMin(value = "0.00", message = "已付金额不能为负数")
    private BigDecimal paidAmount;
    
    /**
     * 账单状态：PENDING-待付款, PAID-已付款, PARTIALLY_PAID-部分付款, CANCELLED-已取消
     */
    @NotBlank(message = "账单状态不能为空")
    @Pattern(regexp = "^(PENDING|PAID|PARTIALLY_PAID|CANCELLED)$", message = "账单状态只能是PENDING、PAID、PARTIALLY_PAID或CANCELLED")
    private String status;
    
    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
    
    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
    
    /**
     * 备注
     */
    private String notes;
    
    /**
     * 创建人ID
     */
    private Long createdBy;
    
    /**
     * 更新人ID
     */
    private Long updatedBy;

    // 构造函数
    public Bill() {
        this.totalAmount = BigDecimal.ZERO;
        this.paidAmount = BigDecimal.ZERO;
        this.status = "PENDING";
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public Bill(Long patientId, String billNumber) {
        this();
        this.patientId = patientId;
        this.billNumber = billNumber;
    }

    // Getters and Setters
    public Long getPatientId() {
        return patientId;
    }

    public void setPatientId(Long patientId) {
        this.patientId = patientId;
    }

    public Long getRegistrationId() {
        return registrationId;
    }

    public void setRegistrationId(Long registrationId) {
        this.registrationId = registrationId;
    }

    public String getBillNumber() {
        return billNumber;
    }

    public void setBillNumber(String billNumber) {
        this.billNumber = billNumber;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
        this.updatedAt = LocalDateTime.now();
    }

    public BigDecimal getPaidAmount() {
        return paidAmount;
    }

    public void setPaidAmount(BigDecimal paidAmount) {
        this.paidAmount = paidAmount;
        this.updatedAt = LocalDateTime.now();
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
        this.updatedAt = LocalDateTime.now();
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
        this.updatedAt = LocalDateTime.now();
    }

    public Long getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Long createdBy) {
        this.createdBy = createdBy;
    }

    public Long getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(Long updatedBy) {
        this.updatedBy = updatedBy;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 计算剩余未付金额
     */
    public BigDecimal getRemainingAmount() {
        return totalAmount.subtract(paidAmount);
    }

    /**
     * 检查账单是否已完全支付
     */
    public boolean isFullyPaid() {
        return paidAmount.compareTo(totalAmount) >= 0;
    }

    /**
     * 检查账单是否部分支付
     */
    public boolean isPartiallyPaid() {
        return paidAmount.compareTo(BigDecimal.ZERO) > 0 && paidAmount.compareTo(totalAmount) < 0;
    }

    /**
     * 更新支付状态
     */
    public void updatePaymentStatus() {
        if (isFullyPaid()) {
            this.status = "PAID";
        } else if (isPartiallyPaid()) {
            this.status = "PARTIALLY_PAID";
        } else {
            this.status = "PENDING";
        }
        this.updatedAt = LocalDateTime.now();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Bill)) return false;
        Bill bill = (Bill) o;
        return billNumber != null && billNumber.equals(bill.billNumber);
    }

    @Override
    public int hashCode() {
        return billNumber != null ? billNumber.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "Bill{" +
                "id=" + getId() +
                ", patientId=" + patientId +
                ", registrationId=" + registrationId +
                ", billNumber='" + billNumber + '\'' +
                ", totalAmount=" + totalAmount +
                ", paidAmount=" + paidAmount +
                ", status='" + status + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}