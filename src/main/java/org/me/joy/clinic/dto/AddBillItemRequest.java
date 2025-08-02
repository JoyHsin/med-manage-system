package org.me.joy.clinic.dto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

/**
 * 添加账单项目请求DTO
 */
public class AddBillItemRequest {
    
    /**
     * 项目类型
     */
    @NotBlank(message = "项目类型不能为空")
    @Pattern(regexp = "^(REGISTRATION|CONSULTATION|MEDICINE|EXAMINATION|TREATMENT|OTHER)$", 
             message = "项目类型只能是REGISTRATION、CONSULTATION、MEDICINE、EXAMINATION、TREATMENT或OTHER")
    private String itemType;
    
    /**
     * 项目名称
     */
    @NotBlank(message = "项目名称不能为空")
    @Size(max = 100, message = "项目名称长度不能超过100个字符")
    private String itemName;
    
    /**
     * 项目编码
     */
    @Size(max = 50, message = "项目编码长度不能超过50个字符")
    private String itemCode;
    
    /**
     * 单价
     */
    @NotNull(message = "单价不能为空")
    @DecimalMin(value = "0.00", message = "单价不能为负数")
    private BigDecimal unitPrice;
    
    /**
     * 数量
     */
    @NotNull(message = "数量不能为空")
    @Min(value = 1, message = "数量必须大于0")
    private Integer quantity;
    
    /**
     * 折扣金额
     */
    @DecimalMin(value = "0.00", message = "折扣金额不能为负数")
    private BigDecimal discount;
    
    /**
     * 规格/单位
     */
    @Size(max = 50, message = "规格/单位长度不能超过50个字符")
    private String specification;
    
    /**
     * 备注
     */
    @Size(max = 500, message = "备注长度不能超过500个字符")
    private String notes;
    
    /**
     * 关联的处方项目ID（如果是药品费用）
     */
    private Long prescriptionItemId;
    
    /**
     * 关联的医疗记录ID
     */
    private Long medicalRecordId;

    // 构造函数
    public AddBillItemRequest() {
        this.quantity = 1;
        this.discount = BigDecimal.ZERO;
    }

    public AddBillItemRequest(String itemType, String itemName, BigDecimal unitPrice, Integer quantity) {
        this();
        this.itemType = itemType;
        this.itemName = itemName;
        this.unitPrice = unitPrice;
        this.quantity = quantity;
    }

    // Getters and Setters
    public String getItemType() {
        return itemType;
    }

    public void setItemType(String itemType) {
        this.itemType = itemType;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getItemCode() {
        return itemCode;
    }

    public void setItemCode(String itemCode) {
        this.itemCode = itemCode;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getDiscount() {
        return discount;
    }

    public void setDiscount(BigDecimal discount) {
        this.discount = discount;
    }

    public String getSpecification() {
        return specification;
    }

    public void setSpecification(String specification) {
        this.specification = specification;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Long getPrescriptionItemId() {
        return prescriptionItemId;
    }

    public void setPrescriptionItemId(Long prescriptionItemId) {
        this.prescriptionItemId = prescriptionItemId;
    }

    public Long getMedicalRecordId() {
        return medicalRecordId;
    }

    public void setMedicalRecordId(Long medicalRecordId) {
        this.medicalRecordId = medicalRecordId;
    }

    @Override
    public String toString() {
        return "AddBillItemRequest{" +
                "itemType='" + itemType + '\'' +
                ", itemName='" + itemName + '\'' +
                ", unitPrice=" + unitPrice +
                ", quantity=" + quantity +
                ", discount=" + discount +
                '}';
    }
}