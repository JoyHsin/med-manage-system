package org.me.joy.clinic.dto;

import java.math.BigDecimal;

/**
 * 药品搜索条件DTO
 */
public class MedicineSearchCriteria {

    /**
     * 关键词（药品名称、通用名称、商品名称）
     */
    private String keyword;

    /**
     * 药品分类
     */
    private String category;

    /**
     * 生产厂家
     */
    private String manufacturer;

    /**
     * 是否启用
     */
    private Boolean enabled;

    /**
     * 是否需要处方
     */
    private Boolean requiresPrescription;

    /**
     * 是否为特殊管制药品
     */
    private Boolean isControlledSubstance;

    /**
     * 最小价格
     */
    private BigDecimal minPrice;

    /**
     * 最大价格
     */
    private BigDecimal maxPrice;

    /**
     * 供应商ID
     */
    private Long supplierId;

    /**
     * 是否需要补货
     */
    private Boolean needsRestock;

    /**
     * 是否库存过多
     */
    private Boolean isOverstocked;

    // Getters and Setters
    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
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

    public BigDecimal getMinPrice() {
        return minPrice;
    }

    public void setMinPrice(BigDecimal minPrice) {
        this.minPrice = minPrice;
    }

    public BigDecimal getMaxPrice() {
        return maxPrice;
    }

    public void setMaxPrice(BigDecimal maxPrice) {
        this.maxPrice = maxPrice;
    }

    public Long getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(Long supplierId) {
        this.supplierId = supplierId;
    }

    public Boolean getNeedsRestock() {
        return needsRestock;
    }

    public void setNeedsRestock(Boolean needsRestock) {
        this.needsRestock = needsRestock;
    }

    public Boolean getIsOverstocked() {
        return isOverstocked;
    }

    public void setIsOverstocked(Boolean isOverstocked) {
        this.isOverstocked = isOverstocked;
    }
}