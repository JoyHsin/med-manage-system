package org.me.joy.clinic.service.impl;

import org.me.joy.clinic.dto.DoctorPerformanceReport;
import org.me.joy.clinic.dto.PatientVisitAnalytics;
import org.me.joy.clinic.dto.PopularService;
import org.me.joy.clinic.dto.WaitTimeAnalytics;
import org.me.joy.clinic.mapper.AppointmentMapper;
import org.me.joy.clinic.mapper.BillMapper;
import org.me.joy.clinic.mapper.MedicalRecordMapper;
import org.me.joy.clinic.mapper.PatientQueueMapper;
import org.me.joy.clinic.mapper.PrescriptionMapper;
import org.me.joy.clinic.mapper.RegistrationMapper;
import org.me.joy.clinic.mapper.StaffMapper;
import org.me.joy.clinic.service.OperationalAnalyticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 运营分析服务实现类
 */
@Service
public class OperationalAnalyticsServiceImpl implements OperationalAnalyticsService {

    @Autowired
    private RegistrationMapper registrationMapper;

    @Autowired
    private PatientQueueMapper patientQueueMapper;

    @Autowired
    private MedicalRecordMapper medicalRecordMapper;

    @Autowired
    private StaffMapper staffMapper;

    @Autowired
    private BillMapper billMapper;

    @Autowired
    private PrescriptionMapper prescriptionMapper;

    @Autowired
    private AppointmentMapper appointmentMapper;

    @Override
    public PatientVisitAnalytics getPatientVisitAnalytics(LocalDate startDate, LocalDate endDate) {
        // 获取总就诊量
        Long totalVisits = registrationMapper.countRegistrationsByDateRange(startDate, endDate);
        
        // 获取新患者数量（首次就诊）
        Long newPatients = registrationMapper.countNewPatientsByDateRange(startDate, endDate);
        
        // 计算回访患者数量
        Long returningPatients = totalVisits - newPatients;
        
        // 计算平均每日就诊量
        long daysBetween = ChronoUnit.DAYS.between(startDate, endDate) + 1;
        Double averageVisitsPerDay = totalVisits.doubleValue() / daysBetween;
        
        // 获取高峰时段数据
        Map<String, Long> hourlyVisits = getHourlyVisitStatistics(startDate, endDate);
        String peakHour = "09:00-10:00"; // 默认值
        Long peakHourVisits = 0L;
        
        for (Map.Entry<String, Long> entry : hourlyVisits.entrySet()) {
            if (entry.getValue() > peakHourVisits) {
                peakHourVisits = entry.getValue();
                peakHour = entry.getKey();
            }
        }
        
        return new PatientVisitAnalytics(
            startDate,
            totalVisits,
            newPatients,
            returningPatients,
            averageVisitsPerDay,
            peakHourVisits,
            peakHour
        );
    }

    @Override
    public List<PopularService> getPopularServices(LocalDate startDate, LocalDate endDate) {
        List<PopularService> popularServices = new ArrayList<>();
        
        // 获取各科室就诊量统计
        List<Map<String, Object>> departmentStatsRaw = registrationMapper.getDepartmentVisitStatistics(startDate, endDate);
        Map<String, Long> departmentStats = new HashMap<>();
        for (Map<String, Object> entry : departmentStatsRaw) {
            String department = (String) entry.get("department");
            Long count = ((Number) entry.get("count")).longValue();
            departmentStats.put(department, count);
        }
        Long totalVisits = departmentStats.values().stream().mapToLong(Long::longValue).sum();
        
        for (Map.Entry<String, Long> entry : departmentStats.entrySet()) {
            String department = entry.getKey();
            Long visitCount = entry.getValue();
            Double percentage = totalVisits > 0 ? (visitCount.doubleValue() / totalVisits * 100) : 0.0;
            
            popularServices.add(new PopularService(
                department + "科",
                department.toUpperCase(),
                visitCount,
                percentage,
                department
            ));
        }
        
        // 按就诊量降序排序
        return popularServices.stream()
            .sorted((a, b) -> Long.compare(b.getVisitCount(), a.getVisitCount()))
            .collect(Collectors.toList());
    }

    @Override
    public DoctorPerformanceReport getDoctorPerformanceReport(Long doctorId, LocalDate startDate, LocalDate endDate) {
        // 获取医生基本信息
        var staff = staffMapper.selectById(doctorId);
        if (staff == null) {
            return null;
        }
        
        // 获取医生接诊患者数量
        Long totalPatients = medicalRecordMapper.countPatientsByDoctorAndDateRange(doctorId, startDate, endDate);
        
        // 获取完成的咨询数量
        Long completedConsultations = medicalRecordMapper.countCompletedConsultationsByDoctorAndDateRange(doctorId, startDate, endDate);
        
        // 计算平均咨询时间（分钟）
        Double averageConsultationTime = calculateAverageConsultationTime(doctorId, startDate, endDate);
        
        // 获取患者满意度评分（模拟数据，实际应从满意度调查表获取）
        Double patientSatisfactionScore = 4.5; // 默认评分
        
        // 获取开具处方数量
        Long prescriptionsIssued = prescriptionMapper.countPrescriptionsByDoctorAndDateRange(doctorId, startDate, endDate);
        
        // 计算产生的收入
        Double revenueGenerated = billMapper.sumRevenueByDoctorAndDateRange(doctorId, startDate, endDate);
        if (revenueGenerated == null) {
            revenueGenerated = 0.0;
        }
        
        return new DoctorPerformanceReport(
            doctorId,
            staff.getName(),
            staff.getDepartment(),
            startDate,
            totalPatients,
            completedConsultations,
            averageConsultationTime,
            patientSatisfactionScore,
            prescriptionsIssued,
            revenueGenerated
        );
    }

