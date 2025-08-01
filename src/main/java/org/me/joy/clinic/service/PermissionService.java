package org.me.joy.clinic.service;

import org.me.joy.clinic.entity.Permission;
import org.me.joy.clinic.entity.User;

import java.util.List;

/**
 * 权限服务接口
 * 提供权限检查和权限管理功能
 */
public interface PermissionService {

    /**
     * 获取所有权限
     * @return 权限列表
     */
    List<Permission> getAllPermissions();

    /**
     * 获取所有启用的权限
     * @return 启用的权限列表
     */
    List<Permission> getEnabledPermissions();

    /**
     * 根据模块获取权限
     * @param module 模块名称
     * @return 权限列表
     */
    List<Permission> getPermissionsByModule(String module);

    /**
     * 根据ID获取权限
     * @param permissionId 权限ID
     * @return 权限信息
     */
    Permission getPermissionById(Long permissionId);

    /**
     * 根据权限代码获取权限
     * @param permissionCode 权限代码
     * @return 权限信息
     */
    Permission getPermissionByCode(String permissionCode);

    /**
     * 获取用户的所有权限
     * @param userId 用户ID
     * @return 权限列表
     */
    List<Permission> getUserPermissions(Long userId);

    /**
     * 获取用户的所有权限代码
     * @param userId 用户ID
     * @return 权限代码列表
     */
    List<String> getUserPermissionCodes(Long userId);

    /**
     * 检查用户是否拥有指定权限
     * @param userId 用户ID
     * @param permissionCode 权限代码
     * @return 是否拥有权限
     */
    boolean hasPermission(Long userId, String permissionCode);

    /**
     * 检查用户是否拥有指定权限中的任意一个
     * @param userId 用户ID
     * @param permissionCodes 权限代码数组
     * @return 是否拥有权限
     */
    boolean hasAnyPermission(Long userId, String[] permissionCodes);

    /**
     * 检查用户是否拥有所有指定权限
     * @param userId 用户ID
     * @param permissionCodes 权限代码数组
     * @return 是否拥有所有权限
     */
    boolean hasAllPermissions(Long userId, String[] permissionCodes);

    /**
     * 检查当前登录用户是否拥有指定权限
     * @param permissionCode 权限代码
     * @return 是否拥有权限
     */
    boolean hasPermission(String permissionCode);

    /**
     * 检查当前登录用户是否拥有指定权限中的任意一个
     * @param permissionCodes 权限代码数组
     * @return 是否拥有权限
     */
    boolean hasAnyPermission(String[] permissionCodes);

    /**
     * 检查当前登录用户是否拥有所有指定权限
     * @param permissionCodes 权限代码数组
     * @return 是否拥有所有权限
     */
    boolean hasAllPermissions(String[] permissionCodes);

    /**
     * 获取当前登录用户
     * @return 当前用户
     */
    User getCurrentUser();

    /**
     * 获取当前登录用户ID
     * @return 当前用户ID
     */
    Long getCurrentUserId();
}