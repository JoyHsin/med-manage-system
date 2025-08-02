package org.me.joy.clinic.service;

import org.me.joy.clinic.dto.CreateMedicalRecordRequest;
import org.me.joy.clinic.dto.UpdateMedicalRecordRequest;
import org.me.joy.clinic.entity.Diagnosis;
import org.me.joy.clinic.entity.MedicalRecord;
import org.me.joy.clinic.entity.Prescription;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 电子病历服务接口
 * 提供病历的创建、更新、查询以及诊断和处方管理功能
 */
public interface ElectronicMedicalRecordService {

    /**
     * 创建病历
     * 
     * @param request 创建病历请求
     * @return 创建的病历
     */
    MedicalRecord createMedicalRecord(CreateMedicalRecordRequest request);

    /**
     * 更新病历
     * 
     * @param recordId 病历ID
     * @param request 更新病历请求
     * @return 更新后的病历
     */
    MedicalRecord updateMedicalRecord(Long recordId, UpdateMedicalRecordRequest request);

    /**
     * 根据ID查询病历
     * 
     * @param recordId 病历ID
     * @return 病历信息
     */
    MedicalRecord getMedicalRecordById(Long recordId);

    /**
     * 根据病历编号查询病历
     * 
     * @param recordNumber 病历编号
     * @return 病历信息
     */
    MedicalRecord getMedicalRecordByNumber(String recordNumber);

    /**
     * 查询患者的病历列表
     * 
     * @param patientId 患者ID
     * @return 病历列表
     */
    List<MedicalRecord> getPatientMedicalRecords(Long patientId);

    /**
     * 查询医生的病历列表
     * 
     * @param doctorId 医生ID
     * @return 病历列表
     */
    List<MedicalRecord> getDoctorMedicalRecords(Long doctorId);

    /**
     * 根据挂号ID查询病历
     * 
     * @param registrationId 挂号ID
     * @return 病历信息
     */
    MedicalRecord getMedicalRecordByRegistrationId(Long registrationId);

    /**
     * 查询指定时间范围内的病历
     * 
     * @param startDate 开始时间
     * @param endDate 结束时间
     * @return 病历列表
     */
    List<MedicalRecord> getMedicalRecordsByDateRange(LocalDateTime startDate, LocalDateTime endDate);

    /**
     * 查询患者在指定时间范围内的病历
     * 
     * @param patientId 患者ID
     * @param startDate 开始时间
     * @param endDate 结束时间
     * @return 病历列表
     */
    List<MedicalRecord> getPatientMedicalRecordsByDateRange(Long patientId, LocalDateTime startDate, LocalDateTime endDate);

    /**
     * 根据状态查询病历列表
     * 
     * @param status 病历状态
     * @return 病历列表
     */
    List<MedicalRecord> getMedicalRecordsByStatus(String status);

    /**
     * 查询待审核的病历
     * 
     * @return 待审核病历列表
     */
    List<MedicalRecord> getPendingReviewRecords();

    /**
     * 查询传染病病历
     * 
     * @return 传染病病历列表
     */
    List<MedicalRecord> getInfectiousDiseaseRecords();

    /**
     * 查询慢性病病历
     * 
     * @return 慢性病病历列表
     */
    List<MedicalRecord> getChronicDiseaseRecords();

    /**
     * 添加诊断
     * 
     * @param recordId 病历ID
     * @param diagnosis 诊断信息
     * @return 添加的诊断
     */
    Diagnosis addDiagnosis(Long recordId, Diagnosis diagnosis);

    /**
     * 更新诊断
     * 
     * @param diagnosisId 诊断ID
     * @param diagnosis 诊断信息
     * @return 更新后的诊断
     */
    Diagnosis updateDiagnosis(Long diagnosisId, Diagnosis diagnosis);

    /**
     * 删除诊断
     * 
     * @param diagnosisId 诊断ID
     */
    void deleteDiagnosis(Long diagnosisId);

    /**
     * 查询病历的诊断列表
     * 
     * @param recordId 病历ID
     * @return 诊断列表
     */
    List<Diagnosis> getMedicalRecordDiagnoses(Long recordId);

    /**
     * 添加处方
     * 
     * @param recordId 病历ID
     * @param prescription 处方信息
     * @return 添加的处方
     */
    Prescription addPrescription(Long recordId, Prescription prescription);

    /**
     * 更新处方
     * 
     * @param prescriptionId 处方ID
     * @param prescription 处方信息
     * @return 更新后的处方
     */
    Prescription updatePrescription(Long prescriptionId, Prescription prescription);

    /**
     * 删除处方
     * 
     * @param prescriptionId 处方ID
     */
    void deletePrescription(Long prescriptionId);

    /**
     * 查询病历的处方列表
     * 
     * @param recordId 病历ID
     * @return 处方列表
     */
    List<Prescription> getMedicalRecordPrescriptions(Long recordId);

    /**
     * 提交病历审核
     * 
     * @param recordId 病历ID
     */
    void submitForReview(Long recordId);

    /**
     * 审核病历
     * 
     * @param recordId 病历ID
     * @param reviewDoctorId 审核医生ID
     * @param approved 是否通过审核
     * @param comments 审核意见
     */
    void reviewMedicalRecord(Long recordId, Long reviewDoctorId, boolean approved, String comments);

    /**
     * 归档病历
     * 
     * @param recordId 病历ID
     */
    void archiveMedicalRecord(Long recordId);

    /**
     * 删除病历
     * 
     * @param recordId 病历ID
     */
    void deleteMedicalRecord(Long recordId);

    /**
     * 统计患者的病历数量
     * 
     * @param patientId 患者ID
     * @return 病历数量
     */
    int countPatientMedicalRecords(Long patientId);

    /**
     * 统计医生的病历数量
     * 
     * @param doctorId 医生ID
     * @return 病历数量
     */
    int countDoctorMedicalRecords(Long doctorId);

    /**
     * 检查病历编号是否存在
     * 
     * @param recordNumber 病历编号
     * @return 是否存在
     */
    boolean existsByRecordNumber(String recordNumber);

    /**
     * 生成病历编号
     * 
     * @param patientId 患者ID
     * @param doctorId 医生ID
     * @return 病历编号
     */
    String generateRecordNumber(Long patientId, Long doctorId);
}