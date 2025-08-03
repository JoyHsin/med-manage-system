package org.me.joy.clinic.service.impl;

import org.me.joy.clinic.entity.*;
import org.me.joy.clinic.exception.BusinessException;
import org.me.joy.clinic.mapper.*;
import org.me.joy.clinic.service.PharmacyService;
import org.me.joy.clinic.service.PrescriptionValidationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 药房服务实现类
 */
@Service
@Transactional
public class PharmacyServiceImpl implements PharmacyService {

    @Autowired
    private PrescriptionMapper prescriptionMapper;

    @Autowired
    private PrescriptionItemMapper prescriptionItemMapper;

    @Autowired
    private DispenseRecordMapper dispenseRecordMapper;

    @Autowired
    private DispenseItemMapper dispenseItemMapper;

    @Autowired
    private PatientMapper patientMapper;

    @Autowired
    private MedicineMapper medicineMapper;

    @Autowired
    private MedicalRecordMapper medicalRecordMapper;

    @Autowired
    private InventoryLevelMapper inventoryLevelMapper;

    @Autowired
    private StockTransactionMapper stockTransactionMapper;

    @Autowired
    private PrescriptionValidationService prescriptionValidationService;

    @Override
    public DispenseRecord startDispensing(Long prescriptionId, Long pharmacistId, String pharmacistName) {
        // 验证处方
        Prescription prescription = prescriptionMapper.selectById(prescriptionId);
        if (prescription == null) {
            throw new BusinessException("PRESCRIPTION_NOT_FOUND", "处方不存在");
        }

        // 检查处方是否可以调剂
        DispenseEligibilityResult eligibility = checkDispenseEligibility(prescriptionId);
        if (!eligibility.isEligible()) {
            throw new BusinessException("PRESCRIPTION_NOT_ELIGIBLE", "处方不符合调剂条件：" + eligibility.getReason());
        }

        // 检查是否已有调剂记录
        if (dispenseRecordMapper.existsByPrescriptionId(prescriptionId)) {
            throw new BusinessException("DISPENSE_RECORD_EXISTS", "该处方已有调剂记录");
        }

        // 获取病历信息来获取患者ID
        MedicalRecord medicalRecord = medicalRecordMapper.selectById(prescription.getMedicalRecordId());
        if (medicalRecord == null) {
            throw new BusinessException("MEDICAL_RECORD_NOT_FOUND", "病历信息不存在");
        }
        
        // 获取患者信息
        Patient patient = patientMapper.selectById(medicalRecord.getPatientId());
        if (patient == null) {
            throw new BusinessException("PATIENT_NOT_FOUND", "患者信息不存在");
        }

        // 创建调剂记录
        DispenseRecord dispenseRecord = new DispenseRecord(
            prescriptionId,
            prescription.getPrescriptionNumber(),
            patient.getId(),
            patient.getName(),
            pharmacistId,
            pharmacistName
        );

        // 执行处方验证
        PrescriptionValidationService.PrescriptionValidationResult validationResult = 
            prescriptionValidationService.validatePrescription(prescription);
        dispenseRecord.setValidationResult(validationResult.getResult());

        // 获取处方项目
        List<PrescriptionItem> prescriptionItems = prescriptionItemMapper.findByPrescriptionId(prescriptionId);
        
        // 执行库存检查
        PrescriptionValidationService.StockCheckResult stockResult = 
            prescriptionValidationService.checkStock(prescriptionItems);
        dispenseRecord.setStockCheckResult(stockResult.getResult());

        // 执行药品相互作用检查
        PrescriptionValidationService.DrugInteractionResult interactionResult = 
            prescriptionValidationService.checkDrugInteractions(prescriptionItems);
        if (interactionResult.isHasInteractions()) {
            String interactionInfo = interactionResult.getInteractions().stream()
                .map(interaction -> interaction.getDrug1() + " 与 " + interaction.getDrug2() + " 有相互作用")
                .collect(Collectors.joining("; "));
            dispenseRecord.setDrugInteractionCheck("发现药物相互作用警告: " + interactionInfo);
        } else {
            dispenseRecord.setDrugInteractionCheck("无药物相互作用");
        }

        // 执行过敏检查
        PrescriptionValidationService.AllergyCheckResult allergyResult = 
            prescriptionValidationService.checkAllergies(patient, prescriptionItems);
        if (allergyResult.isHasAllergyRisk()) {
            String allergyInfo = allergyResult.getAllergyRisks().stream()
                .map(risk -> risk.getMedicineName() + " 可能引起过敏反应")
                .collect(Collectors.joining("; "));
            dispenseRecord.setAllergyCheck("发现过敏风险: " + allergyInfo);
        } else {
            dispenseRecord.setAllergyCheck("无过敏风险");
        }

        // 设置处方总金额
        dispenseRecord.setTotalAmount(prescription.getTotalAmount());
        dispenseRecord.setActualAmount(prescription.getTotalAmount());

        // 保存调剂记录
        dispenseRecordMapper.insert(dispenseRecord);

        // 创建调剂项目明细
        createDispenseItems(dispenseRecord, prescriptionItems);

        // 开始调剂
        dispenseRecord.startDispensing();
        dispenseRecordMapper.updateById(dispenseRecord);

        return dispenseRecord;
    }

