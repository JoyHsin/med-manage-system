package org.me.joy.clinic.service.impl;

import org.me.joy.clinic.dto.CreatePatientRequest;
import org.me.joy.clinic.dto.PatientResponse;
import org.me.joy.clinic.dto.UpdatePatientRequest;
import org.me.joy.clinic.entity.AllergyHistory;
import org.me.joy.clinic.entity.MedicalHistory;
import org.me.joy.clinic.entity.Patient;
import org.me.joy.clinic.exception.BusinessException;
import org.me.joy.clinic.exception.ValidationException;
import org.me.joy.clinic.mapper.AllergyHistoryMapper;
import org.me.joy.clinic.mapper.MedicalHistoryMapper;
import org.me.joy.clinic.mapper.PatientMapper;
import org.me.joy.clinic.service.PatientManagementService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 患者管理服务实现类
 */
@Service
@Transactional
public class PatientManagementServiceImpl implements PatientManagementService {

    private static final Logger logger = LoggerFactory.getLogger(PatientManagementServiceImpl.class);

    @Autowired
    private PatientMapper patientMapper;

    @Autowired
    private AllergyHistoryMapper allergyHistoryMapper;

    @Autowired
    private MedicalHistoryMapper medicalHistoryMapper;

    @Override
    public PatientResponse createPatient(CreatePatientRequest createPatientRequest) {
        logger.info("创建新患者: {}", createPatientRequest.getName());

        // 验证身份证号是否已存在
        if (StringUtils.hasText(createPatientRequest.getIdCard()) && 
            existsByIdCard(createPatientRequest.getIdCard())) {
            throw new ValidationException("PATIENT_ID_CARD_EXISTS", "身份证号已存在");
        }

        // 生成患者编号
        String patientNumber = generatePatientNumber();

        // 创建患者实体
        Patient patient = new Patient();
        patient.setPatientNumber(patientNumber);
        patient.setName(createPatientRequest.getName());
        patient.setPhone(createPatientRequest.getPhone());
        patient.setIdCard(createPatientRequest.getIdCard());
        patient.setBirthDate(createPatientRequest.getBirthDate());
        patient.setGender(createPatientRequest.getGender());
        patient.setAddress(createPatientRequest.getAddress());
        patient.setEmergencyContactName(createPatientRequest.getEmergencyContactName());
        patient.setEmergencyContactPhone(createPatientRequest.getEmergencyContactPhone());
        patient.setEmergencyContactRelation(createPatientRequest.getEmergencyContactRelation());
        patient.setBloodType(createPatientRequest.getBloodType());
        patient.setMaritalStatus(createPatientRequest.getMaritalStatus());
        patient.setOccupation(createPatientRequest.getOccupation());
        patient.setEthnicity(createPatientRequest.getEthnicity());
        patient.setInsuranceType(createPatientRequest.getInsuranceType());
        patient.setInsuranceNumber(createPatientRequest.getInsuranceNumber());
        patient.setRemarks(createPatientRequest.getRemarks());
        patient.setIsVip(createPatientRequest.getIsVip() != null ? createPatientRequest.getIsVip() : false);
        patient.setStatus("正常");

        // 保存患者
        patientMapper.insert(patient);

        logger.info("患者创建成功: {}, ID: {}", patient.getName(), patient.getId());

        return convertToPatientResponse(patient);
    }

