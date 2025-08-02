package org.me.joy.clinic.entity;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 患者队列实体测试
 */
class PatientQueueTest {

    @Test
    void testPatientQueueCreation() {
        // Given
        PatientQueue patientQueue = new PatientQueue();
        LocalDate queueDate = LocalDate.now();
        LocalDateTime now = LocalDateTime.now();

        // When
        patientQueue.setId(1L);
        patientQueue.setPatientId(100L);
        patientQueue.setRegistrationId(200L);
        patientQueue.setQueueDate(queueDate);
        patientQueue.setQueueNumber(1);
        patientQueue.setStatus("WAITING");
        patientQueue.setPriority(3);
        patientQueue.setCallCount(0);
        patientQueue.setCalledBy(300L);
        patientQueue.setConfirmedBy(400L);
        patientQueue.setCalledAt(now);
        patientQueue.setArrivedAt(now.plusMinutes(5));
        patientQueue.setCompletedAt(now.plusMinutes(30));
        patientQueue.setNotes("测试备注");

        // Then
        assertEquals(1L, patientQueue.getId());
        assertEquals(100L, patientQueue.getPatientId());
        assertEquals(200L, patientQueue.getRegistrationId());
        assertEquals(queueDate, patientQueue.getQueueDate());
        assertEquals(1, patientQueue.getQueueNumber());
        assertEquals("WAITING", patientQueue.getStatus());
        assertEquals(3, patientQueue.getPriority());
        assertEquals(0, patientQueue.getCallCount());
        assertEquals(300L, patientQueue.getCalledBy());
        assertEquals(400L, patientQueue.getConfirmedBy());
        assertEquals(now, patientQueue.getCalledAt());
        assertEquals(now.plusMinutes(5), patientQueue.getArrivedAt());
        assertEquals(now.plusMinutes(30), patientQueue.getCompletedAt());
        assertEquals("测试备注", patientQueue.getNotes());
    }

    @Test
    void testPatientQueueEqualsAndHashCode() {
        // Given
        PatientQueue queue1 = new PatientQueue();
        queue1.setId(1L);
        queue1.setPatientId(100L);
        queue1.setQueueNumber(1);

        PatientQueue queue2 = new PatientQueue();
        queue2.setId(1L);
        queue2.setPatientId(100L);
        queue2.setQueueNumber(1);

        PatientQueue queue3 = new PatientQueue();
        queue3.setId(2L);
        queue3.setPatientId(101L);
        queue3.setQueueNumber(2);

        // Then
        assertEquals(queue1, queue2);
        assertNotEquals(queue1, queue3);
        assertEquals(queue1.hashCode(), queue2.hashCode());
        assertNotEquals(queue1.hashCode(), queue3.hashCode());
    }

    @Test
    void testPatientQueueToString() {
        // Given
        PatientQueue patientQueue = new PatientQueue();
        patientQueue.setId(1L);
        patientQueue.setPatientId(100L);
        patientQueue.setQueueNumber(1);
        patientQueue.setStatus("WAITING");

        // When
        String toString = patientQueue.toString();

        // Then
        assertNotNull(toString);
        assertTrue(toString.contains("PatientQueue"), "Should contain 'PatientQueue', but was: " + toString);
        assertTrue(toString.contains("id=1"), "Should contain 'id=1', but was: " + toString);
        assertTrue(toString.contains("patientId=100"), "Should contain 'patientId=100', but was: " + toString);
        assertTrue(toString.contains("queueNumber=1"), "Should contain 'queueNumber=1', but was: " + toString);
        assertTrue(toString.contains("status='WAITING'"), "Should contain 'status='WAITING'', but was: " + toString);
    }

    @Test
    void testPatientQueueStatusValues() {
        // Given
        PatientQueue patientQueue = new PatientQueue();

        // Test all valid status values
        String[] validStatuses = {"WAITING", "CALLED", "ARRIVED", "ABSENT", "COMPLETED"};

        for (String status : validStatuses) {
            // When
            patientQueue.setStatus(status);

            // Then
            assertEquals(status, patientQueue.getStatus());
        }
    }

    @Test
    void testPatientQueuePriorityValues() {
        // Given
        PatientQueue patientQueue = new PatientQueue();

        // Test priority values (1-highest, 5-lowest)
        for (int priority = 1; priority <= 5; priority++) {
            // When
            patientQueue.setPriority(priority);

            // Then
            assertEquals(priority, patientQueue.getPriority());
        }
    }

    @Test
    void testPatientQueueCallCountIncrement() {
        // Given
        PatientQueue patientQueue = new PatientQueue();
        patientQueue.setCallCount(0);

        // When & Then
        assertEquals(0, patientQueue.getCallCount());

        patientQueue.setCallCount(patientQueue.getCallCount() + 1);
        assertEquals(1, patientQueue.getCallCount());

        patientQueue.setCallCount(patientQueue.getCallCount() + 1);
        assertEquals(2, patientQueue.getCallCount());
    }

    @Test
    void testPatientQueueTimeFields() {
        // Given
        PatientQueue patientQueue = new PatientQueue();
        LocalDateTime baseTime = LocalDateTime.now();

        // When
        patientQueue.setCalledAt(baseTime);
        patientQueue.setArrivedAt(baseTime.plusMinutes(5));
        patientQueue.setCompletedAt(baseTime.plusMinutes(30));

        // Then
        assertEquals(baseTime, patientQueue.getCalledAt());
        assertEquals(baseTime.plusMinutes(5), patientQueue.getArrivedAt());
        assertEquals(baseTime.plusMinutes(30), patientQueue.getCompletedAt());

        // Verify time sequence
        assertTrue(patientQueue.getCalledAt().isBefore(patientQueue.getArrivedAt()));
        assertTrue(patientQueue.getArrivedAt().isBefore(patientQueue.getCompletedAt()));
    }
}