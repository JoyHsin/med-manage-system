package org.me.joy.clinic.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.me.joy.clinic.entity.Prescription;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 处方数据访问接口
 */
@Mapper
public interface PrescriptionMapper extends BaseMapper<Prescription> {

    /**
     * 根据病历ID查询处方列表
     */
    @Select("SELECT * FROM prescriptions WHERE medical_record_id = #{medicalRecordId} AND deleted = 0 ORDER BY prescribed_at DESC")
    List<Prescription> findByMedicalRecordId(@Param("medicalRecordId") Long medicalRecordId);

    /**
     * 根据医生ID查询处方列表
     */
    @Select("SELECT * FROM prescriptions WHERE doctor_id = #{doctorId} AND deleted = 0 ORDER BY prescribed_at DESC")
    List<Prescription> findByDoctorId(@Param("doctorId") Long doctorId);

    /**
     * 根据处方编号查询处方
     */
    @Select("SELECT * FROM prescriptions WHERE prescription_number = #{prescriptionNumber} AND deleted = 0")
    Prescription findByPrescriptionNumber(@Param("prescriptionNumber") String prescriptionNumber);

    /**
     * 根据处方类型查询处方列表
     */
    @Select("SELECT * FROM prescriptions WHERE prescription_type = #{prescriptionType} AND deleted = 0 ORDER BY prescribed_at DESC")
    List<Prescription> findByPrescriptionType(@Param("prescriptionType") String prescriptionType);

    /**
     * 根据处方状态查询处方列表
     */
    @Select("SELECT * FROM prescriptions WHERE status = #{status} AND deleted = 0 ORDER BY prescribed_at DESC")
    List<Prescription> findByStatus(@Param("status") String status);

    /**
     * 根据药师ID查询已调配的处方
     */
    @Select("SELECT * FROM prescriptions WHERE pharmacist_id = #{pharmacistId} AND deleted = 0 ORDER BY dispensed_at DESC")
    List<Prescription> findByPharmacistId(@Param("pharmacistId") Long pharmacistId);

    /**
     * 根据审核医生ID查询已审核的处方
     */
    @Select("SELECT * FROM prescriptions WHERE review_doctor_id = #{reviewDoctorId} AND deleted = 0 ORDER BY reviewed_at DESC")
    List<Prescription> findByReviewDoctorId(@Param("reviewDoctorId") Long reviewDoctorId);

    /**
     * 查询待审核的处方
     */
    @Select("SELECT * FROM prescriptions WHERE status = '已开具' AND deleted = 0 ORDER BY prescribed_at ASC")
    List<Prescription> findPendingReview();

    /**
     * 查询待调配的处方
     */
    @Select("SELECT * FROM prescriptions WHERE status = '已审核' AND deleted = 0 ORDER BY reviewed_at ASC")
    List<Prescription> findPendingDispense();

    /**
     * 查询已调配待发药的处方
     */
    @Select("SELECT * FROM prescriptions WHERE status = '已调配' AND deleted = 0 ORDER BY dispensed_at ASC")
    List<Prescription> findPendingDelivery();

    /**
     * 查询急诊处方
     */
    @Select("SELECT * FROM prescriptions WHERE is_emergency = 1 AND deleted = 0 ORDER BY prescribed_at DESC")
    List<Prescription> findEmergencyPrescriptions();

    /**
     * 查询儿童处方
     */
    @Select("SELECT * FROM prescriptions WHERE is_child_prescription = 1 AND deleted = 0 ORDER BY prescribed_at DESC")
    List<Prescription> findChildPrescriptions();

    /**
     * 查询慢性病处方
     */
    @Select("SELECT * FROM prescriptions WHERE is_chronic_disease_prescription = 1 AND deleted = 0 ORDER BY prescribed_at DESC")
    List<Prescription> findChronicDiseasePrescriptions();

    /**
     * 查询特殊处方（麻醉、精神、毒性药品）
     */
    @Select("SELECT * FROM prescriptions WHERE prescription_type IN ('麻醉处方', '精神药品处方', '毒性药品处方') AND deleted = 0 ORDER BY prescribed_at DESC")
    List<Prescription> findSpecialPrescriptions();

    /**
     * 根据时间范围查询处方
     */
    @Select("SELECT * FROM prescriptions WHERE prescribed_at BETWEEN #{startTime} AND #{endTime} AND deleted = 0 ORDER BY prescribed_at DESC")
    List<Prescription> findByTimeRange(@Param("startTime") LocalDateTime startTime, 
                                      @Param("endTime") LocalDateTime endTime);

    /**
     * 查询过期的处方
     */
    @Select("SELECT * FROM prescriptions WHERE status NOT IN ('已发药', '已取消') AND DATE_ADD(prescribed_at, INTERVAL validity_days DAY) < NOW() AND deleted = 0")
    List<Prescription> findExpiredPrescriptions();

    /**
     * 统计医生的处方数量
     */
    @Select("SELECT COUNT(*) FROM prescriptions WHERE doctor_id = #{doctorId} AND deleted = 0")
    int countByDoctorId(@Param("doctorId") Long doctorId);

    /**
     * 统计药师的调配数量
     */
    @Select("SELECT COUNT(*) FROM prescriptions WHERE pharmacist_id = #{pharmacistId} AND deleted = 0")
    int countByPharmacistId(@Param("pharmacistId") Long pharmacistId);

    /**
     * 统计病历的处方数量
     */
    @Select("SELECT COUNT(*) FROM prescriptions WHERE medical_record_id = #{medicalRecordId} AND deleted = 0")
    int countByMedicalRecordId(@Param("medicalRecordId") Long medicalRecordId);

    /**
     * 检查处方编号是否存在
     */
    @Select("SELECT COUNT(*) > 0 FROM prescriptions WHERE prescription_number = #{prescriptionNumber} AND deleted = 0")
    boolean existsByPrescriptionNumber(@Param("prescriptionNumber") String prescriptionNumber);

    /**
     * 根据状态统计处方数量
     */
    @Select("SELECT status, COUNT(*) as count FROM prescriptions WHERE deleted = 0 GROUP BY status")
    List<Object> countByStatus();
}