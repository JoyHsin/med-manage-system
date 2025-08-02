package org.me.joy.clinic.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 诊断实体类测试
 */
class DiagnosisTest {

    private Diagnosis diagnosis;

    @BeforeEach
    void setUp() {
        diagnosis = new Diagnosis(1L, "上呼吸道感染", 2L);
    }

    @Test
    void testConstructor() {
        assertNotNull(diagnosis);
        assertEquals(1L, diagnosis.getMedicalRecordId());
        assertEquals("上呼吸道感染", diagnosis.getDiagnosisName());
        assertEquals(2L, diagnosis.getDoctorId());
        assertNotNull(diagnosis.getDiagnosisTime());
        assertEquals("主要诊断", diagnosis.getDiagnosisType());
        assertEquals("初步诊断", diagnosis.getStatus());
        assertFalse(diagnosis.getIsPrimary());
        assertFalse(diagnosis.getIsChronicDisease());
        assertFalse(diagnosis.getIsInfectious());
        assertFalse(diagnosis.getIsHereditary());
        assertEquals(1, diagnosis.getSortOrder());
    }

    @Test
    void testConfirm() {
        diagnosis.setStatus("初步诊断");
        diagnosis.confirm();
        assertEquals("确定诊断", diagnosis.getStatus());
    }

    @Test
    void testRevise() {
        String newDiagnosisName = "急性上呼吸道感染";
        String reason = "进一步检查确认";

        diagnosis.revise(newDiagnosisName, reason);

        assertEquals(newDiagnosisName, diagnosis.getDiagnosisName());
        assertEquals("修正诊断", diagnosis.getStatus());
        assertTrue(diagnosis.getRemarks().contains("修正原因：" + reason));
    }

    @Test
    void testReviseWithExistingRemarks() {
        diagnosis.setRemarks("原有备注");
        String newDiagnosisName = "急性上呼吸道感染";
        String reason = "进一步检查确认";

        diagnosis.revise(newDiagnosisName, reason);

        assertEquals(newDiagnosisName, diagnosis.getDiagnosisName());
        assertEquals("修正诊断", diagnosis.getStatus());
        assertTrue(diagnosis.getRemarks().contains("原有备注"));
        assertTrue(diagnosis.getRemarks().contains("修正原因：" + reason));
    }

    @Test
    void testExclude() {
        String reason = "检查结果排除";

        diagnosis.exclude(reason);

        assertEquals("排除诊断", diagnosis.getStatus());
        assertTrue(diagnosis.getRemarks().contains("排除原因：" + reason));
    }

    @Test
    void testExcludeWithExistingRemarks() {
        diagnosis.setRemarks("原有备注");
        String reason = "检查结果排除";

        diagnosis.exclude(reason);

        assertEquals("排除诊断", diagnosis.getStatus());
        assertTrue(diagnosis.getRemarks().contains("原有备注"));
        assertTrue(diagnosis.getRemarks().contains("排除原因：" + reason));
    }

    @Test
    void testIsSevere() {
        // 轻度不严重
        diagnosis.setSeverity("轻度");
        assertFalse(diagnosis.isSevere());

        // 中度不严重
        diagnosis.setSeverity("中度");
        assertFalse(diagnosis.isSevere());

        // 重度严重
        diagnosis.setSeverity("重度");
        assertTrue(diagnosis.isSevere());

        // 危重严重
        diagnosis.setSeverity("危重");
        assertTrue(diagnosis.isSevere());

        // null不严重
        diagnosis.setSeverity(null);
        assertFalse(diagnosis.isSevere());
    }

    @Test
    void testNeedsSpecialAttention() {
        // 传染病需要特殊关注
        diagnosis.setIsInfectious(true);
        assertTrue(diagnosis.needsSpecialAttention());

        // 慢性病需要特殊关注
        diagnosis.setIsInfectious(false);
        diagnosis.setIsChronicDisease(true);
        assertTrue(diagnosis.needsSpecialAttention());

        // 严重程度需要特殊关注
        diagnosis.setIsChronicDisease(false);
        diagnosis.setSeverity("重度");
        assertTrue(diagnosis.needsSpecialAttention());

        // 危重需要特殊关注
        diagnosis.setSeverity("危重");
        assertTrue(diagnosis.needsSpecialAttention());

        // 普通情况不需要特殊关注
        diagnosis.setSeverity("轻度");
        assertFalse(diagnosis.needsSpecialAttention());
    }

