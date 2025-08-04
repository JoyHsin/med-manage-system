package org.me.joy.clinic.dto;

import java.time.LocalDate;

/**
 * 等待时间分析数据传输对象
 */
public class WaitTimeAnalytics {
    private LocalDate date;
    private Double averageWaitTime;
    private Double medianWaitTime;
    private Double maxWaitTime;
    private Double minWaitTime;
    private Long totalPatients;
    private Long patientsWithinTarget;
    private Double targetComplianceRate;
    private String peakWaitTimeHour;

    public WaitTimeAnalytics() {}

    public WaitTimeAnalytics(LocalDate date, Double averageWaitTime, Double medianWaitTime,
                           Double maxWaitTime, Double minWaitTime, Long totalPatients,
                           Long patientsWithinTarget, Double targetComplianceRate,
                           String peakWaitTimeHour) {
        this.date = date;
        this.averageWaitTime = averageWaitTime;
        this.medianWaitTime = medianWaitTime;
        this.maxWaitTime = maxWaitTime;
        this.minWaitTime = minWaitTime;
        this.totalPatients = totalPatients;
        this.patientsWithinTarget = patientsWithinTarget;
        this.targetComplianceRate = targetComplianceRate;
        this.peakWaitTimeHour = peakWaitTimeHour;
    }

    // Getters and Setters
    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public Double getAverageWaitTime() {
        return averageWaitTime;
    }

    public void setAverageWaitTime(Double averageWaitTime) {
        this.averageWaitTime = averageWaitTime;
    }

    public Double getMedianWaitTime() {
        return medianWaitTime;
    }

    public void setMedianWaitTime(Double medianWaitTime) {
        this.medianWaitTime = medianWaitTime;
    }

    public Double getMaxWaitTime() {
        return maxWaitTime;
    }

    public void setMaxWaitTime(Double maxWaitTime) {
        this.maxWaitTime = maxWaitTime;
    }

    public Double getMinWaitTime() {
        return minWaitTime;
    }

    public void setMinWaitTime(Double minWaitTime) {
        this.minWaitTime = minWaitTime;
    }

    public Long getTotalPatients() {
        return totalPatients;
    }

    public void setTotalPatients(Long totalPatients) {
        this.totalPatients = totalPatients;
    }

    public Long getPatientsWithinTarget() {
        return patientsWithinTarget;
    }

    public void setPatientsWithinTarget(Long patientsWithinTarget) {
        this.patientsWithinTarget = patientsWithinTarget;
    }

    public Double getTargetComplianceRate() {
        return targetComplianceRate;
    }

    public void setTargetComplianceRate(Double targetComplianceRate) {
        this.targetComplianceRate = targetComplianceRate;
    }

    public String getPeakWaitTimeHour() {
        return peakWaitTimeHour;
    }

    public void setPeakWaitTimeHour(String peakWaitTimeHour) {
        this.peakWaitTimeHour = peakWaitTimeHour;
    }
}