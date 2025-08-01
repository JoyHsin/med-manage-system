package org.me.joy.clinic.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.me.joy.clinic.dto.CreatePatientRequest;
import org.me.joy.clinic.dto.PatientResponse;
import org.me.joy.clinic.dto.UpdatePatientRequest;
import org.me.joy.clinic.entity.AllergyHistory;
import org.me.joy.clinic.entity.MedicalHistory;
import org.me.joy.clinic.entity.Patient;
import org.me.joy.clinic.exception.BusinessException;
import org.me.joy.clinic.exception.ValidationException;
import org.me.joy.clinic.mapper.AllergyHistoryMapper;
import org.me.joy.clinic.mapper.MedicalHistoryMapper;
import org.me.joy.clinic.mapper.PatientMapper;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * 患者管理服务实现类测试
 */
@ExtendWith(MockitoExtension.class)
class PatientManagementServiceImplTest {

    @Mock
    private PatientMapper patientMapper;

    @Mock
    private AllergyHistoryMapper allergyHistoryMapper;

    @Mock
    private MedicalHistoryMapper medicalHistoryMapper;

    @InjectMocks
    private PatientManagementServiceImpl patientManagementService;

    private Patient testPatient;
    private CreatePatientRequest createPatientRequest;
    private UpdatePatientRequest updatePatientRequest;

    @BeforeEach
    void setUp() {
        testPatient = new Patient();
        testPatient.setId(1L);
        testPatient.setPatientNumber("P202401010001");
        testPatient.setName("张三");
        testPatient.setPhone("13800138000");
        testPatient.setIdCard("110101199001011234");
        testPatient.setBirthDate(LocalDate.of(1990, 1, 1));
        testPatient.setGender("男");
        testPatient.setAddress("北京市朝阳区");
        testPatient.setBloodType("A+");
        testPatient.setStatus("正常");
        testPatient.setIsVip(false);
        testPatient.setVisitCount(0);
        testPatient.setCreatedAt(LocalDateTime.now());
        testPatient.setUpdatedAt(LocalDateTime.now());

        createPatientRequest = new CreatePatientRequest();
        createPatientRequest.setName("李四");
        createPatientRequest.setPhone("13900139000");
        createPatientRequest.setIdCard("110101199002021234");
        createPatientRequest.setBirthDate(LocalDate.of(1990, 2, 2));
        createPatientRequest.setGender("女");
        createPatientRequest.setAddress("北京市海淀区");
        createPatientRequest.setBloodType("B+");
        createPatientRequest.setIsVip(false);

        updatePatientRequest = new UpdatePatientRequest();
        updatePatientRequest.setName("张三三");
        updatePatientRequest.setPhone("13800138001");
        updatePatientRequest.setAddress("北京市西城区");
    }

    @Test
    void createPatient_WithValidRequest_ShouldCreatePatient() {
        // Given
        when(patientMapper.findByIdCard(createPatientRequest.getIdCard())).thenReturn(Optional.empty());
        when(patientMapper.findByPatientNumber(anyString())).thenReturn(Optional.empty());
        when(patientMapper.insert(any(Patient.class))).thenReturn(1);

        // When
        PatientResponse response = patientManagementService.createPatient(createPatientRequest);

        // Then
        assertNotNull(response);
        assertEquals(createPatientRequest.getName(), response.getName());
        assertEquals(createPatientRequest.getPhone(), response.getPhone());
        assertEquals(createPatientRequest.getGender(), response.getGender());
        assertEquals("正常", response.getStatus());
        assertFalse(response.getIsVip());

        verify(patientMapper).insert(any(Patient.class));
    }

    @Test
    void createPatient_WithExistingIdCard_ShouldThrowValidationException() {
        // Given
        when(patientMapper.findByIdCard(createPatientRequest.getIdCard())).thenReturn(Optional.of(testPatient));

        // When & Then
        assertThrows(ValidationException.class, () -> {
            patientManagementService.createPatient(createPatientRequest);
        });

        verify(patientMapper, never()).insert(any(Patient.class));
    }

