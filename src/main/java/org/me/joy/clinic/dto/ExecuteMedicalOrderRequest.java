package org.me.joy.clinic.dto;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;

public class ExecuteMedicalOrderRequest {

    @NotBlank(message = "执行备注不能为空")
    private String executionNotes;
    private String medicationName;
    private String dosage;
    private String injectionSite;

    public String getExecutionNotes() {
        return executionNotes;
    }

    public void setExecutionNotes(String executionNotes) {
        this.executionNotes = executionNotes;
    }

    public String getMedicationName() {
        return medicationName;
    }

    public void setMedicationName(String medicationName) {
        this.medicationName = medicationName;
    }

    public String getDosage() {
        return dosage;
    }

    public void setDosage(String dosage) {
        this.dosage = dosage;
    }

    public String getInjectionSite() {
        return injectionSite;
    }

    public void setInjectionSite(String injectionSite) {
        this.injectionSite = injectionSite;
    }
}