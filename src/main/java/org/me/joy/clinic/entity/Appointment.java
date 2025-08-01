package org.me.joy.clinic.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import jakarta.validation.constraints.*;

import java.time.LocalDateTime;

/**
 * 预约实体类
 * 管理患者的预约信息
 */
@TableName("appointments")
public class Appointment extends BaseEntity {

    /**
     * 患者ID
     */
    @NotNull(message = "患者ID不能为空")
    private Long patientId;

    /**
     * 医生ID
     */
    @NotNull(message = "医生ID不能为空")
    private Long doctorId;

    /**
     * 预约时间
     */
    @NotNull(message = "预约时间不能为空")
    @Future(message = "预约时间必须是未来时间")
    private LocalDateTime appointmentTime;

    /**
     * 预约类型
     */
    @NotBlank(message = "预约类型不能为空")
    @Pattern(regexp = "^(初诊|复诊|专家门诊|急诊|体检|疫苗接种|其他)$", 
             message = "预约类型只能是初诊、复诊、专家门诊、急诊、体检、疫苗接种或其他")
    private String appointmentType;

    /**
     * 预约状态
     */
    @NotBlank(message = "预约状态不能为空")
    @Pattern(regexp = "^(已预约|已确认|已到达|进行中|已完成|已取消|未到)$", 
             message = "预约状态只能是已预约、已确认、已到达、进行中、已完成、已取消或未到")
    private String status = "已预约";

    /**
     * 科室
     */
    @Size(max = 100, message = "科室名称长度不能超过100个字符")
    private String department;

    /**
     * 主诉
     */
    @Size(max = 500, message = "主诉长度不能超过500个字符")
    private String chiefComplaint;

    /**
     * 预约备注
     */
    @Size(max = 1000, message = "预约备注长度不能超过1000个字符")
    private String notes;

    /**
     * 预约来源
     */
    @Pattern(regexp = "^(现场|电话|网络|微信|其他)$", 
             message = "预约来源只能是现场、电话、网络、微信或其他")
    private String source = "现场";

    /**
     * 优先级
     */
    @Min(value = 1, message = "优先级最小值为1")
    @Max(value = 5, message = "优先级最大值为5")
    private Integer priority = 3;

    /**
     * 预约费用
     */
    @DecimalMin(value = "0.0", message = "预约费用不能为负数")
    @Digits(integer = 8, fraction = 2, message = "预约费用格式不正确")
    private java.math.BigDecimal appointmentFee;

    /**
     * 是否需要提醒
     */
    private Boolean needReminder = true;

    /**
     * 提醒时间（预约前多少分钟提醒）
     */
    @Min(value = 0, message = "提醒时间不能为负数")
    private Integer reminderMinutes = 30;

    /**
     * 预约确认时间
     */
    private LocalDateTime confirmedAt;

    /**
     * 到达时间
     */
    private LocalDateTime arrivedAt;

    /**
     * 开始时间
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
     * 创建人员ID
     */
    private Long createdBy;

    /**
     * 最后更新人员ID
     */
    private Long lastUpdatedBy;

    // 构造函数
    public Appointment() {}

    public Appointment(Long patientId, Long doctorId, LocalDateTime appointmentTime, String appointmentType) {
        this.patientId = patientId;
        this.doctorId = doctorId;
        this.appointmentTime = appointmentTime;
        this.appointmentType = appointmentType;
    }

    // Getters and Setters
    public Long getPatientId() {
        return patientId;
    }

    public void setPatientId(Long patientId) {
        this.patientId = patientId;
    }

    public Long getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(Long doctorId) {
        this.doctorId = doctorId;
    }

    public LocalDateTime getAppointmentTime() {
        return appointmentTime;
    }

    public void setAppointmentTime(LocalDateTime appointmentTime) {
        this.appointmentTime = appointmentTime;
    }

    public String getAppointmentType() {
        return appointmentType;
    }

