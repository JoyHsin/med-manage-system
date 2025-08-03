package org.me.joy.clinic.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.me.joy.clinic.entity.*;
import org.me.joy.clinic.mapper.*;
import org.me.joy.clinic.service.PrescriptionValidationService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * 处方验证服务实现类测试
 */
@ExtendWith(MockitoExtension.class)
class PrescriptionValidationServiceImplTest {

    @Mock
    private InventoryLevelMapper inventoryLevelMapper;

    @Mock
    private MedicineMapper medicineMapper;

    @Mock
    private AllergyHistoryMapper allergyHistoryMapper;

    @Mock
    private PrescriptionItemMapper prescriptionItemMapper;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private PrescriptionValidationServiceImpl prescriptionValidationService;

    private Prescription prescription;
    private List<PrescriptionItem> prescriptionItems;
    private Patient patient;

    @BeforeEach
    void setUp() {
        // 创建测试处方
        prescription = new Prescription();
        prescription.setId(1L);
        prescription.setPrescriptionNumber("P20240101001");
        prescription.setStatus("已审核");
        prescription.setPrescribedAt(LocalDateTime.now().minusHours(1));
        prescription.setValidityDays(3);
        prescription.setPrescriptionType("普通处方");

        // 创建测试处方项目
        PrescriptionItem item1 = new PrescriptionItem();
        item1.setId(1L);
        item1.setPrescriptionId(1L);
        item1.setMedicineId(1L);
        item1.setMedicineName("阿莫西林胶囊");
        item1.setQuantity(10);
        item1.setUnitPrice(new BigDecimal("2.50"));

        PrescriptionItem item2 = new PrescriptionItem();
        item2.setId(2L);
        item2.setPrescriptionId(1L);
        item2.setMedicineId(2L);
        item2.setMedicineName("布洛芬片");
        item2.setQuantity(20);
        item2.setUnitPrice(new BigDecimal("1.20"));

        prescriptionItems = Arrays.asList(item1, item2);

        // 创建测试患者
        patient = new Patient();
        patient.setId(1L);
        patient.setName("张三");
    }

    @Test
    void testValidatePrescription_Success() {
        // 模拟查询处方项目
        when(prescriptionItemMapper.findByPrescriptionId(1L)).thenReturn(prescriptionItems);

        // 执行验证
        PrescriptionValidationService.PrescriptionValidationResult result = 
            prescriptionValidationService.validatePrescription(prescription);

        // 验证结果
        assertTrue(result.isValid());
        assertEquals("通过", result.getResult());
        assertTrue(result.getIssues() == null || result.getIssues().isEmpty());
    }

    @Test
    void testValidatePrescription_NullPrescription() {
        // 执行验证
        PrescriptionValidationService.PrescriptionValidationResult result = 
            prescriptionValidationService.validatePrescription(null);

        // 验证结果
        assertFalse(result.isValid());
        assertEquals("不通过", result.getResult());
        assertNotNull(result.getIssues());
        assertTrue(result.getIssues().contains("处方信息不能为空"));
    }

    @Test
    void testValidatePrescription_ExpiredPrescription() {
        // 设置过期处方
        prescription.setPrescribedAt(LocalDateTime.now().minusDays(5));
        prescription.setValidityDays(3);

        when(prescriptionItemMapper.findByPrescriptionId(1L)).thenReturn(prescriptionItems);

        // 执行验证
        PrescriptionValidationService.PrescriptionValidationResult result = 
            prescriptionValidationService.validatePrescription(prescription);

        // 验证结果
        assertFalse(result.isValid());
        assertEquals("不通过", result.getResult());
        assertNotNull(result.getIssues());
        assertTrue(result.getIssues().contains("处方已过期，无法调剂"));
    }

