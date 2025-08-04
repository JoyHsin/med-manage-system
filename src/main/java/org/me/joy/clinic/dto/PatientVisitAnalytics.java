package org.me.joy.clinic.dto;

import java.time.LocalDate;

/**
 * 患者就诊量分析数据传输对象
 */
public class PatientVisitAnalytics {
    private LocalDate date;
    private Long totalVisits;
    private Long newPatients;
    private Long returningPatients;
    private Double averageVisitsPerDay;
    private Long peakHourVisits;
    private String peakHour;

    public PatientVisitAnalytics() {}

    public PatientVisitAnalytics(LocalDate date, Long totalVisits, Long newPatients, 
                               Long returningPatients, Double averageVisitsPerDay, 
                               Long peakHourVisits, String peakHour) {
        this.date = date;
        this.totalVisits = totalVisits;
        this.newPatients = newPatients;
        this.returningPatients = returningPatients;
        this.averageVisitsPerDay = averageVisitsPerDay;
        this.peakHourVisits = peakHourVisits;
        this.peakHour = peakHour;
    }

    // Getters and Setters
    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public Long getTotalVisits() {
        return totalVisits;
    }

    public void setTotalVisits(Long totalVisits) {
        this.totalVisits = totalVisits;
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

    public Double getAverageVisitsPerDay() {
        return averageVisitsPerDay;
    }

    public void setAverageVisitsPerDay(Double averageVisitsPerDay) {
        this.averageVisitsPerDay = averageVisitsPerDay;
    }

    public Long getPeakHourVisits() {
        return peakHourVisits;
    }

    public void setPeakHourVisits(Long peakHourVisits) {
        this.peakHourVisits = peakHourVisits;
    }

    public String getPeakHour() {
        return peakHour;
    }

    public void setPeakHour(String peakHour) {
        this.peakHour = peakHour;
    }
}