package org.me.joy.clinic.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.me.joy.clinic.dto.VitalSignsRequest;
import org.me.joy.clinic.entity.Patient;
import org.me.joy.clinic.entity.VitalSigns;
import org.me.joy.clinic.exception.BusinessException;
import org.me.joy.clinic.exception.ValidationException;
import org.me.joy.clinic.mapper.PatientMapper;
import org.me.joy.clinic.mapper.VitalSignsMapper;
import org.me.joy.clinic.service.VitalSignsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 生命体征服务实现类
 * 
 * @author Kiro
 */
@Service
@Transactional
public class VitalSignsServiceImpl implements VitalSignsService {

    @Autowired
    private VitalSignsMapper vitalSignsMapper;

    @Autowired
    private PatientMapper patientMapper;

    @Override
    public VitalSigns recordVitalSigns(VitalSignsRequest request, Long recordedBy) {
        // 验证患者是否存在
        Patient patient = patientMapper.selectById(request.getPatientId());
        if (patient == null) {
            throw new BusinessException("3001", "患者不存在");
        }

        // 验证生命体征数据
        VitalSignsValidationResult validationResult = validateVitalSigns(request);
        if (!validationResult.isValid()) {
            throw new ValidationException("4001", "生命体征数据验证失败: " + String.join(", ", validationResult.getErrors()));
        }

        // 创建生命体征记录
        VitalSigns vitalSigns = new VitalSigns();
        vitalSigns.setPatientId(request.getPatientId());
        vitalSigns.setRecordedBy(recordedBy);
        vitalSigns.setSystolicBp(request.getSystolicBp());
        vitalSigns.setDiastolicBp(request.getDiastolicBp());
        vitalSigns.setTemperature(request.getTemperature());
        vitalSigns.setHeartRate(request.getHeartRate());
        vitalSigns.setRespiratoryRate(request.getRespiratoryRate());
        vitalSigns.setOxygenSaturation(request.getOxygenSaturation());
        vitalSigns.setWeight(request.getWeight());
        vitalSigns.setHeight(request.getHeight());
        vitalSigns.setPainScore(request.getPainScore());
        vitalSigns.setConsciousnessLevel(request.getConsciousnessLevel());
        vitalSigns.setRemarks(request.getRemarks());
        vitalSigns.setRecordedAt(LocalDateTime.now());

        // 计算BMI
        if (request.getWeight() != null && request.getHeight() != null) {
            vitalSigns.setBmi(vitalSigns.calculateBmi());
        }

        // 检查异常情况
        vitalSigns.checkAbnormal();

        // 设置基础字段
        vitalSigns.setCreatedAt(LocalDateTime.now());
        vitalSigns.setUpdatedAt(LocalDateTime.now());
        vitalSigns.setDeleted(false);

        // 保存到数据库
        int result = vitalSignsMapper.insert(vitalSigns);
        if (result <= 0) {
            throw new BusinessException("4002", "生命体征记录保存失败");
        }

        return vitalSigns;
    }

    @Override
    public VitalSigns getVitalSignsById(Long id) {
        VitalSigns vitalSigns = vitalSignsMapper.selectById(id);
        if (vitalSigns == null || vitalSigns.getDeleted()) {
            throw new BusinessException("4003", "生命体征记录不存在");
        }
        return vitalSigns;
    }

    @Override
    public List<VitalSigns> getPatientVitalSigns(Long patientId) {
        // 验证患者是否存在
        Patient patient = patientMapper.selectById(patientId);
        if (patient == null) {
            throw new BusinessException("3001", "患者不存在");
        }

        return vitalSignsMapper.findByPatientId(patientId);
    }

    @Override
    public List<VitalSigns> getPatientVitalSignsByTimeRange(Long patientId, LocalDateTime startTime, LocalDateTime endTime) {
        // 验证患者是否存在
        Patient patient = patientMapper.selectById(patientId);
        if (patient == null) {
            throw new BusinessException("3001", "患者不存在");
        }

        // 验证时间范围
        if (startTime != null && endTime != null && startTime.isAfter(endTime)) {
            throw new ValidationException("4004", "开始时间不能晚于结束时间");
        }

        return vitalSignsMapper.findByPatientIdAndTimeRange(patientId, startTime, endTime);
    }

    @Override
    public VitalSigns getLatestVitalSigns(Long patientId) {
        // 验证患者是否存在
        Patient patient = patientMapper.selectById(patientId);
        if (patient == null) {
            throw new BusinessException("3001", "患者不存在");
        }

        return vitalSignsMapper.findLatestByPatientId(patientId);
    }

    @Override
    public List<VitalSigns> getAbnormalVitalSigns(Long patientId, LocalDateTime startTime, LocalDateTime endTime) {
        // 验证时间范围
        if (startTime != null && endTime != null && startTime.isAfter(endTime)) {
            throw new ValidationException("4004", "开始时间不能晚于结束时间");
        }

        return vitalSignsMapper.findAbnormalVitalSigns(patientId, startTime, endTime);
    }

