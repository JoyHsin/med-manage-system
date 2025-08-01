package org.me.joy.clinic.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 病史实体类测试
 */
class MedicalHistoryTest {

    private Validator validator;
    private MedicalHistory medicalHistory;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();

        medicalHistory = new MedicalHistory();
        medicalHistory.setPatientId(1L);
        medicalHistory.setHistoryType("既往病史");
        medicalHistory.setDiseaseName("高血压");
        medicalHistory.setRecordedTime(LocalDateTime.now());
    }

    @Test
    void testValidMedicalHistory() {
        // When
        Set<ConstraintViolation<MedicalHistory>> violations = validator.validate(medicalHistory);

        // Then
        assertTrue(violations.isEmpty(), "有效的病史信息不应该有验证错误");
    }

    @Test
    void testPatientIdValidation() {
        // Given - 患者ID为空
        medicalHistory.setPatientId(null);

        // When
        Set<ConstraintViolation<MedicalHistory>> violations = validator.validate(medicalHistory);

        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("患者ID不能为空")));
    }

    @Test
    void testHistoryTypeValidation() {
        // Test valid history types
        String[] validTypes = {"既往病史", "家族病史", "手术史", "外伤史", "输血史", "药物史"};
        
        for (String type : validTypes) {
            medicalHistory.setHistoryType(type);
            Set<ConstraintViolation<MedicalHistory>> violations = validator.validate(medicalHistory);
            assertTrue(violations.stream().noneMatch(v -> v.getPropertyPath().toString().equals("historyType")),
                "病史类型 " + type + " 应该是有效的");
        }
    }

    @Test
    void testInvalidHistoryType() {
        // Given - 无效的病史类型
        medicalHistory.setHistoryType("其他病史");

        // When
        Set<ConstraintViolation<MedicalHistory>> violations = validator.validate(medicalHistory);

        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("病史类型只能是既往病史、家族病史、手术史、外伤史、输血史或药物史")));
    }

    @Test
    void testDiseaseNameValidation() {
        // Given - 疾病名称为空
        medicalHistory.setDiseaseName("");

        // When
        Set<ConstraintViolation<MedicalHistory>> violations = validator.validate(medicalHistory);

        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("疾病名称不能为空")));
    }

    @Test
    void testDiseaseNameTooLong() {
        // Given - 疾病名称过长
        medicalHistory.setDiseaseName("疾病".repeat(101)); // 202个字符

        // When
        Set<ConstraintViolation<MedicalHistory>> violations = validator.validate(medicalHistory);

        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("疾病名称长度不能超过200个字符")));
    }

    @Test
    void testTreatmentResultValidation() {
        // Test valid treatment results
        String[] validResults = {"治愈", "好转", "稳定", "恶化", "死亡", "未知"};
        
        for (String result : validResults) {
            medicalHistory.setTreatmentResult(result);
            Set<ConstraintViolation<MedicalHistory>> violations = validator.validate(medicalHistory);
            assertTrue(violations.stream().noneMatch(v -> v.getPropertyPath().toString().equals("treatmentResult")),
                "治疗结果 " + result + " 应该是有效的");
        }
    }

    @Test
    void testInvalidTreatmentResult() {
        // Given - 无效的治疗结果
        medicalHistory.setTreatmentResult("其他");

        // When
        Set<ConstraintViolation<MedicalHistory>> violations = validator.validate(medicalHistory);

        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("治疗结果只能是治愈、好转、稳定、恶化、死亡或未知")));
    }

    @Test
    void testSeverityValidation() {
        // Test valid severity levels
        String[] validSeverities = {"轻微", "中等", "严重", "危及生命"};
        
        for (String severity : validSeverities) {
            medicalHistory.setSeverity(severity);
            Set<ConstraintViolation<MedicalHistory>> violations = validator.validate(medicalHistory);
            assertTrue(violations.stream().noneMatch(v -> v.getPropertyPath().toString().equals("severity")),
                "严重程度 " + severity + " 应该是有效的");
        }
    }

    @Test
    void testInvalidSeverity() {
        // Given - 无效的严重程度
        medicalHistory.setSeverity("一般");

        // When
        Set<ConstraintViolation<MedicalHistory>> violations = validator.validate(medicalHistory);

        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("严重程度只能是轻微、中等、严重或危及生命")));
    }

    @Test
    void testCurrentStatusValidation() {
        // Test valid current statuses
        String[] validStatuses = {"活跃", "稳定", "缓解", "治愈", "复发"};
        
        for (String status : validStatuses) {
            medicalHistory.setCurrentStatus(status);
            Set<ConstraintViolation<MedicalHistory>> violations = validator.validate(medicalHistory);
            assertTrue(violations.stream().noneMatch(v -> v.getPropertyPath().toString().equals("currentStatus")),
                "当前状态 " + status + " 应该是有效的");
        }
    }

    @Test
    void testInvalidCurrentStatus() {
        // Given - 无效的当前状态
        medicalHistory.setCurrentStatus("其他");

        // When
        Set<ConstraintViolation<MedicalHistory>> violations = validator.validate(medicalHistory);

        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("当前状态只能是活跃、稳定、缓解、治愈或复发")));
    }

    @Test
    void testFieldLengthValidations() {
        // Test various field length validations
        medicalHistory.setDiseaseCategory("分类".repeat(51)); // 102个字符
        Set<ConstraintViolation<MedicalHistory>> violations = validator.validate(medicalHistory);
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("疾病分类长度不能超过100个字符")));

        medicalHistory.setDiseaseCategory("心血管疾病"); // 重置为有效值
        medicalHistory.setTreatmentStatus("治疗".repeat(251)); // 1004个字符
        violations = validator.validate(medicalHistory);
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("治疗情况长度不能超过1000个字符")));

        medicalHistory.setTreatmentStatus("药物治疗"); // 重置为有效值
        medicalHistory.setHospital("医院".repeat(51)); // 204个字符
        violations = validator.validate(medicalHistory);
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("治疗医院长度不能超过200个字符")));

        medicalHistory.setHospital("北京协和医院"); // 重置为有效值
        medicalHistory.setDoctor("医生".repeat(26)); // 104个字符
        violations = validator.validate(medicalHistory);
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("主治医生长度不能超过100个字符")));
    }

    @Test
    void testEqualsAndHashCode() {
        // Given
        MedicalHistory history1 = new MedicalHistory();
        history1.setPatientId(1L);
        history1.setHistoryType("既往病史");
        history1.setDiseaseName("高血压");
        
        MedicalHistory history2 = new MedicalHistory();
        history2.setPatientId(1L);
        history2.setHistoryType("既往病史");
        history2.setDiseaseName("高血压");
        
        MedicalHistory history3 = new MedicalHistory();
        history3.setPatientId(1L);
        history3.setHistoryType("既往病史");
        history3.setDiseaseName("糖尿病");

        // Then
        assertEquals(history1, history2);
        assertNotEquals(history1, history3);
        assertEquals(history1.hashCode(), history2.hashCode());
    }

    @Test
    void testToString() {
        // When
        String result = medicalHistory.toString();

        // Then
        assertNotNull(result);
        assertTrue(result.contains("MedicalHistory{"));
        assertTrue(result.contains("patientId=1"));
        assertTrue(result.contains("historyType='既往病史'"));
        assertTrue(result.contains("diseaseName='高血压'"));
    }

    @Test
    void testConstructors() {
        // Test default constructor
        MedicalHistory history1 = new MedicalHistory();
        assertNotNull(history1);

        // Test parameterized constructor
        MedicalHistory history2 = new MedicalHistory(1L, "家族病史", "糖尿病");
        assertEquals(1L, history2.getPatientId());
        assertEquals("家族病史", history2.getHistoryType());
        assertEquals("糖尿病", history2.getDiseaseName());
        assertNotNull(history2.getRecordedTime());
    }

    @Test
    void testDefaultValues() {
        // Given
        MedicalHistory newHistory = new MedicalHistory();

        // Then
        assertEquals(false, newHistory.getIsHereditary());
        assertEquals(false, newHistory.getIsChronic());
        assertEquals(false, newHistory.getIsContagious());
    }

    @Test
    void testSettersAndGetters() {
        // Test all setters and getters
        LocalDate onsetDate = LocalDate.of(2020, 1, 1);
        LocalDate diagnosisDate = LocalDate.of(2020, 1, 15);
        LocalDateTime recordedTime = LocalDateTime.now();

        medicalHistory.setDiseaseCategory("心血管疾病");
        medicalHistory.setOnsetDate(onsetDate);
        medicalHistory.setDiagnosisDate(diagnosisDate);
        medicalHistory.setTreatmentStatus("药物治疗中");
        medicalHistory.setTreatmentResult("稳定");
        medicalHistory.setHospital("北京协和医院");
        medicalHistory.setDoctor("张医生");
        medicalHistory.setDescription("详细病史描述");
        medicalHistory.setExamResults("血压160/90");
        medicalHistory.setMedications("降压药");
        medicalHistory.setFamilyRelation("父亲");
        medicalHistory.setIsHereditary(true);
        medicalHistory.setIsChronic(true);
        medicalHistory.setIsContagious(false);
        medicalHistory.setSeverity("中等");
        medicalHistory.setCurrentStatus("稳定");
        medicalHistory.setRemarks("需要定期复查");
        medicalHistory.setRecordedBy(100L);
        medicalHistory.setRecordedTime(recordedTime);
        medicalHistory.setLastUpdatedBy(101L);

        assertEquals("心血管疾病", medicalHistory.getDiseaseCategory());
        assertEquals(onsetDate, medicalHistory.getOnsetDate());
        assertEquals(diagnosisDate, medicalHistory.getDiagnosisDate());
        assertEquals("药物治疗中", medicalHistory.getTreatmentStatus());
        assertEquals("稳定", medicalHistory.getTreatmentResult());
        assertEquals("北京协和医院", medicalHistory.getHospital());
        assertEquals("张医生", medicalHistory.getDoctor());
        assertEquals("详细病史描述", medicalHistory.getDescription());
        assertEquals("血压160/90", medicalHistory.getExamResults());
        assertEquals("降压药", medicalHistory.getMedications());
        assertEquals("父亲", medicalHistory.getFamilyRelation());
        assertTrue(medicalHistory.getIsHereditary());
        assertTrue(medicalHistory.getIsChronic());
        assertFalse(medicalHistory.getIsContagious());
        assertEquals("中等", medicalHistory.getSeverity());
        assertEquals("稳定", medicalHistory.getCurrentStatus());
        assertEquals("需要定期复查", medicalHistory.getRemarks());
        assertEquals(100L, medicalHistory.getRecordedBy());
        assertEquals(recordedTime, medicalHistory.getRecordedTime());
        assertEquals(101L, medicalHistory.getLastUpdatedBy());
    }
}