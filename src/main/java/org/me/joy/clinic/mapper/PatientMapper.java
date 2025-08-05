package org.me.joy.clinic.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.me.joy.clinic.entity.Patient;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 患者Mapper接口
 */
@Mapper
public interface PatientMapper extends BaseMapper<Patient> {

    /**
     * 根据患者编号查找患者
     * @param patientNumber 患者编号
     * @return 患者信息
     */
    Optional<Patient> findByPatientNumber(@Param("patientNumber") String patientNumber);

    /**
     * 根据身份证号查找患者
     * @param idCard 身份证号
     * @return 患者信息
     */
    Optional<Patient> findByIdCard(@Param("idCard") String idCard);

    /**
     * 根据手机号查找患者
     * @param phone 手机号
     * @return 患者列表
     */
    List<Patient> findByPhone(@Param("phone") String phone);

    /**
     * 根据姓名模糊查找患者
     * @param name 患者姓名
     * @return 患者列表
     */
    List<Patient> findByNameLike(@Param("name") String name);

    /**
     * 根据状态查找患者
     * @param status 患者状态
     * @return 患者列表
     */
    List<Patient> findByStatus(@Param("status") String status);

    /**
     * 查找VIP患者
     * @return VIP患者列表
     */
    List<Patient> findVipPatients();

    /**
     * 根据年龄范围查找患者
     * @param minAge 最小年龄
     * @param maxAge 最大年龄
     * @return 患者列表
     */
    List<Patient> findByAgeRange(@Param("minAge") Integer minAge, @Param("maxAge") Integer maxAge);

    /**
     * 根据性别查找患者
     * @param gender 性别
     * @return 患者列表
     */
    List<Patient> findByGender(@Param("gender") String gender);

    /**
     * 根据就诊日期范围查找患者
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 患者列表
     */
    List<Patient> findByVisitDateRange(@Param("startDate") LocalDate startDate, 
                                      @Param("endDate") LocalDate endDate);

    /**
     * 查找今日就诊患者
     * @param date 日期
     * @return 患者列表
     */
    List<Patient> findTodayPatients(@Param("date") LocalDate date);

    /**
     * 搜索患者（根据姓名、手机号、患者编号）
     * @param keyword 关键词
     * @return 患者列表
     */
    List<Patient> searchPatients(@Param("keyword") String keyword);

    /**
     * 统计患者总数
     * @return 患者总数
     */
    Long countAllPatients();

    /**
     * 统计指定状态的患者数量
     * @param status 患者状态
     * @return 患者数量
     */
    Long countPatientsByStatus(@Param("status") String status);

    /**
     * 统计VIP患者数量
     * @return VIP患者数量
     */
    Long countVipPatients();

    /**
     * 更新患者就诊信息
     * @param patientId 患者ID
     * @return 更新记录数
     */
    int updateVisitInfo(@Param("patientId") Long patientId);

    /**
     * 批量更新患者状态
     * @param patientIds 患者ID列表
     * @param status 新状态
     * @return 更新记录数
     */
    int batchUpdateStatus(@Param("patientIds") List<Long> patientIds, @Param("status") String status);
    
    // Analytics methods
    
    /**
     * 统计总患者数
     * @return 总患者数
     */
    Long countTotalPatients();
    
    /**
     * 获取性别分布统计
     * @return 性别分布数据
     */
    List<Map<String, Object>> getGenderDistribution();
    
    /**
     * 获取所有患者的出生日期信息
     * @return 患者出生日期列表
     */
    List<Map<String, Object>> getAllPatientsWithBirthDate();
    
    /**
     * 获取地区分布统计
     * @return 地区分布数据
     */
    List<Map<String, Object>> getLocationDistribution();
}