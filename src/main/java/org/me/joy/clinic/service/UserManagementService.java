package org.me.joy.clinic.service;

import org.me.joy.clinic.dto.CreateUserRequest;
import org.me.joy.clinic.dto.UpdateUserRequest;
import org.me.joy.clinic.dto.UserResponse;
import org.me.joy.clinic.entity.User;

import java.util.List;

/**
 * 用户管理服务接口
 * 负责用户账号的创建、编辑、禁用、删除等管理功能
 */
public interface UserManagementService {

    /**
     * 创建新用户账号
     * @param createUserRequest 创建用户请求
     * @return 创建的用户信息
     */
    UserResponse createUser(CreateUserRequest createUserRequest);

    /**
     * 更新用户信息
     * @param userId 用户ID
     * @param updateUserRequest 更新用户请求
     * @return 更新后的用户信息
     */
    UserResponse updateUser(Long userId, UpdateUserRequest updateUserRequest);

    /**
     * 根据ID获取用户信息
     * @param userId 用户ID
     * @return 用户信息
     */
    UserResponse getUserById(Long userId);

    /**
     * 根据用户名获取用户信息
     * @param username 用户名
     * @return 用户信息
     */
    UserResponse getUserByUsername(String username);

    /**
     * 获取所有用户列表
     * @return 用户列表
     */
    List<UserResponse> getAllUsers();

    /**
     * 获取启用状态的用户列表
     * @param enabled 是否启用
     * @return 用户列表
     */
    List<UserResponse> getUsersByEnabled(Boolean enabled);

    /**
     * 禁用用户账号
     * @param userId 用户ID
     */
    void disableUser(Long userId);

    /**
     * 启用用户账号
     * @param userId 用户ID
     */
    void enableUser(Long userId);

    /**
     * 删除用户账号（逻辑删除）
     * @param userId 用户ID
     */
    void deleteUser(Long userId);

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
     * 重置用户密码
     * @param userId 用户ID
     * @param newPassword 新密码
     */
    void resetUserPassword(Long userId, String newPassword);

    /**
     * 检查用户名是否存在
     * @param username 用户名
     * @return 是否存在
     */
    boolean existsByUsername(String username);

    /**
     * 检查邮箱是否存在
     * @param email 邮箱
     * @return 是否存在
     */
    boolean existsByEmail(String email);

    /**
     * 根据部门获取用户列表
     * @param department 部门
     * @return 用户列表
     */
    List<UserResponse> getUsersByDepartment(String department);

    /**
     * 搜索用户
     * @param keyword 关键词（用户名、姓名、邮箱）
     * @return 用户列表
     */
    List<UserResponse> searchUsers(String keyword);
}