package org.me.joy.clinic.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.me.joy.clinic.dto.DoctorPerformanceReport;
import org.me.joy.clinic.dto.PatientVisitAnalytics;
import org.me.joy.clinic.dto.PopularService;
import org.me.joy.clinic.dto.WaitTimeAnalytics;
import org.me.joy.clinic.service.OperationalAnalyticsService;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

/**
 * 运营分析控制器测试
 */
@ExtendWith(MockitoExtension.class)
class OperationalAnalyticsControllerTest {

    @Mock
    private OperationalAnalyticsService operationalAnalyticsService;

    @InjectMocks
    private OperationalAnalyticsController operationalAnalyticsController;

    private LocalDate startDate;
    private LocalDate endDate;

    @BeforeEach
    void setUp() {
        startDate = LocalDate.of(2024, 1, 1);
        endDate = LocalDate.of(2024, 1, 31);
    }

    @Test
    void testGetPatientVisitAnalytics() {
        // Given
        PatientVisitAnalytics analytics = new PatientVisitAnalytics(
            startDate, 150L, 50L, 100L, 4.84, 25L, "09:00-10:00"
        );
        when(operationalAnalyticsService.getPatientVisitAnalytics(startDate, endDate))
            .thenReturn(analytics);

        // When
        ResponseEntity<PatientVisitAnalytics> response = operationalAnalyticsController.getPatientVisitAnalytics(startDate, endDate);

        // Then
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        PatientVisitAnalytics result = response.getBody();
        assertNotNull(result);
        assertEquals(150L, result.getTotalVisits());
        assertEquals(50L, result.getNewPatients());
        assertEquals(100L, result.getReturningPatients());
        assertEquals(4.84, result.getAverageVisitsPerDay());
        assertEquals(25L, result.getPeakHourVisits());
        assertEquals("09:00-10:00", result.getPeakHour());
    }

    @Test
    void testGetPopularServices() {
        // Given
        List<PopularService> services = Arrays.asList(
            new PopularService("内科科", "INTERNAL", 80L, 53.33, "内科"),
            new PopularService("外科科", "SURGERY", 70L, 46.67, "外科")
        );
        when(operationalAnalyticsService.getPopularServices(startDate, endDate))
            .thenReturn(services);

        // When
        ResponseEntity<List<PopularService>> response = operationalAnalyticsController.getPopularServices(startDate, endDate);

        // Then
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        List<PopularService> result = response.getBody();
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("内科科", result.get(0).getServiceName());
        assertEquals(80L, result.get(0).getVisitCount());
        assertEquals(53.33, result.get(0).getPercentage());
        assertEquals("外科科", result.get(1).getServiceName());
        assertEquals(70L, result.get(1).getVisitCount());
    }

    @Test
    void testGetDoctorPerformanceReport() {
        // Given
        Long doctorId = 1L;
        DoctorPerformanceReport report = new DoctorPerformanceReport(
            doctorId, "张医生", "内科", startDate, 50L, 45L, 25.5, 4.5, 40L, 15000.0
        );
        when(operationalAnalyticsService.getDoctorPerformanceReport(doctorId, startDate, endDate))
            .thenReturn(report);

        // When
        ResponseEntity<DoctorPerformanceReport> response = operationalAnalyticsController.getDoctorPerformanceReport(doctorId, startDate, endDate);

        // Then
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        DoctorPerformanceReport result = response.getBody();
        assertNotNull(result);
        assertEquals(1L, result.getDoctorId());
        assertEquals("张医生", result.getDoctorName());
        assertEquals("内科", result.getDepartment());
        assertEquals(50L, result.getTotalPatients());
        assertEquals(45L, result.getCompletedConsultations());
        assertEquals(25.5, result.getAverageConsultationTime());
        assertEquals(4.5, result.getPatientSatisfactionScore());
        assertEquals(40L, result.getPrescriptionsIssued());
        assertEquals(15000.0, result.getRevenueGenerated());
    }

    @Test
    void testGetDoctorPerformanceReportNotFound() {
        // Given
        Long doctorId = 999L;
        when(operationalAnalyticsService.getDoctorPerformanceReport(doctorId, startDate, endDate))
            .thenReturn(null);

        // When
        ResponseEntity<DoctorPerformanceReport> response = operationalAnalyticsController.getDoctorPerformanceReport(doctorId, startDate, endDate);

        // Then
        assertNotNull(response);
        assertEquals(404, response.getStatusCodeValue());
        assertNull(response.getBody());
    }

    @Test
    void testGetAllDoctorsPerformanceReport() {
        // Given
        List<DoctorPerformanceReport> reports = Arrays.asList(
            new DoctorPerformanceReport(1L, "张医生", "内科", startDate, 50L, 45L, 25.5, 4.5, 40L, 15000.0),
            new DoctorPerformanceReport(2L, "李医生", "外科", startDate, 30L, 28L, 20.0, 4.2, 25L, 12000.0)
        );
        when(operationalAnalyticsService.getAllDoctorsPerformanceReport(startDate, endDate))
            .thenReturn(reports);

        // When
        ResponseEntity<List<DoctorPerformanceReport>> response = operationalAnalyticsController.getAllDoctorsPerformanceReport(startDate, endDate);

        // Then
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        List<DoctorPerformanceReport> result = response.getBody();
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("张医生", result.get(0).getDoctorName());
        assertEquals(50L, result.get(0).getTotalPatients());
        assertEquals("李医生", result.get(1).getDoctorName());
        assertEquals(30L, result.get(1).getTotalPatients());
    }

    @Test
    void testGetWaitTimeAnalytics() {
        // Given
        WaitTimeAnalytics analytics = new WaitTimeAnalytics(
            startDate, 25.0, 22.0, 45.0, 10.0, 100L, 80L, 80.0, "10:00-11:00"
        );
        when(operationalAnalyticsService.getWaitTimeAnalytics(startDate, endDate))
            .thenReturn(analytics);

        // When
        ResponseEntity<WaitTimeAnalytics> response = operationalAnalyticsController.getWaitTimeAnalytics(startDate, endDate);

        // Then
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        WaitTimeAnalytics result = response.getBody();
        assertNotNull(result);
        assertEquals(25.0, result.getAverageWaitTime());
        assertEquals(22.0, result.getMedianWaitTime());
        assertEquals(45.0, result.getMaxWaitTime());
        assertEquals(10.0, result.getMinWaitTime());
        assertEquals(100L, result.getTotalPatients());
        assertEquals(80L, result.getPatientsWithinTarget());
        assertEquals(80.0, result.getTargetComplianceRate());
        assertEquals("10:00-11:00", result.getPeakWaitTimeHour());
    }

    @Test
    void testGetDepartmentVisitStatistics() {
        // Given
        List<PopularService> statistics = Arrays.asList(
            new PopularService("内科科", "INTERNAL", 80L, 53.33, "内科"),
            new PopularService("外科科", "SURGERY", 70L, 46.67, "外科")
        );
        when(operationalAnalyticsService.getDepartmentVisitStatistics(startDate, endDate))
            .thenReturn(statistics);

        // When
        ResponseEntity<List<PopularService>> response = operationalAnalyticsController.getDepartmentVisitStatistics(startDate, endDate);

        // Then
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        List<PopularService> result = response.getBody();
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("内科科", result.get(0).getServiceName());
        assertEquals("内科", result.get(0).getDepartment());
        assertEquals("外科科", result.get(1).getServiceName());
        assertEquals("外科", result.get(1).getDepartment());
    }
}