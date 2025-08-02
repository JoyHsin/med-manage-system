package org.me.joy.clinic.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.me.joy.clinic.entity.Patient;
import org.me.joy.clinic.entity.PatientQueue;
import org.me.joy.clinic.entity.Registration;
import org.me.joy.clinic.exception.BusinessException;
import org.me.joy.clinic.mapper.PatientQueueMapper;
import org.me.joy.clinic.mapper.RegistrationMapper;
import org.me.joy.clinic.service.TriageService;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * 分诊叫号服务测试
 */
@ExtendWith(MockitoExtension.class)
class TriageServiceImplTest {

    @Mock
    private PatientQueueMapper patientQueueMapper;

    @Mock
    private RegistrationMapper registrationMapper;

    @InjectMocks
    private TriageServiceImpl triageService;

    private PatientQueue patientQueue;
    private Registration registration;
    private LocalDate testDate;

    @BeforeEach
    void setUp() {
        testDate = LocalDate.now();
        
        patientQueue = new PatientQueue();
        patientQueue.setId(1L);
        patientQueue.setPatientId(100L);
        patientQueue.setRegistrationId(200L);
        patientQueue.setQueueDate(testDate);
        patientQueue.setQueueNumber(1);
        patientQueue.setStatus("WAITING");
        patientQueue.setPriority(3);
        patientQueue.setCallCount(0);

        registration = new Registration();
        registration.setId(200L);
        registration.setPatientId(100L);
        registration.setRegistrationDate(testDate);
        registration.setDepartment("内科");
    }

    @Test
    void testGetTodayPatientQueue() {
        // Given
        List<PatientQueue> expectedQueues = Arrays.asList(patientQueue);
        when(patientQueueMapper.findByQueueDate(LocalDate.now())).thenReturn(expectedQueues);

        // When
        List<PatientQueue> result = triageService.getTodayPatientQueue();

        // Then
        assertEquals(1, result.size());
        assertEquals(patientQueue.getId(), result.get(0).getId());
        verify(patientQueueMapper).findByQueueDate(LocalDate.now());
    }

    @Test
    void testGetPatientQueueByDate() {
        // Given
        List<PatientQueue> expectedQueues = Arrays.asList(patientQueue);
        when(patientQueueMapper.findByQueueDate(testDate)).thenReturn(expectedQueues);

        // When
        List<PatientQueue> result = triageService.getPatientQueueByDate(testDate);

        // Then
        assertEquals(1, result.size());
        assertEquals(patientQueue.getId(), result.get(0).getId());
        verify(patientQueueMapper).findByQueueDate(testDate);
    }

    @Test
    void testCallPatientSuccess() {
        // Given
        Long calledBy = 300L;
        when(patientQueueMapper.selectById(1L)).thenReturn(patientQueue);
        when(patientQueueMapper.updateById(any(PatientQueue.class))).thenReturn(1);

        // When
        triageService.callPatient(1L, calledBy);

        // Then
        verify(patientQueueMapper).selectById(1L);
        verify(patientQueueMapper).updateById(argThat(queue -> 
            "CALLED".equals(queue.getStatus()) && 
            calledBy.equals(queue.getCalledBy()) &&
            queue.getCallCount() == 1 &&
            queue.getCalledAt() != null
        ));
    }

    @Test
    void testCallPatientNotFound() {
        // Given
        when(patientQueueMapper.selectById(1L)).thenReturn(null);

        // When & Then
        BusinessException exception = assertThrows(BusinessException.class, 
            () -> triageService.callPatient(1L, 300L));
        assertEquals("4001", exception.getErrorCode());
        assertEquals("患者队列记录不存在", exception.getMessage());
    }

    @Test
    void testCallPatientInvalidStatus() {
        // Given
        patientQueue.setStatus("COMPLETED");
        when(patientQueueMapper.selectById(1L)).thenReturn(patientQueue);

        // When & Then
        BusinessException exception = assertThrows(BusinessException.class, 
            () -> triageService.callPatient(1L, 300L));
        assertEquals("4002", exception.getErrorCode());
        assertEquals("患者状态不允许叫号", exception.getMessage());
    }

