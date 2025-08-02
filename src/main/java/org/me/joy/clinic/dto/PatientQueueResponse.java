package org.me.joy.clinic.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 患者队列响应
 */
public class PatientQueueResponse {

    /**
     * 队列ID
     */
    private Long id;

    /**
     * 患者ID
     */
    private Long patientId;

    /**
     * 患者姓名
     */
    private String patientName;

    /**
     * 患者电话
     */
    private String patientPhone;

    /**
     * 挂号ID
     */
    private Long registrationId;

    /**
     * 挂号号码
     */
    private String registrationNumber;

    /**
     * 科室
     */
    private String department;

    /**
     * 队列日期
     */
    private LocalDate queueDate;

    /**
     * 队列号码
     */
    private Integer queueNumber;

    /**
     * 队列状态
     */
    private String status;

    /**
     * 优先级
     */
    private Integer priority;

    /**
     * 叫号时间
     */
    private LocalDateTime calledAt;

    /**
     * 到达时间
     */
    private LocalDateTime arrivedAt;

    /**
     * 完成时间
     */
    private LocalDateTime completedAt;

    /**
     * 叫号次数
     */
    private Integer callCount;

    /**
     * 备注
     */
    private String notes;

    /**
     * 叫号护士ID
     */
    private Long calledBy;

    /**
     * 确认到达护士ID
     */
    private Long confirmedBy;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;

    // 构造函数
    public PatientQueueResponse() {}

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPatientId() {
        return patientId;
    }

    public void setPatientId(Long patientId) {
        this.patientId = patientId;
    }

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public String getPatientPhone() {
        return patientPhone;
    }

    public void setPatientPhone(String patientPhone) {
        this.patientPhone = patientPhone;
    }

    public Long getRegistrationId() {
        return registrationId;
    }

    public void setRegistrationId(Long registrationId) {
        this.registrationId = registrationId;
    }

    public String getRegistrationNumber() {
        return registrationNumber;
    }

    public void setRegistrationNumber(String registrationNumber) {
        this.registrationNumber = registrationNumber;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public LocalDate getQueueDate() {
        return queueDate;
    }

    public void setQueueDate(LocalDate queueDate) {
        this.queueDate = queueDate;
    }

    public Integer getQueueNumber() {
        return queueNumber;
    }

    public void setQueueNumber(Integer queueNumber) {
        this.queueNumber = queueNumber;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public LocalDateTime getCalledAt() {
        return calledAt;
    }

    public void setCalledAt(LocalDateTime calledAt) {
        this.calledAt = calledAt;
    }

    public LocalDateTime getArrivedAt() {
        return arrivedAt;
    }

    public void setArrivedAt(LocalDateTime arrivedAt) {
        this.arrivedAt = arrivedAt;
    }

    public LocalDateTime getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(LocalDateTime completedAt) {
        this.completedAt = completedAt;
    }

    public Integer getCallCount() {
        return callCount;
    }

    public void setCallCount(Integer callCount) {
        this.callCount = callCount;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Long getCalledBy() {
        return calledBy;
    }

    public void setCalledBy(Long calledBy) {
        this.calledBy = calledBy;
    }

    public Long getConfirmedBy() {
        return confirmedBy;
    }

    public void setConfirmedBy(Long confirmedBy) {
        this.confirmedBy = confirmedBy;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    /**
     * 获取状态描述
     */
    public String getStatusDescription() {
        if (status == null) {
            return "";
        }
        switch (status) {
            case "WAITING":
                return "等待中";
            case "CALLED":
                return "已叫号";
            case "ARRIVED":
                return "已到达";
            case "ABSENT":
                return "未到";
            case "COMPLETED":
                return "已完成";
            default:
                return status;
        }
    }
}