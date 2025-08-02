package org.me.joy.clinic.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.me.joy.clinic.dto.AddBillItemRequest;
import org.me.joy.clinic.dto.CreateBillRequest;
import org.me.joy.clinic.entity.Bill;
import org.me.joy.clinic.entity.BillItem;
import org.me.joy.clinic.entity.Registration;
import org.me.joy.clinic.exception.BusinessException;
import org.me.joy.clinic.mapper.BillItemMapper;
import org.me.joy.clinic.mapper.BillMapper;
import org.me.joy.clinic.mapper.RegistrationMapper;
import org.me.joy.clinic.service.BillingService;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * 账单管理服务测试类
 */
@ExtendWith(MockitoExtension.class)
class BillingServiceImplTest {

    @Mock
    private BillMapper billMapper;

    @Mock
    private BillItemMapper billItemMapper;

    @Mock
    private RegistrationMapper registrationMapper;

    @InjectMocks
    private BillingServiceImpl billingService;

    private CreateBillRequest createBillRequest;
    private AddBillItemRequest addBillItemRequest;
    private Bill testBill;
    private BillItem testBillItem;
    private Registration testRegistration;

    @BeforeEach
    void setUp() {
        // 准备测试数据
        createBillRequest = new CreateBillRequest();
        createBillRequest.setPatientId(1L);
        createBillRequest.setRegistrationId(100L);
        createBillRequest.setNotes("测试账单");

        addBillItemRequest = new AddBillItemRequest();
        addBillItemRequest.setItemType("CONSULTATION");
        addBillItemRequest.setItemName("专家门诊费");
        addBillItemRequest.setItemCode("CONS001");
        addBillItemRequest.setUnitPrice(new BigDecimal("50.00"));
        addBillItemRequest.setQuantity(1);
        addBillItemRequest.setSpecification("次");

        testBill = new Bill();
        testBill.setId(1L);
        testBill.setPatientId(1L);
        testBill.setRegistrationId(100L);
        testBill.setBillNumber("BILL202401010001");
        testBill.setTotalAmount(new BigDecimal("50.00"));
        testBill.setPaidAmount(BigDecimal.ZERO);
        testBill.setStatus("PENDING");

        testBillItem = new BillItem();
        testBillItem.setId(1L);
        testBillItem.setBillId(1L);
        testBillItem.setItemType("CONSULTATION");
        testBillItem.setItemName("专家门诊费");
        testBillItem.setUnitPrice(new BigDecimal("50.00"));
        testBillItem.setQuantity(1);
        testBillItem.setSubtotal(new BigDecimal("50.00"));
        testBillItem.setDiscount(BigDecimal.ZERO);
        testBillItem.setActualAmount(new BigDecimal("50.00"));

        testRegistration = new Registration();
        testRegistration.setId(100L);
        testRegistration.setPatientId(1L);
        testRegistration.setRegistrationFee(new BigDecimal("10.00"));
    }