    @Override
    public PatientResponse updatePatient(Long patientId, UpdatePatientRequest updatePatientRequest) {
        logger.info("更新患者信息: {}", patientId);

        Patient patient = getPatientEntityById(patientId);

        // 验证身份证号是否已被其他患者使用
        if (StringUtils.hasText(updatePatientRequest.getIdCard()) && 
            !updatePatientRequest.getIdCard().equals(patient.getIdCard())) {
            if (existsByIdCard(updatePatientRequest.getIdCard())) {
                throw new ValidationException("PATIENT_ID_CARD_EXISTS", "身份证号已存在");
            }
        }

        // 更新患者信息
        if (StringUtils.hasText(updatePatientRequest.getName())) {
            patient.setName(updatePatientRequest.getName());
        }
        if (StringUtils.hasText(updatePatientRequest.getPhone())) {
            patient.setPhone(updatePatientRequest.getPhone());
        }
        if (StringUtils.hasText(updatePatientRequest.getIdCard())) {
            patient.setIdCard(updatePatientRequest.getIdCard());
        }
        if (updatePatientRequest.getBirthDate() != null) {
            patient.setBirthDate(updatePatientRequest.getBirthDate());
        }
        if (StringUtils.hasText(updatePatientRequest.getGender())) {
            patient.setGender(updatePatientRequest.getGender());
        }
        if (updatePatientRequest.getAddress() != null) {
            patient.setAddress(updatePatientRequest.getAddress());
        }
        if (updatePatientRequest.getEmergencyContactName() != null) {
            patient.setEmergencyContactName(updatePatientRequest.getEmergencyContactName());
        }
        if (updatePatientRequest.getEmergencyContactPhone() != null) {
            patient.setEmergencyContactPhone(updatePatientRequest.getEmergencyContactPhone());
        }
        if (updatePatientRequest.getEmergencyContactRelation() != null) {
            patient.setEmergencyContactRelation(updatePatientRequest.getEmergencyContactRelation());
        }
        if (updatePatientRequest.getBloodType() != null) {
            patient.setBloodType(updatePatientRequest.getBloodType());
        }
        if (updatePatientRequest.getMaritalStatus() != null) {
            patient.setMaritalStatus(updatePatientRequest.getMaritalStatus());
        }
        if (updatePatientRequest.getOccupation() != null) {
            patient.setOccupation(updatePatientRequest.getOccupation());
        }
        if (updatePatientRequest.getEthnicity() != null) {
            patient.setEthnicity(updatePatientRequest.getEthnicity());
        }
        if (updatePatientRequest.getInsuranceType() != null) {
            patient.setInsuranceType(updatePatientRequest.getInsuranceType());
        }
        if (updatePatientRequest.getInsuranceNumber() != null) {
            patient.setInsuranceNumber(updatePatientRequest.getInsuranceNumber());
        }
        if (updatePatientRequest.getRemarks() != null) {
            patient.setRemarks(updatePatientRequest.getRemarks());
        }
        if (updatePatientRequest.getIsVip() != null) {
            patient.setIsVip(updatePatientRequest.getIsVip());
        }
        if (StringUtils.hasText(updatePatientRequest.getStatus())) {
            patient.setStatus(updatePatientRequest.getStatus());
        }

        patientMapper.updateById(patient);

        logger.info("患者信息更新成功: {}", patientId);

        return convertToPatientResponse(patient);
    }

    @Override
    @Transactional(readOnly = true)
    public PatientResponse getPatientById(Long patientId) {
        Patient patient = getPatientEntityById(patientId);
        return convertToPatientResponse(patient);
    }

    @Override
    @Transactional(readOnly = true)
    public PatientResponse getPatientByNumber(String patientNumber) {
        if (!StringUtils.hasText(patientNumber)) {
            throw new ValidationException("PATIENT_NUMBER_EMPTY", "患者编号不能为空");
        }

        Optional<Patient> patientOpt = patientMapper.findByPatientNumber(patientNumber);
        if (patientOpt.isEmpty()) {
            throw new BusinessException("PATIENT_NOT_FOUND", "患者不存在");
        }

        return convertToPatientResponse(patientOpt.get());
    }

    @Override
    @Transactional(readOnly = true)
    public PatientResponse getPatientByIdCard(String idCard) {
        if (!StringUtils.hasText(idCard)) {
            throw new ValidationException("ID_CARD_EMPTY", "身份证号不能为空");
        }

        Optional<Patient> patientOpt = patientMapper.findByIdCard(idCard);
        if (patientOpt.isEmpty()) {
            throw new BusinessException("PATIENT_NOT_FOUND", "患者不存在");
        }

        return convertToPatientResponse(patientOpt.get());
    }

