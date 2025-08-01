package org.me.joy.clinic.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import jakarta.validation.constraints.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 医护人员实体类
 * 存储医护人员的基本信息和工作相关数据
 */
@TableName("staff")
public class Staff extends BaseEntity {

    /**
     * 员工编号（唯一标识）
     */
    @NotBlank(message = "员工编号不能为空")
    @Size(max = 50, message = "员工编号长度不能超过50个字符")
    private String staffNumber;

    /**
     * 员工姓名
     */
    @NotBlank(message = "员工姓名不能为空")
    @Size(max = 100, message = "员工姓名长度不能超过100个字符")
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
    @Pattern(regexp = "^(男|女)$", message = "性别只能是男或女")
    private String gender;

    /**
     * 邮箱地址
     */
    @Email(message = "邮箱格式不正确")
    @Size(max = 100, message = "邮箱长度不能超过100个字符")
    private String email;

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
     * 职位类型
     */
    @NotBlank(message = "职位类型不能为空")
    @Pattern(regexp = "^(医生|护士|药剂师|前台|管理员|其他)$", 
             message = "职位类型只能是医生、护士、药剂师、前台、管理员或其他")
    private String position;

    /**
     * 科室
     */
    @Size(max = 100, message = "科室名称长度不能超过100个字符")
    private String department;

    /**
     * 职称
     */
    @Size(max = 100, message = "职称长度不能超过100个字符")
    private String title;

    /**
     * 专业特长
     */
    @Size(max = 500, message = "专业特长长度不能超过500个字符")
    private String specialization;

    /**
     * 学历
     */
    @Pattern(regexp = "^(高中|大专|本科|硕士|博士|其他)$", 
             message = "学历只能是高中、大专、本科、硕士、博士或其他")
    private String education;

    /**
     * 毕业院校
     */
    @Size(max = 200, message = "毕业院校长度不能超过200个字符")
    private String graduateSchool;

    /**
     * 执业证书编号
     */
    @Size(max = 100, message = "执业证书编号长度不能超过100个字符")
    private String licenseNumber;

    /**
     * 执业证书有效期
     */
    private LocalDate licenseExpiryDate;

    /**
     * 入职日期
     */
    @NotNull(message = "入职日期不能为空")
    private LocalDate hireDate;

    /**
     * 离职日期
     */
    private LocalDate resignationDate;

    /**
     * 工作状态
     */
    @NotBlank(message = "工作状态不能为空")
    @Pattern(regexp = "^(在职|离职|休假|停职)$", message = "工作状态只能是在职、离职、休假或停职")
    private String workStatus = "在职";

    /**
     * 基本工资
     */
    @DecimalMin(value = "0.0", message = "基本工资不能为负数")
    private Double baseSalary;

    /**
     * 工作经验年数
     */
    @Min(value = 0, message = "工作经验年数不能为负数")
    private Integer experienceYears;

    /**
     * 备注信息
     */
    @Size(max = 1000, message = "备注信息长度不能超过1000个字符")
    private String remarks;

    /**
     * 是否可排班
     */
    private Boolean isSchedulable = true;

    /**
     * 最后工作日期
     */
    private LocalDate lastWorkDate;

    /**
     * 关联的用户ID（如果有系统账号）
     */
    private Long userId;

    /**
     * 排班列表（不存储在数据库中，通过关联查询获取）
     */
    @TableField(exist = false)
    private List<Schedule> schedules;

    // 构造函数
    public Staff() {}

    public Staff(String staffNumber, String name, String position) {
        this.staffNumber = staffNumber;
        this.name = name;
        this.position = position;
        this.hireDate = LocalDate.now();
    }

    // Getters and Setters
    public String getStaffNumber() {
        return staffNumber;
    }

    public void setStaffNumber(String staffNumber) {
        this.staffNumber = staffNumber;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSpecialization() {
        return specialization;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }

    public String getEducation() {
        return education;
    }

    public void setEducation(String education) {
        this.education = education;
    }

    public String getGraduateSchool() {
        return graduateSchool;
    }

    public void setGraduateSchool(String graduateSchool) {
        this.graduateSchool = graduateSchool;
    }

    public String getLicenseNumber() {
        return licenseNumber;
    }

    public void setLicenseNumber(String licenseNumber) {
        this.licenseNumber = licenseNumber;
    }

    public LocalDate getLicenseExpiryDate() {
        return licenseExpiryDate;
    }

    public void setLicenseExpiryDate(LocalDate licenseExpiryDate) {
        this.licenseExpiryDate = licenseExpiryDate;
    }

    public LocalDate getHireDate() {
        return hireDate;
    }

    public void setHireDate(LocalDate hireDate) {
        this.hireDate = hireDate;
    }

    public LocalDate getResignationDate() {
        return resignationDate;
    }

    public void setResignationDate(LocalDate resignationDate) {
        this.resignationDate = resignationDate;
    }

    public String getWorkStatus() {
        return workStatus;
    }

    public void setWorkStatus(String workStatus) {
        this.workStatus = workStatus;
    }

    public Double getBaseSalary() {
        return baseSalary;
    }

    public void setBaseSalary(Double baseSalary) {
        this.baseSalary = baseSalary;
    }

    public Integer getExperienceYears() {
        return experienceYears;
    }

    public void setExperienceYears(Integer experienceYears) {
        this.experienceYears = experienceYears;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public Boolean getIsSchedulable() {
        return isSchedulable;
    }

    public void setIsSchedulable(Boolean isSchedulable) {
        this.isSchedulable = isSchedulable;
    }

    public LocalDate getLastWorkDate() {
        return lastWorkDate;
    }

    public void setLastWorkDate(LocalDate lastWorkDate) {
        this.lastWorkDate = lastWorkDate;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public List<Schedule> getSchedules() {
        return schedules;
    }

    public void setSchedules(List<Schedule> schedules) {
        this.schedules = schedules;
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
     * 计算工作年限
     */
    public Integer getWorkYears() {
        if (hireDate == null) {
            return null;
        }
        LocalDate endDate = resignationDate != null ? resignationDate : LocalDate.now();
        return endDate.getYear() - hireDate.getYear();
    }

    /**
     * 检查执业证书是否即将过期（30天内）
     */
    public boolean isLicenseExpiringSoon() {
        if (licenseExpiryDate == null) {
            return false;
        }
        return licenseExpiryDate.isBefore(LocalDate.now().plusDays(30));
    }

    /**
     * 检查是否在职
     */
    public boolean isActive() {
        return "在职".equals(workStatus);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Staff)) return false;
        Staff staff = (Staff) o;
        return staffNumber != null && staffNumber.equals(staff.staffNumber);
    }

    @Override
    public int hashCode() {
        return staffNumber != null ? staffNumber.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "Staff{" +
                "id=" + getId() +
                ", staffNumber='" + staffNumber + '\'' +
                ", name='" + name + '\'' +
                ", position='" + position + '\'' +
                ", department='" + department + '\'' +
                ", workStatus='" + workStatus + '\'' +
                '}';
    }
}