package org.me.joy.clinic.dto;

import java.time.LocalDate;

/**
 * 医生绩效报表数据传输对象
 */
public class DoctorPerformanceReport {
    private Long doctorId;
    private String doctorName;
    private String department;
    private LocalDate reportDate;
    private Long totalPatients;
    private Long completedConsultations;
    private Double averageConsultationTime;
    private Double patientSatisfactionScore;
    private Long prescriptionsIssued;
    private Double revenueGenerated;

    public DoctorPerformanceReport() {}

    public DoctorPerformanceReport(Long doctorId, String doctorName, String department,
                                 LocalDate reportDate, Long totalPatients, Long completedConsultations,
                                 Double averageConsultationTime, Double patientSatisfactionScore,
                                 Long prescriptionsIssued, Double revenueGenerated) {
        this.doctorId = doctorId;
        this.doctorName = doctorName;
        this.department = department;
        this.reportDate = reportDate;
        this.totalPatients = totalPatients;
        this.completedConsultations = completedConsultations;
        this.averageConsultationTime = averageConsultationTime;
        this.patientSatisfactionScore = patientSatisfactionScore;
        this.prescriptionsIssued = prescriptionsIssued;
        this.revenueGenerated = revenueGenerated;
    }

    // Getters and Setters
    public Long getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(Long doctorId) {
        this.doctorId = doctorId;
    }

    public String getDoctorName() {
        return doctorName;
    }

    public void setDoctorName(String doctorName) {
        this.doctorName = doctorName;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public LocalDate getReportDate() {
        return reportDate;
    }

    public void setReportDate(LocalDate reportDate) {
        this.reportDate = reportDate;
    }

    public Long getTotalPatients() {
        return totalPatients;
    }

    public void setTotalPatients(Long totalPatients) {
        this.totalPatients = totalPatients;
    }

    public Long getCompletedConsultations() {
        return completedConsultations;
    }

    public void setCompletedConsultations(Long completedConsultations) {
        this.completedConsultations = completedConsultations;
    }

    public Double getAverageConsultationTime() {
        return averageConsultationTime;
    }

    public void setAverageConsultationTime(Double averageConsultationTime) {
        this.averageConsultationTime = averageConsultationTime;
    }

    public Double getPatientSatisfactionScore() {
        return patientSatisfactionScore;
    }

    public void setPatientSatisfactionScore(Double patientSatisfactionScore) {
        this.patientSatisfactionScore = patientSatisfactionScore;
    }

    public Long getPrescriptionsIssued() {
        return prescriptionsIssued;
    }

    public void setPrescriptionsIssued(Long prescriptionsIssued) {
        this.prescriptionsIssued = prescriptionsIssued;
    }

    public Double getRevenueGenerated() {
        return revenueGenerated;
    }

    public void setRevenueGenerated(Double revenueGenerated) {
        this.revenueGenerated = revenueGenerated;
    }
}