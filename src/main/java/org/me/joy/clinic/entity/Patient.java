package org.me.joy.clinic.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import jakarta.validation.constraints.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 患者实体类
 * 存储患者的基本信息和档案数据
 */
@TableName("patients")
public class Patient extends BaseEntity {

    /**
     * 患者编号（唯一标识）
     */
    @NotBlank(message = "患者编号不能为空")
    @Size(max = 50, message = "患者编号长度不能超过50个字符")
    private String patientNumber;

    /**
     * 患者姓名
     */
    @NotBlank(message = "患者姓名不能为空")
    @Size(max = 100, message = "患者姓名长度不能超过100个字符")
    private String name;

    /**
     * 手机号码
     */
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号码格式不正确")
    private String phone;

    /**
     * 身份证号码
     */
    @Pattern(regexp = "^[1-9]\\d{5}(18|19|20)\\d{2}((0[1-9])|(1[0-2]))(([0-2][1-9])|10|20|30|31)\\d{3}[0-9Xx]$", 
             message = "身份证号码格式不正确")
    private String idCard;

    /**
     * 出生日期
     */
    @Past(message = "出生日期不能晚于当前日期")
    private LocalDate birthDate;

    /**
     * 性别
     */
    @NotBlank(message = "性别不能为空")
    @Pattern(regexp = "^(男|女|未知)$", message = "性别只能是男、女或未知")
    private String gender;

    /**
     * 联系地址
     */
    @Size(max = 500, message = "联系地址长度不能超过500个字符")
    private String address;

    /**
     * 紧急联系人姓名
     */
    @Size(max = 100, message = "紧急联系人姓名长度不能超过100个字符")
    private String emergencyContactName;

