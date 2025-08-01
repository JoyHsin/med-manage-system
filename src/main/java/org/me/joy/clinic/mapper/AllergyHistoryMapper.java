package org.me.joy.clinic.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.me.joy.clinic.entity.AllergyHistory;

import java.util.List;

/**
 * 过敏史Mapper接口
 */
@Mapper
public interface AllergyHistoryMapper extends BaseMapper<AllergyHistory> {

    /**
     * 根据患者ID查找过敏史
     * @param patientId 患者ID
     * @return 过敏史列表
     */
    List<AllergyHistory> findByPatientId(@Param("patientId") Long patientId);

    /**
     * 根据患者ID和过敏原查找过敏史
     * @param patientId 患者ID
     * @param allergen 过敏原
     * @return 过敏史列表
     */
    List<AllergyHistory> findByPatientIdAndAllergen(@Param("patientId") Long patientId, 
                                                   @Param("allergen") String allergen);

    /**
     * 根据过敏原类型查找过敏史
     * @param patientId 患者ID
     * @param allergenType 过敏原类型
     * @return 过敏史列表
     */
    List<AllergyHistory> findByPatientIdAndAllergenType(@Param("patientId") Long patientId, 
                                                       @Param("allergenType") String allergenType);

    /**
     * 根据严重程度查找过敏史
     * @param patientId 患者ID
     * @param severity 严重程度
     * @return 过敏史列表
     */
    List<AllergyHistory> findByPatientIdAndSeverity(@Param("patientId") Long patientId, 
                                                   @Param("severity") String severity);

    /**
     * 查找已确认的过敏史
     * @param patientId 患者ID
     * @return 已确认的过敏史列表
     */
    List<AllergyHistory> findConfirmedAllergies(@Param("patientId") Long patientId);

    /**
     * 查找严重过敏史
     * @param patientId 患者ID
     * @return 严重过敏史列表
     */
    List<AllergyHistory> findSevereAllergies(@Param("patientId") Long patientId);

    /**
     * 根据过敏原名称搜索过敏史
     * @param allergenKeyword 过敏原关键词
     * @return 过敏史列表
     */
    List<AllergyHistory> searchByAllergen(@Param("allergenKeyword") String allergenKeyword);

    /**
     * 统计患者过敏史数量
     * @param patientId 患者ID
     * @return 过敏史数量
     */
    Long countByPatientId(@Param("patientId") Long patientId);

    /**
     * 删除患者的所有过敏史
     * @param patientId 患者ID
     * @return 删除记录数
     */
    int deleteByPatientId(@Param("patientId") Long patientId);

    /**
     * 批量插入过敏史
     * @param allergyHistories 过敏史列表
     * @return 插入记录数
     */
    int batchInsert(@Param("allergyHistories") List<AllergyHistory> allergyHistories);
}