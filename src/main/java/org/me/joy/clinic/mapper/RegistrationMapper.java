package org.me.joy.clinic.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.me.joy.clinic.entity.Registration;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * 挂号数据访问层
 */
@Mapper
public interface RegistrationMapper extends BaseMapper<Registration> {

    /**
     * 根据挂号编号查询
     */
    @Select("SELECT * FROM registrations WHERE registration_number = #{registrationNumber} AND deleted = 0")
    Registration findByRegistrationNumber(@Param("registrationNumber") String registrationNumber);

    /**
     * 根据患者ID查询挂号记录
     */
    @Select("SELECT * FROM registrations WHERE patient_id = #{patientId} AND deleted = 0 " +
            "ORDER BY registration_date DESC, registration_time DESC")
    List<Registration> findByPatientId(@Param("patientId") Long patientId);

    /**
     * 根据预约ID查询挂号记录
     */
    @Select("SELECT * FROM registrations WHERE appointment_id = #{appointmentId} AND deleted = 0")
    Registration findByAppointmentId(@Param("appointmentId") Long appointmentId);

    /**
     * 根据挂号日期查询
     */
    @Select("SELECT * FROM registrations WHERE registration_date = #{date} AND deleted = 0 " +
            "ORDER BY queue_number ASC, registration_time ASC")
    List<Registration> findByRegistrationDate(@Param("date") LocalDate date);

    /**
     * 根据科室查询挂号记录
     */
    @Select("SELECT * FROM registrations WHERE department = #{department} AND deleted = 0 " +
            "ORDER BY registration_date DESC, queue_number ASC")
    List<Registration> findByDepartment(@Param("department") String department);

    /**
     * 根据医生ID查询挂号记录
     */
    @Select("SELECT * FROM registrations WHERE doctor_id = #{doctorId} AND deleted = 0 " +
            "ORDER BY registration_date DESC, queue_number ASC")
    List<Registration> findByDoctorId(@Param("doctorId") Long doctorId);

    /**
     * 根据挂号类型查询
     */
    @Select("SELECT * FROM registrations WHERE registration_type = #{registrationType} AND deleted = 0 " +
            "ORDER BY registration_date DESC")
    List<Registration> findByRegistrationType(@Param("registrationType") String registrationType);

    /**
     * 根据状态查询挂号记录
     */
    @Select("SELECT * FROM registrations WHERE status = #{status} AND deleted = 0 " +
            "ORDER BY priority DESC, queue_number ASC")
    List<Registration> findByStatus(@Param("status") String status);

    /**
     * 根据支付状态查询
     */
    @Select("SELECT * FROM registrations WHERE payment_status = #{paymentStatus} AND deleted = 0 " +
            "ORDER BY registration_date DESC")
    List<Registration> findByPaymentStatus(@Param("paymentStatus") String paymentStatus);

    /**
     * 查询今日挂号记录
     */
    @Select("SELECT * FROM registrations WHERE registration_date = CURDATE() AND deleted = 0 " +
            "ORDER BY queue_number ASC, priority DESC")
    List<Registration> findTodayRegistrations();

    /**
     * 查询今日指定科室的挂号记录
     */
    @Select("SELECT * FROM registrations WHERE registration_date = CURDATE() " +
            "AND department = #{department} AND deleted = 0 " +
            "ORDER BY queue_number ASC, priority DESC")
    List<Registration> findTodayRegistrationsByDepartment(@Param("department") String department);

    /**
     * 查询今日指定医生的挂号记录
     */
    @Select("SELECT * FROM registrations WHERE registration_date = CURDATE() " +
            "AND doctor_id = #{doctorId} AND deleted = 0 " +
            "ORDER BY queue_number ASC, priority DESC")
    List<Registration> findTodayRegistrationsByDoctor(@Param("doctorId") Long doctorId);

    /**
     * 查询待叫号的挂号记录
     */
    @Select("SELECT * FROM registrations WHERE status = '已挂号' " +
            "AND registration_date = CURDATE() AND deleted = 0 " +
            "ORDER BY priority DESC, queue_number ASC")
    List<Registration> findPendingRegistrations();