    @Override
    public DispenseItem dispenseMedicineItem(DispenseMedicineRequest request) {
        // 获取调剂项目
        DispenseItem dispenseItem = dispenseItemMapper.selectById(request.getDispenseItemId());
        if (dispenseItem == null) {
            throw new BusinessException("DISPENSE_ITEM_NOT_FOUND", "调剂项目不存在");
        }

        if (!"待调剂".equals(dispenseItem.getStatus())) {
            throw new BusinessException("DISPENSE_ITEM_INVALID_STATUS", "调剂项目状态不正确，无法调剂");
        }

        // 检查库存
        Integer totalStock = inventoryLevelMapper.getTotalStockByMedicine(request.getMedicineId());
        if (totalStock == null || totalStock < request.getDispensedQuantity()) {
            throw new BusinessException("INSUFFICIENT_STOCK", "库存不足，无法调剂");
        }

        // 获取最早过期的批次进行调剂
        InventoryLevel inventory = inventoryLevelMapper.getEarliestExpiringBatch(request.getMedicineId());
        if (inventory == null || inventory.getCurrentStock() < request.getDispensedQuantity()) {
            throw new BusinessException("INSUFFICIENT_STOCK", "库存不足，无法调剂");
        }

        // 更新调剂项目信息
        dispenseItem.setDispensedQuantity(request.getDispensedQuantity());
        dispenseItem.setBatchNumber(request.getBatchNumber());
        dispenseItem.setExpiryDate(request.getExpiryDate());
        dispenseItem.setDispenseNotes(request.getDispenseNotes());
        
        // 调剂药品
        dispenseItem.dispense(
            request.getDispensedBy(),
            request.getDispensedByName(),
            request.getBatchNumber(),
            request.getExpiryDate(),
            inventory.getCurrentStock(),
            inventory.getCurrentStock() - request.getDispensedQuantity()
        );

        // 更新库存
        updateStock(request.getMedicineId(), -request.getDispensedQuantity(), 
                   "处方调剂", "调剂记录ID: " + dispenseItem.getDispenseRecordId());

        // 保存调剂项目
        dispenseItemMapper.updateById(dispenseItem);

        return dispenseItem;
    }

    @Override
    public DispenseRecord completeDispensing(Long dispenseRecordId) {
        DispenseRecord dispenseRecord = dispenseRecordMapper.selectById(dispenseRecordId);
        if (dispenseRecord == null) {
            throw new BusinessException("DISPENSE_RECORD_NOT_FOUND", "调剂记录不存在");
        }

        if (!"调剂中".equals(dispenseRecord.getStatus())) {
            throw new BusinessException("DISPENSE_RECORD_INVALID_STATUS", "调剂记录状态不正确，无法完成调剂");
        }

        // 检查所有调剂项目是否已完成
        List<DispenseItem> items = dispenseItemMapper.findByDispenseRecordId(dispenseRecordId);
        boolean allDispensed = items.stream().allMatch(item -> "已调剂".equals(item.getStatus()));
        
        if (!allDispensed) {
            throw new BusinessException("INCOMPLETE_DISPENSE_ITEMS", "存在未完成调剂的药品项目");
        }

        // 计算实际调剂金额
        BigDecimal actualAmount = items.stream()
            .map(DispenseItem::getSubtotal)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        dispenseRecord.setActualAmount(actualAmount);

        // 完成调剂
        dispenseRecord.completeDispensing();
        dispenseRecordMapper.updateById(dispenseRecord);

        // 更新处方状态
        Prescription prescription = prescriptionMapper.selectById(dispenseRecord.getPrescriptionId());
        if (prescription != null) {
            prescription.dispense(dispenseRecord.getPharmacistId());
            prescriptionMapper.updateById(prescription);
        }

        return dispenseRecord;
    }