    @Override
    public VitalSigns updateVitalSigns(Long id, VitalSignsRequest request, Long updatedBy) {
        // 获取现有记录
        VitalSigns existingVitalSigns = getVitalSignsById(id);

        // 验证生命体征数据
        VitalSignsValidationResult validationResult = validateVitalSigns(request);
        if (!validationResult.isValid()) {
            throw new ValidationException("4001", "生命体征数据验证失败: " + String.join(", ", validationResult.getErrors()));
        }

        // 更新字段
        existingVitalSigns.setSystolicBp(request.getSystolicBp());
        existingVitalSigns.setDiastolicBp(request.getDiastolicBp());
        existingVitalSigns.setTemperature(request.getTemperature());
        existingVitalSigns.setHeartRate(request.getHeartRate());
        existingVitalSigns.setRespiratoryRate(request.getRespiratoryRate());
        existingVitalSigns.setOxygenSaturation(request.getOxygenSaturation());
        existingVitalSigns.setWeight(request.getWeight());
        existingVitalSigns.setHeight(request.getHeight());
        existingVitalSigns.setPainScore(request.getPainScore());
        existingVitalSigns.setConsciousnessLevel(request.getConsciousnessLevel());
        existingVitalSigns.setRemarks(request.getRemarks());

        // 重新计算BMI
        if (request.getWeight() != null && request.getHeight() != null) {
            existingVitalSigns.setBmi(existingVitalSigns.calculateBmi());
        } else {
            existingVitalSigns.setBmi(null);
        }

        // 重新检查异常情况
        existingVitalSigns.checkAbnormal();

        // 更新时间戳
        existingVitalSigns.setUpdatedAt(LocalDateTime.now());

        // 保存更新
        int result = vitalSignsMapper.updateById(existingVitalSigns);
        if (result <= 0) {
            throw new BusinessException("4005", "生命体征记录更新失败");
        }

        return existingVitalSigns;
    }

    @Override
    public void deleteVitalSigns(Long id, Long deletedBy) {
        // 获取现有记录
        VitalSigns existingVitalSigns = getVitalSignsById(id);

        // 逻辑删除
        existingVitalSigns.setDeleted(true);
        existingVitalSigns.setUpdatedAt(LocalDateTime.now());

        int result = vitalSignsMapper.updateById(existingVitalSigns);
        if (result <= 0) {
            throw new BusinessException("4006", "生命体征记录删除失败");
        }
    }

    @Override
    public VitalSignsValidationResult validateVitalSigns(VitalSignsRequest request) {
        List<String> warnings = new ArrayList<>();
        List<String> errors = new ArrayList<>();

        // 验证血压一致性
        if (!request.isBloodPressureValid()) {
            errors.add("收缩压必须大于舒张压");
        }

        // 检查异常值并生成警告
        if (request.getSystolicBp() != null) {
            if (request.getSystolicBp() < 90 || request.getSystolicBp() > 140) {
                warnings.add("收缩压异常：正常范围为90-140mmHg");
            }
        }

        if (request.getDiastolicBp() != null) {
            if (request.getDiastolicBp() < 60 || request.getDiastolicBp() > 90) {
                warnings.add("舒张压异常：正常范围为60-90mmHg");
            }
        }

        if (request.getTemperature() != null) {
            if (request.getTemperature().compareTo(new BigDecimal("36.0")) < 0 || 
                request.getTemperature().compareTo(new BigDecimal("37.5")) > 0) {
                warnings.add("体温异常：正常范围为36.0-37.5°C");
            }
        }

        if (request.getHeartRate() != null) {
            if (request.getHeartRate() < 60 || request.getHeartRate() > 100) {
                warnings.add("心率异常：正常范围为60-100次/分钟");
            }
        }

        if (request.getRespiratoryRate() != null) {
            if (request.getRespiratoryRate() < 12 || request.getRespiratoryRate() > 20) {
                warnings.add("呼吸频率异常：正常范围为12-20次/分钟");
            }
        }

        if (request.getOxygenSaturation() != null) {
            if (request.getOxygenSaturation() < 95) {
                warnings.add("血氧饱和度异常：正常范围为95-100%");
            }
        }

        // 检查BMI异常
        if (request.getWeight() != null && request.getHeight() != null && request.getHeight() > 0) {
            BigDecimal heightInMeters = new BigDecimal(request.getHeight()).divide(new BigDecimal(100));
            BigDecimal bmi = request.getWeight().divide(heightInMeters.multiply(heightInMeters), 1, BigDecimal.ROUND_HALF_UP);
            
            if (bmi.compareTo(new BigDecimal("18.5")) < 0) {
                warnings.add("BMI偏低：" + bmi + "，正常范围为18.5-24.0");
            } else if (bmi.compareTo(new BigDecimal("24.0")) > 0) {
                warnings.add("BMI偏高：" + bmi + "，正常范围为18.5-24.0");
            }
        }

        boolean valid = errors.isEmpty();
        boolean hasWarnings = !warnings.isEmpty();

        return new VitalSignsValidationResult(valid, hasWarnings, warnings, errors);
    }

    @Override
    public List<VitalSigns> getVitalSignsByRecorder(Long recordedBy, LocalDateTime startTime, LocalDateTime endTime) {
        // 验证时间范围
        if (startTime != null && endTime != null && startTime.isAfter(endTime)) {
            throw new ValidationException("4004", "开始时间不能晚于结束时间");
        }

        return vitalSignsMapper.findByRecordedByAndTimeRange(recordedBy, startTime, endTime);
    }
}