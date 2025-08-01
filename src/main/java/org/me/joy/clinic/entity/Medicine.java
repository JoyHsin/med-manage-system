package org.me.joy.clinic.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 药品实体类
 * 存储药品的基本信息和规格数据
 */
@TableName("medicines")
public class Medicine extends BaseEntity {

    /**
     * 药品编码（唯一标识）
     */
    @NotBlank(message = "药品编码不能为空")
    @Size(max = 50, message = "药品编码长度不能超过50个字符")
    private String medicineCode;

    /**
     * 药品名称
     */
    @NotBlank(message = "药品名称不能为空")
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
    @NotBlank(message = "药品分类不能为空")
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
    @NotBlank(message = "单位不能为空")
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
    private Boolean requiresPrescription = false;

    /**
     * 是否为特殊管制药品
     */
    private Boolean isControlledSubstance = false;

    /**
     * 是否启用
     */
    private Boolean enabled = true;

    /**
     * 备注信息
     */
    @Size(max = 500, message = "备注信息长度不能超过500个字符")
    private String remarks;

    /**
     * 供应商ID
     */
    private Long supplierId;

    /**
     * 创建人员ID
     */
    private Long createdBy;

    /**
     * 最后更新人员ID
     */
    private Long lastUpdatedBy;

    /**
     * 库存交易记录（不存储在数据库中，通过关联查询获取）
     */
    @TableField(exist = false)
    private List<StockTransaction> stockTransactions;

    /**
     * 库存水平记录（不存储在数据库中，通过关联查询获取）
     */
    @TableField(exist = false)
    private List<InventoryLevel> inventoryLevels;

    // 构造函数
    public Medicine() {}

    public Medicine(String medicineCode, String name, String category) {
        this.medicineCode = medicineCode;
        this.name = name;
        this.category = category;
    }

    // Getters and Setters
    public String getMedicineCode() {
        return medicineCode;
    }

    public void setMedicineCode(String medicineCode) {
        this.medicineCode = medicineCode;
    }

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

    public Long getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Long createdBy) {
        this.createdBy = createdBy;
    }

    public Long getLastUpdatedBy() {
        return lastUpdatedBy;
    }

    public void setLastUpdatedBy(Long lastUpdatedBy) {
        this.lastUpdatedBy = lastUpdatedBy;
    }

    public List<StockTransaction> getStockTransactions() {
        return stockTransactions;
    }

    public void setStockTransactions(List<StockTransaction> stockTransactions) {
        this.stockTransactions = stockTransactions;
    }

    public List<InventoryLevel> getInventoryLevels() {
        return inventoryLevels;
    }

    public void setInventoryLevels(List<InventoryLevel> inventoryLevels) {
        this.inventoryLevels = inventoryLevels;
    }

    /**
     * 计算利润率
     */
    public BigDecimal getProfitMargin() {
        if (purchasePrice == null || sellingPrice == null || 
            purchasePrice.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        return sellingPrice.subtract(purchasePrice)
                          .divide(purchasePrice, 4, BigDecimal.ROUND_HALF_UP)
                          .multiply(new BigDecimal("100"));
    }

    /**
     * 检查是否需要补货
     */
    public boolean needsRestock(Integer currentStock) {
        if (currentStock == null || minStockLevel == null) {
            return false;
        }
        return currentStock <= minStockLevel;
    }

    /**
     * 检查是否库存过多
     */
    public boolean isOverstocked(Integer currentStock) {
        if (currentStock == null || maxStockLevel == null) {
            return false;
        }
        return currentStock > maxStockLevel;
    }

    /**
     * 检查是否为处方药
     */
    public boolean isPrescriptionDrug() {
        return "处方药".equals(category) || Boolean.TRUE.equals(requiresPrescription);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Medicine)) return false;
        Medicine medicine = (Medicine) o;
        return medicineCode != null && medicineCode.equals(medicine.medicineCode);
    }

    @Override
    public int hashCode() {
        return medicineCode != null ? medicineCode.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "Medicine{" +
                "id=" + getId() +
                ", medicineCode='" + medicineCode + '\'' +
                ", name='" + name + '\'' +
                ", category='" + category + '\'' +
                ", specification='" + specification + '\'' +
                ", unit='" + unit + '\'' +
                ", enabled=" + enabled +
                '}';
    }
}