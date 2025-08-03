package org.me.joy.clinic.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.me.joy.clinic.entity.DispenseRecord;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 处方调剂记录数据访问接口
 */
@Mapper
public interface DispenseRecordMapper extends BaseMapper<DispenseRecord> {

    /**
     * 根据处方ID查询调剂记录
     */
    @Select("SELECT * FROM dispense_records WHERE prescription_id = #{prescriptionId} AND deleted = 0 ORDER BY created_at DESC")
    List<DispenseRecord> findByPrescriptionId(@Param("prescriptionId") Long prescriptionId);

    /**
     * 根据处方编号查询调剂记录
     */
    @Select("SELECT * FROM dispense_records WHERE prescription_number = #{prescriptionNumber} AND deleted = 0 ORDER BY created_at DESC")
    List<DispenseRecord> findByPrescriptionNumber(@Param("prescriptionNumber") String prescriptionNumber);

    /**
     * 根据患者ID查询调剂记录
     */
    @Select("SELECT * FROM dispense_records WHERE patient_id = #{patientId} AND deleted = 0 ORDER BY created_at DESC")
    List<DispenseRecord> findByPatientId(@Param("patientId") Long patientId);

    /**
     * 根据调剂药师ID查询调剂记录
     */
    @Select("SELECT * FROM dispense_records WHERE pharmacist_id = #{pharmacistId} AND deleted = 0 ORDER BY dispense_start_time DESC")
    List<DispenseRecord> findByPharmacistId(@Param("pharmacistId") Long pharmacistId);

    /**
     * 根据发药药师ID查询发药记录
     */
    @Select("SELECT * FROM dispense_records WHERE dispensing_pharmacist_id = #{dispensingPharmacistId} AND deleted = 0 ORDER BY delivered_time DESC")
    List<DispenseRecord> findByDispensingPharmacistId(@Param("dispensingPharmacistId") Long dispensingPharmacistId);

    /**
     * 根据调剂状态查询记录
     */
    @Select("SELECT * FROM dispense_records WHERE status = #{status} AND deleted = 0 ORDER BY created_at DESC")
    List<DispenseRecord> findByStatus(@Param("status") String status);

    /**
     * 查询待调剂的记录
     */
    @Select("SELECT * FROM dispense_records WHERE status = '待调剂' AND deleted = 0 ORDER BY created_at ASC")
    List<DispenseRecord> findPendingDispense();

    /**
     * 查询调剂中的记录
     */
    @Select("SELECT * FROM dispense_records WHERE status = '调剂中' AND deleted = 0 ORDER BY dispense_start_time ASC")
    List<DispenseRecord> findInProgress();

    /**
     * 查询已调剂待发药的记录
     */
    @Select("SELECT * FROM dispense_records WHERE status = '已调剂' AND deleted = 0 ORDER BY dispense_completed_time ASC")
    List<DispenseRecord> findReadyForDelivery();

    /**
     * 查询已发药的记录
     */
    @Select("SELECT * FROM dispense_records WHERE status = '已发药' AND deleted = 0 ORDER BY delivered_time DESC")
    List<DispenseRecord> findDelivered();

    /**
     * 查询需要复核的记录
     */
    @Select("SELECT * FROM dispense_records WHERE validation_result = '需要复核' AND deleted = 0 ORDER BY created_at ASC")
    List<DispenseRecord> findNeedingReview();

    /**
     * 查询库存不足的记录
     */
    @Select("SELECT * FROM dispense_records WHERE stock_check_result IN ('不足', '部分不足') AND deleted = 0 ORDER BY created_at DESC")
    List<DispenseRecord> findWithStockIssues();

    /**
     * 查询有药品相互作用警告的记录
     */
    @Select("SELECT * FROM dispense_records WHERE drug_interaction_check LIKE '%警告%' AND deleted = 0 ORDER BY created_at DESC")
    List<DispenseRecord> findWithDrugInteractionWarnings();

    /**
     * 查询有过敏风险的记录
     */
    @Select("SELECT * FROM dispense_records WHERE allergy_check LIKE '%过敏%' AND deleted = 0 ORDER BY created_at DESC")
    List<DispenseRecord> findWithAllergyRisks();

