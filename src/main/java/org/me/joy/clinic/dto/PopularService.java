package org.me.joy.clinic.dto;

/**
 * 热门服务数据传输对象
 */
public class PopularService {
    private String serviceName;
    private String serviceCode;
    private Long visitCount;
    private Double percentage;
    private String department;

    public PopularService() {}

    public PopularService(String serviceName, String serviceCode, Long visitCount, 
                         Double percentage, String department) {
        this.serviceName = serviceName;
        this.serviceCode = serviceCode;
        this.visitCount = visitCount;
        this.percentage = percentage;
        this.department = department;
    }

    // Getters and Setters
    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getServiceCode() {
        return serviceCode;
    }

    public void setServiceCode(String serviceCode) {
        this.serviceCode = serviceCode;
    }

    public Long getVisitCount() {
        return visitCount;
    }

    public void setVisitCount(Long visitCount) {
        this.visitCount = visitCount;
    }

    public Double getPercentage() {
        return percentage;
    }

    public void setPercentage(Double percentage) {
        this.percentage = percentage;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }
}