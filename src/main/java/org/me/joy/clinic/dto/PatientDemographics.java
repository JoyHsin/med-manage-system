package org.me.joy.clinic.dto;

import java.math.BigDecimal;
import java.util.Map;

/**
 * 患者人口统计数据
 */
public class PatientDemographics {
    private Long totalPatients;
    private Map<String, Long> genderDistribution;
    private Map<String, Long> ageGroupDistribution;
    private BigDecimal averageAge;
    private Map<String, Long> locationDistribution;
    
    public PatientDemographics() {}
    
    public PatientDemographics(Long totalPatients, Map<String, Long> genderDistribution, 
                              Map<String, Long> ageGroupDistribution, BigDecimal averageAge,
                              Map<String, Long> locationDistribution) {
        this.totalPatients = totalPatients;
        this.genderDistribution = genderDistribution;
        this.ageGroupDistribution = ageGroupDistribution;
        this.averageAge = averageAge;
        this.locationDistribution = locationDistribution;
    }
    
    // Getters and Setters
    public Long getTotalPatients() {
        return totalPatients;
    }
    
    public void setTotalPatients(Long totalPatients) {
        this.totalPatients = totalPatients;
    }
    
    public Map<String, Long> getGenderDistribution() {
        return genderDistribution;
    }
    
    public void setGenderDistribution(Map<String, Long> genderDistribution) {
        this.genderDistribution = genderDistribution;
    }
    
    public Map<String, Long> getAgeGroupDistribution() {
        return ageGroupDistribution;
    }
    
    public void setAgeGroupDistribution(Map<String, Long> ageGroupDistribution) {
        this.ageGroupDistribution = ageGroupDistribution;
    }
    
    public BigDecimal getAverageAge() {
        return averageAge;
    }
    
    public void setAverageAge(BigDecimal averageAge) {
        this.averageAge = averageAge;
    }
    
    public Map<String, Long> getLocationDistribution() {
        return locationDistribution;
    }
    
    public void setLocationDistribution(Map<String, Long> locationDistribution) {
        this.locationDistribution = locationDistribution;
    }
}