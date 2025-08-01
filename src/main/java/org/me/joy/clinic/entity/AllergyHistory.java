package org.me.joy.clinic.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import jakarta.validation.constraints.*;

import java.time.LocalDateTime;

/**
 * 过敏史实体类
 * 记录患者的过敏信息和过敏反应
 */
@TableName("allergy_histories")
public class AllergyHistory extends BaseEntity {

    /**
     * 患者ID
     */
    @NotNull(message = "患者ID不能为空")
    private Long patientId;

    /**
     * 过敏原名称
     */
    @NotBlank(message = "过敏原名称不能为空")
    @Size(max = 200, message = "过敏原名称长度不能超过200个字符")
    private String allergen;

    /**
     * 过敏原类型
     */
    @NotBlank(message = "过敏原类型不能为空")
    @Pattern(regexp = "^(药物|食物|环境|其他)$", message = "过敏原类型只能是药物、食物、环境或其他")
    private String allergenType;

    /**
     * 过敏反应症状
     */
    @NotBlank(message = "过敏反应症状不能为空")
    @Size(max = 1000, message = "过敏反应症状长度不能超过1000个字符")
    private String symptoms;

    /**
     * 过敏严重程度
     */
    @NotBlank(message = "过敏严重程度不能为空")
    @Pattern(regexp = "^(轻微|中等|严重|危及生命)$", message = "过敏严重程度只能是轻微、中等、严重或危及生命")
    private String severity;

    /**
     * 首次发现时间
     */
    private LocalDateTime firstDiscoveredTime;

    /**
     * 最后发作时间
     */
    private LocalDateTime lastOccurrenceTime;

    /**
     * 处理措施
     */
    @Size(max = 1000, message = "处理措施长度不能超过1000个字符")
    private String treatment;

    /**
     * 备注信息
     */
    @Size(max = 500, message = "备注信息长度不能超过500个字符")
    private String remarks;

    /**
     * 是否确认过敏
     */
    private Boolean isConfirmed = false;

    /**
     * 记录人员ID
     */
    private Long recordedBy;

    /**
     * 记录时间
     */
    private LocalDateTime recordedTime;

    // 构造函数
    public AllergyHistory() {}

    public AllergyHistory(Long patientId, String allergen, String allergenType, String symptoms, String severity) {
        this.patientId = patientId;
        this.allergen = allergen;
        this.allergenType = allergenType;
        this.symptoms = symptoms;
        this.severity = severity;
        this.recordedTime = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getPatientId() {
        return patientId;
    }

    public void setPatientId(Long patientId) {
        this.patientId = patientId;
    }

    public String getAllergen() {
        return allergen;
    }

    public void setAllergen(String allergen) {
        this.allergen = allergen;
    }

    public String getAllergenType() {
        return allergenType;
    }

    public void setAllergenType(String allergenType) {
        this.allergenType = allergenType;
    }

    public String getSymptoms() {
        return symptoms;
    }

    public void setSymptoms(String symptoms) {
        this.symptoms = symptoms;
    }

    public String getSeverity() {
        return severity;
    }

    public void setSeverity(String severity) {
        this.severity = severity;
    }

    public LocalDateTime getFirstDiscoveredTime() {
        return firstDiscoveredTime;
    }

    public void setFirstDiscoveredTime(LocalDateTime firstDiscoveredTime) {
        this.firstDiscoveredTime = firstDiscoveredTime;
    }

    public LocalDateTime getLastOccurrenceTime() {
        return lastOccurrenceTime;
    }

    public void setLastOccurrenceTime(LocalDateTime lastOccurrenceTime) {
        this.lastOccurrenceTime = lastOccurrenceTime;
    }

    public String getTreatment() {
        return treatment;
    }

    public void setTreatment(String treatment) {
        this.treatment = treatment;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public Boolean getIsConfirmed() {
        return isConfirmed;
    }

    public void setIsConfirmed(Boolean isConfirmed) {
        this.isConfirmed = isConfirmed;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AllergyHistory)) return false;
        AllergyHistory that = (AllergyHistory) o;
        return patientId != null && patientId.equals(that.patientId) &&
               allergen != null && allergen.equals(that.allergen);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(patientId, allergen);
    }

    @Override
    public String toString() {
        return "AllergyHistory{" +
                "id=" + getId() +
                ", patientId=" + patientId +
                ", allergen='" + allergen + '\'' +
                ", allergenType='" + allergenType + '\'' +
                ", severity='" + severity + '\'' +
                ", isConfirmed=" + isConfirmed +
                '}';
    }
}