package org.me.joy.clinic.entity;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 生命体征实体测试类
 * 
 * @author Kiro
 */
class VitalSignsTest {

    private Validator validator;
    private VitalSigns vitalSigns;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
        
        vitalSigns = new VitalSigns();
        vitalSigns.setPatientId(1L);
        vitalSigns.setRecordedBy(1L);
        vitalSigns.setRecordedAt(LocalDateTime.now());
        vitalSigns.setIsAbnormal(false);
    }

    @Test
    void testValidVitalSigns() {
        // 设置正常的生命体征数据
        vitalSigns.setSystolicBp(120);
        vitalSigns.setDiastolicBp(80);
        vitalSigns.setTemperature(new BigDecimal("36.5"));
        vitalSigns.setHeartRate(75);
        vitalSigns.setRespiratoryRate(16);
        vitalSigns.setOxygenSaturation(98);
        vitalSigns.setWeight(new BigDecimal("65.50"));
        vitalSigns.setHeight(170);
        vitalSigns.setPainScore(2);

        Set<ConstraintViolation<VitalSigns>> violations = validator.validate(vitalSigns);
        assertTrue(violations.isEmpty(), "正常的生命体征数据应该通过验证");
    }

    @Test
    void testRequiredFields() {
        VitalSigns emptyVitalSigns = new VitalSigns();
        
        Set<ConstraintViolation<VitalSigns>> violations = validator.validate(emptyVitalSigns);
        
        // 检查必填字段的验证错误
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("patientId")));
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("recordedBy")));
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("recordedAt")));
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("isAbnormal")));
    }

    @Test
    void testBloodPressureValidation() {
        // 测试收缩压过低
        vitalSigns.setSystolicBp(50);
        Set<ConstraintViolation<VitalSigns>> violations = validator.validate(vitalSigns);
        assertTrue(violations.stream().anyMatch(v -> 
            v.getPropertyPath().toString().equals("systolicBp") && 
            v.getMessage().contains("不能低于60mmHg")));

        // 测试收缩压过高
        vitalSigns.setSystolicBp(350);
        violations = validator.validate(vitalSigns);
        assertTrue(violations.stream().anyMatch(v -> 
            v.getPropertyPath().toString().equals("systolicBp") && 
            v.getMessage().contains("不能高于300mmHg")));

        // 测试舒张压过低
        vitalSigns.setSystolicBp(120);
        vitalSigns.setDiastolicBp(30);
        violations = validator.validate(vitalSigns);
        assertTrue(violations.stream().anyMatch(v -> 
            v.getPropertyPath().toString().equals("diastolicBp") && 
            v.getMessage().contains("不能低于40mmHg")));

        // 测试舒张压过高
        vitalSigns.setDiastolicBp(250);
        violations = validator.validate(vitalSigns);
        assertTrue(violations.stream().anyMatch(v -> 
            v.getPropertyPath().toString().equals("diastolicBp") && 
            v.getMessage().contains("不能高于200mmHg")));
    }

    @Test
    void testTemperatureValidation() {
        // 测试体温过低
        vitalSigns.setTemperature(new BigDecimal("30.0"));
        Set<ConstraintViolation<VitalSigns>> violations = validator.validate(vitalSigns);
        assertTrue(violations.stream().anyMatch(v -> 
            v.getPropertyPath().toString().equals("temperature") && 
            v.getMessage().contains("不能低于35.0°C")));

        // 测试体温过高
        vitalSigns.setTemperature(new BigDecimal("45.0"));
        violations = validator.validate(vitalSigns);
        assertTrue(violations.stream().anyMatch(v -> 
            v.getPropertyPath().toString().equals("temperature") && 
            v.getMessage().contains("不能高于42.0°C")));

        // 测试体温格式错误
        vitalSigns.setTemperature(new BigDecimal("36.55"));
        violations = validator.validate(vitalSigns);
        assertTrue(violations.stream().anyMatch(v -> 
            v.getPropertyPath().toString().equals("temperature") && 
            v.getMessage().contains("格式不正确")));
    }

    @Test
    void testHeartRateValidation() {
        // 测试心率过低
        vitalSigns.setHeartRate(30);
        Set<ConstraintViolation<VitalSigns>> violations = validator.validate(vitalSigns);
        assertTrue(violations.stream().anyMatch(v -> 
            v.getPropertyPath().toString().equals("heartRate") && 
            v.getMessage().contains("不能低于40次/分钟")));

        // 测试心率过高
        vitalSigns.setHeartRate(250);
        violations = validator.validate(vitalSigns);
        assertTrue(violations.stream().anyMatch(v -> 
            v.getPropertyPath().toString().equals("heartRate") && 
            v.getMessage().contains("不能高于200次/分钟")));
    }

    @Test
    void testRespiratoryRateValidation() {
        // 测试呼吸频率过低
        vitalSigns.setRespiratoryRate(5);
        Set<ConstraintViolation<VitalSigns>> violations = validator.validate(vitalSigns);
        assertTrue(violations.stream().anyMatch(v -> 
            v.getPropertyPath().toString().equals("respiratoryRate") && 
            v.getMessage().contains("不能低于8次/分钟")));

        // 测试呼吸频率过高
        vitalSigns.setRespiratoryRate(50);
        violations = validator.validate(vitalSigns);
        assertTrue(violations.stream().anyMatch(v -> 
            v.getPropertyPath().toString().equals("respiratoryRate") && 
            v.getMessage().contains("不能高于40次/分钟")));
    }

    @Test
    void testOxygenSaturationValidation() {
        // 测试血氧饱和度过低
        vitalSigns.setOxygenSaturation(60);
        Set<ConstraintViolation<VitalSigns>> violations = validator.validate(vitalSigns);
        assertTrue(violations.stream().anyMatch(v -> 
            v.getPropertyPath().toString().equals("oxygenSaturation") && 
            v.getMessage().contains("不能低于70%")));

        // 测试血氧饱和度过高
        vitalSigns.setOxygenSaturation(110);
        violations = validator.validate(vitalSigns);
        assertTrue(violations.stream().anyMatch(v -> 
            v.getPropertyPath().toString().equals("oxygenSaturation") && 
            v.getMessage().contains("不能高于100%")));
    }

    @Test
    void testWeightValidation() {
        // 测试体重过低
        vitalSigns.setWeight(new BigDecimal("0.3"));
        Set<ConstraintViolation<VitalSigns>> violations = validator.validate(vitalSigns);
        assertTrue(violations.stream().anyMatch(v -> 
            v.getPropertyPath().toString().equals("weight") && 
            v.getMessage().contains("不能低于0.5kg")));

        // 测试体重过高
        vitalSigns.setWeight(new BigDecimal("600.0"));
        violations = validator.validate(vitalSigns);
        assertTrue(violations.stream().anyMatch(v -> 
            v.getPropertyPath().toString().equals("weight") && 
            v.getMessage().contains("不能高于500kg")));
    }

    @Test
    void testHeightValidation() {
        // 测试身高过低
        vitalSigns.setHeight(20);
        Set<ConstraintViolation<VitalSigns>> violations = validator.validate(vitalSigns);
        assertTrue(violations.stream().anyMatch(v -> 
            v.getPropertyPath().toString().equals("height") && 
            v.getMessage().contains("不能低于30cm")));

        // 测试身高过高
        vitalSigns.setHeight(300);
        violations = validator.validate(vitalSigns);
        assertTrue(violations.stream().anyMatch(v -> 
            v.getPropertyPath().toString().equals("height") && 
            v.getMessage().contains("不能高于250cm")));
    }

    @Test
    void testPainScoreValidation() {
        // 测试疼痛评分过低
        vitalSigns.setPainScore(-1);
        Set<ConstraintViolation<VitalSigns>> violations = validator.validate(vitalSigns);
        assertTrue(violations.stream().anyMatch(v -> 
            v.getPropertyPath().toString().equals("painScore") && 
            v.getMessage().contains("不能低于0")));

        // 测试疼痛评分过高
        vitalSigns.setPainScore(15);
        violations = validator.validate(vitalSigns);
        assertTrue(violations.stream().anyMatch(v -> 
            v.getPropertyPath().toString().equals("painScore") && 
            v.getMessage().contains("不能高于10")));
    }

    @Test
    void testBloodPressureConsistency() {
        // 测试正常的血压一致性
        vitalSigns.setSystolicBp(120);
        vitalSigns.setDiastolicBp(80);
        assertTrue(vitalSigns.isBloodPressureValid(), "收缩压应该大于舒张压");

        // 测试异常的血压一致性
        vitalSigns.setSystolicBp(80);
        vitalSigns.setDiastolicBp(120);
        assertFalse(vitalSigns.isBloodPressureValid(), "收缩压不应该小于舒张压");

        // 测试空值情况
        vitalSigns.setSystolicBp(null);
        vitalSigns.setDiastolicBp(80);
        assertTrue(vitalSigns.isBloodPressureValid(), "空值情况应该返回true");
    }

    @Test
    void testBmiCalculation() {
        // 测试正常BMI计算
        vitalSigns.setWeight(new BigDecimal("70.0"));
        vitalSigns.setHeight(175);
        
        BigDecimal expectedBmi = new BigDecimal("22.9");
        BigDecimal actualBmi = vitalSigns.calculateBmi();
        
        assertNotNull(actualBmi, "BMI计算结果不应该为空");
        assertEquals(expectedBmi, actualBmi, "BMI计算结果应该正确");

        // 测试空值情况
        vitalSigns.setWeight(null);
        assertNull(vitalSigns.calculateBmi(), "体重为空时BMI应该为null");

        vitalSigns.setWeight(new BigDecimal("70.0"));
        vitalSigns.setHeight(null);
        assertNull(vitalSigns.calculateBmi(), "身高为空时BMI应该为null");

        vitalSigns.setHeight(0);
        assertNull(vitalSigns.calculateBmi(), "身高为0时BMI应该为null");
    }

    @Test
    void testAbnormalDetection() {
        // 测试正常生命体征
        vitalSigns.setSystolicBp(120);
        vitalSigns.setDiastolicBp(80);
        vitalSigns.setTemperature(new BigDecimal("36.5"));
        vitalSigns.setHeartRate(75);
        vitalSigns.setRespiratoryRate(16);
        vitalSigns.setOxygenSaturation(98);
        vitalSigns.setBmi(new BigDecimal("22.0"));

        assertFalse(vitalSigns.checkAbnormal(), "正常生命体征应该返回false");
        assertFalse(vitalSigns.getIsAbnormal(), "isAbnormal应该为false");
        assertNull(vitalSigns.getAbnormalIndicators(), "异常指标说明应该为空");

        // 测试异常生命体征
        vitalSigns.setSystolicBp(160); // 高血压
        vitalSigns.setTemperature(new BigDecimal("38.5")); // 发热
        vitalSigns.setHeartRate(110); // 心动过速
        vitalSigns.setOxygenSaturation(90); // 血氧饱和度低

        assertTrue(vitalSigns.checkAbnormal(), "异常生命体征应该返回true");
        assertTrue(vitalSigns.getIsAbnormal(), "isAbnormal应该为true");
        assertNotNull(vitalSigns.getAbnormalIndicators(), "异常指标说明不应该为空");
        
        String abnormalIndicators = vitalSigns.getAbnormalIndicators();
        assertTrue(abnormalIndicators.contains("收缩压异常"), "应该包含收缩压异常");
        assertTrue(abnormalIndicators.contains("体温异常"), "应该包含体温异常");
        assertTrue(abnormalIndicators.contains("心率异常"), "应该包含心率异常");
        assertTrue(abnormalIndicators.contains("血氧饱和度异常"), "应该包含血氧饱和度异常");
    }

    @Test
    void testStringFieldsLength() {
        // 测试意识状态长度 - 这个字符串有21个字符，应该超过20个字符的限制
        vitalSigns.setConsciousnessLevel("这是一个超过二十个字符的意识状态描述测试内容");
        Set<ConstraintViolation<VitalSigns>> violations = validator.validate(vitalSigns);
        assertTrue(violations.stream().anyMatch(v -> 
            v.getPropertyPath().toString().equals("consciousnessLevel") && 
            v.getMessage().contains("不能超过20个字符")));

        // 测试备注信息长度
        StringBuilder longRemarks = new StringBuilder();
        for (int i = 0; i < 1001; i++) {
            longRemarks.append("a");
        }
        vitalSigns.setRemarks(longRemarks.toString());
        violations = validator.validate(vitalSigns);
        assertTrue(violations.stream().anyMatch(v -> 
            v.getPropertyPath().toString().equals("remarks") && 
            v.getMessage().contains("不能超过1000个字符")));

        // 测试异常指标说明长度
        StringBuilder longAbnormalIndicators = new StringBuilder();
        for (int i = 0; i < 501; i++) {
            longAbnormalIndicators.append("a");
        }
        vitalSigns.setAbnormalIndicators(longAbnormalIndicators.toString());
        violations = validator.validate(vitalSigns);
        assertTrue(violations.stream().anyMatch(v -> 
            v.getPropertyPath().toString().equals("abnormalIndicators") && 
            v.getMessage().contains("不能超过500个字符")));
    }
}