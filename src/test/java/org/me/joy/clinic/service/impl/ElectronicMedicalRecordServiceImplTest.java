package org.me.joy.clinic.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.me.joy.clinic.dto.CreateMedicalRecordRequest;
import org.me.joy.clinic.dto.UpdateMedicalRecordRequest;
import org.me.joy.clinic.entity.Diagnosis;
import org.me.joy.clinic.entity.MedicalRecord;
import org.me.joy.clinic.entity.Prescription;
import org.me.joy.clinic.exception.BusinessException;
import org.me.joy.clinic.exception.ValidationException;
import org.me.joy.clinic.mapper.DiagnosisMapper;
import org.me.joy.clinic.mapper.MedicalRecordMapper;
import org.me.joy.clinic.mapper.PrescriptionMapper;
import org.me.joy.clinic.service.ElectronicMedicalRecordService;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * 电子病历服务实现类测试
 */
@ExtendWith(MockitoExtension.class)
class ElectronicMedicalRecordServiceImplTest {

    @Mock
    private MedicalRecordMapper medicalRecordMapper;

    @Mock
    private DiagnosisMapper diagnosisMapper;

    @Mock
    private PrescriptionMapper prescriptionMapper;

    @InjectMocks
    private ElectronicMedicalRecordServiceImpl medicalRecordService;

    private CreateMedicalRecordRequest createRequest;
    private UpdateMedicalRecordRequest updateRequest;
    private MedicalRecord medicalRecord;
    private Diagnosis diagnosis;
    private Prescription prescription;

    @BeforeEach
    void setUp() {
        // 创建病历请求
        createRequest = new CreateMedicalRecordRequest();
        createRequest.setPatientId(1L);
        createRequest.setDoctorId(2L);
        createRequest.setRegistrationId(3L);
        createRequest.setChiefComplaint("头痛");
        createRequest.setPresentIllness("头痛3天");
        createRequest.setDepartment("内科");
        createRequest.setRecordType("门诊病历");

        // 更新病历请求
        updateRequest = new UpdateMedicalRecordRequest();
        updateRequest.setChiefComplaint("头痛加重");
        updateRequest.setFinalDiagnosis("偏头痛");
        updateRequest.setTreatmentPlan("药物治疗");

        // 病历实体
        medicalRecord = new MedicalRecord();
        medicalRecord.setId(1L);
        medicalRecord.setPatientId(1L);
        medicalRecord.setDoctorId(2L);
        medicalRecord.setRegistrationId(3L);
        medicalRecord.setRecordNumber("MR202401010001");
        medicalRecord.setChiefComplaint("头痛");
        medicalRecord.setPresentIllness("头痛3天");
        medicalRecord.setDepartment("内科");
        medicalRecord.setRecordType("门诊病历");
        medicalRecord.setStatus("草稿");
        medicalRecord.setRecordDate(LocalDateTime.now());

        // 诊断实体
        diagnosis = new Diagnosis();
        diagnosis.setId(1L);
        diagnosis.setMedicalRecordId(1L);
        diagnosis.setDiagnosisName("偏头痛");
        diagnosis.setDiagnosisType("主要诊断");
        diagnosis.setDoctorId(2L);
        diagnosis.setDiagnosisTime(LocalDateTime.now());

        // 处方实体
        prescription = new Prescription();
        prescription.setId(1L);
        prescription.setMedicalRecordId(1L);
        prescription.setDoctorId(2L);
        prescription.setPrescriptionNumber("RX202401010001");
        prescription.setPrescriptionType("普通处方");
        prescription.setStatus("草稿");
        prescription.setPrescribedAt(LocalDateTime.now());
    }

    @Test
    void testCreateMedicalRecord_Success() {
        // Mock
        when(medicalRecordMapper.existsByRecordNumber(anyString())).thenReturn(false);
        when(medicalRecordMapper.insert(any(MedicalRecord.class))).thenReturn(1);
        when(medicalRecordMapper.updateById(any(MedicalRecord.class))).thenReturn(1);

        // 执行
        MedicalRecord result = medicalRecordService.createMedicalRecord(createRequest);

        // 验证
        assertNotNull(result);
        assertEquals(createRequest.getPatientId(), result.getPatientId());
        assertEquals(createRequest.getDoctorId(), result.getDoctorId());
        assertEquals(createRequest.getChiefComplaint(), result.getChiefComplaint());
        assertNotNull(result.getRecordNumber());
        assertTrue(result.getRecordNumber().startsWith("MR"));

        verify(medicalRecordMapper).insert(any(MedicalRecord.class));
        verify(medicalRecordMapper).updateById(any(MedicalRecord.class));
    }