    @Override
    public DispenseRecord deliverMedicine(Long dispenseRecordId, Long dispensingPharmacistId, 
                                         String dispensingPharmacistName, String deliveryNotes) {
        DispenseRecord dispenseRecord = dispenseRecordMapper.selectById(dispenseRecordId);
        if (dispenseRecord == null) {
            throw new BusinessException("DISPENSE_RECORD_NOT_FOUND", "调剂记录不存在");
        }

        if (!dispenseRecord.canBeDelivered()) {
            throw new BusinessException("DISPENSE_RECORD_NOT_DELIVERABLE", "调剂记录不符合发药条件");
        }

        // 设置发药信息
        dispenseRecord.setDeliveryNotes(deliveryNotes);
        
        // 发药
        dispenseRecord.deliver(dispensingPharmacistId, dispensingPharmacistName);
        dispenseRecordMapper.updateById(dispenseRecord);

        // 更新处方状态
        Prescription prescription = prescriptionMapper.selectById(dispenseRecord.getPrescriptionId());
        if (prescription != null) {
            prescription.deliver();
            prescriptionMapper.updateById(prescription);
        }

        return dispenseRecord;
    }

    @Override
    public DispenseRecord returnPrescription(Long dispenseRecordId, String reason) {
        DispenseRecord dispenseRecord = dispenseRecordMapper.selectById(dispenseRecordId);
        if (dispenseRecord == null) {
            throw new BusinessException("DISPENSE_RECORD_NOT_FOUND", "调剂记录不存在");
        }

        if ("已发药".equals(dispenseRecord.getStatus())) {
            throw new BusinessException("PRESCRIPTION_ALREADY_DELIVERED", "已发药的处方无法退回");
        }

        // 退回库存
        List<DispenseItem> items = dispenseItemMapper.findByDispenseRecordId(dispenseRecordId);
        for (DispenseItem item : items) {
            if ("已调剂".equals(item.getStatus()) && item.getDispensedQuantity() != null) {
                // 退回库存
                updateStock(item.getMedicineId(), item.getDispensedQuantity(), 
                           "处方退回", "调剂记录ID: " + dispenseRecordId);
                
                // 更新调剂项目状态
                item.returnItem(reason);
                dispenseItemMapper.updateById(item);
            }
        }

        // 退回处方
        dispenseRecord.returnPrescription(reason);
        dispenseRecordMapper.updateById(dispenseRecord);

        return dispenseRecord;
    }

    @Override
    public DispenseRecord cancelDispensing(Long dispenseRecordId, String reason) {
        DispenseRecord dispenseRecord = dispenseRecordMapper.selectById(dispenseRecordId);
        if (dispenseRecord == null) {
            throw new BusinessException("DISPENSE_RECORD_NOT_FOUND", "调剂记录不存在");
        }

        if ("已发药".equals(dispenseRecord.getStatus())) {
            throw new BusinessException("PRESCRIPTION_ALREADY_DELIVERED", "已发药的处方无法取消");
        }

        // 退回库存
        List<DispenseItem> items = dispenseItemMapper.findByDispenseRecordId(dispenseRecordId);
        for (DispenseItem item : items) {
            if ("已调剂".equals(item.getStatus()) && item.getDispensedQuantity() != null) {
                // 退回库存
                updateStock(item.getMedicineId(), item.getDispensedQuantity(), 
                           "调剂取消", "调剂记录ID: " + dispenseRecordId);
            }
        }

        // 取消调剂
        dispenseRecord.cancel(reason);
        dispenseRecordMapper.updateById(dispenseRecord);

        return dispenseRecord;
    }

