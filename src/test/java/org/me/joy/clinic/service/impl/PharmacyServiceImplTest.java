package org.me.joy.clinic.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.me.joy.clinic.entity.*;
import org.me.joy.clinic.exception.BusinessException;
import org.me.joy.clinic.mapper.*;
import org.me.joy.clinic.service.PharmacyService;
import org.me.joy.clinic.service.PrescriptionValidationService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * 药房服务实现类测试
 */
@ExtendWith(MockitoExtension.class)
class PharmacyServiceImplTest {

    @Mock
    private PrescriptionMapper prescriptionMapper;

    @Mock
    private PrescriptionItemMapper prescriptionItemMapper;

    @Mock
    private DispenseRecordMapper dispenseRecordMapper;

    @Mock
    private DispenseItemMapper dispenseItemMapper;

    @Mock
    private PatientMapper patientMapper;

    @Mock
    private MedicineMapper medicineMapper;

    @Mock
    private InventoryLevelMapper inventoryLevelMapper;

    @Mock
    private StockTransactionMapper stockTransactionMapper;

    @Mock
    private PrescriptionValidationService prescriptionValidationService;

    @Mock
    private MedicalRecordMapper medicalRecordMapper;

    @InjectMocks
    private PharmacyServiceImpl pharmacyService;

    private Prescription prescription;
    private Patient patient;
    private List<PrescriptionItem> prescriptionItems;
    private DispenseRecord dispenseRecord;
    private DispenseItem dispenseItem;

    @BeforeEach
    void setUp() {
        // 创建测试处方
        prescription = new Prescription();
        prescription.setId(1L);
        prescription.setPrescriptionNumber("P20240101001");
        prescription.setStatus("已审核");
        prescription.setMedicalRecordId(1L);
        prescription.setTotalAmount(new BigDecimal("50.00"));
        prescription.setPrescribedAt(LocalDateTime.now().minusHours(1));
        prescription.setValidityDays(3);

        // 创建测试患者
        patient = new Patient();
        patient.setId(1L);
        patient.setName("张三");

        // 创建测试处方项目
        PrescriptionItem item = new PrescriptionItem();
        item.setId(1L);
        item.setPrescriptionId(1L);
        item.setMedicineId(1L);
        item.setMedicineName("阿莫西林胶囊");
        item.setQuantity(10);
        item.setUnitPrice(new BigDecimal("2.50"));
        item.setUnit("粒");
        prescriptionItems = Collections.singletonList(item);

        // 创建测试调剂记录
        dispenseRecord = new DispenseRecord();
        dispenseRecord.setId(1L);
        dispenseRecord.setPrescriptionId(1L);
        dispenseRecord.setPrescriptionNumber("P20240101001");
        dispenseRecord.setPatientId(1L);
        dispenseRecord.setPatientName("张三");
        dispenseRecord.setPharmacistId(1L);
        dispenseRecord.setPharmacistName("李药师");
        dispenseRecord.setStatus("待调剂");

        // 创建测试调剂项目
        dispenseItem = new DispenseItem();
        dispenseItem.setId(1L);
        dispenseItem.setDispenseRecordId(1L);
        dispenseItem.setPrescriptionItemId(1L);
        dispenseItem.setMedicineId(1L);
        dispenseItem.setMedicineName("阿莫西林胶囊");
        dispenseItem.setPrescribedQuantity(10);
        dispenseItem.setDispensedQuantity(10);
        dispenseItem.setUnit("粒");
        dispenseItem.setUnitPrice(new BigDecimal("2.50"));
        dispenseItem.setStatus("待调剂");
    }

