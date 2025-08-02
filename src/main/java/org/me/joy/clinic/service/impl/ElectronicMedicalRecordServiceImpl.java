package org.me.joy.clinic.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.me.joy.clinic.dto.CreateMedicalRecordRequest;
import org.me.joy.clinic.dto.UpdateMedicalRecordRequest;
import org.me.joy.clinic.entity.Diagnosis;
import org.me.joy.clinic.entity.MedicalRecord;
import org.me.joy.clinic.entity.Prescription;
import org.me.joy.clinic.exception.BusinessException;
import org.me.joy.clinic.exception.ValidationException;
import org.me.joy.clinic.mapper.DiagnosisMapper;
import org.me.joy.clinic.mapper.MedicalRecordMapper;
import org.me.joy.clinic.mapper.PrescriptionMapper;
import org.me.joy.clinic.service.ElectronicMedicalRecordService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * 电子病历服务实现类
 */
@Service
@Transactional
public class ElectronicMedicalRecordServiceImpl implements ElectronicMedicalRecordService {

    @Autowired
    private MedicalRecordMapper medicalRecordMapper;

    @Autowired
    private DiagnosisMapper diagnosisMapper;

    @Autowired
    private PrescriptionMapper prescriptionMapper;

    @Override
    public MedicalRecord createMedicalRecord(CreateMedicalRecordRequest request) {
        // 验证请求参数
        validateCreateRequest(request);

        // 生成病历编号
        String recordNumber = generateRecordNumber(request.getPatientId(), request.getDoctorId());

        // 创建病历实体
        MedicalRecord medicalRecord = new MedicalRecord();
        BeanUtils.copyProperties(request, medicalRecord);
        medicalRecord.setRecordNumber(recordNumber);
        medicalRecord.setCreatedBy(request.getDoctorId());
        medicalRecord.setLastUpdatedBy(request.getDoctorId());
        
        if (request.getRecordDate() == null) {
            medicalRecord.setRecordDate(LocalDateTime.now());
        }

        // 保存病历
        medicalRecordMapper.insert(medicalRecord);

        // 生成病历摘要
        medicalRecord.generateSummary();
        medicalRecordMapper.updateById(medicalRecord);

        return medicalRecord;
    }

    @Override
    public MedicalRecord updateMedicalRecord(Long recordId, UpdateMedicalRecordRequest request) {
        // 查询病历
        MedicalRecord medicalRecord = getMedicalRecordById(recordId);

        // 检查病历是否可以编辑
        if (!medicalRecord.canBeEdited()) {
            throw new BusinessException("3001", "病历状态为" + medicalRecord.getStatus() + "，不允许编辑");
        }

        // 更新病历信息
        if (request.getChiefComplaint() != null) {
            medicalRecord.setChiefComplaint(request.getChiefComplaint());
        }
        if (request.getPresentIllness() != null) {
            medicalRecord.setPresentIllness(request.getPresentIllness());
        }
        if (request.getPastHistory() != null) {
            medicalRecord.setPastHistory(request.getPastHistory());
        }
        if (request.getPersonalHistory() != null) {
            medicalRecord.setPersonalHistory(request.getPersonalHistory());
        }
        if (request.getFamilyHistory() != null) {
            medicalRecord.setFamilyHistory(request.getFamilyHistory());
        }
        if (request.getPhysicalExamination() != null) {
            medicalRecord.setPhysicalExamination(request.getPhysicalExamination());
        }
        if (request.getAuxiliaryExamination() != null) {
            medicalRecord.setAuxiliaryExamination(request.getAuxiliaryExamination());
        }
        if (request.getPreliminaryDiagnosis() != null) {
            medicalRecord.setPreliminaryDiagnosis(request.getPreliminaryDiagnosis());
        }
        if (request.getFinalDiagnosis() != null) {
            medicalRecord.setFinalDiagnosis(request.getFinalDiagnosis());
        }
        if (request.getTreatmentPlan() != null) {
            medicalRecord.setTreatmentPlan(request.getTreatmentPlan());
        }
        if (request.getMedicalOrders() != null) {
            medicalRecord.setMedicalOrders(request.getMedicalOrders());
        }
        if (request.getConditionAssessment() != null) {
            medicalRecord.setConditionAssessment(request.getConditionAssessment());
        }
        if (request.getPrognosis() != null) {
            medicalRecord.setPrognosis(request.getPrognosis());
        }
        if (request.getFollowUpAdvice() != null) {
            medicalRecord.setFollowUpAdvice(request.getFollowUpAdvice());
        }
        if (request.getDepartment() != null) {
            medicalRecord.setDepartment(request.getDepartment());
        }
        if (request.getIsInfectious() != null) {
            medicalRecord.setIsInfectious(request.getIsInfectious());
        }
        if (request.getIsChronicDisease() != null) {
            medicalRecord.setIsChronicDisease(request.getIsChronicDisease());
        }
        if (request.getRemarks() != null) {
            medicalRecord.setRemarks(request.getRemarks());
        }

        // 更新最后修改信息
        medicalRecord.setUpdatedAt(LocalDateTime.now());

        // 重新生成病历摘要
        medicalRecord.generateSummary();

        // 保存更新
        medicalRecordMapper.updateById(medicalRecord);

        return medicalRecord;
    }

