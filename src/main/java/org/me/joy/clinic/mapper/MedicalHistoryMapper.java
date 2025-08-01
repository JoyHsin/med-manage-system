package org.me.joy.clinic.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.me.joy.clinic.entity.MedicalHistory;

import java.time.LocalDate;
import java.util.List;

/**
 * 病史Mapper接口
 */
@Mapper
public interface MedicalHistoryMapper extends BaseMapper<MedicalHistory> {

    /**
     * 根据患者ID查找病史
     * @param patientId 患者ID
     * @return 病史列表
     */
    List<MedicalHistory> findByPatientId(@Param("patientId") Long patientId);

    /**
     * 根据患者ID和病史类型查找病史
     * @param patientId 患者ID
     * @param historyType 病史类型
     * @return 病史列表
     */
    List<MedicalHistory> findByPatientIdAndHistoryType(@Param("patientId") Long patientId, 
                                                      @Param("historyType") String historyType);

    /**
     * 根据疾病名称查找病史
     * @param patientId 患者ID
     * @param diseaseName 疾病名称
     * @return 病史列表
     */
    List<MedicalHistory> findByPatientIdAndDiseaseName(@Param("patientId") Long patientId, 
                                                      @Param("diseaseName") String diseaseName);

    /**
     * 根据疾病分类查找病史
     * @param patientId 患者ID
     * @param diseaseCategory 疾病分类
     * @return 病史列表
     */
    List<MedicalHistory> findByPatientIdAndDiseaseCategory(@Param("patientId") Long patientId, 
                                                          @Param("diseaseCategory") String diseaseCategory);

    /**
     * 查找既往病史
     * @param patientId 患者ID
     * @return 既往病史列表
     */
    List<MedicalHistory> findPastMedicalHistory(@Param("patientId") Long patientId);

    /**
     * 查找家族病史
     * @param patientId 患者ID
     * @return 家族病史列表
     */
    List<MedicalHistory> findFamilyHistory(@Param("patientId") Long patientId);

    /**
     * 查找手术史
     * @param patientId 患者ID
     * @return 手术史列表
     */
    List<MedicalHistory> findSurgicalHistory(@Param("patientId") Long patientId);

    /**
     * 查找慢性疾病史
     * @param patientId 患者ID
     * @return 慢性疾病史列表
     */
    List<MedicalHistory> findChronicDiseases(@Param("patientId") Long patientId);

    /**
     * 查找遗传性疾病史
     * @param patientId 患者ID
     * @return 遗传性疾病史列表
     */
    List<MedicalHistory> findHereditaryDiseases(@Param("patientId") Long patientId);

    /**
     * 查找传染性疾病史
     * @param patientId 患者ID
     * @return 传染性疾病史列表
     */
    List<MedicalHistory> findContagiousDiseases(@Param("patientId") Long patientId);

    /**
     * 根据严重程度查找病史
     * @param patientId 患者ID
     * @param severity 严重程度
     * @return 病史列表
     */
    List<MedicalHistory> findByPatientIdAndSeverity(@Param("patientId") Long patientId, 
                                                   @Param("severity") String severity);

    /**
     * 根据当前状态查找病史
     * @param patientId 患者ID
     * @param currentStatus 当前状态
     * @return 病史列表
     */
    List<MedicalHistory> findByPatientIdAndCurrentStatus(@Param("patientId") Long patientId, 
                                                        @Param("currentStatus") String currentStatus);

    /**
     * 根据诊断日期范围查找病史
     * @param patientId 患者ID
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 病史列表
     */
    List<MedicalHistory> findByPatientIdAndDiagnosisDateRange(@Param("patientId") Long patientId,
                                                             @Param("startDate") LocalDate startDate,
                                                             @Param("endDate") LocalDate endDate);

    /**
     * 搜索病史（根据疾病名称、描述等）
     * @param patientId 患者ID
     * @param keyword 关键词
     * @return 病史列表
     */
    List<MedicalHistory> searchMedicalHistory(@Param("patientId") Long patientId, 
                                            @Param("keyword") String keyword);

    /**
     * 统计患者病史数量
     * @param patientId 患者ID
     * @return 病史数量
     */
    Long countByPatientId(@Param("patientId") Long patientId);

    /**
     * 统计指定类型的病史数量
     * @param patientId 患者ID
     * @param historyType 病史类型
     * @return 病史数量
     */
    Long countByPatientIdAndHistoryType(@Param("patientId") Long patientId, 
                                       @Param("historyType") String historyType);

    /**
     * 删除患者的所有病史
     * @param patientId 患者ID
     * @return 删除记录数
     */
    int deleteByPatientId(@Param("patientId") Long patientId);

    /**
     * 批量插入病史
     * @param medicalHistories 病史列表
     * @return 插入记录数
     */
    int batchInsert(@Param("medicalHistories") List<MedicalHistory> medicalHistories);
}