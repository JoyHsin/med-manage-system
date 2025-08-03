package org.me.joy.clinic.service.impl;

import org.me.joy.clinic.entity.*;
import org.me.joy.clinic.mapper.*;
import org.me.joy.clinic.service.PrescriptionValidationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 处方验证服务实现类
 */
@Service
public class PrescriptionValidationServiceImpl implements PrescriptionValidationService {

    @Autowired
    private InventoryLevelMapper inventoryLevelMapper;

    @Autowired
    private MedicineMapper medicineMapper;

    @Autowired
    private AllergyHistoryMapper allergyHistoryMapper;

    @Autowired
    private PrescriptionItemMapper prescriptionItemMapper;

    @Autowired
    private UserMapper userMapper;

    @Override
    public PrescriptionValidationResult validatePrescription(Prescription prescription) {
        PrescriptionValidationResult result = new PrescriptionValidationResult();
        List<String> issues = new ArrayList<>();
        List<String> warnings = new ArrayList<>();

        // 检查处方基本信息
        if (prescription == null) {
            issues.add("处方信息不能为空");
            result.setValid(false);
            result.setResult("不通过");
            result.setIssues(issues);
            return result;
        }

        // 检查处方是否过期
        if (isPrescriptionExpired(prescription)) {
            issues.add("处方已过期，无法调剂");
        }

        // 检查处方状态
        if (!"已审核".equals(prescription.getStatus())) {
            issues.add("处方未经审核，无法调剂");
        }

        // 检查处方类型
        if (prescription.needsSpecialReview()) {
            warnings.add("特殊处方需要额外审核");
        }

        // 检查处方项目
        List<PrescriptionItem> items = prescriptionItemMapper.findByPrescriptionId(prescription.getId());
        if (items == null || items.isEmpty()) {
            issues.add("处方无药品项目");
        } else {
            // 检查每个药品项目
            for (PrescriptionItem item : items) {
                if (item.getQuantity() == null || item.getQuantity() <= 0) {
                    issues.add("药品 " + item.getMedicineName() + " 数量无效");
                }
                if (item.getUnitPrice() == null || item.getUnitPrice().compareTo(java.math.BigDecimal.ZERO) <= 0) {
                    issues.add("药品 " + item.getMedicineName() + " 价格无效");
                }
            }
        }

        // 设置验证结果
        if (!issues.isEmpty()) {
            result.setValid(false);
            result.setResult("不通过");
        } else if (!warnings.isEmpty()) {
            result.setValid(true);
            result.setResult("需要复核");
        } else {
            result.setValid(true);
            result.setResult("通过");
        }

        result.setIssues(issues);
        result.setWarnings(warnings);
        return result;
    }

    @Override
    public StockCheckResult checkStock(List<PrescriptionItem> prescriptionItems) {
        StockCheckResult result = new StockCheckResult();
        List<StockCheckResult.StockIssue> stockIssues = new ArrayList<>();

        if (prescriptionItems == null || prescriptionItems.isEmpty()) {
            result.setResult("充足");
            return result;
        }

        boolean hasStockIssues = false;
        boolean hasPartialIssues = false;

        for (PrescriptionItem item : prescriptionItems) {
            // 查询当前库存
            Integer totalStock = inventoryLevelMapper.getTotalStockByMedicine(item.getMedicineId());
            
            if (totalStock == null || totalStock == 0) {
                stockIssues.add(new StockCheckResult.StockIssue(
                    item.getMedicineId(),
                    item.getMedicineName(),
                    item.getQuantity(),
                    0,
                    "无库存记录"
                ));
                hasStockIssues = true;
            } else {
                Integer currentStock = totalStock;
                Integer requiredQuantity = item.getQuantity();

                if (currentStock < requiredQuantity) {
                    stockIssues.add(new StockCheckResult.StockIssue(
                        item.getMedicineId(),
                        item.getMedicineName(),
                        requiredQuantity,
                        currentStock,
                        currentStock == 0 ? "无库存" : "库存不足"
                    ));
                    
                    if (currentStock == 0) {
                        hasStockIssues = true;
                    } else {
                        hasPartialIssues = true;
                    }
                }
            }
        }

        // 设置检查结果
        if (hasStockIssues) {
            result.setResult("不足");
        } else if (hasPartialIssues) {
            result.setResult("部分不足");
        } else {
            result.setResult("充足");
        }

        result.setStockIssues(stockIssues);
        return result;
    }