    @Test
    void testValidatePrescription_NotReviewed() {
        // 设置未审核处方
        prescription.setStatus("已开具");

        when(prescriptionItemMapper.findByPrescriptionId(1L)).thenReturn(prescriptionItems);

        // 执行验证
        PrescriptionValidationService.PrescriptionValidationResult result = 
            prescriptionValidationService.validatePrescription(prescription);

        // 验证结果
        assertFalse(result.isValid());
        assertEquals("不通过", result.getResult());
        assertNotNull(result.getIssues());
        assertTrue(result.getIssues().contains("处方未经审核，无法调剂"));
    }

    @Test
    void testValidatePrescription_SpecialPrescription() {
        // 设置特殊处方
        prescription.setPrescriptionType("麻醉处方");

        when(prescriptionItemMapper.findByPrescriptionId(1L)).thenReturn(prescriptionItems);

        // 执行验证
        PrescriptionValidationService.PrescriptionValidationResult result = 
            prescriptionValidationService.validatePrescription(prescription);

        // 验证结果
        assertTrue(result.isValid());
        assertEquals("需要复核", result.getResult());
        assertNotNull(result.getWarnings());
        assertTrue(result.getWarnings().contains("特殊处方需要额外审核"));
    }

    @Test
    void testValidatePrescription_NoItems() {
        // 模拟无处方项目
        when(prescriptionItemMapper.findByPrescriptionId(1L)).thenReturn(Collections.emptyList());

        // 执行验证
        PrescriptionValidationService.PrescriptionValidationResult result = 
            prescriptionValidationService.validatePrescription(prescription);

        // 验证结果
        assertFalse(result.isValid());
        assertEquals("不通过", result.getResult());
        assertNotNull(result.getIssues());
        assertTrue(result.getIssues().contains("处方无药品项目"));
    }

    @Test
    void testCheckStock_SufficientStock() {
        // 模拟充足库存
        when(inventoryLevelMapper.getTotalStockByMedicine(1L)).thenReturn(50);
        when(inventoryLevelMapper.getTotalStockByMedicine(2L)).thenReturn(100);

        // 执行库存检查
        PrescriptionValidationService.StockCheckResult result = 
            prescriptionValidationService.checkStock(prescriptionItems);

        // 验证结果
        assertEquals("充足", result.getResult());
        assertTrue(result.getStockIssues() == null || result.getStockIssues().isEmpty());
    }

    @Test
    void testCheckStock_InsufficientStock() {
        // 模拟库存不足
        when(inventoryLevelMapper.getTotalStockByMedicine(1L)).thenReturn(5); // 不足
        when(inventoryLevelMapper.getTotalStockByMedicine(2L)).thenReturn(0); // 无库存

        // 执行库存检查
        PrescriptionValidationService.StockCheckResult result = 
            prescriptionValidationService.checkStock(prescriptionItems);

        // 验证结果
        assertEquals("不足", result.getResult());
        assertNotNull(result.getStockIssues());
        assertEquals(2, result.getStockIssues().size());
    }

    @Test
    void testCheckStock_NoInventoryRecord() {
        // 模拟无库存记录
        when(inventoryLevelMapper.getTotalStockByMedicine(any())).thenReturn(null);

        // 执行库存检查
        PrescriptionValidationService.StockCheckResult result = 
            prescriptionValidationService.checkStock(prescriptionItems);

        // 验证结果
        assertEquals("不足", result.getResult());
        assertNotNull(result.getStockIssues());
        assertEquals(2, result.getStockIssues().size());
        assertTrue(result.getStockIssues().stream()
            .allMatch(issue -> "无库存记录".equals(issue.getIssue())));
    }

    @Test
    void testCheckStock_EmptyItems() {
        // 执行库存检查
        PrescriptionValidationService.StockCheckResult result = 
            prescriptionValidationService.checkStock(Collections.emptyList());

        // 验证结果
        assertEquals("充足", result.getResult());
    }

    @Test
    void testCheckDrugInteractions_NoInteractions() {
        // 执行药品相互作用检查
        PrescriptionValidationService.DrugInteractionResult result = 
            prescriptionValidationService.checkDrugInteractions(prescriptionItems);

        // 验证结果
        assertFalse(result.isHasInteractions());
        assertTrue(result.getInteractions() == null || result.getInteractions().isEmpty());
    }