    @Test
    void testCreateMedicalRecord_ValidationError() {
        // 测试患者ID为空
        createRequest.setPatientId(null);

        // 执行和验证
        ValidationException exception = assertThrows(ValidationException.class, 
            () -> medicalRecordService.createMedicalRecord(createRequest));
        assertEquals("3041", exception.getErrorCode());
        assertEquals("患者ID不能为空", exception.getMessage());
    }

    @Test
    void testUpdateMedicalRecord_Success() {
        // Mock
        when(medicalRecordMapper.selectById(1L)).thenReturn(medicalRecord);
        when(medicalRecordMapper.updateById(any(MedicalRecord.class))).thenReturn(1);

        // 执行
        MedicalRecord result = medicalRecordService.updateMedicalRecord(1L, updateRequest);

        // 验证
        assertNotNull(result);
        assertEquals(updateRequest.getChiefComplaint(), result.getChiefComplaint());
        assertEquals(updateRequest.getFinalDiagnosis(), result.getFinalDiagnosis());
        assertEquals(updateRequest.getTreatmentPlan(), result.getTreatmentPlan());

        verify(medicalRecordMapper).selectById(1L);
        verify(medicalRecordMapper).updateById(any(MedicalRecord.class));
    }

    @Test
    void testUpdateMedicalRecord_CannotEdit() {
        // 设置病历状态为已审核
        medicalRecord.setStatus("已审核");
        when(medicalRecordMapper.selectById(1L)).thenReturn(medicalRecord);

        // 执行和验证
        BusinessException exception = assertThrows(BusinessException.class, 
            () -> medicalRecordService.updateMedicalRecord(1L, updateRequest));
        assertEquals("3001", exception.getErrorCode());
        assertTrue(exception.getMessage().contains("不允许编辑"));
    }

    @Test
    void testGetMedicalRecordById_Success() {
        // Mock
        when(medicalRecordMapper.selectById(1L)).thenReturn(medicalRecord);
        when(diagnosisMapper.selectList(any(QueryWrapper.class))).thenReturn(Arrays.asList(diagnosis));
        when(prescriptionMapper.selectList(any(QueryWrapper.class))).thenReturn(Arrays.asList(prescription));

        // 执行
        MedicalRecord result = medicalRecordService.getMedicalRecordById(1L);

        // 验证
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertNotNull(result.getDiagnoses());
        assertEquals(1, result.getDiagnoses().size());
        assertNotNull(result.getPrescriptions());
        assertEquals(1, result.getPrescriptions().size());

        verify(medicalRecordMapper).selectById(1L);
    }

    @Test
    void testGetMedicalRecordById_NotFound() {
        // Mock
        when(medicalRecordMapper.selectById(1L)).thenReturn(null);

        // 执行和验证
        BusinessException exception = assertThrows(BusinessException.class, 
            () -> medicalRecordService.getMedicalRecordById(1L));
        assertEquals("3003", exception.getErrorCode());
        assertEquals("病历不存在", exception.getMessage());
    }

    @Test
    void testGetMedicalRecordByNumber_Success() {
        // Mock
        when(medicalRecordMapper.findByRecordNumber("MR202401010001")).thenReturn(medicalRecord);
        when(diagnosisMapper.selectList(any(QueryWrapper.class))).thenReturn(Arrays.asList(diagnosis));
        when(prescriptionMapper.selectList(any(QueryWrapper.class))).thenReturn(Arrays.asList(prescription));

        // 执行
        MedicalRecord result = medicalRecordService.getMedicalRecordByNumber("MR202401010001");

        // 验证
        assertNotNull(result);
        assertEquals("MR202401010001", result.getRecordNumber());

        verify(medicalRecordMapper).findByRecordNumber("MR202401010001");
    }

    @Test
    void testGetPatientMedicalRecords_Success() {
        // Mock
        List<MedicalRecord> records = Arrays.asList(medicalRecord);
        when(medicalRecordMapper.findByPatientId(1L)).thenReturn(records);
        when(diagnosisMapper.selectList(any(QueryWrapper.class))).thenReturn(Arrays.asList(diagnosis));
        when(prescriptionMapper.selectList(any(QueryWrapper.class))).thenReturn(Arrays.asList(prescription));

        // 执行
        List<MedicalRecord> result = medicalRecordService.getPatientMedicalRecords(1L);

        // 验证
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getPatientId());