    @Test
    void testConfirmPatientArrivalSuccess() {
        // Given
        patientQueue.setStatus("CALLED");
        Long confirmedBy = 400L;
        when(patientQueueMapper.selectById(1L)).thenReturn(patientQueue);
        when(patientQueueMapper.updateById(any(PatientQueue.class))).thenReturn(1);

        // When
        triageService.confirmPatientArrival(1L, confirmedBy);

        // Then
        verify(patientQueueMapper).selectById(1L);
        verify(patientQueueMapper).updateById(argThat(queue -> 
            "ARRIVED".equals(queue.getStatus()) && 
            confirmedBy.equals(queue.getConfirmedBy()) &&
            queue.getArrivedAt() != null
        ));
    }

    @Test
    void testConfirmPatientArrivalInvalidStatus() {
        // Given
        patientQueue.setStatus("WAITING");
        when(patientQueueMapper.selectById(1L)).thenReturn(patientQueue);

        // When & Then
        BusinessException exception = assertThrows(BusinessException.class, 
            () -> triageService.confirmPatientArrival(1L, 400L));
        assertEquals("4003", exception.getErrorCode());
        assertEquals("患者状态不允许确认到达", exception.getMessage());
    }

    @Test
    void testMarkPatientAbsentSuccess() {
        // Given
        patientQueue.setStatus("CALLED");
        Long calledBy = 300L;
        when(patientQueueMapper.selectById(1L)).thenReturn(patientQueue);
        when(patientQueueMapper.updateById(any(PatientQueue.class))).thenReturn(1);

        // When
        triageService.markPatientAbsent(1L, calledBy);

        // Then
        verify(patientQueueMapper).selectById(1L);
        verify(patientQueueMapper).updateById(argThat(queue -> 
            "ABSENT".equals(queue.getStatus()) && 
            calledBy.equals(queue.getCalledBy())
        ));
    }

    @Test
    void testMarkPatientAbsentInvalidStatus() {
        // Given
        patientQueue.setStatus("WAITING");
        when(patientQueueMapper.selectById(1L)).thenReturn(patientQueue);

        // When & Then
        BusinessException exception = assertThrows(BusinessException.class, 
            () -> triageService.markPatientAbsent(1L, 300L));
        assertEquals("4004", exception.getErrorCode());
        assertEquals("患者状态不允许标记未到", exception.getMessage());
    }

    @Test
    void testGetNextPatient() {
        // Given
        when(patientQueueMapper.findNextPatient(LocalDate.now())).thenReturn(patientQueue);

        // When
        PatientQueue result = triageService.getNextPatient();

        // Then
        assertEquals(patientQueue.getId(), result.getId());
        verify(patientQueueMapper).findNextPatient(LocalDate.now());
    }

    @Test
    void testGetNextPatientWithDate() {
        // Given
        when(patientQueueMapper.findNextPatient(testDate)).thenReturn(patientQueue);

        // When
        PatientQueue result = triageService.getNextPatient(testDate);

        // Then
        assertEquals(patientQueue.getId(), result.getId());
        verify(patientQueueMapper).findNextPatient(testDate);
    }

    @Test
    void testRecallPatientSuccess() {
        // Given
        patientQueue.setStatus("ABSENT");
        patientQueue.setCallCount(1);
        Long calledBy = 300L;
        when(patientQueueMapper.selectById(1L)).thenReturn(patientQueue);
        when(patientQueueMapper.updateById(any(PatientQueue.class))).thenReturn(1);

        // When
        triageService.recallPatient(1L, calledBy);

        // Then
        verify(patientQueueMapper).selectById(1L);
        verify(patientQueueMapper).updateById(argThat(queue -> 
            "CALLED".equals(queue.getStatus()) && 
            calledBy.equals(queue.getCalledBy()) &&
            queue.getCallCount() == 2 &&
            queue.getCalledAt() != null
        ));
    }

