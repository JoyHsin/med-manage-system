package org.me.joy.clinic.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

import java.math.BigDecimal;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 药品实体类单元测试
 */
@DisplayName("药品实体测试")
class MedicineTest {

    private Validator validator;
    private Medicine medicine;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
        
        medicine = new Medicine();
        medicine.setMedicineCode("MED001");
        medicine.setName("阿莫西林胶囊");
        medicine.setCategory("处方药");
        medicine.setUnit("盒");
        medicine.setPurchasePrice(new BigDecimal("15.50"));
        medicine.setSellingPrice(new BigDecimal("18.80"));
        medicine.setMinStockLevel(10);
        medicine.setMaxStockLevel(100);
        medicine.setSafetyStockLevel(20);
    }

    @Test
    @DisplayName("创建有效药品实体")
    void testCreateValidMedicine() {
        Set<ConstraintViolation<Medicine>> violations = validator.validate(medicine);
        assertTrue(violations.isEmpty(), "有效的药品实体不应有验证错误");
    }

    @Test
    @DisplayName("药品编码不能为空")
    void testMedicineCodeNotBlank() {
        medicine.setMedicineCode("");
        Set<ConstraintViolation<Medicine>> violations = validator.validate(medicine);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("药品编码不能为空")));
    }

    @Test
    @DisplayName("药品编码长度限制")
    void testMedicineCodeMaxLength() {
        medicine.setMedicineCode("A".repeat(51));
        Set<ConstraintViolation<Medicine>> violations = validator.validate(medicine);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("药品编码长度不能超过50个字符")));
    }

    @Test
    @DisplayName("药品名称不能为空")
    void testNameNotBlank() {
        medicine.setName("");
        Set<ConstraintViolation<Medicine>> violations = validator.validate(medicine);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("药品名称不能为空")));
    }

    @Test
    @DisplayName("药品分类验证")
    void testCategoryValidation() {
        medicine.setCategory("无效分类");
        Set<ConstraintViolation<Medicine>> violations = validator.validate(medicine);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("药品分类只能是")));
    }

    @Test
    @DisplayName("有效的药品分类")
    void testValidCategories() {
        String[] validCategories = {"处方药", "非处方药", "中药", "西药", "生物制品", "疫苗", "其他"};
        
        for (String category : validCategories) {
            medicine.setCategory(category);
            Set<ConstraintViolation<Medicine>> violations = validator.validate(medicine);
            assertTrue(violations.isEmpty(), "分类 '" + category + "' 应该是有效的");
        }
    }

    @Test
    @DisplayName("单位不能为空")
    void testUnitNotBlank() {
        medicine.setUnit("");
        Set<ConstraintViolation<Medicine>> violations = validator.validate(medicine);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("单位不能为空")));
    }

    @Test
    @DisplayName("价格不能为负数")
    void testPriceNotNegative() {
        medicine.setPurchasePrice(new BigDecimal("-1.00"));
        Set<ConstraintViolation<Medicine>> violations = validator.validate(medicine);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("进价不能为负数")));
    }

    @Test
    @DisplayName("库存水平不能为负数")
    void testStockLevelNotNegative() {
        medicine.setMinStockLevel(-1);
        Set<ConstraintViolation<Medicine>> violations = validator.validate(medicine);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("最小库存量不能为负数")));
    }

    @Test
    @DisplayName("计算利润率")
    void testCalculateProfitMargin() {
        medicine.setPurchasePrice(new BigDecimal("10.00"));
        medicine.setSellingPrice(new BigDecimal("12.00"));
        
        BigDecimal profitMargin = medicine.getProfitMargin();
        assertEquals(new BigDecimal("20.0000"), profitMargin);
    }

    @Test
    @DisplayName("利润率计算 - 进价为零")
    void testProfitMarginWithZeroPurchasePrice() {
        medicine.setPurchasePrice(BigDecimal.ZERO);
        medicine.setSellingPrice(new BigDecimal("12.00"));
        
        BigDecimal profitMargin = medicine.getProfitMargin();
        assertEquals(BigDecimal.ZERO, profitMargin);
    }

    @Test
    @DisplayName("检查是否需要补货")
    void testNeedsRestock() {
        medicine.setMinStockLevel(20);
        
        assertTrue(medicine.needsRestock(15), "库存低于最小库存时应需要补货");
        assertTrue(medicine.needsRestock(20), "库存等于最小库存时应需要补货");
        assertFalse(medicine.needsRestock(25), "库存高于最小库存时不需要补货");
        assertFalse(medicine.needsRestock(null), "库存为null时不需要补货");
    }

    @Test
    @DisplayName("检查是否库存过多")
    void testIsOverstocked() {
        medicine.setMaxStockLevel(100);
        
        assertTrue(medicine.isOverstocked(150), "库存高于最大库存时应为过多");
        assertFalse(medicine.isOverstocked(100), "库存等于最大库存时不为过多");
        assertFalse(medicine.isOverstocked(80), "库存低于最大库存时不为过多");
        assertFalse(medicine.isOverstocked(null), "库存为null时不为过多");
    }

    @Test
    @DisplayName("检查是否为处方药")
    void testIsPrescriptionDrug() {
        medicine.setCategory("处方药");
        medicine.setRequiresPrescription(false);
        assertTrue(medicine.isPrescriptionDrug(), "分类为处方药时应返回true");
        
        medicine.setCategory("非处方药");
        medicine.setRequiresPrescription(true);
        assertTrue(medicine.isPrescriptionDrug(), "需要处方标志为true时应返回true");
        
        medicine.setCategory("非处方药");
        medicine.setRequiresPrescription(false);
        assertFalse(medicine.isPrescriptionDrug(), "分类为非处方药且不需要处方时应返回false");
    }

    @Test
    @DisplayName("药品实体相等性测试")
    void testEquality() {
        Medicine medicine1 = new Medicine();
        medicine1.setMedicineCode("MED001");
        
        Medicine medicine2 = new Medicine();
        medicine2.setMedicineCode("MED001");
        
        Medicine medicine3 = new Medicine();
        medicine3.setMedicineCode("MED002");
        
        assertEquals(medicine1, medicine2, "相同药品编码的药品应相等");
        assertNotEquals(medicine1, medicine3, "不同药品编码的药品不应相等");
        assertEquals(medicine1.hashCode(), medicine2.hashCode(), "相等的药品应有相同的hashCode");
    }

    @Test
    @DisplayName("药品实体toString测试")
    void testToString() {
        medicine.setId(1L);
        String toString = medicine.toString();
        
        assertTrue(toString.contains("Medicine{"));
        assertTrue(toString.contains("id=1"));
        assertTrue(toString.contains("medicineCode='MED001'"));
        assertTrue(toString.contains("name='阿莫西林胶囊'"));
        assertTrue(toString.contains("category='处方药'"));
    }

    @Test
    @DisplayName("构造函数测试")
    void testConstructors() {
        Medicine defaultMedicine = new Medicine();
        assertNull(defaultMedicine.getMedicineCode());
        assertNull(defaultMedicine.getName());
        assertNull(defaultMedicine.getCategory());
        
        Medicine parameterizedMedicine = new Medicine("MED002", "布洛芬片", "非处方药");
        assertEquals("MED002", parameterizedMedicine.getMedicineCode());
        assertEquals("布洛芬片", parameterizedMedicine.getName());
        assertEquals("非处方药", parameterizedMedicine.getCategory());
    }

    @Test
    @DisplayName("默认值测试")
    void testDefaultValues() {
        Medicine newMedicine = new Medicine();
        assertEquals(Boolean.FALSE, newMedicine.getRequiresPrescription());
        assertEquals(Boolean.FALSE, newMedicine.getIsControlledSubstance());
        assertEquals(Boolean.TRUE, newMedicine.getEnabled());
    }
}