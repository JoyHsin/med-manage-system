package org.me.joy.clinic.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 挂号实体类
 * 管理患者的挂号信息
 */
@TableName("registrations")
public class Registration extends BaseEntity {

    /**
     * 患者ID
     */
    @NotNull(message = "患者ID不能为空")
    private Long patientId;

    /**
     * 预约ID（可选，如果是从预约转来的挂号）
     */
    private Long appointmentId;

    /**
     * 挂号编号（唯一标识）
     */
    @NotBlank(message = "挂号编号不能为空")
    @Size(max = 50, message = "挂号编号长度不能超过50个字符")
    private String registrationNumber;

    /**
     * 挂号日期
     */
    @NotNull(message = "挂号日期不能为空")
    private LocalDate registrationDate;

    /**
     * 挂号时间
     */
    @NotNull(message = "挂号时间不能为空")
    private LocalDateTime registrationTime;

    /**
     * 科室
     */
    @NotBlank(message = "科室不能为空")
    @Size(max = 100, message = "科室名称长度不能超过100个字符")
    private String department;

    /**
     * 医生ID
     */
    private Long doctorId;

    /**
     * 医生姓名
     */
    @Size(max = 100, message = "医生姓名长度不能超过100个字符")
    private String doctorName;

    /**
     * 挂号类型
     */
    @NotBlank(message = "挂号类型不能为空")
    @Pattern(regexp = "^(普通门诊|专家门诊|急诊|专科门诊|体检|疫苗接种|其他)$", 
             message = "挂号类型只能是普通门诊、专家门诊、急诊、专科门诊、体检、疫苗接种或其他")
    private String registrationType;

    /**
     * 挂号状态
     */
    @NotBlank(message = "挂号状态不能为空")
    @Pattern(regexp = "^(已挂号|已叫号|已到达|就诊中|已完成|已取消|未到)$", 
             message = "挂号状态只能是已挂号、已叫号、已到达、就诊中、已完成、已取消或未到")
    private String status = "已挂号";

    /**
     * 队列号
     */
    @Min(value = 1, message = "队列号必须大于0")
    private Integer queueNumber;

    /**
     * 优先级
     */
    @Min(value = 1, message = "优先级最小值为1")
    @Max(value = 5, message = "优先级最大值为5")
    private Integer priority = 3;

    /**
     * 挂号费
     */
    @NotNull(message = "挂号费不能为空")
    @DecimalMin(value = "0.0", message = "挂号费不能为负数")
    @Digits(integer = 8, fraction = 2, message = "挂号费格式不正确")
    private BigDecimal registrationFee;

    /**
     * 诊疗费
     */
    @DecimalMin(value = "0.0", message = "诊疗费不能为负数")
    @Digits(integer = 8, fraction = 2, message = "诊疗费格式不正确")
    private BigDecimal consultationFee;

    /**
     * 总费用
     */
    @DecimalMin(value = "0.0", message = "总费用不能为负数")
    @Digits(integer = 8, fraction = 2, message = "总费用格式不正确")
    private BigDecimal totalFee;

    /**
     * 支付状态
     */
    @Pattern(regexp = "^(未支付|已支付|部分支付|已退费)$", 
             message = "支付状态只能是未支付、已支付、部分支付或已退费")
    private String paymentStatus = "未支付";

    /**
     * 支付方式
     */
    @Pattern(regexp = "^(现金|银行卡|支付宝|微信|医保|其他)$", 
             message = "支付方式只能是现金、银行卡、支付宝、微信、医保或其他")
    private String paymentMethod;

    /**
     * 主诉
     */
    @Size(max = 500, message = "主诉长度不能超过500个字符")
    private String chiefComplaint;

    /**
     * 现病史
     */
    @Size(max = 1000, message = "现病史长度不能超过1000个字符")
    private String presentIllness;

    /**
     * 挂号来源
     */
    @Pattern(regexp = "^(现场|预约|急诊|转诊|其他)$", 
             message = "挂号来源只能是现场、预约、急诊、转诊或其他")
    private String source = "现场";

    /**
     * 是否初诊
     */
    private Boolean isFirstVisit = true;

    /**
     * 是否急诊
     */
    private Boolean isEmergency = false;