    @Test
    void testStartDispensing_Success() {
        // 创建测试病历
        MedicalRecord medicalRecord = new MedicalRecord();
        medicalRecord.setId(1L);
        medicalRecord.setPatientId(1L);

        // 模拟依赖调用
        when(prescriptionMapper.selectById(1L)).thenReturn(prescription);
        when(medicalRecordMapper.selectById(1L)).thenReturn(medicalRecord);
        when(patientMapper.selectById(1L)).thenReturn(patient);
        when(dispenseRecordMapper.existsByPrescriptionId(1L)).thenReturn(false);
        when(prescriptionItemMapper.findByPrescriptionId(1L)).thenReturn(prescriptionItems);

        // 模拟验证服务调用
        PrescriptionValidationService.PrescriptionValidationResult validationResult = 
            new PrescriptionValidationService.PrescriptionValidationResult(true, "通过");
        when(prescriptionValidationService.validatePrescription(any())).thenReturn(validationResult);

        PrescriptionValidationService.StockCheckResult stockResult = 
            new PrescriptionValidationService.StockCheckResult("充足");
        when(prescriptionValidationService.checkStock(any())).thenReturn(stockResult);

        PrescriptionValidationService.DrugInteractionResult interactionResult = 
            new PrescriptionValidationService.DrugInteractionResult(false);
        when(prescriptionValidationService.checkDrugInteractions(any())).thenReturn(interactionResult);

        PrescriptionValidationService.AllergyCheckResult allergyResult = 
            new PrescriptionValidationService.AllergyCheckResult(false);
        when(prescriptionValidationService.checkAllergies(any(), any())).thenReturn(allergyResult);

        when(dispenseRecordMapper.insert(any())).thenReturn(1);
        when(dispenseItemMapper.insert(any())).thenReturn(1);
        when(inventoryLevelMapper.getTotalStockByMedicine(1L)).thenReturn(50);

        // 执行测试
        DispenseRecord result = pharmacyService.startDispensing(1L, 1L, "李药师");

        // 验证结果
        assertNotNull(result);
        assertEquals(1L, result.getPrescriptionId());
        assertEquals("P20240101001", result.getPrescriptionNumber());
        assertEquals("张三", result.getPatientName());
        assertEquals("李药师", result.getPharmacistName());

        // 验证方法调用
        verify(dispenseRecordMapper).insert(any(DispenseRecord.class));
        verify(dispenseItemMapper).insert(any(DispenseItem.class));
    }

    @Test
    void testStartDispensing_PrescriptionNotFound() {
        // 模拟处方不存在
        when(prescriptionMapper.selectById(1L)).thenReturn(null);

        // 执行测试并验证异常
        BusinessException exception = assertThrows(BusinessException.class, 
            () -> pharmacyService.startDispensing(1L, 1L, "李药师"));
        assertEquals("处方不存在", exception.getMessage());
    }

    @Test
    void testStartDispensing_AlreadyHasDispenseRecord() {
        // 模拟已有调剂记录
        when(prescriptionMapper.selectById(1L)).thenReturn(prescription);
        when(dispenseRecordMapper.existsByPrescriptionId(1L)).thenReturn(true);

        // 模拟已有调剂记录
        when(dispenseRecordMapper.existsByPrescriptionId(1L)).thenReturn(true);

        // 执行测试并验证异常
        BusinessException exception = assertThrows(BusinessException.class, 
            () -> pharmacyService.startDispensing(1L, 1L, "李药师"));
        assertTrue(exception.getMessage().contains("处方不符合调剂条件"));
    }

