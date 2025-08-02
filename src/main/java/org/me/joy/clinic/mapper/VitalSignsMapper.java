package org.me.joy.clinic.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.me.joy.clinic.entity.VitalSigns;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 生命体征数据访问层
 * 
 * @author Kiro
 */
@Mapper
public interface VitalSignsMapper extends BaseMapper<VitalSigns> {

    /**
     * 根据患者ID查询生命体征记录
     * 
     * @param patientId 患者ID
     * @return 生命体征记录列表
     */
    @Select("SELECT * FROM vital_signs WHERE patient_id = #{patientId} AND deleted = false ORDER BY recorded_at DESC")
    List<VitalSigns> findByPatientId(@Param("patientId") Long patientId);

    /**
     * 根据患者ID和时间范围查询生命体征记录
     * 
     * @param patientId 患者ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 生命体征记录列表
     */
    @Select("SELECT * FROM vital_signs WHERE patient_id = #{patientId} " +
            "AND recorded_at BETWEEN #{startTime} AND #{endTime} " +
            "AND deleted = false ORDER BY recorded_at DESC")
    List<VitalSigns> findByPatientIdAndTimeRange(@Param("patientId") Long patientId,
                                                  @Param("startTime") LocalDateTime startTime,
                                                  @Param("endTime") LocalDateTime endTime);

    /**
     * 查询异常生命体征记录
     * 
     * @param patientId 患者ID（可选）
     * @param startTime 开始时间（可选）
     * @param endTime 结束时间（可选）
     * @return 异常生命体征记录列表
     */
    @Select("<script>" +
            "SELECT * FROM vital_signs WHERE is_abnormal = true AND deleted = false" +
            "<if test='patientId != null'> AND patient_id = #{patientId}</if>" +
            "<if test='startTime != null'> AND recorded_at >= #{startTime}</if>" +
            "<if test='endTime != null'> AND recorded_at <= #{endTime}</if>" +
            " ORDER BY recorded_at DESC" +
            "</script>")
    List<VitalSigns> findAbnormalVitalSigns(@Param("patientId") Long patientId,
                                            @Param("startTime") LocalDateTime startTime,
                                            @Param("endTime") LocalDateTime endTime);

    /**
     * 获取患者最新的生命体征记录
     * 
     * @param patientId 患者ID
     * @return 最新的生命体征记录
     */
    @Select("SELECT * FROM vital_signs WHERE patient_id = #{patientId} AND deleted = false " +
            "ORDER BY recorded_at DESC LIMIT 1")
    VitalSigns findLatestByPatientId(@Param("patientId") Long patientId);

    /**
     * 根据记录人员查询生命体征记录
     * 
     * @param recordedBy 记录人员ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 生命体征记录列表
     */
    @Select("SELECT * FROM vital_signs WHERE recorded_by = #{recordedBy} " +
            "AND recorded_at BETWEEN #{startTime} AND #{endTime} " +
            "AND deleted = false ORDER BY recorded_at DESC")
    List<VitalSigns> findByRecordedByAndTimeRange(@Param("recordedBy") Long recordedBy,
                                                   @Param("startTime") LocalDateTime startTime,
                                                   @Param("endTime") LocalDateTime endTime);
}