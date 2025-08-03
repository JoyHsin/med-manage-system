package org.me.joy.clinic.service;

import org.me.joy.clinic.dto.AuthenticationResponse;
import org.me.joy.clinic.dto.ChangePasswordRequest;
import org.me.joy.clinic.dto.LoginRequest;
import org.me.joy.clinic.dto.UserResponse;

/**
 * 认证服务接口
 */
public interface AuthenticationService {

    /**
     * 用户登录认证
     * @param loginRequest 登录请求
     * @return 认证响应
     */
    AuthenticationResponse authenticate(LoginRequest loginRequest);

    /**
     * 用户登出
     * @param token JWT令牌
     */
    void logout(String token);

    /**
     * 验证令牌是否有效
     * @param token JWT令牌
     * @return 是否有效
     */
    boolean validateToken(String token);

    /**
     * 刷新令牌
     * @param token 当前令牌
     * @return 新的认证响应
     */
    AuthenticationResponse refreshToken(String token);

    /**
     * 修改密码
     * @param username 用户名
     * @param changePasswordRequest 修改密码请求
     */
    void changePassword(String username, ChangePasswordRequest changePasswordRequest);

    /**
     * 获取当前用户信息（包括角色和权限）
     * @param username 用户名
     * @return 用户信息
     */
    UserResponse getCurrentUserInfo(String username);
}