package org.me.joy.clinic.dto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 创建预约请求DTO
 */
public class CreateAppointmentRequest {

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
    private BigDecimal appointmentFee;

    /**
     * 是否需要提醒
     */
    private Boolean needReminder = true;

    /**
     * 提醒时间（预约前多少分钟提醒）
     */
    @Min(value = 0, message = "提醒时间不能为负数")
    private Integer reminderMinutes = 30;

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

    public BigDecimal getAppointmentFee() {
        return appointmentFee;
    }

    public void setAppointmentFee(BigDecimal appointmentFee) {
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
}