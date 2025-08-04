package org.me.joy.clinic.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.me.joy.clinic.dto.*;
import org.me.joy.clinic.entity.Bill;
import org.me.joy.clinic.entity.BillItem;
import org.me.joy.clinic.mapper.BillItemMapper;
import org.me.joy.clinic.mapper.BillMapper;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

/**
 * 财务报表服务实现类测试
 */
@ExtendWith(MockitoExtension.class)
class FinancialReportServiceImplTest {

    @Mock
    private BillMapper billMapper;

    @Mock
    private BillItemMapper billItemMapper;

    @InjectMocks
    private FinancialReportServiceImpl financialReportService;

    private Bill testBill1;
    private Bill testBill2;
    private List<BillItem> testBillItems1;
    private List<BillItem> testBillItems2;

    @BeforeEach
    void setUp() {
        // 创建测试账单数据
        testBill1 = new Bill();
        testBill1.setId(1L);
        testBill1.setPatientId(1L);
        testBill1.setBillNumber("BILL001");
        testBill1.setTotalAmount(new BigDecimal("150.00"));
        testBill1.setCreatedAt(LocalDateTime.now());

        testBill2 = new Bill();
        testBill2.setId(2L);
        testBill2.setPatientId(2L);
        testBill2.setBillNumber("BILL002");
        testBill2.setTotalAmount(new BigDecimal("200.00"));
        testBill2.setCreatedAt(LocalDateTime.now());

        // 创建测试账单项目数据
        testBillItems1 = Arrays.asList(
            createBillItem(1L, 1L, "REGISTRATION", "挂号费", new BigDecimal("10.00")),
            createBillItem(2L, 1L, "CONSULTATION", "诊疗费", new BigDecimal("50.00")),
            createBillItem(3L, 1L, "MEDICINE", "药品费", new BigDecimal("90.00"))
        );

        testBillItems2 = Arrays.asList(
            createBillItem(4L, 2L, "REGISTRATION", "挂号费", new BigDecimal("10.00")),
            createBillItem(5L, 2L, "MEDICAL", "检查费", new BigDecimal("100.00")),
            createBillItem(6L, 2L, "MEDICINE", "药品费", new BigDecimal("90.00"))
        );
    }

    private BillItem createBillItem(Long id, Long billId, String itemType, String itemName, BigDecimal amount) {
        BillItem item = new BillItem();
        item.setId(id);
        item.setBillId(billId);
        item.setItemType(itemType);
        item.setItemName(itemName);
        item.setActualAmount(amount);
        return item;
    }