    @Override
    public DrugInteractionResult checkDrugInteractions(List<PrescriptionItem> prescriptionItems) {
        DrugInteractionResult result = new DrugInteractionResult();
        List<DrugInteractionResult.DrugInteraction> interactions = new ArrayList<>();

        if (prescriptionItems == null || prescriptionItems.size() < 2) {
            result.setHasInteractions(false);
            result.setInteractions(interactions);
            return result;
        }

        // 简化的药品相互作用检查逻辑
        // 在实际应用中，这里应该连接专业的药品相互作用数据库
        List<String> medicineNames = prescriptionItems.stream()
            .map(PrescriptionItem::getMedicineName)
            .collect(Collectors.toList());

        // 检查常见的药品相互作用
        for (int i = 0; i < medicineNames.size(); i++) {
            for (int j = i + 1; j < medicineNames.size(); j++) {
                String drug1 = medicineNames.get(i);
                String drug2 = medicineNames.get(j);
                
                // 示例：检查一些常见的相互作用
                if (hasKnownInteraction(drug1, drug2)) {
                    interactions.add(new DrugInteractionResult.DrugInteraction(
                        drug1,
                        drug2,
                        "药效相互作用",
                        "中等",
                        "可能影响药效，建议监测"
                    ));
                }
            }
        }

        result.setHasInteractions(!interactions.isEmpty());
        result.setInteractions(interactions);
        return result;
    }

    @Override
    public AllergyCheckResult checkAllergies(Patient patient, List<PrescriptionItem> prescriptionItems) {
        AllergyCheckResult result = new AllergyCheckResult();
        List<AllergyCheckResult.AllergyRisk> allergyRisks = new ArrayList<>();

        if (patient == null || prescriptionItems == null || prescriptionItems.isEmpty()) {
            result.setHasAllergyRisk(false);
            result.setAllergyRisks(allergyRisks);
            return result;
        }

        // 查询患者过敏史
        List<AllergyHistory> allergies = allergyHistoryMapper.findByPatientId(patient.getId());
        
        if (allergies == null || allergies.isEmpty()) {
            result.setHasAllergyRisk(false);
            result.setAllergyRisks(allergyRisks);
            return result;
        }

        // 检查处方药品是否与过敏史冲突
        for (PrescriptionItem item : prescriptionItems) {
            for (AllergyHistory allergy : allergies) {
                if (isAllergyRisk(item.getMedicineName(), allergy.getAllergen())) {
                    allergyRisks.add(new AllergyCheckResult.AllergyRisk(
                        item.getMedicineName(),
                        allergy.getAllergen(),
                        allergy.getSeverity(),
                        "患者对 " + allergy.getAllergen() + " 过敏，使用 " + item.getMedicineName() + " 可能有风险"
                    ));
                }
            }
        }

        result.setHasAllergyRisk(!allergyRisks.isEmpty());
        result.setAllergyRisks(allergyRisks);
        return result;
    }

    @Override
    public boolean isPrescriptionExpired(Prescription prescription) {
        if (prescription == null || prescription.getPrescribedAt() == null || 
            prescription.getValidityDays() == null) {
            return false;
        }

        LocalDateTime expiryTime = prescription.getPrescribedAt()
            .plusDays(prescription.getValidityDays());
        return LocalDateTime.now().isAfter(expiryTime);
    }

