package org.me.joy.clinic.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import jakarta.validation.constraints.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 病史实体类
 * 记录患者的既往病史和家族病史
 */
@TableName("medical_histories")
public class MedicalHistory extends BaseEntity {

    /**
     * 患者ID
     */
    @NotNull(message = "患者ID不能为空")
    private Long patientId;

    /**
     * 病史类型
     */
    @NotBlank(message = "病史类型不能为空")
    @Pattern(regexp = "^(既往病史|家族病史|手术史|外伤史|输血史|药物史)$", 
             message = "病史类型只能是既往病史、家族病史、手术史、外伤史、输血史或药物史")
    private String historyType;

    /**
     * 疾病名称
     */
    @NotBlank(message = "疾病名称不能为空")
    @Size(max = 200, message = "疾病名称长度不能超过200个字符")
    private String diseaseName;

    /**
     * 疾病分类
     */
    @Size(max = 100, message = "疾病分类长度不能超过100个字符")
    private String diseaseCategory;

    /**
     * 发病时间
     */
    private LocalDate onsetDate;

    /**
     * 诊断时间
     */
    private LocalDate diagnosisDate;

    /**
     * 治疗情况
     */
    @Size(max = 1000, message = "治疗情况长度不能超过1000个字符")
    private String treatmentStatus;

    /**
     * 治疗结果
     */
    @Pattern(regexp = "^(治愈|好转|稳定|恶化|死亡|未知)$", 
             message = "治疗结果只能是治愈、好转、稳定、恶化、死亡或未知")
    private String treatmentResult;

    /**
     * 治疗医院
     */
    @Size(max = 200, message = "治疗医院长度不能超过200个字符")
    private String hospital;

    /**
     * 主治医生
     */
    @Size(max = 100, message = "主治医生长度不能超过100个字符")
    private String doctor;

    /**
     * 详细描述
     */
    @Size(max = 2000, message = "详细描述长度不能超过2000个字符")
    private String description;

    /**
     * 相关检查结果
     */
    @Size(max = 1000, message = "相关检查结果长度不能超过1000个字符")
    private String examResults;

    /**
     * 用药情况
     */
    @Size(max = 1000, message = "用药情况长度不能超过1000个字符")
    private String medications;

    /**
     * 家族关系（仅家族病史）
     */
    @Size(max = 50, message = "家族关系长度不能超过50个字符")
    private String familyRelation;

    /**
     * 是否遗传性疾病
     */
    private Boolean isHereditary = false;

    /**
     * 是否慢性疾病
     */
    private Boolean isChronic = false;

    /**
     * 是否传染性疾病
     */
    private Boolean isContagious = false;

    /**
     * 严重程度
     */
    @Pattern(regexp = "^(轻微|中等|严重|危及生命)$", message = "严重程度只能是轻微、中等、严重或危及生命")
    private String severity;

    /**
     * 当前状态
     */
    @Pattern(regexp = "^(活跃|稳定|缓解|治愈|复发)$", message = "当前状态只能是活跃、稳定、缓解、治愈或复发")
    private String currentStatus;

    /**
     * 备注信息
     */
    @Size(max = 500, message = "备注信息长度不能超过500个字符")
    private String remarks;

    /**
     * 记录人员ID
     */
    private Long recordedBy;

    /**
     * 记录时间
     */
    private LocalDateTime recordedTime;

    /**
     * 最后更新人员ID
     */
    private Long lastUpdatedBy;

    // 构造函数
    public MedicalHistory() {}