    @Test
    void testGenerateDailyReportWithNoBills() {
        // Given
        LocalDate testDate = LocalDate.now();
        when(billMapper.findByCreatedAtBetween(any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(Collections.emptyList());

        // When
        DailyFinancialReport report = financialReportService.generateDailyReport(testDate);

        // Then
        assertNotNull(report);
        assertEquals(testDate, report.getReportDate());
        assertEquals(0, report.getTotalPatients());
        assertEquals(0, report.getTotalBills());
        assertEquals(BigDecimal.ZERO, report.getTotalRevenue());
        assertEquals(BigDecimal.ZERO, report.getNetRevenue());
    }

    @Test
    void testGenerateDailyReportWithBills() {
        // Given
        LocalDate testDate = LocalDate.now();
        List<Bill> bills = Arrays.asList(testBill1, testBill2);
        
        when(billMapper.findByCreatedAtBetween(any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(bills);
        when(billItemMapper.findByBillId(1L)).thenReturn(testBillItems1);
        when(billItemMapper.findByBillId(2L)).thenReturn(testBillItems2);

        // When
        DailyFinancialReport report = financialReportService.generateDailyReport(testDate);

        // Then
        assertNotNull(report);
        assertEquals(testDate, report.getReportDate());
        assertEquals(2, report.getTotalPatients()); // 2个不同患者
        assertEquals(2, report.getTotalBills());
        assertEquals(new BigDecimal("350.00"), report.getTotalRevenue()); // 150 + 200
        assertEquals(new BigDecimal("20.00"), report.getTotalRegistrationFees()); // 10 + 10
        assertEquals(new BigDecimal("150.00"), report.getTotalMedicalFees()); // 50 + 100
        assertEquals(new BigDecimal("180.00"), report.getTotalMedicineFees()); // 90 + 90
        assertNotNull(report.getRevenueByServices());
        assertNotNull(report.getPaymentMethodSummaries());
    }

    @Test
    void testGenerateMonthlyReport() {
        // Given
        int year = 2024;
        int month = 1;
        List<Bill> bills = Arrays.asList(testBill1, testBill2);
        
        when(billMapper.findByCreatedAtBetween(any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(bills);
        when(billItemMapper.findByBillId(1L)).thenReturn(testBillItems1);
        when(billItemMapper.findByBillId(2L)).thenReturn(testBillItems2);

        // When
        MonthlyFinancialReport report = financialReportService.generateMonthlyReport(year, month);

        // Then
        assertNotNull(report);
        assertEquals(year, report.getReportMonth().getYear());
        assertEquals(month, report.getReportMonth().getMonthValue());
        assertEquals(2, report.getTotalPatients());
        assertEquals(2, report.getTotalBills());
        assertEquals(new BigDecimal("350.00"), report.getTotalRevenue());
        assertTrue(report.getAverageDailyRevenue().compareTo(BigDecimal.ZERO) > 0);
        assertTrue(report.getAverageBillAmount().compareTo(BigDecimal.ZERO) > 0);
        assertNotNull(report.getDailySummaries());
        assertNotNull(report.getRevenueByServices());
        assertNotNull(report.getPaymentMethodSummaries());
    }

    @Test
    void testGetRevenueByService() {
        // Given
        LocalDate startDate = LocalDate.now().minusDays(1);
        LocalDate endDate = LocalDate.now();
        List<Bill> bills = Arrays.asList(testBill1, testBill2);
        
        when(billMapper.findByCreatedAtBetween(any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(bills);
        when(billItemMapper.findByBillId(1L)).thenReturn(testBillItems1);
        when(billItemMapper.findByBillId(2L)).thenReturn(testBillItems2);

        // When
        List<RevenueByService> revenueByServices = financialReportService.getRevenueByService(startDate, endDate);

        // Then
        assertNotNull(revenueByServices);
        assertFalse(revenueByServices.isEmpty());
        
        // 验证收入按降序排列
        for (int i = 0; i < revenueByServices.size() - 1; i++) {
            assertTrue(revenueByServices.get(i).getRevenue()
                    .compareTo(revenueByServices.get(i + 1).getRevenue()) >= 0);
        }
        
        // 验证百分比计算
        BigDecimal totalPercentage = revenueByServices.stream()
                .map(RevenueByService::getPercentage)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        assertEquals(0, totalPercentage.compareTo(new BigDecimal("100.0000")));
    }

    @Test
    void testGetPaymentMethodSummary() {
        // Given
        LocalDate startDate = LocalDate.now().minusDays(1);
        LocalDate endDate = LocalDate.now();
        List<Bill> bills = Arrays.asList(testBill1, testBill2);
        
        when(billMapper.findByCreatedAtBetween(any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(bills);

        // When
        List<PaymentMethodSummary> paymentSummaries = financialReportService.getPaymentMethodSummary(startDate, endDate);

        // Then
        assertNotNull(paymentSummaries);
        assertFalse(paymentSummaries.isEmpty());
        
        // 验证支付方式统计
        PaymentMethodSummary cashSummary = paymentSummaries.stream()
                .filter(summary -> "CASH".equals(summary.getPaymentMethod()))
                .findFirst()
                .orElse(null);
        
        assertNotNull(cashSummary);
        assertEquals(new BigDecimal("350.00"), cashSummary.getTotalAmount());
        assertEquals(2, cashSummary.getTransactionCount());
        assertEquals(new BigDecimal("175.00"), cashSummary.getAverageAmount());
    }

    @Test
    void testGenerateDailyReportWithRefunds() {
        // Given
        LocalDate testDate = LocalDate.now();
        List<Bill> bills = Arrays.asList(testBill1);
        
        // 添加退款项目
        List<BillItem> billItemsWithRefund = Arrays.asList(
            createBillItem(1L, 1L, "REGISTRATION", "挂号费", new BigDecimal("10.00")),
            createBillItem(2L, 1L, "REFUND", "退款", new BigDecimal("5.00"))
        );
        
        when(billMapper.findByCreatedAtBetween(any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(bills);
        when(billItemMapper.findByBillId(1L)).thenReturn(billItemsWithRefund);

        // When
        DailyFinancialReport report = financialReportService.generateDailyReport(testDate);

        // Then
        assertNotNull(report);
        assertEquals(new BigDecimal("150.00"), report.getTotalRevenue());
        assertEquals(new BigDecimal("5.00"), report.getTotalRefunds());
        assertEquals(new BigDecimal("145.00"), report.getNetRevenue()); // 150 - 5
    }

    @Test
    void testGenerateMonthlyReportWithEmptyBills() {
        // Given
        int year = 2024;
        int month = 1;
        
        when(billMapper.findByCreatedAtBetween(any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(Collections.emptyList());

        // When
        MonthlyFinancialReport report = financialReportService.generateMonthlyReport(year, month);

        // Then
        assertNotNull(report);
        assertEquals(year, report.getReportMonth().getYear());
        assertEquals(month, report.getReportMonth().getMonthValue());
        assertEquals(0, report.getTotalPatients());
        assertEquals(0, report.getTotalBills());
        assertEquals(BigDecimal.ZERO, report.getTotalRevenue());
        assertEquals(BigDecimal.ZERO, report.getAverageDailyRevenue());
        assertEquals(BigDecimal.ZERO, report.getAverageBillAmount());
    }

    @Test
    void testRevenueByServiceCalculation() {
        // Given
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = LocalDate.now();
        List<Bill> bills = Arrays.asList(testBill1);
        
        when(billMapper.findByCreatedAtBetween(any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(bills);
        when(billItemMapper.findByBillId(1L)).thenReturn(testBillItems1);

        // When
        List<RevenueByService> revenueByServices = financialReportService.getRevenueByService(startDate, endDate);

        // Then
        assertNotNull(revenueByServices);
        assertEquals(3, revenueByServices.size()); // REGISTRATION, CONSULTATION, MEDICINE
        
        // 验证药品费用最高（90.00）
        RevenueByService topRevenue = revenueByServices.get(0);
        assertEquals("MEDICINE", topRevenue.getServiceType());
        assertEquals(new BigDecimal("90.00"), topRevenue.getRevenue());
        assertEquals(1, topRevenue.getCount());
        assertEquals(new BigDecimal("90.00"), topRevenue.getAverageAmount());
    }
}