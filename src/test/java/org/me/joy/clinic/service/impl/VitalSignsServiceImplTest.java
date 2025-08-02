package org.me.joy.clinic.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.me.joy.clinic.dto.VitalSignsRequest;
import org.me.joy.clinic.entity.Patient;
import org.me.joy.clinic.entity.VitalSigns;
import org.me.joy.clinic.exception.BusinessException;
import org.me.joy.clinic.exception.ValidationException;
import org.me.joy.clinic.mapper.PatientMapper;
import org.me.joy.clinic.mapper.VitalSignsMapper;
import org.me.joy.clinic.service.VitalSignsService;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * 生命体征服务实现类测试
 * 
 * @author Kiro
 */
@ExtendWith(MockitoExtension.class)
class VitalSignsServiceImplTest {

    @Mock
    private VitalSignsMapper vitalSignsMapper;

    @Mock
    private PatientMapper patientMapper;

    @InjectMocks
    private VitalSignsServiceImpl vitalSignsService;

    private VitalSignsRequest validRequest;
    private Patient testPatient;
    private VitalSigns testVitalSigns;

    @BeforeEach
    void setUp() {
        // 创建测试用的患者
        testPatient = new Patient();
        testPatient.setId(1L);
        testPatient.setName("测试患者");
        testPatient.setPatientNumber("P001");

        // 创建有效的生命体征请求
        validRequest = new VitalSignsRequest();
        validRequest.setPatientId(1L);
        validRequest.setSystolicBp(120);
        validRequest.setDiastolicBp(80);
        validRequest.setTemperature(new BigDecimal("36.5"));
        validRequest.setHeartRate(75);
        validRequest.setRespiratoryRate(16);
        validRequest.setOxygenSaturation(98);
        validRequest.setWeight(new BigDecimal("65.50"));
        validRequest.setHeight(170);
        validRequest.setPainScore(2);
        validRequest.setConsciousnessLevel("清醒");
        validRequest.setRemarks("正常");

        // 创建测试用的生命体征记录
        testVitalSigns = new VitalSigns();
        testVitalSigns.setId(1L);
        testVitalSigns.setPatientId(1L);
        testVitalSigns.setRecordedBy(1L);
        testVitalSigns.setSystolicBp(120);
        testVitalSigns.setDiastolicBp(80);
        testVitalSigns.setTemperature(new BigDecimal("36.5"));
        testVitalSigns.setHeartRate(75);
        testVitalSigns.setRespiratoryRate(16);
        testVitalSigns.setOxygenSaturation(98);
        testVitalSigns.setWeight(new BigDecimal("65.50"));
        testVitalSigns.setHeight(170);
        testVitalSigns.setBmi(new BigDecimal("22.7"));
        testVitalSigns.setPainScore(2);
        testVitalSigns.setConsciousnessLevel("清醒");
        testVitalSigns.setRemarks("正常");
        testVitalSigns.setIsAbnormal(false);
        testVitalSigns.setRecordedAt(LocalDateTime.now());
        testVitalSigns.setDeleted(false);
    }

    @Test
    void testRecordVitalSigns_Success() {
        // 模拟患者存在
        when(patientMapper.selectById(1L)).thenReturn(testPatient);
        when(vitalSignsMapper.insert(any(VitalSigns.class))).thenReturn(1);

        VitalSigns result = vitalSignsService.recordVitalSigns(validRequest, 1L);

        assertNotNull(result);
        assertEquals(validRequest.getPatientId(), result.getPatientId());
        assertEquals(1L, result.getRecordedBy());
        assertEquals(validRequest.getSystolicBp(), result.getSystolicBp());
        assertEquals(validRequest.getDiastolicBp(), result.getDiastolicBp());
        assertEquals(validRequest.getTemperature(), result.getTemperature());
        assertEquals(validRequest.getHeartRate(), result.getHeartRate());
        assertEquals(validRequest.getRespiratoryRate(), result.getRespiratoryRate());
        assertEquals(validRequest.getOxygenSaturation(), result.getOxygenSaturation());
        assertEquals(validRequest.getWeight(), result.getWeight());
        assertEquals(validRequest.getHeight(), result.getHeight());
        assertEquals(validRequest.getPainScore(), result.getPainScore());
        assertEquals(validRequest.getConsciousnessLevel(), result.getConsciousnessLevel());
        assertEquals(validRequest.getRemarks(), result.getRemarks());
        assertNotNull(result.getBmi());
        assertFalse(result.getIsAbnormal());

        verify(patientMapper).selectById(1L);
        verify(vitalSignsMapper).insert(any(VitalSigns.class));
    }

