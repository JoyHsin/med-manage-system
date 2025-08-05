package org.me.joy.clinic.service.impl;

import org.me.joy.clinic.dto.CommonDiagnosis;
import org.me.joy.clinic.dto.PatientDemographics;
import org.me.joy.clinic.dto.PatientRetentionReport;
import org.me.joy.clinic.dto.PatientSatisfactionScore;
import org.me.joy.clinic.mapper.DiagnosisMapper;
import org.me.joy.clinic.mapper.PatientMapper;
import org.me.joy.clinic.mapper.RegistrationMapper;
import org.me.joy.clinic.service.PatientAnalyticsService;
import org.me.joy.clinic.constants.BusinessConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.Period;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 患者分析服务实现
 */
@Service
public class PatientAnalyticsServiceImpl implements PatientAnalyticsService {
    
    
    @Autowired
    private PatientMapper patientMapper;
    
    @Autowired
    private DiagnosisMapper diagnosisMapper;
    
    @Autowired
    private RegistrationMapper registrationMapper;
    
    @Override
    public PatientDemographics getPatientDemographics() {
        // 获取总患者数
        Long totalPatients = patientMapper.countTotalPatients();
        
        // 获取性别分布
        Map<String, Long> genderDistribution = patientMapper.getGenderDistribution()
                .stream()
                .collect(Collectors.toMap(
                    map -> (String) map.get("gender"),
                    map -> ((Number) map.get("count")).longValue()
                ));
        
        // 获取年龄组分布
        List<Map<String, Object>> patients = patientMapper.getAllPatientsWithBirthDate();
        Map<String, Long> ageGroupDistribution = calculateAgeGroupDistribution(patients);
        
        // 计算平均年龄
        BigDecimal averageAge = calculateAverageAge(patients);
        
        // 获取地区分布
        Map<String, Long> locationDistribution = patientMapper.getLocationDistribution()
                .stream()
                .collect(Collectors.toMap(
                    map -> (String) map.get("location"),
                    map -> ((Number) map.get("count")).longValue()
                ));
        
        return new PatientDemographics(totalPatients, genderDistribution, 
                                     ageGroupDistribution, averageAge, locationDistribution);
    }
    
    @Override
    public List<CommonDiagnosis> getCommonDiagnoses(LocalDate startDate, LocalDate endDate) {
        List<Map<String, Object>> diagnosisStats = diagnosisMapper.getCommonDiagnoses(startDate, endDate);
        Long totalDiagnoses = diagnosisMapper.countTotalDiagnoses(startDate, endDate);
        
        return diagnosisStats.stream()
                .map(stat -> {
                    String diagnosisCode = (String) stat.get("diagnosis_code");
                    String diagnosisName = (String) stat.get("diagnosis_name");
                    Long count = ((Number) stat.get("count")).longValue();
                    Double percentage = totalDiagnoses > 0 ? 
                        (count.doubleValue() / totalDiagnoses.doubleValue()) * 100 : 0.0;
                    
                    return new CommonDiagnosis(diagnosisCode, diagnosisName, count, percentage);
                })
                .collect(Collectors.toList());
    }
    
    @Override
    public PatientRetentionReport getPatientRetentionReport() {
        LocalDate reportDate = LocalDate.now();
        LocalDate thirtyDaysAgo = reportDate.minusDays(BusinessConstants.TimeConstants.RETENTION_REPORT_DAYS);
        
        // 获取新患者数量（30天内首次就诊）
        Long newPatients = registrationMapper.countNewPatients(thirtyDaysAgo, reportDate);
        
        // 获取回访患者数量（30天内有多次就诊记录）
        Long returningPatients = registrationMapper.countReturningPatients(thirtyDaysAgo, reportDate);
        
        // 计算留存率
        Long totalPatients = newPatients + returningPatients;
        BigDecimal retentionRate = totalPatients > 0 ? 
            BigDecimal.valueOf(returningPatients.doubleValue() / totalPatients.doubleValue() * 100)
                .setScale(2, RoundingMode.HALF_UP) : BigDecimal.ZERO;
        
        // 获取总就诊次数
        Long totalVisits = registrationMapper.countTotalVisits(thirtyDaysAgo, reportDate);
        
        // 计算平均就诊次数
        BigDecimal averageVisitsPerPatient = totalPatients > 0 ?
            BigDecimal.valueOf(totalVisits.doubleValue() / totalPatients.doubleValue())
                .setScale(2, RoundingMode.HALF_UP) : BigDecimal.ZERO;
        
        return new PatientRetentionReport(reportDate, newPatients, returningPatients,
                                        retentionRate, totalVisits, averageVisitsPerPatient);
    }
    
    @Override
    public List<PatientSatisfactionScore> getPatientSatisfactionScores() {
        // TODO: 实现真实的患者满意度数据查询
        // 需要创建患者满意度调查表和相关的数据访问逻辑
        throw new UnsupportedOperationException("患者满意度功能暂未实现，需要创建满意度调查表和相关数据源");
    }
    
    /**
     * 计算年龄组分布
     */
    private Map<String, Long> calculateAgeGroupDistribution(List<Map<String, Object>> patients) {
        Map<String, Long> ageGroups = new HashMap<>();
        ageGroups.put("0-18", 0L);
        ageGroups.put("19-35", 0L);
        ageGroups.put("36-50", 0L);
        ageGroups.put("51-65", 0L);
        ageGroups.put("65+", 0L);
        
        for (Map<String, Object> patient : patients) {
            LocalDate birthDate = (LocalDate) patient.get("birth_date");
            if (birthDate != null) {
                int age = Period.between(birthDate, LocalDate.now()).getYears();
                String ageGroup = getAgeGroup(age);
                ageGroups.put(ageGroup, ageGroups.get(ageGroup) + 1);
            }
        }
        
        return ageGroups;
    }
    
    /**
     * 计算平均年龄
     */
    private BigDecimal calculateAverageAge(List<Map<String, Object>> patients) {
        if (patients.isEmpty()) {
            return BigDecimal.ZERO;
        }
        
        int totalAge = 0;
        int validPatients = 0;
        
        for (Map<String, Object> patient : patients) {
            LocalDate birthDate = (LocalDate) patient.get("birth_date");
            if (birthDate != null) {
                int age = Period.between(birthDate, LocalDate.now()).getYears();
                totalAge += age;
                validPatients++;
            }
        }
        
        return validPatients > 0 ? 
            BigDecimal.valueOf((double) totalAge / validPatients).setScale(1, RoundingMode.HALF_UP) :
            BigDecimal.ZERO;
    }
    
    /**
     * 根据年龄获取年龄组
     */
    private String getAgeGroup(int age) {
        if (age <= 18) return "0-18";
        if (age <= 35) return "19-35";
        if (age <= 50) return "36-50";
        if (age <= 65) return "51-65";
        return "65+";
    }
}