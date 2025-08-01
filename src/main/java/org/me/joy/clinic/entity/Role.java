package org.me.joy.clinic.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.HashSet;
import java.util.Set;

/**
 * 角色实体类
 * 定义系统中的各种角色，如超级管理员、医生、护士、药剂师、前台文员等
 */
@TableName("roles")
public class Role extends BaseEntity {

    /**
     * 角色代码，用于程序中的角色判断
     */
    @NotBlank(message = "角色代码不能为空")
    @Size(max = 50, message = "角色代码长度不能超过50个字符")
    private String roleCode;

    /**
     * 角色名称，用于界面显示
     */
    @NotBlank(message = "角色名称不能为空")
    @Size(max = 100, message = "角色名称长度不能超过100个字符")
    private String roleName;

    /**
     * 角色描述
     */
    @Size(max = 500, message = "角色描述长度不能超过500个字符")
    private String description;

    /**
     * 角色是否启用
     */
    private Boolean enabled = true;

    /**
     * 是否为系统预设角色（不可删除）
     */
    private Boolean isSystemRole = false;

    /**
     * 角色权限关联
     */
    @TableField(exist = false)
    private Set<Permission> permissions = new HashSet<>();

    /**
     * 用户角色关联（反向关联）
     */
    @TableField(exist = false)
    private Set<User> users = new HashSet<>();

    // 构造函数
    public Role() {}

    public Role(String roleCode, String roleName, String description) {
        this.roleCode = roleCode;
        this.roleName = roleName;
        this.description = description;
    }

    public Role(String roleCode, String roleName, String description, Boolean isSystemRole) {
        this.roleCode = roleCode;
        this.roleName = roleName;
        this.description = description;
        this.isSystemRole = isSystemRole;
    }

    // Getters and Setters
    public String getRoleCode() {
        return roleCode;
    }

    public void setRoleCode(String roleCode) {
        this.roleCode = roleCode;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public Boolean getIsSystemRole() {
        return isSystemRole;
    }

    public void setIsSystemRole(Boolean isSystemRole) {
        this.isSystemRole = isSystemRole;
    }

    public Set<Permission> getPermissions() {
        return permissions;
    }

    public void setPermissions(Set<Permission> permissions) {
        this.permissions = permissions;
    }

    public void addPermission(Permission permission) {
        this.permissions.add(permission);
    }

    public void removePermission(Permission permission) {
        this.permissions.remove(permission);
    }

    public Set<User> getUsers() {
        return users;
    }

    public void setUsers(Set<User> users) {
        this.users = users;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Role)) return false;
        Role role = (Role) o;
        return roleCode != null && roleCode.equals(role.roleCode);
    }

    @Override
    public int hashCode() {
        return roleCode != null ? roleCode.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "Role{" +
                "id=" + getId() +
                ", roleCode='" + roleCode + '\'' +
                ", roleName='" + roleName + '\'' +
                ", enabled=" + enabled +
                ", isSystemRole=" + isSystemRole +
                '}';
    }
}