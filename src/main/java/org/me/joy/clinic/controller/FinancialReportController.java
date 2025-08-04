package org.me.joy.clinic.controller;

import org.me.joy.clinic.dto.DailyFinancialReport;
import org.me.joy.clinic.dto.MonthlyFinancialReport;
import org.me.joy.clinic.dto.PaymentMethodSummary;
import org.me.joy.clinic.dto.RevenueByService;
import org.me.joy.clinic.security.RequiresPermission;
import org.me.joy.clinic.service.FinancialReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * 财务报表控制器
 */
@RestController
@RequestMapping("/api/financial-reports")
@CrossOrigin(origins = "*")
public class FinancialReportController {

    @Autowired
    private FinancialReportService financialReportService;

    /**
     * 生成日财务报表
     */
    @GetMapping("/daily")
    @RequiresPermission("FINANCIAL_REPORT_READ")
    public ResponseEntity<DailyFinancialReport> generateDailyReport(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        
        DailyFinancialReport report = financialReportService.generateDailyReport(date);
        return ResponseEntity.ok(report);
    }

    /**
     * 生成月财务报表
     */
    @GetMapping("/monthly")
    @RequiresPermission("FINANCIAL_REPORT_READ")
    public ResponseEntity<MonthlyFinancialReport> generateMonthlyReport(
            @RequestParam int year,
            @RequestParam int month) {
        
        MonthlyFinancialReport report = financialReportService.generateMonthlyReport(year, month);
        return ResponseEntity.ok(report);
    }

    /**
     * 获取按服务类型统计的收入
     */
    @GetMapping("/revenue-by-service")
    @RequiresPermission("FINANCIAL_REPORT_READ")
    public ResponseEntity<List<RevenueByService>> getRevenueByService(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        List<RevenueByService> revenueByServices = financialReportService.getRevenueByService(startDate, endDate);
        return ResponseEntity.ok(revenueByServices);
    }

    /**
     * 获取支付方式统计
     */
    @GetMapping("/payment-method-summary")
    @RequiresPermission("FINANCIAL_REPORT_READ")
    public ResponseEntity<List<PaymentMethodSummary>> getPaymentMethodSummary(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        List<PaymentMethodSummary> paymentSummaries = financialReportService.getPaymentMethodSummary(startDate, endDate);
        return ResponseEntity.ok(paymentSummaries);
    }

    /**
     * 获取当日财务报表
     */
    @GetMapping("/today")
    @RequiresPermission("FINANCIAL_REPORT_READ")
    public ResponseEntity<DailyFinancialReport> getTodayReport() {
        DailyFinancialReport report = financialReportService.generateDailyReport(LocalDate.now());
        return ResponseEntity.ok(report);
    }

    /**
     * 获取当月财务报表
     */
    @GetMapping("/current-month")
    @RequiresPermission("FINANCIAL_REPORT_READ")
    public ResponseEntity<MonthlyFinancialReport> getCurrentMonthReport() {
        LocalDate now = LocalDate.now();
        MonthlyFinancialReport report = financialReportService.generateMonthlyReport(now.getYear(), now.getMonthValue());
        return ResponseEntity.ok(report);
    }
}