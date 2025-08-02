package org.me.joy.clinic.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.me.joy.clinic.entity.BillItem;

import java.util.List;

/**
 * 账单项目数据访问层
 */
@Mapper
public interface BillItemMapper extends BaseMapper<BillItem> {
    
    /**
     * 根据账单ID查询账单项目列表
     */
    @Select("SELECT * FROM bill_items WHERE bill_id = #{billId} ORDER BY id")
    List<BillItem> findByBillId(@Param("billId") Long billId);
    
    /**
     * 根据项目类型查询账单项目
     */
    @Select("SELECT * FROM bill_items WHERE item_type = #{itemType}")
    List<BillItem> findByItemType(@Param("itemType") String itemType);
    
    /**
     * 根据处方项目ID查询账单项目
     */
    @Select("SELECT * FROM bill_items WHERE prescription_item_id = #{prescriptionItemId}")
    BillItem findByPrescriptionItemId(@Param("prescriptionItemId") Long prescriptionItemId);
    
    /**
     * 根据医疗记录ID查询账单项目
     */
    @Select("SELECT * FROM bill_items WHERE medical_record_id = #{medicalRecordId}")
    List<BillItem> findByMedicalRecordId(@Param("medicalRecordId") Long medicalRecordId);
    
    /**
     * 计算账单的总金额
     */
    @Select("SELECT COALESCE(SUM(actual_amount), 0) FROM bill_items WHERE bill_id = #{billId}")
    java.math.BigDecimal calculateTotalAmount(@Param("billId") Long billId);
}