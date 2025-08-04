package org.me.joy.clinic.service.impl;

import org.me.joy.clinic.dto.*;
import org.me.joy.clinic.entity.Bill;
import org.me.joy.clinic.entity.BillItem;
import org.me.joy.clinic.mapper.BillMapper;
import org.me.joy.clinic.mapper.BillItemMapper;
import org.me.joy.clinic.service.FinancialReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 财务报表服务实现类
 */
@Service
public class FinancialReportServiceImpl implements FinancialReportService {

    @Autowired
    private BillMapper billMapper;
    
    @Autowired
    private BillItemMapper billItemMapper;

    @Override
    public DailyFinancialReport generateDailyReport(LocalDate date) {
        DailyFinancialReport report = new DailyFinancialReport(date);
        
        // 获取当日所有账单
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.plusDays(1).atStartOfDay();
        
        List<Bill> dailyBills = billMapper.findByCreatedAtBetween(startOfDay, endOfDay);
        
        if (dailyBills.isEmpty()) {
            return report;
        }
        
        // 计算基本统计数据
        calculateBasicStatistics(report, dailyBills);
        
        // 计算按服务类型的收入
        List<RevenueByService> revenueByServices = calculateRevenueByService(dailyBills);
        report.setRevenueByServices(revenueByServices);
        
        // 计算支付方式统计
        List<PaymentMethodSummary> paymentSummaries = calculatePaymentMethodSummary(dailyBills);
        report.setPaymentMethodSummaries(paymentSummaries);
        
        return report;
    }    
@Override
    public MonthlyFinancialReport generateMonthlyReport(int year, int month) {
        YearMonth yearMonth = YearMonth.of(year, month);
        MonthlyFinancialReport report = new MonthlyFinancialReport(yearMonth);
        
        LocalDate startDate = yearMonth.atDay(1);
        LocalDate endDate = yearMonth.atEndOfMonth();
        
        LocalDateTime startOfMonth = startDate.atStartOfDay();
        LocalDateTime endOfMonth = endDate.plusDays(1).atStartOfDay();
        
        List<Bill> monthlyBills = billMapper.findByCreatedAtBetween(startOfMonth, endOfMonth);
        
        if (monthlyBills.isEmpty()) {
            return report;
        }
        
        // 计算基本统计数据
        calculateBasicStatistics(report, monthlyBills);
        
        // 计算平均值
        int daysInMonth = yearMonth.lengthOfMonth();
        report.setAverageDailyRevenue(report.getTotalRevenue().divide(BigDecimal.valueOf(daysInMonth), 2, RoundingMode.HALF_UP));
        report.setAverageBillAmount(report.getTotalBills() > 0 ? 
            report.getTotalRevenue().divide(BigDecimal.valueOf(report.getTotalBills()), 2, RoundingMode.HALF_UP) : 
            BigDecimal.ZERO);
        
        // 生成每日汇总
        List<DailyFinancialSummary> dailySummaries = generateDailySummaries(startDate, endDate);
        report.setDailySummaries(dailySummaries);
        
        // 计算按服务类型的收入
        List<RevenueByService> revenueByServices = calculateRevenueByService(monthlyBills);
        report.setRevenueByServices(revenueByServices);
        
        // 计算支付方式统计
        List<PaymentMethodSummary> paymentSummaries = calculatePaymentMethodSummary(monthlyBills);
        report.setPaymentMethodSummaries(paymentSummaries);
        
        return report;
    }

    @Override
    public List<RevenueByService> getRevenueByService(LocalDate startDate, LocalDate endDate) {
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.plusDays(1).atStartOfDay();
        
        List<Bill> bills = billMapper.findByCreatedAtBetween(startDateTime, endDateTime);
        return calculateRevenueByService(bills);
    }

    @Override
    public List<PaymentMethodSummary> getPaymentMethodSummary(LocalDate startDate, LocalDate endDate) {
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.plusDays(1).atStartOfDay();
        
        List<Bill> bills = billMapper.findByCreatedAtBetween(startDateTime, endDateTime);
        return calculatePaymentMethodSummary(bills);
    }

