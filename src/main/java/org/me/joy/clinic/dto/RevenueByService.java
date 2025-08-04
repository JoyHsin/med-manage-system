package org.me.joy.clinic.dto;

import java.math.BigDecimal;

/**
 * 按服务类型统计收入数据传输对象
 */
public class RevenueByService {
    private String serviceType;
    private String serviceName;
    private BigDecimal revenue;
    private Integer count;
    private BigDecimal averageAmount;
    private BigDecimal percentage;

    public RevenueByService() {}

    public RevenueByService(String serviceType, String serviceName, BigDecimal revenue, Integer count) {
        this.serviceType = serviceType;
        this.serviceName = serviceName;
        this.revenue = revenue;
        this.count = count;
        this.averageAmount = count > 0 ? revenue.divide(BigDecimal.valueOf(count), 2, BigDecimal.ROUND_HALF_UP) : BigDecimal.ZERO;
    }

    // Getters and Setters
    public String getServiceType() {
        return serviceType;
    }

    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public BigDecimal getRevenue() {
        return revenue;
    }

    public void setRevenue(BigDecimal revenue) {
        this.revenue = revenue;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public BigDecimal getAverageAmount() {
        return averageAmount;
    }

    public void setAverageAmount(BigDecimal averageAmount) {
        this.averageAmount = averageAmount;
    }

    public BigDecimal getPercentage() {
        return percentage;
    }

    public void setPercentage(BigDecimal percentage) {
        this.percentage = percentage;
    }
}