    @Test
    void testEquals() {
        diagnosis.setId(1L);
        
        Diagnosis diagnosis2 = new Diagnosis();
        diagnosis2.setId(1L);
        
        Diagnosis diagnosis3 = new Diagnosis();
        diagnosis3.setId(2L);

        // 相同ID应该相等
        assertEquals(diagnosis, diagnosis2);
        
        // 不同ID不相等
        assertNotEquals(diagnosis, diagnosis3);
        
        // 与null不相等
        assertNotEquals(diagnosis, null);
        
        // 与其他类型不相等
        assertNotEquals(diagnosis, "string");
    }

    @Test
    void testHashCode() {
        diagnosis.setId(1L);
        
        Diagnosis diagnosis2 = new Diagnosis();
        diagnosis2.setId(1L);

        assertEquals(diagnosis.hashCode(), diagnosis2.hashCode());
    }

    @Test
    void testToString() {
        diagnosis.setId(1L);
        diagnosis.setDiagnosisCode("J06.9");
        diagnosis.setSeverity("轻度");
        diagnosis.setIsPrimary(true);

        String toString = diagnosis.toString();
        assertNotNull(toString);
        assertTrue(toString.contains("Diagnosis"));
        assertTrue(toString.contains("上呼吸道感染"));
        assertTrue(toString.contains("J06.9"));
        assertTrue(toString.contains("轻度"));
        assertTrue(toString.contains("true"));
    }

    @Test
    void testSettersAndGetters() {
        diagnosis.setDiagnosisCode("J06.9");
        diagnosis.setDiagnosisType("次要诊断");
        diagnosis.setDescription("急性上呼吸道感染，症状轻微");
        diagnosis.setSeverity("轻度");
        diagnosis.setStatus("确定诊断");
        diagnosis.setEvidence("临床症状和体征");
        diagnosis.setDiagnosisTime(LocalDateTime.now());
        diagnosis.setIsPrimary(true);
        diagnosis.setIsChronicDisease(false);
        diagnosis.setIsInfectious(true);
        diagnosis.setIsHereditary(false);
        diagnosis.setPrognosis("良好");
        diagnosis.setTreatmentAdvice("抗感染治疗");
        diagnosis.setFollowUpRequirement("3天后复诊");
        diagnosis.setRemarks("患者配合度好");
        diagnosis.setSortOrder(2);

        assertEquals("J06.9", diagnosis.getDiagnosisCode());
        assertEquals("次要诊断", diagnosis.getDiagnosisType());
        assertEquals("急性上呼吸道感染，症状轻微", diagnosis.getDescription());
        assertEquals("轻度", diagnosis.getSeverity());
        assertEquals("确定诊断", diagnosis.getStatus());
        assertEquals("临床症状和体征", diagnosis.getEvidence());
        assertNotNull(diagnosis.getDiagnosisTime());
        assertTrue(diagnosis.getIsPrimary());
        assertFalse(diagnosis.getIsChronicDisease());
        assertTrue(diagnosis.getIsInfectious());
        assertFalse(diagnosis.getIsHereditary());
        assertEquals("良好", diagnosis.getPrognosis());
        assertEquals("抗感染治疗", diagnosis.getTreatmentAdvice());
        assertEquals("3天后复诊", diagnosis.getFollowUpRequirement());
        assertEquals("患者配合度好", diagnosis.getRemarks());
        assertEquals(2, diagnosis.getSortOrder());
    }

    @Test
    void testDefaultConstructor() {
        Diagnosis emptyDiagnosis = new Diagnosis();
        assertNotNull(emptyDiagnosis);
        assertEquals("主要诊断", emptyDiagnosis.getDiagnosisType());
        assertEquals("初步诊断", emptyDiagnosis.getStatus());
        assertFalse(emptyDiagnosis.getIsPrimary());
        assertFalse(emptyDiagnosis.getIsChronicDisease());
        assertFalse(emptyDiagnosis.getIsInfectious());
        assertFalse(emptyDiagnosis.getIsHereditary());
        assertEquals(1, emptyDiagnosis.getSortOrder());
    }
}