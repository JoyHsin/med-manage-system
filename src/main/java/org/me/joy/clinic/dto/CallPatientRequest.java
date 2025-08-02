package org.me.joy.clinic.dto;

import jakarta.validation.constraints.NotNull;

/**
 * 叫号请求
 */
public class CallPatientRequest {

    /**
     * 患者队列ID
     */
    @NotNull(message = "患者队列ID不能为空")
    private Long patientQueueId;

    /**
     * 叫号护士ID
     */
    @NotNull(message = "叫号护士ID不能为空")
    private Long calledBy;

    // 构造函数
    public CallPatientRequest() {}

    public CallPatientRequest(Long patientQueueId, Long calledBy) {
        this.patientQueueId = patientQueueId;
        this.calledBy = calledBy;
    }

    // Getters and Setters
    public Long getPatientQueueId() {
        return patientQueueId;
    }

    public void setPatientQueueId(Long patientQueueId) {
        this.patientQueueId = patientQueueId;
    }

    public Long getCalledBy() {
        return calledBy;
    }

    public void setCalledBy(Long calledBy) {
        this.calledBy = calledBy;
    }

    @Override
    public String toString() {
        return "CallPatientRequest{" +
                "patientQueueId=" + patientQueueId +
                ", calledBy=" + calledBy +
                '}';
    }
}