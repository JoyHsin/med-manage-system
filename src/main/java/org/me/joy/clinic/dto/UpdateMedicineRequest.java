package org.me.joy.clinic.dto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

/**
 * 更新药品请求DTO
 */
public class UpdateMedicineRequest {

    /**
     * 药品名称
     */
    @Size(max = 200, message = "药品名称长度不能超过200个字符")
    private String name;

    /**
     * 通用名称
     */
    @Size(max = 200, message = "通用名称长度不能超过200个字符")
    private String genericName;

    /**
     * 商品名称
     */
    @Size(max = 200, message = "商品名称长度不能超过200个字符")
    private String brandName;

    /**
     * 药品分类
     */
    @Pattern(regexp = "^(处方药|非处方药|中药|西药|生物制品|疫苗|其他)$", 
             message = "药品分类只能是处方药、非处方药、中药、西药、生物制品、疫苗或其他")
    private String category;

    /**
     * 药品类型
     */
    @Size(max = 100, message = "药品类型长度不能超过100个字符")
    private String type;

    /**
     * 剂型
     */
    @Size(max = 50, message = "剂型长度不能超过50个字符")
    private String dosageForm;

    /**
     * 规格
     */
    @Size(max = 100, message = "规格长度不能超过100个字符")
    private String specification;

    /**
     * 单位
     */
    @Size(max = 20, message = "单位长度不能超过20个字符")
    private String unit;

    /**
     * 生产厂家
     */
    @Size(max = 200, message = "生产厂家长度不能超过200个字符")
    private String manufacturer;

    /**
     * 批准文号
     */
    @Size(max = 100, message = "批准文号长度不能超过100个字符")
    private String approvalNumber;

    /**
     * 国药准字号
     */
    @Size(max = 100, message = "国药准字号长度不能超过100个字符")
    private String drugLicenseNumber;

    /**
     * 进价
     */
    @DecimalMin(value = "0.0", message = "进价不能为负数")
    @Digits(integer = 8, fraction = 2, message = "进价格式不正确")
    private BigDecimal purchasePrice;

    /**
     * 售价
     */
    @DecimalMin(value = "0.0", message = "售价不能为负数")
    @Digits(integer = 8, fraction = 2, message = "售价格式不正确")
    private BigDecimal sellingPrice;

    /**
     * 存储条件
     */
    @Size(max = 200, message = "存储条件长度不能超过200个字符")
    private String storageConditions;

    /**
     * 适应症
     */
    @Size(max = 1000, message = "适应症长度不能超过1000个字符")
    private String indications;

    /**
     * 禁忌症
     */
    @Size(max = 1000, message = "禁忌症长度不能超过1000个字符")
    private String contraindications;

    /**
     * 不良反应
     */
    @Size(max = 1000, message = "不良反应长度不能超过1000个字符")
    private String adverseReactions;

    /**
     * 用法用量
     */
    @Size(max = 500, message = "用法用量长度不能超过500个字符")
    private String dosageAndUsage;

    /**
     * 注意事项
     */
    @Size(max = 1000, message = "注意事项长度不能超过1000个字符")
    private String precautions;

    /**
     * 药物相互作用
     */
    @Size(max = 1000, message = "药物相互作用长度不能超过1000个字符")
    private String drugInteractions;

    /**
     * 有效期（月）
     */
    @Min(value = 1, message = "有效期必须大于0")
    private Integer shelfLifeMonths;

    /**
     * 最小库存量
     */
    @Min(value = 0, message = "最小库存量不能为负数")
    private Integer minStockLevel;

    /**
     * 最大库存量
     */
    @Min(value = 0, message = "最大库存量不能为负数")
    private Integer maxStockLevel;

    /**
     * 安全库存量
     */
    @Min(value = 0, message = "安全库存量不能为负数")
    private Integer safetyStockLevel;

    /**
     * 是否需要处方
     */
    private Boolean requiresPrescription;

    /**
     * 是否为特殊管制药品
     */
    private Boolean isControlledSubstance;