    /**
     * 查询指定科室待叫号的挂号记录
     */
    @Select("SELECT * FROM registrations WHERE status = '已挂号' " +
            "AND registration_date = CURDATE() AND department = #{department} " +
            "AND deleted = 0 ORDER BY priority DESC, queue_number ASC")
    List<Registration> findPendingRegistrationsByDepartment(@Param("department") String department);

    /**
     * 查询指定医生待叫号的挂号记录
     */
    @Select("SELECT * FROM registrations WHERE status = '已挂号' " +
            "AND registration_date = CURDATE() AND doctor_id = #{doctorId} " +
            "AND deleted = 0 ORDER BY priority DESC, queue_number ASC")
    List<Registration> findPendingRegistrationsByDoctor(@Param("doctorId") Long doctorId);

    /**
     * 查询已叫号但未到达的挂号记录
     */
    @Select("SELECT * FROM registrations WHERE status = '已叫号' " +
            "AND registration_date = CURDATE() AND deleted = 0 " +
            "ORDER BY called_at ASC")
    List<Registration> findCalledButNotArrivedRegistrations();

    /**
     * 查询正在就诊的挂号记录
     */
    @Select("SELECT * FROM registrations WHERE status = '就诊中' " +
            "AND registration_date = CURDATE() AND deleted = 0 " +
            "ORDER BY started_at ASC")
    List<Registration> findActiveConsultations();

    /**
     * 查询急诊挂号记录
     */
    @Select("SELECT * FROM registrations WHERE is_emergency = true AND deleted = 0 " +
            "ORDER BY registration_date DESC, registration_time DESC")
    List<Registration> findEmergencyRegistrations();

    /**
     * 查询初诊挂号记录
     */
    @Select("SELECT * FROM registrations WHERE is_first_visit = true AND deleted = 0 " +
            "ORDER BY registration_date DESC")
    List<Registration> findFirstVisitRegistrations();

    /**
     * 根据挂号来源查询
     */
    @Select("SELECT * FROM registrations WHERE source = #{source} AND deleted = 0 " +
            "ORDER BY registration_date DESC")
    List<Registration> findBySource(@Param("source") String source);

    /**
     * 获取指定日期科室的下一个队列号
     */
    @Select("SELECT COALESCE(MAX(queue_number), 0) + 1 FROM registrations " +
            "WHERE registration_date = #{date} AND department = #{department} AND deleted = 0")
    Integer getNextQueueNumber(@Param("date") LocalDate date, @Param("department") String department);

    /**
     * 获取指定日期医生的下一个队列号
     */
    @Select("SELECT COALESCE(MAX(queue_number), 0) + 1 FROM registrations " +
            "WHERE registration_date = #{date} AND doctor_id = #{doctorId} AND deleted = 0")
    Integer getNextQueueNumberByDoctor(@Param("date") LocalDate date, @Param("doctorId") Long doctorId);

    /**
     * 统计指定日期的挂号数量
     */
    @Select("SELECT COUNT(*) FROM registrations WHERE registration_date = #{date} AND deleted = 0")
    Long countRegistrationsByDate(@Param("date") LocalDate date);

    /**
     * 统计指定科室指定日期的挂号数量
     */
    @Select("SELECT COUNT(*) FROM registrations WHERE registration_date = #{date} " +
            "AND department = #{department} AND deleted = 0")
    Long countRegistrationsByDateAndDepartment(@Param("date") LocalDate date, 
                                             @Param("department") String department);

    /**
     * 统计指定医生指定日期的挂号数量
     */
    @Select("SELECT COUNT(*) FROM registrations WHERE registration_date = #{date} " +
            "AND doctor_id = #{doctorId} AND deleted = 0")
    Long countRegistrationsByDateAndDoctor(@Param("date") LocalDate date, @Param("doctorId") Long doctorId);

