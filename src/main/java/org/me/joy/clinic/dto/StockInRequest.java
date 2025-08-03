package org.me.joy.clinic.dto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 入库请求DTO
 */
public class StockInRequest {

    /**
     * 药品ID
     */
    @NotNull(message = "药品ID不能为空")
    private Long medicineId;

    /**
     * 入库数量
     */
    @NotNull(message = "入库数量不能为空")
    @Min(value = 1, message = "入库数量必须大于0")
    private Integer quantity;

    /**
     * 批次号
     */
    @NotBlank(message = "批次号不能为空")
    @Size(max = 100, message = "批次号长度不能超过100个字符")
    private String batchNumber;

    /**
     * 进价
     */
    @NotNull(message = "进价不能为空")
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
    @NotNull(message = "过期日期不能为空")
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
     * 相关单据号
     */
    @Size(max = 100, message = "相关单据号长度不能超过100个字符")
    private String relatedDocumentNumber;

    /**
     * 入库原因
     */
    @Size(max = 200, message = "入库原因长度不能超过200个字符")
    private String reason;

    /**
     * 备注
     */
    @Size(max = 500, message = "备注长度不能超过500个字符")
    private String remarks;

    // 构造函数
    public StockInRequest() {}

    public StockInRequest(Long medicineId, Integer quantity, String batchNumber, BigDecimal purchasePrice, LocalDate expiryDate) {
        this.medicineId = medicineId;
        this.quantity = quantity;
        this.batchNumber = batchNumber;
        this.purchasePrice = purchasePrice;
        this.expiryDate = expiryDate;
    }

    // Getters and Setters
    public Long getMedicineId() {
        return medicineId;
    }

    public void setMedicineId(Long medicineId) {
        this.medicineId = medicineId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public String getBatchNumber() {
        return batchNumber;
    }

    public void setBatchNumber(String batchNumber) {
        this.batchNumber = batchNumber;
    }

    public BigDecimal getPurchasePrice() {
        return purchasePrice;
    }

    public void setPurchasePrice(BigDecimal purchasePrice) {
        this.purchasePrice = purchasePrice;
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

    public String getRelatedDocumentNumber() {
        return relatedDocumentNumber;
    }

    public void setRelatedDocumentNumber(String relatedDocumentNumber) {
        this.relatedDocumentNumber = relatedDocumentNumber;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    @Override
    public String toString() {
        return "StockInRequest{" +
                "medicineId=" + medicineId +
                ", quantity=" + quantity +
                ", batchNumber='" + batchNumber + '\'' +
                ", purchasePrice=" + purchasePrice +
                ", expiryDate=" + expiryDate +
                ", supplierName='" + supplierName + '\'' +
                '}';
    }
}