    @Override
    public MedicalRecord getMedicalRecordById(Long recordId) {
        if (recordId == null) {
            throw new ValidationException("3002", "病历ID不能为空");
        }

        MedicalRecord medicalRecord = medicalRecordMapper.selectById(recordId);
        if (medicalRecord == null) {
            throw new BusinessException("3003", "病历不存在");
        }

        // 加载关联的诊断和处方信息
        loadRelatedData(medicalRecord);

        return medicalRecord;
    }

    @Override
    public MedicalRecord getMedicalRecordByNumber(String recordNumber) {
        if (recordNumber == null || recordNumber.trim().isEmpty()) {
            throw new ValidationException("3004", "病历编号不能为空");
        }

        MedicalRecord medicalRecord = medicalRecordMapper.findByRecordNumber(recordNumber);
        if (medicalRecord == null) {
            throw new BusinessException("3005", "病历不存在");
        }

        // 加载关联的诊断和处方信息
        loadRelatedData(medicalRecord);

        return medicalRecord;
    }

    @Override
    public List<MedicalRecord> getPatientMedicalRecords(Long patientId) {
        if (patientId == null) {
            throw new ValidationException("3006", "患者ID不能为空");
        }

        List<MedicalRecord> records = medicalRecordMapper.findByPatientId(patientId);
        
        // 为每个病历加载关联数据
        records.forEach(this::loadRelatedData);

        return records;
    }

    @Override
    public List<MedicalRecord> getDoctorMedicalRecords(Long doctorId) {
        if (doctorId == null) {
            throw new ValidationException("3007", "医生ID不能为空");
        }

        List<MedicalRecord> records = medicalRecordMapper.findByDoctorId(doctorId);
        
        // 为每个病历加载关联数据
        records.forEach(this::loadRelatedData);

        return records;
    }

    @Override
    public MedicalRecord getMedicalRecordByRegistrationId(Long registrationId) {
        if (registrationId == null) {
            throw new ValidationException("3008", "挂号ID不能为空");
        }

        MedicalRecord medicalRecord = medicalRecordMapper.findByRegistrationId(registrationId);
        if (medicalRecord == null) {
            throw new BusinessException("3009", "该挂号记录对应的病历不存在");
        }

        // 加载关联的诊断和处方信息
        loadRelatedData(medicalRecord);

        return medicalRecord;
    }

    @Override
    public List<MedicalRecord> getMedicalRecordsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        if (startDate == null || endDate == null) {
            throw new ValidationException("3010", "开始时间和结束时间不能为空");
        }

        if (startDate.isAfter(endDate)) {
            throw new ValidationException("3011", "开始时间不能晚于结束时间");
        }

        List<MedicalRecord> records = medicalRecordMapper.findByDateRange(startDate, endDate);
        
        // 为每个病历加载关联数据
        records.forEach(this::loadRelatedData);