    @Test
    void updatePatient_WithValidRequest_ShouldUpdatePatient() {
        // Given
        when(patientMapper.selectById(1L)).thenReturn(testPatient);
        when(patientMapper.updateById(any(Patient.class))).thenReturn(1);

        // When
        PatientResponse response = patientManagementService.updatePatient(1L, updatePatientRequest);

        // Then
        assertNotNull(response);
        assertEquals(updatePatientRequest.getName(), response.getName());
        assertEquals(updatePatientRequest.getPhone(), response.getPhone());
        assertEquals(updatePatientRequest.getAddress(), response.getAddress());

        verify(patientMapper).updateById(any(Patient.class));
    }

    @Test
    void updatePatient_WithNonExistentPatient_ShouldThrowBusinessException() {
        // Given
        when(patientMapper.selectById(1L)).thenReturn(null);

        // When & Then
        assertThrows(BusinessException.class, () -> {
            patientManagementService.updatePatient(1L, updatePatientRequest);
        });

        verify(patientMapper, never()).updateById(any(Patient.class));
    }

    @Test
    void getPatientById_WithValidId_ShouldReturnPatient() {
        // Given
        when(patientMapper.selectById(1L)).thenReturn(testPatient);
        when(allergyHistoryMapper.findByPatientId(1L)).thenReturn(Arrays.asList());
        when(medicalHistoryMapper.findByPatientId(1L)).thenReturn(Arrays.asList());

        // When
        PatientResponse response = patientManagementService.getPatientById(1L);

        // Then
        assertNotNull(response);
        assertEquals(testPatient.getId(), response.getId());
        assertEquals(testPatient.getName(), response.getName());
        assertEquals(testPatient.getPatientNumber(), response.getPatientNumber());

        verify(patientMapper).selectById(1L);
    }

    @Test
    void getPatientById_WithNonExistentId_ShouldThrowBusinessException() {
        // Given
        when(patientMapper.selectById(1L)).thenReturn(null);

        // When & Then
        assertThrows(BusinessException.class, () -> {
            patientManagementService.getPatientById(1L);
        });
    }

    @Test
    void getPatientByNumber_WithValidNumber_ShouldReturnPatient() {
        // Given
        when(patientMapper.findByPatientNumber("P202401010001")).thenReturn(Optional.of(testPatient));
        when(allergyHistoryMapper.findByPatientId(1L)).thenReturn(Arrays.asList());
        when(medicalHistoryMapper.findByPatientId(1L)).thenReturn(Arrays.asList());

        // When
        PatientResponse response = patientManagementService.getPatientByNumber("P202401010001");

        // Then
        assertNotNull(response);
        assertEquals(testPatient.getPatientNumber(), response.getPatientNumber());
        assertEquals(testPatient.getName(), response.getName());

        verify(patientMapper).findByPatientNumber("P202401010001");
    }

    @Test
    void getPatientByNumber_WithNonExistentNumber_ShouldThrowBusinessException() {
        // Given
        when(patientMapper.findByPatientNumber("P202401010001")).thenReturn(Optional.empty());

        // When & Then
        assertThrows(BusinessException.class, () -> {
            patientManagementService.getPatientByNumber("P202401010001");
        });
    }

    @Test
    void getPatientByIdCard_WithValidIdCard_ShouldReturnPatient() {
        // Given
        when(patientMapper.findByIdCard("110101199001011234")).thenReturn(Optional.of(testPatient));
        when(allergyHistoryMapper.findByPatientId(1L)).thenReturn(Arrays.asList());
        when(medicalHistoryMapper.findByPatientId(1L)).thenReturn(Arrays.asList());

        // When
        PatientResponse response = patientManagementService.getPatientByIdCard("110101199001011234");

        // Then
        assertNotNull(response);
        assertEquals(testPatient.getIdCard(), response.getIdCard());
        assertEquals(testPatient.getName(), response.getName());

        verify(patientMapper).findByIdCard("110101199001011234");
    }

    @Test
    void getAllPatients_ShouldReturnAllPatients() {
        // Given
        List<Patient> patients = Arrays.asList(testPatient);
        when(patientMapper.selectList(null)).thenReturn(patients);
        when(allergyHistoryMapper.findByPatientId(anyLong())).thenReturn(Arrays.asList());
        when(medicalHistoryMapper.findByPatientId(anyLong())).thenReturn(Arrays.asList());

        // When
        List<PatientResponse> responses = patientManagementService.getAllPatients();

        // Then
        assertNotNull(responses);
        assertEquals(1, responses.size());
        assertEquals(testPatient.getName(), responses.get(0).getName());

        verify(patientMapper).selectList(null);
    }

