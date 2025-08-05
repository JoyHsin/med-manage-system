package org.me.joy.clinic.dto;

/**
 * 常见诊断统计
 */
public class CommonDiagnosis {
    private String diagnosisCode;
    private String diagnosisName;
    private Long count;
    private Double percentage;
    
    public CommonDiagnosis() {}
    
    public CommonDiagnosis(String diagnosisCode, String diagnosisName, Long count, Double percentage) {
        this.diagnosisCode = diagnosisCode;
        this.diagnosisName = diagnosisName;
        this.count = count;
        this.percentage = percentage;
    }
    
    // Getters and Setters
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
    
    public Long getCount() {
        return count;
    }
    
    public void setCount(Long count) {
        this.count = count;
    }
    
    public Double getPercentage() {
        return percentage;
    }
    
    public void setPercentage(Double percentage) {
        this.percentage = percentage;
    }
}