package org.me.joy.clinic.dto;

import jakarta.validation.constraints.*;

/**
 * 出库请求DTO
 */
public class StockOutRequest {

    /**
     * 药品ID
     */
    @NotNull(message = "药品ID不能为空")
    private Long medicineId;

    /**
     * 出库数量
     */
    @NotNull(message = "出库数量不能为空")
    @Min(value = 1, message = "出库数量必须大于0")
    private Integer quantity;

    /**
     * 批次号（可选，如果不指定则按先进先出原则自动选择）
     */
    @Size(max = 100, message = "批次号长度不能超过100个字符")
    private String batchNumber;

    /**
     * 出库类型
     */
    @NotBlank(message = "出库类型不能为空")
    @Pattern(regexp = "^(销售|调拨|报损|过期处理|退货|其他)$", 
             message = "出库类型只能是销售、调拨、报损、过期处理、退货或其他")
    private String outboundType;

    /**
     * 相关单据号
     */
    @Size(max = 100, message = "相关单据号长度不能超过100个字符")
    private String relatedDocumentNumber;

    /**
     * 出库原因
     */
    @Size(max = 200, message = "出库原因长度不能超过200个字符")
    private String reason;

    /**
     * 目标位置（调拨时使用）
     */
    @Size(max = 100, message = "目标位置长度不能超过100个字符")
    private String targetLocation;

    /**
     * 接收人（调拨时使用）
     */
    @Size(max = 100, message = "接收人长度不能超过100个字符")
    private String receiver;

    /**
     * 备注
     */
    @Size(max = 500, message = "备注长度不能超过500个字符")
    private String remarks;

    // 构造函数
    public StockOutRequest() {}

    public StockOutRequest(Long medicineId, Integer quantity, String outboundType) {
        this.medicineId = medicineId;
        this.quantity = quantity;
        this.outboundType = outboundType;
    }

    public StockOutRequest(Long medicineId, Integer quantity, String batchNumber, String outboundType) {
        this.medicineId = medicineId;
        this.quantity = quantity;
        this.batchNumber = batchNumber;
        this.outboundType = outboundType;
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

    public String getOutboundType() {
        return outboundType;
    }

    public void setOutboundType(String outboundType) {
        this.outboundType = outboundType;
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

    public String getTargetLocation() {
        return targetLocation;
    }

    public void setTargetLocation(String targetLocation) {
        this.targetLocation = targetLocation;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    @Override
    public String toString() {
        return "StockOutRequest{" +
                "medicineId=" + medicineId +
                ", quantity=" + quantity +
                ", batchNumber='" + batchNumber + '\'' +
                ", outboundType='" + outboundType + '\'' +
                ", reason='" + reason + '\'' +
                '}';
    }
}