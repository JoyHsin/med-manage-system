package org.me.joy.clinic.dto;

import jakarta.validation.constraints.*;

/**
 * 更新病历请求DTO
 */
public class UpdateMedicalRecordRequest {

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
     * 最终诊断
     */
    @Size(max = 1000, message = "最终诊断长度不能超过1000个字符")
    private String finalDiagnosis;

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
     * 是否传染病
     */
    private Boolean isInfectious;

    /**
     * 是否慢性病
     */
    private Boolean isChronicDisease;

    /**
     * 备注信息
     */
    @Size(max = 1000, message = "备注信息长度不能超过1000个字符")
    private String remarks;

    // 构造函数
    public UpdateMedicalRecordRequest() {}

    // Getters and Setters
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

    public String getFinalDiagnosis() {
        return finalDiagnosis;
    }

    public void setFinalDiagnosis(String finalDiagnosis) {
        this.finalDiagnosis = finalDiagnosis;
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

    @Override
    public String toString() {
        return "UpdateMedicalRecordRequest{" +
                "chiefComplaint='" + chiefComplaint + '\'' +
                ", finalDiagnosis='" + finalDiagnosis + '\'' +
                ", treatmentPlan='" + treatmentPlan + '\'' +
                ", department='" + department + '\'' +
                ", isInfectious=" + isInfectious +
                ", isChronicDisease=" + isChronicDisease +
                '}';
    }
}