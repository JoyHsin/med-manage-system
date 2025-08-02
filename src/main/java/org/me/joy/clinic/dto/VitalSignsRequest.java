package org.me.joy.clinic.dto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

/**
 * 生命体征录入请求DTO
 * 
 * @author Kiro
 */
public class VitalSignsRequest {

    /**
     * 患者ID
     */
    @NotNull(message = "患者ID不能为空")
    private Long patientId;

    /**
     * 收缩压(mmHg)
     */
    @Min(value = 60, message = "收缩压不能低于60mmHg")
    @Max(value = 300, message = "收缩压不能高于300mmHg")
    private Integer systolicBp;

    /**
     * 舒张压(mmHg)
     */
    @Min(value = 40, message = "舒张压不能低于40mmHg")
    @Max(value = 200, message = "舒张压不能高于200mmHg")
    private Integer diastolicBp;

    /**
     * 体温(°C)
     */
    @DecimalMin(value = "35.0", message = "体温不能低于35.0°C")
    @DecimalMax(value = "42.0", message = "体温不能高于42.0°C")
    @Digits(integer = 2, fraction = 1, message = "体温格式不正确，应为XX.X格式")
    private BigDecimal temperature;

    /**
     * 心率(次/分钟)
     */
    @Min(value = 40, message = "心率不能低于40次/分钟")
    @Max(value = 200, message = "心率不能高于200次/分钟")
    private Integer heartRate;

    /**
     * 呼吸频率(次/分钟)
     */
    @Min(value = 8, message = "呼吸频率不能低于8次/分钟")
    @Max(value = 40, message = "呼吸频率不能高于40次/分钟")
    private Integer respiratoryRate;

    /**
     * 血氧饱和度(%)
     */
    @Min(value = 70, message = "血氧饱和度不能低于70%")
    @Max(value = 100, message = "血氧饱和度不能高于100%")
    private Integer oxygenSaturation;

    /**
     * 体重(kg)
     */
    @DecimalMin(value = "0.5", message = "体重不能低于0.5kg")
    @DecimalMax(value = "500.0", message = "体重不能高于500kg")
    @Digits(integer = 3, fraction = 2, message = "体重格式不正确，应为XXX.XX格式")
    private BigDecimal weight;

    /**
     * 身高(cm)
     */
    @Min(value = 30, message = "身高不能低于30cm")
    @Max(value = 250, message = "身高不能高于250cm")
    private Integer height;

    /**
     * 疼痛评分(0-10)
     */
    @Min(value = 0, message = "疼痛评分不能低于0")
    @Max(value = 10, message = "疼痛评分不能高于10")
    private Integer painScore;

    /**
     * 意识状态
     */
    @Size(max = 20, message = "意识状态描述不能超过20个字符")
    private String consciousnessLevel;

    /**
     * 备注信息
     */
    @Size(max = 1000, message = "备注信息不能超过1000个字符")
    private String remarks;

    // 构造函数
    public VitalSignsRequest() {}

    // Getters and Setters
    public Long getPatientId() {
        return patientId;
    }

    public void setPatientId(Long patientId) {
        this.patientId = patientId;
    }

    public Integer getSystolicBp() {
        return systolicBp;
    }

    public void setSystolicBp(Integer systolicBp) {
        this.systolicBp = systolicBp;
    }

    public Integer getDiastolicBp() {
        return diastolicBp;
    }

    public void setDiastolicBp(Integer diastolicBp) {
        this.diastolicBp = diastolicBp;
    }

    public BigDecimal getTemperature() {
        return temperature;
    }

    public void setTemperature(BigDecimal temperature) {
        this.temperature = temperature;
    }

    public Integer getHeartRate() {
        return heartRate;
    }

    public void setHeartRate(Integer heartRate) {
        this.heartRate = heartRate;
    }

    public Integer getRespiratoryRate() {
        return respiratoryRate;
    }

    public void setRespiratoryRate(Integer respiratoryRate) {
        this.respiratoryRate = respiratoryRate;
    }

    public Integer getOxygenSaturation() {
        return oxygenSaturation;
    }

    public void setOxygenSaturation(Integer oxygenSaturation) {
        this.oxygenSaturation = oxygenSaturation;
    }

    public BigDecimal getWeight() {
        return weight;
    }

    public void setWeight(BigDecimal weight) {
        this.weight = weight;
    }

    public Integer getHeight() {
        return height;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }

    public Integer getPainScore() {
        return painScore;
    }

    public void setPainScore(Integer painScore) {
        this.painScore = painScore;
    }

    public String getConsciousnessLevel() {
        return consciousnessLevel;
    }

    public void setConsciousnessLevel(String consciousnessLevel) {
        this.consciousnessLevel = consciousnessLevel;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    /**
     * 验证血压数据的一致性
     */
    public boolean isBloodPressureValid() {
        if (systolicBp != null && diastolicBp != null) {
            return systolicBp > diastolicBp;
        }
        return true;
    }

    @Override
    public String toString() {
        return "VitalSignsRequest{" +
                "patientId=" + patientId +
                ", systolicBp=" + systolicBp +
                ", diastolicBp=" + diastolicBp +
                ", temperature=" + temperature +
                ", heartRate=" + heartRate +
                ", respiratoryRate=" + respiratoryRate +
                ", oxygenSaturation=" + oxygenSaturation +
                ", weight=" + weight +
                ", height=" + height +
                ", painScore=" + painScore +
                ", consciousnessLevel='" + consciousnessLevel + '\'' +
                ", remarks='" + remarks + '\'' +
                '}';
    }
}