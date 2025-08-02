package org.me.joy.clinic.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 生命体征实体类
 * 
 * @author Kiro
 */
@TableName("vital_signs")
public class VitalSigns extends BaseEntity {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 患者ID
     */
    @NotNull(message = "患者ID不能为空")
    private Long patientId;

    /**
     * 记录人员ID
     */
    @NotNull(message = "记录人员ID不能为空")
    private Long recordedBy;

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
     * BMI指数
     */
    @DecimalMin(value = "10.0", message = "BMI指数不能低于10.0")
    @DecimalMax(value = "50.0", message = "BMI指数不能高于50.0")
    @Digits(integer = 2, fraction = 1, message = "BMI格式不正确，应为XX.X格式")
    private BigDecimal bmi;

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

    /**
     * 是否异常
     */
    @NotNull(message = "异常标识不能为空")
    private Boolean isAbnormal;

    /**
     * 异常指标说明
     */
    @Size(max = 500, message = "异常指标说明不能超过500个字符")
    private String abnormalIndicators;

    /**
     * 记录时间
     */
    @NotNull(message = "记录时间不能为空")
    private LocalDateTime recordedAt;

    // 构造函数
    public VitalSigns() {}

    public VitalSigns(Long patientId, Long recordedBy) {
        this.patientId = patientId;
        this.recordedBy = recordedBy;
        this.recordedAt = LocalDateTime.now();
        this.isAbnormal = false;
    }

    // Getters and Setters
    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public Long getPatientId() {
        return patientId;
    }

    public void setPatientId(Long patientId) {
        this.patientId = patientId;
    }

    public Long getRecordedBy() {
        return recordedBy;
    }

    public void setRecordedBy(Long recordedBy) {
        this.recordedBy = recordedBy;
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

    public BigDecimal getBmi() {
        return bmi;
    }

    public void setBmi(BigDecimal bmi) {
        this.bmi = bmi;
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

    public Boolean getIsAbnormal() {
        return isAbnormal;
    }

    public void setIsAbnormal(Boolean isAbnormal) {
        this.isAbnormal = isAbnormal;
    }

    public String getAbnormalIndicators() {
        return abnormalIndicators;
    }

    public void setAbnormalIndicators(String abnormalIndicators) {
        this.abnormalIndicators = abnormalIndicators;
    }

    public LocalDateTime getRecordedAt() {
        return recordedAt;
    }

    public void setRecordedAt(LocalDateTime recordedAt) {
        this.recordedAt = recordedAt;
    }

    /**
     * 验证血压数据的一致性
     * 收缩压应该大于舒张压
     */
    public boolean isBloodPressureValid() {
        if (systolicBp != null && diastolicBp != null) {
            return systolicBp > diastolicBp;
        }
        return true; // 如果任一值为空，则不进行验证
    }

    /**
     * 计算BMI指数
     * BMI = 体重(kg) / (身高(m))²
     */
    public BigDecimal calculateBmi() {
        if (weight != null && height != null && height > 0) {
            BigDecimal heightInMeters = new BigDecimal(height).divide(new BigDecimal(100));
            return weight.divide(heightInMeters.multiply(heightInMeters), 1, BigDecimal.ROUND_HALF_UP);
        }
        return null;
    }

    /**
     * 检查生命体征是否异常
     */
    public boolean checkAbnormal() {
        StringBuilder abnormalList = new StringBuilder();
        
        // 检查血压异常
        if (systolicBp != null && (systolicBp < 90 || systolicBp > 140)) {
            abnormalList.append("收缩压异常;");
        }
        if (diastolicBp != null && (diastolicBp < 60 || diastolicBp > 90)) {
            abnormalList.append("舒张压异常;");
        }
        
        // 检查体温异常
        if (temperature != null && (temperature.compareTo(new BigDecimal("36.0")) < 0 || 
                                   temperature.compareTo(new BigDecimal("37.5")) > 0)) {
            abnormalList.append("体温异常;");
        }
        
        // 检查心率异常
        if (heartRate != null && (heartRate < 60 || heartRate > 100)) {
            abnormalList.append("心率异常;");
        }
        
        // 检查呼吸频率异常
        if (respiratoryRate != null && (respiratoryRate < 12 || respiratoryRate > 20)) {
            abnormalList.append("呼吸频率异常;");
        }
        
        // 检查血氧饱和度异常
        if (oxygenSaturation != null && oxygenSaturation < 95) {
            abnormalList.append("血氧饱和度异常;");
        }
        
        // 检查BMI异常
        if (bmi != null && (bmi.compareTo(new BigDecimal("18.5")) < 0 || 
                           bmi.compareTo(new BigDecimal("24.0")) > 0)) {
            abnormalList.append("BMI异常;");
        }
        
        if (abnormalList.length() > 0) {
            this.abnormalIndicators = abnormalList.toString();
            this.isAbnormal = true;
            return true;
        } else {
            this.abnormalIndicators = null;
            this.isAbnormal = false;
            return false;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof VitalSigns)) return false;
        VitalSigns that = (VitalSigns) o;
        return getId() != null && getId().equals(that.getId());
    }

    @Override
    public int hashCode() {
        return getId() != null ? getId().hashCode() : 0;
    }

    @Override
    public String toString() {
        return "VitalSigns{" +
                "id=" + id +
                ", patientId=" + patientId +
                ", recordedBy=" + recordedBy +
                ", systolicBp=" + systolicBp +
                ", diastolicBp=" + diastolicBp +
                ", temperature=" + temperature +
                ", heartRate=" + heartRate +
                ", respiratoryRate=" + respiratoryRate +
                ", oxygenSaturation=" + oxygenSaturation +
                ", weight=" + weight +
                ", height=" + height +
                ", bmi=" + bmi +
                ", painScore=" + painScore +
                ", consciousnessLevel='" + consciousnessLevel + '\'' +
                ", isAbnormal=" + isAbnormal +
                ", abnormalIndicators='" + abnormalIndicators + '\'' +
                ", recordedAt=" + recordedAt +
                '}';
    }
}