    @Override
    public boolean validateDispensePermission(Prescription prescription, Long pharmacistId) {
        if (prescription == null || pharmacistId == null) {
            return false;
        }

        // 查询药师信息
        User pharmacist = userMapper.selectById(pharmacistId);
        if (pharmacist == null || !pharmacist.getEnabled()) {
            return false;
        }

        // 检查药师是否有调剂权限
        // 这里应该检查用户的角色和权限
        // 简化实现：假设所有启用的用户都有基本调剂权限
        
        // 对于特殊处方，需要特殊权限
        if (prescription.needsSpecialReview()) {
            // 检查是否有特殊处方调剂权限
            // 这里应该查询用户的具体权限
            return hasSpecialDispensePermission(pharmacistId);
        }

        return true;
    }

    @Override
    public SpecialPrescriptionValidationResult validateSpecialPrescription(Prescription prescription) {
        SpecialPrescriptionValidationResult result = new SpecialPrescriptionValidationResult();
        List<String> requirements = new ArrayList<>();
        List<String> warnings = new ArrayList<>();

        if (prescription == null || !prescription.needsSpecialReview()) {
            result.setValid(true);
            result.setRequiresSpecialApproval(false);
            return result;
        }

        String prescriptionType = prescription.getPrescriptionType();
        
        switch (prescriptionType) {
            case "麻醉处方":
                requirements.add("需要麻醉药品处方权医师开具");
                requirements.add("需要专用处方笺");
                requirements.add("需要双人核对");
                warnings.add("严格按照麻醉药品管理规定执行");
                break;
                
            case "精神药品处方":
                requirements.add("需要精神药品处方权医师开具");
                requirements.add("需要专用处方笺");
                requirements.add("需要登记患者身份信息");
                warnings.add("严格按照精神药品管理规定执行");
                break;
                
            case "毒性药品处方":
                requirements.add("需要毒性药品处方权医师开具");
                requirements.add("需要专用处方笺");
                requirements.add("需要双人核对");
                requirements.add("需要详细记录用药情况");
                warnings.add("严格按照毒性药品管理规定执行");
                break;
        }

        result.setValid(true);
        result.setRequiresSpecialApproval(true);
        result.setRequirements(requirements);
        result.setWarnings(warnings);
        return result;
    }

    /**
     * 检查两个药品是否有已知的相互作用
     */
    private boolean hasKnownInteraction(String drug1, String drug2) {
        // 简化的相互作用检查逻辑
        // 在实际应用中，这里应该查询专业的药品相互作用数据库
        
        // 示例：一些常见的相互作用检查
        if ((drug1.contains("阿司匹林") && drug2.contains("华法林")) ||
            (drug1.contains("华法林") && drug2.contains("阿司匹林"))) {
            return true;
        }
        
        if ((drug1.contains("地高辛") && drug2.contains("维拉帕米")) ||
            (drug1.contains("维拉帕米") && drug2.contains("地高辛"))) {
            return true;
        }
        
        return false;
    }

    /**
     * 检查药品是否与过敏原有关联
     */
    private boolean isAllergyRisk(String medicineName, String allergen) {
        if (medicineName == null || allergen == null) {
            return false;
        }
        
        // 简化的过敏风险检查
        // 在实际应用中，这里应该有更完善的过敏原-药品关联数据库
        
        // 检查药品名称是否包含过敏原
        if (medicineName.toLowerCase().contains(allergen.toLowerCase())) {
            return true;
        }
        
        // 检查一些常见的过敏关联
        if ("青霉素".equals(allergen) && medicineName.contains("青霉素")) {
            return true;
        }
        
        if ("磺胺".equals(allergen) && medicineName.contains("磺胺")) {
            return true;
        }
        
        return false;
    }

    /**
     * 检查用户是否有特殊处方调剂权限
     */
    private boolean hasSpecialDispensePermission(Long pharmacistId) {
        // 简化实现：假设所有药师都有特殊处方调剂权限
        // 在实际应用中，这里应该查询用户的具体权限
        return true;
    }
}