    @Override
    public DispenseItem substituteMedicine(Long dispenseItemId, Long newMedicineId, String reason) {
        DispenseItem dispenseItem = dispenseItemMapper.selectById(dispenseItemId);
        if (dispenseItem == null) {
            throw new BusinessException("DISPENSE_ITEM_NOT_FOUND", "调剂项目不存在");
        }

        if (!"待调剂".equals(dispenseItem.getStatus())) {
            throw new BusinessException("DISPENSE_ITEM_INVALID_STATUS", "只能替代待调剂的药品项目");
        }

        // 获取新药品信息
        Medicine newMedicine = medicineMapper.selectById(newMedicineId);
        if (newMedicine == null) {
            throw new BusinessException("SUBSTITUTE_MEDICINE_NOT_FOUND", "替代药品不存在");
        }

        // 检查新药品库存
        Integer totalStock = inventoryLevelMapper.getTotalStockByMedicine(newMedicineId);
        if (totalStock == null || totalStock < dispenseItem.getPrescribedQuantity()) {
            throw new BusinessException("SUBSTITUTE_MEDICINE_INSUFFICIENT_STOCK", "替代药品库存不足");
        }

        // 替代药品
        dispenseItem.substitute(newMedicineId, newMedicine.getName(), reason);
        dispenseItem.setSpecification(newMedicine.getSpecification());
        dispenseItem.setUnitPrice(newMedicine.getSellingPrice());
        dispenseItem.calculateSubtotal();

        dispenseItemMapper.updateById(dispenseItem);

        return dispenseItem;
    }

    @Override
    public DispenseRecord reviewDispensing(Long dispenseRecordId, Long reviewPharmacistId, 
                                          String reviewPharmacistName, String comments) {
        DispenseRecord dispenseRecord = dispenseRecordMapper.selectById(dispenseRecordId);
        if (dispenseRecord == null) {
            throw new BusinessException("DISPENSE_RECORD_NOT_FOUND", "调剂记录不存在");
        }

        // 复核调剂
        dispenseRecord.review(reviewPharmacistId, reviewPharmacistName, comments);
        dispenseRecordMapper.updateById(dispenseRecord);

        return dispenseRecord;
    }

    @Override
    public List<Prescription> getPendingPrescriptions() {
        return prescriptionMapper.findPendingDispense();
    }

    @Override
    public List<DispenseRecord> getInProgressDispenses() {
        return dispenseRecordMapper.findInProgress();
    }

    @Override
    public List<DispenseRecord> getReadyForDelivery() {
        return dispenseRecordMapper.findReadyForDelivery();
    }

    @Override
    public List<DispenseRecord> getNeedingReview() {
        return dispenseRecordMapper.findNeedingReview();
    }

    @Override
    public DispenseRecord getDispenseRecordByPrescriptionId(Long prescriptionId) {
        List<DispenseRecord> records = dispenseRecordMapper.findByPrescriptionId(prescriptionId);
        return records.isEmpty() ? null : records.get(0);
    }

    @Override
    public DispenseRecord getDispenseRecordWithItems(Long dispenseRecordId) {
        DispenseRecord dispenseRecord = dispenseRecordMapper.selectById(dispenseRecordId);
        if (dispenseRecord != null) {
            List<DispenseItem> items = dispenseItemMapper.findByDispenseRecordId(dispenseRecordId);
            dispenseRecord.setDispenseItems(items);
        }
        return dispenseRecord;
    }

    @Override
    public List<DispenseRecord> getDispenseRecordsByPharmacist(Long pharmacistId) {
        return dispenseRecordMapper.findByPharmacistId(pharmacistId);
    }

    @Override
    public List<DispenseRecord> getDispenseRecordsByPatient(Long patientId) {
        return dispenseRecordMapper.findByPatientId(patientId);
    }

