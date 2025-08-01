package org.me.joy.clinic.service;

import org.me.joy.clinic.dto.ChangePasswordRequest;

/**
 * 密码管理服务接口
 */
public interface PasswordService {

    /**
     * 修改用户密码
     * @param username 用户名
     * @param changePasswordRequest 修改密码请求
     */
    void changePassword(String username, ChangePasswordRequest changePasswordRequest);

    /**
     * 验证密码复杂度
     * @param password 密码
     * @return 是否符合复杂度要求
     */
    boolean validatePasswordComplexity(String password);

    /**
     * 验证当前密码是否正确
     * @param username 用户名
     * @param currentPassword 当前密码
     * @return 是否正确
     */
    boolean validateCurrentPassword(String username, String currentPassword);

    /**
     * 加密密码
     * @param rawPassword 原始密码
     * @return 加密后的密码
     */
    String encodePassword(String rawPassword);

    /**
     * 验证密码是否匹配
     * @param rawPassword 原始密码
     * @param encodedPassword 加密后的密码
     * @return 是否匹配
     */
    boolean matches(String rawPassword, String encodedPassword);
}