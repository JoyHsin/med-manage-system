package org.me.joy.clinic.dto;

import jakarta.validation.constraints.*;

import java.time.LocalDateTime;

/**
 * 创建病历请求DTO
 */
public class CreateMedicalRecordRequest {

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
     * 挂号ID
     */
    private Long registrationId;

    /**
     * 主诉
     */
    @Size(max = 1000, message = "主诉长度不能超过1000个字符")
    private String chiefComplaint;

    /**
     * 现病史
     */
    @Size(max = 2000, message = "现病史长度不能超过2000个字符")
    private String presentIllness;

    /**
     * 既往史
     */
    @Size(max = 2000, message = "既往史长度不能超过2000个字符")
    private String pastHistory;

    /**
     * 个人史
     */
    @Size(max = 1000, message = "个人史长度不能超过1000个字符")
    private String personalHistory;

    /**
     * 家族史
     */
    @Size(max = 1000, message = "家族史长度不能超过1000个字符")
    private String familyHistory;

    /**
     * 体格检查
     */
    @Size(max = 2000, message = "体格检查长度不能超过2000个字符")
    private String physicalExamination;

    /**
     * 辅助检查
     */
    @Size(max = 2000, message = "辅助检查长度不能超过2000个字符")
    private String auxiliaryExamination;

    /**
     * 初步诊断
     */
    @Size(max = 1000, message = "初步诊断长度不能超过1000个字符")
    private String preliminaryDiagnosis;

    /**
     * 治疗方案
     */
    @Size(max = 2000, message = "治疗方案长度不能超过2000个字符")
    private String treatmentPlan;

    /**
     * 医嘱
     */
    @Size(max = 2000, message = "医嘱长度不能超过2000个字符")
    private String medicalOrders;

    /**
     * 病情评估
     */
    @Size(max = 1000, message = "病情评估长度不能超过1000个字符")
    private String conditionAssessment;

    /**
     * 预后
     */
    @Size(max = 500, message = "预后长度不能超过500个字符")
    private String prognosis;

    /**
     * 随访建议
     */
    @Size(max = 1000, message = "随访建议长度不能超过1000个字符")
    private String followUpAdvice;

    /**
     * 科室
     */
    @Size(max = 100, message = "科室名称长度不能超过100个字符")
    private String department;

    /**
     * 病历类型
     */
    @Pattern(regexp = "^(门诊病历|急诊病历|住院病历|体检报告|其他)$", 
             message = "病历类型只能是门诊病历、急诊病历、住院病历、体检报告或其他")
    private String recordType = "门诊病历";

    /**
     * 是否传染病
     */
    private Boolean isInfectious = false;

    /**
     * 是否慢性病
     */
    private Boolean isChronicDisease = false;

    /**
     * 备注信息
     */
    @Size(max = 1000, message = "备注信息长度不能超过1000个字符")
    private String remarks;

    /**
     * 记录日期
     */
    private LocalDateTime recordDate;

    // 构造函数
    public CreateMedicalRecordRequest() {}

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

    public Long getRegistrationId() {
        return registrationId;
    }

    public void setRegistrationId(Long registrationId) {
        this.registrationId = registrationId;
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

    public String getPastHistory() {
        return pastHistory;
    }

    public void setPastHistory(String pastHistory) {
        this.pastHistory = pastHistory;
    }

    public String getPersonalHistory() {
        return personalHistory;
    }

    public void setPersonalHistory(String personalHistory) {
        this.personalHistory = personalHistory;
    }

    public String getFamilyHistory() {
        return familyHistory;
    }

    public void setFamilyHistory(String familyHistory) {
        this.familyHistory = familyHistory;
    }

    public String getPhysicalExamination() {
        return physicalExamination;
    }

    public void setPhysicalExamination(String physicalExamination) {
        this.physicalExamination = physicalExamination;
    }

    public String getAuxiliaryExamination() {
        return auxiliaryExamination;
    }

    public void setAuxiliaryExamination(String auxiliaryExamination) {
        this.auxiliaryExamination = auxiliaryExamination;
    }

    public String getPreliminaryDiagnosis() {
        return preliminaryDiagnosis;
    }

    public void setPreliminaryDiagnosis(String preliminaryDiagnosis) {
        this.preliminaryDiagnosis = preliminaryDiagnosis;
    }

    public String getTreatmentPlan() {
        return treatmentPlan;
    }

    public void setTreatmentPlan(String treatmentPlan) {
        this.treatmentPlan = treatmentPlan;
    }

    public String getMedicalOrders() {
        return medicalOrders;
    }

    public void setMedicalOrders(String medicalOrders) {
        this.medicalOrders = medicalOrders;
    }

    public String getConditionAssessment() {
        return conditionAssessment;
    }

    public void setConditionAssessment(String conditionAssessment) {
        this.conditionAssessment = conditionAssessment;
    }

    public String getPrognosis() {
        return prognosis;
    }

    public void setPrognosis(String prognosis) {
        this.prognosis = prognosis;
    }

    public String getFollowUpAdvice() {
        return followUpAdvice;
    }

    public void setFollowUpAdvice(String followUpAdvice) {
        this.followUpAdvice = followUpAdvice;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getRecordType() {
        return recordType;
    }

    public void setRecordType(String recordType) {
        this.recordType = recordType;
    }

    public Boolean getIsInfectious() {
        return isInfectious;
    }

    public void setIsInfectious(Boolean isInfectious) {
        this.isInfectious = isInfectious;
    }

    public Boolean getIsChronicDisease() {
        return isChronicDisease;
    }

    public void setIsChronicDisease(Boolean isChronicDisease) {
        this.isChronicDisease = isChronicDisease;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public LocalDateTime getRecordDate() {
        return recordDate;
    }

    public void setRecordDate(LocalDateTime recordDate) {
        this.recordDate = recordDate;
    }

    @Override
    public String toString() {
        return "CreateMedicalRecordRequest{" +
                "patientId=" + patientId +
                ", doctorId=" + doctorId +
                ", registrationId=" + registrationId +
                ", chiefComplaint='" + chiefComplaint + '\'' +
                ", department='" + department + '\'' +
                ", recordType='" + recordType + '\'' +
                ", recordDate=" + recordDate +
                '}';
    }
}