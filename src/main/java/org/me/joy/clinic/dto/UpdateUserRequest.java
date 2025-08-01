package org.me.joy.clinic.dto;

import jakarta.validation.constraints.*;

import java.time.LocalDate;

/**
 * 更新用户请求DTO
 */
public class UpdateUserRequest {

    /**
     * 员工姓名
     */
    @Size(max = 100, message = "员工姓名长度不能超过100个字符")
    private String fullName;

    /**
     * 邮箱地址
     */
    @Email(message = "邮箱格式不正确")
    @Size(max = 100, message = "邮箱长度不能超过100个字符")
    private String email;

    /**
     * 手机号码
     */
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号码格式不正确")
    private String phone;

    /**
     * 入职日期
     */
    private LocalDate hireDate;

    /**
     * 员工工号
     */
    @Size(max = 50, message = "员工工号长度不能超过50个字符")
    private String employeeId;

    /**
     * 部门
     */
    @Size(max = 100, message = "部门名称长度不能超过100个字符")
    private String department;

    /**
     * 职位
     */
    @Size(max = 100, message = "职位名称长度不能超过100个字符")
    private String position;

    // 构造函数
    public UpdateUserRequest() {}

    // Getters and Setters
    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public LocalDate getHireDate() {
        return hireDate;
    }

    public void setHireDate(LocalDate hireDate) {
        this.hireDate = hireDate;
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    @Override
    public String toString() {
        return "UpdateUserRequest{" +
                "fullName='" + fullName + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", hireDate=" + hireDate +
                ", employeeId='" + employeeId + '\'' +
                ", department='" + department + '\'' +
                ", position='" + position + '\'' +
                '}';
    }
}