package org.me.joy.clinic.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import jakarta.validation.constraints.*;

import java.time.LocalDateTime;

/**
 * 诊断实体类
 * 存储病历的诊断信息
 */
@TableName("diagnoses")
public class Diagnosis extends BaseEntity {

    /**
     * 病历ID
     */
    @NotNull(message = "病历ID不能为空")
    private Long medicalRecordId;

    /**
     * 诊断编码（ICD-10编码）
     */
    @Size(max = 20, message = "诊断编码长度不能超过20个字符")
    private String diagnosisCode;

    /**
     * 诊断名称
     */
    @NotBlank(message = "诊断名称不能为空")
    @Size(max = 200, message = "诊断名称长度不能超过200个字符")
    private String diagnosisName;

    /**
     * 诊断类型
     */
    @Pattern(regexp = "^(主要诊断|次要诊断|疑似诊断|排除诊断|并发症|合并症)$", 
             message = "诊断类型只能是主要诊断、次要诊断、疑似诊断、排除诊断、并发症或合并症")
    private String diagnosisType = "主要诊断";

    /**
     * 诊断描述
     */
    @Size(max = 1000, message = "诊断描述长度不能超过1000个字符")
    private String description;

    /**
     * 严重程度
     */
    @Pattern(regexp = "^(轻度|中度|重度|危重)$", 
             message = "严重程度只能是轻度、中度、重度或危重")
    private String severity;

    /**
     * 诊断状态
     */
    @Pattern(regexp = "^(初步诊断|确定诊断|修正诊断|排除诊断)$", 
             message = "诊断状态只能是初步诊断、确定诊断、修正诊断或排除诊断")
    private String status = "初步诊断";

    /**
     * 诊断依据
     */
    @Size(max = 1000, message = "诊断依据长度不能超过1000个字符")
    private String evidence;

    /**
     * 诊断医生ID
     */
    @NotNull(message = "诊断医生ID不能为空")
    private Long doctorId;

    /**
     * 诊断时间
     */
    @NotNull(message = "诊断时间不能为空")
    private LocalDateTime diagnosisTime;

    /**
     * 是否主诊断
     */
    private Boolean isPrimary = false;

    /**
     * 是否慢性病
     */
    private Boolean isChronicDisease = false;

    /**
     * 是否传染病
     */
    private Boolean isInfectious = false;

    /**
     * 是否遗传病
     */
    private Boolean isHereditary = false;

    /**
     * 预后评估
     */
    @Size(max = 500, message = "预后评估长度不能超过500个字符")
    private String prognosis;

    /**
     * 治疗建议
     */
    @Size(max = 1000, message = "治疗建议长度不能超过1000个字符")
    private String treatmentAdvice;

    /**
     * 随访要求
     */
    @Size(max = 500, message = "随访要求长度不能超过500个字符")
    private String followUpRequirement;

    /**
     * 备注信息
     */
    @Size(max = 500, message = "备注信息长度不能超过500个字符")
    private String remarks;

    /**
     * 排序序号
     */
    private Integer sortOrder = 1;

    // 构造函数
    public Diagnosis() {}

    public Diagnosis(Long medicalRecordId, String diagnosisName, Long doctorId) {
        this.medicalRecordId = medicalRecordId;
        this.diagnosisName = diagnosisName;
        this.doctorId = doctorId;
        this.diagnosisTime = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getMedicalRecordId() {
        return medicalRecordId;
    }

    public void setMedicalRecordId(Long medicalRecordId) {
        this.medicalRecordId = medicalRecordId;
    }

    public String getDiagnosisCode() {
        return diagnosisCode;
    }

    public void setDiagnosisCode(String diagnosisCode) {
        this.diagnosisCode = diagnosisCode;
    }

    public String getDiagnosisName() {
        return diagnosisName;
    }

    public void setDiagnosisName(String diagnosisName) {
        this.diagnosisName = diagnosisName;
    }

    public String getDiagnosisType() {
        return diagnosisType;
    }

    public void setDiagnosisType(String diagnosisType) {
        this.diagnosisType = diagnosisType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSeverity() {
        return severity;
    }

    public void setSeverity(String severity) {
        this.severity = severity;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getEvidence() {
        return evidence;
    }

    public void setEvidence(String evidence) {
        this.evidence = evidence;
    }

    public Long getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(Long doctorId) {
        this.doctorId = doctorId;
    }

    public LocalDateTime getDiagnosisTime() {
        return diagnosisTime;
    }

    public void setDiagnosisTime(LocalDateTime diagnosisTime) {
        this.diagnosisTime = diagnosisTime;
    }

    public Boolean getIsPrimary() {
        return isPrimary;
    }

    public void setIsPrimary(Boolean isPrimary) {
        this.isPrimary = isPrimary;
    }

    public Boolean getIsChronicDisease() {
        return isChronicDisease;
    }

    public void setIsChronicDisease(Boolean isChronicDisease) {
        this.isChronicDisease = isChronicDisease;
    }

    public Boolean getIsInfectious() {
        return isInfectious;
    }

    public void setIsInfectious(Boolean isInfectious) {
        this.isInfectious = isInfectious;
    }

    public Boolean getIsHereditary() {
        return isHereditary;
    }

    public void setIsHereditary(Boolean isHereditary) {
        this.isHereditary = isHereditary;
    }

    public String getPrognosis() {
        return prognosis;
    }

    public void setPrognosis(String prognosis) {
        this.prognosis = prognosis;
    }

    public String getTreatmentAdvice() {
        return treatmentAdvice;
    }

    public void setTreatmentAdvice(String treatmentAdvice) {
        this.treatmentAdvice = treatmentAdvice;
    }

    public String getFollowUpRequirement() {
        return followUpRequirement;
    }

    public void setFollowUpRequirement(String followUpRequirement) {
        this.followUpRequirement = followUpRequirement;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public Integer getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }

    /**
     * 确认诊断
     */
    public void confirm() {
        this.status = "确定诊断";
    }

    /**
     * 修正诊断
     */
    public void revise(String newDiagnosisName, String reason) {
        this.diagnosisName = newDiagnosisName;
        this.status = "修正诊断";
        this.remarks = (this.remarks != null ? this.remarks + "；" : "") + "修正原因：" + reason;
    }

    /**
     * 排除诊断
     */
    public void exclude(String reason) {
        this.status = "排除诊断";
        this.remarks = (this.remarks != null ? this.remarks + "；" : "") + "排除原因：" + reason;
    }

    /**
     * 检查是否为严重诊断
     */
    public boolean isSevere() {
        return "重度".equals(severity) || "危重".equals(severity);
    }

    /**
     * 检查是否需要特殊关注
     */
    public boolean needsSpecialAttention() {
        return Boolean.TRUE.equals(isInfectious) || 
               Boolean.TRUE.equals(isChronicDisease) || 
               isSevere();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Diagnosis)) return false;
        Diagnosis diagnosis = (Diagnosis) o;
        return getId() != null && getId().equals(diagnosis.getId());
    }

    @Override
    public int hashCode() {
        return getId() != null ? getId().hashCode() : 0;
    }

    @Override
    public String toString() {
        return "Diagnosis{" +
                "id=" + getId() +
                ", medicalRecordId=" + medicalRecordId +
                ", diagnosisCode='" + diagnosisCode + '\'' +
                ", diagnosisName='" + diagnosisName + '\'' +
                ", diagnosisType='" + diagnosisType + '\'' +
                ", status='" + status + '\'' +
                ", severity='" + severity + '\'' +
                ", isPrimary=" + isPrimary +
                ", diagnosisTime=" + diagnosisTime +
                '}';
    }
}