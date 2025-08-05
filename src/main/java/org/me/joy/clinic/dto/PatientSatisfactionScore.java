package org.me.joy.clinic.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 患者满意度评分
 */
public class PatientSatisfactionScore {
    private LocalDate surveyDate;
    private String category;
    private BigDecimal averageScore;
    private Long responseCount;
    private BigDecimal satisfactionRate;
    
    public PatientSatisfactionScore() {}
    
    public PatientSatisfactionScore(LocalDate surveyDate, String category, BigDecimal averageScore,
                                   Long responseCount, BigDecimal satisfactionRate) {
        this.surveyDate = surveyDate;
        this.category = category;
        this.averageScore = averageScore;
        this.responseCount = responseCount;
        this.satisfactionRate = satisfactionRate;
    }
    
    // Getters and Setters
    public LocalDate getSurveyDate() {
        return surveyDate;
    }
    
    public void setSurveyDate(LocalDate surveyDate) {
        this.surveyDate = surveyDate;
    }
    
    public String getCategory() {
        return category;
    }
    
    public void setCategory(String category) {
        this.category = category;
    }
    
    public BigDecimal getAverageScore() {
        return averageScore;
    }
    
    public void setAverageScore(BigDecimal averageScore) {
        this.averageScore = averageScore;
    }
    
    public Long getResponseCount() {
        return responseCount;
    }
    
    public void setResponseCount(Long responseCount) {
        this.responseCount = responseCount;
    }
    
    public BigDecimal getSatisfactionRate() {
        return satisfactionRate;
    }
    
    public void setSatisfactionRate(BigDecimal satisfactionRate) {
        this.satisfactionRate = satisfactionRate;
    }
}