        verify(medicalRecordMapper).findByPatientId(1L);
    }

    @Test
    void testGetMedicalRecordsByDateRange_Success() {
        // Mock
        LocalDateTime startDate = LocalDateTime.now().minusDays(7);
        LocalDateTime endDate = LocalDateTime.now();
        List<MedicalRecord> records = Arrays.asList(medicalRecord);
        when(medicalRecordMapper.findByDateRange(startDate, endDate)).thenReturn(records);
        when(diagnosisMapper.selectList(any(QueryWrapper.class))).thenReturn(Arrays.asList(diagnosis));
        when(prescriptionMapper.selectList(any(QueryWrapper.class))).thenReturn(Arrays.asList(prescription));

        // 执行
        List<MedicalRecord> result = medicalRecordService.getMedicalRecordsByDateRange(startDate, endDate);

        // 验证
        assertNotNull(result);
        assertEquals(1, result.size());

        verify(medicalRecordMapper).findByDateRange(startDate, endDate);
    }

    @Test
    void testGetMedicalRecordsByDateRange_InvalidDateRange() {
        // 执行和验证
        LocalDateTime startDate = LocalDateTime.now();
        LocalDateTime endDate = LocalDateTime.now().minusDays(1);

        ValidationException exception = assertThrows(ValidationException.class, 
            () -> medicalRecordService.getMedicalRecordsByDateRange(startDate, endDate));
        assertEquals("3011", exception.getErrorCode());
        assertEquals("开始时间不能晚于结束时间", exception.getMessage());
    }

    @Test
    void testAddDiagnosis_Success() {
        // Mock
        when(medicalRecordMapper.selectById(1L)).thenReturn(medicalRecord);
        when(diagnosisMapper.selectList(any(QueryWrapper.class))).thenReturn(Arrays.asList());
        when(prescriptionMapper.selectList(any(QueryWrapper.class))).thenReturn(Arrays.asList());
        when(diagnosisMapper.insert(any(Diagnosis.class))).thenReturn(1);

        // 执行
        Diagnosis result = medicalRecordService.addDiagnosis(1L, diagnosis);

        // 验证
        assertNotNull(result);
        assertEquals(1L, result.getMedicalRecordId());
        assertNotNull(result.getDiagnosisTime());

        verify(diagnosisMapper).insert(any(Diagnosis.class));
    }

    @Test
    void testAddDiagnosis_CannotEdit() {
        // 设置病历状态为已审核
        medicalRecord.setStatus("已审核");
        when(medicalRecordMapper.selectById(1L)).thenReturn(medicalRecord);
        when(diagnosisMapper.selectList(any(QueryWrapper.class))).thenReturn(Arrays.asList());
        when(prescriptionMapper.selectList(any(QueryWrapper.class))).thenReturn(Arrays.asList());

        // 执行和验证
        BusinessException exception = assertThrows(BusinessException.class, 
            () -> medicalRecordService.addDiagnosis(1L, diagnosis));
        assertEquals("3016", exception.getErrorCode());
        assertTrue(exception.getMessage().contains("不允许添加诊断"));
    }

    @Test
    void testUpdateDiagnosis_Success() {
        // Mock
        when(diagnosisMapper.selectById(1L)).thenReturn(diagnosis);
        when(medicalRecordMapper.selectById(1L)).thenReturn(medicalRecord);
        when(diagnosisMapper.selectList(any(QueryWrapper.class))).thenReturn(Arrays.asList());
        when(prescriptionMapper.selectList(any(QueryWrapper.class))).thenReturn(Arrays.asList());
        when(diagnosisMapper.updateById(any(Diagnosis.class))).thenReturn(1);

        // 执行
        Diagnosis updatedDiagnosis = new Diagnosis();
        updatedDiagnosis.setDiagnosisName("更新的诊断");
        Diagnosis result = medicalRecordService.updateDiagnosis(1L, updatedDiagnosis);

        // 验证
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(1L, result.getMedicalRecordId());

        verify(diagnosisMapper).updateById(any(Diagnosis.class));
    }

    @Test
    void testDeleteDiagnosis_Success() {
        // Mock
        when(diagnosisMapper.selectById(1L)).thenReturn(diagnosis);
        when(medicalRecordMapper.selectById(1L)).thenReturn(medicalRecord);
        when(diagnosisMapper.selectList(any(QueryWrapper.class))).thenReturn(Arrays.asList());
        when(prescriptionMapper.selectList(any(QueryWrapper.class))).thenReturn(Arrays.asList());
        when(diagnosisMapper.deleteById(1L)).thenReturn(1);

        // 执行
        assertDoesNotThrow(() -> medicalRecordService.deleteDiagnosis(1L));

        // 验证
        verify(diagnosisMapper).deleteById(1L);
    }

    @Test
    void testAddPrescription_Success() {
        // Mock
        when(medicalRecordMapper.selectById(1L)).thenReturn(medicalRecord);
        when(diagnosisMapper.selectList(any(QueryWrapper.class))).thenReturn(Arrays.asList());
        when(prescriptionMapper.selectList(any(QueryWrapper.class))).thenReturn(Arrays.asList());
        when(prescriptionMapper.insert(any(Prescription.class))).thenReturn(1);

        // 执行
        Prescription result = medicalRecordService.addPrescription(1L, prescription);

        // 验证
        assertNotNull(result);
        assertEquals(1L, result.getMedicalRecordId());
        assertNotNull(result.getPrescribedAt());

        verify(prescriptionMapper).insert(any(Prescription.class));
    }

    @Test
    void testSubmitForReview_Success() {
        // Mock
        when(medicalRecordMapper.selectById(1L)).thenReturn(medicalRecord);
        when(diagnosisMapper.selectList(any(QueryWrapper.class))).thenReturn(Arrays.asList());
        when(prescriptionMapper.selectList(any(QueryWrapper.class))).thenReturn(Arrays.asList());
        when(medicalRecordMapper.updateById(any(MedicalRecord.class))).thenReturn(1);

        // 执行
        assertDoesNotThrow(() -> medicalRecordService.submitForReview(1L));

        // 验证
        verify(medicalRecordMapper).updateById(any(MedicalRecord.class));
    }

    @Test
    void testSubmitForReview_InvalidStatus() {
        // 设置病历状态为已审核
        medicalRecord.setStatus("已审核");
        when(medicalRecordMapper.selectById(1L)).thenReturn(medicalRecord);
        when(diagnosisMapper.selectList(any(QueryWrapper.class))).thenReturn(Arrays.asList());
        when(prescriptionMapper.selectList(any(QueryWrapper.class))).thenReturn(Arrays.asList());

        // 执行和验证
        BusinessException exception = assertThrows(BusinessException.class, 
            () -> medicalRecordService.submitForReview(1L));
        assertEquals("3032", exception.getErrorCode());
        assertEquals("只有草稿状态的病历才能提交审核", exception.getMessage());
    }

    @Test
    void testReviewMedicalRecord_Approve() {
        // 设置病历状态为待审核
        medicalRecord.setStatus("待审核");
        when(medicalRecordMapper.selectById(1L)).thenReturn(medicalRecord);
        when(diagnosisMapper.selectList(any(QueryWrapper.class))).thenReturn(Arrays.asList());
        when(prescriptionMapper.selectList(any(QueryWrapper.class))).thenReturn(Arrays.asList());
        when(medicalRecordMapper.updateById(any(MedicalRecord.class))).thenReturn(1);

        // 执行
        assertDoesNotThrow(() -> medicalRecordService.reviewMedicalRecord(1L, 3L, true, "审核通过"));

        // 验证
        verify(medicalRecordMapper).updateById(any(MedicalRecord.class));
    }

    @Test
    void testReviewMedicalRecord_Reject() {
        // 设置病历状态为待审核
        medicalRecord.setStatus("待审核");
        when(medicalRecordMapper.selectById(1L)).thenReturn(medicalRecord);
        when(diagnosisMapper.selectList(any(QueryWrapper.class))).thenReturn(Arrays.asList());
        when(prescriptionMapper.selectList(any(QueryWrapper.class))).thenReturn(Arrays.asList());
        when(medicalRecordMapper.updateById(any(MedicalRecord.class))).thenReturn(1);

        // 执行
        assertDoesNotThrow(() -> medicalRecordService.reviewMedicalRecord(1L, 3L, false, "需要修改"));

        // 验证
        verify(medicalRecordMapper).updateById(any(MedicalRecord.class));
    }

    @Test
    void testArchiveMedicalRecord_Success() {
        // 设置病历状态为已审核
        medicalRecord.setStatus("已审核");
        when(medicalRecordMapper.selectById(1L)).thenReturn(medicalRecord);
        when(diagnosisMapper.selectList(any(QueryWrapper.class))).thenReturn(Arrays.asList());
        when(prescriptionMapper.selectList(any(QueryWrapper.class))).thenReturn(Arrays.asList());
        when(medicalRecordMapper.updateById(any(MedicalRecord.class))).thenReturn(1);

        // 执行
        assertDoesNotThrow(() -> medicalRecordService.archiveMedicalRecord(1L));

        // 验证
        verify(medicalRecordMapper).updateById(any(MedicalRecord.class));
    }

    @Test
    void testDeleteMedicalRecord_Success() {
        // Mock
        when(medicalRecordMapper.selectById(1L)).thenReturn(medicalRecord);
        when(diagnosisMapper.selectList(any(QueryWrapper.class))).thenReturn(Arrays.asList());
        when(prescriptionMapper.selectList(any(QueryWrapper.class))).thenReturn(Arrays.asList());
        when(medicalRecordMapper.updateById(any(MedicalRecord.class))).thenReturn(1);

        // 执行
        assertDoesNotThrow(() -> medicalRecordService.deleteMedicalRecord(1L));

        // 验证
        verify(medicalRecordMapper).updateById(any(MedicalRecord.class));
    }

    @Test
    void testDeleteMedicalRecord_CannotDelete() {
        // 设置病历状态为已审核
        medicalRecord.setStatus("已审核");
        when(medicalRecordMapper.selectById(1L)).thenReturn(medicalRecord);
        when(diagnosisMapper.selectList(any(QueryWrapper.class))).thenReturn(Arrays.asList());
        when(prescriptionMapper.selectList(any(QueryWrapper.class))).thenReturn(Arrays.asList());

        // 执行和验证
        BusinessException exception = assertThrows(BusinessException.class, 
            () -> medicalRecordService.deleteMedicalRecord(1L));
        assertEquals("3036", exception.getErrorCode());
        assertEquals("已审核或已归档的病历不能删除", exception.getMessage());
    }

    @Test
    void testCountPatientMedicalRecords_Success() {
        // Mock
        when(medicalRecordMapper.countByPatientId(1L)).thenReturn(5);

        // 执行
        int count = medicalRecordService.countPatientMedicalRecords(1L);

        // 验证
        assertEquals(5, count);
        verify(medicalRecordMapper).countByPatientId(1L);
    }

    @Test
    void testCountDoctorMedicalRecords_Success() {
        // Mock
        when(medicalRecordMapper.countByDoctorId(2L)).thenReturn(10);

        // 执行
        int count = medicalRecordService.countDoctorMedicalRecords(2L);

        // 验证
        assertEquals(10, count);
        verify(medicalRecordMapper).countByDoctorId(2L);
    }

    @Test
    void testExistsByRecordNumber_Success() {
        // Mock
        when(medicalRecordMapper.existsByRecordNumber("MR202401010001")).thenReturn(true);

        // 执行
        boolean exists = medicalRecordService.existsByRecordNumber("MR202401010001");

        // 验证
        assertTrue(exists);
        verify(medicalRecordMapper).existsByRecordNumber("MR202401010001");
    }

    @Test
    void testGenerateRecordNumber_Success() {
        // Mock
        when(medicalRecordMapper.existsByRecordNumber(anyString())).thenReturn(false);

        // 执行
        String recordNumber = medicalRecordService.generateRecordNumber(1L, 2L);

        // 验证
        assertNotNull(recordNumber);
        assertTrue(recordNumber.startsWith("MR"));
        assertTrue(recordNumber.length() > 10);
    }

    @Test
    void testGenerateRecordNumber_WithDuplicates() {
        // Mock - 第一次检查存在重复，第二次不存在
        when(medicalRecordMapper.existsByRecordNumber(anyString()))
            .thenReturn(true)
            .thenReturn(false);

        // 执行
        String recordNumber = medicalRecordService.generateRecordNumber(1L, 2L);

        // 验证
        assertNotNull(recordNumber);
        assertTrue(recordNumber.startsWith("MR"));
        verify(medicalRecordMapper, times(2)).existsByRecordNumber(anyString());
    }
}