package org.me.joy.clinic.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.me.joy.clinic.dto.DoctorPerformanceReport;
import org.me.joy.clinic.dto.PatientVisitAnalytics;
import org.me.joy.clinic.dto.PopularService;
import org.me.joy.clinic.dto.WaitTimeAnalytics;
import org.me.joy.clinic.entity.Staff;
import org.me.joy.clinic.mapper.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * 运营分析服务实现类测试
 */
@ExtendWith(MockitoExtension.class)
class OperationalAnalyticsServiceImplTest {

    @Mock
    private RegistrationMapper registrationMapper;

    @Mock
    private PatientQueueMapper patientQueueMapper;

    @Mock
    private MedicalRecordMapper medicalRecordMapper;

    @Mock
    private StaffMapper staffMapper;

    @Mock
    private BillMapper billMapper;

    @Mock
    private PrescriptionMapper prescriptionMapper;

    @Mock
    private AppointmentMapper appointmentMapper;

    @InjectMocks
    private OperationalAnalyticsServiceImpl operationalAnalyticsService;

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
        Long totalVisits = 150L;
        Long newPatients = 50L;
        Map<String, Object> hourlyData = new HashMap<>();
        hourlyData.put("hour", 9);
        hourlyData.put("count", 25L);
        List<Map<String, Object>> hourlyStats = Arrays.asList(hourlyData);

        when(registrationMapper.countRegistrationsByDateRange(startDate, endDate)).thenReturn(totalVisits);
        when(registrationMapper.countNewPatientsByDateRange(startDate, endDate)).thenReturn(newPatients);
        when(registrationMapper.getHourlyVisitStatistics(startDate, endDate)).thenReturn(hourlyStats);

        // When
        PatientVisitAnalytics result = operationalAnalyticsService.getPatientVisitAnalytics(startDate, endDate);

        // Then
        assertNotNull(result);
        assertEquals(totalVisits, result.getTotalVisits());
        assertEquals(newPatients, result.getNewPatients());
        assertEquals(100L, result.getReturningPatients()); // 150 - 50
        assertEquals(4.84, result.getAverageVisitsPerDay(), 0.01); // 150 / 31
        assertEquals("09:00-10:00", result.getPeakHour());
        assertEquals(25L, result.getPeakHourVisits());

