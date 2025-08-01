package org.me.joy.clinic.dto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 库存更新请求DTO
 */
public class StockUpdateRequest {

    /**
     * 交易类型
     */
    @NotBlank(message = "交易类型不能为空")
    @Pattern(regexp = "^(入库|出库|调拨|盘点|报损|退货|过期处理)$", 
             message = "交易类型只能是入库、出库、调拨、盘点、报损、退货或过期处理")
    private String transactionType;

    /**
     * 交易数量（正数表示增加，负数表示减少）
     */
    @NotNull(message = "交易数量不能为空")
    private Integer quantity;

    /**
     * 单价
     */
    @DecimalMin(value = "0.0", message = "单价不能为负数")
    @Digits(integer = 8, fraction = 2, message = "单价格式不正确")
    private BigDecimal unitPrice;

    /**
     * 批次号
     */
    @Size(max = 100, message = "批次号长度不能超过100个字符")
    private String batchNumber;

    /**
     * 生产日期
     */
    private LocalDate productionDate;

    /**
     * 过期日期
     */
    private LocalDate expiryDate;

    /**
     * 供应商ID（入库时）
     */
    private Long supplierId;

    /**
     * 供应商名称
     */
    @Size(max = 200, message = "供应商名称长度不能超过200个字符")
    private String supplierName;

    /**
     * 交易原因
     */
    @Size(max = 200, message = "交易原因长度不能超过200个字符")
    private String reason;

    /**
     * 相关单据号（如采购单号、销售单号等）
     */
    @Size(max = 100, message = "相关单据号长度不能超过100个字符")
    private String relatedDocumentNumber;

    /**
     * 存储位置
     */
    @Size(max = 100, message = "存储位置长度不能超过100个字符")
    private String storageLocation;

    /**
     * 备注信息
     */
    @Size(max = 500, message = "备注信息长度不能超过500个字符")
    private String remarks;

    // Getters and Setters
    public String getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    public String getBatchNumber() {
        return batchNumber;
    }

    public void setBatchNumber(String batchNumber) {
        this.batchNumber = batchNumber;
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

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getRelatedDocumentNumber() {
        return relatedDocumentNumber;
    }

    public void setRelatedDocumentNumber(String relatedDocumentNumber) {
        this.relatedDocumentNumber = relatedDocumentNumber;
    }

    public String getStorageLocation() {
        return storageLocation;
    }

    public void setStorageLocation(String storageLocation) {
        this.storageLocation = storageLocation;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
}