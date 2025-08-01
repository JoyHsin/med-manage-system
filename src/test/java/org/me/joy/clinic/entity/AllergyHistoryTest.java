package org.me.joy.clinic.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

import java.time.LocalDateTime;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 过敏史实体类测试
 */
class AllergyHistoryTest {

    private Validator validator;
    private AllergyHistory allergyHistory;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();

        allergyHistory = new AllergyHistory();
        allergyHistory.setPatientId(1L);
        allergyHistory.setAllergen("青霉素");
        allergyHistory.setAllergenType("药物");
        allergyHistory.setSymptoms("皮疹、瘙痒");
        allergyHistory.setSeverity("中等");
        allergyHistory.setRecordedTime(LocalDateTime.now());
    }

    @Test
    void testValidAllergyHistory() {
        // When
        Set<ConstraintViolation<AllergyHistory>> violations = validator.validate(allergyHistory);

        // Then
        assertTrue(violations.isEmpty(), "有效的过敏史信息不应该有验证错误");
    }

    @Test
    void testPatientIdValidation() {
        // Given - 患者ID为空
        allergyHistory.setPatientId(null);

        // When
        Set<ConstraintViolation<AllergyHistory>> violations = validator.validate(allergyHistory);

        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("患者ID不能为空")));
    }

    @Test
    void testAllergenValidation() {
        // Given - 过敏原名称为空
        allergyHistory.setAllergen("");

        // When
        Set<ConstraintViolation<AllergyHistory>> violations = validator.validate(allergyHistory);

        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("过敏原名称不能为空")));
    }

    @Test
    void testAllergenTooLong() {
        // Given - 过敏原名称过长
        allergyHistory.setAllergen("过敏原".repeat(67)); // 201个字符

        // When
        Set<ConstraintViolation<AllergyHistory>> violations = validator.validate(allergyHistory);

        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("过敏原名称长度不能超过200个字符")));
    }

    @Test
    void testAllergenTypeValidation() {
        // Test valid allergen types
        String[] validTypes = {"药物", "食物", "环境", "其他"};
        
        for (String type : validTypes) {
            allergyHistory.setAllergenType(type);
            Set<ConstraintViolation<AllergyHistory>> violations = validator.validate(allergyHistory);
            assertTrue(violations.stream().noneMatch(v -> v.getPropertyPath().toString().equals("allergenType")),
                "过敏原类型 " + type + " 应该是有效的");
        }
    }

    @Test
    void testInvalidAllergenType() {
        // Given - 无效的过敏原类型
        allergyHistory.setAllergenType("未知");

        // When
        Set<ConstraintViolation<AllergyHistory>> violations = validator.validate(allergyHistory);

        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("过敏原类型只能是药物、食物、环境或其他")));
    }

    @Test
    void testSymptomsValidation() {
        // Given - 过敏反应症状为空
        allergyHistory.setSymptoms("");

        // When
        Set<ConstraintViolation<AllergyHistory>> violations = validator.validate(allergyHistory);

        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("过敏反应症状不能为空")));
    }

    @Test
    void testSymptomsTooLong() {
        // Given - 过敏反应症状过长
        allergyHistory.setSymptoms("症状".repeat(501)); // 1002个字符

        // When
        Set<ConstraintViolation<AllergyHistory>> violations = validator.validate(allergyHistory);

        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("过敏反应症状长度不能超过1000个字符")));
    }

    @Test
    void testSeverityValidation() {
        // Test valid severity levels
        String[] validSeverities = {"轻微", "中等", "严重", "危及生命"};
        
        for (String severity : validSeverities) {
            allergyHistory.setSeverity(severity);
            Set<ConstraintViolation<AllergyHistory>> violations = validator.validate(allergyHistory);
            assertTrue(violations.stream().noneMatch(v -> v.getPropertyPath().toString().equals("severity")),
                "过敏严重程度 " + severity + " 应该是有效的");
        }
    }

    @Test
    void testInvalidSeverity() {
        // Given - 无效的过敏严重程度
        allergyHistory.setSeverity("一般");

        // When
        Set<ConstraintViolation<AllergyHistory>> violations = validator.validate(allergyHistory);

        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("过敏严重程度只能是轻微、中等、严重或危及生命")));
    }

    @Test
    void testTreatmentTooLong() {
        // Given - 处理措施过长
        allergyHistory.setTreatment("处理措施".repeat(201)); // 1005个字符

        // When
        Set<ConstraintViolation<AllergyHistory>> violations = validator.validate(allergyHistory);

        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("处理措施长度不能超过1000个字符")));
    }

    @Test
    void testRemarksTooLong() {
        // Given - 备注信息过长
        allergyHistory.setRemarks("备注".repeat(126)); // 504个字符

        // When
        Set<ConstraintViolation<AllergyHistory>> violations = validator.validate(allergyHistory);

        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("备注信息长度不能超过500个字符")));
    }

    @Test
    void testEqualsAndHashCode() {
        // Given
        AllergyHistory allergy1 = new AllergyHistory();
        allergy1.setPatientId(1L);
        allergy1.setAllergen("青霉素");
        
        AllergyHistory allergy2 = new AllergyHistory();
        allergy2.setPatientId(1L);
        allergy2.setAllergen("青霉素");
        
        AllergyHistory allergy3 = new AllergyHistory();
        allergy3.setPatientId(1L);
        allergy3.setAllergen("阿莫西林");

        // Then
        assertEquals(allergy1, allergy2);
        assertNotEquals(allergy1, allergy3);
        assertEquals(allergy1.hashCode(), allergy2.hashCode());
    }

    @Test
    void testToString() {
        // When
        String result = allergyHistory.toString();

        // Then
        assertNotNull(result);
        assertTrue(result.contains("AllergyHistory{"));
        assertTrue(result.contains("patientId=1"));
        assertTrue(result.contains("allergen='青霉素'"));
        assertTrue(result.contains("allergenType='药物'"));
        assertTrue(result.contains("severity='中等'"));
    }

    @Test
    void testConstructors() {
        // Test default constructor
        AllergyHistory allergy1 = new AllergyHistory();
        assertNotNull(allergy1);

        // Test parameterized constructor
        AllergyHistory allergy2 = new AllergyHistory(1L, "阿司匹林", "药物", "胃痛", "轻微");
        assertEquals(1L, allergy2.getPatientId());
        assertEquals("阿司匹林", allergy2.getAllergen());
        assertEquals("药物", allergy2.getAllergenType());
        assertEquals("胃痛", allergy2.getSymptoms());
        assertEquals("轻微", allergy2.getSeverity());
        assertNotNull(allergy2.getRecordedTime());
    }

    @Test
    void testDefaultValues() {
        // Given
        AllergyHistory newAllergy = new AllergyHistory();

        // Then
        assertEquals(false, newAllergy.getIsConfirmed());
    }

    @Test
    void testSettersAndGetters() {
        // Test all setters and getters
        LocalDateTime firstDiscovered = LocalDateTime.now().minusDays(30);
        LocalDateTime lastOccurrence = LocalDateTime.now().minusDays(1);
        LocalDateTime recorded = LocalDateTime.now();

        allergyHistory.setFirstDiscoveredTime(firstDiscovered);
        allergyHistory.setLastOccurrenceTime(lastOccurrence);
        allergyHistory.setTreatment("停用药物，使用抗过敏药");
        allergyHistory.setRemarks("需要特别注意");
        allergyHistory.setIsConfirmed(true);
        allergyHistory.setRecordedBy(100L);
        allergyHistory.setRecordedTime(recorded);

        assertEquals(firstDiscovered, allergyHistory.getFirstDiscoveredTime());
        assertEquals(lastOccurrence, allergyHistory.getLastOccurrenceTime());
        assertEquals("停用药物，使用抗过敏药", allergyHistory.getTreatment());
        assertEquals("需要特别注意", allergyHistory.getRemarks());
        assertTrue(allergyHistory.getIsConfirmed());
        assertEquals(100L, allergyHistory.getRecordedBy());
        assertEquals(recorded, allergyHistory.getRecordedTime());
    }
}