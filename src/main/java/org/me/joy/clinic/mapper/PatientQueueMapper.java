package org.me.joy.clinic.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.me.joy.clinic.entity.PatientQueue;

import java.time.LocalDate;
import java.util.List;

/**
 * 患者队列数据访问层
 */
@Mapper
public interface PatientQueueMapper extends BaseMapper<PatientQueue> {

    /**
     * 获取指定日期的患者队列
     */
    @Select("SELECT pq.*, p.name as patient_name, p.phone as patient_phone, " +
            "r.registration_number, r.department " +
            "FROM patient_queue pq " +
            "LEFT JOIN patient p ON pq.patient_id = p.id " +
            "LEFT JOIN registration r ON pq.registration_id = r.id " +
            "WHERE pq.queue_date = #{queueDate} " +
            "ORDER BY pq.priority ASC, pq.queue_number ASC")
    List<PatientQueue> findByQueueDate(@Param("queueDate") LocalDate queueDate);

    /**
     * 获取下一个待叫号的患者
     */
    @Select("SELECT pq.*, p.name as patient_name, p.phone as patient_phone, " +
            "r.registration_number, r.department " +
            "FROM patient_queue pq " +
            "LEFT JOIN patient p ON pq.patient_id = p.id " +
            "LEFT JOIN registration r ON pq.registration_id = r.id " +
            "WHERE pq.queue_date = #{queueDate} " +
            "AND pq.status = 'WAITING' " +
            "ORDER BY pq.priority ASC, pq.queue_number ASC " +
            "LIMIT 1")
    PatientQueue findNextPatient(@Param("queueDate") LocalDate queueDate);

    /**
     * 获取指定状态的患者队列
     */
    @Select("SELECT pq.*, p.name as patient_name, p.phone as patient_phone, " +
            "r.registration_number, r.department " +
            "FROM patient_queue pq " +
            "LEFT JOIN patient p ON pq.patient_id = p.id " +
            "LEFT JOIN registration r ON pq.registration_id = r.id " +
            "WHERE pq.queue_date = #{queueDate} " +
            "AND pq.status = #{status} " +
            "ORDER BY pq.priority ASC, pq.queue_number ASC")
    List<PatientQueue> findByQueueDateAndStatus(@Param("queueDate") LocalDate queueDate, 
                                               @Param("status") String status);

    /**
     * 获取当日最大队列号
     */
    @Select("SELECT COALESCE(MAX(queue_number), 0) FROM patient_queue WHERE queue_date = #{queueDate}")
    Integer getMaxQueueNumber(@Param("queueDate") LocalDate queueDate);
}