    @Override
    public WaitTimeAnalytics getWaitTimeAnalytics(LocalDate startDate, LocalDate endDate) {
        // 获取等待时间数据
        List<Double> waitTimes = patientQueueMapper.getWaitTimesByDateRange(startDate, endDate);
        
        if (waitTimes.isEmpty()) {
            return new WaitTimeAnalytics(
                startDate, 0.0, 0.0, 0.0, 0.0, 0L, 0L, 0.0, "09:00-10:00"
            );
        }
        
        // 计算平均等待时间
        Double averageWaitTime = waitTimes.stream()
            .mapToDouble(Double::doubleValue)
            .average()
            .orElse(0.0);
        
        // 计算中位数等待时间
        waitTimes.sort(Double::compareTo);
        Double medianWaitTime = waitTimes.size() % 2 == 0 ?
            (waitTimes.get(waitTimes.size() / 2 - 1) + waitTimes.get(waitTimes.size() / 2)) / 2.0 :
            waitTimes.get(waitTimes.size() / 2);
        
        // 获取最大和最小等待时间
        Double maxWaitTime = waitTimes.stream().mapToDouble(Double::doubleValue).max().orElse(0.0);
        Double minWaitTime = waitTimes.stream().mapToDouble(Double::doubleValue).min().orElse(0.0);
        
        Long totalPatients = (long) waitTimes.size();
        
        // 计算在目标时间内的患者数量（假设目标等待时间为30分钟）
        Long patientsWithinTarget = waitTimes.stream()
            .mapToLong(time -> time <= 30.0 ? 1L : 0L)
            .sum();
        
        Double targetComplianceRate = totalPatients > 0 ? 
            (patientsWithinTarget.doubleValue() / totalPatients * 100) : 0.0;
        
        // 获取等待时间最长的时段
        String peakWaitTimeHour = getPeakWaitTimeHour(startDate, endDate);
        
        return new WaitTimeAnalytics(
            startDate,
            averageWaitTime,
            medianWaitTime,
            maxWaitTime,
            minWaitTime,
            totalPatients,
            patientsWithinTarget,
            targetComplianceRate,
            peakWaitTimeHour
        );
    }

    @Override
    public List<DoctorPerformanceReport> getAllDoctorsPerformanceReport(LocalDate startDate, LocalDate endDate) {
        List<DoctorPerformanceReport> reports = new ArrayList<>();
        
        // 获取所有医生列表
        List<Long> doctorIds = staffMapper.getAllDoctorIds();
        
        for (Long doctorId : doctorIds) {
            DoctorPerformanceReport report = getDoctorPerformanceReport(doctorId, startDate, endDate);
            if (report != null) {
                reports.add(report);
            }
        }
        
        // 按总患者数降序排序
        return reports.stream()
            .sorted((a, b) -> Long.compare(b.getTotalPatients(), a.getTotalPatients()))
            .collect(Collectors.toList());
    }

    @Override
    public List<PopularService> getDepartmentVisitStatistics(LocalDate startDate, LocalDate endDate) {
        return getPopularServices(startDate, endDate);
    }

    /**
     * 获取每小时就诊统计
     */
    private Map<String, Long> getHourlyVisitStatistics(LocalDate startDate, LocalDate endDate) {
        Map<String, Long> hourlyStats = new HashMap<>();
        
        // 初始化24小时时段
        for (int hour = 0; hour < 24; hour++) {
            String timeSlot = String.format("%02d:00-%02d:00", hour, hour + 1);
            hourlyStats.put(timeSlot, 0L);
        }
        
        // 获取实际数据（这里需要根据实际数据库结构调整）
        List<Map<String, Object>> hourlyData = registrationMapper.getHourlyVisitStatistics(startDate, endDate);
        
        for (Map<String, Object> data : hourlyData) {
            Integer hour = (Integer) data.get("hour");
            Long count = (Long) data.get("count");
            String timeSlot = String.format("%02d:00-%02d:00", hour, hour + 1);
            hourlyStats.put(timeSlot, count);
        }
        
        return hourlyStats;
    }

    /**
     * 计算平均咨询时间
     */
    private Double calculateAverageConsultationTime(Long doctorId, LocalDate startDate, LocalDate endDate) {
        // 获取咨询时间数据
        List<Map<String, Object>> consultationTimes = medicalRecordMapper.getConsultationTimesByDoctorAndDateRange(doctorId, startDate, endDate);
        
        if (consultationTimes.isEmpty()) {
            return 0.0;
        }
        
        double totalMinutes = 0.0;
        int count = 0;
        
        for (Map<String, Object> record : consultationTimes) {
            LocalDateTime startTime = (LocalDateTime) record.get("start_time");
            LocalDateTime endTime = (LocalDateTime) record.get("end_time");
            
            if (startTime != null && endTime != null) {
                long minutes = Duration.between(startTime, endTime).toMinutes();
                totalMinutes += minutes;
                count++;
            }
        }
        
        return count > 0 ? totalMinutes / count : 0.0;
    }

    /**
     * 获取等待时间最长的时段
     */
    private String getPeakWaitTimeHour(LocalDate startDate, LocalDate endDate) {
        Map<String, Double> hourlyWaitTimes = patientQueueMapper.getHourlyAverageWaitTimes(startDate, endDate);
        
        String peakHour = "09:00-10:00";
        Double maxWaitTime = 0.0;
        
        for (Map.Entry<String, Double> entry : hourlyWaitTimes.entrySet()) {
            if (entry.getValue() > maxWaitTime) {
                maxWaitTime = entry.getValue();
                peakHour = entry.getKey();
            }
        }
        
        return peakHour;
    }
}