    @Test
    void testDispenseMedicineItem_Success() {
        // 准备测试数据
        PharmacyService.DispenseMedicineRequest request = new PharmacyService.DispenseMedicineRequest();
        request.setDispenseItemId(1L);
        request.setMedicineId(1L);
        request.setDispensedQuantity(10);
        request.setBatchNumber("20240101");
        request.setExpiryDate(LocalDate.now().plusYears(2));
        request.setDispensedBy(1L);
        request.setDispensedByName("李药师");

        // 模拟依赖调用
        when(dispenseItemMapper.selectById(1L)).thenReturn(dispenseItem);
        when(inventoryLevelMapper.getTotalStockByMedicine(1L)).thenReturn(50);
        when(inventoryLevelMapper.getEarliestExpiringBatch(1L)).thenReturn(createInventoryLevel(50));
        when(inventoryLevelMapper.updateById(any())).thenReturn(1);
        when(stockTransactionMapper.insert(any())).thenReturn(1);
        when(dispenseItemMapper.updateById(any())).thenReturn(1);

        // 执行测试
        DispenseItem result = pharmacyService.dispenseMedicineItem(request);

        // 验证结果
        assertNotNull(result);
        assertEquals("已调剂", result.getStatus());
        assertEquals("20240101", result.getBatchNumber());
        assertEquals(request.getExpiryDate(), result.getExpiryDate());

        // 验证方法调用
        verify(inventoryLevelMapper).updateById(any(InventoryLevel.class));
        verify(stockTransactionMapper).insert(any(StockTransaction.class));
        verify(dispenseItemMapper).updateById(any(DispenseItem.class));
    }

    @Test
    void testDispenseMedicineItem_ItemNotFound() {
        // 模拟调剂项目不存在
        PharmacyService.DispenseMedicineRequest request = new PharmacyService.DispenseMedicineRequest();
        request.setDispenseItemId(1L);

        when(dispenseItemMapper.selectById(1L)).thenReturn(null);

        // 执行测试并验证异常
        BusinessException exception = assertThrows(BusinessException.class, 
            () -> pharmacyService.dispenseMedicineItem(request));
        assertEquals("调剂项目不存在", exception.getMessage());
    }

    @Test
    void testDispenseMedicineItem_InsufficientStock() {
        // 准备测试数据
        PharmacyService.DispenseMedicineRequest request = new PharmacyService.DispenseMedicineRequest();
        request.setDispenseItemId(1L);
        request.setMedicineId(1L);
        request.setDispensedQuantity(10);

        // 模拟库存不足
        when(dispenseItemMapper.selectById(1L)).thenReturn(dispenseItem);
        when(inventoryLevelMapper.getTotalStockByMedicine(1L)).thenReturn(5);

        // 执行测试并验证异常
        BusinessException exception = assertThrows(BusinessException.class, 
            () -> pharmacyService.dispenseMedicineItem(request));
        assertEquals("库存不足，无法调剂", exception.getMessage());
    }

    @Test
    void testCompleteDispensing_Success() {
        // 设置调剂记录状态
        dispenseRecord.setStatus("调剂中");

        // 模拟依赖调用
        when(dispenseRecordMapper.selectById(1L)).thenReturn(dispenseRecord);
        
        // 模拟所有调剂项目已完成
        dispenseItem.setStatus("已调剂");
        when(dispenseItemMapper.findByDispenseRecordId(1L)).thenReturn(Collections.singletonList(dispenseItem));
        
        when(dispenseRecordMapper.updateById(any())).thenReturn(1);
        when(prescriptionMapper.selectById(1L)).thenReturn(prescription);
        when(prescriptionMapper.updateById(any())).thenReturn(1);

        // 执行测试
        DispenseRecord result = pharmacyService.completeDispensing(1L);

        // 验证结果
        assertNotNull(result);
        assertEquals("已调剂", result.getStatus());

        // 验证方法调用
        verify(dispenseRecordMapper).updateById(any(DispenseRecord.class));
        verify(prescriptionMapper).updateById(any(Prescription.class));
    }

    @Test
    void testCompleteDispensing_InvalidStatus() {
        // 设置错误状态
        dispenseRecord.setStatus("待调剂");

        when(dispenseRecordMapper.selectById(1L)).thenReturn(dispenseRecord);

        // 执行测试并验证异常
        BusinessException exception = assertThrows(BusinessException.class, 
            () -> pharmacyService.completeDispensing(1L));
        assertEquals("调剂记录状态不正确，无法完成调剂", exception.getMessage());
    }

