package org.me.joy.clinic.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.me.joy.clinic.entity.InsuranceEligibility;

import java.util.List;

/**
 * 保险资格数据访问接口
 */
@Mapper
public interface InsuranceEligibilityMapper extends BaseMapper<InsuranceEligibility> {

    /**
     * 根据患者ID查询保险资格
     */
    @Select("SELECT * FROM insurance_eligibilities WHERE patient_id = #{patientId} AND deleted = 0 ORDER BY created_at DESC")
    List<InsuranceEligibility> findByPatientId(@Param("patientId") Long patientId);

    /**
     * 根据保险提供商ID查询保险资格
     */
    @Select("SELECT * FROM insurance_eligibilities WHERE insurance_provider_id = #{insuranceProviderId} AND deleted = 0 ORDER BY created_at DESC")
    List<InsuranceEligibility> findByInsuranceProviderId(@Param("insuranceProviderId") Long insuranceProviderId);

    /**
     * 根据保险号码查询保险资格
     */
    @Select("SELECT * FROM insurance_eligibilities WHERE insurance_number = #{insuranceNumber} AND deleted = 0")
    InsuranceEligibility findByInsuranceNumber(@Param("insuranceNumber") String insuranceNumber);

    /**
     * 根据患者ID和保险提供商ID查询保险资格
     */
    @Select("SELECT * FROM insurance_eligibilities WHERE patient_id = #{patientId} AND insurance_provider_id = #{insuranceProviderId} AND deleted = 0")
    InsuranceEligibility findByPatientAndProvider(@Param("patientId") Long patientId, 
                                                 @Param("insuranceProviderId") Long insuranceProviderId);

    /**
     * 查询有效的保险资格
     */
    @Select("SELECT * FROM insurance_eligibilities WHERE status = '有效' AND (expiry_date IS NULL OR expiry_date >= CURDATE()) AND deleted = 0 ORDER BY created_at DESC")
    List<InsuranceEligibility> findValid();

    /**
     * 查询过期的保险资格
     */
    @Select("SELECT * FROM insurance_eligibilities WHERE expiry_date < CURDATE() AND deleted = 0 ORDER BY expiry_date DESC")
    List<InsuranceEligibility> findExpired();

    /**
     * 查询即将过期的保险资格（30天内）
     */
    @Select("SELECT * FROM insurance_eligibilities WHERE expiry_date BETWEEN CURDATE() AND DATE_ADD(CURDATE(), INTERVAL #{days} DAY) AND deleted = 0 ORDER BY expiry_date ASC")
    List<InsuranceEligibility> findExpiringSoon(@Param("days") int days);

    /**
     * 根据状态查询保险资格
     */
    @Select("SELECT * FROM insurance_eligibilities WHERE status = #{status} AND deleted = 0 ORDER BY created_at DESC")
    List<InsuranceEligibility> findByStatus(@Param("status") String status);

    /**
     * 根据验证状态查询保险资格
     */
    @Select("SELECT * FROM insurance_eligibilities WHERE verification_status = #{verificationStatus} AND deleted = 0 ORDER BY created_at DESC")
    List<InsuranceEligibility> findByVerificationStatus(@Param("verificationStatus") String verificationStatus);

    /**
     * 统计患者的保险资格数量
     */
    @Select("SELECT COUNT(*) FROM insurance_eligibilities WHERE patient_id = #{patientId} AND deleted = 0")
    int countByPatientId(@Param("patientId") Long patientId);

    /**
     * 统计保险提供商的保险资格数量
     */
    @Select("SELECT COUNT(*) FROM insurance_eligibilities WHERE insurance_provider_id = #{insuranceProviderId} AND deleted = 0")
    int countByInsuranceProviderId(@Param("insuranceProviderId") Long insuranceProviderId);

    /**
     * 根据状态统计保险资格数量
     */
    @Select("SELECT status, COUNT(*) as count FROM insurance_eligibilities WHERE deleted = 0 GROUP BY status")
    List<Object> countByStatus();

    /**
     * 检查保险号码是否存在
     */
    @Select("SELECT COUNT(*) > 0 FROM insurance_eligibilities WHERE insurance_number = #{insuranceNumber} AND deleted = 0")
    boolean existsByInsuranceNumber(@Param("insuranceNumber") String insuranceNumber);

    /**
     * 查询患者的有效保险资格
     */
    @Select("SELECT * FROM insurance_eligibilities WHERE patient_id = #{patientId} AND status = '有效' AND (expiry_date IS NULL OR expiry_date >= CURDATE()) AND deleted = 0 ORDER BY created_at DESC")
    List<InsuranceEligibility> findValidByPatientId(@Param("patientId") Long patientId);
}