    @Test
    void testRecallPatientInvalidStatus() {
        // Given
        patientQueue.setStatus("WAITING");
        when(patientQueueMapper.selectById(1L)).thenReturn(patientQueue);

        // When & Then
        BusinessException exception = assertThrows(BusinessException.class, 
            () -> triageService.recallPatient(1L, 300L));
        assertEquals("4005", exception.getErrorCode());
        assertEquals("只能重新叫号未到的患者", exception.getMessage());
    }

    @Test
    void testCompletePatientSuccess() {
        // Given
        patientQueue.setStatus("ARRIVED");
        when(patientQueueMapper.selectById(1L)).thenReturn(patientQueue);
        when(patientQueueMapper.updateById(any(PatientQueue.class))).thenReturn(1);

        // When
        triageService.completePatient(1L);

        // Then
        verify(patientQueueMapper).selectById(1L);
        verify(patientQueueMapper).updateById(argThat(queue -> 
            "COMPLETED".equals(queue.getStatus()) && 
            queue.getCompletedAt() != null
        ));
    }

    @Test
    void testCompletePatientInvalidStatus() {
        // Given
        patientQueue.setStatus("WAITING");
        when(patientQueueMapper.selectById(1L)).thenReturn(patientQueue);

        // When & Then
        BusinessException exception = assertThrows(BusinessException.class, 
            () -> triageService.completePatient(1L));
        assertEquals("4006", exception.getErrorCode());
        assertEquals("患者状态不允许完成就诊", exception.getMessage());
    }

    @Test
    void testCreatePatientQueueSuccess() {
        // Given
        Long registrationId = 200L;
        Integer priority = 2;
        when(registrationMapper.selectById(registrationId)).thenReturn(registration);
        when(patientQueueMapper.selectOne(any())).thenReturn(null);
        when(patientQueueMapper.getMaxQueueNumber(testDate)).thenReturn(0);
        when(patientQueueMapper.insert(any(PatientQueue.class))).thenReturn(1);

        // When
        PatientQueue result = triageService.createPatientQueue(registrationId, priority);

        // Then
        assertNotNull(result);
        assertEquals(registration.getPatientId(), result.getPatientId());
        assertEquals(registrationId, result.getRegistrationId());
        assertEquals(testDate, result.getQueueDate());
        assertEquals(1, result.getQueueNumber());
        assertEquals("WAITING", result.getStatus());
        assertEquals(priority, result.getPriority());
        assertEquals(0, result.getCallCount());
        
        verify(registrationMapper).selectById(registrationId);
        verify(patientQueueMapper).selectOne(any());
        verify(patientQueueMapper).getMaxQueueNumber(testDate);
        verify(patientQueueMapper).insert(any(PatientQueue.class));
    }

    @Test
    void testCreatePatientQueueRegistrationNotFound() {
        // Given
        Long registrationId = 200L;
        when(registrationMapper.selectById(registrationId)).thenReturn(null);

        // When & Then
        BusinessException exception = assertThrows(BusinessException.class, 
            () -> triageService.createPatientQueue(registrationId, 3));
        assertEquals("4007", exception.getErrorCode());
        assertEquals("挂号记录不存在", exception.getMessage());
    }

    @Test
    void testCreatePatientQueueAlreadyExists() {
        // Given
        Long registrationId = 200L;
        when(registrationMapper.selectById(registrationId)).thenReturn(registration);
        when(patientQueueMapper.selectOne(any())).thenReturn(patientQueue);

        // When & Then
        BusinessException exception = assertThrows(BusinessException.class, 
            () -> triageService.createPatientQueue(registrationId, 3));
        assertEquals("4008", exception.getErrorCode());
        assertEquals("该挂号记录已存在队列中", exception.getMessage());
    }

    @Test
    void testGetPatientQueueByStatus() {
        // Given
        String status = "WAITING";
        List<PatientQueue> expectedQueues = Arrays.asList(patientQueue);
        when(patientQueueMapper.findByQueueDateAndStatus(testDate, status)).thenReturn(expectedQueues);

        // When
        List<PatientQueue> result = triageService.getPatientQueueByStatus(testDate, status);

        // Then
        assertEquals(1, result.size());
        assertEquals(patientQueue.getId(), result.get(0).getId());
        verify(patientQueueMapper).findByQueueDateAndStatus(testDate, status);
    }
}