    /**
     * 是否启用
     */
    private Boolean enabled;

    /**
     * 备注信息
     */
    @Size(max = 500, message = "备注信息长度不能超过500个字符")
    private String remarks;

    /**
     * 供应商ID
     */
    private Long supplierId;

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGenericName() {
        return genericName;
    }

    public void setGenericName(String genericName) {
        this.genericName = genericName;
    }

    public String getBrandName() {
        return brandName;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDosageForm() {
        return dosageForm;
    }

    public void setDosageForm(String dosageForm) {
        this.dosageForm = dosageForm;
    }

    public String getSpecification() {
        return specification;
    }

    public void setSpecification(String specification) {
        this.specification = specification;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public String getApprovalNumber() {
        return approvalNumber;
    }

    public void setApprovalNumber(String approvalNumber) {
        this.approvalNumber = approvalNumber;
    }

    public String getDrugLicenseNumber() {
        return drugLicenseNumber;
    }

    public void setDrugLicenseNumber(String drugLicenseNumber) {
        this.drugLicenseNumber = drugLicenseNumber;
    }

    public BigDecimal getPurchasePrice() {
        return purchasePrice;
    }

    public void setPurchasePrice(BigDecimal purchasePrice) {
        this.purchasePrice = purchasePrice;
    }

    public BigDecimal getSellingPrice() {
        return sellingPrice;
    }

    public void setSellingPrice(BigDecimal sellingPrice) {
        this.sellingPrice = sellingPrice;
    }

    public String getStorageConditions() {
        return storageConditions;
    }

    public void setStorageConditions(String storageConditions) {
        this.storageConditions = storageConditions;
    }

    public String getIndications() {
        return indications;
    }

    public void setIndications(String indications) {
        this.indications = indications;
    }

    public String getContraindications() {
        return contraindications;
    }

    public void setContraindications(String contraindications) {
        this.contraindications = contraindications;
    }

    public String getAdverseReactions() {
        return adverseReactions;
    }

    public void setAdverseReactions(String adverseReactions) {
        this.adverseReactions = adverseReactions;
    }

    public String getDosageAndUsage() {
        return dosageAndUsage;
    }

    public void setDosageAndUsage(String dosageAndUsage) {
        this.dosageAndUsage = dosageAndUsage;
    }

    public String getPrecautions() {
        return precautions;
    }

    public void setPrecautions(String precautions) {
        this.precautions = precautions;
    }

    public String getDrugInteractions() {
        return drugInteractions;
    }

    public void setDrugInteractions(String drugInteractions) {
        this.drugInteractions = drugInteractions;
    }

    public Integer getShelfLifeMonths() {
        return shelfLifeMonths;
    }

    public void setShelfLifeMonths(Integer shelfLifeMonths) {
        this.shelfLifeMonths = shelfLifeMonths;
    }

    public Integer getMinStockLevel() {
        return minStockLevel;
    }

    public void setMinStockLevel(Integer minStockLevel) {
        this.minStockLevel = minStockLevel;
    }

    public Integer getMaxStockLevel() {
        return maxStockLevel;
    }

    public void setMaxStockLevel(Integer maxStockLevel) {
        this.maxStockLevel = maxStockLevel;
    }

    public Integer getSafetyStockLevel() {
        return safetyStockLevel;
    }

    public void setSafetyStockLevel(Integer safetyStockLevel) {
        this.safetyStockLevel = safetyStockLevel;
    }

    public Boolean getRequiresPrescription() {
        return requiresPrescription;
    }

    public void setRequiresPrescription(Boolean requiresPrescription) {
        this.requiresPrescription = requiresPrescription;
    }

    public Boolean getIsControlledSubstance() {
        return isControlledSubstance;
    }

    public void setIsControlledSubstance(Boolean isControlledSubstance) {
        this.isControlledSubstance = isControlledSubstance;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public Long getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(Long supplierId) {
        this.supplierId = supplierId;
    }
}