    @Test
    void testCheckDrugInteractions_HasInteractions() {
        // 创建有相互作用的药品
        PrescriptionItem item1 = new PrescriptionItem();
        item1.setMedicineName("阿司匹林");

        PrescriptionItem item2 = new PrescriptionItem();
        item2.setMedicineName("华法林");

        List<PrescriptionItem> interactingItems = Arrays.asList(item1, item2);

        // 执行药品相互作用检查
        PrescriptionValidationService.DrugInteractionResult result = 
            prescriptionValidationService.checkDrugInteractions(interactingItems);

        // 验证结果
        assertTrue(result.isHasInteractions());
        assertNotNull(result.getInteractions());
        assertFalse(result.getInteractions().isEmpty());
    }

    @Test
    void testCheckDrugInteractions_SingleItem() {
        // 执行药品相互作用检查（单个药品）
        PrescriptionValidationService.DrugInteractionResult result = 
            prescriptionValidationService.checkDrugInteractions(
                Collections.singletonList(prescriptionItems.get(0)));

        // 验证结果
        assertFalse(result.isHasInteractions());
    }

    @Test
    void testCheckAllergies_NoAllergies() {
        // 模拟无过敏史
        when(allergyHistoryMapper.findByPatientId(1L)).thenReturn(Collections.emptyList());

        // 执行过敏检查
        PrescriptionValidationService.AllergyCheckResult result = 
            prescriptionValidationService.checkAllergies(patient, prescriptionItems);

        // 验证结果
        assertFalse(result.isHasAllergyRisk());
        assertTrue(result.getAllergyRisks() == null || result.getAllergyRisks().isEmpty());
    }

    @Test
    void testCheckAllergies_HasAllergyRisk() {
        // 创建过敏史
        AllergyHistory allergy = new AllergyHistory();
        allergy.setPatientId(1L);
        allergy.setAllergen("青霉素");
        allergy.setSeverity("严重");

        when(allergyHistoryMapper.findByPatientId(1L)).thenReturn(Collections.singletonList(allergy));

        // 创建含青霉素的药品
        PrescriptionItem item = new PrescriptionItem();
        item.setMedicineName("青霉素V钾片");

        // 执行过敏检查
        PrescriptionValidationService.AllergyCheckResult result = 
            prescriptionValidationService.checkAllergies(patient, Collections.singletonList(item));

        // 验证结果
        assertTrue(result.isHasAllergyRisk());
        assertNotNull(result.getAllergyRisks());
        assertFalse(result.getAllergyRisks().isEmpty());
    }

    @Test
    void testCheckAllergies_NullPatient() {
        // 执行过敏检查（患者为空）
        PrescriptionValidationService.AllergyCheckResult result = 
            prescriptionValidationService.checkAllergies(null, prescriptionItems);

        // 验证结果
        assertFalse(result.isHasAllergyRisk());
    }

    @Test
    void testIsPrescriptionExpired_NotExpired() {
        // 测试未过期处方
        prescription.setPrescribedAt(LocalDateTime.now().minusHours(1));
        prescription.setValidityDays(3);

        boolean expired = prescriptionValidationService.isPrescriptionExpired(prescription);
        assertFalse(expired);
    }

    @Test
    void testIsPrescriptionExpired_Expired() {
        // 测试过期处方
        prescription.setPrescribedAt(LocalDateTime.now().minusDays(5));
        prescription.setValidityDays(3);

        boolean expired = prescriptionValidationService.isPrescriptionExpired(prescription);
        assertTrue(expired);
    }

    @Test
    void testIsPrescriptionExpired_NullValues() {
        // 测试空值情况
        prescription.setPrescribedAt(null);
        prescription.setValidityDays(3);

        boolean expired = prescriptionValidationService.isPrescriptionExpired(prescription);
        assertFalse(expired);

        prescription.setPrescribedAt(LocalDateTime.now());
        prescription.setValidityDays(null);

        expired = prescriptionValidationService.isPrescriptionExpired(prescription);
        assertFalse(expired);
    }

