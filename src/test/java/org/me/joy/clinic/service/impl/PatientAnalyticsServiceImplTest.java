package org.me.joy.clinic.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.me.joy.clinic.dto.CommonDiagnosis;
import org.me.joy.clinic.dto.PatientDemographics;
import org.me.joy.clinic.dto.PatientRetentionReport;
import org.me.joy.clinic.dto.PatientSatisfactionScore;
import org.me.joy.clinic.mapper.DiagnosisMapper;
import org.me.joy.clinic.mapper.PatientMapper;
import org.me.joy.clinic.mapper.RegistrationMapper;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * 患者分析服务测试
 */
@ExtendWith(MockitoExtension.class)
class PatientAnalyticsServiceImplTest {
    
    @Mock
    private PatientMapper patientMapper;
    
    @Mock
    private DiagnosisMapper diagnosisMapper;
    
    @Mock
    private RegistrationMapper registrationMapper;
    
    @InjectMocks
    private PatientAnalyticsServiceImpl patientAnalyticsService;
    
    @BeforeEach
    void setUp() {
        // Setup common mock data
    }
    
    @Test
    void testGetPatientDemographics() {
        // Given
        Long totalPatients = 1000L;
        List<Map<String, Object>> genderData = Arrays.asList(
            createMap("gender", "男", "count", 600L),
            createMap("gender", "女", "count", 400L)
        );
        List<Map<String, Object>> patientBirthData = Arrays.asList(
            createMap("id", 1L, "birth_date", LocalDate.of(1990, 1, 1)),
            createMap("id", 2L, "birth_date", LocalDate.of(1985, 5, 15)),
            createMap("id", 3L, "birth_date", LocalDate.of(2000, 12, 31))
        );
        List<Map<String, Object>> locationData = Arrays.asList(
            createMap("location", "北京", "count", 300L),
            createMap("location", "上海", "count", 200L)
        );
        
        when(patientMapper.countTotalPatients()).thenReturn(totalPatients);
        when(patientMapper.getGenderDistribution()).thenReturn(genderData);
        when(patientMapper.getAllPatientsWithBirthDate()).thenReturn(patientBirthData);
        when(patientMapper.getLocationDistribution()).thenReturn(locationData);
        
        // When
        PatientDemographics result = patientAnalyticsService.getPatientDemographics();
        
        // Then
        assertNotNull(result);
        assertEquals(totalPatients, result.getTotalPatients());
        assertEquals(2, result.getGenderDistribution().size());
        assertEquals(600L, result.getGenderDistribution().get("男"));
        assertEquals(400L, result.getGenderDistribution().get("女"));
        assertNotNull(result.getAverageAge());
        assertTrue(result.getAverageAge().compareTo(BigDecimal.ZERO) > 0);
        assertEquals(2, result.getLocationDistribution().size());
        
        verify(patientMapper).countTotalPatients();
        verify(patientMapper).getGenderDistribution();
        verify(patientMapper).getAllPatientsWithBirthDate();
        verify(patientMapper).getLocationDistribution();
    }
    
    @Test
    void testGetCommonDiagnoses() {
        // Given
        LocalDate startDate = LocalDate.of(2024, 1, 1);
        LocalDate endDate = LocalDate.of(2024, 12, 31);
        Long totalDiagnoses = 500L;
        
        List<Map<String, Object>> diagnosisData = Arrays.asList(
            createDiagnosisMap("J00", "急性鼻咽炎", 100L),
            createDiagnosisMap("K59", "功能性肠病", 80L),
            createDiagnosisMap("M79", "软组织疾病", 60L)
        );
        
        when(diagnosisMapper.getCommonDiagnoses(startDate, endDate)).thenReturn(diagnosisData);
        when(diagnosisMapper.countTotalDiagnoses(startDate, endDate)).thenReturn(totalDiagnoses);
        
        // When
        List<CommonDiagnosis> result = patientAnalyticsService.getCommonDiagnoses(startDate, endDate);
        
        // Then
        assertNotNull(result);
        assertEquals(3, result.size());
        
        CommonDiagnosis firstDiagnosis = result.get(0);
        assertEquals("J00", firstDiagnosis.getDiagnosisCode());
        assertEquals("急性鼻咽炎", firstDiagnosis.getDiagnosisName());
        assertEquals(100L, firstDiagnosis.getCount());
        assertEquals(20.0, firstDiagnosis.getPercentage(), 0.01);
        
        verify(diagnosisMapper).getCommonDiagnoses(startDate, endDate);
        verify(diagnosisMapper).countTotalDiagnoses(startDate, endDate);
    }
    
