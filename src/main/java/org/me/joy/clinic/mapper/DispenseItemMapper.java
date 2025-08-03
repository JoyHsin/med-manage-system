package org.me.joy.clinic.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.me.joy.clinic.entity.DispenseItem;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 调剂项目明细数据访问接口
 */
@Mapper
public interface DispenseItemMapper extends BaseMapper<DispenseItem> {

    /**
     * 根据调剂记录ID查询调剂项目
     */
    @Select("SELECT * FROM dispense_items WHERE dispense_record_id = #{dispenseRecordId} AND deleted = 0 ORDER BY sort_order ASC")
    List<DispenseItem> findByDispenseRecordId(@Param("dispenseRecordId") Long dispenseRecordId);

    /**
     * 根据处方项目ID查询调剂项目
     */
    @Select("SELECT * FROM dispense_items WHERE prescription_item_id = #{prescriptionItemId} AND deleted = 0 ORDER BY created_at DESC")
    List<DispenseItem> findByPrescriptionItemId(@Param("prescriptionItemId") Long prescriptionItemId);

    /**
     * 根据药品ID查询调剂项目
     */
    @Select("SELECT * FROM dispense_items WHERE medicine_id = #{medicineId} AND deleted = 0 ORDER BY dispensed_time DESC")
    List<DispenseItem> findByMedicineId(@Param("medicineId") Long medicineId);

    /**
     * 根据调剂状态查询项目
     */
    @Select("SELECT * FROM dispense_items WHERE status = #{status} AND deleted = 0 ORDER BY created_at DESC")
    List<DispenseItem> findByStatus(@Param("status") String status);

    /**
     * 查询待调剂的项目
     */
    @Select("SELECT * FROM dispense_items WHERE status = '待调剂' AND deleted = 0 ORDER BY created_at ASC")
    List<DispenseItem> findPendingDispense();

    /**
     * 查询已调剂的项目
     */
    @Select("SELECT * FROM dispense_items WHERE status = '已调剂' AND deleted = 0 ORDER BY dispensed_time DESC")
    List<DispenseItem> findDispensed();

    /**
     * 查询缺货的项目
     */
    @Select("SELECT * FROM dispense_items WHERE status = '缺货' AND deleted = 0 ORDER BY created_at DESC")
    List<DispenseItem> findOutOfStock();

    /**
     * 查询替代药品项目
     */
    @Select("SELECT * FROM dispense_items WHERE status = '替代' OR is_substitute = 1 AND deleted = 0 ORDER BY created_at DESC")
    List<DispenseItem> findSubstitutes();

    /**
     * 查询退回的项目
     */
    @Select("SELECT * FROM dispense_items WHERE status = '退回' AND deleted = 0 ORDER BY returned_time DESC")
    List<DispenseItem> findReturned();

    /**
     * 根据库存状态查询项目
     */
    @Select("SELECT * FROM dispense_items WHERE stock_status = #{stockStatus} AND deleted = 0 ORDER BY created_at DESC")
    List<DispenseItem> findByStockStatus(@Param("stockStatus") String stockStatus);

    /**
     * 查询库存不足的项目
     */
    @Select("SELECT * FROM dispense_items WHERE stock_status IN ('不足', '无库存') AND deleted = 0 ORDER BY created_at DESC")
    List<DispenseItem> findWithStockIssues();

    /**
     * 根据调剂人员查询项目
     */
    @Select("SELECT * FROM dispense_items WHERE dispensed_by = #{dispensedBy} AND deleted = 0 ORDER BY dispensed_time DESC")
    List<DispenseItem> findByDispensedBy(@Param("dispensedBy") Long dispensedBy);

    /**
     * 根据复核人员查询项目
     */
    @Select("SELECT * FROM dispense_items WHERE reviewed_by = #{reviewedBy} AND deleted = 0 ORDER BY reviewed_time DESC")
    List<DispenseItem> findByReviewedBy(@Param("reviewedBy") Long reviewedBy);

    /**
     * 查询需要复核的项目
     */
    @Select("SELECT * FROM dispense_items WHERE quality_check_result = '需要复检' AND deleted = 0 ORDER BY created_at ASC")
    List<DispenseItem> findNeedingReview();

    /**
     * 查询质量不合格的项目
     */
    @Select("SELECT * FROM dispense_items WHERE quality_check_result = '不合格' AND deleted = 0 ORDER BY created_at DESC")
    List<DispenseItem> findQualityFailed();