    /**
     * 紧急联系人电话
     */
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "紧急联系人电话格式不正确")
    private String emergencyContactPhone;

    /**
     * 紧急联系人关系
     */
    @Size(max = 50, message = "紧急联系人关系长度不能超过50个字符")
    private String emergencyContactRelation;

    /**
     * 血型
     */
    @Pattern(regexp = "^(A|B|AB|O)[+-]?$", message = "血型格式不正确")
    private String bloodType;

    /**
     * 婚姻状况
     */
    @Pattern(regexp = "^(未婚|已婚|离异|丧偶|未知)$", message = "婚姻状况只能是未婚、已婚、离异、丧偶或未知")
    private String maritalStatus;

    /**
     * 职业
     */
    @Size(max = 100, message = "职业长度不能超过100个字符")
    private String occupation;

    /**
     * 民族
     */
    @Size(max = 50, message = "民族长度不能超过50个字符")
    private String ethnicity;

    /**
     * 医保类型
     */
    @Size(max = 50, message = "医保类型长度不能超过50个字符")
    private String insuranceType;

    /**
     * 医保号码
     */
    @Size(max = 100, message = "医保号码长度不能超过100个字符")
    private String insuranceNumber;

    /**
     * 备注信息
     */
    @Size(max = 1000, message = "备注信息长度不能超过1000个字符")
    private String remarks;

    /**
     * 是否为VIP患者
     */
    private Boolean isVip = false;

    /**
     * 患者状态（正常、黑名单等）
     */
    @NotBlank(message = "患者状态不能为空")
    @Pattern(regexp = "^(正常|黑名单|暂停服务)$", message = "患者状态只能是正常、黑名单或暂停服务")
    private String status = "正常";

    /**
     * 首次就诊时间
     */
    private LocalDateTime firstVisitTime;

    /**
     * 最后就诊时间
     */
    private LocalDateTime lastVisitTime;

    /**
     * 就诊次数
     */
    private Integer visitCount = 0;

    /**
     * 过敏史列表（不存储在数据库中，通过关联查询获取）
     */
    @TableField(exist = false)
    private List<AllergyHistory> allergyHistories;

    /**
     * 病史列表（不存储在数据库中，通过关联查询获取）
     */
    @TableField(exist = false)
    private List<MedicalHistory> medicalHistories;

    // 构造函数
    public Patient() {}

    public Patient(String patientNumber, String name, String phone) {
        this.patientNumber = patientNumber;
        this.name = name;
        this.phone = phone;
    }

    // Getters and Setters
    public String getPatientNumber() {
        return patientNumber;
    }

    public void setPatientNumber(String patientNumber) {
        this.patientNumber = patientNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getIdCard() {
        return idCard;
    }

    public void setIdCard(String idCard) {
        this.idCard = idCard;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getEmergencyContactName() {
        return emergencyContactName;
    }

    public void setEmergencyContactName(String emergencyContactName) {
        this.emergencyContactName = emergencyContactName;
    }

    public String getEmergencyContactPhone() {
        return emergencyContactPhone;
    }

    public void setEmergencyContactPhone(String emergencyContactPhone) {
        this.emergencyContactPhone = emergencyContactPhone;
    }

    public String getEmergencyContactRelation() {
        return emergencyContactRelation;
    }

    public void setEmergencyContactRelation(String emergencyContactRelation) {
        this.emergencyContactRelation = emergencyContactRelation;
    }

    public String getBloodType() {
        return bloodType;
    }

    public void setBloodType(String bloodType) {
        this.bloodType = bloodType;
    }

    public String getMaritalStatus() {
        return maritalStatus;
    }

    public void setMaritalStatus(String maritalStatus) {
        this.maritalStatus = maritalStatus;
    }

    public String getOccupation() {
        return occupation;
    }

    public void setOccupation(String occupation) {
        this.occupation = occupation;
    }

    public String getEthnicity() {
        return ethnicity;
    }

    public void setEthnicity(String ethnicity) {
        this.ethnicity = ethnicity;
    }

    public String getInsuranceType() {
        return insuranceType;
    }

    public void setInsuranceType(String insuranceType) {
        this.insuranceType = insuranceType;
    }

    public String getInsuranceNumber() {
        return insuranceNumber;
    }

    public void setInsuranceNumber(String insuranceNumber) {
        this.insuranceNumber = insuranceNumber;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public Boolean getIsVip() {
        return isVip;
    }

    public void setIsVip(Boolean isVip) {
        this.isVip = isVip;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getFirstVisitTime() {
        return firstVisitTime;
    }

    public void setFirstVisitTime(LocalDateTime firstVisitTime) {
        this.firstVisitTime = firstVisitTime;
    }

    public LocalDateTime getLastVisitTime() {
        return lastVisitTime;
    }

    public void setLastVisitTime(LocalDateTime lastVisitTime) {
        this.lastVisitTime = lastVisitTime;
    }

    public Integer getVisitCount() {
        return visitCount;
    }

    public void setVisitCount(Integer visitCount) {
        this.visitCount = visitCount;
    }

    public List<AllergyHistory> getAllergyHistories() {
        return allergyHistories;
    }

    public void setAllergyHistories(List<AllergyHistory> allergyHistories) {
        this.allergyHistories = allergyHistories;
    }

    public List<MedicalHistory> getMedicalHistories() {
        return medicalHistories;
    }

    public void setMedicalHistories(List<MedicalHistory> medicalHistories) {
        this.medicalHistories = medicalHistories;
    }

    /**
     * 计算年龄
     */
    public Integer getAge() {
        if (birthDate == null) {
            return null;
        }
        return LocalDate.now().getYear() - birthDate.getYear();
    }

    /**
     * 增加就诊次数
     */
    public void incrementVisitCount() {
        this.visitCount = (this.visitCount == null ? 0 : this.visitCount) + 1;
        this.lastVisitTime = LocalDateTime.now();
        if (this.firstVisitTime == null) {
            this.firstVisitTime = LocalDateTime.now();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Patient)) return false;
        Patient patient = (Patient) o;
        return patientNumber != null && patientNumber.equals(patient.patientNumber);
    }

    @Override
    public int hashCode() {
        return patientNumber != null ? patientNumber.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "Patient{" +
                "id=" + getId() +
                ", patientNumber='" + patientNumber + '\'' +
                ", name='" + name + '\'' +
                ", phone='" + phone + '\'' +
                ", gender='" + gender + '\'' +
                ", birthDate=" + birthDate +
                ", status='" + status + '\'' +
                '}';
    }
}