    /**
     * 统计各状态挂号数量
     */
    @Select("SELECT status, COUNT(*) as count FROM registrations " +
            "WHERE deleted = 0 GROUP BY status")
    List<Object> countRegistrationsByStatus();

    /**
     * 统计各科室挂号数量
     */
    @Select("SELECT department, COUNT(*) as count FROM registrations " +
            "WHERE deleted = 0 GROUP BY department")
    List<Object> countRegistrationsByDepartment();

    /**
     * 统计各挂号类型数量
     */
    @Select("SELECT registration_type, COUNT(*) as count FROM registrations " +
            "WHERE deleted = 0 GROUP BY registration_type")
    List<Object> countRegistrationsByType();

    /**
     * 统计各支付状态数量
     */
    @Select("SELECT payment_status, COUNT(*) as count FROM registrations " +
            "WHERE deleted = 0 GROUP BY payment_status")
    List<Object> countRegistrationsByPaymentStatus();

    /**
     * 查询指定患者的最近挂号记录
     */
    @Select("SELECT * FROM registrations WHERE patient_id = #{patientId} AND deleted = 0 " +
            "ORDER BY registration_date DESC, registration_time DESC LIMIT 1")
    Registration findLatestByPatientId(@Param("patientId") Long patientId);

    /**
     * 查询指定时间范围内的挂号记录
     */
    @Select("SELECT * FROM registrations WHERE registration_date BETWEEN #{startDate} AND #{endDate} " +
            "AND deleted = 0 ORDER BY registration_date DESC, registration_time DESC")
    List<Registration> findByDateRange(@Param("startDate") LocalDate startDate, 
                                     @Param("endDate") LocalDate endDate);

    /**
     * 查询未支付的挂号记录
     */
    @Select("SELECT * FROM registrations WHERE payment_status = '未支付' AND deleted = 0 " +
            "ORDER BY registration_date DESC")
    List<Registration> findUnpaidRegistrations();

    /**
     * 查询已完成的挂号记录
     */
    @Select("SELECT * FROM registrations WHERE status = '已完成' AND deleted = 0 " +
            "ORDER BY completed_at DESC")
    List<Registration> findCompletedRegistrations();

    // Analytics methods
    
    /**
     * 统计指定日期范围内的挂号数量
     */
    @Select("SELECT COUNT(*) FROM registrations WHERE registration_date BETWEEN #{startDate} AND #{endDate} AND deleted = 0")
    Long countRegistrationsByDateRange(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    /**
     * 统计指定日期范围内的新患者数量
     */
    @Select("SELECT COUNT(DISTINCT r.patient_id) FROM registrations r " +
            "WHERE r.registration_date BETWEEN #{startDate} AND #{endDate} " +
            "AND r.is_first_visit = true AND r.deleted = 0")
    Long countNewPatientsByDateRange(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    /**
     * 获取各科室就诊量统计
     */
    @Select("SELECT department, COUNT(*) as count FROM registrations " +
            "WHERE registration_date BETWEEN #{startDate} AND #{endDate} AND deleted = 0 " +
            "GROUP BY department ORDER BY count DESC")
    List<Map<String, Object>> getDepartmentVisitStatistics(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    /**
     * 获取每小时就诊统计
     */
    @Select("SELECT HOUR(registration_time) as hour, COUNT(*) as count FROM registrations " +
            "WHERE registration_date BETWEEN #{startDate} AND #{endDate} AND deleted = 0 " +
            "GROUP BY HOUR(registration_time) ORDER BY hour")
    List<Map<String, Object>> getHourlyVisitStatistics(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
    
    // Additional analytics methods for patient analytics
    
    /**
     * 统计新患者数量（首次就诊）
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 新患者数量
     */
    Long countNewPatients(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
    
    /**
     * 统计回访患者数量（非首次就诊）
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 回访患者数量
     */
    Long countReturningPatients(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
    
    /**
     * 统计总就诊次数
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 总就诊次数
     */
    Long countTotalVisits(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
}