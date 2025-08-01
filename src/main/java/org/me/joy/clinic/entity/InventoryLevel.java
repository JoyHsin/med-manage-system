package org.me.joy.clinic.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 库存水平实体类
 * 记录药品的当前库存状态和批次信息
 */
@TableName("inventory_levels")
public class InventoryLevel extends BaseEntity {

    /**
     * 药品ID
     */
    @NotNull(message = "药品ID不能为空")
    private Long medicineId;

    /**
     * 批次号
     */
    @NotBlank(message = "批次号不能为空")
    @Size(max = 100, message = "批次号长度不能超过100个字符")
    private String batchNumber;

    /**
     * 当前库存数量
     */
    @Min(value = 0, message = "当前库存数量不能为负数")
    private Integer currentStock;

    /**
     * 可用库存数量（扣除预留、锁定等）
     */
    @Min(value = 0, message = "可用库存数量不能为负数")
    private Integer availableStock;

    /**
     * 预留库存数量
     */
    @Min(value = 0, message = "预留库存数量不能为负数")
    private Integer reservedStock = 0;

    /**
     * 锁定库存数量
     */
    @Min(value = 0, message = "锁定库存数量不能为负数")
    private Integer lockedStock = 0;

    /**
     * 进价
     */
    @DecimalMin(value = "0.0", message = "进价不能为负数")
    @Digits(integer = 8, fraction = 2, message = "进价格式不正确")
    private BigDecimal purchasePrice;

    /**
     * 生产日期
     */
    private LocalDate productionDate;

    /**
     * 过期日期
     */
    private LocalDate expiryDate;

    /**
     * 供应商ID
     */
    private Long supplierId;

    /**
     * 供应商名称
     */
    @Size(max = 200, message = "供应商名称长度不能超过200个字符")
    private String supplierName;

    /**
     * 存储位置
     */
    @Size(max = 100, message = "存储位置长度不能超过100个字符")
    private String storageLocation;

    /**
     * 库存状态
     */
    @NotBlank(message = "库存状态不能为空")
    @Pattern(regexp = "^(正常|预警|过期|损坏|冻结)$", message = "库存状态只能是正常、预警、过期、损坏或冻结")
    private String status = "正常";

    /**
     * 最后盘点日期
     */
    private LocalDate lastInventoryDate;

    /**
     * 最后出库日期
     */
    private LocalDateTime lastOutboundDate;

    /**
     * 最后入库日期
     */
    private LocalDateTime lastInboundDate;

    /**
     * 库存成本
     */
    @DecimalMin(value = "0.0", message = "库存成本不能为负数")
    @Digits(integer = 10, fraction = 2, message = "库存成本格式不正确")
    private BigDecimal inventoryCost;

    /**
     * 备注信息
     */
    @Size(max = 500, message = "备注信息长度不能超过500个字符")
    private String remarks;

    /**
     * 创建人员ID
     */
    private Long createdBy;

    /**
     * 最后更新人员ID
     */
    private Long lastUpdatedBy;

    // 构造函数
    public InventoryLevel() {}

