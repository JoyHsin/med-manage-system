package org.me.joy.clinic.dto;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.List;

/**
 * 月财务报表数据传输对象
 */
public class MonthlyFinancialReport {
    private YearMonth reportMonth;
    private BigDecimal totalRevenue;
    private BigDecimal totalRegistrationFees;
    private BigDecimal totalMedicalFees;
    private BigDecimal totalMedicineFees;
    private BigDecimal totalRefunds;
    private BigDecimal netRevenue;
    private Integer totalPatients;
    private Integer totalBills;
    private BigDecimal averageDailyRevenue;
    private BigDecimal averageBillAmount;
    private List<DailyFinancialSummary> dailySummaries;
    private List<RevenueByService> revenueByServices;
    private List<PaymentMethodSummary> paymentMethodSummaries;

    public MonthlyFinancialReport() {}

    public MonthlyFinancialReport(YearMonth reportMonth) {
        this.reportMonth = reportMonth;
        this.totalRevenue = BigDecimal.ZERO;
        this.totalRegistrationFees = BigDecimal.ZERO;
        this.totalMedicalFees = BigDecimal.ZERO;
        this.totalMedicineFees = BigDecimal.ZERO;
        this.totalRefunds = BigDecimal.ZERO;
        this.netRevenue = BigDecimal.ZERO;
        this.totalPatients = 0;
        this.totalBills = 0;
        this.averageDailyRevenue = BigDecimal.ZERO;
        this.averageBillAmount = BigDecimal.ZERO;
    }

    // Getters and Setters
    public YearMonth getReportMonth() {
        return reportMonth;
    }

    public void setReportMonth(YearMonth reportMonth) {
        this.reportMonth = reportMonth;
    }

    public BigDecimal getTotalRevenue() {
        return totalRevenue;
    }

    public void setTotalRevenue(BigDecimal totalRevenue) {
        this.totalRevenue = totalRevenue;
    }

    public BigDecimal getTotalRegistrationFees() {
        return totalRegistrationFees;
    }

    public void setTotalRegistrationFees(BigDecimal totalRegistrationFees) {
        this.totalRegistrationFees = totalRegistrationFees;
    }

    public BigDecimal getTotalMedicalFees() {
        return totalMedicalFees;
    }

    public void setTotalMedicalFees(BigDecimal totalMedicalFees) {
        this.totalMedicalFees = totalMedicalFees;
    }

    public BigDecimal getTotalMedicineFees() {
        return totalMedicineFees;
    }

    public void setTotalMedicineFees(BigDecimal totalMedicineFees) {
        this.totalMedicineFees = totalMedicineFees;
    }

    public BigDecimal getTotalRefunds() {
        return totalRefunds;
    }

    public void setTotalRefunds(BigDecimal totalRefunds) {
        this.totalRefunds = totalRefunds;
    }

    public BigDecimal getNetRevenue() {
        return netRevenue;
    }

    public void setNetRevenue(BigDecimal netRevenue) {
        this.netRevenue = netRevenue;
    }

    public Integer getTotalPatients() {
        return totalPatients;
    }

    public void setTotalPatients(Integer totalPatients) {
        this.totalPatients = totalPatients;
    }

    public Integer getTotalBills() {
        return totalBills;
    }

    public void setTotalBills(Integer totalBills) {
        this.totalBills = totalBills;
    }

    public BigDecimal getAverageDailyRevenue() {
        return averageDailyRevenue;
    }

    public void setAverageDailyRevenue(BigDecimal averageDailyRevenue) {
        this.averageDailyRevenue = averageDailyRevenue;
    }

    public BigDecimal getAverageBillAmount() {
        return averageBillAmount;
    }

    public void setAverageBillAmount(BigDecimal averageBillAmount) {
        this.averageBillAmount = averageBillAmount;
    }

    public List<DailyFinancialSummary> getDailySummaries() {
        return dailySummaries;
    }

    public void setDailySummaries(List<DailyFinancialSummary> dailySummaries) {
        this.dailySummaries = dailySummaries;
    }

    public List<RevenueByService> getRevenueByServices() {
        return revenueByServices;
    }

    public void setRevenueByServices(List<RevenueByService> revenueByServices) {
        this.revenueByServices = revenueByServices;
    }

    public List<PaymentMethodSummary> getPaymentMethodSummaries() {
        return paymentMethodSummaries;
    }

    public void setPaymentMethodSummaries(List<PaymentMethodSummary> paymentMethodSummaries) {
        this.paymentMethodSummaries = paymentMethodSummaries;
    }
}