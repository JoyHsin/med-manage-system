package org.me.joy.clinic.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.me.joy.clinic.entity.Bill;

import java.time.LocalDate;
import java.util.List;

/**
 * 账单数据访问层
 */
@Mapper
public interface BillMapper extends BaseMapper<Bill> {
    
    /**
     * 根据患者ID查询账单列表
     */
    @Select("SELECT * FROM bills WHERE patient_id = #{patientId} ORDER BY created_at DESC")
    List<Bill> findByPatientId(@Param("patientId") Long patientId);
    
    /**
     * 根据挂号ID查询账单
     */
    @Select("SELECT * FROM bills WHERE registration_id = #{registrationId}")
    Bill findByRegistrationId(@Param("registrationId") Long registrationId);
    
    /**
     * 根据账单编号查询账单
     */
    @Select("SELECT * FROM bills WHERE bill_number = #{billNumber}")
    Bill findByBillNumber(@Param("billNumber") String billNumber);
    
    /**
     * 根据状态查询账单列表
     */
    @Select("SELECT * FROM bills WHERE status = #{status} ORDER BY created_at DESC")
    List<Bill> findByStatus(@Param("status") String status);
    
    /**
     * 查询指定日期范围内的账单
     */
    @Select("SELECT * FROM bills WHERE DATE(created_at) BETWEEN #{startDate} AND #{endDate} ORDER BY created_at DESC")
    List<Bill> findByDateRange(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
    
    /**
     * 统计指定日期的账单总金额
     */
    @Select("SELECT COALESCE(SUM(total_amount), 0) FROM bills WHERE DATE(created_at) = #{date} AND status IN ('PAID', 'PARTIALLY_PAID')")
    java.math.BigDecimal getTotalAmountByDate(@Param("date") LocalDate date);
}