    public MedicalHistory(Long patientId, String historyType, String diseaseName) {
        this.patientId = patientId;
        this.historyType = historyType;
        this.diseaseName = diseaseName;
        this.recordedTime = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getPatientId() {
        return patientId;
    }

    public void setPatientId(Long patientId) {
        this.patientId = patientId;
    }

    public String getHistoryType() {
        return historyType;
    }

    public void setHistoryType(String historyType) {
        this.historyType = historyType;
    }

    public String getDiseaseName() {
        return diseaseName;
    }

    public void setDiseaseName(String diseaseName) {
        this.diseaseName = diseaseName;
    }

    public String getDiseaseCategory() {
        return diseaseCategory;
    }

    public void setDiseaseCategory(String diseaseCategory) {
        this.diseaseCategory = diseaseCategory;
    }

    public LocalDate getOnsetDate() {
        return onsetDate;
    }

    public void setOnsetDate(LocalDate onsetDate) {
        this.onsetDate = onsetDate;
    }

    public LocalDate getDiagnosisDate() {
        return diagnosisDate;
    }

    public void setDiagnosisDate(LocalDate diagnosisDate) {
        this.diagnosisDate = diagnosisDate;
    }

    public String getTreatmentStatus() {
        return treatmentStatus;
    }

    public void setTreatmentStatus(String treatmentStatus) {
        this.treatmentStatus = treatmentStatus;
    }

    public String getTreatmentResult() {
        return treatmentResult;
    }

    public void setTreatmentResult(String treatmentResult) {
        this.treatmentResult = treatmentResult;
    }

    public String getHospital() {
        return hospital;
    }

    public void setHospital(String hospital) {
        this.hospital = hospital;
    }

    public String getDoctor() {
        return doctor;
    }

    public void setDoctor(String doctor) {
        this.doctor = doctor;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getExamResults() {
        return examResults;
    }

    public void setExamResults(String examResults) {
        this.examResults = examResults;
    }

    public String getMedications() {
        return medications;
    }

    public void setMedications(String medications) {
        this.medications = medications;
    }

    public String getFamilyRelation() {
        return familyRelation;
    }

    public void setFamilyRelation(String familyRelation) {
        this.familyRelation = familyRelation;
    }

    public Boolean getIsHereditary() {
        return isHereditary;
    }

    public void setIsHereditary(Boolean isHereditary) {
        this.isHereditary = isHereditary;
    }

    public Boolean getIsChronic() {
        return isChronic;
    }

    public void setIsChronic(Boolean isChronic) {
        this.isChronic = isChronic;
    }

    public Boolean getIsContagious() {
        return isContagious;
    }

    public void setIsContagious(Boolean isContagious) {
        this.isContagious = isContagious;
    }

    public String getSeverity() {
        return severity;
    }

    public void setSeverity(String severity) {
        this.severity = severity;
    }

    public String getCurrentStatus() {
        return currentStatus;
    }

    public void setCurrentStatus(String currentStatus) {
        this.currentStatus = currentStatus;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public Long getRecordedBy() {
        return recordedBy;
    }

    public void setRecordedBy(Long recordedBy) {
        this.recordedBy = recordedBy;
    }

    public LocalDateTime getRecordedTime() {
        return recordedTime;
    }

    public void setRecordedTime(LocalDateTime recordedTime) {
        this.recordedTime = recordedTime;
    }

    public Long getLastUpdatedBy() {
        return lastUpdatedBy;
    }

    public void setLastUpdatedBy(Long lastUpdatedBy) {
        this.lastUpdatedBy = lastUpdatedBy;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MedicalHistory)) return false;
        MedicalHistory that = (MedicalHistory) o;
        return patientId != null && patientId.equals(that.patientId) &&
               diseaseName != null && diseaseName.equals(that.diseaseName) &&
               historyType != null && historyType.equals(that.historyType);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(patientId, diseaseName, historyType);
    }

    @Override
    public String toString() {
        return "MedicalHistory{" +
                "id=" + getId() +
                ", patientId=" + patientId +
                ", historyType='" + historyType + '\'' +
                ", diseaseName='" + diseaseName + '\'' +
                ", severity='" + severity + '\'' +
                ", currentStatus='" + currentStatus + '\'' +
                '}';
    }
}