    @Test
    void testDeliverMedicine_Success() {
        // 设置调剂记录状态
        dispenseRecord.setStatus("已调剂");
        dispenseRecord.setValidationResult("通过");
        dispenseRecord.setQualityCheckResult("合格");

        // 模拟依赖调用
        when(dispenseRecordMapper.selectById(1L)).thenReturn(dispenseRecord);
        when(dispenseRecordMapper.updateById(any())).thenReturn(1);
        when(prescriptionMapper.selectById(1L)).thenReturn(prescription);
        when(prescriptionMapper.updateById(any())).thenReturn(1);

        // 执行测试
        DispenseRecord result = pharmacyService.deliverMedicine(1L, 2L, "王药师", "发药完成");

        // 验证结果
        assertNotNull(result);
        assertEquals("已发药", result.getStatus());
        assertEquals(2L, result.getDispensingPharmacistId());
        assertEquals("王药师", result.getDispensingPharmacistName());
        assertEquals("发药完成", result.getDeliveryNotes());

        // 验证方法调用
        verify(dispenseRecordMapper).updateById(any(DispenseRecord.class));
        verify(prescriptionMapper).updateById(any(Prescription.class));
    }

    @Test
    void testReturnPrescription_Success() {
        // 设置调剂记录状态
        dispenseRecord.setStatus("调剂中");

        // 设置调剂项目
        dispenseItem.setStatus("已调剂");
        dispenseItem.setDispensedQuantity(10);

        // 模拟依赖调用
        when(dispenseRecordMapper.selectById(1L)).thenReturn(dispenseRecord);
        when(dispenseItemMapper.findByDispenseRecordId(1L)).thenReturn(Collections.singletonList(dispenseItem));
        when(inventoryLevelMapper.getEarliestExpiringBatch(1L)).thenReturn(createInventoryLevel(40));
        when(inventoryLevelMapper.updateById(any())).thenReturn(1);
        when(stockTransactionMapper.insert(any())).thenReturn(1);
        when(dispenseItemMapper.updateById(any())).thenReturn(1);
        when(dispenseRecordMapper.updateById(any())).thenReturn(1);

        // 执行测试
        DispenseRecord result = pharmacyService.returnPrescription(1L, "患者取消");

        // 验证结果
        assertNotNull(result);
        assertEquals("已退回", result.getStatus());
        assertEquals("患者取消", result.getReturnReason());

        // 验证方法调用
        verify(inventoryLevelMapper).updateById(any(InventoryLevel.class));
        verify(stockTransactionMapper).insert(any(StockTransaction.class));
        verify(dispenseItemMapper).updateById(any(DispenseItem.class));
        verify(dispenseRecordMapper).updateById(any(DispenseRecord.class));
    }

    @Test
    void testSubstituteMedicine_Success() {
        // 准备新药品
        Medicine newMedicine = new Medicine();
        newMedicine.setId(2L);
        newMedicine.setName("头孢氨苄胶囊");
        newMedicine.setSpecification("250mg");
        newMedicine.setSellingPrice(new BigDecimal("3.00"));

        // 模拟依赖调用
        when(dispenseItemMapper.selectById(1L)).thenReturn(dispenseItem);
        when(medicineMapper.selectById(2L)).thenReturn(newMedicine);
        when(inventoryLevelMapper.getTotalStockByMedicine(2L)).thenReturn(50);
        when(dispenseItemMapper.updateById(any())).thenReturn(1);

        // 执行测试
        DispenseItem result = pharmacyService.substituteMedicine(1L, 2L, "原药品缺货");

        // 验证结果
        assertNotNull(result);
        assertEquals(1L, result.getOriginalMedicineId());
        assertEquals("阿莫西林胶囊", result.getOriginalMedicineName());
        assertEquals(2L, result.getMedicineId());
        assertEquals("头孢氨苄胶囊", result.getMedicineName());
        assertTrue(result.getIsSubstitute());
        assertEquals("原药品缺货", result.getSubstituteReason());
        assertEquals("替代", result.getStatus());

        // 验证方法调用
        verify(dispenseItemMapper).updateById(any(DispenseItem.class));
    }

