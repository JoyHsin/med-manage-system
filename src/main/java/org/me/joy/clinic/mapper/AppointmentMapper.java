package org.me.joy.clinic.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.me.joy.clinic.entity.Appointment;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 预约数据访问层
 */
@Mapper
public interface AppointmentMapper extends BaseMapper<Appointment> {

    /**
     * 根据患者ID查询预约
     */
    @Select("SELECT * FROM appointments WHERE patient_id = #{patientId} AND deleted = 0 " +
            "ORDER BY appointment_time DESC")
    List<Appointment> findByPatientId(@Param("patientId") Long patientId);

    /**
     * 根据医生ID查询预约
     */
    @Select("SELECT * FROM appointments WHERE doctor_id = #{doctorId} AND deleted = 0 " +
            "ORDER BY appointment_time ASC")
    List<Appointment> findByDoctorId(@Param("doctorId") Long doctorId);

    /**
     * 根据日期查询预约
     */
    @Select("SELECT * FROM appointments WHERE DATE(appointment_time) = #{date} AND deleted = 0 " +
            "ORDER BY appointment_time ASC")
    List<Appointment> findByDate(@Param("date") LocalDate date);

    /**
     * 根据医生ID和日期查询预约
     */
    @Select("SELECT * FROM appointments WHERE doctor_id = #{doctorId} " +
            "AND DATE(appointment_time) = #{date} AND deleted = 0 " +
            "ORDER BY appointment_time ASC")
    List<Appointment> findByDoctorIdAndDate(@Param("doctorId") Long doctorId, @Param("date") LocalDate date);

    /**
     * 根据状态查询预约
     */
    @Select("SELECT * FROM appointments WHERE status = #{status} AND deleted = 0 " +
            "ORDER BY appointment_time ASC")
    List<Appointment> findByStatus(@Param("status") String status);

    /**
     * 根据预约类型查询预约
     */
    @Select("SELECT * FROM appointments WHERE appointment_type = #{appointmentType} AND deleted = 0 " +
            "ORDER BY appointment_time ASC")
    List<Appointment> findByAppointmentType(@Param("appointmentType") String appointmentType);

    /**
     * 根据科室查询预约
     */
    @Select("SELECT * FROM appointments WHERE department = #{department} AND deleted = 0 " +
            "ORDER BY appointment_time ASC")
    List<Appointment> findByDepartment(@Param("department") String department);

    /**
     * 根据时间范围查询预约
     */
    @Select("SELECT * FROM appointments WHERE appointment_time BETWEEN #{startTime} AND #{endTime} " +
            "AND deleted = 0 ORDER BY appointment_time ASC")
    List<Appointment> findByTimeRange(@Param("startTime") LocalDateTime startTime, 
                                    @Param("endTime") LocalDateTime endTime);

    /**
     * 根据医生ID和时间范围查询预约
     */
    @Select("SELECT * FROM appointments WHERE doctor_id = #{doctorId} " +
            "AND appointment_time BETWEEN #{startTime} AND #{endTime} " +
            "AND deleted = 0 ORDER BY appointment_time ASC")
    List<Appointment> findByDoctorIdAndTimeRange(@Param("doctorId") Long doctorId,
                                               @Param("startTime") LocalDateTime startTime,
                                               @Param("endTime") LocalDateTime endTime);

    /**
     * 查询今日预约
     */
    @Select("SELECT * FROM appointments WHERE DATE(appointment_time) = CURDATE() AND deleted = 0 " +
            "ORDER BY appointment_time ASC")
    List<Appointment> findTodayAppointments();

    /**
     * 查询明日预约
     */
    @Select("SELECT * FROM appointments WHERE DATE(appointment_time) = DATE_ADD(CURDATE(), INTERVAL 1 DAY) " +
            "AND deleted = 0 ORDER BY appointment_time ASC")
    List<Appointment> findTomorrowAppointments();

