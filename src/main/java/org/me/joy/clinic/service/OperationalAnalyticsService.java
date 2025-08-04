package org.me.joy.clinic.service;

import org.me.joy.clinic.dto.DoctorPerformanceReport;
import org.me.joy.clinic.dto.PatientVisitAnalytics;
import org.me.joy.clinic.dto.PopularService;
import org.me.joy.clinic.dto.WaitTimeAnalytics;

import java.time.LocalDate;
import java.util.List;

/**
 * 运营分析服务接口
 */
public interface OperationalAnalyticsService {

    /**
     * 获取患者就诊量分析
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 患者就诊量分析数据
     */
    PatientVisitAnalytics getPatientVisitAnalytics(LocalDate startDate, LocalDate endDate);

    /**
     * 获取热门服务列表
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 热门服务列表
     */
    List<PopularService> getPopularServices(LocalDate startDate, LocalDate endDate);

    /**
     * 获取医生绩效报表
     * @param doctorId 医生ID
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 医生绩效报表
     */
    DoctorPerformanceReport getDoctorPerformanceReport(Long doctorId, LocalDate startDate, LocalDate endDate);

    /**
     * 获取等待时间分析
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 等待时间分析数据
     */
    WaitTimeAnalytics getWaitTimeAnalytics(LocalDate startDate, LocalDate endDate);

    /**
     * 获取所有医生的绩效报表
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 所有医生绩效报表列表
     */
    List<DoctorPerformanceReport> getAllDoctorsPerformanceReport(LocalDate startDate, LocalDate endDate);

    /**
     * 获取部门就诊量统计
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 部门就诊量统计
     */
    List<PopularService> getDepartmentVisitStatistics(LocalDate startDate, LocalDate endDate);
}