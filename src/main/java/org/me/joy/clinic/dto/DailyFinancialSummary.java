package org.me.joy.clinic.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 日财务汇总数据传输对象
 */
public class DailyFinancialSummary {
    private LocalDate date;
    private BigDecimal totalRevenue;
    private BigDecimal netRevenue;
    private Integer totalPatients;
    private Integer totalBills;
    private BigDecimal averageBillAmount;

    public DailyFinancialSummary() {}

    public DailyFinancialSummary(LocalDate date, BigDecimal totalRevenue, BigDecimal netRevenue, 
                                Integer totalPatients, Integer totalBills) {
        this.date = date;
        this.totalRevenue = totalRevenue;
        this.netRevenue = netRevenue;
        this.totalPatients = totalPatients;
        this.totalBills = totalBills;
        this.averageBillAmount = totalBills > 0 ? 
            totalRevenue.divide(BigDecimal.valueOf(totalBills), 2, BigDecimal.ROUND_HALF_UP) : 
            BigDecimal.ZERO;
    }

    // Getters and Setters
    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public BigDecimal getTotalRevenue() {
        return totalRevenue;
    }

    public void setTotalRevenue(BigDecimal totalRevenue) {
        this.totalRevenue = totalRevenue;
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

    public BigDecimal getAverageBillAmount() {
        return averageBillAmount;
    }

    public void setAverageBillAmount(BigDecimal averageBillAmount) {
        this.averageBillAmount = averageBillAmount;
    }
}