    @Test
    void testCheckDispenseEligibility_Eligible() {
        // 模拟符合条件的处方
        when(prescriptionMapper.selectById(1L)).thenReturn(prescription);
        when(dispenseRecordMapper.existsByPrescriptionId(1L)).thenReturn(false);
        when(prescriptionItemMapper.findByPrescriptionId(1L)).thenReturn(prescriptionItems);

        // 执行测试
        PharmacyService.DispenseEligibilityResult result = pharmacyService.checkDispenseEligibility(1L);

        // 验证结果
        assertTrue(result.isEligible());
        assertEquals("符合调剂条件", result.getReason());
    }

    @Test
    void testCheckDispenseEligibility_PrescriptionNotFound() {
        // 模拟处方不存在
        when(prescriptionMapper.selectById(1L)).thenReturn(null);

        // 执行测试
        PharmacyService.DispenseEligibilityResult result = pharmacyService.checkDispenseEligibility(1L);

        // 验证结果
        assertFalse(result.isEligible());
        assertEquals("处方不存在", result.getReason());
    }

    @Test
    void testCheckDispenseEligibility_NotReviewed() {
        // 设置未审核状态
        prescription.setStatus("已开具");

        when(prescriptionMapper.selectById(1L)).thenReturn(prescription);
        when(dispenseRecordMapper.existsByPrescriptionId(1L)).thenReturn(false);
        when(prescriptionItemMapper.findByPrescriptionId(1L)).thenReturn(prescriptionItems);

        // 执行测试
        PharmacyService.DispenseEligibilityResult result = pharmacyService.checkDispenseEligibility(1L);

        // 验证结果
        assertFalse(result.isEligible());
        assertTrue(result.getReason().contains("处方未经审核"));
    }

    @Test
    void testGetPendingPrescriptions() {
        // 模拟待调剂处方列表
        List<Prescription> pendingPrescriptions = Arrays.asList(prescription);
        when(prescriptionMapper.findPendingDispense()).thenReturn(pendingPrescriptions);

        // 执行测试
        List<Prescription> result = pharmacyService.getPendingPrescriptions();

        // 验证结果
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(prescription.getId(), result.get(0).getId());
    }

    @Test
    void testGetInProgressDispenses() {
        // 模拟调剂中记录列表
        List<DispenseRecord> inProgressRecords = Arrays.asList(dispenseRecord);
        when(dispenseRecordMapper.findInProgress()).thenReturn(inProgressRecords);

        // 执行测试
        List<DispenseRecord> result = pharmacyService.getInProgressDispenses();

        // 验证结果
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(dispenseRecord.getId(), result.get(0).getId());
    }

    @Test
    void testGetDispenseRecordWithItems() {
        // 模拟调剂记录和项目
        List<DispenseItem> items = Arrays.asList(dispenseItem);
        when(dispenseRecordMapper.selectById(1L)).thenReturn(dispenseRecord);
        when(dispenseItemMapper.findByDispenseRecordId(1L)).thenReturn(items);

        // 执行测试
        DispenseRecord result = pharmacyService.getDispenseRecordWithItems(1L);

        // 验证结果
        assertNotNull(result);
        assertEquals(dispenseRecord.getId(), result.getId());
        assertNotNull(result.getDispenseItems());
        assertEquals(1, result.getDispenseItems().size());
        assertEquals(dispenseItem.getId(), result.getDispenseItems().get(0).getId());
    }

    /**
     * 创建库存水平测试数据
     */
    private InventoryLevel createInventoryLevel(int currentStock) {
        InventoryLevel inventory = new InventoryLevel();
        inventory.setMedicineId(1L);
        inventory.setCurrentStock(currentStock);
        return inventory;
    }
}