    @Test
    void testRecordVitalSigns_PatientNotFound() {
        // 模拟患者不存在
        when(patientMapper.selectById(1L)).thenReturn(null);

        BusinessException exception = assertThrows(BusinessException.class, () -> {
            vitalSignsService.recordVitalSigns(validRequest, 1L);
        });

        assertEquals("3001", exception.getErrorCode());
        assertEquals("患者不存在", exception.getMessage());

        verify(patientMapper).selectById(1L);
        verify(vitalSignsMapper, never()).insert(any(VitalSigns.class));
    }

    @Test
    void testRecordVitalSigns_InvalidBloodPressure() {
        // 设置无效的血压数据（收缩压小于舒张压）
        validRequest.setSystolicBp(80);
        validRequest.setDiastolicBp(120);

        when(patientMapper.selectById(1L)).thenReturn(testPatient);

        ValidationException exception = assertThrows(ValidationException.class, () -> {
            vitalSignsService.recordVitalSigns(validRequest, 1L);
        });

        assertEquals("4001", exception.getErrorCode());
        assertTrue(exception.getMessage().contains("收缩压必须大于舒张压"));

        verify(patientMapper).selectById(1L);
        verify(vitalSignsMapper, never()).insert(any(VitalSigns.class));
    }

    @Test
    void testRecordVitalSigns_AbnormalValues() {
        // 设置异常的生命体征数据
        validRequest.setSystolicBp(160); // 高血压
        validRequest.setTemperature(new BigDecimal("38.5")); // 发热
        validRequest.setHeartRate(110); // 心动过速
        validRequest.setOxygenSaturation(90); // 血氧饱和度低

        when(patientMapper.selectById(1L)).thenReturn(testPatient);
        when(vitalSignsMapper.insert(any(VitalSigns.class))).thenReturn(1);

        VitalSigns result = vitalSignsService.recordVitalSigns(validRequest, 1L);

        assertNotNull(result);
        assertTrue(result.getIsAbnormal());
        assertNotNull(result.getAbnormalIndicators());
        assertTrue(result.getAbnormalIndicators().contains("收缩压异常"));
        assertTrue(result.getAbnormalIndicators().contains("体温异常"));
        assertTrue(result.getAbnormalIndicators().contains("心率异常"));
        assertTrue(result.getAbnormalIndicators().contains("血氧饱和度异常"));

        verify(patientMapper).selectById(1L);
        verify(vitalSignsMapper).insert(any(VitalSigns.class));
    }

    @Test
    void testGetVitalSignsById_Success() {
        when(vitalSignsMapper.selectById(1L)).thenReturn(testVitalSigns);

        VitalSigns result = vitalSignsService.getVitalSignsById(1L);

        assertNotNull(result);
        assertEquals(testVitalSigns.getId(), result.getId());
        assertEquals(testVitalSigns.getPatientId(), result.getPatientId());

        verify(vitalSignsMapper).selectById(1L);
    }

    @Test
    void testGetVitalSignsById_NotFound() {
        when(vitalSignsMapper.selectById(1L)).thenReturn(null);

        BusinessException exception = assertThrows(BusinessException.class, () -> {
            vitalSignsService.getVitalSignsById(1L);
        });

        assertEquals("4003", exception.getErrorCode());
        assertEquals("生命体征记录不存在", exception.getMessage());

        verify(vitalSignsMapper).selectById(1L);
    }

    @Test
    void testGetVitalSignsById_Deleted() {
        testVitalSigns.setDeleted(true);
        when(vitalSignsMapper.selectById(1L)).thenReturn(testVitalSigns);

        BusinessException exception = assertThrows(BusinessException.class, () -> {
            vitalSignsService.getVitalSignsById(1L);
        });

        assertEquals("4003", exception.getErrorCode());
        assertEquals("生命体征记录不存在", exception.getMessage());

        verify(vitalSignsMapper).selectById(1L);
    }

