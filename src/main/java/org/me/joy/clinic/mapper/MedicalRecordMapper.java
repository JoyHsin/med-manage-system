package org.me.joy.clinic.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.me.joy.clinic.entity.MedicalRecord;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 病历数据访问接口
 */
@Mapper
public interface MedicalRecordMapper extends BaseMapper<MedicalRecord> {

    /**
     * 根据患者ID查询病历列表
     */
    @Select("SELECT * FROM medical_records WHERE patient_id = #{patientId} AND deleted = 0 ORDER BY record_date DESC")
    List<MedicalRecord> findByPatientId(@Param("patientId") Long patientId);

    /**
     * 根据医生ID查询病历列表
     */
    @Select("SELECT * FROM medical_records WHERE doctor_id = #{doctorId} AND deleted = 0 ORDER BY record_date DESC")
    List<MedicalRecord> findByDoctorId(@Param("doctorId") Long doctorId);

    /**
     * 根据病历编号查询病历
     */
    @Select("SELECT * FROM medical_records WHERE record_number = #{recordNumber} AND deleted = 0")
    MedicalRecord findByRecordNumber(@Param("recordNumber") String recordNumber);

    /**
     * 根据挂号ID查询病历
     */
    @Select("SELECT * FROM medical_records WHERE registration_id = #{registrationId} AND deleted = 0")
    MedicalRecord findByRegistrationId(@Param("registrationId") Long registrationId);

    /**
     * 根据状态查询病历列表
     */
    @Select("SELECT * FROM medical_records WHERE status = #{status} AND deleted = 0 ORDER BY record_date DESC")
    List<MedicalRecord> findByStatus(@Param("status") String status);

    /**
     * 根据科室查询病历列表
     */
    @Select("SELECT * FROM medical_records WHERE department = #{department} AND deleted = 0 ORDER BY record_date DESC")
    List<MedicalRecord> findByDepartment(@Param("department") String department);

    /**
     * 根据病历类型查询病历列表
     */
    @Select("SELECT * FROM medical_records WHERE record_type = #{recordType} AND deleted = 0 ORDER BY record_date DESC")
    List<MedicalRecord> findByRecordType(@Param("recordType") String recordType);

    /**
     * 查询指定时间范围内的病历
     */
    @Select("SELECT * FROM medical_records WHERE record_date BETWEEN #{startDate} AND #{endDate} AND deleted = 0 ORDER BY record_date DESC")
    List<MedicalRecord> findByDateRange(@Param("startDate") LocalDateTime startDate, 
                                       @Param("endDate") LocalDateTime endDate);

    /**
     * 查询待审核的病历
     */
    @Select("SELECT * FROM medical_records WHERE status = '待审核' AND deleted = 0 ORDER BY record_date ASC")
    List<MedicalRecord> findPendingReview();

    /**
     * 查询传染病病历
     */
    @Select("SELECT * FROM medical_records WHERE is_infectious = 1 AND deleted = 0 ORDER BY record_date DESC")
    List<MedicalRecord> findInfectiousDiseaseRecords();

    /**
     * 查询慢性病病历
     */
    @Select("SELECT * FROM medical_records WHERE is_chronic_disease = 1 AND deleted = 0 ORDER BY record_date DESC")
    List<MedicalRecord> findChronicDiseaseRecords();

    /**
     * 根据患者ID和时间范围查询病历
     */
    @Select("SELECT * FROM medical_records WHERE patient_id = #{patientId} AND record_date BETWEEN #{startDate} AND #{endDate} AND deleted = 0 ORDER BY record_date DESC")
    List<MedicalRecord> findByPatientIdAndDateRange(@Param("patientId") Long patientId,
                                                   @Param("startDate") LocalDateTime startDate,
                                                   @Param("endDate") LocalDateTime endDate);

    /**
     * 统计患者的病历数量
     */
    @Select("SELECT COUNT(*) FROM medical_records WHERE patient_id = #{patientId} AND deleted = 0")
    int countByPatientId(@Param("patientId") Long patientId);

    /**
     * 统计医生的病历数量
     */
    @Select("SELECT COUNT(*) FROM medical_records WHERE doctor_id = #{doctorId} AND deleted = 0")
    int countByDoctorId(@Param("doctorId") Long doctorId);

    /**
     * 检查病历编号是否存在
     */
    @Select("SELECT COUNT(*) > 0 FROM medical_records WHERE record_number = #{recordNumber} AND deleted = 0")
    boolean existsByRecordNumber(@Param("recordNumber") String recordNumber);

    // Analytics methods
    
    /**
     * 统计指定医生在指定日期范围内的患者数量
     */
    @Select("SELECT COUNT(DISTINCT patient_id) FROM medical_records " +
            "WHERE doctor_id = #{doctorId} " +
            "AND DATE(record_date) BETWEEN #{startDate} AND #{endDate} " +
            "AND deleted = 0")
    Long countPatientsByDoctorAndDateRange(@Param("doctorId") Long doctorId, 
                                         @Param("startDate") LocalDate startDate, 
                                         @Param("endDate") LocalDate endDate);

    /**
     * 统计指定医生在指定日期范围内完成的咨询数量
     */
    @Select("SELECT COUNT(*) FROM medical_records " +
            "WHERE doctor_id = #{doctorId} " +
            "AND DATE(record_date) BETWEEN #{startDate} AND #{endDate} " +
            "AND status = '已完成' " +
            "AND deleted = 0")
    Long countCompletedConsultationsByDoctorAndDateRange(@Param("doctorId") Long doctorId, 
                                                       @Param("startDate") LocalDate startDate, 
                                                       @Param("endDate") LocalDate endDate);

    /**
     * 获取指定医生在指定日期范围内的咨询时间数据
     */
    @Select("SELECT record_date as start_time, completed_at as end_time " +
            "FROM medical_records " +
            "WHERE doctor_id = #{doctorId} " +
            "AND DATE(record_date) BETWEEN #{startDate} AND #{endDate} " +
            "AND completed_at IS NOT NULL " +
            "AND deleted = 0")
    List<Map<String, Object>> getConsultationTimesByDoctorAndDateRange(@Param("doctorId") Long doctorId, 
                                                                      @Param("startDate") LocalDate startDate, 
                                                                      @Param("endDate") LocalDate endDate);
}