    private void calculateBasicStatistics(DailyFinancialReport report, List<Bill> bills) {
        BigDecimal totalRevenue = BigDecimal.ZERO;
        BigDecimal totalRegistrationFees = BigDecimal.ZERO;
        BigDecimal totalMedicalFees = BigDecimal.ZERO;
        BigDecimal totalMedicineFees = BigDecimal.ZERO;
        BigDecimal totalRefunds = BigDecimal.ZERO;
        
        Set<Long> uniquePatients = new HashSet<>();
        
        for (Bill bill : bills) {
            totalRevenue = totalRevenue.add(bill.getTotalAmount());
            uniquePatients.add(bill.getPatientId());
            
            // 获取账单项目详情
            List<BillItem> billItems = billItemMapper.findByBillId(bill.getId());
            for (BillItem item : billItems) {
                switch (item.getItemType()) {
                    case "REGISTRATION":
                        totalRegistrationFees = totalRegistrationFees.add(item.getActualAmount());
                        break;
                    case "CONSULTATION":
                    case "MEDICAL":
                        totalMedicalFees = totalMedicalFees.add(item.getActualAmount());
                        break;
                    case "MEDICINE":
                        totalMedicineFees = totalMedicineFees.add(item.getActualAmount());
                        break;
                    case "REFUND":
                        totalRefunds = totalRefunds.add(item.getActualAmount());
                        break;
                }
            }
        }
        
        BigDecimal netRevenue = totalRevenue.subtract(totalRefunds);
        
        report.setTotalRevenue(totalRevenue);
        report.setTotalRegistrationFees(totalRegistrationFees);
        report.setTotalMedicalFees(totalMedicalFees);
        report.setTotalMedicineFees(totalMedicineFees);
        report.setTotalRefunds(totalRefunds);
        report.setNetRevenue(netRevenue);
        report.setTotalPatients(uniquePatients.size());
        report.setTotalBills(bills.size());
    }

    private void calculateBasicStatistics(MonthlyFinancialReport report, List<Bill> bills) {
        BigDecimal totalRevenue = BigDecimal.ZERO;
        BigDecimal totalRegistrationFees = BigDecimal.ZERO;
        BigDecimal totalMedicalFees = BigDecimal.ZERO;
        BigDecimal totalMedicineFees = BigDecimal.ZERO;
        BigDecimal totalRefunds = BigDecimal.ZERO;
        
        Set<Long> uniquePatients = new HashSet<>();
        
        for (Bill bill : bills) {
            totalRevenue = totalRevenue.add(bill.getTotalAmount());
            uniquePatients.add(bill.getPatientId());
            
            // 获取账单项目详情
            List<BillItem> billItems = billItemMapper.findByBillId(bill.getId());
            for (BillItem item : billItems) {
                switch (item.getItemType()) {
                    case "REGISTRATION":
                        totalRegistrationFees = totalRegistrationFees.add(item.getActualAmount());
                        break;
                    case "CONSULTATION":
                    case "MEDICAL":
                        totalMedicalFees = totalMedicalFees.add(item.getActualAmount());
                        break;
                    case "MEDICINE":
                        totalMedicineFees = totalMedicineFees.add(item.getActualAmount());
                        break;
                    case "REFUND":
                        totalRefunds = totalRefunds.add(item.getActualAmount());
                        break;
                }
            }
        }
        
        BigDecimal netRevenue = totalRevenue.subtract(totalRefunds);
        
        report.setTotalRevenue(totalRevenue);
        report.setTotalRegistrationFees(totalRegistrationFees);
        report.setTotalMedicalFees(totalMedicalFees);
        report.setTotalMedicineFees(totalMedicineFees);
        report.setTotalRefunds(totalRefunds);
        report.setNetRevenue(netRevenue);
        report.setTotalPatients(uniquePatients.size());
        report.setTotalBills(bills.size());
    } 
   private List<RevenueByService> calculateRevenueByService(List<Bill> bills) {
        Map<String, RevenueByService> serviceRevenueMap = new HashMap<>();
        
        for (Bill bill : bills) {
            List<BillItem> billItems = billItemMapper.findByBillId(bill.getId());
            for (BillItem item : billItems) {
                String serviceKey = item.getItemType() + "_" + item.getItemName();
                
                RevenueByService revenue = serviceRevenueMap.getOrDefault(serviceKey, 
                    new RevenueByService(item.getItemType(), item.getItemName(), BigDecimal.ZERO, 0));
                
                revenue.setRevenue(revenue.getRevenue().add(item.getActualAmount()));
                revenue.setCount(revenue.getCount() + 1);
                revenue.setAverageAmount(revenue.getRevenue().divide(BigDecimal.valueOf(revenue.getCount()), 2, RoundingMode.HALF_UP));
                
                serviceRevenueMap.put(serviceKey, revenue);
            }
        }
        
        List<RevenueByService> result = new ArrayList<>(serviceRevenueMap.values());
        
        // 计算百分比
        BigDecimal totalRevenue = result.stream()
            .map(RevenueByService::getRevenue)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        if (totalRevenue.compareTo(BigDecimal.ZERO) > 0) {
            for (RevenueByService revenue : result) {
                BigDecimal percentage = revenue.getRevenue()
                    .divide(totalRevenue, 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100));
                revenue.setPercentage(percentage);
            }
        }
        