    /**
     * 查询即将到期的预约（需要提醒的）
     */
    @Select("SELECT * FROM appointments WHERE need_reminder = true " +
            "AND status IN ('已预约', '已确认') " +
            "AND appointment_time > NOW() " +
            "AND appointment_time <= DATE_ADD(NOW(), INTERVAL reminder_minutes MINUTE) " +
            "AND deleted = 0 ORDER BY appointment_time ASC")
    List<Appointment> findAppointmentsNeedingReminder();

    /**
     * 查询过期的预约
     */
    @Select("SELECT * FROM appointments WHERE appointment_time < NOW() " +
            "AND status NOT IN ('已完成', '已取消') AND deleted = 0 " +
            "ORDER BY appointment_time ASC")
    List<Appointment> findExpiredAppointments();

    /**
     * 查询指定医生的可用时间段
     */
    @Select("SELECT appointment_time FROM appointments WHERE doctor_id = #{doctorId} " +
            "AND DATE(appointment_time) = #{date} " +
            "AND status NOT IN ('已取消') AND deleted = 0 " +
            "ORDER BY appointment_time ASC")
    List<LocalDateTime> findBookedTimeSlots(@Param("doctorId") Long doctorId, @Param("date") LocalDate date);

    /**
     * 检查时间冲突
     */
    @Select("SELECT COUNT(*) FROM appointments WHERE doctor_id = #{doctorId} " +
            "AND appointment_time = #{appointmentTime} " +
            "AND status NOT IN ('已取消') AND deleted = 0")
    Integer checkTimeConflict(@Param("doctorId") Long doctorId, 
                            @Param("appointmentTime") LocalDateTime appointmentTime);

    /**
     * 统计指定日期的预约数量
     */
    @Select("SELECT COUNT(*) FROM appointments WHERE DATE(appointment_time) = #{date} " +
            "AND deleted = 0")
    Long countAppointmentsByDate(@Param("date") LocalDate date);

    /**
     * 统计指定医生指定日期的预约数量
     */
    @Select("SELECT COUNT(*) FROM appointments WHERE doctor_id = #{doctorId} " +
            "AND DATE(appointment_time) = #{date} AND deleted = 0")
    Long countAppointmentsByDoctorAndDate(@Param("doctorId") Long doctorId, @Param("date") LocalDate date);

    /**
     * 统计各状态预约数量
     */
    @Select("SELECT status, COUNT(*) as count FROM appointments " +
            "WHERE deleted = 0 GROUP BY status")
    List<Object> countAppointmentsByStatus();

    /**
     * 统计各预约类型数量
     */
    @Select("SELECT appointment_type, COUNT(*) as count FROM appointments " +
            "WHERE deleted = 0 GROUP BY appointment_type")
    List<Object> countAppointmentsByType();

    /**
     * 根据优先级查询预约
     */
    @Select("SELECT * FROM appointments WHERE priority = #{priority} AND deleted = 0 " +
            "ORDER BY appointment_time ASC")
    List<Appointment> findByPriority(@Param("priority") Integer priority);

    /**
     * 查询高优先级预约
     */
    @Select("SELECT * FROM appointments WHERE priority >= 4 AND deleted = 0 " +
            "ORDER BY priority DESC, appointment_time ASC")
    List<Appointment> findHighPriorityAppointments();

    /**
     * 根据预约来源查询预约
     */
    @Select("SELECT * FROM appointments WHERE source = #{source} AND deleted = 0 " +
            "ORDER BY appointment_time DESC")
    List<Appointment> findBySource(@Param("source") String source);

    /**
     * 查询指定患者的最近预约
     */
    @Select("SELECT * FROM appointments WHERE patient_id = #{patientId} AND deleted = 0 " +
            "ORDER BY appointment_time DESC LIMIT 1")
    Appointment findLatestByPatientId(@Param("patientId") Long patientId);

    /**
     * 查询指定医生的下一个预约
     */
    @Select("SELECT * FROM appointments WHERE doctor_id = #{doctorId} " +
            "AND appointment_time > NOW() AND status IN ('已预约', '已确认') " +
            "AND deleted = 0 ORDER BY appointment_time ASC LIMIT 1")
    Appointment findNextByDoctorId(@Param("doctorId") Long doctorId);
}