    @Test
    void testGetPatientVitalSigns_Success() {
        List<VitalSigns> vitalSignsList = Arrays.asList(testVitalSigns);
        
        when(patientMapper.selectById(1L)).thenReturn(testPatient);
        when(vitalSignsMapper.findByPatientId(1L)).thenReturn(vitalSignsList);

        List<VitalSigns> result = vitalSignsService.getPatientVitalSigns(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testVitalSigns.getId(), result.get(0).getId());

        verify(patientMapper).selectById(1L);
        verify(vitalSignsMapper).findByPatientId(1L);
    }

    @Test
    void testGetPatientVitalSigns_PatientNotFound() {
        when(patientMapper.selectById(1L)).thenReturn(null);

        BusinessException exception = assertThrows(BusinessException.class, () -> {
            vitalSignsService.getPatientVitalSigns(1L);
        });

        assertEquals("3001", exception.getErrorCode());
        assertEquals("患者不存在", exception.getMessage());

        verify(patientMapper).selectById(1L);
        verify(vitalSignsMapper, never()).findByPatientId(1L);
    }

    @Test
    void testGetPatientVitalSignsByTimeRange_Success() {
        LocalDateTime startTime = LocalDateTime.now().minusDays(7);
        LocalDateTime endTime = LocalDateTime.now();
        List<VitalSigns> vitalSignsList = Arrays.asList(testVitalSigns);
        
        when(patientMapper.selectById(1L)).thenReturn(testPatient);
        when(vitalSignsMapper.findByPatientIdAndTimeRange(1L, startTime, endTime)).thenReturn(vitalSignsList);

        List<VitalSigns> result = vitalSignsService.getPatientVitalSignsByTimeRange(1L, startTime, endTime);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testVitalSigns.getId(), result.get(0).getId());