    @Test
    void testValidateDispensePermission_ValidPharmacist() {
        // 模拟有效药师
        User pharmacist = new User();
        pharmacist.setId(1L);
        pharmacist.setEnabled(true);

        when(userMapper.selectById(1L)).thenReturn(pharmacist);

        // 执行权限验证
        boolean hasPermission = prescriptionValidationService.validateDispensePermission(prescription, 1L);
        assertTrue(hasPermission);
    }

    @Test
    void testValidateDispensePermission_DisabledPharmacist() {
        // 模拟禁用药师
        User pharmacist = new User();
        pharmacist.setId(1L);
        pharmacist.setEnabled(false);

        when(userMapper.selectById(1L)).thenReturn(pharmacist);

        // 执行权限验证
        boolean hasPermission = prescriptionValidationService.validateDispensePermission(prescription, 1L);
        assertFalse(hasPermission);
    }

    @Test
    void testValidateDispensePermission_NonexistentPharmacist() {
        // 模拟不存在的药师
        when(userMapper.selectById(1L)).thenReturn(null);

        // 执行权限验证
        boolean hasPermission = prescriptionValidationService.validateDispensePermission(prescription, 1L);
        assertFalse(hasPermission);
    }

    @Test
    void testValidateDispensePermission_NullValues() {
        // 测试空值情况
        boolean hasPermission = prescriptionValidationService.validateDispensePermission(null, 1L);
        assertFalse(hasPermission);

        hasPermission = prescriptionValidationService.validateDispensePermission(prescription, null);
        assertFalse(hasPermission);
    }

    @Test
    void testValidateSpecialPrescription_NormalPrescription() {
        // 测试普通处方
        prescription.setPrescriptionType("普通处方");

        PrescriptionValidationService.SpecialPrescriptionValidationResult result = 
            prescriptionValidationService.validateSpecialPrescription(prescription);

        assertTrue(result.isValid());
        assertFalse(result.isRequiresSpecialApproval());
    }

    @Test
    void testValidateSpecialPrescription_NarcoticsPrescrip() {
        // 测试麻醉处方
        prescription.setPrescriptionType("麻醉处方");

        PrescriptionValidationService.SpecialPrescriptionValidationResult result = 
            prescriptionValidationService.validateSpecialPrescription(prescription);

        assertTrue(result.isValid());
        assertTrue(result.isRequiresSpecialApproval());
        assertNotNull(result.getRequirements());
        assertFalse(result.getRequirements().isEmpty());
        assertTrue(result.getRequirements().contains("需要麻醉药品处方权医师开具"));
    }

    @Test
    void testValidateSpecialPrescription_PsychotropicPrescription() {
        // 测试精神药品处方
        prescription.setPrescriptionType("精神药品处方");

        PrescriptionValidationService.SpecialPrescriptionValidationResult result = 
            prescriptionValidationService.validateSpecialPrescription(prescription);

        assertTrue(result.isValid());
        assertTrue(result.isRequiresSpecialApproval());
        assertNotNull(result.getRequirements());
        assertTrue(result.getRequirements().contains("需要精神药品处方权医师开具"));
    }

    @Test
    void testValidateSpecialPrescription_ToxicPrescription() {
        // 测试毒性药品处方
        prescription.setPrescriptionType("毒性药品处方");

        PrescriptionValidationService.SpecialPrescriptionValidationResult result = 
            prescriptionValidationService.validateSpecialPrescription(prescription);

        assertTrue(result.isValid());
        assertTrue(result.isRequiresSpecialApproval());
        assertNotNull(result.getRequirements());
        assertTrue(result.getRequirements().contains("需要毒性药品处方权医师开具"));
    }

    @Test
    void testValidateSpecialPrescription_NullPrescription() {
        // 测试空处方
        PrescriptionValidationService.SpecialPrescriptionValidationResult result = 
            prescriptionValidationService.validateSpecialPrescription(null);

        assertTrue(result.isValid());
        assertFalse(result.isRequiresSpecialApproval());
    }
}