package org.me.joy.clinic.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.HashSet;
import java.util.Set;

/**
 * 权限实体类
 * 定义系统中的各种权限，如访问特定模块、执行特定操作等
 */
@TableName("permissions")
public class Permission extends BaseEntity {

    /**
     * 权限代码，用于程序中的权限判断
     * 格式：模块:操作，如 USER:CREATE, PATIENT:READ
     */
    @NotBlank(message = "权限代码不能为空")
    @Size(max = 100, message = "权限代码长度不能超过100个字符")
    private String permissionCode;

    /**
     * 权限名称，用于界面显示
     */
    @NotBlank(message = "权限名称不能为空")
    @Size(max = 100, message = "权限名称长度不能超过100个字符")
    private String permissionName;

    /**
     * 权限描述
     */
    @Size(max = 500, message = "权限描述长度不能超过500个字符")
    private String description;

    /**
     * 权限模块分类
     */
    @Size(max = 50, message = "模块名称长度不能超过50个字符")
    private String module;

    /**
     * 权限是否启用
     */
    private Boolean enabled = true;

    /**
     * 角色权限关联（反向关联）
     */
    @TableField(exist = false)
    private Set<Role> roles = new HashSet<>();

    // 构造函数
    public Permission() {}

    public Permission(String permissionCode, String permissionName, String description, String module) {
        this.permissionCode = permissionCode;
        this.permissionName = permissionName;
        this.description = description;
        this.module = module;
    }

    // Getters and Setters
    public String getPermissionCode() {
        return permissionCode;
    }

    public void setPermissionCode(String permissionCode) {
        this.permissionCode = permissionCode;
    }

    public String getPermissionName() {
        return permissionName;
    }

    public void setPermissionName(String permissionName) {
        this.permissionName = permissionName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getModule() {
        return module;
    }

    public void setModule(String module) {
        this.module = module;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Permission)) return false;
        Permission that = (Permission) o;
        return permissionCode != null && permissionCode.equals(that.permissionCode);
    }

    @Override
    public int hashCode() {
        return permissionCode != null ? permissionCode.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "Permission{" +
                "id=" + getId() +
                ", permissionCode='" + permissionCode + '\'' +
                ", permissionName='" + permissionName + '\'' +
                ", module='" + module + '\'' +
                ", enabled=" + enabled +
                '}';
    }
}