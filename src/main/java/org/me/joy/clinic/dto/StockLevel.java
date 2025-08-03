package org.me.joy.clinic.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 库存水平DTO
 */
public class StockLevel {

    /**
     * 药品ID
     */
    private Long medicineId;

    /**
     * 药品编码
     */
    private String medicineCode;

    /**
     * 药品名称
     */
    private String medicineName;

    /**
     * 批次号
     */
    private String batchNumber;

    /**
     * 当前库存数量
     */
    private Integer currentStock;

    /**
     * 可用库存数量
     */
    private Integer availableStock;

    /**
     * 预留库存数量
     */
    private Integer reservedStock;

    /**
     * 锁定库存数量
     */
    private Integer lockedStock;

    /**
     * 最小库存水平
     */
    private Integer minStockLevel;

    /**
     * 最大库存水平
     */
    private Integer maxStockLevel;

    /**
     * 安全库存水平
     */
    private Integer safetyStockLevel;

    /**
     * 进价
     */
    private BigDecimal purchasePrice;

    /**
     * 库存成本
     */
    private BigDecimal inventoryCost;

    /**
     * 过期日期
     */
    private LocalDate expiryDate;

    /**
     * 剩余保质期天数
     */
    private Long remainingShelfLifeDays;

    /**
     * 库存状态
     */
    private String status;

    /**
     * 存储位置
     */
    private String storageLocation;

    /**
     * 供应商名称
     */
    private String supplierName;

    // 构造函数
    public StockLevel() {}

    public StockLevel(Long medicineId, String medicineCode, String medicineName, 
                     String batchNumber, Integer currentStock, Integer availableStock) {
        this.medicineId = medicineId;
        this.medicineCode = medicineCode;
        this.medicineName = medicineName;
        this.batchNumber = batchNumber;
        this.currentStock = currentStock;
        this.availableStock = availableStock;
    }

    // Getters and Setters
    public Long getMedicineId() {
        return medicineId;
    }

    public void setMedicineId(Long medicineId) {
        this.medicineId = medicineId;
    }

    public String getMedicineCode() {
        return medicineCode;
    }

    public void setMedicineCode(String medicineCode) {
        this.medicineCode = medicineCode;
    }

    public String getMedicineName() {
        return medicineName;
    }

    public void setMedicineName(String medicineName) {
        this.medicineName = medicineName;
    }

    public String getBatchNumber() {
        return batchNumber;
    }

    public void setBatchNumber(String batchNumber) {
        this.batchNumber = batchNumber;
    }

    public Integer getCurrentStock() {
        return currentStock;
    }

    public void setCurrentStock(Integer currentStock) {
        this.currentStock = currentStock;
    }

    public Integer getAvailableStock() {
        return availableStock;
    }

    public void setAvailableStock(Integer availableStock) {
        this.availableStock = availableStock;
    }

    public Integer getReservedStock() {
        return reservedStock;
    }

    public void setReservedStock(Integer reservedStock) {
        this.reservedStock = reservedStock;
    }

    public Integer getLockedStock() {
        return lockedStock;
    }

    public void setLockedStock(Integer lockedStock) {
        this.lockedStock = lockedStock;
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

    public BigDecimal getPurchasePrice() {
        return purchasePrice;
    }

    public void setPurchasePrice(BigDecimal purchasePrice) {
        this.purchasePrice = purchasePrice;
    }

    public BigDecimal getInventoryCost() {
        return inventoryCost;
    }

    public void setInventoryCost(BigDecimal inventoryCost) {
        this.inventoryCost = inventoryCost;
    }

    public LocalDate getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(LocalDate expiryDate) {
        this.expiryDate = expiryDate;
    }

    public Long getRemainingShelfLifeDays() {
        return remainingShelfLifeDays;
    }

    public void setRemainingShelfLifeDays(Long remainingShelfLifeDays) {
        this.remainingShelfLifeDays = remainingShelfLifeDays;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStorageLocation() {
        return storageLocation;
    }

    public void setStorageLocation(String storageLocation) {
        this.storageLocation = storageLocation;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }

    /**
     * 检查是否库存不足
     */
    public boolean isLowStock() {
        if (currentStock == null || minStockLevel == null) {
            return false;
        }
        return currentStock <= minStockLevel;
    }

    /**
     * 检查是否库存过多
     */
    public boolean isOverstocked() {
        if (currentStock == null || maxStockLevel == null) {
            return false;
        }
        return currentStock > maxStockLevel;
    }

    /**
     * 检查是否即将过期（30天内）
     */
    public boolean isExpiringSoon() {
        return remainingShelfLifeDays != null && remainingShelfLifeDays <= 30 && remainingShelfLifeDays > 0;
    }

    /**
     * 检查是否已过期
     */
    public boolean isExpired() {
        return remainingShelfLifeDays != null && remainingShelfLifeDays <= 0;
    }

    /**
     * 检查是否可用
     */
    public boolean isAvailable() {
        return "正常".equals(status) && !isExpired() && 
               availableStock != null && availableStock > 0;
    }

    @Override
    public String toString() {
        return "StockLevel{" +
                "medicineId=" + medicineId +
                ", medicineCode='" + medicineCode + '\'' +
                ", medicineName='" + medicineName + '\'' +
                ", batchNumber='" + batchNumber + '\'' +
                ", currentStock=" + currentStock +
                ", availableStock=" + availableStock +
                ", status='" + status + '\'' +
                '}';
    }
}