    /**
     * 根据批号查询调剂项目
     */
    @Select("SELECT * FROM dispense_items WHERE batch_number = #{batchNumber} AND deleted = 0 ORDER BY dispensed_time DESC")
    List<DispenseItem> findByBatchNumber(@Param("batchNumber") String batchNumber);

    /**
     * 查询即将过期的调剂项目（30天内）
     */
    @Select("SELECT * FROM dispense_items WHERE expiry_date BETWEEN CURDATE() AND DATE_ADD(CURDATE(), INTERVAL 30 DAY) AND deleted = 0 ORDER BY expiry_date ASC")
    List<DispenseItem> findNearExpiry();

    /**
     * 查询已过期的调剂项目
     */
    @Select("SELECT * FROM dispense_items WHERE expiry_date < CURDATE() AND deleted = 0 ORDER BY expiry_date DESC")
    List<DispenseItem> findExpired();

    /**
     * 根据时间范围查询调剂项目
     */
    @Select("SELECT * FROM dispense_items WHERE dispensed_time BETWEEN #{startTime} AND #{endTime} AND deleted = 0 ORDER BY dispensed_time DESC")
    List<DispenseItem> findByTimeRange(@Param("startTime") LocalDateTime startTime, 
                                      @Param("endTime") LocalDateTime endTime);

    /**
     * 统计药品的调剂数量
     */
    @Select("SELECT SUM(dispensed_quantity) FROM dispense_items WHERE medicine_id = #{medicineId} AND status = '已调剂' AND deleted = 0")
    Integer sumDispensedQuantityByMedicine(@Param("medicineId") Long medicineId);

    /**
     * 统计调剂人员的调剂项目数量
     */
    @Select("SELECT COUNT(*) FROM dispense_items WHERE dispensed_by = #{dispensedBy} AND deleted = 0")
    int countByDispensedBy(@Param("dispensedBy") Long dispensedBy);

    /**
     * 统计复核人员的复核项目数量
     */
    @Select("SELECT COUNT(*) FROM dispense_items WHERE reviewed_by = #{reviewedBy} AND deleted = 0")
    int countByReviewedBy(@Param("reviewedBy") Long reviewedBy);

    /**
     * 根据状态统计调剂项目数量
     */
    @Select("SELECT status, COUNT(*) as count FROM dispense_items WHERE deleted = 0 GROUP BY status")
    List<Object> countByStatus();

    /**
     * 统计今日调剂项目数量
     */
    @Select("SELECT COUNT(*) FROM dispense_items WHERE DATE(dispensed_time) = CURDATE() AND deleted = 0")
    int countTodayDispenses();

    /**
     * 统计药品的替代次数
     */
    @Select("SELECT COUNT(*) FROM dispense_items WHERE original_medicine_id = #{medicineId} AND is_substitute = 1 AND deleted = 0")
    int countSubstitutesByOriginalMedicine(@Param("medicineId") Long medicineId);

    /**
     * 查询最常用的替代药品
     */
    @Select("SELECT medicine_id, medicine_name, COUNT(*) as substitute_count " +
            "FROM dispense_items WHERE is_substitute = 1 AND deleted = 0 " +
            "GROUP BY medicine_id, medicine_name ORDER BY substitute_count DESC LIMIT #{limit}")
    List<Object> findMostUsedSubstitutes(@Param("limit") int limit);

    /**
     * 查询调剂项目的平均数量
     */
    @Select("SELECT AVG(dispensed_quantity) FROM dispense_items WHERE status = '已调剂' AND deleted = 0")
    Double getAverageDispensedQuantity();

    /**
     * 查询药品的平均调剂数量
     */
    @Select("SELECT AVG(dispensed_quantity) FROM dispense_items WHERE medicine_id = #{medicineId} AND status = '已调剂' AND deleted = 0")
    Double getAverageDispensedQuantityByMedicine(@Param("medicineId") Long medicineId);

    /**
     * 检查处方项目是否已调剂
     */
    @Select("SELECT COUNT(*) > 0 FROM dispense_items WHERE prescription_item_id = #{prescriptionItemId} AND status = '已调剂' AND deleted = 0")
    boolean isDispensedByPrescriptionItem(@Param("prescriptionItemId") Long prescriptionItemId);

    /**
     * 查询最近的调剂项目
     */
    @Select("SELECT * FROM dispense_items WHERE deleted = 0 ORDER BY created_at DESC LIMIT #{limit}")
    List<DispenseItem> findRecent(@Param("limit") int limit);
}