        // 按收入降序排序
        result.sort((a, b) -> b.getRevenue().compareTo(a.getRevenue()));
        
        return result;
    }

    private List<PaymentMethodSummary> calculatePaymentMethodSummary(List<Bill> bills) {
        Map<String, PaymentMethodSummary> paymentMethodMap = new HashMap<>();
        
        for (Bill bill : bills) {
            // 假设每个账单都有支付记录，这里简化处理
            String paymentMethod = "CASH"; // 默认现金支付，实际应该从Payment表获取
            
            PaymentMethodSummary summary = paymentMethodMap.getOrDefault(paymentMethod,
                new PaymentMethodSummary(paymentMethod, BigDecimal.ZERO, 0));
            
            summary.setTotalAmount(summary.getTotalAmount().add(bill.getTotalAmount()));
            summary.setTransactionCount(summary.getTransactionCount() + 1);
            summary.setAverageAmount(summary.getTotalAmount().divide(BigDecimal.valueOf(summary.getTransactionCount()), 2, RoundingMode.HALF_UP));
            
            paymentMethodMap.put(paymentMethod, summary);
        }
        
        List<PaymentMethodSummary> result = new ArrayList<>(paymentMethodMap.values());
        
        // 计算百分比
        BigDecimal totalAmount = result.stream()
            .map(PaymentMethodSummary::getTotalAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        if (totalAmount.compareTo(BigDecimal.ZERO) > 0) {
            for (PaymentMethodSummary summary : result) {
                BigDecimal percentage = summary.getTotalAmount()
                    .divide(totalAmount, 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100));
                summary.setPercentage(percentage);
            }
        }
        
        // 按金额降序排序
        result.sort((a, b) -> b.getTotalAmount().compareTo(a.getTotalAmount()));
        
        return result;
    }

    private List<DailyFinancialSummary> generateDailySummaries(LocalDate startDate, LocalDate endDate) {
        List<DailyFinancialSummary> summaries = new ArrayList<>();
        
        LocalDate currentDate = startDate;
        while (!currentDate.isAfter(endDate)) {
            DailyFinancialReport dailyReport = generateDailyReport(currentDate);
            
            DailyFinancialSummary summary = new DailyFinancialSummary(
                currentDate,
                dailyReport.getTotalRevenue(),
                dailyReport.getNetRevenue(),
                dailyReport.getTotalPatients(),
                dailyReport.getTotalBills()
            );
            
            summaries.add(summary);
            currentDate = currentDate.plusDays(1);
        }
        
        return summaries;
    }
}