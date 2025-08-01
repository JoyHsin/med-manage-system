package org.me.joy.clinic.service;

import org.me.joy.clinic.dto.CreateRoleRequest;
import org.me.joy.clinic.dto.UpdateRoleRequest;
import org.me.joy.clinic.entity.Permission;
import org.me.joy.clinic.entity.Role;

import java.util.List;

/**
 * 角色管理服务接口
 * 提供角色的CRUD操作、权限分配等功能
 */
public interface RoleManagementService {

    /**
     * 获取所有角色
     * @return 角色列表
     */
    List<Role> getAllRoles();

    /**
     * 获取所有启用的角色
     * @return 启用的角色列表
     */
    List<Role> getEnabledRoles();

    /**
     * 根据ID获取角色
     * @param roleId 角色ID
     * @return 角色信息
     */
    Role getRoleById(Long roleId);

    /**
     * 根据角色代码获取角色
     * @param roleCode 角色代码
     * @return 角色信息
     */
    Role getRoleByCode(String roleCode);

    /**
     * 创建新角色
     * @param request 创建角色请求
     * @return 创建的角色
     */
    Role createRole(CreateRoleRequest request);

    /**
     * 更新角色信息
     * @param roleId 角色ID
     * @param request 更新角色请求
     * @return 更新后的角色
     */
    Role updateRole(Long roleId, UpdateRoleRequest request);

    /**
     * 删除角色
     * @param roleId 角色ID
     */
    void deleteRole(Long roleId);

    /**
     * 启用角色
     * @param roleId 角色ID
     */
    void enableRole(Long roleId);

    /**
     * 禁用角色
     * @param roleId 角色ID
     */
    void disableRole(Long roleId);

    /**
     * 为用户分配角色
     * @param userId 用户ID
     * @param roleId 角色ID
     */
    void assignRoleToUser(Long userId, Long roleId);

    /**
     * 移除用户角色
     * @param userId 用户ID
     * @param roleId 角色ID
     */
    void removeRoleFromUser(Long userId, Long roleId);

    /**
     * 获取用户的所有角色
     * @param userId 用户ID
     * @return 角色列表
     */
    List<Role> getUserRoles(Long userId);

    /**
     * 获取角色的所有权限
     * @param roleId 角色ID
     * @return 权限列表
     */
    List<Permission> getRolePermissions(Long roleId);

    /**
     * 为角色分配权限
     * @param roleId 角色ID
     * @param permissionId 权限ID
     */
    void assignPermissionToRole(Long roleId, Long permissionId);

    /**
     * 移除角色权限
     * @param roleId 角色ID
     * @param permissionId 权限ID
     */
    void removePermissionFromRole(Long roleId, Long permissionId);

    /**
     * 批量为角色分配权限
     * @param roleId 角色ID
     * @param permissionIds 权限ID列表
     */
    void assignPermissionsToRole(Long roleId, List<Long> permissionIds);

    /**
     * 检查用户是否拥有指定角色
     * @param userId 用户ID
     * @param roleCode 角色代码
     * @return 是否拥有角色
     */
    boolean hasRole(Long userId, String roleCode);

    /**
     * 检查角色是否拥有指定权限
     * @param roleId 角色ID
     * @param permissionCode 权限代码
     * @return 是否拥有权限
     */
    boolean hasPermission(Long roleId, String permissionCode);

    /**
     * 初始化系统默认角色
     * 创建超级管理员、医生、护士、药剂师、前台等预设角色
     */
    void initializeDefaultRoles();
}