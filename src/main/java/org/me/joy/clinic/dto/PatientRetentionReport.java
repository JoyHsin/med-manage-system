package org.me.joy.clinic.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 患者留存报告
 */
public class PatientRetentionReport {
    private LocalDate reportDate;
    private Long newPatients;
    private Long returningPatients;
    private BigDecimal retentionRate;
    private Long totalVisits;
    private BigDecimal averageVisitsPerPatient;
    
    public PatientRetentionReport() {}
    
    public PatientRetentionReport(LocalDate reportDate, Long newPatients, Long returningPatients,
                                 BigDecimal retentionRate, Long totalVisits, BigDecimal averageVisitsPerPatient) {
        this.reportDate = reportDate;
        this.newPatients = newPatients;
        this.returningPatients = returningPatients;
        this.retentionRate = retentionRate;
        this.totalVisits = totalVisits;
        this.averageVisitsPerPatient = averageVisitsPerPatient;
    }
    
    // Getters and Setters
    public LocalDate getReportDate() {
        return reportDate;
    }
    
    public void setReportDate(LocalDate reportDate) {
        this.reportDate = reportDate;
    }
    
    public Long getNewPatients() {
        return newPatients;
    }
    
    public void setNewPatients(Long newPatients) {
        this.newPatients = newPatients;
    }
    
    public Long getReturningPatients() {
        return returningPatients;
    }
    
    public void setReturningPatients(Long returningPatients) {
        this.returningPatients = returningPatients;
    }
    
    public BigDecimal getRetentionRate() {
        return retentionRate;
    }
    
    public void setRetentionRate(BigDecimal retentionRate) {
        this.retentionRate = retentionRate;
    }
    
    public Long getTotalVisits() {
        return totalVisits;
    }
    
    public void setTotalVisits(Long totalVisits) {
        this.totalVisits = totalVisits;
    }
    
    public BigDecimal getAverageVisitsPerPatient() {
        return averageVisitsPerPatient;
    }
    
    public void setAverageVisitsPerPatient(BigDecimal averageVisitsPerPatient) {
        this.averageVisitsPerPatient = averageVisitsPerPatient;
    }
}