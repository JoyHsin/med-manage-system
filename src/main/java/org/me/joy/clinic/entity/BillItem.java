package org.me.joy.clinic.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;

/**
 * 账单项目实体
 */
@TableName("bill_items")
public class BillItem extends BaseEntity {
    
    /**
     * 账单ID
     */
    @NotNull(message = "账单ID不能为空")
    private Long billId;
    
    /**
     * 项目类型：REGISTRATION-挂号费, CONSULTATION-诊疗费, MEDICINE-药品费, 
     * EXAMINATION-检查费, TREATMENT-治疗费, OTHER-其他费用
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
    @Min(value = 0, message = "数量不能为负数")
    private Integer quantity;
    
    /**
     * 小计金额
     */
    @NotNull(message = "小计金额不能为空")
    @DecimalMin(value = "0.00", message = "小计金额不能为负数")
    private BigDecimal subtotal;
    
    /**
     * 折扣金额
     */
    @NotNull(message = "折扣金额不能为空")
    @DecimalMin(value = "0.00", message = "折扣金额不能为负数")
    private BigDecimal discount;
    
    /**
     * 实际金额
     */
    @NotNull(message = "实际金额不能为空")
    @DecimalMin(value = "0.00", message = "实际金额不能为负数")
    private BigDecimal actualAmount;
    
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
    public BillItem() {
        this.quantity = 1;
        this.discount = BigDecimal.ZERO;
    }

    public BillItem(Long billId, String itemType, String itemName, BigDecimal unitPrice, Integer quantity) {
        this();
        this.billId = billId;
        this.itemType = itemType;
        this.itemName = itemName;
        this.unitPrice = unitPrice;
        this.quantity = quantity;
        calculateAmounts();
    }

    // Getters and Setters
    public Long getBillId() {
        return billId;
    }

    public void setBillId(Long billId) {
        this.billId = billId;
    }

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
        calculateAmounts();
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
        calculateAmounts();
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }

    public BigDecimal getDiscount() {
        return discount;
    }

    public void setDiscount(BigDecimal discount) {
        this.discount = discount;
        calculateActualAmount();
    }

    public BigDecimal getActualAmount() {
        return actualAmount;
    }

    public void setActualAmount(BigDecimal actualAmount) {
        this.actualAmount = actualAmount;
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

    /**
     * 计算小计和实际金额
     */
    public void calculateAmounts() {
        if (unitPrice != null && quantity != null) {
            this.subtotal = unitPrice.multiply(new BigDecimal(quantity));
            calculateActualAmount();
        }
    }

    /**
     * 计算实际金额（小计 - 折扣）
     */
    private void calculateActualAmount() {
        if (subtotal != null && discount != null) {
            this.actualAmount = subtotal.subtract(discount);
            // 确保实际金额不为负数
            if (this.actualAmount.compareTo(BigDecimal.ZERO) < 0) {
                this.actualAmount = BigDecimal.ZERO;
            }
        }
    }

    /**
     * 应用折扣率
     */
    public void applyDiscountRate(BigDecimal discountRate) {
        if (subtotal != null && discountRate != null) {
            this.discount = subtotal.multiply(discountRate);
            calculateActualAmount();
        }
    }

    /**
     * 检查是否为药品项目
     */
    public boolean isMedicineItem() {
        return "MEDICINE".equals(itemType);
    }

    /**
     * 检查是否有折扣
     */
    public boolean hasDiscount() {
        return discount != null && discount.compareTo(BigDecimal.ZERO) > 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BillItem)) return false;
        BillItem billItem = (BillItem) o;
        return getId() != null && getId().equals(billItem.getId());
    }

    @Override
    public int hashCode() {
        return getId() != null ? getId().hashCode() : 0;
    }

    @Override
    public String toString() {
        return "BillItem{" +
                "id=" + getId() +
                ", billId=" + billId +
                ", itemType='" + itemType + '\'' +
                ", itemName='" + itemName + '\'' +
                ", unitPrice=" + unitPrice +
                ", quantity=" + quantity +
                ", actualAmount=" + actualAmount +
                '}';
    }
}