    @Override
    @Transactional(readOnly = true)
    public List<PatientResponse> getAllPatients() {
        List<Patient> patients = patientMapper.selectList(null);
        return patients.stream()
                .map(this::convertToPatientResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<PatientResponse> getPatientsByStatus(String status) {
        if (!StringUtils.hasText(status)) {
            throw new ValidationException("STATUS_EMPTY", "患者状态不能为空");
        }

        List<Patient> patients = patientMapper.findByStatus(status);
        return patients.stream()
                .map(this::convertToPatientResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<PatientResponse> getVipPatients() {
        List<Patient> patients = patientMapper.findVipPatients();
        return patients.stream()
                .map(this::convertToPatientResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<PatientResponse> getPatientsByGender(String gender) {
        if (!StringUtils.hasText(gender)) {
            throw new ValidationException("GENDER_EMPTY", "性别不能为空");
        }

        List<Patient> patients = patientMapper.findByGender(gender);
        return patients.stream()
                .map(this::convertToPatientResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<PatientResponse> getPatientsByAgeRange(Integer minAge, Integer maxAge) {
        if (minAge == null || maxAge == null) {
            throw new ValidationException("AGE_RANGE_INVALID", "年龄范围不能为空");
        }
        if (minAge < 0 || maxAge < 0 || minAge > maxAge) {
            throw new ValidationException("AGE_RANGE_INVALID", "年龄范围无效");
        }

        List<Patient> patients = patientMapper.findByAgeRange(minAge, maxAge);
        return patients.stream()
                .map(this::convertToPatientResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<PatientResponse> searchPatients(String keyword) {
        if (!StringUtils.hasText(keyword)) {
            return getAllPatients();
        }

        List<Patient> patients = patientMapper.searchPatients(keyword.trim());
        return patients.stream()
                .map(this::convertToPatientResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<PatientResponse> getTodayPatients(LocalDate date) {
        if (date == null) {
            date = LocalDate.now();
        }

        List<Patient> patients = patientMapper.findTodayPatients(date);
        return patients.stream()
                .map(this::convertToPatientResponse)
                .collect(Collectors.toList());
    }

    @Override
    public void deletePatient(Long patientId) {
        logger.info("删除患者: {}", patientId);

        Patient patient = getPatientEntityById(patientId);

        // 软删除
        patientMapper.deleteById(patientId);

        logger.info("患者删除成功: {}", patientId);
    }

    @Override
    public void setPatientAsVip(Long patientId) {
        logger.info("设置患者为VIP: {}", patientId);

        Patient patient = getPatientEntityById(patientId);
        patient.setIsVip(true);
        patientMapper.updateById(patient);

        logger.info("患者VIP设置成功: {}", patientId);
    }

    @Override
    public void removePatientVipStatus(Long patientId) {
        logger.info("取消患者VIP状态: {}", patientId);

        Patient patient = getPatientEntityById(patientId);
        patient.setIsVip(false);
        patientMapper.updateById(patient);

        logger.info("患者VIP状态取消成功: {}", patientId);
    }

    @Override
    public void updatePatientStatus(Long patientId, String status) {
        if (!StringUtils.hasText(status)) {
            throw new ValidationException("STATUS_EMPTY", "患者状态不能为空");
        }

        logger.info("更新患者状态: patientId={}, status={}", patientId, status);

        Patient patient = getPatientEntityById(patientId);
        patient.setStatus(status);
        patientMapper.updateById(patient);

        logger.info("患者状态更新成功: patientId={}, status={}", patientId, status);
    }

    @Override
    public void recordPatientVisit(Long patientId) {
        logger.info("记录患者就诊: {}", patientId);

        Patient patient = getPatientEntityById(patientId);
        patient.incrementVisitCount();
        patientMapper.updateById(patient);

        logger.info("患者就诊记录成功: {}", patientId);
    }   
 @Override
    public AllergyHistory addAllergyHistory(Long patientId, AllergyHistory allergyHistory) {
        logger.info("添加患者过敏史: patientId={}, allergen={}", patientId, allergyHistory.getAllergen());

        // 验证患者存在
        getPatientEntityById(patientId);

        // 设置患者ID和记录时间
        allergyHistory.setPatientId(patientId);
        allergyHistory.setRecordedTime(LocalDateTime.now());

        // 保存过敏史
        allergyHistoryMapper.insert(allergyHistory);

        logger.info("患者过敏史添加成功: patientId={}, allergyHistoryId={}", patientId, allergyHistory.getId());

        return allergyHistory;
    }

    @Override
    public AllergyHistory updateAllergyHistory(Long allergyHistoryId, AllergyHistory allergyHistory) {
        logger.info("更新患者过敏史: {}", allergyHistoryId);

        AllergyHistory existingHistory = allergyHistoryMapper.selectById(allergyHistoryId);
        if (existingHistory == null) {
            throw new BusinessException("ALLERGY_HISTORY_NOT_FOUND", "过敏史不存在");
        }

        // 更新过敏史信息
        if (StringUtils.hasText(allergyHistory.getAllergen())) {
            existingHistory.setAllergen(allergyHistory.getAllergen());
        }
        if (StringUtils.hasText(allergyHistory.getAllergenType())) {
            existingHistory.setAllergenType(allergyHistory.getAllergenType());
        }
        if (StringUtils.hasText(allergyHistory.getSymptoms())) {
            existingHistory.setSymptoms(allergyHistory.getSymptoms());
        }
        if (StringUtils.hasText(allergyHistory.getSeverity())) {
            existingHistory.setSeverity(allergyHistory.getSeverity());
        }
        if (allergyHistory.getFirstDiscoveredTime() != null) {
            existingHistory.setFirstDiscoveredTime(allergyHistory.getFirstDiscoveredTime());
        }
        if (allergyHistory.getLastOccurrenceTime() != null) {
            existingHistory.setLastOccurrenceTime(allergyHistory.getLastOccurrenceTime());
        }
        if (allergyHistory.getTreatment() != null) {
            existingHistory.setTreatment(allergyHistory.getTreatment());
        }
        if (allergyHistory.getRemarks() != null) {
            existingHistory.setRemarks(allergyHistory.getRemarks());
        }
        if (allergyHistory.getIsConfirmed() != null) {
            existingHistory.setIsConfirmed(allergyHistory.getIsConfirmed());
        }

        allergyHistoryMapper.updateById(existingHistory);

        logger.info("患者过敏史更新成功: {}", allergyHistoryId);

        return existingHistory;
    }

    @Override
    public void deleteAllergyHistory(Long allergyHistoryId) {
        logger.info("删除患者过敏史: {}", allergyHistoryId);

        AllergyHistory existingHistory = allergyHistoryMapper.selectById(allergyHistoryId);
        if (existingHistory == null) {
            throw new BusinessException("ALLERGY_HISTORY_NOT_FOUND", "过敏史不存在");
        }

        allergyHistoryMapper.deleteById(allergyHistoryId);

        logger.info("患者过敏史删除成功: {}", allergyHistoryId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AllergyHistory> getPatientAllergyHistories(Long patientId) {
        // 验证患者存在
        getPatientEntityById(patientId);

        return allergyHistoryMapper.findByPatientId(patientId);
    }

    @Override
    public MedicalHistory addMedicalHistory(Long patientId, MedicalHistory medicalHistory) {
        logger.info("添加患者病史: patientId={}, diseaseName={}", patientId, medicalHistory.getDiseaseName());

        // 验证患者存在
        getPatientEntityById(patientId);

        // 设置患者ID和记录时间
        medicalHistory.setPatientId(patientId);
        medicalHistory.setRecordedTime(LocalDateTime.now());

        // 保存病史
        medicalHistoryMapper.insert(medicalHistory);

        logger.info("患者病史添加成功: patientId={}, medicalHistoryId={}", patientId, medicalHistory.getId());

        return medicalHistory;
    }

    @Override
    public MedicalHistory updateMedicalHistory(Long medicalHistoryId, MedicalHistory medicalHistory) {
        logger.info("更新患者病史: {}", medicalHistoryId);

        MedicalHistory existingHistory = medicalHistoryMapper.selectById(medicalHistoryId);
        if (existingHistory == null) {
            throw new BusinessException("MEDICAL_HISTORY_NOT_FOUND", "病史不存在");
        }

        // 更新病史信息
        if (StringUtils.hasText(medicalHistory.getHistoryType())) {
            existingHistory.setHistoryType(medicalHistory.getHistoryType());
        }
        if (StringUtils.hasText(medicalHistory.getDiseaseName())) {
            existingHistory.setDiseaseName(medicalHistory.getDiseaseName());
        }
        if (medicalHistory.getDiseaseCategory() != null) {
            existingHistory.setDiseaseCategory(medicalHistory.getDiseaseCategory());
        }
        if (medicalHistory.getOnsetDate() != null) {
            existingHistory.setOnsetDate(medicalHistory.getOnsetDate());
        }
        if (medicalHistory.getDiagnosisDate() != null) {
            existingHistory.setDiagnosisDate(medicalHistory.getDiagnosisDate());
        }
        if (medicalHistory.getTreatmentStatus() != null) {
            existingHistory.setTreatmentStatus(medicalHistory.getTreatmentStatus());
        }
        if (medicalHistory.getTreatmentResult() != null) {
            existingHistory.setTreatmentResult(medicalHistory.getTreatmentResult());
        }
        if (medicalHistory.getHospital() != null) {
            existingHistory.setHospital(medicalHistory.getHospital());
        }
        if (medicalHistory.getDoctor() != null) {
            existingHistory.setDoctor(medicalHistory.getDoctor());
        }
        if (medicalHistory.getDescription() != null) {
            existingHistory.setDescription(medicalHistory.getDescription());
        }
        if (medicalHistory.getExamResults() != null) {
            existingHistory.setExamResults(medicalHistory.getExamResults());
        }
        if (medicalHistory.getMedications() != null) {
            existingHistory.setMedications(medicalHistory.getMedications());
        }
        if (medicalHistory.getFamilyRelation() != null) {
            existingHistory.setFamilyRelation(medicalHistory.getFamilyRelation());
        }
        if (medicalHistory.getIsHereditary() != null) {
            existingHistory.setIsHereditary(medicalHistory.getIsHereditary());
        }
        if (medicalHistory.getIsChronic() != null) {
            existingHistory.setIsChronic(medicalHistory.getIsChronic());
        }
        if (medicalHistory.getIsContagious() != null) {
            existingHistory.setIsContagious(medicalHistory.getIsContagious());
        }
        if (medicalHistory.getSeverity() != null) {
            existingHistory.setSeverity(medicalHistory.getSeverity());
        }
        if (medicalHistory.getCurrentStatus() != null) {
            existingHistory.setCurrentStatus(medicalHistory.getCurrentStatus());
        }
        if (medicalHistory.getRemarks() != null) {
            existingHistory.setRemarks(medicalHistory.getRemarks());
        }

        medicalHistoryMapper.updateById(existingHistory);

        logger.info("患者病史更新成功: {}", medicalHistoryId);

        return existingHistory;
    }

    @Override
    public void deleteMedicalHistory(Long medicalHistoryId) {
        logger.info("删除患者病史: {}", medicalHistoryId);

        MedicalHistory existingHistory = medicalHistoryMapper.selectById(medicalHistoryId);
        if (existingHistory == null) {
            throw new BusinessException("MEDICAL_HISTORY_NOT_FOUND", "病史不存在");
        }

        medicalHistoryMapper.deleteById(medicalHistoryId);

        logger.info("患者病史删除成功: {}", medicalHistoryId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MedicalHistory> getPatientMedicalHistories(Long patientId) {
        // 验证患者存在
        getPatientEntityById(patientId);

        return medicalHistoryMapper.findByPatientId(patientId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MedicalHistory> getPatientMedicalHistoriesByType(Long patientId, String historyType) {
        if (!StringUtils.hasText(historyType)) {
            throw new ValidationException("HISTORY_TYPE_EMPTY", "病史类型不能为空");
        }

        // 验证患者存在
        getPatientEntityById(patientId);

        return medicalHistoryMapper.findByPatientIdAndHistoryType(patientId, historyType);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByPatientNumber(String patientNumber) {
        if (!StringUtils.hasText(patientNumber)) {
            return false;
        }
        return patientMapper.findByPatientNumber(patientNumber).isPresent();
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByIdCard(String idCard) {
        if (!StringUtils.hasText(idCard)) {
            return false;
        }
        return patientMapper.findByIdCard(idCard).isPresent();
    }

    @Override
    public String generatePatientNumber() {
        // 生成格式：P + 年月日 + 4位序号，例如：P202401010001
        String datePrefix = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String prefix = "P" + datePrefix;
        
        // 查询当日最大序号
        int sequence = 1;
        String patientNumber;
        do {
            patientNumber = prefix + String.format("%04d", sequence);
            sequence++;
        } while (existsByPatientNumber(patientNumber));
        
        return patientNumber;
    }

    @Override
    @Transactional(readOnly = true)
    public Long countAllPatients() {
        return patientMapper.countAllPatients();
    }

    @Override
    @Transactional(readOnly = true)
    public Long countPatientsByStatus(String status) {
        if (!StringUtils.hasText(status)) {
            throw new ValidationException("STATUS_EMPTY", "患者状态不能为空");
        }
        return patientMapper.countPatientsByStatus(status);
    }

    @Override
    @Transactional(readOnly = true)
    public Long countVipPatients() {
        return patientMapper.countVipPatients();
    }

    /**
     * 根据ID获取患者实体
     */
    private Patient getPatientEntityById(Long patientId) {
        if (patientId == null) {
            throw new ValidationException("PATIENT_ID_NULL", "患者ID不能为空");
        }

        Patient patient = patientMapper.selectById(patientId);
        if (patient == null) {
            throw new BusinessException("PATIENT_NOT_FOUND", "患者不存在");
        }
        return patient;
    }

    /**
     * 将患者实体转换为响应DTO
     */
    private PatientResponse convertToPatientResponse(Patient patient) {
        PatientResponse response = new PatientResponse();
        response.setId(patient.getId());
        response.setPatientNumber(patient.getPatientNumber());
        response.setName(patient.getName());
        response.setPhone(patient.getPhone());
        response.setIdCard(patient.getIdCard());
        response.setBirthDate(patient.getBirthDate());
        response.setAge(patient.getAge());
        response.setGender(patient.getGender());
        response.setAddress(patient.getAddress());
        response.setEmergencyContactName(patient.getEmergencyContactName());
        response.setEmergencyContactPhone(patient.getEmergencyContactPhone());
        response.setEmergencyContactRelation(patient.getEmergencyContactRelation());
        response.setBloodType(patient.getBloodType());
        response.setMaritalStatus(patient.getMaritalStatus());
        response.setOccupation(patient.getOccupation());
        response.setEthnicity(patient.getEthnicity());
        response.setInsuranceType(patient.getInsuranceType());
        response.setInsuranceNumber(patient.getInsuranceNumber());
        response.setRemarks(patient.getRemarks());
        response.setIsVip(patient.getIsVip());
        response.setStatus(patient.getStatus());
        response.setFirstVisitTime(patient.getFirstVisitTime());
        response.setLastVisitTime(patient.getLastVisitTime());
        response.setVisitCount(patient.getVisitCount());
        response.setCreatedAt(patient.getCreatedAt());
        response.setUpdatedAt(patient.getUpdatedAt());

        // 获取过敏史和病史
        try {
            List<AllergyHistory> allergyHistories = allergyHistoryMapper.findByPatientId(patient.getId());
            response.setAllergyHistories(allergyHistories);

            List<MedicalHistory> medicalHistories = medicalHistoryMapper.findByPatientId(patient.getId());
            response.setMedicalHistories(medicalHistories);
        } catch (Exception e) {
            logger.warn("获取患者历史记录失败: patientId={}", patient.getId(), e);
        }

        return response;
    }
}