    @Test
    void testGetCommonDiagnosesWithZeroTotal() {
        // Given
        LocalDate startDate = LocalDate.of(2024, 1, 1);
        LocalDate endDate = LocalDate.of(2024, 12, 31);
        Long totalDiagnoses = 0L;
        
        List<Map<String, Object>> diagnosisData = new ArrayList<>();
        
        when(diagnosisMapper.getCommonDiagnoses(startDate, endDate)).thenReturn(diagnosisData);
        when(diagnosisMapper.countTotalDiagnoses(startDate, endDate)).thenReturn(totalDiagnoses);
        
        // When
        List<CommonDiagnosis> result = patientAnalyticsService.getCommonDiagnoses(startDate, endDate);
        
        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
        
        verify(diagnosisMapper).getCommonDiagnoses(startDate, endDate);
        verify(diagnosisMapper).countTotalDiagnoses(startDate, endDate);
    }
    
    @Test
    void testGetPatientRetentionReport() {
        // Given
        Long newPatients = 50L;
        Long returningPatients = 150L;
        Long totalVisits = 300L;
        
        when(registrationMapper.countNewPatients(any(LocalDate.class), any(LocalDate.class)))
            .thenReturn(newPatients);
        when(registrationMapper.countReturningPatients(any(LocalDate.class), any(LocalDate.class)))
            .thenReturn(returningPatients);
        when(registrationMapper.countTotalVisits(any(LocalDate.class), any(LocalDate.class)))
            .thenReturn(totalVisits);
        
        // When
        PatientRetentionReport result = patientAnalyticsService.getPatientRetentionReport();
        
        // Then
        assertNotNull(result);
        assertEquals(newPatients, result.getNewPatients());
        assertEquals(returningPatients, result.getReturningPatients());
        assertEquals(totalVisits, result.getTotalVisits());
        
        // 留存率应该是 150 / (50 + 150) * 100 = 75%
        assertEquals(new BigDecimal("75.00"), result.getRetentionRate());
        
        // 平均就诊次数应该是 300 / (50 + 150) = 1.5
        assertEquals(new BigDecimal("1.50"), result.getAverageVisitsPerPatient());
        
        verify(registrationMapper).countNewPatients(any(LocalDate.class), any(LocalDate.class));
        verify(registrationMapper).countReturningPatients(any(LocalDate.class), any(LocalDate.class));
        verify(registrationMapper).countTotalVisits(any(LocalDate.class), any(LocalDate.class));
    }
    
    @Test
    void testGetPatientRetentionReportWithZeroPatients() {
        // Given
        Long newPatients = 0L;
        Long returningPatients = 0L;
        Long totalVisits = 0L;
        
        when(registrationMapper.countNewPatients(any(LocalDate.class), any(LocalDate.class)))
            .thenReturn(newPatients);
        when(registrationMapper.countReturningPatients(any(LocalDate.class), any(LocalDate.class)))
            .thenReturn(returningPatients);
        when(registrationMapper.countTotalVisits(any(LocalDate.class), any(LocalDate.class)))
            .thenReturn(totalVisits);
        
        // When
        PatientRetentionReport result = patientAnalyticsService.getPatientRetentionReport();
        
        // Then
        assertNotNull(result);
        assertEquals(newPatients, result.getNewPatients());
        assertEquals(returningPatients, result.getReturningPatients());
        assertEquals(totalVisits, result.getTotalVisits());
        assertEquals(BigDecimal.ZERO, result.getRetentionRate());
        assertEquals(BigDecimal.ZERO, result.getAverageVisitsPerPatient());
    }
    
