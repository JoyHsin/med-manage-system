package org.me.joy.clinic.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.me.joy.clinic.dto.CommonDiagnosis;
import org.me.joy.clinic.dto.PatientDemographics;
import org.me.joy.clinic.dto.PatientRetentionReport;
import org.me.joy.clinic.dto.PatientSatisfactionScore;
import org.me.joy.clinic.service.PatientAnalyticsService;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

/**
 * 患者分析控制器测试
 */
@ExtendWith(MockitoExtension.class)
class PatientAnalyticsControllerTest {
    
    @Mock
    private PatientAnalyticsService patientAnalyticsService;
    
    @InjectMocks
    private PatientAnalyticsController patientAnalyticsController;
    
    private PatientDemographics mockDemographics;
    private List<CommonDiagnosis> mockDiagnoses;
    private PatientRetentionReport mockRetentionReport;
    private List<PatientSatisfactionScore> mockSatisfactionScores;
    
    @BeforeEach
    void setUp() {
        setupMockData();
    }
    
    @Test
    void testGetPatientDemographics() {
        // Given
        when(patientAnalyticsService.getPatientDemographics()).thenReturn(mockDemographics);
        
        // When
        ResponseEntity<PatientDemographics> response = patientAnalyticsController.getPatientDemographics();
        
        // Then
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(1000L, response.getBody().getTotalPatients());
        assertEquals(600L, response.getBody().getGenderDistribution().get("男"));
        assertEquals(400L, response.getBody().getGenderDistribution().get("女"));
        assertEquals(new BigDecimal("35.5"), response.getBody().getAverageAge());
    }
    
    @Test
    void testGetCommonDiagnoses() {
        // Given
        LocalDate startDate = LocalDate.of(2024, 1, 1);
        LocalDate endDate = LocalDate.of(2024, 12, 31);
        when(patientAnalyticsService.getCommonDiagnoses(startDate, endDate))
                .thenReturn(mockDiagnoses);
        
        // When
        ResponseEntity<List<CommonDiagnosis>> response = patientAnalyticsController.getCommonDiagnoses(startDate, endDate);
        
        // Then
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(3, response.getBody().size());
        assertEquals("J00", response.getBody().get(0).getDiagnosisCode());
        assertEquals("急性鼻咽炎", response.getBody().get(0).getDiagnosisName());
        assertEquals(100L, response.getBody().get(0).getCount());
        assertEquals(20.0, response.getBody().get(0).getPercentage());
    }
    
    @Test
    void testGetPatientRetentionReport() {
        // Given
        when(patientAnalyticsService.getPatientRetentionReport()).thenReturn(mockRetentionReport);
        
        // When
        ResponseEntity<PatientRetentionReport> response = patientAnalyticsController.getPatientRetentionReport();
        
        // Then
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(50L, response.getBody().getNewPatients());
        assertEquals(150L, response.getBody().getReturningPatients());
        assertEquals(new BigDecimal("75.00"), response.getBody().getRetentionRate());
        assertEquals(300L, response.getBody().getTotalVisits());
        assertEquals(new BigDecimal("1.50"), response.getBody().getAverageVisitsPerPatient());
    }
    
    @Test
    void testGetPatientSatisfactionScores() {
        // Given
        when(patientAnalyticsService.getPatientSatisfactionScores()).thenReturn(mockSatisfactionScores);
        
        // When
        ResponseEntity<List<PatientSatisfactionScore>> response = patientAnalyticsController.getPatientSatisfactionScores();
        
        // Then
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(4, response.getBody().size());
        
        // 验证服务质量满意度
        PatientSatisfactionScore serviceQuality = response.getBody().stream()
            .filter(score -> "服务质量".equals(score.getCategory()))
            .findFirst()
            .orElse(null);
        assertNotNull(serviceQuality);
        assertEquals(new BigDecimal("4.2"), serviceQuality.getAverageScore());
        assertEquals(150L, serviceQuality.getResponseCount());
        assertEquals(new BigDecimal("84.0"), serviceQuality.getSatisfactionRate());
    }
    
    private void setupMockData() {
        // Setup PatientDemographics
        Map<String, Long> genderDistribution = new HashMap<>();
        genderDistribution.put("男", 600L);
        genderDistribution.put("女", 400L);
        
        Map<String, Long> ageGroupDistribution = new HashMap<>();
        ageGroupDistribution.put("0-18", 100L);
        ageGroupDistribution.put("19-35", 300L);
        ageGroupDistribution.put("36-50", 250L);
        ageGroupDistribution.put("51-65", 200L);
        ageGroupDistribution.put("65+", 150L);
        
        Map<String, Long> locationDistribution = new HashMap<>();
        locationDistribution.put("北京", 300L);
        locationDistribution.put("上海", 200L);
        locationDistribution.put("广州", 150L);
        
        mockDemographics = new PatientDemographics(1000L, genderDistribution, 
                ageGroupDistribution, new BigDecimal("35.5"), locationDistribution);
        
        // Setup CommonDiagnosis list
        mockDiagnoses = Arrays.asList(
            new CommonDiagnosis("J00", "急性鼻咽炎", 100L, 20.0),
            new CommonDiagnosis("K59", "功能性肠病", 80L, 16.0),
            new CommonDiagnosis("M79", "软组织疾病", 60L, 12.0)
        );
        
        // Setup PatientRetentionReport
        mockRetentionReport = new PatientRetentionReport(
            LocalDate.now(), 50L, 150L, new BigDecimal("75.00"), 
            300L, new BigDecimal("1.50")
        );
        
        // Setup PatientSatisfactionScore list
        mockSatisfactionScores = Arrays.asList(
            new PatientSatisfactionScore(LocalDate.now(), "服务质量", 
                new BigDecimal("4.2"), 150L, new BigDecimal("84.0")),
            new PatientSatisfactionScore(LocalDate.now(), "医疗技术", 
                new BigDecimal("4.5"), 150L, new BigDecimal("90.0")),
            new PatientSatisfactionScore(LocalDate.now(), "环境设施", 
                new BigDecimal("4.0"), 150L, new BigDecimal("80.0")),
            new PatientSatisfactionScore(LocalDate.now(), "等待时间", 
                new BigDecimal("3.8"), 150L, new BigDecimal("76.0"))
        );
    }
}