package org.me.joy.clinic.dto;

import jakarta.validation.constraints.NotNull;

/**
 * 创建账单请求DTO
 */
public class CreateBillRequest {
    
    /**
     * 患者ID
     */
    @NotNull(message = "患者ID不能为空")
    private Long patientId;
    
    /**
     * 挂号ID
     */
    private Long registrationId;
    
    /**
     * 备注
     */
    private String notes;

    // 构造函数
    public CreateBillRequest() {}

    public CreateBillRequest(Long patientId, Long registrationId) {
        this.patientId = patientId;
        this.registrationId = registrationId;
    }

    // Getters and Setters
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

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    @Override
    public String toString() {
        return "CreateBillRequest{" +
                "patientId=" + patientId +
                ", registrationId=" + registrationId +
                ", notes='" + notes + '\'' +
                '}';
    }
}