    @Test
    void getPatientsByStatus_WithValidStatus_ShouldReturnPatients() {
        // Given
        List<Patient> patients = Arrays.asList(testPatient);
        when(patientMapper.findByStatus("正常")).thenReturn(patients);
        when(allergyHistoryMapper.findByPatientId(anyLong())).thenReturn(Arrays.asList());
        when(medicalHistoryMapper.findByPatientId(anyLong())).thenReturn(Arrays.asList());

        // When
        List<PatientResponse> responses = patientManagementService.getPatientsByStatus("正常");

        // Then
        assertNotNull(responses);
        assertEquals(1, responses.size());
        assertEquals("正常", responses.get(0).getStatus());

        verify(patientMapper).findByStatus("正常");
    }

    @Test
    void getVipPatients_ShouldReturnVipPatients() {
        // Given
        testPatient.setIsVip(true);
        List<Patient> patients = Arrays.asList(testPatient);
        when(patientMapper.findVipPatients()).thenReturn(patients);
        when(allergyHistoryMapper.findByPatientId(anyLong())).thenReturn(Arrays.asList());
        when(medicalHistoryMapper.findByPatientId(anyLong())).thenReturn(Arrays.asList());

        // When
        List<PatientResponse> responses = patientManagementService.getVipPatients();

        // Then
        assertNotNull(responses);
        assertEquals(1, responses.size());
        assertTrue(responses.get(0).getIsVip());

        verify(patientMapper).findVipPatients();
    }

    @Test
    void searchPatients_WithKeyword_ShouldReturnMatchingPatients() {
        // Given
        List<Patient> patients = Arrays.asList(testPatient);
        when(patientMapper.searchPatients("张三")).thenReturn(patients);
        when(allergyHistoryMapper.findByPatientId(anyLong())).thenReturn(Arrays.asList());
        when(medicalHistoryMapper.findByPatientId(anyLong())).thenReturn(Arrays.asList());

        // When
        List<PatientResponse> responses = patientManagementService.searchPatients("张三");

        // Then
        assertNotNull(responses);
        assertEquals(1, responses.size());
        assertEquals("张三", responses.get(0).getName());

        verify(patientMapper).searchPatients("张三");
    }

    @Test
    void deletePatient_WithValidId_ShouldDeletePatient() {
        // Given
        when(patientMapper.selectById(1L)).thenReturn(testPatient);
        when(patientMapper.deleteById(1L)).thenReturn(1);

        // When
        patientManagementService.deletePatient(1L);

        // Then
        verify(patientMapper).deleteById(1L);
    }

    @Test
    void setPatientAsVip_WithValidId_ShouldSetVipStatus() {
        // Given
        when(patientMapper.selectById(1L)).thenReturn(testPatient);
        when(patientMapper.updateById(any(Patient.class))).thenReturn(1);

        // When
        patientManagementService.setPatientAsVip(1L);

        // Then
        assertTrue(testPatient.getIsVip());
        verify(patientMapper).updateById(testPatient);
    }

    @Test
    void removePatientVipStatus_WithValidId_ShouldRemoveVipStatus() {
        // Given
        testPatient.setIsVip(true);
        when(patientMapper.selectById(1L)).thenReturn(testPatient);
        when(patientMapper.updateById(any(Patient.class))).thenReturn(1);

        // When
        patientManagementService.removePatientVipStatus(1L);

        // Then
        assertFalse(testPatient.getIsVip());
        verify(patientMapper).updateById(testPatient);
    }

    @Test
    void updatePatientStatus_WithValidStatus_ShouldUpdateStatus() {
        // Given
        when(patientMapper.selectById(1L)).thenReturn(testPatient);
        when(patientMapper.updateById(any(Patient.class))).thenReturn(1);

        // When
        patientManagementService.updatePatientStatus(1L, "黑名单");

        // Then
        assertEquals("黑名单", testPatient.getStatus());
        verify(patientMapper).updateById(testPatient);
    }