    public void setAppointmentType(String appointmentType) {
        this.appointmentType = appointmentType;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getChiefComplaint() {
        return chiefComplaint;
    }

    public void setChiefComplaint(String chiefComplaint) {
        this.chiefComplaint = chiefComplaint;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public java.math.BigDecimal getAppointmentFee() {
        return appointmentFee;
    }

    public void setAppointmentFee(java.math.BigDecimal appointmentFee) {
        this.appointmentFee = appointmentFee;
    }

    public Boolean getNeedReminder() {
        return needReminder;
    }

    public void setNeedReminder(Boolean needReminder) {
        this.needReminder = needReminder;
    }

    public Integer getReminderMinutes() {
        return reminderMinutes;
    }

    public void setReminderMinutes(Integer reminderMinutes) {
        this.reminderMinutes = reminderMinutes;
    }

    public LocalDateTime getConfirmedAt() {
        return confirmedAt;
    }

    public void setConfirmedAt(LocalDateTime confirmedAt) {
        this.confirmedAt = confirmedAt;
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
     * 检查预约是否可以取消
     */
    public boolean canBeCancelled() {
        return "已预约".equals(status) || "已确认".equals(status);
    }

    /**
     * 检查预约是否已过期
     */
    public boolean isExpired() {
        return appointmentTime != null && appointmentTime.isBefore(LocalDateTime.now()) 
               && !"已完成".equals(status) && !"已取消".equals(status);
    }

    /**
     * 检查是否需要提醒
     */
    public boolean shouldRemind() {
        if (!Boolean.TRUE.equals(needReminder) || appointmentTime == null || reminderMinutes == null) {
            return false;
        }
        LocalDateTime reminderTime = appointmentTime.minusMinutes(reminderMinutes);
        LocalDateTime now = LocalDateTime.now();
        return now.isAfter(reminderTime) && now.isBefore(appointmentTime);
    }

    /**
     * 获取预约状态的中文描述
     */
    public String getStatusDescription() {
        return switch (status) {
            case "已预约" -> "患者已预约，等待确认";
            case "已确认" -> "预约已确认，等待患者到达";
            case "已到达" -> "患者已到达，等待就诊";
            case "进行中" -> "正在就诊中";
            case "已完成" -> "就诊已完成";
            case "已取消" -> "预约已取消";
            case "未到" -> "患者未按时到达";
            default -> status;
        };
    }

    /**
     * 确认预约
     */
    public void confirm() {
        if ("已预约".equals(status)) {
            this.status = "已确认";
            this.confirmedAt = LocalDateTime.now();
        }
    }

    /**
     * 标记患者到达
     */
    public void markArrived() {
        if ("已确认".equals(status)) {
            this.status = "已到达";
            this.arrivedAt = LocalDateTime.now();
        }
    }

    /**
     * 开始就诊
     */
    public void start() {
        if ("已到达".equals(status)) {
            this.status = "进行中";
            this.startedAt = LocalDateTime.now();
        }
    }

    /**
     * 完成就诊
     */
    public void complete() {
        if ("进行中".equals(status)) {
            this.status = "已完成";
            this.completedAt = LocalDateTime.now();
        }
    }

    /**
     * 取消预约
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
        if ("已确认".equals(status) && isExpired()) {
            this.status = "未到";
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Appointment)) return false;
        Appointment that = (Appointment) o;
        return getId() != null && getId().equals(that.getId());
    }

    @Override
    public int hashCode() {
        return getId() != null ? getId().hashCode() : 0;
    }

    @Override
    public String toString() {
        return "Appointment{" +
                "id=" + getId() +
                ", patientId=" + patientId +
                ", doctorId=" + doctorId +
                ", appointmentTime=" + appointmentTime +
                ", appointmentType='" + appointmentType + '\'' +
                ", status='" + status + '\'' +
                ", department='" + department + '\'' +
                ", priority=" + priority +
                '}';
    }
}