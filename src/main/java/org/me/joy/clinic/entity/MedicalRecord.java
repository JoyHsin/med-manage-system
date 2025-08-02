package org.me.joy.clinic.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import jakarta.validation.constraints.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 病历实体类
 * 存储患者的诊疗记录信息
 */
@TableName("medical_records")
public class MedicalRecord extends BaseEntity {

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
     * 病历编号
     */
    @NotBlank(message = "病历编号不能为空")
    @Size(max = 50, message = "病历编号长度不能超过50个字符")
    private String recordNumber;

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
     * 病历状态
     */
    @Pattern(regexp = "^(草稿|待审核|已审核|已归档)$", 
             message = "病历状态只能是草稿、待审核、已审核或已归档")
    private String status = "草稿";

    /**
     * 记录日期
     */
    @NotNull(message = "记录日期不能为空")
    private LocalDateTime recordDate;

    /**
     * 审核医生ID
     */
    private Long reviewDoctorId;

    /**
     * 审核时间
     */
    private LocalDateTime reviewTime;

    /**
     * 审核意见
     */
    @Size(max = 500, message = "审核意见长度不能超过500个字符")
    private String reviewComments;

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
     * 病历摘要
     */
    @Size(max = 500, message = "病历摘要长度不能超过500个字符")
    private String summary;

    /**
     * 备注信息
     */
    @Size(max = 1000, message = "备注信息长度不能超过1000个字符")
    private String remarks;

    /**
     * 创建医生ID
     */
    private Long createdBy;

    /**
     * 最后更新医生ID
     */
    private Long lastUpdatedBy;

    /**
     * 诊断列表（不存储在数据库中，通过关联查询获取）
     */
    @TableField(exist = false)
    private List<Diagnosis> diagnoses;

    /**
     * 处方列表（不存储在数据库中，通过关联查询获取）
     */
    @TableField(exist = false)
    private List<Prescription> prescriptions;

    // 构造函数
    public MedicalRecord() {}

    public MedicalRecord(Long patientId, Long doctorId, String recordNumber) {
        this.patientId = patientId;
        this.doctorId = doctorId;
        this.recordNumber = recordNumber;
        this.recordDate = LocalDateTime.now();
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

    public Long getRegistrationId() {
        return registrationId;
    }

    public void setRegistrationId(Long registrationId) {
        this.registrationId = registrationId;
    }

    public String getRecordNumber() {
        return recordNumber;
    }

    public void setRecordNumber(String recordNumber) {
        this.recordNumber = recordNumber;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getRecordDate() {
        return recordDate;
    }

    public void setRecordDate(LocalDateTime recordDate) {
        this.recordDate = recordDate;
    }

    public Long getReviewDoctorId() {
        return reviewDoctorId;
    }

    public void setReviewDoctorId(Long reviewDoctorId) {
        this.reviewDoctorId = reviewDoctorId;
    }

    public LocalDateTime getReviewTime() {
        return reviewTime;
    }

    public void setReviewTime(LocalDateTime reviewTime) {
        this.reviewTime = reviewTime;
    }

    public String getReviewComments() {
        return reviewComments;
    }

    public void setReviewComments(String reviewComments) {
        this.reviewComments = reviewComments;
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

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
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

    public List<Diagnosis> getDiagnoses() {
        return diagnoses;
    }

    public void setDiagnoses(List<Diagnosis> diagnoses) {
        this.diagnoses = diagnoses;
    }

    public List<Prescription> getPrescriptions() {
        return prescriptions;
    }

    public void setPrescriptions(List<Prescription> prescriptions) {
        this.prescriptions = prescriptions;
    }

    /**
     * 检查病历是否可以编辑
     */
    public boolean canBeEdited() {
        return "草稿".equals(status) || "待审核".equals(status);
    }

    /**
     * 检查病历是否已审核
     */
    public boolean isReviewed() {
        return "已审核".equals(status) || "已归档".equals(status);
    }

    /**
     * 提交审核
     */
    public void submitForReview() {
        if ("草稿".equals(status)) {
            this.status = "待审核";
        }
    }

    /**
     * 审核通过
     */
    public void approve(Long reviewDoctorId, String comments) {
        if ("待审核".equals(status)) {
            this.status = "已审核";
            this.reviewDoctorId = reviewDoctorId;
            this.reviewTime = LocalDateTime.now();
            this.reviewComments = comments;
        }
    }

    /**
     * 审核拒绝
     */
    public void reject(Long reviewDoctorId, String comments) {
        if ("待审核".equals(status)) {
            this.status = "草稿";
            this.reviewDoctorId = reviewDoctorId;
            this.reviewTime = LocalDateTime.now();
            this.reviewComments = comments;
        }
    }

    /**
     * 归档
     */
    public void archive() {
        if ("已审核".equals(status)) {
            this.status = "已归档";
        }
    }

    /**
     * 生成病历摘要
     */
    public void generateSummary() {
        StringBuilder summaryBuilder = new StringBuilder();
        
        if (chiefComplaint != null && !chiefComplaint.trim().isEmpty()) {
            summaryBuilder.append("主诉：").append(chiefComplaint.substring(0, Math.min(50, chiefComplaint.length())));
        }
        
        if (finalDiagnosis != null && !finalDiagnosis.trim().isEmpty()) {
            if (summaryBuilder.length() > 0) summaryBuilder.append("；");
            summaryBuilder.append("诊断：").append(finalDiagnosis.substring(0, Math.min(50, finalDiagnosis.length())));
        }
        
        if (treatmentPlan != null && !treatmentPlan.trim().isEmpty()) {
            if (summaryBuilder.length() > 0) summaryBuilder.append("；");
            summaryBuilder.append("治疗：").append(treatmentPlan.substring(0, Math.min(50, treatmentPlan.length())));
        }
        
        this.summary = summaryBuilder.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MedicalRecord)) return false;
        MedicalRecord that = (MedicalRecord) o;
        return recordNumber != null && recordNumber.equals(that.recordNumber);
    }

    @Override
    public int hashCode() {
        return recordNumber != null ? recordNumber.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "MedicalRecord{" +
                "id=" + getId() +
                ", patientId=" + patientId +
                ", doctorId=" + doctorId +
                ", recordNumber='" + recordNumber + '\'' +
                ", status='" + status + '\'' +
                ", recordType='" + recordType + '\'' +
                ", department='" + department + '\'' +
                ", recordDate=" + recordDate +
                '}';
    }
}