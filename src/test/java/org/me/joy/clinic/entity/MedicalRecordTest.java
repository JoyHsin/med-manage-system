package org.me.joy.clinic.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 病历实体类测试
 */
class MedicalRecordTest {

    private MedicalRecord medicalRecord;

    @BeforeEach
    void setUp() {
        medicalRecord = new MedicalRecord(1L, 2L, "MR20240101001");
    }

    @Test
    void testConstructor() {
        assertNotNull(medicalRecord);
        assertEquals(1L, medicalRecord.getPatientId());
        assertEquals(2L, medicalRecord.getDoctorId());
        assertEquals("MR20240101001", medicalRecord.getRecordNumber());
        assertNotNull(medicalRecord.getRecordDate());
        assertEquals("草稿", medicalRecord.getStatus());
        assertEquals("门诊病历", medicalRecord.getRecordType());
        assertFalse(medicalRecord.getIsInfectious());
        assertFalse(medicalRecord.getIsChronicDisease());
    }

    @Test
    void testCanBeEdited() {
        // 草稿状态可以编辑
        medicalRecord.setStatus("草稿");
        assertTrue(medicalRecord.canBeEdited());

        // 待审核状态可以编辑
        medicalRecord.setStatus("待审核");
        assertTrue(medicalRecord.canBeEdited());

        // 已审核状态不可编辑
        medicalRecord.setStatus("已审核");
        assertFalse(medicalRecord.canBeEdited());

        // 已归档状态不可编辑
        medicalRecord.setStatus("已归档");
        assertFalse(medicalRecord.canBeEdited());
    }

    @Test
    void testIsReviewed() {
        // 草稿状态未审核
        medicalRecord.setStatus("草稿");
        assertFalse(medicalRecord.isReviewed());

        // 待审核状态未审核
        medicalRecord.setStatus("待审核");
        assertFalse(medicalRecord.isReviewed());

        // 已审核状态已审核
        medicalRecord.setStatus("已审核");
        assertTrue(medicalRecord.isReviewed());

        // 已归档状态已审核
        medicalRecord.setStatus("已归档");
        assertTrue(medicalRecord.isReviewed());
    }

    @Test
    void testSubmitForReview() {
        // 草稿状态可以提交审核
        medicalRecord.setStatus("草稿");
        medicalRecord.submitForReview();
        assertEquals("待审核", medicalRecord.getStatus());

        // 其他状态不能提交审核
        medicalRecord.setStatus("已审核");
        medicalRecord.submitForReview();
        assertEquals("已审核", medicalRecord.getStatus());
    }

    @Test
    void testApprove() {
        Long reviewDoctorId = 3L;
        String comments = "审核通过";

        // 待审核状态可以审核通过
        medicalRecord.setStatus("待审核");
        medicalRecord.approve(reviewDoctorId, comments);
        
        assertEquals("已审核", medicalRecord.getStatus());
        assertEquals(reviewDoctorId, medicalRecord.getReviewDoctorId());
        assertEquals(comments, medicalRecord.getReviewComments());
        assertNotNull(medicalRecord.getReviewTime());

        // 其他状态不能审核通过
        medicalRecord.setStatus("草稿");
        LocalDateTime originalReviewTime = medicalRecord.getReviewTime();
        medicalRecord.approve(4L, "测试");
        assertEquals("草稿", medicalRecord.getStatus());
        assertEquals(originalReviewTime, medicalRecord.getReviewTime());
    }

    @Test
    void testReject() {
        Long reviewDoctorId = 3L;
        String comments = "需要补充信息";

        // 待审核状态可以审核拒绝
        medicalRecord.setStatus("待审核");
        medicalRecord.reject(reviewDoctorId, comments);
        
        assertEquals("草稿", medicalRecord.getStatus());
        assertEquals(reviewDoctorId, medicalRecord.getReviewDoctorId());
        assertEquals(comments, medicalRecord.getReviewComments());
        assertNotNull(medicalRecord.getReviewTime());

        // 其他状态不能审核拒绝
        medicalRecord.setStatus("已审核");
        LocalDateTime originalReviewTime = medicalRecord.getReviewTime();
        medicalRecord.reject(4L, "测试");
        assertEquals("已审核", medicalRecord.getStatus());
        assertEquals(originalReviewTime, medicalRecord.getReviewTime());
    }

    @Test
    void testArchive() {
        // 已审核状态可以归档
        medicalRecord.setStatus("已审核");
        medicalRecord.archive();
        assertEquals("已归档", medicalRecord.getStatus());

        // 其他状态不能归档
        medicalRecord.setStatus("草稿");
        medicalRecord.archive();
        assertEquals("草稿", medicalRecord.getStatus());
    }

    @Test
    void testGenerateSummary() {
        medicalRecord.setChiefComplaint("头痛3天，伴有发热");
        medicalRecord.setFinalDiagnosis("上呼吸道感染");
        medicalRecord.setTreatmentPlan("抗感染治疗，对症处理");

        medicalRecord.generateSummary();

        String summary = medicalRecord.getSummary();
        assertNotNull(summary);
        assertTrue(summary.contains("主诉：头痛3天，伴有发热"));
        assertTrue(summary.contains("诊断：上呼吸道感染"));
        assertTrue(summary.contains("治疗：抗感染治疗，对症处理"));
    }