    /**
     * 叫号时间
     */
    private LocalDateTime calledAt;

    /**
     * 到达时间
     */
    private LocalDateTime arrivedAt;

    /**
     * 开始就诊时间
     */
    private LocalDateTime startedAt;

    /**
     * 完成时间
     */
    private LocalDateTime completedAt;

    /**
     * 取消时间
     */
    private LocalDateTime cancelledAt;

    /**
     * 取消原因
     */
    @Size(max = 500, message = "取消原因长度不能超过500个字符")
    private String cancelReason;

    /**
     * 备注信息
     */
    @Size(max = 1000, message = "备注信息长度不能超过1000个字符")
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
    public Registration() {}

    public Registration(Long patientId, String registrationNumber, LocalDate registrationDate, 
                       String department, String registrationType) {
        this.patientId = patientId;
        this.registrationNumber = registrationNumber;
        this.registrationDate = registrationDate;
        this.registrationTime = LocalDateTime.now();
        this.department = department;
        this.registrationType = registrationType;
    }

    // Getters and Setters
    public Long getPatientId() {
        return patientId;
    }

    public void setPatientId(Long patientId) {
        this.patientId = patientId;
    }

    public Long getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentId(Long appointmentId) {
        this.appointmentId = appointmentId;
    }

    public String getRegistrationNumber() {
        return registrationNumber;
    }

    public void setRegistrationNumber(String registrationNumber) {
        this.registrationNumber = registrationNumber;
    }

    public LocalDate getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(LocalDate registrationDate) {
        this.registrationDate = registrationDate;
    }

    public LocalDateTime getRegistrationTime() {
        return registrationTime;
    }

