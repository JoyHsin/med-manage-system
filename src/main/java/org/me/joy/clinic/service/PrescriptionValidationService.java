package org.me.joy.clinic.service;

import org.me.joy.clinic.entity.Prescription;
import org.me.joy.clinic.entity.PrescriptionItem;
import org.me.joy.clinic.entity.Patient;

import java.util.List;

/**
 * 处方验证服务接口
 * 提供处方调剂前的各种验证功能
 */
public interface PrescriptionValidationService {

    /**
     * 验证处方的完整性和合规性
     * 
     * @param prescription 处方信息
     * @return 验证结果
     */
    PrescriptionValidationResult validatePrescription(Prescription prescription);

    /**
     * 检查药品库存是否充足
     * 
     * @param prescriptionItems 处方项目列表
     * @return 库存检查结果
     */
    StockCheckResult checkStock(List<PrescriptionItem> prescriptionItems);

    /**
     * 检查药品相互作用
     * 
     * @param prescriptionItems 处方项目列表
     * @return 药品相互作用检查结果
     */
    DrugInteractionResult checkDrugInteractions(List<PrescriptionItem> prescriptionItems);

    /**
     * 检查患者过敏史
     * 
     * @param patient 患者信息
     * @param prescriptionItems 处方项目列表
     * @return 过敏检查结果
     */
    AllergyCheckResult checkAllergies(Patient patient, List<PrescriptionItem> prescriptionItems);

    /**
     * 验证处方是否过期
     * 
     * @param prescription 处方信息
     * @return 是否过期
     */
    boolean isPrescriptionExpired(Prescription prescription);

    /**
     * 验证处方权限
     * 
     * @param prescription 处方信息
     * @param pharmacistId 药师ID
     * @return 权限验证结果
     */
    boolean validateDispensePermission(Prescription prescription, Long pharmacistId);

    /**
     * 验证特殊处方（麻醉、精神、毒性药品）
     * 
     * @param prescription 处方信息
     * @return 特殊处方验证结果
     */
    SpecialPrescriptionValidationResult validateSpecialPrescription(Prescription prescription);

    /**
     * 处方验证结果
     */
    class PrescriptionValidationResult {
        private boolean valid;
        private String result; // 通过、不通过、需要复核
        private List<String> issues;
        private List<String> warnings;

        public PrescriptionValidationResult() {}

        public PrescriptionValidationResult(boolean valid, String result) {
            this.valid = valid;
            this.result = result;
        }

        // Getters and Setters
        public boolean isValid() {
            return valid;
        }

        public void setValid(boolean valid) {
            this.valid = valid;
        }

        public String getResult() {
            return result;
        }

        public void setResult(String result) {
            this.result = result;
        }

        public List<String> getIssues() {
            return issues;
        }

        public void setIssues(List<String> issues) {
            this.issues = issues;
        }

        public List<String> getWarnings() {
            return warnings;
        }

        public void setWarnings(List<String> warnings) {
            this.warnings = warnings;
        }
    }

    /**
     * 库存检查结果
     */
    class StockCheckResult {
        private String result; // 充足、不足、部分不足
        private List<StockIssue> stockIssues;

        public StockCheckResult() {}

        public StockCheckResult(String result) {
            this.result = result;
        }

        // Getters and Setters
        public String getResult() {
            return result;
        }

        public void setResult(String result) {
            this.result = result;
        }

        public List<StockIssue> getStockIssues() {
            return stockIssues;
        }

        public void setStockIssues(List<StockIssue> stockIssues) {
            this.stockIssues = stockIssues;
        }

        public static class StockIssue {
            private Long medicineId;
            private String medicineName;
            private Integer requiredQuantity;
            private Integer availableQuantity;
            private String issue;

            public StockIssue() {}

            public StockIssue(Long medicineId, String medicineName, Integer requiredQuantity, 
                            Integer availableQuantity, String issue) {
                this.medicineId = medicineId;
                this.medicineName = medicineName;
                this.requiredQuantity = requiredQuantity;
                this.availableQuantity = availableQuantity;
                this.issue = issue;
            }

            // Getters and Setters
            public Long getMedicineId() {
                return medicineId;
            }

            public void setMedicineId(Long medicineId) {
                this.medicineId = medicineId;
            }

            public String getMedicineName() {
                return medicineName;
            }

            public void setMedicineName(String medicineName) {
                this.medicineName = medicineName;
            }

            public Integer getRequiredQuantity() {
                return requiredQuantity;
            }

            public void setRequiredQuantity(Integer requiredQuantity) {
                this.requiredQuantity = requiredQuantity;
            }

            public Integer getAvailableQuantity() {
                return availableQuantity;
            }

            public void setAvailableQuantity(Integer availableQuantity) {
                this.availableQuantity = availableQuantity;
            }