        verify(registrationMapper).countRegistrationsByDateRange(startDate, endDate);
        verify(registrationMapper).countNewPatientsByDateRange(startDate, endDate);
        verify(registrationMapper).getHourlyVisitStatistics(startDate, endDate);
    }

    @Test
    void testGetPopularServices() {
        // Given
        Map<String, Object> deptData1 = new HashMap<>();
        deptData1.put("department", "内科");
        deptData1.put("count", 80L);
        
        Map<String, Object> deptData2 = new HashMap<>();
        deptData2.put("department", "外科");
        deptData2.put("count", 70L);

        List<Map<String, Object>> departmentStats = Arrays.asList(deptData1, deptData2);
        when(registrationMapper.getDepartmentVisitStatistics(startDate, endDate)).thenReturn(departmentStats);

        // When
        List<PopularService> result = operationalAnalyticsService.getPopularServices(startDate, endDate);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        
        PopularService first = result.get(0);
        assertEquals("内科科", first.getServiceName());
        assertEquals("内科", first.getServiceCode());
        assertEquals(80L, first.getVisitCount());
        assertEquals(53.33, first.getPercentage(), 0.01); // 80 / 150 * 100
        assertEquals("内科", first.getDepartment());

        PopularService second = result.get(1);
        assertEquals("外科科", second.getServiceName());
        assertEquals("外科", second.getServiceCode());
        assertEquals(70L, second.getVisitCount());
        assertEquals(46.67, second.getPercentage(), 0.01); // 70 / 150 * 100

        verify(registrationMapper).getDepartmentVisitStatistics(startDate, endDate);
    }

    @Test
    void testGetDoctorPerformanceReport() {
        // Given
        Long doctorId = 1L;
        Staff staff = new Staff();
        staff.setId(doctorId);
        staff.setName("张医生");
        staff.setDepartment("内科");

        when(staffMapper.selectById(doctorId)).thenReturn(staff);
        when(medicalRecordMapper.countPatientsByDoctorAndDateRange(doctorId, startDate, endDate)).thenReturn(50L);
        when(medicalRecordMapper.countCompletedConsultationsByDoctorAndDateRange(doctorId, startDate, endDate)).thenReturn(45L);
        when(medicalRecordMapper.getConsultationTimesByDoctorAndDateRange(doctorId, startDate, endDate)).thenReturn(new ArrayList<>());
        when(prescriptionMapper.countPrescriptionsByDoctorAndDateRange(doctorId, startDate, endDate)).thenReturn(40L);
        when(billMapper.sumRevenueByDoctorAndDateRange(doctorId, startDate, endDate)).thenReturn(15000.0);

        // When
        DoctorPerformanceReport result = operationalAnalyticsService.getDoctorPerformanceReport(doctorId, startDate, endDate);

        // Then
        assertNotNull(result);
        assertEquals(doctorId, result.getDoctorId());
        assertEquals("张医生", result.getDoctorName());
        assertEquals("内科", result.getDepartment());
        assertEquals(50L, result.getTotalPatients());
        assertEquals(45L, result.getCompletedConsultations());
        assertEquals(0.0, result.getAverageConsultationTime());
        assertEquals(4.5, result.getPatientSatisfactionScore());
        assertEquals(40L, result.getPrescriptionsIssued());
        assertEquals(15000.0, result.getRevenueGenerated());

        verify(staffMapper).selectById(doctorId);
        verify(medicalRecordMapper).countPatientsByDoctorAndDateRange(doctorId, startDate, endDate);
        verify(medicalRecordMapper).countCompletedConsultationsByDoctorAndDateRange(doctorId, startDate, endDate);
        verify(prescriptionMapper).countPrescriptionsByDoctorAndDateRange(doctorId, startDate, endDate);
        verify(billMapper).sumRevenueByDoctorAndDateRange(doctorId, startDate, endDate);
    }

    @Test
    void testGetDoctorPerformanceReportWhenDoctorNotFound() {
        // Given
        Long doctorId = 999L;
        when(staffMapper.selectById(doctorId)).thenReturn(null);

        // When
        DoctorPerformanceReport result = operationalAnalyticsService.getDoctorPerformanceReport(doctorId, startDate, endDate);

        // Then
        assertNull(result);
        verify(staffMapper).selectById(doctorId);
        verifyNoInteractions(medicalRecordMapper, prescriptionMapper, billMapper);
    }

    @Test
    void testGetWaitTimeAnalytics() {
        // Given
        List<Double> waitTimes = Arrays.asList(15.0, 20.0, 25.0, 30.0, 35.0);
        Map<String, Double> hourlyWaitTimes = new HashMap<>();
        hourlyWaitTimes.put("09:00-10:00", 25.0);
        hourlyWaitTimes.put("10:00-11:00", 30.0);

        when(patientQueueMapper.getWaitTimesByDateRange(startDate, endDate)).thenReturn(waitTimes);
        when(patientQueueMapper.getHourlyAverageWaitTimes(startDate, endDate)).thenReturn(hourlyWaitTimes);

        // When
        WaitTimeAnalytics result = operationalAnalyticsService.getWaitTimeAnalytics(startDate, endDate);

        // Then
        assertNotNull(result);
        assertEquals(25.0, result.getAverageWaitTime());
        assertEquals(25.0, result.getMedianWaitTime());
        assertEquals(35.0, result.getMaxWaitTime());
        assertEquals(15.0, result.getMinWaitTime());
        assertEquals(5L, result.getTotalPatients());
        assertEquals(4L, result.getPatientsWithinTarget()); // 15.0, 20.0, 25.0, 30.0 are <= 30.0
        assertEquals(80.0, result.getTargetComplianceRate()); // 4/5 * 100
        assertEquals("10:00-11:00", result.getPeakWaitTimeHour());

        verify(patientQueueMapper).getWaitTimesByDateRange(startDate, endDate);
        verify(patientQueueMapper).getHourlyAverageWaitTimes(startDate, endDate);
    }

    @Test
    void testGetWaitTimeAnalyticsWithEmptyData() {
        // Given
        when(patientQueueMapper.getWaitTimesByDateRange(startDate, endDate)).thenReturn(new ArrayList<>());

        // When
        WaitTimeAnalytics result = operationalAnalyticsService.getWaitTimeAnalytics(startDate, endDate);

        // Then
        assertNotNull(result);
        assertEquals(0.0, result.getAverageWaitTime());
        assertEquals(0.0, result.getMedianWaitTime());
        assertEquals(0.0, result.getMaxWaitTime());
        assertEquals(0.0, result.getMinWaitTime());
        assertEquals(0L, result.getTotalPatients());
        assertEquals(0L, result.getPatientsWithinTarget());
        assertEquals(0.0, result.getTargetComplianceRate());
        assertEquals("09:00-10:00", result.getPeakWaitTimeHour());

        verify(patientQueueMapper).getWaitTimesByDateRange(startDate, endDate);
    }

    @Test
    void testGetAllDoctorsPerformanceReport() {
        // Given
        List<Long> doctorIds = Arrays.asList(1L, 2L);
        
        Staff staff1 = new Staff();
        staff1.setId(1L);
        staff1.setName("张医生");
        staff1.setDepartment("内科");
        
        Staff staff2 = new Staff();
        staff2.setId(2L);
        staff2.setName("李医生");
        staff2.setDepartment("外科");

        when(staffMapper.getAllDoctorIds()).thenReturn(doctorIds);
        when(staffMapper.selectById(1L)).thenReturn(staff1);
        when(staffMapper.selectById(2L)).thenReturn(staff2);
        
        // Mock data for doctor 1
        when(medicalRecordMapper.countPatientsByDoctorAndDateRange(1L, startDate, endDate)).thenReturn(50L);
        when(medicalRecordMapper.countCompletedConsultationsByDoctorAndDateRange(1L, startDate, endDate)).thenReturn(45L);
        when(medicalRecordMapper.getConsultationTimesByDoctorAndDateRange(1L, startDate, endDate)).thenReturn(new ArrayList<>());
        when(prescriptionMapper.countPrescriptionsByDoctorAndDateRange(1L, startDate, endDate)).thenReturn(40L);
        when(billMapper.sumRevenueByDoctorAndDateRange(1L, startDate, endDate)).thenReturn(15000.0);
        
        // Mock data for doctor 2
        when(medicalRecordMapper.countPatientsByDoctorAndDateRange(2L, startDate, endDate)).thenReturn(30L);
        when(medicalRecordMapper.countCompletedConsultationsByDoctorAndDateRange(2L, startDate, endDate)).thenReturn(28L);
        when(medicalRecordMapper.getConsultationTimesByDoctorAndDateRange(2L, startDate, endDate)).thenReturn(new ArrayList<>());
        when(prescriptionMapper.countPrescriptionsByDoctorAndDateRange(2L, startDate, endDate)).thenReturn(25L);
        when(billMapper.sumRevenueByDoctorAndDateRange(2L, startDate, endDate)).thenReturn(12000.0);

        // When
        List<DoctorPerformanceReport> result = operationalAnalyticsService.getAllDoctorsPerformanceReport(startDate, endDate);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        
        // Should be sorted by total patients descending
        assertEquals("张医生", result.get(0).getDoctorName());
        assertEquals(50L, result.get(0).getTotalPatients());
        
        assertEquals("李医生", result.get(1).getDoctorName());
        assertEquals(30L, result.get(1).getTotalPatients());

        verify(staffMapper).getAllDoctorIds();
        verify(staffMapper, times(2)).selectById(anyLong());
    }

    @Test
    void testGetDepartmentVisitStatistics() {
        // Given
        Map<String, Object> deptData = new HashMap<>();
        deptData.put("department", "内科");
        deptData.put("count", 80L);
        
        List<Map<String, Object>> departmentStats = Arrays.asList(deptData);
        when(registrationMapper.getDepartmentVisitStatistics(startDate, endDate)).thenReturn(departmentStats);

        // When
        List<PopularService> result = operationalAnalyticsService.getDepartmentVisitStatistics(startDate, endDate);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("内科科", result.get(0).getServiceName());
        assertEquals(80L, result.get(0).getVisitCount());

        verify(registrationMapper).getDepartmentVisitStatistics(startDate, endDate);
    }

    @Test
    void testGetWaitTimeAnalyticsWithEvenNumberOfElements() {
        // Given - even number of wait times for median calculation
        List<Double> waitTimes = Arrays.asList(10.0, 20.0, 30.0, 40.0);
        when(patientQueueMapper.getWaitTimesByDateRange(startDate, endDate)).thenReturn(waitTimes);
        when(patientQueueMapper.getHourlyAverageWaitTimes(startDate, endDate)).thenReturn(new HashMap<>());

        // When
        WaitTimeAnalytics result = operationalAnalyticsService.getWaitTimeAnalytics(startDate, endDate);

        // Then
        assertNotNull(result);
        assertEquals(25.0, result.getAverageWaitTime()); // (10+20+30+40)/4
        assertEquals(25.0, result.getMedianWaitTime()); // (20+30)/2
        assertEquals(40.0, result.getMaxWaitTime());
        assertEquals(10.0, result.getMinWaitTime());
        assertEquals(4L, result.getTotalPatients());
        assertEquals(3L, result.getPatientsWithinTarget()); // 10.0, 20.0, 30.0 are <= 30.0 target
        assertEquals(75.0, result.getTargetComplianceRate()); // 3/4 * 100

        verify(patientQueueMapper).getWaitTimesByDateRange(startDate, endDate);
    }
}