    public void setRegistrationTime(LocalDateTime registrationTime) {
        this.registrationTime = registrationTime;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public Long getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(Long doctorId) {
        this.doctorId = doctorId;
    }

    public String getDoctorName() {
        return doctorName;
    }

    public void setDoctorName(String doctorName) {
        this.doctorName = doctorName;
    }

    public String getRegistrationType() {
        return registrationType;
    }

    public void setRegistrationType(String registrationType) {
        this.registrationType = registrationType;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getQueueNumber() {
        return queueNumber;
    }

    public void setQueueNumber(Integer queueNumber) {
        this.queueNumber = queueNumber;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public BigDecimal getRegistrationFee() {
        return registrationFee;
    }

    public void setRegistrationFee(BigDecimal registrationFee) {
        this.registrationFee = registrationFee;
    }

    public BigDecimal getConsultationFee() {
        return consultationFee;
    }

    public void setConsultationFee(BigDecimal consultationFee) {
        this.consultationFee = consultationFee;
    }

    public BigDecimal getTotalFee() {
        return totalFee;
    }

    public void setTotalFee(BigDecimal totalFee) {
        this.totalFee = totalFee;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getChiefComplaint() {
        return chiefComplaint;
    }

    public void setChiefComplaint(String chiefComplaint) {
        this.chiefComplaint = chiefComplaint;
    }

    public String getPresentIllness() {
        return presentIllness;
    }

    public void setPresentIllness(String presentIllness) {
        this.presentIllness = presentIllness;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public Boolean getIsFirstVisit() {
        return isFirstVisit;
    }

    public void setIsFirstVisit(Boolean isFirstVisit) {
        this.isFirstVisit = isFirstVisit;
    }

    public Boolean getIsEmergency() {
        return isEmergency;
    }

    public void setIsEmergency(Boolean isEmergency) {
        this.isEmergency = isEmergency;
    }

    public LocalDateTime getCalledAt() {
        return calledAt;
    }

    public void setCalledAt(LocalDateTime calledAt) {
        this.calledAt = calledAt;
    }

    public LocalDateTime getArrivedAt() {
        return arrivedAt;
    }

    public void setArrivedAt(LocalDateTime arrivedAt) {
        this.arrivedAt = arrivedAt;
    }

    public LocalDateTime getStartedAt() {
        return startedAt;
    }

    public void setStartedAt(LocalDateTime startedAt) {
        this.startedAt = startedAt;
    }

    public LocalDateTime getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(LocalDateTime completedAt) {
        this.completedAt = completedAt;
    }

    public LocalDateTime getCancelledAt() {
        return cancelledAt;
    }

    public void setCancelledAt(LocalDateTime cancelledAt) {
        this.cancelledAt = cancelledAt;
    }

    public String getCancelReason() {
        return cancelReason;
    }

    public void setCancelReason(String cancelReason) {
        this.cancelReason = cancelReason;
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
     * 计算总费用
     */
    public void calculateTotalFee() {
        BigDecimal regFee = registrationFee != null ? registrationFee : BigDecimal.ZERO;
        BigDecimal conFee = consultationFee != null ? consultationFee : BigDecimal.ZERO;
        this.totalFee = regFee.add(conFee);
    }

    /**
     * 检查是否可以取消
     */
    public boolean canBeCancelled() {
        return "已挂号".equals(status) || "已叫号".equals(status);
    }

    /**
     * 检查是否已支付
     */
    public boolean isPaid() {
        return "已支付".equals(paymentStatus);
    }

    /**
     * 检查是否为当日挂号
     */
    public boolean isTodayRegistration() {
        return registrationDate != null && registrationDate.equals(LocalDate.now());
    }

    /**
     * 获取等待时间（分钟）
     */
    public Long getWaitingTimeMinutes() {
        if (calledAt == null) {
            return null;
        }
        LocalDateTime startTime = arrivedAt != null ? arrivedAt : registrationTime;
        return java.time.Duration.between(startTime, calledAt).toMinutes();
    }

    /**
     * 获取就诊时长（分钟）
     */
    public Long getConsultationDurationMinutes() {
        if (startedAt == null || completedAt == null) {
            return null;
        }
        return java.time.Duration.between(startedAt, completedAt).toMinutes();
    }

    /**
     * 叫号
     */
    public void call() {
        if ("已挂号".equals(status)) {
            this.status = "已叫号";
            this.calledAt = LocalDateTime.now();
        }
    }

    /**
     * 标记患者到达
     */
    public void markArrived() {
        if ("已叫号".equals(status)) {
            this.status = "已到达";
            this.arrivedAt = LocalDateTime.now();
        }
    }

    /**
     * 开始就诊
     */
    public void startConsultation() {
        if ("已到达".equals(status)) {
            this.status = "就诊中";
            this.startedAt = LocalDateTime.now();
        }
    }

    /**
     * 完成就诊
     */
    public void complete() {
        if ("就诊中".equals(status)) {
            this.status = "已完成";
            this.completedAt = LocalDateTime.now();
        }
    }

    /**
     * 取消挂号
     */
    public void cancel(String reason) {
        if (canBeCancelled()) {
            this.status = "已取消";
            this.cancelledAt = LocalDateTime.now();
            this.cancelReason = reason;
        }
    }

    /**
     * 标记未到
     */
    public void markNoShow() {
        if ("已叫号".equals(status)) {
            this.status = "未到";
        }
    }

    /**
     * 标记已支付
     */
    public void markPaid(String method) {
        this.paymentStatus = "已支付";
        this.paymentMethod = method;
    }

    /**
     * 获取状态描述
     */
    public String getStatusDescription() {
        return switch (status) {
            case "已挂号" -> "已挂号，等待叫号";
            case "已叫号" -> "已叫号，等待患者到达";
            case "已到达" -> "患者已到达，等待就诊";
            case "就诊中" -> "正在就诊中";
            case "已完成" -> "就诊已完成";
            case "已取消" -> "挂号已取消";
            case "未到" -> "患者未按时到达";
            default -> status;
        };
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Registration)) return false;
        Registration that = (Registration) o;
        return registrationNumber != null && registrationNumber.equals(that.registrationNumber);
    }

    @Override
    public int hashCode() {
        return registrationNumber != null ? registrationNumber.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "Registration{" +
                "id=" + getId() +
                ", patientId=" + patientId +
                ", registrationNumber='" + registrationNumber + '\'' +
                ", registrationDate=" + registrationDate +
                ", department='" + department + '\'' +
                ", registrationType='" + registrationType + '\'' +
                ", status='" + status + '\'' +
                ", queueNumber=" + queueNumber +
                ", priority=" + priority +
                '}';
    }
}