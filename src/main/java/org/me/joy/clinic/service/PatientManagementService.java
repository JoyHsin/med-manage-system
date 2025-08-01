package org.me.joy.clinic.service;

import org.me.joy.clinic.dto.CreatePatientRequest;
import org.me.joy.clinic.dto.PatientResponse;
import org.me.joy.clinic.dto.UpdatePatientRequest;
import org.me.joy.clinic.entity.AllergyHistory;
import org.me.joy.clinic.entity.MedicalHistory;

import java.time.LocalDate;
import java.util.List;

/**
 * 患者管理服务接口
 * 提供患者档案的创建、更新、查询和管理功能
 */
public interface PatientManagementService {

    /**
     * 创建新患者
     * @param createPatientRequest 创建患者请求
     * @return 患者响应信息
     */
    PatientResponse createPatient(CreatePatientRequest createPatientRequest);

    /**
     * 更新患者信息
     * @param patientId 患者ID
     * @param updatePatientRequest 更新患者请求
     * @return 更新后的患者信息
     */
    PatientResponse updatePatient(Long patientId, UpdatePatientRequest updatePatientRequest);

    /**
     * 根据ID获取患者信息
     * @param patientId 患者ID
     * @return 患者信息
     */
    PatientResponse getPatientById(Long patientId);

    /**
     * 根据患者编号获取患者信息
     * @param patientNumber 患者编号
     * @return 患者信息
     */
    PatientResponse getPatientByNumber(String patientNumber);

    /**
     * 根据身份证号获取患者信息
     * @param idCard 身份证号
     * @return 患者信息
     */
    PatientResponse getPatientByIdCard(String idCard);

    /**
     * 获取所有患者列表
     * @return 患者列表
     */
    List<PatientResponse> getAllPatients();

    /**
     * 根据状态获取患者列表
     * @param status 患者状态
     * @return 患者列表
     */
    List<PatientResponse> getPatientsByStatus(String status);

    /**
     * 获取VIP患者列表
     * @return VIP患者列表
     */
    List<PatientResponse> getVipPatients();

    /**
     * 根据性别获取患者列表
     * @param gender 性别
     * @return 患者列表
     */
    List<PatientResponse> getPatientsByGender(String gender);

    /**
     * 根据年龄范围获取患者列表
     * @param minAge 最小年龄
     * @param maxAge 最大年龄
     * @return 患者列表
     */
    List<PatientResponse> getPatientsByAgeRange(Integer minAge, Integer maxAge);

    /**
     * 搜索患者
     * @param keyword 关键词（姓名、手机号、患者编号）
     * @return 患者列表
     */
    List<PatientResponse> searchPatients(String keyword);

    /**
     * 获取今日就诊患者
     * @param date 日期
     * @return 患者列表
     */
    List<PatientResponse> getTodayPatients(LocalDate date);

    /**
     * 删除患者（软删除）
     * @param patientId 患者ID
     */
    void deletePatient(Long patientId);

    /**
     * 设置患者为VIP
     * @param patientId 患者ID
     */
    void setPatientAsVip(Long patientId);

    /**
     * 取消患者VIP状态
     * @param patientId 患者ID
     */
    void removePatientVipStatus(Long patientId);

    /**
     * 更新患者状态
     * @param patientId 患者ID
     * @param status 新状态
     */
    void updatePatientStatus(Long patientId, String status);

    /**
     * 记录患者就诊
     * @param patientId 患者ID
     */
    void recordPatientVisit(Long patientId);

    /**
     * 添加患者过敏史
     * @param patientId 患者ID
     * @param allergyHistory 过敏史信息
     * @return 添加的过敏史
     */
    AllergyHistory addAllergyHistory(Long patientId, AllergyHistory allergyHistory);

    /**
     * 更新患者过敏史
     * @param allergyHistoryId 过敏史ID
     * @param allergyHistory 过敏史信息
     * @return 更新后的过敏史
     */
    AllergyHistory updateAllergyHistory(Long allergyHistoryId, AllergyHistory allergyHistory);

    /**
     * 删除患者过敏史
     * @param allergyHistoryId 过敏史ID
     */
    void deleteAllergyHistory(Long allergyHistoryId);

    /**
     * 获取患者过敏史
     * @param patientId 患者ID
     * @return 过敏史列表
     */
    List<AllergyHistory> getPatientAllergyHistories(Long patientId);

    /**
     * 添加患者病史
     * @param patientId 患者ID
     * @param medicalHistory 病史信息
     * @return 添加的病史
     */
    MedicalHistory addMedicalHistory(Long patientId, MedicalHistory medicalHistory);

    /**
     * 更新患者病史
     * @param medicalHistoryId 病史ID
     * @param medicalHistory 病史信息
     * @return 更新后的病史
     */
    MedicalHistory updateMedicalHistory(Long medicalHistoryId, MedicalHistory medicalHistory);

    /**
     * 删除患者病史
     * @param medicalHistoryId 病史ID
     */
    void deleteMedicalHistory(Long medicalHistoryId);

    /**
     * 获取患者病史
     * @param patientId 患者ID
     * @return 病史列表
     */
    List<MedicalHistory> getPatientMedicalHistories(Long patientId);

    /**
     * 根据病史类型获取患者病史
     * @param patientId 患者ID
     * @param historyType 病史类型
     * @return 病史列表
     */
    List<MedicalHistory> getPatientMedicalHistoriesByType(Long patientId, String historyType);

    /**
     * 检查患者编号是否存在
     * @param patientNumber 患者编号
     * @return 是否存在
     */
    boolean existsByPatientNumber(String patientNumber);

    /**
     * 检查身份证号是否存在
     * @param idCard 身份证号
     * @return 是否存在
     */
    boolean existsByIdCard(String idCard);

    /**
     * 生成患者编号
     * @return 患者编号
     */
    String generatePatientNumber();

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
    Long countPatientsByStatus(String status);

    /**
     * 统计VIP患者数量
     * @return VIP患者数量
     */
    Long countVipPatients();
}