            public String getIssue() {
                return issue;
            }

            public void setIssue(String issue) {
                this.issue = issue;
            }
        }
    }

    /**
     * 药品相互作用检查结果
     */
    class DrugInteractionResult {
        private boolean hasInteractions;
        private List<DrugInteraction> interactions;

        public DrugInteractionResult() {}

        public DrugInteractionResult(boolean hasInteractions) {
            this.hasInteractions = hasInteractions;
        }

        // Getters and Setters
        public boolean isHasInteractions() {
            return hasInteractions;
        }

        public void setHasInteractions(boolean hasInteractions) {
            this.hasInteractions = hasInteractions;
        }

        public List<DrugInteraction> getInteractions() {
            return interactions;
        }

        public void setInteractions(List<DrugInteraction> interactions) {
            this.interactions = interactions;
        }

        public static class DrugInteraction {
            private String drug1;
            private String drug2;
            private String interactionType;
            private String severity;
            private String description;

            public DrugInteraction() {}

            public DrugInteraction(String drug1, String drug2, String interactionType, 
                                 String severity, String description) {
                this.drug1 = drug1;
                this.drug2 = drug2;
                this.interactionType = interactionType;
                this.severity = severity;
                this.description = description;
            }

            // Getters and Setters
            public String getDrug1() {
                return drug1;
            }

            public void setDrug1(String drug1) {
                this.drug1 = drug1;
            }

            public String getDrug2() {
                return drug2;
            }

            public void setDrug2(String drug2) {
                this.drug2 = drug2;
            }

            public String getInteractionType() {
                return interactionType;
            }

            public void setInteractionType(String interactionType) {
                this.interactionType = interactionType;
            }

            public String getSeverity() {
                return severity;
            }

            public void setSeverity(String severity) {
                this.severity = severity;
            }

            public String getDescription() {
                return description;
            }

            public void setDescription(String description) {
                this.description = description;
            }
        }
    }

    /**
     * 过敏检查结果
     */
    class AllergyCheckResult {
        private boolean hasAllergyRisk;
        private List<AllergyRisk> allergyRisks;

        public AllergyCheckResult() {}

        public AllergyCheckResult(boolean hasAllergyRisk) {
            this.hasAllergyRisk = hasAllergyRisk;
        }

        // Getters and Setters
        public boolean isHasAllergyRisk() {
            return hasAllergyRisk;
        }

        public void setHasAllergyRisk(boolean hasAllergyRisk) {
            this.hasAllergyRisk = hasAllergyRisk;
        }

        public List<AllergyRisk> getAllergyRisks() {
            return allergyRisks;
        }

        public void setAllergyRisks(List<AllergyRisk> allergyRisks) {
            this.allergyRisks = allergyRisks;
        }

        public static class AllergyRisk {
            private String medicineName;
            private String allergen;
            private String severity;
            private String description;

            public AllergyRisk() {}

            public AllergyRisk(String medicineName, String allergen, String severity, String description) {
                this.medicineName = medicineName;
                this.allergen = allergen;
                this.severity = severity;
                this.description = description;
            }

            // Getters and Setters
            public String getMedicineName() {
                return medicineName;
            }

            public void setMedicineName(String medicineName) {
                this.medicineName = medicineName;
            }

            public String getAllergen() {
                return allergen;
            }

            public void setAllergen(String allergen) {
                this.allergen = allergen;
            }

            public String getSeverity() {
                return severity;
            }

            public void setSeverity(String severity) {
                this.severity = severity;
            }

            public String getDescription() {
                return description;
            }

            public void setDescription(String description) {
                this.description = description;
            }
        }
    }

    /**
     * 特殊处方验证结果
     */
    class SpecialPrescriptionValidationResult {
        private boolean valid;
        private boolean requiresSpecialApproval;
        private List<String> requirements;
        private List<String> warnings;

        public SpecialPrescriptionValidationResult() {}

        public SpecialPrescriptionValidationResult(boolean valid, boolean requiresSpecialApproval) {
            this.valid = valid;
            this.requiresSpecialApproval = requiresSpecialApproval;
        }

        // Getters and Setters
        public boolean isValid() {
            return valid;
        }

        public void setValid(boolean valid) {
            this.valid = valid;
        }

        public boolean isRequiresSpecialApproval() {
            return requiresSpecialApproval;
        }

        public void setRequiresSpecialApproval(boolean requiresSpecialApproval) {
            this.requiresSpecialApproval = requiresSpecialApproval;
        }

        public List<String> getRequirements() {
            return requirements;
        }

        public void setRequirements(List<String> requirements) {
            this.requirements = requirements;
        }

        public List<String> getWarnings() {
            return warnings;
        }

        public void setWarnings(List<String> warnings) {
            this.warnings = warnings;
        }
    }
}