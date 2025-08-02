package org.me.joy.clinic.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 患者队列实体
 * 用于管理分诊叫号的患者队列信息
 */
@TableName("patient_queue")
public class PatientQueue extends BaseEntity {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 患者ID
     */
    @NotNull(message = "患者ID不能为空")
    private Long patientId;

    /**
     * 挂号ID
     */
    @NotNull(message = "挂号ID不能为空")
    private Long registrationId;

    /**
     * 队列日期
     */
    @NotNull(message = "队列日期不能为空")
    private LocalDate queueDate;

    /**
     * 队列号码
     */
    @NotNull(message = "队列号码不能为空")
    private Integer queueNumber;

    /**
     * 队列状态
     * WAITING - 等待中
     * CALLED - 已叫号
     * ARRIVED - 已到达
     * ABSENT - 未到
     * COMPLETED - 已完成
     */
    @NotNull(message = "队列状态不能为空")
    @Pattern(regexp = "^(WAITING|CALLED|ARRIVED|ABSENT|COMPLETED)$", message = "队列状态只能是WAITING、CALLED、ARRIVED、ABSENT或COMPLETED")
    private String status = "WAITING";

    /**
     * 优先级 (1-最高, 5-最低)
     */
    @NotNull(message = "优先级不能为空")
    private Integer priority = 3;

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
    @NotNull(message = "叫号次数不能为空")
    private Integer callCount = 0;

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

    // 构造函数
    public PatientQueue() {}

    public PatientQueue(Long patientId, Long registrationId, LocalDate queueDate, Integer queueNumber) {
        this.patientId = patientId;
        this.registrationId = registrationId;
        this.queueDate = queueDate;
        this.queueNumber = queueNumber;
        this.status = "WAITING";
        this.priority = 3;
        this.callCount = 0;
    }

    // Getters and Setters
    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public Long getPatientId() {
        return patientId;
    }

    public void setPatientId(Long patientId) {
        this.patientId = patientId;
    }

    public Long getRegistrationId() {
        return registrationId;
    }

    public void setRegistrationId(Long registrationId) {
        this.registrationId = registrationId;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PatientQueue)) return false;
        PatientQueue that = (PatientQueue) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "PatientQueue{" +
                "id=" + id +
                ", patientId=" + patientId +
                ", registrationId=" + registrationId +
                ", queueDate=" + queueDate +
                ", queueNumber=" + queueNumber +
                ", status='" + status + '\'' +
                ", priority=" + priority +
                ", callCount=" + callCount +
                '}';
    }
}