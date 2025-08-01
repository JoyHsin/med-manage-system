package org.me.joy.clinic.service;

import org.me.joy.clinic.dto.CreateRegistrationRequest;
import org.me.joy.clinic.entity.Registration;

import java.time.LocalDate;
import java.util.List;

/**
 * 挂号服务接口
 */
public interface RegistrationService {

    /**
     * 创建挂号
     */
    Registration createRegistration(CreateRegistrationRequest request);

    /**
     * 根据ID获取挂号记录
     */
    Registration getRegistrationById(Long registrationId);

    /**
     * 根据挂号编号获取挂号记录
     */
    Registration getRegistrationByNumber(String registrationNumber);

    /**
     * 根据患者ID获取挂号记录
     */
    List<Registration> getRegistrationsByPatientId(Long patientId);

    /**
     * 根据预约ID获取挂号记录
     */
    Registration getRegistrationByAppointmentId(Long appointmentId);

    /**
     * 根据日期获取挂号记录
     */
    List<Registration> getRegistrationsByDate(LocalDate date);

    /**
     * 根据科室获取挂号记录
     */
    List<Registration> getRegistrationsByDepartment(String department);

    /**
     * 根据医生ID获取挂号记录
     */
    List<Registration> getRegistrationsByDoctorId(Long doctorId);

    /**
     * 获取今日挂号记录
     */
    List<Registration> getTodayRegistrations();

    /**
     * 获取今日指定科室的挂号记录
     */
    List<Registration> getTodayRegistrationsByDepartment(String department);

    /**
     * 获取今日指定医生的挂号记录
     */
    List<Registration> getTodayRegistrationsByDoctor(Long doctorId);

    /**
     * 获取待叫号的挂号记录
     */
    List<Registration> getPendingRegistrations();

    /**
     * 获取指定科室待叫号的挂号记录
     */
    List<Registration> getPendingRegistrationsByDepartment(String department);

    /**
     * 获取指定医生待叫号的挂号记录
     */
    List<Registration> getPendingRegistrationsByDoctor(Long doctorId);

    /**
     * 获取已叫号但未到达的挂号记录
     */
    List<Registration> getCalledButNotArrivedRegistrations();

    /**
     * 获取正在就诊的挂号记录
     */
    List<Registration> getActiveConsultations();

    /**
     * 叫号
     */
    void callPatient(Long registrationId);

    /**
     * 标记患者到达
     */
    void markPatientArrived(Long registrationId);

    /**
     * 开始就诊
     */
    void startConsultation(Long registrationId);

    /**
     * 完成就诊
     */
    void completeConsultation(Long registrationId);

    /**
     * 取消挂号
     */
    void cancelRegistration(Long registrationId, String reason);

    /**
     * 标记未到
     */
    void markNoShow(Long registrationId);

    /**
     * 标记已支付
     */
    void markAsPaid(Long registrationId, String paymentMethod);

    /**
     * 更新挂号状态
     */
    void updateRegistrationStatus(Long registrationId, String status);

    /**
     * 根据状态获取挂号记录
     */
    List<Registration> getRegistrationsByStatus(String status);

    /**
     * 根据支付状态获取挂号记录
     */
    List<Registration> getRegistrationsByPaymentStatus(String paymentStatus);

    /**
     * 获取急诊挂号记录
     */
    List<Registration> getEmergencyRegistrations();

    /**
     * 获取初诊挂号记录
     */
    List<Registration> getFirstVisitRegistrations();

    /**
     * 获取未支付的挂号记录
     */
    List<Registration> getUnpaidRegistrations();

    /**
     * 获取已完成的挂号记录
     */
    List<Registration> getCompletedRegistrations();

    /**
     * 统计指定日期的挂号数量
     */
    Long countRegistrationsByDate(LocalDate date);

    /**
     * 统计指定科室指定日期的挂号数量
     */
    Long countRegistrationsByDateAndDepartment(LocalDate date, String department);

    /**
     * 统计指定医生指定日期的挂号数量
     */
    Long countRegistrationsByDateAndDoctor(LocalDate date, Long doctorId);

    /**
     * 统计各状态挂号数量
     */
    List<Object> countRegistrationsByStatus();

    /**
     * 统计各科室挂号数量
     */
    List<Object> countRegistrationsByDepartment();

    /**
     * 统计各挂号类型数量
     */
    List<Object> countRegistrationsByType();

    /**
     * 统计各支付状态数量
     */
    List<Object> countRegistrationsByPaymentStatus();

    /**
     * 获取患者最近的挂号记录
     */
    Registration getLatestRegistrationByPatient(Long patientId);

    /**
     * 从预约创建挂号
     */
    Registration createRegistrationFromAppointment(Long appointmentId);

    /**
     * 生成挂号编号
     */
    String generateRegistrationNumber();
}