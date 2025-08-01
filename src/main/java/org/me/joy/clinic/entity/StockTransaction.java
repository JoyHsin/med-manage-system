package org.me.joy.clinic.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 库存交易实体类
 * 记录药品的入库、出库等库存变动信息
 */
@TableName("stock_transactions")
public class StockTransaction extends BaseEntity {

    /**
     * 交易编号（唯一标识）
     */
    @NotBlank(message = "交易编号不能为空")
    @Size(max = 50, message = "交易编号长度不能超过50个字符")
    private String transactionNumber;

    /**
     * 药品ID
     */
    @NotNull(message = "药品ID不能为空")
    private Long medicineId;

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
     * 总金额
     */
    @DecimalMin(value = "0.0", message = "总金额不能为负数")
    @Digits(integer = 10, fraction = 2, message = "总金额格式不正确")
    private BigDecimal totalAmount;

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
     * 交易前库存
     */
    @Min(value = 0, message = "交易前库存不能为负数")
    private Integer stockBefore;

    /**
     * 交易后库存
     */
    @Min(value = 0, message = "交易后库存不能为负数")
    private Integer stockAfter;

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
     * 交易状态
     */
    @NotBlank(message = "交易状态不能为空")
    @Pattern(regexp = "^(待审核|已确认|已取消)$", message = "交易状态只能是待审核、已确认或已取消")
    private String status = "待审核";

    /**
     * 交易日期
     */
    @NotNull(message = "交易日期不能为空")
    private LocalDateTime transactionDate;

    /**
     * 操作人员ID
     */
    private Long operatorId;

    /**
     * 操作人员姓名
     */
    @Size(max = 100, message = "操作人员姓名长度不能超过100个字符")
    private String operatorName;

    /**
     * 审核人员ID
     */
    private Long reviewerId;

    /**
     * 审核人员姓名
     */
    @Size(max = 100, message = "审核人员姓名长度不能超过100个字符")
    private String reviewerName;

    /**
     * 审核时间
     */
    private LocalDateTime reviewTime;

    /**
     * 备注信息
     */
    @Size(max = 500, message = "备注信息长度不能超过500个字符")
    private String remarks;

    // 构造函数
    public StockTransaction() {}

    public StockTransaction(String transactionNumber, Long medicineId, String transactionType, Integer quantity) {
        this.transactionNumber = transactionNumber;
        this.medicineId = medicineId;
        this.transactionType = transactionType;
        this.quantity = quantity;
        this.transactionDate = LocalDateTime.now();
    }

    // Getters and Setters
    public String getTransactionNumber() {
        return transactionNumber;
    }

    public void setTransactionNumber(String transactionNumber) {
        this.transactionNumber = transactionNumber;
    }

    public Long getMedicineId() {
        return medicineId;
    }

    public void setMedicineId(Long medicineId) {
        this.medicineId = medicineId;
    }

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

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
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

    public Integer getStockBefore() {
        return stockBefore;
    }

    public void setStockBefore(Integer stockBefore) {
        this.stockBefore = stockBefore;
    }

    public Integer getStockAfter() {
        return stockAfter;
    }

    public void setStockAfter(Integer stockAfter) {
        this.stockAfter = stockAfter;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(LocalDateTime transactionDate) {
        this.transactionDate = transactionDate;
    }

    public Long getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(Long operatorId) {
        this.operatorId = operatorId;
    }

    public String getOperatorName() {
        return operatorName;
    }

    public void setOperatorName(String operatorName) {
        this.operatorName = operatorName;
    }

    public Long getReviewerId() {
        return reviewerId;
    }

    public void setReviewerId(Long reviewerId) {
        this.reviewerId = reviewerId;
    }

    public String getReviewerName() {
        return reviewerName;
    }

    public void setReviewerName(String reviewerName) {
        this.reviewerName = reviewerName;
    }

    public LocalDateTime getReviewTime() {
        return reviewTime;
    }

    public void setReviewTime(LocalDateTime reviewTime) {
        this.reviewTime = reviewTime;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    /**
     * 计算总金额
     */
    public void calculateTotalAmount() {
        if (unitPrice != null && quantity != null) {
            this.totalAmount = unitPrice.multiply(new BigDecimal(Math.abs(quantity)));
        }
    }

    /**
     * 检查是否为入库交易
     */
    public boolean isInboundTransaction() {
        return "入库".equals(transactionType) && quantity != null && quantity > 0;
    }

    /**
     * 检查是否为出库交易
     */
    public boolean isOutboundTransaction() {
        return "出库".equals(transactionType) && quantity != null && quantity < 0;
    }

    /**
     * 检查是否已确认
     */
    public boolean isConfirmed() {
        return "已确认".equals(status);
    }

    /**
     * 检查药品是否即将过期（30天内）
     */
    public boolean isExpiringSoon() {
        if (expiryDate == null) {
            return false;
        }
        return expiryDate.isBefore(LocalDate.now().plusDays(30));
    }

    /**
     * 检查药品是否已过期
     */
    public boolean isExpired() {
        if (expiryDate == null) {
            return false;
        }
        return expiryDate.isBefore(LocalDate.now());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof StockTransaction)) return false;
        StockTransaction that = (StockTransaction) o;
        return transactionNumber != null && transactionNumber.equals(that.transactionNumber);
    }

    @Override
    public int hashCode() {
        return transactionNumber != null ? transactionNumber.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "StockTransaction{" +
                "id=" + getId() +
                ", transactionNumber='" + transactionNumber + '\'' +
                ", medicineId=" + medicineId +
                ", transactionType='" + transactionType + '\'' +
                ", quantity=" + quantity +
                ", status='" + status + '\'' +
                ", transactionDate=" + transactionDate +
                '}';
    }
}