    public InventoryLevel(Long medicineId, String batchNumber, Integer currentStock) {
        this.medicineId = medicineId;
        this.batchNumber = batchNumber;
        this.currentStock = currentStock;
        this.availableStock = currentStock;
        this.lastInboundDate = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getMedicineId() {
        return medicineId;
    }

    public void setMedicineId(Long medicineId) {
        this.medicineId = medicineId;
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
        // 自动更新可用库存
        updateAvailableStock();
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
        updateAvailableStock();
    }

    public Integer getLockedStock() {
        return lockedStock;
    }

    public void setLockedStock(Integer lockedStock) {
        this.lockedStock = lockedStock;
        updateAvailableStock();
    }

    public BigDecimal getPurchasePrice() {
        return purchasePrice;
    }

    public void setPurchasePrice(BigDecimal purchasePrice) {
        this.purchasePrice = purchasePrice;
        calculateInventoryCost();
    }

    public LocalDate getProductionDate() {
        return productionDate;
    }

    public void setProductionDate(LocalDate productionDate) {
        this.productionDate = productionDate;
    }

    public LocalDate getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(LocalDate expiryDate) {
        this.expiryDate = expiryDate;
    }

    public Long getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(Long supplierId) {
        this.supplierId = supplierId;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }

    public String getStorageLocation() {
        return storageLocation;
    }

    public void setStorageLocation(String storageLocation) {
        this.storageLocation = storageLocation;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDate getLastInventoryDate() {
        return lastInventoryDate;
    }

    public void setLastInventoryDate(LocalDate lastInventoryDate) {
        this.lastInventoryDate = lastInventoryDate;
    }

    public LocalDateTime getLastOutboundDate() {
        return lastOutboundDate;
    }

    public void setLastOutboundDate(LocalDateTime lastOutboundDate) {
        this.lastOutboundDate = lastOutboundDate;
    }

    public LocalDateTime getLastInboundDate() {
        return lastInboundDate;
    }

    public void setLastInboundDate(LocalDateTime lastInboundDate) {
        this.lastInboundDate = lastInboundDate;
    }

    public BigDecimal getInventoryCost() {
        return inventoryCost;
    }

    public void setInventoryCost(BigDecimal inventoryCost) {
        this.inventoryCost = inventoryCost;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
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

    /**
     * 更新可用库存
     */
    private void updateAvailableStock() {
        if (currentStock != null) {
            int reserved = reservedStock != null ? reservedStock : 0;
            int locked = lockedStock != null ? lockedStock : 0;
            this.availableStock = Math.max(0, currentStock - reserved - locked);
        }
    }

    /**
     * 计算库存成本
     */
    private void calculateInventoryCost() {
        if (currentStock != null && purchasePrice != null) {
            this.inventoryCost = purchasePrice.multiply(new BigDecimal(currentStock));
        }
    }

    /**
     * 增加库存
     */
    public void addStock(Integer quantity) {
        if (quantity != null && quantity > 0) {
            this.currentStock = (this.currentStock != null ? this.currentStock : 0) + quantity;
            this.lastInboundDate = LocalDateTime.now();
            updateAvailableStock();
            calculateInventoryCost();
        }
    }

    /**
     * 减少库存
     */
    public void reduceStock(Integer quantity) {
        if (quantity != null && quantity > 0) {
            this.currentStock = Math.max(0, (this.currentStock != null ? this.currentStock : 0) - quantity);
            this.lastOutboundDate = LocalDateTime.now();
            updateAvailableStock();
            calculateInventoryCost();
        }
    }

    /**
     * 预留库存
     */
    public boolean reserveStock(Integer quantity) {
        if (quantity != null && quantity > 0 && availableStock != null && availableStock >= quantity) {
            this.reservedStock = (this.reservedStock != null ? this.reservedStock : 0) + quantity;
            updateAvailableStock();
            return true;
        }
        return false;
    }

    /**
     * 释放预留库存
     */
    public void releaseReservedStock(Integer quantity) {
        if (quantity != null && quantity > 0 && reservedStock != null) {
            this.reservedStock = Math.max(0, reservedStock - quantity);
            updateAvailableStock();
        }
    }

    /**
     * 检查是否库存不足
     */
    public boolean isLowStock(Integer minLevel) {
        if (currentStock == null || minLevel == null) {
            return false;
        }
        return currentStock <= minLevel;
    }

    /**
     * 检查是否即将过期（30天内）
     */
    public boolean isExpiringSoon() {
        if (expiryDate == null) {
            return false;
        }
        return expiryDate.isBefore(LocalDate.now().plusDays(30));
    }

    /**
     * 检查是否已过期
     */
    public boolean isExpired() {
        if (expiryDate == null) {
            return false;
        }
        return expiryDate.isBefore(LocalDate.now());
    }

    /**
     * 检查是否可用
     */
    public boolean isAvailable() {
        return "正常".equals(status) && !isExpired() && 
               availableStock != null && availableStock > 0;
    }

    /**
     * 计算剩余保质期天数
     */
    public Long getRemainingShelfLifeDays() {
        if (expiryDate == null) {
            return null;
        }
        return (long) LocalDate.now().until(expiryDate).getDays();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof InventoryLevel)) return false;
        InventoryLevel that = (InventoryLevel) o;
        return medicineId != null && medicineId.equals(that.medicineId) &&
               batchNumber != null && batchNumber.equals(that.batchNumber);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(medicineId, batchNumber);
    }

    @Override
    public String toString() {
        return "InventoryLevel{" +
                "id=" + getId() +
                ", medicineId=" + medicineId +
                ", batchNumber='" + batchNumber + '\'' +
                ", currentStock=" + currentStock +
                ", availableStock=" + availableStock +
                ", status='" + status + '\'' +
                ", expiryDate=" + expiryDate +
                '}';
    }
}