        verify(patientMapper).selectById(1L);
        verify(vitalSignsMapper).findByPatientIdAndTimeRange(1L, startTime, endTime);
    }

    @Test
    void testGetPatientVitalSignsByTimeRange_InvalidTimeRange() {
        LocalDateTime startTime = LocalDateTime.now();
        LocalDateTime endTime = LocalDateTime.now().minusDays(7);
        
        when(patientMapper.selectById(1L)).thenReturn(testPatient);

        ValidationException exception = assertThrows(ValidationException.class, () -> {
            vitalSignsService.getPatientVitalSignsByTimeRange(1L, startTime, endTime);
        });

        assertEquals("4004", exception.getErrorCode());
        assertEquals("开始时间不能晚于结束时间", exception.getMessage());

        verify(patientMapper).selectById(1L);
        verify(vitalSignsMapper, never()).findByPatientIdAndTimeRange(anyLong(), any(), any());
    }

    @Test
    void testGetLatestVitalSigns_Success() {
        when(patientMapper.selectById(1L)).thenReturn(testPatient);
        when(vitalSignsMapper.findLatestByPatientId(1L)).thenReturn(testVitalSigns);

        VitalSigns result = vitalSignsService.getLatestVitalSigns(1L);

        assertNotNull(result);
        assertEquals(testVitalSigns.getId(), result.getId());

        verify(patientMapper).selectById(1L);
        verify(vitalSignsMapper).findLatestByPatientId(1L);
    }

    @Test
    void testGetAbnormalVitalSigns_Success() {
        testVitalSigns.setIsAbnormal(true);
        testVitalSigns.setAbnormalIndicators("收缩压异常;体温异常;");
        List<VitalSigns> abnormalVitalSigns = Arrays.asList(testVitalSigns);
        
        LocalDateTime startTime = LocalDateTime.now().minusDays(7);
        LocalDateTime endTime = LocalDateTime.now();
        
        when(vitalSignsMapper.findAbnormalVitalSigns(1L, startTime, endTime)).thenReturn(abnormalVitalSigns);

        List<VitalSigns> result = vitalSignsService.getAbnormalVitalSigns(1L, startTime, endTime);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.get(0).getIsAbnormal());

        verify(vitalSignsMapper).findAbnormalVitalSigns(1L, startTime, endTime);
    }

    @Test
    void testUpdateVitalSigns_Success() {
        VitalSignsRequest updateRequest = new VitalSignsRequest();
        updateRequest.setPatientId(1L);
        updateRequest.setSystolicBp(130);
        updateRequest.setDiastolicBp(85);
        updateRequest.setTemperature(new BigDecimal("37.0"));
        updateRequest.setHeartRate(80);
        updateRequest.setRespiratoryRate(18);
        updateRequest.setOxygenSaturation(97);
        updateRequest.setWeight(new BigDecimal("66.00"));
        updateRequest.setHeight(170);
        updateRequest.setPainScore(3);
        updateRequest.setConsciousnessLevel("清醒");
        updateRequest.setRemarks("轻微不适");

        when(vitalSignsMapper.selectById(1L)).thenReturn(testVitalSigns);
        when(vitalSignsMapper.updateById(any(VitalSigns.class))).thenReturn(1);

        VitalSigns result = vitalSignsService.updateVitalSigns(1L, updateRequest, 1L);

        assertNotNull(result);
        assertEquals(updateRequest.getSystolicBp(), result.getSystolicBp());
        assertEquals(updateRequest.getDiastolicBp(), result.getDiastolicBp());
        assertEquals(updateRequest.getTemperature(), result.getTemperature());
        assertEquals(updateRequest.getHeartRate(), result.getHeartRate());
        assertEquals(updateRequest.getRespiratoryRate(), result.getRespiratoryRate());
        assertEquals(updateRequest.getOxygenSaturation(), result.getOxygenSaturation());
        assertEquals(updateRequest.getWeight(), result.getWeight());
        assertEquals(updateRequest.getHeight(), result.getHeight());
        assertEquals(updateRequest.getPainScore(), result.getPainScore());
        assertEquals(updateRequest.getConsciousnessLevel(), result.getConsciousnessLevel());
        assertEquals(updateRequest.getRemarks(), result.getRemarks());

        verify(vitalSignsMapper).selectById(1L);
        verify(vitalSignsMapper).updateById(any(VitalSigns.class));
    }

    @Test
    void testDeleteVitalSigns_Success() {
        when(vitalSignsMapper.selectById(1L)).thenReturn(testVitalSigns);
        when(vitalSignsMapper.updateById(any(VitalSigns.class))).thenReturn(1);

        assertDoesNotThrow(() -> {
            vitalSignsService.deleteVitalSigns(1L, 1L);
        });

        verify(vitalSignsMapper).selectById(1L);
        verify(vitalSignsMapper).updateById(any(VitalSigns.class));
    }

    @Test
    void testValidateVitalSigns_ValidData() {
        VitalSignsService.VitalSignsValidationResult result = vitalSignsService.validateVitalSigns(validRequest);

        assertTrue(result.isValid());
        assertFalse(result.isHasWarnings());
        assertTrue(result.getErrors().isEmpty());
        assertTrue(result.getWarnings().isEmpty());
    }

    @Test
    void testValidateVitalSigns_InvalidBloodPressure() {
        validRequest.setSystolicBp(80);
        validRequest.setDiastolicBp(120);

        VitalSignsService.VitalSignsValidationResult result = vitalSignsService.validateVitalSigns(validRequest);

        assertFalse(result.isValid());
        assertFalse(result.getErrors().isEmpty());
        assertTrue(result.getErrors().contains("收缩压必须大于舒张压"));
    }

    @Test
    void testValidateVitalSigns_WithWarnings() {
        validRequest.setSystolicBp(160); // 高血压
        validRequest.setTemperature(new BigDecimal("38.0")); // 发热
        validRequest.setHeartRate(110); // 心动过速

        VitalSignsService.VitalSignsValidationResult result = vitalSignsService.validateVitalSigns(validRequest);

        assertTrue(result.isValid());
        assertTrue(result.isHasWarnings());
        assertFalse(result.getWarnings().isEmpty());
        assertTrue(result.getWarnings().stream().anyMatch(w -> w.contains("收缩压异常")));
        assertTrue(result.getWarnings().stream().anyMatch(w -> w.contains("体温异常")));
        assertTrue(result.getWarnings().stream().anyMatch(w -> w.contains("心率异常")));
    }

    @Test
    void testGetVitalSignsByRecorder_Success() {
        LocalDateTime startTime = LocalDateTime.now().minusDays(7);
        LocalDateTime endTime = LocalDateTime.now();
        List<VitalSigns> vitalSignsList = Arrays.asList(testVitalSigns);
        
        when(vitalSignsMapper.findByRecordedByAndTimeRange(1L, startTime, endTime)).thenReturn(vitalSignsList);

        List<VitalSigns> result = vitalSignsService.getVitalSignsByRecorder(1L, startTime, endTime);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testVitalSigns.getId(), result.get(0).getId());

        verify(vitalSignsMapper).findByRecordedByAndTimeRange(1L, startTime, endTime);
    }
}