    @Override
    public DispenseEligibilityResult checkDispenseEligibility(Long prescriptionId) {
        DispenseEligibilityResult result = new DispenseEligibilityResult();
        List<String> issues = new ArrayList<>();
        List<String> warnings = new ArrayList<>();

        // 获取处方信息
        Prescription prescription = prescriptionMapper.selectById(prescriptionId);
        if (prescription == null) {
            result.setEligible(false);
            result.setReason("处方不存在");
            return result;
        }

        // 检查处方状态
        if (!"已审核".equals(prescription.getStatus())) {
            issues.add("处方未经审核");
        }

        // 检查处方是否过期
        if (prescription.isExpired()) {
            issues.add("处方已过期");
        }

        // 检查是否已有调剂记录
        if (dispenseRecordMapper.existsByPrescriptionId(prescriptionId)) {
            issues.add("处方已有调剂记录");
        }

        // 检查处方项目
        List<PrescriptionItem> items = prescriptionItemMapper.findByPrescriptionId(prescriptionId);
        if (items == null || items.isEmpty()) {
            issues.add("处方无药品项目");
        }

        // 检查特殊处方
        if (prescription.needsSpecialReview()) {
            warnings.add("特殊处方需要额外审核");
        }

        // 设置结果
        if (!issues.isEmpty()) {
            result.setEligible(false);
            result.setReason(String.join("; ", issues));
        } else {
            result.setEligible(true);
            result.setReason("符合调剂条件");
        }

        result.setIssues(issues);
        result.setWarnings(warnings);
        return result;
    }

    /**
     * 创建调剂项目明细
     */
    private void createDispenseItems(DispenseRecord dispenseRecord, List<PrescriptionItem> prescriptionItems) {
        for (PrescriptionItem prescriptionItem : prescriptionItems) {
            DispenseItem dispenseItem = new DispenseItem(
                dispenseRecord.getId(),
                prescriptionItem.getId(),
                prescriptionItem.getMedicineId(),
                prescriptionItem.getMedicineName(),
                prescriptionItem.getQuantity(),
                prescriptionItem.getUnit(),
                prescriptionItem.getUnitPrice()
            );

            // 设置用法用量信息
            dispenseItem.setUsage(prescriptionItem.getUsage());
            dispenseItem.setDosage(prescriptionItem.getDosage());
            dispenseItem.setFrequency(prescriptionItem.getFrequency());
            dispenseItem.setDuration(prescriptionItem.getDuration());
            dispenseItem.setSpecialInstructions(prescriptionItem.getSpecialInstructions());
            dispenseItem.setSpecification(prescriptionItem.getSpecification());

            // 检查库存状态
            Integer totalStock = inventoryLevelMapper.getTotalStockByMedicine(prescriptionItem.getMedicineId());
            if (totalStock == null || totalStock == 0) {
                dispenseItem.setStockStatus("无库存");
            } else if (totalStock < prescriptionItem.getQuantity()) {
                dispenseItem.setStockStatus("不足");
            } else {
                dispenseItem.setStockStatus("充足");
            }

            dispenseItemMapper.insert(dispenseItem);
        }
    }

    /**
     * 更新库存
     */
    private void updateStock(Long medicineId, Integer quantity, String transactionType, String notes) {
        // 获取最早过期的批次进行库存更新
        InventoryLevel inventory = inventoryLevelMapper.getEarliestExpiringBatch(medicineId);
        if (inventory == null) {
            throw new BusinessException("INVENTORY_NOT_FOUND", "药品库存记录不存在");
        }

        // 更新库存数量
        int newStock = inventory.getCurrentStock() + quantity;
        if (newStock < 0) {
            throw new BusinessException("INSUFFICIENT_STOCK", "库存不足，无法完成操作");
        }

        inventory.setCurrentStock(newStock);
        inventoryLevelMapper.updateById(inventory);

        // 创建库存交易记录
        StockTransaction transaction = new StockTransaction();
        transaction.setTransactionNumber("TXN" + System.currentTimeMillis());
        transaction.setMedicineId(medicineId);
        transaction.setTransactionType(transactionType);
        transaction.setQuantity(quantity); // 保持原始数量（正负表示方向）
        transaction.setStockBefore(inventory.getCurrentStock() - quantity);
        transaction.setStockAfter(newStock);
        transaction.setTransactionDate(LocalDateTime.now());
        transaction.setRemarks(notes);

        stockTransactionMapper.insert(transaction);
    }
}