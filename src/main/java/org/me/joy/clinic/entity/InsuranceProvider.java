package org.me.joy.clinic.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.math.BigDecimal;

/**
 * 医保提供商实体
 */
@TableName("insurance_provider")
public class InsuranceProvider extends BaseEntity {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 医保机构代码
     */
    private String providerCode;
    
    /**
     * 医保机构名称
     */
    private String providerName;
    
    /**
     * 医保类型 (BASIC_MEDICAL, COMMERCIAL, GOVERNMENT)
     */
    private String insuranceType;
    
    /**
     * 联系电话
     */
    private String contactPhone;
    
    /**
     * 联系地址
     */
    private String contactAddress;
    
    /**
     * 报销比例 (0-1之间的小数)
     */
    private BigDecimal reimbursementRate;
    
    /**
     * 起付线金额
     */
    private BigDecimal deductibleAmount;
    
    /**
     * 年度报销上限
     */
    private BigDecimal annualLimit;
    
    /**
     * 是否启用
     */
    private Boolean enabled;
    
    /**
     * API接口地址
     */
    private String apiEndpoint;
    
    /**
     * API密钥
     */
    private String apiKey;
    
    /**
     * 备注
     */
    private String remarks;

    // Constructors
    public InsuranceProvider() {}

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getProviderCode() {
        return providerCode;
    }

    public void setProviderCode(String providerCode) {
        this.providerCode = providerCode;
    }

    public String getProviderName() {
        return providerName;
    }

    public void setProviderName(String providerName) {
        this.providerName = providerName;
    }

    public String getInsuranceType() {
        return insuranceType;
    }

    public void setInsuranceType(String insuranceType) {
        this.insuranceType = insuranceType;
    }

    public String getContactPhone() {
        return contactPhone;
    }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }

    public String getContactAddress() {
        return contactAddress;
    }

    public void setContactAddress(String contactAddress) {
        this.contactAddress = contactAddress;
    }

    public BigDecimal getReimbursementRate() {
        return reimbursementRate;
    }

    public void setReimbursementRate(BigDecimal reimbursementRate) {
        this.reimbursementRate = reimbursementRate;
    }

    public BigDecimal getDeductibleAmount() {
        return deductibleAmount;
    }

    public void setDeductibleAmount(BigDecimal deductibleAmount) {
        this.deductibleAmount = deductibleAmount;
    }

    public BigDecimal getAnnualLimit() {
        return annualLimit;
    }

    public void setAnnualLimit(BigDecimal annualLimit) {
        this.annualLimit = annualLimit;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public String getApiEndpoint() {
        return apiEndpoint;
    }

    public void setApiEndpoint(String apiEndpoint) {
        this.apiEndpoint = apiEndpoint;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof InsuranceProvider)) return false;
        InsuranceProvider that = (InsuranceProvider) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "InsuranceProvider{" +
                "id=" + id +
                ", providerCode='" + providerCode + '\'' +
                ", providerName='" + providerName + '\'' +
                ", insuranceType='" + insuranceType + '\'' +
                ", enabled=" + enabled +
                '}';
    }
}