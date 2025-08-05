package org.me.joy.clinic.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.me.joy.clinic.entity.Diagnosis;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 诊断数据访问接口
 */
@Mapper
public interface DiagnosisMapper extends BaseMapper<Diagnosis> {

    /**
     * 根据病历ID查询诊断列表
     */
    @Select("SELECT * FROM diagnoses WHERE medical_record_id = #{medicalRecordId} AND deleted = 0 ORDER BY sort_order ASC, created_at ASC")
    List<Diagnosis> findByMedicalRecordId(@Param("medicalRecordId") Long medicalRecordId);

    /**
     * 根据医生ID查询诊断列表
     */
    @Select("SELECT * FROM diagnoses WHERE doctor_id = #{doctorId} AND deleted = 0 ORDER BY diagnosis_time DESC")
    List<Diagnosis> findByDoctorId(@Param("doctorId") Long doctorId);

    /**
     * 根据诊断编码查询诊断列表
     */
    @Select("SELECT * FROM diagnoses WHERE diagnosis_code = #{diagnosisCode} AND deleted = 0 ORDER BY diagnosis_time DESC")
    List<Diagnosis> findByDiagnosisCode(@Param("diagnosisCode") String diagnosisCode);

    /**
     * 根据诊断名称模糊查询
     */
    @Select("SELECT * FROM diagnoses WHERE diagnosis_name LIKE CONCAT('%', #{diagnosisName}, '%') AND deleted = 0 ORDER BY diagnosis_time DESC")
    List<Diagnosis> findByDiagnosisNameContaining(@Param("diagnosisName") String diagnosisName);

    /**
     * 根据诊断类型查询诊断列表
     */
    @Select("SELECT * FROM diagnoses WHERE diagnosis_type = #{diagnosisType} AND deleted = 0 ORDER BY diagnosis_time DESC")
    List<Diagnosis> findByDiagnosisType(@Param("diagnosisType") String diagnosisType);

    /**
     * 根据诊断状态查询诊断列表
     */
    @Select("SELECT * FROM diagnoses WHERE status = #{status} AND deleted = 0 ORDER BY diagnosis_time DESC")
    List<Diagnosis> findByStatus(@Param("status") String status);

    /**
     * 根据严重程度查询诊断列表
     */
    @Select("SELECT * FROM diagnoses WHERE severity = #{severity} AND deleted = 0 ORDER BY diagnosis_time DESC")
    List<Diagnosis> findBySeverity(@Param("severity") String severity);

    /**
     * 查询主诊断列表
     */
    @Select("SELECT * FROM diagnoses WHERE is_primary = 1 AND deleted = 0 ORDER BY diagnosis_time DESC")
    List<Diagnosis> findPrimaryDiagnoses();

    /**
     * 查询传染病诊断
     */
    @Select("SELECT * FROM diagnoses WHERE is_infectious = 1 AND deleted = 0 ORDER BY diagnosis_time DESC")
    List<Diagnosis> findInfectiousDiagnoses();

    /**
     * 查询慢性病诊断
     */
    @Select("SELECT * FROM diagnoses WHERE is_chronic_disease = 1 AND deleted = 0 ORDER BY diagnosis_time DESC")
    List<Diagnosis> findChronicDiseaseDiagnoses();

    /**
     * 查询遗传病诊断
     */
    @Select("SELECT * FROM diagnoses WHERE is_hereditary = 1 AND deleted = 0 ORDER BY diagnosis_time DESC")
    List<Diagnosis> findHereditaryDiagnoses();

    /**
     * 根据时间范围查询诊断
     */
    @Select("SELECT * FROM diagnoses WHERE diagnosis_time BETWEEN #{startTime} AND #{endTime} AND deleted = 0 ORDER BY diagnosis_time DESC")
    List<Diagnosis> findByTimeRange(@Param("startTime") LocalDateTime startTime, 
                                   @Param("endTime") LocalDateTime endTime);

    /**
     * 根据病历ID查询主诊断
     */
    @Select("SELECT * FROM diagnoses WHERE medical_record_id = #{medicalRecordId} AND is_primary = 1 AND deleted = 0 ORDER BY sort_order ASC LIMIT 1")
    Diagnosis findPrimaryDiagnosisByMedicalRecordId(@Param("medicalRecordId") Long medicalRecordId);

    /**
     * 统计诊断数量按诊断名称分组
     */
    @Select("SELECT diagnosis_name, COUNT(*) as count FROM diagnoses WHERE deleted = 0 GROUP BY diagnosis_name ORDER BY count DESC LIMIT #{limit}")
    List<Object> countByDiagnosisName(@Param("limit") int limit);

    /**
     * 统计医生的诊断数量
     */
    @Select("SELECT COUNT(*) FROM diagnoses WHERE doctor_id = #{doctorId} AND deleted = 0")
    int countByDoctorId(@Param("doctorId") Long doctorId);

    /**
     * 统计病历的诊断数量
     */
    @Select("SELECT COUNT(*) FROM diagnoses WHERE medical_record_id = #{medicalRecordId} AND deleted = 0")
    int countByMedicalRecordId(@Param("medicalRecordId") Long medicalRecordId);

    /**
     * 查询需要特殊关注的诊断
     */
    @Select("SELECT * FROM diagnoses WHERE (is_infectious = 1 OR is_chronic_disease = 1 OR severity IN ('重度', '危重')) AND deleted = 0 ORDER BY diagnosis_time DESC")
    List<Diagnosis> findDiagnosesNeedingSpecialAttention();
    
    // Analytics methods
    
    /**
     * 获取常见诊断统计
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 常见诊断统计数据
     */
    List<Map<String, Object>> getCommonDiagnoses(@Param("startDate") LocalDate startDate, 
                                                @Param("endDate") LocalDate endDate);
    
    /**
     * 统计指定时间范围内的总诊断数量
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 总诊断数量
     */
    Long countTotalDiagnoses(@Param("startDate") LocalDate startDate, 
                           @Param("endDate") LocalDate endDate);
}