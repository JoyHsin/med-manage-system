package org.me.joy.clinic.service;

import org.me.joy.clinic.dto.VitalSignsRequest;
import org.me.joy.clinic.entity.VitalSigns;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 生命体征服务接口
 * 
 * @author Kiro
 */
public interface VitalSignsService {

    /**
     * 录入生命体征
     * 
     * @param request 生命体征录入请求
     * @param recordedBy 记录人员ID
     * @return 生命体征记录
     */
    VitalSigns recordVitalSigns(VitalSignsRequest request, Long recordedBy);

    /**
     * 根据ID获取生命体征记录
     * 
     * @param id 生命体征记录ID
     * @return 生命体征记录
     */
    VitalSigns getVitalSignsById(Long id);

    /**
     * 获取患者的生命体征记录列表
     * 
     * @param patientId 患者ID
     * @return 生命体征记录列表
     */
    List<VitalSigns> getPatientVitalSigns(Long patientId);

    /**
     * 获取患者指定时间范围内的生命体征记录
     * 
     * @param patientId 患者ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 生命体征记录列表
     */
    List<VitalSigns> getPatientVitalSignsByTimeRange(Long patientId, LocalDateTime startTime, LocalDateTime endTime);

    /**
     * 获取患者最新的生命体征记录
     * 
     * @param patientId 患者ID
     * @return 最新的生命体征记录
     */
    VitalSigns getLatestVitalSigns(Long patientId);

    /**
     * 获取异常生命体征记录
     * 
     * @param patientId 患者ID（可选）
     * @param startTime 开始时间（可选）
     * @param endTime 结束时间（可选）
     * @return 异常生命体征记录列表
     */
    List<VitalSigns> getAbnormalVitalSigns(Long patientId, LocalDateTime startTime, LocalDateTime endTime);

    /**
     * 更新生命体征记录
     * 
     * @param id 生命体征记录ID
     * @param request 更新请求
     * @param updatedBy 更新人员ID
     * @return 更新后的生命体征记录
     */
    VitalSigns updateVitalSigns(Long id, VitalSignsRequest request, Long updatedBy);

    /**
     * 删除生命体征记录（逻辑删除）
     * 
     * @param id 生命体征记录ID
     * @param deletedBy 删除人员ID
     */
    void deleteVitalSigns(Long id, Long deletedBy);

    /**
     * 验证生命体征数据
     * 
     * @param request 生命体征请求
     * @return 验证结果和警告信息
     */
    VitalSignsValidationResult validateVitalSigns(VitalSignsRequest request);

    /**
     * 获取记录人员的生命体征记录
     * 
     * @param recordedBy 记录人员ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 生命体征记录列表
     */
    List<VitalSigns> getVitalSignsByRecorder(Long recordedBy, LocalDateTime startTime, LocalDateTime endTime);

    /**
     * 生命体征验证结果
     */
    class VitalSignsValidationResult {
        private boolean valid;
        private boolean hasWarnings;
        private List<String> warnings;
        private List<String> errors;

        public VitalSignsValidationResult() {}

        public VitalSignsValidationResult(boolean valid, boolean hasWarnings, List<String> warnings, List<String> errors) {
            this.valid = valid;
            this.hasWarnings = hasWarnings;
            this.warnings = warnings;
            this.errors = errors;
        }

        // Getters and Setters
        public boolean isValid() {
            return valid;
        }

        public void setValid(boolean valid) {
            this.valid = valid;
        }

        public boolean isHasWarnings() {
            return hasWarnings;
        }

        public void setHasWarnings(boolean hasWarnings) {
            this.hasWarnings = hasWarnings;
        }

        public List<String> getWarnings() {
            return warnings;
        }

        public void setWarnings(List<String> warnings) {
            this.warnings = warnings;
        }

        public List<String> getErrors() {
            return errors;
        }

        public void setErrors(List<String> errors) {
            this.errors = errors;
        }
    }
}