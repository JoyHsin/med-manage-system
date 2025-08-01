package org.me.joy.clinic.dto;

import jakarta.validation.constraints.*;

import java.time.LocalDate;

/**
 * 创建用户请求DTO
 */
public class CreateUserRequest {

    /**
     * 用户名
     */
    @NotBlank(message = "用户名不能为空")
    @Size(min = 3, max = 50, message = "用户名长度必须在3-50个字符之间")
    @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "用户名只能包含字母、数字和下划线")
    private String username;

    /**
     * 密码
     */
    @NotBlank(message = "密码不能为空")
    @Size(min = 8, max = 50, message = "密码长度必须在8-50个字符之间")
    @Pattern(
        regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",
        message = "密码必须包含至少一个大写字母、一个小写字母、一个数字和一个特殊字符(@$!%*?&)"
    )
    private String password;

    /**
     * 员工姓名
     */
    @NotBlank(message = "员工姓名不能为空")
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

    /**
     * 账号是否启用
     */
    private Boolean enabled = true;

    // 构造函数
    public CreateUserRequest() {}

    // Getters and Setters
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

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

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public String toString() {
        return "CreateUserRequest{" +
                "username='" + username + '\'' +
                ", password='[PROTECTED]'" +
                ", fullName='" + fullName + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", hireDate=" + hireDate +
                ", employeeId='" + employeeId + '\'' +
                ", department='" + department + '\'' +
                ", position='" + position + '\'' +
                ", enabled=" + enabled +
                '}';
    }
}