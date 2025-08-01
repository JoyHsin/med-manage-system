package org.me.joy.clinic.dto;

import jakarta.validation.constraints.Size;

/**
 * 更新角色请求DTO
 */
public class UpdateRoleRequest {

    /**
     * 角色名称
     */
    @Size(max = 100, message = "角色名称长度不能超过100个字符")
    private String roleName;

    /**
     * 角色描述
     */
    @Size(max = 500, message = "角色描述长度不能超过500个字符")
    private String description;

    /**
     * 是否启用
     */
    private Boolean enabled;

    // 构造函数
    public UpdateRoleRequest() {}

    public UpdateRoleRequest(String roleName, String description, Boolean enabled) {
        this.roleName = roleName;
        this.description = description;
        this.enabled = enabled;
    }

    // Getters and Setters
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

    @Override
    public String toString() {
        return "UpdateRoleRequest{" +
                "roleName='" + roleName + '\'' +
                ", description='" + description + '\'' +
                ", enabled=" + enabled +
                '}';
    }
}