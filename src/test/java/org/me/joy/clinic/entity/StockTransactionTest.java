package org.me.joy.clinic.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 库存交易实体类单元测试
 */
@DisplayName("库存交易实体测试")
class StockTransactionTest {

    private Validator validator;
    private StockTransaction transaction;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
        
        transaction = new StockTransaction();
        transaction.setTransactionNumber("TXN001");
        transaction.setMedicineId(1L);
        transaction.setTransactionType("入库");
        transaction.setQuantity(100);
        transaction.setUnitPrice(new BigDecimal("15.50"));
        transaction.setTransactionDate(LocalDateTime.now());
        transaction.setStatus("待审核");
    }

    @Test
    @DisplayName("创建有效库存交易实体")
    void testCreateValidStockTransaction() {
        Set<ConstraintViolation<StockTransaction>> violations = validator.validate(transaction);
        assertTrue(violations.isEmpty(), "有效的库存交易实体不应有验证错误");
    }

    @Test
    @DisplayName("交易编号不能为空")
    void testTransactionNumberNotBlank() {
        transaction.setTransactionNumber("");
        Set<ConstraintViolation<StockTransaction>> violations = validator.validate(transaction);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("交易编号不能为空")));
    }

    @Test
    @DisplayName("交易编号长度限制")
    void testTransactionNumberMaxLength() {
        transaction.setTransactionNumber("A".repeat(51));
        Set<ConstraintViolation<StockTransaction>> violations = validator.validate(transaction);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("交易编号长度不能超过50个字符")));
    }

    @Test
    @DisplayName("药品ID不能为空")
    void testMedicineIdNotNull() {
        transaction.setMedicineId(null);
        Set<ConstraintViolation<StockTransaction>> violations = validator.validate(transaction);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("药品ID不能为空")));
    }

    @Test
    @DisplayName("交易类型验证")
    void testTransactionTypeValidation() {
        transaction.setTransactionType("无效类型");
        Set<ConstraintViolation<StockTransaction>> violations = validator.validate(transaction);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("交易类型只能是")));
    }

    @Test
    @DisplayName("有效的交易类型")
    void testValidTransactionTypes() {
        String[] validTypes = {"入库", "出库", "调拨", "盘点", "报损", "退货", "过期处理"};
        
        for (String type : validTypes) {
            transaction.setTransactionType(type);
            Set<ConstraintViolation<StockTransaction>> violations = validator.validate(transaction);
            assertTrue(violations.isEmpty(), "交易类型 '" + type + "' 应该是有效的");
        }
    }

    @Test
    @DisplayName("交易数量不能为空")
    void testQuantityNotNull() {
        transaction.setQuantity(null);
        Set<ConstraintViolation<StockTransaction>> violations = validator.validate(transaction);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("交易数量不能为空")));
    }

    @Test
    @DisplayName("单价不能为负数")
    void testUnitPriceNotNegative() {
        transaction.setUnitPrice(new BigDecimal("-1.00"));
        Set<ConstraintViolation<StockTransaction>> violations = validator.validate(transaction);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("单价不能为负数")));
    }

    @Test
    @DisplayName("交易状态验证")
    void testStatusValidation() {
        transaction.setStatus("无效状态");
        Set<ConstraintViolation<StockTransaction>> violations = validator.validate(transaction);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("交易状态只能是")));
    }

    @Test
    @DisplayName("有效的交易状态")
    void testValidStatuses() {
        String[] validStatuses = {"待审核", "已确认", "已取消"};
        
        for (String status : validStatuses) {
            transaction.setStatus(status);
            Set<ConstraintViolation<StockTransaction>> violations = validator.validate(transaction);
            assertTrue(violations.isEmpty(), "交易状态 '" + status + "' 应该是有效的");
        }
    }

    @Test
    @DisplayName("交易日期不能为空")
    void testTransactionDateNotNull() {
        transaction.setTransactionDate(null);
        Set<ConstraintViolation<StockTransaction>> violations = validator.validate(transaction);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("交易日期不能为空")));
    }

    @Test
    @DisplayName("计算总金额")
    void testCalculateTotalAmount() {
        transaction.setUnitPrice(new BigDecimal("10.50"));
        transaction.setQuantity(20);
        
        transaction.calculateTotalAmount();
        assertEquals(new BigDecimal("210.00"), transaction.getTotalAmount());
    }

    @Test
    @DisplayName("计算总金额 - 负数量")
    void testCalculateTotalAmountWithNegativeQuantity() {
        transaction.setUnitPrice(new BigDecimal("10.50"));
        transaction.setQuantity(-20);
        
        transaction.calculateTotalAmount();
        assertEquals(new BigDecimal("210.00"), transaction.getTotalAmount());
    }

    @Test
    @DisplayName("检查是否为入库交易")
    void testIsInboundTransaction() {
        transaction.setTransactionType("入库");
        transaction.setQuantity(100);
        assertTrue(transaction.isInboundTransaction());
        
        transaction.setQuantity(-100);
        assertFalse(transaction.isInboundTransaction());
        
        transaction.setTransactionType("出库");
        transaction.setQuantity(100);
        assertFalse(transaction.isInboundTransaction());
    }

    @Test
    @DisplayName("检查是否为出库交易")
    void testIsOutboundTransaction() {
        transaction.setTransactionType("出库");
        transaction.setQuantity(-100);
        assertTrue(transaction.isOutboundTransaction());
        
        transaction.setQuantity(100);
        assertFalse(transaction.isOutboundTransaction());
        
        transaction.setTransactionType("入库");
        transaction.setQuantity(-100);
        assertFalse(transaction.isOutboundTransaction());
    }

    @Test
    @DisplayName("检查是否已确认")
    void testIsConfirmed() {
        transaction.setStatus("已确认");
        assertTrue(transaction.isConfirmed());
        
        transaction.setStatus("待审核");
        assertFalse(transaction.isConfirmed());
        
        transaction.setStatus("已取消");
        assertFalse(transaction.isConfirmed());
    }

    @Test
    @DisplayName("检查药品是否即将过期")
    void testIsExpiringSoon() {
        transaction.setExpiryDate(LocalDate.now().plusDays(15));
        assertTrue(transaction.isExpiringSoon());
        
        transaction.setExpiryDate(LocalDate.now().plusDays(45));
        assertFalse(transaction.isExpiringSoon());
        
        transaction.setExpiryDate(null);
        assertFalse(transaction.isExpiringSoon());
    }

    @Test
    @DisplayName("检查药品是否已过期")
    void testIsExpired() {
        transaction.setExpiryDate(LocalDate.now().minusDays(1));
        assertTrue(transaction.isExpired());
        
        transaction.setExpiryDate(LocalDate.now().plusDays(1));
        assertFalse(transaction.isExpired());
        
        transaction.setExpiryDate(null);
        assertFalse(transaction.isExpired());
    }

    @Test
    @DisplayName("库存交易实体相等性测试")
    void testEquality() {
        StockTransaction transaction1 = new StockTransaction();
        transaction1.setTransactionNumber("TXN001");
        
        StockTransaction transaction2 = new StockTransaction();
        transaction2.setTransactionNumber("TXN001");
        
        StockTransaction transaction3 = new StockTransaction();
        transaction3.setTransactionNumber("TXN002");
        
        assertEquals(transaction1, transaction2, "相同交易编号的交易应相等");
        assertNotEquals(transaction1, transaction3, "不同交易编号的交易不应相等");
        assertEquals(transaction1.hashCode(), transaction2.hashCode(), "相等的交易应有相同的hashCode");
    }

    @Test
    @DisplayName("库存交易实体toString测试")
    void testToString() {
        transaction.setId(1L);
        String toString = transaction.toString();
        
        assertTrue(toString.contains("StockTransaction{"));
        assertTrue(toString.contains("id=1"));
        assertTrue(toString.contains("transactionNumber='TXN001'"));
        assertTrue(toString.contains("medicineId=1"));
        assertTrue(toString.contains("transactionType='入库'"));
        assertTrue(toString.contains("quantity=100"));
        assertTrue(toString.contains("status='待审核'"));
    }

    @Test
    @DisplayName("构造函数测试")
    void testConstructors() {
        StockTransaction defaultTransaction = new StockTransaction();
        assertNull(defaultTransaction.getTransactionNumber());
        assertNull(defaultTransaction.getMedicineId());
        assertNull(defaultTransaction.getTransactionType());
        assertNull(defaultTransaction.getQuantity());
        
        StockTransaction parameterizedTransaction = new StockTransaction("TXN002", 2L, "出库", -50);
        assertEquals("TXN002", parameterizedTransaction.getTransactionNumber());
        assertEquals(2L, parameterizedTransaction.getMedicineId());
        assertEquals("出库", parameterizedTransaction.getTransactionType());
        assertEquals(-50, parameterizedTransaction.getQuantity());
        assertNotNull(parameterizedTransaction.getTransactionDate());
    }

    @Test
    @DisplayName("默认值测试")
    void testDefaultValues() {
        StockTransaction newTransaction = new StockTransaction();
        assertEquals("待审核", newTransaction.getStatus());
    }
}