    @Test
    void testGenerateSummaryWithLongText() {
        String longComplaint = "患者主诉头痛3天，伴有发热、咳嗽、咽痛等症状，症状逐渐加重，影响日常生活和工作，特来就诊";
        medicalRecord.setChiefComplaint(longComplaint);

        medicalRecord.generateSummary();

        String summary = medicalRecord.getSummary();
        assertNotNull(summary);
        // 应该截取前50个字符
        assertTrue(summary.length() <= 100); // 考虑到还有其他内容
        assertTrue(summary.contains("主诉："));
    }

    @Test
    void testGenerateSummaryWithEmptyFields() {
        medicalRecord.setChiefComplaint("");
        medicalRecord.setFinalDiagnosis(null);
        medicalRecord.setTreatmentPlan("   ");

        medicalRecord.generateSummary();

        String summary = medicalRecord.getSummary();
        assertNotNull(summary);
        assertEquals("", summary);
    }

    @Test
    void testEquals() {
        MedicalRecord record1 = new MedicalRecord(1L, 2L, "MR001");
        MedicalRecord record2 = new MedicalRecord(2L, 3L, "MR001");
        MedicalRecord record3 = new MedicalRecord(1L, 2L, "MR002");

        // 相同病历编号应该相等
        assertEquals(record1, record2);
        
        // 不同病历编号不相等
        assertNotEquals(record1, record3);
        
        // 与null不相等
        assertNotEquals(record1, null);
        
        // 与其他类型不相等
        assertNotEquals(record1, "string");
    }

    @Test
    void testHashCode() {
        MedicalRecord record1 = new MedicalRecord(1L, 2L, "MR001");
        MedicalRecord record2 = new MedicalRecord(2L, 3L, "MR001");

        assertEquals(record1.hashCode(), record2.hashCode());
    }

    @Test
    void testToString() {
        String toString = medicalRecord.toString();
        assertNotNull(toString);
        assertTrue(toString.contains("MedicalRecord"));
        assertTrue(toString.contains("MR20240101001"));
        assertTrue(toString.contains("草稿"));
        assertTrue(toString.contains("门诊病历"));
    }

    @Test
    void testSettersAndGetters() {
        medicalRecord.setRegistrationId(100L);
        medicalRecord.setChiefComplaint("头痛");
        medicalRecord.setPresentIllness("头痛3天");
        medicalRecord.setPastHistory("既往体健");
        medicalRecord.setPersonalHistory("无特殊");
        medicalRecord.setFamilyHistory("无家族史");
        medicalRecord.setPhysicalExamination("体温38.5℃");
        medicalRecord.setAuxiliaryExamination("血常规正常");
        medicalRecord.setPreliminaryDiagnosis("上感");
        medicalRecord.setFinalDiagnosis("上呼吸道感染");
        medicalRecord.setTreatmentPlan("抗感染");
        medicalRecord.setMedicalOrders("口服药物");
        medicalRecord.setConditionAssessment("病情稳定");
        medicalRecord.setPrognosis("良好");
        medicalRecord.setFollowUpAdvice("3天后复诊");
        medicalRecord.setDepartment("内科");
        medicalRecord.setIsInfectious(true);
        medicalRecord.setIsChronicDisease(false);
        medicalRecord.setSummary("测试摘要");
        medicalRecord.setRemarks("备注信息");
        medicalRecord.setCreatedBy(1L);
        medicalRecord.setLastUpdatedBy(2L);

        assertEquals(100L, medicalRecord.getRegistrationId());
        assertEquals("头痛", medicalRecord.getChiefComplaint());
        assertEquals("头痛3天", medicalRecord.getPresentIllness());
        assertEquals("既往体健", medicalRecord.getPastHistory());
        assertEquals("无特殊", medicalRecord.getPersonalHistory());
        assertEquals("无家族史", medicalRecord.getFamilyHistory());
        assertEquals("体温38.5℃", medicalRecord.getPhysicalExamination());
        assertEquals("血常规正常", medicalRecord.getAuxiliaryExamination());
        assertEquals("上感", medicalRecord.getPreliminaryDiagnosis());
        assertEquals("上呼吸道感染", medicalRecord.getFinalDiagnosis());
        assertEquals("抗感染", medicalRecord.getTreatmentPlan());
        assertEquals("口服药物", medicalRecord.getMedicalOrders());
        assertEquals("病情稳定", medicalRecord.getConditionAssessment());
        assertEquals("良好", medicalRecord.getPrognosis());
        assertEquals("3天后复诊", medicalRecord.getFollowUpAdvice());
        assertEquals("内科", medicalRecord.getDepartment());
        assertTrue(medicalRecord.getIsInfectious());
        assertFalse(medicalRecord.getIsChronicDisease());
        assertEquals("测试摘要", medicalRecord.getSummary());
        assertEquals("备注信息", medicalRecord.getRemarks());
        assertEquals(1L, medicalRecord.getCreatedBy());
        assertEquals(2L, medicalRecord.getLastUpdatedBy());
    }
}