package org.me.joy.clinic.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.me.joy.clinic.entity.PrescriptionItem;

import java.util.List;

/**
 * 处方项目数据访问接口
 */
@Mapper
public interface PrescriptionItemMapper extends BaseMapper<PrescriptionItem> {

    /**
     * 根据处方ID查询处方项目列表
     */
    @Select("SELECT * FROM prescription_items WHERE prescription_id = #{prescriptionId} AND deleted = 0 ORDER BY sort_order ASC, created_at ASC")
    List<PrescriptionItem> findByPrescriptionId(@Param("prescriptionId") Long prescriptionId);

    /**
     * 根据药品ID查询处方项目列表
     */
    @Select("SELECT * FROM prescription_items WHERE medicine_id = #{medicineId} AND deleted = 0 ORDER BY created_at DESC")
    List<PrescriptionItem> findByMedicineId(@Param("medicineId") Long medicineId);

    /**
     * 根据药品名称模糊查询处方项目
     */
    @Select("SELECT * FROM prescription_items WHERE medicine_name LIKE CONCAT('%', #{medicineName}, '%') AND deleted = 0 ORDER BY created_at DESC")
    List<PrescriptionItem> findByMedicineNameContaining(@Param("medicineName") String medicineName);

    /**
     * 查询替代药品的处方项目
     */
    @Select("SELECT * FROM prescription_items WHERE is_substitute = 1 AND deleted = 0 ORDER BY created_at DESC")
    List<PrescriptionItem> findSubstituteMedicines();

    /**
     * 根据原药品ID查询替代药品
     */
    @Select("SELECT * FROM prescription_items WHERE original_medicine_id = #{originalMedicineId} AND deleted = 0 ORDER BY created_at DESC")
    List<PrescriptionItem> findByOriginalMedicineId(@Param("originalMedicineId") Long originalMedicineId);

    /**
     * 统计处方项目数量
     */
    @Select("SELECT COUNT(*) FROM prescription_items WHERE prescription_id = #{prescriptionId} AND deleted = 0")
    int countByPrescriptionId(@Param("prescriptionId") Long prescriptionId);

    /**
     * 统计药品的使用次数
     */
    @Select("SELECT COUNT(*) FROM prescription_items WHERE medicine_id = #{medicineId} AND deleted = 0")
    int countByMedicineId(@Param("medicineId") Long medicineId);

    /**
     * 查询热门药品统计
     */
    @Select("SELECT medicine_name, COUNT(*) as count FROM prescription_items WHERE deleted = 0 GROUP BY medicine_name ORDER BY count DESC LIMIT #{limit}")
    List<Object> findPopularMedicines(@Param("limit") int limit);

    /**
     * 根据处方ID删除所有项目
     */
    @Select("UPDATE prescription_items SET deleted = 1 WHERE prescription_id = #{prescriptionId}")
    int deleteByPrescriptionId(@Param("prescriptionId") Long prescriptionId);
}