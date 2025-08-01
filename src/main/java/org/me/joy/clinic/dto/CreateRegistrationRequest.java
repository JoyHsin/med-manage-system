package org.me.joy.clinic.dto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 创建挂号请求DTO
 */
public class CreateRegistrationRequest {

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
     * 挂号日期
     */
    @NotNull(message = "挂号日期不能为空")
    private LocalDate registrationDate;

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
     * 备注信息
     */
    @Size(max = 1000, message = "备注信息长度不能超过1000个字符")
    private String remarks;

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

    public LocalDate getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(LocalDate registrationDate) {
        this.registrationDate = registrationDate;
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

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
}