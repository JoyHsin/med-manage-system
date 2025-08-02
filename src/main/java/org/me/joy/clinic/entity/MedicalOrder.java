package org.me.joy.clinic.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

/**
 * 医嘱实体类
 * 用于记录医生开具的医嘱信息
 */
@TableName("medical_orders")
public class MedicalOrder extends BaseEntity {

    /**
     * 患者ID
     */
    @NotNull(message = "患者ID不能为空")
    private Long patientId;

    /**
     * 开具医嘱的医生ID
     */
    @NotNull(message = "医生ID不能为空")
    private Long prescribedBy;

    /**
     * 执行医嘱的护士ID
     */
    private Long executedBy;

    /**
     * 医嘱类型（注射、口服、检查、护理等）
     */
    @NotBlank(message = "医嘱类型不能为空")
    @Size(max = 50, message = "医嘱类型长度不能超过50个字符")
    private String orderType;

    /**
     * 医嘱内容
     */
    @NotBlank(message = "医嘱内容不能为空")
    @Size(max = 500, message = "医嘱内容长度不能超过500个字符")
    private String content;

    /**
     * 剂量
     */
    @Size(max = 100, message = "剂量长度不能超过100个字符")
    private String dosage;

    /**
     * 频次
     */
    @Size(max = 100, message = "频次长度不能超过100个字符")
    private String frequency;

    /**
     * 医嘱状态（待执行、已执行、暂缓执行、已取消）
     */
    @NotBlank(message = "医嘱状态不能为空")
    @Size(max = 20, message = "医嘱状态长度不能超过20个字符")
    private String status = "PENDING";

    /**
     * 优先级（紧急、普通、低）
     */
    @Size(max = 20, message = "优先级长度不能超过20个字符")
    private String priority = "NORMAL";

    /**
     * 医嘱开具时间
     */
    @NotNull(message = "医嘱开具时间不能为空")
    private LocalDateTime prescribedAt;

    /**
     * 医嘱执行时间
     */
    private LocalDateTime executedAt;

    /**
     * 给药途径
     */
    @Size(max = 100, message = "给药途径长度不能超过100个字符")
    private String route;

    /**
     * 备注
     */
    @Size(max = 500, message = "备注长度不能超过500个字符")
    private String notes;

    /**
     * 价格
     */
    private java.math.BigDecimal price;

    /**
     * 数量
     */
    private Integer quantity;

    /**
     * 单位
     */
    @Size(max = 50, message = "单位长度不能超过50个字符")
    private String unit;

    /**
     * 执行备注
     */
    @Size(max = 500, message = "执行备注长度不能超过500个字符")
    private String executionNotes;

    /**
     * 暂缓原因
     */
    @Size(max = 500, message = "暂缓原因长度不能超过500个字符")
    private String postponeReason;

    /**
     * 暂缓执行人ID
     */
    private Long postponedBy;

    /**
     * 暂缓执行时间
     */
    private LocalDateTime postponedAt;

    /**
     * 取消原因
     */
    @Size(max = 500, message = "取消原因长度不能超过500个字符")
    private String cancelReason;

    /**
     * 取消人ID
     */
    private Long cancelledBy;

    /**
     * 取消时间
     */
    private LocalDateTime cancelledAt;

    // 构造函数
    public MedicalOrder() {}

    public MedicalOrder(Long patientId, Long prescribedBy, String orderType, String content) {
        this.patientId = patientId;
        this.prescribedBy = prescribedBy;
        this.orderType = orderType;
        this.content = content;
        this.prescribedAt = LocalDateTime.now();
    }

    // Getter和Setter方法
    public Long getPatientId() {
        return patientId;
    }

    public void setPatientId(Long patientId) {
        this.patientId = patientId;
    }

    public Long getPrescribedBy() {
        return prescribedBy;
    }

    public void setPrescribedBy(Long prescribedBy) {
        this.prescribedBy = prescribedBy;
    }

    public Long getExecutedBy() {
        return executedBy;
    }

    public void setExecutedBy(Long executedBy) {
        this.executedBy = executedBy;
    }

    public String getOrderType() {
        return orderType;
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDosage() {
        return dosage;
    }

    public void setDosage(String dosage) {
        this.dosage = dosage;
    }

    public String getFrequency() {
        return frequency;
    }

    public void setFrequency(String frequency) {
        this.frequency = frequency;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public LocalDateTime getPrescribedAt() {
        return prescribedAt;
    }

    public void setPrescribedAt(LocalDateTime prescribedAt) {
        this.prescribedAt = prescribedAt;
    }

    public LocalDateTime getExecutedAt() {
        return executedAt;
    }

    public void setExecutedAt(LocalDateTime executedAt) {
        this.executedAt = executedAt;
    }

    public String getExecutionNotes() {
        return executionNotes;
    }

    public void setExecutionNotes(String executionNotes) {
        this.executionNotes = executionNotes;
    }

    public String getRoute() {
        return route;
    }

    public void setRoute(String route) {
        this.route = route;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public java.math.BigDecimal getPrice() {
        return price;
    }

    public void setPrice(java.math.BigDecimal price) {
        this.price = price;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public Long getPostponedBy() {
        return postponedBy;
    }

    public void setPostponedBy(Long postponedBy) {
        this.postponedBy = postponedBy;
    }

    public LocalDateTime getPostponedAt() {
        return postponedAt;
    }

    public void setPostponedAt(LocalDateTime postponedAt) {
        this.postponedAt = postponedAt;
    }

    public String getCancelReason() {
        return cancelReason;
    }

    public void setCancelReason(String cancelReason) {
        this.cancelReason = cancelReason;
    }

    public Long getCancelledBy() {
        return cancelledBy;
    }

    public void setCancelledBy(Long cancelledBy) {
        this.cancelledBy = cancelledBy;
    }

    public LocalDateTime getCancelledAt() {
        return cancelledAt;
    }

    public void setCancelledAt(LocalDateTime cancelledAt) {
        this.cancelledAt = cancelledAt;
    }

    public String getPostponeReason() {
        return postponeReason;
    }

    public void setPostponeReason(String postponeReason) {
        this.postponeReason = postponeReason;
    }

    /**
     * 医嘱状态枚举
     */
    public enum Status {
        PENDING("待执行"),
        EXECUTED("已执行"),
        POSTPONED("暂缓执行"),
        CANCELLED("已取消");

        private final String description;

        Status(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    /**
     * 医嘱优先级枚举
     */
    public enum Priority {
        URGENT("紧急"),
        NORMAL("普通"),
        LOW("低");

        private final String description;

        Priority(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    /**
     * 医嘱类型枚举
     */
    public enum OrderType {
        INJECTION("注射"),
        ORAL_MEDICATION("口服药物"),
        EXAMINATION("检查"),
        NURSING_CARE("护理"),
        TREATMENT("治疗"),
        OBSERVATION("观察");

        private final String description;

        OrderType(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }
}