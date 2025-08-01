package org.me.joy.clinic.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import jakarta.validation.constraints.*;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * 排班实体类
 * 记录医护人员的排班信息
 */
@TableName("schedules")
public class Schedule extends BaseEntity {

    /**
     * 员工ID
     */
    @NotNull(message = "员工ID不能为空")
    private Long staffId;

    /**
     * 排班日期
     */
    @NotNull(message = "排班日期不能为空")
    private LocalDate scheduleDate;

    /**
     * 班次类型
     */
    @NotBlank(message = "班次类型不能为空")
    @Pattern(regexp = "^(白班|夜班|中班|全天|休息)$", message = "班次类型只能是白班、夜班、中班、全天或休息")
    private String shiftType;

    /**
     * 开始时间
     */
    private LocalTime startTime;

    /**
     * 结束时间
     */
    private LocalTime endTime;

    /**
     * 工作地点
     */
    @Size(max = 100, message = "工作地点长度不能超过100个字符")
    private String workLocation;

    /**
     * 科室
     */
    @Size(max = 100, message = "科室名称长度不能超过100个字符")
    private String department;

    /**
     * 排班状态
     */
    @NotBlank(message = "排班状态不能为空")
    @Pattern(regexp = "^(正常|请假|调班|加班|取消)$", message = "排班状态只能是正常、请假、调班、加班或取消")
    private String status = "正常";

    /**
     * 是否为加班
     */
    private Boolean isOvertime = false;

    /**
     * 备注信息
     */
    @Size(max = 500, message = "备注信息长度不能超过500个字符")
    private String remarks;

    /**
     * 创建人员ID
     */
    private Long createdBy;

    /**
     * 最后更新人员ID
     */
    private Long lastUpdatedBy;

    // 构造函数
    public Schedule() {}

    public Schedule(Long staffId, LocalDate scheduleDate, String shiftType) {
        this.staffId = staffId;
        this.scheduleDate = scheduleDate;
        this.shiftType = shiftType;
        this.setDefaultTimes();
    }

    /**
     * 根据班次类型设置默认时间
     */
    private void setDefaultTimes() {
        switch (shiftType) {
            case "白班":
                this.startTime = LocalTime.of(8, 0);
                this.endTime = LocalTime.of(17, 0);
                break;
            case "夜班":
                this.startTime = LocalTime.of(20, 0);
                this.endTime = LocalTime.of(8, 0);
                break;
            case "中班":
                this.startTime = LocalTime.of(14, 0);
                this.endTime = LocalTime.of(22, 0);
                break;
            case "全天":
                this.startTime = LocalTime.of(8, 0);
                this.endTime = LocalTime.of(20, 0);
                break;
            case "休息":
                this.startTime = null;
                this.endTime = null;
                break;
        }
    }

    // Getters and Setters
    public Long getStaffId() {
        return staffId;
    }

    public void setStaffId(Long staffId) {
        this.staffId = staffId;
    }

    public LocalDate getScheduleDate() {
        return scheduleDate;
    }

    public void setScheduleDate(LocalDate scheduleDate) {
        this.scheduleDate = scheduleDate;
    }

    public String getShiftType() {
        return shiftType;
    }

    public void setShiftType(String shiftType) {
        this.shiftType = shiftType;
        this.setDefaultTimes();
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    public String getWorkLocation() {
        return workLocation;
    }

    public void setWorkLocation(String workLocation) {
        this.workLocation = workLocation;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Boolean getIsOvertime() {
        return isOvertime;
    }

    public void setIsOvertime(Boolean isOvertime) {
        this.isOvertime = isOvertime;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public Long getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Long createdBy) {
        this.createdBy = createdBy;
    }

    public Long getLastUpdatedBy() {
        return lastUpdatedBy;
    }

    public void setLastUpdatedBy(Long lastUpdatedBy) {
        this.lastUpdatedBy = lastUpdatedBy;
    }

    /**
     * 计算工作时长（小时）
     */
    public Double getWorkHours() {
        if (startTime == null || endTime == null || "休息".equals(shiftType)) {
            return 0.0;
        }
        
        // 处理跨天的情况（如夜班）
        if (endTime.isBefore(startTime)) {
            return (double) (24 * 60 - startTime.toSecondOfDay() / 60 + endTime.toSecondOfDay() / 60) / 60;
        } else {
            return (double) (endTime.toSecondOfDay() - startTime.toSecondOfDay()) / 3600;
        }
    }

    /**
     * 检查是否为休息日
     */
    public boolean isRestDay() {
        return "休息".equals(shiftType);
    }

    /**
     * 检查排班是否有效
     */
    public boolean isValid() {
        return "正常".equals(status) || "加班".equals(status);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Schedule)) return false;
        Schedule schedule = (Schedule) o;
        return staffId != null && staffId.equals(schedule.staffId) &&
               scheduleDate != null && scheduleDate.equals(schedule.scheduleDate);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(staffId, scheduleDate);
    }

    @Override
    public String toString() {
        return "Schedule{" +
                "id=" + getId() +
                ", staffId=" + staffId +
                ", scheduleDate=" + scheduleDate +
                ", shiftType='" + shiftType + '\'' +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", status='" + status + '\'' +
                '}';
    }
}