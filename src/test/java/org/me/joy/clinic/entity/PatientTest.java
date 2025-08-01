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
 * 患者实体类测试
 */
class PatientTest {

    private Validator validator;
    private Patient patient;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();

        patient = new Patient();
        patient.setPatientNumber("P202401001");
        patient.setName("张三");
        patient.setPhone("13800138000");
        patient.setIdCard("110101199001011234");
        patient.setBirthDate(LocalDate.of(1990, 1, 1));
        patient.setGender("男");
        patient.setAddress("北京市朝阳区");
        patient.setStatus("正常");
    }

    @Test
    void testValidPatient() {
        // When
        Set<ConstraintViolation<Patient>> violations = validator.validate(patient);

        // Then
        assertTrue(violations.isEmpty(), "有效的患者信息不应该有验证错误");
    }

    @Test
    void testPatientNumberValidation() {
        // Given - 患者编号为空
        patient.setPatientNumber("");

        // When
        Set<ConstraintViolation<Patient>> violations = validator.validate(patient);

        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("患者编号不能为空")));
    }

    @Test
    void testPatientNumberTooLong() {
        // Given - 患者编号过长
        patient.setPatientNumber("P".repeat(51));

        // When
        Set<ConstraintViolation<Patient>> violations = validator.validate(patient);

        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("患者编号长度不能超过50个字符")));
    }

    @Test
    void testNameValidation() {
        // Given - 患者姓名为空
        patient.setName("");

        // When
        Set<ConstraintViolation<Patient>> violations = validator.validate(patient);

        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("患者姓名不能为空")));
    }

    @Test
    void testNameTooLong() {
        // Given - 患者姓名过长
        patient.setName("张".repeat(101));

        // When
        Set<ConstraintViolation<Patient>> violations = validator.validate(patient);

        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("患者姓名长度不能超过100个字符")));
    }

    @Test
    void testPhoneValidation() {
        // Given - 无效的手机号
        patient.setPhone("12345678901");

        // When
        Set<ConstraintViolation<Patient>> violations = validator.validate(patient);

        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("手机号码格式不正确")));
    }

    @Test
    void testValidPhoneNumbers() {
        // Test valid phone numbers
        String[] validPhones = {"13800138000", "15912345678", "18612345678", "19912345678"};
        
        for (String phone : validPhones) {
            patient.setPhone(phone);
            Set<ConstraintViolation<Patient>> violations = validator.validate(patient);
            assertTrue(violations.stream().noneMatch(v -> v.getPropertyPath().toString().equals("phone")),
                "手机号 " + phone + " 应该是有效的");
        }
    }

    @Test
    void testIdCardValidation() {
        // Given - 无效的身份证号
        patient.setIdCard("123456789012345678");

        // When
        Set<ConstraintViolation<Patient>> violations = validator.validate(patient);

        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("身份证号码格式不正确")));
    }

    @Test
    void testValidIdCards() {
        // Test valid ID cards
        String[] validIdCards = {
            "110101199001011234", 
            "110101200001011234", 
            "11010119900101123X"
        };
        
        for (String idCard : validIdCards) {
            patient.setIdCard(idCard);
            Set<ConstraintViolation<Patient>> violations = validator.validate(patient);
            assertTrue(violations.stream().noneMatch(v -> v.getPropertyPath().toString().equals("idCard")),
                "身份证号 " + idCard + " 应该是有效的");
        }
    }

    @Test
    void testBirthDateValidation() {
        // Given - 未来的出生日期
        patient.setBirthDate(LocalDate.now().plusDays(1));

        // When
        Set<ConstraintViolation<Patient>> violations = validator.validate(patient);

        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("出生日期不能晚于当前日期")));
    }

    @Test
    void testGenderValidation() {
        // Given - 无效的性别
        patient.setGender("其他");

        // When
        Set<ConstraintViolation<Patient>> violations = validator.validate(patient);

        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("性别只能是男、女或未知")));
    }

    @Test
    void testValidGenders() {
        // Test valid genders
        String[] validGenders = {"男", "女", "未知"};
        
        for (String gender : validGenders) {
            patient.setGender(gender);
            Set<ConstraintViolation<Patient>> violations = validator.validate(patient);
            assertTrue(violations.stream().noneMatch(v -> v.getPropertyPath().toString().equals("gender")),
                "性别 " + gender + " 应该是有效的");
        }
    }

    @Test
    void testBloodTypeValidation() {
        // Test valid blood types
        String[] validBloodTypes = {"A", "B", "AB", "O", "A+", "B+", "AB+", "O+", "A-", "B-", "AB-", "O-"};
        
        for (String bloodType : validBloodTypes) {
            patient.setBloodType(bloodType);
            Set<ConstraintViolation<Patient>> violations = validator.validate(patient);
            assertTrue(violations.stream().noneMatch(v -> v.getPropertyPath().toString().equals("bloodType")),
                "血型 " + bloodType + " 应该是有效的");
        }
    }

    @Test
    void testInvalidBloodType() {
        // Given - 无效的血型
        patient.setBloodType("C+");

        // When
        Set<ConstraintViolation<Patient>> violations = validator.validate(patient);

        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("血型格式不正确")));
    }

    @Test
    void testMaritalStatusValidation() {
        // Test valid marital statuses
        String[] validStatuses = {"未婚", "已婚", "离异", "丧偶", "未知"};
        
        for (String status : validStatuses) {
            patient.setMaritalStatus(status);
            Set<ConstraintViolation<Patient>> violations = validator.validate(patient);
            assertTrue(violations.stream().noneMatch(v -> v.getPropertyPath().toString().equals("maritalStatus")),
                "婚姻状况 " + status + " 应该是有效的");
        }
    }

    @Test
    void testInvalidMaritalStatus() {
        // Given - 无效的婚姻状况
        patient.setMaritalStatus("其他");

        // When
        Set<ConstraintViolation<Patient>> violations = validator.validate(patient);

        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("婚姻状况只能是未婚、已婚、离异、丧偶或未知")));
    }

    @Test
    void testPatientStatusValidation() {
        // Test valid patient statuses
        String[] validStatuses = {"正常", "黑名单", "暂停服务"};
        
        for (String status : validStatuses) {
            patient.setStatus(status);
            Set<ConstraintViolation<Patient>> violations = validator.validate(patient);
            assertTrue(violations.stream().noneMatch(v -> v.getPropertyPath().toString().equals("status")),
                "患者状态 " + status + " 应该是有效的");
        }
    }

    @Test
    void testInvalidPatientStatus() {
        // Given - 无效的患者状态
        patient.setStatus("其他");

        // When
        Set<ConstraintViolation<Patient>> violations = validator.validate(patient);

        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("患者状态只能是正常、黑名单或暂停服务")));
    }

    @Test
    void testGetAge() {
        // Given
        patient.setBirthDate(LocalDate.of(1990, 1, 1));

        // When
        Integer age = patient.getAge();

        // Then
        assertNotNull(age);
        assertEquals(LocalDate.now().getYear() - 1990, age);
    }

    @Test
    void testGetAgeWithNullBirthDate() {
        // Given
        patient.setBirthDate(null);

        // When
        Integer age = patient.getAge();

        // Then
        assertNull(age);
    }

    @Test
    void testIncrementVisitCount() {
        // Given
        patient.setVisitCount(0);
        patient.setFirstVisitTime(null);
        patient.setLastVisitTime(null);

        // When
        patient.incrementVisitCount();

        // Then
        assertEquals(1, patient.getVisitCount());
        assertNotNull(patient.getFirstVisitTime());
        assertNotNull(patient.getLastVisitTime());
    }

    @Test
    void testIncrementVisitCountWithExistingVisits() {
        // Given
        patient.setVisitCount(5);
        LocalDateTime firstVisit = LocalDateTime.now().minusDays(30);
        patient.setFirstVisitTime(firstVisit);

        // When
        patient.incrementVisitCount();

        // Then
        assertEquals(6, patient.getVisitCount());
        assertEquals(firstVisit, patient.getFirstVisitTime()); // 首次就诊时间不变
        assertNotNull(patient.getLastVisitTime());
    }

    @Test
    void testIncrementVisitCountWithNullCount() {
        // Given
        patient.setVisitCount(null);

        // When
        patient.incrementVisitCount();

        // Then
        assertEquals(1, patient.getVisitCount());
    }

    @Test
    void testEqualsAndHashCode() {
        // Given
        Patient patient1 = new Patient();
        patient1.setPatientNumber("P001");
        
        Patient patient2 = new Patient();
        patient2.setPatientNumber("P001");
        
        Patient patient3 = new Patient();
        patient3.setPatientNumber("P002");

        // Then
        assertEquals(patient1, patient2);
        assertNotEquals(patient1, patient3);
        assertEquals(patient1.hashCode(), patient2.hashCode());
        assertNotEquals(patient1.hashCode(), patient3.hashCode());
    }

    @Test
    void testToString() {
        // When
        String result = patient.toString();

        // Then
        assertNotNull(result);
        assertTrue(result.contains("Patient{"));
        assertTrue(result.contains("patientNumber='P202401001'"));
        assertTrue(result.contains("name='张三'"));
        assertTrue(result.contains("phone='13800138000'"));
    }

    @Test
    void testConstructors() {
        // Test default constructor
        Patient patient1 = new Patient();
        assertNotNull(patient1);

        // Test parameterized constructor
        Patient patient2 = new Patient("P001", "李四", "13900139000");
        assertEquals("P001", patient2.getPatientNumber());
        assertEquals("李四", patient2.getName());
        assertEquals("13900139000", patient2.getPhone());
    }

    @Test
    void testDefaultValues() {
        // Given
        Patient newPatient = new Patient();

        // Then
        assertEquals(false, newPatient.getIsVip());
        assertEquals("正常", newPatient.getStatus());
        assertEquals(0, newPatient.getVisitCount());
    }
}