    @Test
    void testGetPatientSatisfactionScores() {
        // When
        List<PatientSatisfactionScore> result = patientAnalyticsService.getPatientSatisfactionScores();
        
        // Then
        assertNotNull(result);
        assertEquals(4, result.size());
        
        // 验证服务质量满意度
        PatientSatisfactionScore serviceQuality = result.stream()
            .filter(score -> "服务质量".equals(score.getCategory()))
            .findFirst()
            .orElse(null);
        assertNotNull(serviceQuality);
        assertEquals(new BigDecimal("4.2"), serviceQuality.getAverageScore());
        assertEquals(150L, serviceQuality.getResponseCount());
        assertEquals(new BigDecimal("84.0"), serviceQuality.getSatisfactionRate());
        
        // 验证医疗技术满意度
        PatientSatisfactionScore medicalTechnology = result.stream()
            .filter(score -> "医疗技术".equals(score.getCategory()))
            .findFirst()
            .orElse(null);
        assertNotNull(medicalTechnology);
        assertEquals(new BigDecimal("4.5"), medicalTechnology.getAverageScore());
        assertEquals(new BigDecimal("90.0"), medicalTechnology.getSatisfactionRate());
    }
    
    @Test
    void testGetPatientDemographicsWithEmptyData() {
        // Given
        when(patientMapper.countTotalPatients()).thenReturn(0L);
        when(patientMapper.getGenderDistribution()).thenReturn(new ArrayList<>());
        when(patientMapper.getAllPatientsWithBirthDate()).thenReturn(new ArrayList<>());
        when(patientMapper.getLocationDistribution()).thenReturn(new ArrayList<>());
        
        // When
        PatientDemographics result = patientAnalyticsService.getPatientDemographics();
        
        // Then
        assertNotNull(result);
        assertEquals(0L, result.getTotalPatients());
        assertTrue(result.getGenderDistribution().isEmpty());
        assertTrue(result.getLocationDistribution().isEmpty());
        assertEquals(BigDecimal.ZERO, result.getAverageAge());
    }
    
    @Test
    void testAgeGroupCalculation() {
        // Given - 测试年龄组计算
        List<Map<String, Object>> patientBirthData = Arrays.asList(
            createMap("id", 1L, "birth_date", LocalDate.now().minusYears(10)), // 0-18
            createMap("id", 2L, "birth_date", LocalDate.now().minusYears(25)), // 19-35
            createMap("id", 3L, "birth_date", LocalDate.now().minusYears(40)), // 36-50
            createMap("id", 4L, "birth_date", LocalDate.now().minusYears(60)), // 51-65
            createMap("id", 5L, "birth_date", LocalDate.now().minusYears(70))  // 65+
        );
        
        when(patientMapper.countTotalPatients()).thenReturn(5L);
        when(patientMapper.getGenderDistribution()).thenReturn(new ArrayList<>());
        when(patientMapper.getAllPatientsWithBirthDate()).thenReturn(patientBirthData);
        when(patientMapper.getLocationDistribution()).thenReturn(new ArrayList<>());
        
        // When
        PatientDemographics result = patientAnalyticsService.getPatientDemographics();
        
        // Then
        Map<String, Long> ageGroups = result.getAgeGroupDistribution();
        assertEquals(1L, ageGroups.get("0-18"));
        assertEquals(1L, ageGroups.get("19-35"));
        assertEquals(1L, ageGroups.get("36-50"));
        assertEquals(1L, ageGroups.get("51-65"));
        assertEquals(1L, ageGroups.get("65+"));
    }
    
    /**
     * 创建测试用的Map对象
     */
    private Map<String, Object> createMap(String key1, Object value1, String key2, Object value2) {
        Map<String, Object> map = new HashMap<>();
        map.put(key1, value1);
        map.put(key2, value2);
        return map;
    }
    
    /**
     * 创建诊断数据Map
     */
    private Map<String, Object> createDiagnosisMap(String code, String name, Long count) {
        Map<String, Object> map = new HashMap<>();
        map.put("diagnosis_code", code);
        map.put("diagnosis_name", name);
        map.put("count", count);
        return map;
    }
}