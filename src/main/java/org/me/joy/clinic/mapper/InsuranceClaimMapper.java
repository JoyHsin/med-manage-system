package org.me.joy.clinic.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.me.joy.clinic.entity.InsuranceClaim;

import java.util.List;

/**
 * 保险理赔数据访问接口
 */
@Mapper
public interface InsuranceClaimMapper extends BaseMapper<InsuranceClaim> {

    /**
     * 根据患者ID查询理赔记录
     */
    @Select("SELECT * FROM insurance_claims WHERE patient_id = #{patientId} AND deleted = 0 ORDER BY created_at DESC")
    List<InsuranceClaim> findByPatientId(@Param("patientId") Long patientId);

    /**
     * 根据保险提供商ID查询理赔记录
     */
    @Select("SELECT * FROM insurance_claims WHERE insurance_provider_id = #{insuranceProviderId} AND deleted = 0 ORDER BY created_at DESC")
    List<InsuranceClaim> findByInsuranceProviderId(@Param("insuranceProviderId") Long insuranceProviderId);

    /**
     * 根据账单ID查询理赔记录
     */
    @Select("SELECT * FROM insurance_claims WHERE bill_id = #{billId} AND deleted = 0 ORDER BY created_at DESC")
    List<InsuranceClaim> findByBillId(@Param("billId") Long billId);

    /**
     * 根据理赔编号查询理赔记录
     */
    @Select("SELECT * FROM insurance_claims WHERE claim_number = #{claimNumber} AND deleted = 0")
    InsuranceClaim findByClaimNumber(@Param("claimNumber") String claimNumber);

    /**
     * 根据状态查询理赔记录
     */
    @Select("SELECT * FROM insurance_claims WHERE status = #{status} AND deleted = 0 ORDER BY created_at DESC")
    List<InsuranceClaim> findByStatus(@Param("status") String status);

    /**
     * 查询待提交的理赔记录
     */
    @Select("SELECT * FROM insurance_claims WHERE status = '待提交' AND deleted = 0 ORDER BY created_at ASC")
    List<InsuranceClaim> findPendingSubmission();

    /**
     * 查询审核中的理赔记录
     */
    @Select("SELECT * FROM insurance_claims WHERE status = '审核中' AND deleted = 0 ORDER BY created_at ASC")
    List<InsuranceClaim> findUnderReview();

    /**
     * 查询已批准的理赔记录
     */
    @Select("SELECT * FROM insurance_claims WHERE status = '已批准' AND deleted = 0 ORDER BY processed_at DESC")
    List<InsuranceClaim> findApproved();

    /**
     * 查询已拒绝的理赔记录
     */
    @Select("SELECT * FROM insurance_claims WHERE status = '已拒绝' AND deleted = 0 ORDER BY processed_at DESC")
    List<InsuranceClaim> findRejected();

    /**
     * 统计患者的理赔数量
     */
    @Select("SELECT COUNT(*) FROM insurance_claims WHERE patient_id = #{patientId} AND deleted = 0")
    int countByPatientId(@Param("patientId") Long patientId);

    /**
     * 统计保险提供商的理赔数量
     */
    @Select("SELECT COUNT(*) FROM insurance_claims WHERE insurance_provider_id = #{insuranceProviderId} AND deleted = 0")
    int countByInsuranceProviderId(@Param("insuranceProviderId") Long insuranceProviderId);

    /**
     * 根据状态统计理赔数量
     */
    @Select("SELECT status, COUNT(*) as count FROM insurance_claims WHERE deleted = 0 GROUP BY status")
    List<Object> countByStatus();

    /**
     * 检查理赔编号是否存在
     */
    @Select("SELECT COUNT(*) > 0 FROM insurance_claims WHERE claim_number = #{claimNumber} AND deleted = 0")
    boolean existsByClaimNumber(@Param("claimNumber") String claimNumber);
}