package org.me.joy.clinic.service;

import org.me.joy.clinic.dto.CommonDiagnosis;
import org.me.joy.clinic.dto.PatientDemographics;
import org.me.joy.clinic.dto.PatientRetentionReport;
import org.me.joy.clinic.dto.PatientSatisfactionScore;

import java.time.LocalDate;
import java.util.List;

/**
 * 患者分析服务接口
 */
public interface PatientAnalyticsService {
    
    /**
     * 获取患者人口统计数据
     * @return 患者人口统计信息
     */
    PatientDemographics getPatientDemographics();
    
    /**
     * 获取常见诊断统计
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 常见诊断列表
     */
    List<CommonDiagnosis> getCommonDiagnoses(LocalDate startDate, LocalDate endDate);
    
    /**
     * 获取患者留存报告
     * @return 患者留存报告
     */
    PatientRetentionReport getPatientRetentionReport();
    
    /**
     * 获取患者满意度评分
     * @return 患者满意度评分列表
     */
    List<PatientSatisfactionScore> getPatientSatisfactionScores();
}