    @Test
    void testCreateBill_Success() {
        // Given
        when(registrationMapper.selectById(100L)).thenReturn(testRegistration);
        when(billMapper.findByRegistrationId(100L)).thenReturn(null);
        when(billMapper.insert(any(Bill.class))).thenAnswer(invocation -> {
            Bill bill = invocation.getArgument(0);
            bill.setId(1L); // Set ID after insert
            return 1;
        });

        // When
        Bill result = billingService.createBill(createBillRequest);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getPatientId());
        assertEquals(100L, result.getRegistrationId());
        assertEquals("测试账单", result.getNotes());
        verify(billMapper).insert(any(Bill.class));
    }

    @Test
    void testCreateBill_PatientIdNull() {
        // Given
        createBillRequest.setPatientId(null);

        // When & Then
        BusinessException exception = assertThrows(BusinessException.class, 
            () -> billingService.createBill(createBillRequest));
        assertEquals("患者ID不能为空", exception.getMessage());
    }

    @Test
    void testCreateBill_RegistrationNotFound() {
        // Given
        when(registrationMapper.selectById(100L)).thenReturn(null);

        // When & Then
        BusinessException exception = assertThrows(BusinessException.class, 
            () -> billingService.createBill(createBillRequest));
        assertEquals("挂号记录不存在", exception.getMessage());
    }

    @Test
    void testCreateBill_BillAlreadyExists() {
        // Given
        when(registrationMapper.selectById(100L)).thenReturn(testRegistration);
        when(billMapper.findByRegistrationId(100L)).thenReturn(testBill);

        // When & Then
        BusinessException exception = assertThrows(BusinessException.class, 
            () -> billingService.createBill(createBillRequest));
        assertEquals("该挂号记录已存在账单", exception.getMessage());
    }

    @Test
    void testCalculateBill_Success() {
        // Given
        when(registrationMapper.selectById(100L)).thenReturn(testRegistration);
        when(billMapper.findByRegistrationId(100L)).thenReturn(null);
        when(billMapper.insert(any(Bill.class))).thenAnswer(invocation -> {
            Bill bill = invocation.getArgument(0);
            bill.setId(1L); // Set ID after insert
            return 1;
        });
        when(billMapper.selectById(1L)).thenReturn(testBill);
        when(billItemMapper.insert(any(BillItem.class))).thenReturn(1);
        when(billItemMapper.calculateTotalAmount(1L)).thenReturn(new BigDecimal("60.00"));

        // When
        Bill result = billingService.calculateBill(100L);

        // Then
        assertNotNull(result);
        verify(billMapper).insert(any(Bill.class));
        verify(billItemMapper).insert(any(BillItem.class)); // 挂号费项目
    }

    @Test
    void testCalculateBill_ExistingBill() {
        // Given
        when(registrationMapper.selectById(100L)).thenReturn(testRegistration);
        when(billMapper.findByRegistrationId(100L)).thenReturn(testBill);

        // When
        Bill result = billingService.calculateBill(100L);

        // Then
        assertNotNull(result);
        assertEquals(testBill, result);
        verify(billMapper, never()).insert(any(Bill.class));
    }

    @Test
    void testAddBillItem_Success() {
        // Given
        when(billMapper.selectById(1L)).thenReturn(testBill);
        when(billItemMapper.insert(any(BillItem.class))).thenReturn(1);
        when(billItemMapper.calculateTotalAmount(1L)).thenReturn(new BigDecimal("100.00"));

        // When
        BillItem result = billingService.addBillItem(1L, addBillItemRequest);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getBillId());
        assertEquals("CONSULTATION", result.getItemType());
        assertEquals("专家门诊费", result.getItemName());
        assertEquals(new BigDecimal("50.00"), result.getUnitPrice());
        verify(billItemMapper).insert(any(BillItem.class));
        verify(billMapper).updateById(any(Bill.class)); // 更新总金额
    }

    @Test
    void testAddBillItem_BillNotFound() {
        // Given
        when(billMapper.selectById(1L)).thenReturn(null);

        // When & Then
        BusinessException exception = assertThrows(BusinessException.class, 
            () -> billingService.addBillItem(1L, addBillItemRequest));
        assertEquals("账单不存在", exception.getMessage());
    }

    @Test
    void testAddBillItem_CancelledBill() {
        // Given
        testBill.setStatus("CANCELLED");
        when(billMapper.selectById(1L)).thenReturn(testBill);

        // When & Then
        BusinessException exception = assertThrows(BusinessException.class, 
            () -> billingService.addBillItem(1L, addBillItemRequest));
        assertEquals("已取消的账单不能添加项目", exception.getMessage());
    }

    @Test
    void testGetBillById_Success() {
        // Given
        when(billMapper.selectById(1L)).thenReturn(testBill);

        // When
        Bill result = billingService.getBillById(1L);

        // Then
        assertNotNull(result);
        assertEquals(testBill, result);
    }

    @Test
    void testGetBillById_NotFound() {
        // Given
        when(billMapper.selectById(1L)).thenReturn(null);

        // When & Then
        BusinessException exception = assertThrows(BusinessException.class, 
            () -> billingService.getBillById(1L));
        assertEquals("账单不存在", exception.getMessage());
    }

    @Test
    void testGetBillByNumber_Success() {
        // Given
        when(billMapper.findByBillNumber("BILL202401010001")).thenReturn(testBill);

        // When
        Bill result = billingService.getBillByNumber("BILL202401010001");

        // Then
        assertNotNull(result);
        assertEquals(testBill, result);
    }

    @Test
    void testGetPatientBills_Success() {
        // Given
        List<Bill> bills = Arrays.asList(testBill);
        when(billMapper.findByPatientId(1L)).thenReturn(bills);

        // When
        List<Bill> result = billingService.getPatientBills(1L);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testBill, result.get(0));
    }

    @Test
    void testGetBillItems_Success() {
        // Given
        List<BillItem> items = Arrays.asList(testBillItem);
        when(billItemMapper.findByBillId(1L)).thenReturn(items);

        // When
        List<BillItem> result = billingService.getBillItems(1L);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testBillItem, result.get(0));
    }

    @Test
    void testUpdateBillTotalAmount_Success() {
        // Given
        when(billMapper.selectById(1L)).thenReturn(testBill);
        when(billItemMapper.calculateTotalAmount(1L)).thenReturn(new BigDecimal("100.00"));

        // When
        billingService.updateBillTotalAmount(1L);

        // Then
        verify(billMapper).updateById(any(Bill.class));
    }

    @Test
    void testUpdatePaymentStatus_Success() {
        // Given
        when(billMapper.selectById(1L)).thenReturn(testBill);

        // When
        billingService.updatePaymentStatus(1L, new BigDecimal("50.00"));

        // Then
        verify(billMapper).updateById(any(Bill.class));
    }

    @Test
    void testUpdatePaymentStatus_CancelledBill() {
        // Given
        testBill.setStatus("CANCELLED");
        when(billMapper.selectById(1L)).thenReturn(testBill);

        // When & Then
        BusinessException exception = assertThrows(BusinessException.class, 
            () -> billingService.updatePaymentStatus(1L, new BigDecimal("50.00")));
        assertEquals("已取消的账单不能更新支付状态", exception.getMessage());
    }

    @Test
    void testCancelBill_Success() {
        // Given
        when(billMapper.selectById(1L)).thenReturn(testBill);

        // When
        billingService.cancelBill(1L, "测试取消");

        // Then
        verify(billMapper).updateById(any(Bill.class));
    }

    @Test
    void testCancelBill_PaidBill() {
        // Given
        testBill.setStatus("PAID");
        when(billMapper.selectById(1L)).thenReturn(testBill);

        // When & Then
        BusinessException exception = assertThrows(BusinessException.class, 
            () -> billingService.cancelBill(1L, "测试取消"));
        assertEquals("已支付的账单不能取消", exception.getMessage());
    }

    @Test
    void testGetBillsByStatus_Success() {
        // Given
        List<Bill> bills = Arrays.asList(testBill);
        when(billMapper.findByStatus("PENDING")).thenReturn(bills);

        // When
        List<Bill> result = billingService.getBillsByStatus("PENDING");

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testBill, result.get(0));
    }

    @Test
    void testGetBillsByDateRange_Success() {
        // Given
        LocalDate startDate = LocalDate.of(2024, 1, 1);
        LocalDate endDate = LocalDate.of(2024, 1, 31);
        List<Bill> bills = Arrays.asList(testBill);
        when(billMapper.findByDateRange(startDate, endDate)).thenReturn(bills);

        // When
        List<Bill> result = billingService.getBillsByDateRange(startDate, endDate);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testBill, result.get(0));
    }

    @Test
    void testGetBillsByDateRange_InvalidRange() {
        // Given
        LocalDate startDate = LocalDate.of(2024, 1, 31);
        LocalDate endDate = LocalDate.of(2024, 1, 1);

        // When & Then
        BusinessException exception = assertThrows(BusinessException.class, 
            () -> billingService.getBillsByDateRange(startDate, endDate));
        assertEquals("开始日期不能晚于结束日期", exception.getMessage());
    }

    @Test
    void testGetTotalRevenueByDate_Success() {
        // Given
        LocalDate date = LocalDate.of(2024, 1, 1);
        when(billMapper.getTotalAmountByDate(date)).thenReturn(new BigDecimal("1000.00"));

        // When
        BigDecimal result = billingService.getTotalRevenueByDate(date);

        // Then
        assertNotNull(result);
        assertEquals(new BigDecimal("1000.00"), result);
    }

    @Test
    void testGetTotalRevenueByDate_NoRevenue() {
        // Given
        LocalDate date = LocalDate.of(2024, 1, 1);
        when(billMapper.getTotalAmountByDate(date)).thenReturn(null);

        // When
        BigDecimal result = billingService.getTotalRevenueByDate(date);

        // Then
        assertNotNull(result);
        assertEquals(BigDecimal.ZERO, result);
    }

    @Test
    void testRemoveBillItem_Success() {
        // Given
        when(billItemMapper.selectById(1L)).thenReturn(testBillItem);
        when(billMapper.selectById(1L)).thenReturn(testBill);
        when(billItemMapper.calculateTotalAmount(1L)).thenReturn(new BigDecimal("0.00"));

        // When
        billingService.removeBillItem(1L);

        // Then
        verify(billItemMapper).deleteById(1L);
        verify(billMapper).updateById(any(Bill.class)); // 更新总金额
    }

    @Test
    void testUpdateBillItem_Success() {
        // Given
        when(billItemMapper.selectById(1L)).thenReturn(testBillItem);
        when(billMapper.selectById(1L)).thenReturn(testBill);
        when(billItemMapper.calculateTotalAmount(1L)).thenReturn(new BigDecimal("75.00"));

        addBillItemRequest.setUnitPrice(new BigDecimal("75.00"));

        // When
        BillItem result = billingService.updateBillItem(1L, addBillItemRequest);

        // Then
        assertNotNull(result);
        verify(billItemMapper).updateById(any(BillItem.class));
        verify(billMapper).updateById(any(Bill.class)); // 更新总金额
    }
}