package org.me.joy.clinic.entity;

import java.time.LocalDateTime;

/**
 * 可用时间段实体类
 * 用于表示医生的可预约时间段
 */
public class AvailableSlot {

    /**
     * 开始时间
     */
    private LocalDateTime startTime;

    /**
     * 结束时间
     */
    private LocalDateTime endTime;

    /**
     * 是否可用
     */
    private Boolean available;

    /**
     * 医生ID
     */
    private Long doctorId;

    /**
     * 医生姓名
     */
    private String doctorName;

    /**
     * 科室
     */
    private String department;

    /**
     * 预约类型限制
     */
    private String appointmentTypeRestriction;

    /**
     * 最大预约数量
     */
    private Integer maxAppointments;

    /**
     * 当前预约数量
     */
    private Integer currentAppointments;

    /**
     * 备注信息
     */
    private String remarks;

    // 构造函数
    public AvailableSlot() {}

    public AvailableSlot(LocalDateTime startTime, LocalDateTime endTime, Boolean available) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.available = available;
    }

    public AvailableSlot(LocalDateTime startTime, LocalDateTime endTime, Boolean available, 
                        Long doctorId, String doctorName) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.available = available;
        this.doctorId = doctorId;
        this.doctorName = doctorName;
    }

    // Getters and Setters
    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public Boolean getAvailable() {
        return available;
    }

    public void setAvailable(Boolean available) {
        this.available = available;
    }

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

    public String getAppointmentTypeRestriction() {
        return appointmentTypeRestriction;
    }

    public void setAppointmentTypeRestriction(String appointmentTypeRestriction) {
        this.appointmentTypeRestriction = appointmentTypeRestriction;
    }

    public Integer getMaxAppointments() {
        return maxAppointments;
    }

    public void setMaxAppointments(Integer maxAppointments) {
        this.maxAppointments = maxAppointments;
    }

    public Integer getCurrentAppointments() {
        return currentAppointments;
    }

    public void setCurrentAppointments(Integer currentAppointments) {
        this.currentAppointments = currentAppointments;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    /**
     * 检查时间段是否包含指定时间
     */
    public boolean contains(LocalDateTime time) {
        return time != null && startTime != null && endTime != null &&
               !time.isBefore(startTime) && time.isBefore(endTime);
    }

    /**
     * 检查是否与另一个时间段重叠
     */
    public boolean overlaps(AvailableSlot other) {
        if (other == null || startTime == null || endTime == null || 
            other.startTime == null || other.endTime == null) {
            return false;
        }
        return startTime.isBefore(other.endTime) && endTime.isAfter(other.startTime);
    }

    /**
     * 获取时间段长度（分钟）
     */
    public long getDurationMinutes() {
        if (startTime == null || endTime == null) {
            return 0;
        }
        return java.time.Duration.between(startTime, endTime).toMinutes();
    }

    /**
     * 检查是否还有可用预约位置
     */
    public boolean hasAvailableSlots() {
        if (!Boolean.TRUE.equals(available)) {
            return false;
        }
        if (maxAppointments == null) {
            return true;
        }
        int current = currentAppointments != null ? currentAppointments : 0;
        return current < maxAppointments;
    }

    /**
     * 预约一个位置
     */
    public boolean bookSlot() {
        if (!hasAvailableSlots()) {
            return false;
        }
        currentAppointments = (currentAppointments != null ? currentAppointments : 0) + 1;
        return true;
    }

    /**
     * 释放一个位置
     */
    public void releaseSlot() {
        if (currentAppointments != null && currentAppointments > 0) {
            currentAppointments--;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AvailableSlot)) return false;
        AvailableSlot that = (AvailableSlot) o;
        return startTime != null && startTime.equals(that.startTime) &&
               endTime != null && endTime.equals(that.endTime) &&
               doctorId != null && doctorId.equals(that.doctorId);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(startTime, endTime, doctorId);
    }

    @Override
    public String toString() {
        return "AvailableSlot{" +
                "startTime=" + startTime +
                ", endTime=" + endTime +
                ", available=" + available +
                ", doctorId=" + doctorId +
                ", doctorName='" + doctorName + '\'' +
                ", department='" + department + '\'' +
                ", currentAppointments=" + currentAppointments +
                ", maxAppointments=" + maxAppointments +
                '}';
    }
}