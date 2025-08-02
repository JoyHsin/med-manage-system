package org.me.joy.clinic.service;

import org.me.joy.clinic.entity.PatientQueue;

import java.time.LocalDate;
import java.util.List;

/**
 * 分诊叫号服务接口
 */
public interface TriageService {

    /**
     * 获取当日患者队列
     * @return 患者队列列表
     */
    List<PatientQueue> getTodayPatientQueue();

    /**
     * 获取指定日期的患者队列
     * @param queueDate 队列日期
     * @return 患者队列列表
     */
    List<PatientQueue> getPatientQueueByDate(LocalDate queueDate);

    /**
     * 叫号
     * @param patientQueueId 患者队列ID
     * @param calledBy 叫号护士ID
     */
    void callPatient(Long patientQueueId, Long calledBy);

    /**
     * 确认患者到达
     * @param patientQueueId 患者队列ID
     * @param confirmedBy 确认护士ID
     */
    void confirmPatientArrival(Long patientQueueId, Long confirmedBy);

    /**
     * 标记患者未到
     * @param patientQueueId 患者队列ID
     * @param calledBy 操作护士ID
     */
    void markPatientAbsent(Long patientQueueId, Long calledBy);

    /**
     * 获取下一个待叫号的患者
     * @return 下一个患者队列信息
     */
    PatientQueue getNextPatient();

    /**
     * 获取下一个待叫号的患者（指定日期）
     * @param queueDate 队列日期
     * @return 下一个患者队列信息
     */
    PatientQueue getNextPatient(LocalDate queueDate);

    /**
     * 重新叫号
     * @param patientQueueId 患者队列ID
     * @param calledBy 叫号护士ID
     */
    void recallPatient(Long patientQueueId, Long calledBy);

    /**
     * 完成患者就诊
     * @param patientQueueId 患者队列ID
     */
    void completePatient(Long patientQueueId);

    /**
     * 创建患者队列记录
     * @param registrationId 挂号ID
     * @param priority 优先级
     * @return 患者队列信息
     */
    PatientQueue createPatientQueue(Long registrationId, Integer priority);

    /**
     * 获取指定状态的患者队列
     * @param queueDate 队列日期
     * @param status 状态
     * @return 患者队列列表
     */
    List<PatientQueue> getPatientQueueByStatus(LocalDate queueDate, String status);
}