    /**
     * 根据时间范围查询调剂记录
     */
    @Select("SELECT * FROM dispense_records WHERE dispense_start_time BETWEEN #{startTime} AND #{endTime} AND deleted = 0 ORDER BY dispense_start_time DESC")
    List<DispenseRecord> findByTimeRange(@Param("startTime") LocalDateTime startTime, 
                                        @Param("endTime") LocalDateTime endTime);

    /**
     * 根据发药时间范围查询记录
     */
    @Select("SELECT * FROM dispense_records WHERE delivered_time BETWEEN #{startTime} AND #{endTime} AND deleted = 0 ORDER BY delivered_time DESC")
    List<DispenseRecord> findByDeliveryTimeRange(@Param("startTime") LocalDateTime startTime, 
                                                @Param("endTime") LocalDateTime endTime);

    /**
     * 查询需要特殊包装的记录
     */
    @Select("SELECT * FROM dispense_records WHERE requires_special_packaging = 1 AND deleted = 0 ORDER BY created_at DESC")
    List<DispenseRecord> findRequiringSpecialPackaging();

    /**
     * 查询需要冷藏的记录
     */
    @Select("SELECT * FROM dispense_records WHERE requires_refrigeration = 1 AND deleted = 0 ORDER BY created_at DESC")
    List<DispenseRecord> findRequiringRefrigeration();

    /**
     * 统计药师的调剂数量
     */
    @Select("SELECT COUNT(*) FROM dispense_records WHERE pharmacist_id = #{pharmacistId} AND deleted = 0")
    int countByPharmacistId(@Param("pharmacistId") Long pharmacistId);

    /**
     * 统计发药药师的发药数量
     */
    @Select("SELECT COUNT(*) FROM dispense_records WHERE dispensing_pharmacist_id = #{dispensingPharmacistId} AND deleted = 0")
    int countByDispensingPharmacistId(@Param("dispensingPharmacistId") Long dispensingPharmacistId);

    /**
     * 统计患者的调剂记录数量
     */
    @Select("SELECT COUNT(*) FROM dispense_records WHERE patient_id = #{patientId} AND deleted = 0")
    int countByPatientId(@Param("patientId") Long patientId);

    /**
     * 根据状态统计调剂记录数量
     */
    @Select("SELECT status, COUNT(*) as count FROM dispense_records WHERE deleted = 0 GROUP BY status")
    List<Object> countByStatus();

    /**
     * 统计今日调剂数量
     */
    @Select("SELECT COUNT(*) FROM dispense_records WHERE DATE(dispense_start_time) = CURDATE() AND deleted = 0")
    int countTodayDispenses();

    /**
     * 统计今日发药数量
     */
    @Select("SELECT COUNT(*) FROM dispense_records WHERE DATE(delivered_time) = CURDATE() AND deleted = 0")
    int countTodayDeliveries();

    /**
     * 查询平均调剂时间（分钟）
     */
    @Select("SELECT AVG(TIMESTAMPDIFF(MINUTE, dispense_start_time, dispense_completed_time)) " +
            "FROM dispense_records WHERE dispense_start_time IS NOT NULL AND dispense_completed_time IS NOT NULL AND deleted = 0")
    Double getAverageDispenseTimeInMinutes();

    /**
     * 查询药师的平均调剂时间
     */
    @Select("SELECT AVG(TIMESTAMPDIFF(MINUTE, dispense_start_time, dispense_completed_time)) " +
            "FROM dispense_records WHERE pharmacist_id = #{pharmacistId} AND dispense_start_time IS NOT NULL " +
            "AND dispense_completed_time IS NOT NULL AND deleted = 0")
    Double getAverageDispenseTimeByPharmacist(@Param("pharmacistId") Long pharmacistId);

    /**
     * 检查处方是否已有调剂记录
     */
    @Select("SELECT COUNT(*) > 0 FROM dispense_records WHERE prescription_id = #{prescriptionId} AND deleted = 0")
    boolean existsByPrescriptionId(@Param("prescriptionId") Long prescriptionId);

    /**
     * 查询最近的调剂记录
     */
    @Select("SELECT * FROM dispense_records WHERE deleted = 0 ORDER BY created_at DESC LIMIT #{limit}")
    List<DispenseRecord> findRecent(@Param("limit") int limit);
}