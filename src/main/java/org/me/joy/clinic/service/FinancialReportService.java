package org.me.joy.clinic.service;

import org.me.joy.clinic.dto.DailyFinancialReport;
import org.me.joy.clinic.dto.MonthlyFinancialReport;
import org.me.joy.clinic.dto.PaymentMethodSummary;
import org.me.joy.clinic.dto.RevenueByService;

import java.time.LocalDate;
import java.util.List;

/**
 * 财务报表服务接口
 */
public interface FinancialReportService {
    
    /**
     * 生成日财务报表
     * @param date 报表日期
     * @return 日财务报表
     */
    DailyFinancialReport generateDailyReport(LocalDate date);
    
    /**
     * 生成月财务报表
     * @param year 年份
     * @param month 月份
     * @return 月财务报表
     */
    MonthlyFinancialReport generateMonthlyReport(int year, int month);
    
    /**
     * 获取按服务类型统计的收入
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 按服务类型统计的收入列表
     */
    List<RevenueByService> getRevenueByService(LocalDate startDate, LocalDate endDate);
    
    /**
     * 获取支付方式统计
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 支付方式统计列表
     */
    List<PaymentMethodSummary> getPaymentMethodSummary(LocalDate startDate, LocalDate endDate);
}