        return records;
    }

    @Override
    public List<MedicalRecord> getPatientMedicalRecordsByDateRange(Long patientId, LocalDateTime startDate, LocalDateTime endDate) {
        if (patientId == null) {
            throw new ValidationException("3012", "患者ID不能为空");
        }
        if (startDate == null || endDate == null) {
            throw new ValidationException("3013", "开始时间和结束时间不能为空");
        }

        if (startDate.isAfter(endDate)) {
            throw new ValidationException("3014", "开始时间不能晚于结束时间");
        }

        List<MedicalRecord> records = medicalRecordMapper.findByPatientIdAndDateRange(patientId, startDate, endDate);
        
        // 为每个病历加载关联数据
        records.forEach(this::loadRelatedData);

        return records;
    }

    @Override
    public List<MedicalRecord> getMedicalRecordsByStatus(String status) {
        if (status == null || status.trim().isEmpty()) {
            throw new ValidationException("3015", "病历状态不能为空");
        }

        List<MedicalRecord> records = medicalRecordMapper.findByStatus(status);
        
        // 为每个病历加载关联数据
        records.forEach(this::loadRelatedData);

        return records;
    }

    @Override
    public List<MedicalRecord> getPendingReviewRecords() {
        List<MedicalRecord> records = medicalRecordMapper.findPendingReview();
        
        // 为每个病历加载关联数据
        records.forEach(this::loadRelatedData);

        return records;
    }

    @Override
    public List<MedicalRecord> getInfectiousDiseaseRecords() {
        List<MedicalRecord> records = medicalRecordMapper.findInfectiousDiseaseRecords();
        
        // 为每个病历加载关联数据
        records.forEach(this::loadRelatedData);

        return records;
    }

    @Override
    public List<MedicalRecord> getChronicDiseaseRecords() {
        List<MedicalRecord> records = medicalRecordMapper.findChronicDiseaseRecords();
        
        // 为每个病历加载关联数据
        records.forEach(this::loadRelatedData);

        return records;
    }

    @Override
    public Diagnosis addDiagnosis(Long recordId, Diagnosis diagnosis) {
        // 验证病历是否存在
        MedicalRecord medicalRecord = getMedicalRecordById(recordId);

        // 检查病历是否可以编辑
        if (!medicalRecord.canBeEdited()) {
            throw new BusinessException("3016", "病历状态为" + medicalRecord.getStatus() + "，不允许添加诊断");
        }

        // 设置诊断信息
        diagnosis.setMedicalRecordId(recordId);
        if (diagnosis.getDiagnosisTime() == null) {
            diagnosis.setDiagnosisTime(LocalDateTime.now());
        }

        // 保存诊断
        diagnosisMapper.insert(diagnosis);

        return diagnosis;
    }

    @Override
    public Diagnosis updateDiagnosis(Long diagnosisId, Diagnosis diagnosis) {
        if (diagnosisId == null) {
            throw new ValidationException("3017", "诊断ID不能为空");
        }

        // 查询现有诊断
        Diagnosis existingDiagnosis = diagnosisMapper.selectById(diagnosisId);
        if (existingDiagnosis == null) {
            throw new BusinessException("3018", "诊断不存在");
        }

        // 检查关联的病历是否可以编辑
        MedicalRecord medicalRecord = getMedicalRecordById(existingDiagnosis.getMedicalRecordId());
        if (!medicalRecord.canBeEdited()) {
            throw new BusinessException("3019", "病历状态为" + medicalRecord.getStatus() + "，不允许修改诊断");
        }

        // 更新诊断信息
        diagnosis.setId(diagnosisId);
        diagnosis.setMedicalRecordId(existingDiagnosis.getMedicalRecordId());
        diagnosisMapper.updateById(diagnosis);

        return diagnosis;
    }

    @Override
    public void deleteDiagnosis(Long diagnosisId) {
        if (diagnosisId == null) {
            throw new ValidationException("3020", "诊断ID不能为空");
        }

        // 查询现有诊断
        Diagnosis existingDiagnosis = diagnosisMapper.selectById(diagnosisId);
        if (existingDiagnosis == null) {
            throw new BusinessException("3021", "诊断不存在");
        }

        // 检查关联的病历是否可以编辑
        MedicalRecord medicalRecord = getMedicalRecordById(existingDiagnosis.getMedicalRecordId());
        if (!medicalRecord.canBeEdited()) {
            throw new BusinessException("3022", "病历状态为" + medicalRecord.getStatus() + "，不允许删除诊断");
        }

        // 删除诊断
        diagnosisMapper.deleteById(diagnosisId);
    }

    @Override
    public List<Diagnosis> getMedicalRecordDiagnoses(Long recordId) {
        if (recordId == null) {
            throw new ValidationException("3023", "病历ID不能为空");
        }

        QueryWrapper<Diagnosis> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("medical_record_id", recordId)
                   .orderByAsc("sort_order", "id");

        return diagnosisMapper.selectList(queryWrapper);
    }

    @Override
    public Prescription addPrescription(Long recordId, Prescription prescription) {
        // 验证病历是否存在
        MedicalRecord medicalRecord = getMedicalRecordById(recordId);

        // 检查病历是否可以编辑
        if (!medicalRecord.canBeEdited()) {
            throw new BusinessException("3024", "病历状态为" + medicalRecord.getStatus() + "，不允许添加处方");
        }

        // 设置处方信息
        prescription.setMedicalRecordId(recordId);
        if (prescription.getPrescribedAt() == null) {
            prescription.setPrescribedAt(LocalDateTime.now());
        }

        // 保存处方
        prescriptionMapper.insert(prescription);

        return prescription;
    }

    @Override
    public Prescription updatePrescription(Long prescriptionId, Prescription prescription) {
        if (prescriptionId == null) {
            throw new ValidationException("3025", "处方ID不能为空");
        }

        // 查询现有处方
        Prescription existingPrescription = prescriptionMapper.selectById(prescriptionId);
        if (existingPrescription == null) {
            throw new BusinessException("3026", "处方不存在");
        }

        // 检查关联的病历是否可以编辑
        MedicalRecord medicalRecord = getMedicalRecordById(existingPrescription.getMedicalRecordId());
        if (!medicalRecord.canBeEdited()) {
            throw new BusinessException("3027", "病历状态为" + medicalRecord.getStatus() + "，不允许修改处方");
        }

        // 更新处方信息
        prescription.setId(prescriptionId);
        prescription.setMedicalRecordId(existingPrescription.getMedicalRecordId());
        prescriptionMapper.updateById(prescription);

        return prescription;
    }

    @Override
    public void deletePrescription(Long prescriptionId) {
        if (prescriptionId == null) {
            throw new ValidationException("3028", "处方ID不能为空");
        }

        // 查询现有处方
        Prescription existingPrescription = prescriptionMapper.selectById(prescriptionId);
        if (existingPrescription == null) {
            throw new BusinessException("3029", "处方不存在");
        }

        // 检查关联的病历是否可以编辑
        MedicalRecord medicalRecord = getMedicalRecordById(existingPrescription.getMedicalRecordId());
        if (!medicalRecord.canBeEdited()) {
            throw new BusinessException("3030", "病历状态为" + medicalRecord.getStatus() + "，不允许删除处方");
        }

        // 删除处方
        prescriptionMapper.deleteById(prescriptionId);
    }

    @Override
    public List<Prescription> getMedicalRecordPrescriptions(Long recordId) {
        if (recordId == null) {
            throw new ValidationException("3031", "病历ID不能为空");
        }

        QueryWrapper<Prescription> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("medical_record_id", recordId)
                   .orderByDesc("prescribed_at");

        return prescriptionMapper.selectList(queryWrapper);
    }

    @Override
    public void submitForReview(Long recordId) {
        MedicalRecord medicalRecord = getMedicalRecordById(recordId);

        if (!"草稿".equals(medicalRecord.getStatus())) {
            throw new BusinessException("3032", "只有草稿状态的病历才能提交审核");
        }

        medicalRecord.submitForReview();
        medicalRecordMapper.updateById(medicalRecord);
    }

    @Override
    public void reviewMedicalRecord(Long recordId, Long reviewDoctorId, boolean approved, String comments) {
        if (reviewDoctorId == null) {
            throw new ValidationException("3033", "审核医生ID不能为空");
        }

        MedicalRecord medicalRecord = getMedicalRecordById(recordId);

        if (!"待审核".equals(medicalRecord.getStatus())) {
            throw new BusinessException("3034", "只有待审核状态的病历才能进行审核");
        }

        if (approved) {
            medicalRecord.approve(reviewDoctorId, comments);
        } else {
            medicalRecord.reject(reviewDoctorId, comments);
        }

        medicalRecordMapper.updateById(medicalRecord);
    }

    @Override
    public void archiveMedicalRecord(Long recordId) {
        MedicalRecord medicalRecord = getMedicalRecordById(recordId);

        if (!"已审核".equals(medicalRecord.getStatus())) {
            throw new BusinessException("3035", "只有已审核状态的病历才能归档");
        }

        medicalRecord.archive();
        medicalRecordMapper.updateById(medicalRecord);
    }

    @Override
    public void deleteMedicalRecord(Long recordId) {
        MedicalRecord medicalRecord = getMedicalRecordById(recordId);

        if (medicalRecord.isReviewed()) {
            throw new BusinessException("3036", "已审核或已归档的病历不能删除");
        }

        // 软删除病历
        medicalRecord.setDeleted(true);
        medicalRecordMapper.updateById(medicalRecord);
    }

    @Override
    public int countPatientMedicalRecords(Long patientId) {
        if (patientId == null) {
            throw new ValidationException("3037", "患者ID不能为空");
        }

        return medicalRecordMapper.countByPatientId(patientId);
    }

    @Override
    public int countDoctorMedicalRecords(Long doctorId) {
        if (doctorId == null) {
            throw new ValidationException("3038", "医生ID不能为空");
        }

        return medicalRecordMapper.countByDoctorId(doctorId);
    }

    @Override
    public boolean existsByRecordNumber(String recordNumber) {
        if (recordNumber == null || recordNumber.trim().isEmpty()) {
            return false;
        }

        return medicalRecordMapper.existsByRecordNumber(recordNumber);
    }

    @Override
    public String generateRecordNumber(Long patientId, Long doctorId) {
        if (patientId == null || doctorId == null) {
            throw new ValidationException("3039", "患者ID和医生ID不能为空");
        }

        // 生成格式：MR + 年月日 + 患者ID后4位 + 医生ID后2位 + 序号
        String dateStr = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String patientSuffix = String.format("%04d", patientId % 10000);
        String doctorSuffix = String.format("%02d", doctorId % 100);
        
        String baseNumber = "MR" + dateStr + patientSuffix + doctorSuffix;
        
        // 检查是否存在重复，如果存在则添加序号
        String recordNumber = baseNumber;
        int sequence = 1;
        
        while (existsByRecordNumber(recordNumber)) {
            recordNumber = baseNumber + String.format("%02d", sequence);
            sequence++;
            
            if (sequence > 99) {
                throw new BusinessException("3040", "病历编号生成失败，请稍后重试");
            }
        }
        
        return recordNumber;
    }

    /**
     * 验证创建病历请求
     */
    private void validateCreateRequest(CreateMedicalRecordRequest request) {
        if (request.getPatientId() == null) {
            throw new ValidationException("3041", "患者ID不能为空");
        }
        if (request.getDoctorId() == null) {
            throw new ValidationException("3042", "医生ID不能为空");
        }
    }

    /**
     * 加载病历的关联数据（诊断和处方）
     */
    private void loadRelatedData(MedicalRecord medicalRecord) {
        if (medicalRecord == null || medicalRecord.getId() == null) {
            return;
        }

        // 加载诊断信息
        List<Diagnosis> diagnoses = getMedicalRecordDiagnoses(medicalRecord.getId());
        medicalRecord.setDiagnoses(diagnoses);

        // 加载处方信息
        List<Prescription> prescriptions = getMedicalRecordPrescriptions(medicalRecord.getId());
        medicalRecord.setPrescriptions(prescriptions);
    }
}