    @Test
    void recordPatientVisit_WithValidId_ShouldIncrementVisitCount() {
        // Given
        when(patientMapper.selectById(1L)).thenReturn(testPatient);
        when(patientMapper.updateById(any(Patient.class))).thenReturn(1);

        // When
        patientManagementService.recordPatientVisit(1L);

        // Then
        assertEquals(1, testPatient.getVisitCount());
        assertNotNull(testPatient.getLastVisitTime());
        assertNotNull(testPatient.getFirstVisitTime());
        verify(patientMapper).updateById(testPatient);
    }

    @Test
    void addAllergyHistory_WithValidData_ShouldAddAllergyHistory() {
        // Given
        when(patientMapper.selectById(1L)).thenReturn(testPatient);
        when(allergyHistoryMapper.insert(any(AllergyHistory.class))).thenReturn(1);

        AllergyHistory allergyHistory = new AllergyHistory();
        allergyHistory.setAllergen("青霉素");
        allergyHistory.setAllergenType("药物");
        allergyHistory.setSymptoms("皮疹");
        allergyHistory.setSeverity("中等");

        // When
        AllergyHistory result = patientManagementService.addAllergyHistory(1L, allergyHistory);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getPatientId());
        assertEquals("青霉素", result.getAllergen());
        assertNotNull(result.getRecordedTime());

        verify(allergyHistoryMapper).insert(allergyHistory);
    }

    @Test
    void addMedicalHistory_WithValidData_ShouldAddMedicalHistory() {
        // Given
        when(patientMapper.selectById(1L)).thenReturn(testPatient);
        when(medicalHistoryMapper.insert(any(MedicalHistory.class))).thenReturn(1);

        MedicalHistory medicalHistory = new MedicalHistory();
        medicalHistory.setHistoryType("既往病史");
        medicalHistory.setDiseaseName("高血压");
        medicalHistory.setSeverity("中等");

        // When
        MedicalHistory result = patientManagementService.addMedicalHistory(1L, medicalHistory);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getPatientId());
        assertEquals("高血压", result.getDiseaseName());
        assertNotNull(result.getRecordedTime());

        verify(medicalHistoryMapper).insert(medicalHistory);
    }

    @Test
    void generatePatientNumber_ShouldGenerateUniqueNumber() {
        // Given
        when(patientMapper.findByPatientNumber(anyString())).thenReturn(Optional.empty());

        // When
        String patientNumber = patientManagementService.generatePatientNumber();

        // Then
        assertNotNull(patientNumber);
        assertTrue(patientNumber.startsWith("P"));
        assertEquals(13, patientNumber.length()); // P + 8位日期 + 4位序号
    }

    @Test
    void existsByPatientNumber_WithExistingNumber_ShouldReturnTrue() {
        // Given
        when(patientMapper.findByPatientNumber("P202401010001")).thenReturn(Optional.of(testPatient));

        // When
        boolean exists = patientManagementService.existsByPatientNumber("P202401010001");

        // Then
        assertTrue(exists);
        verify(patientMapper).findByPatientNumber("P202401010001");
    }

    @Test
    void existsByIdCard_WithExistingIdCard_ShouldReturnTrue() {
        // Given
        when(patientMapper.findByIdCard("110101199001011234")).thenReturn(Optional.of(testPatient));

        // When
        boolean exists = patientManagementService.existsByIdCard("110101199001011234");

        // Then
        assertTrue(exists);
        verify(patientMapper).findByIdCard("110101199001011234");
    }

    @Test
    void countAllPatients_ShouldReturnTotalCount() {
        // Given
        when(patientMapper.countAllPatients()).thenReturn(100L);

        // When
        Long count = patientManagementService.countAllPatients();

        // Then
        assertEquals(100L, count);
        verify(patientMapper).countAllPatients();
    }

    @Test
    void countVipPatients_ShouldReturnVipCount() {
        // Given
        when(patientMapper.countVipPatients()).thenReturn(10L);

        // When
        Long count = patientManagementService.countVipPatients();

        // Then
        assertEquals(10L, count);
        verify(patientMapper).countVipPatients();
    }
}