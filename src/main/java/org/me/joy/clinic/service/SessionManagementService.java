package org.me.joy.clinic.service;

import java.time.LocalDateTime;

/**
 * 会话管理服务接口
 * 负责用户会话的创建、验证和清理
 */
public interface SessionManagementService {

    /**
     * 创建用户会话
     * @param username 用户名
     * @param token JWT令牌
     * @param expirationTime 过期时间
     */
    void createSession(String username, String token, LocalDateTime expirationTime);

    /**
     * 验证会话是否有效
     * @param token JWT令牌
     * @return 是否有效
     */
    boolean isSessionValid(String token);

    /**
     * 刷新会话过期时间
     * @param token JWT令牌
     * @param newExpirationTime 新的过期时间
     */
    void refreshSession(String token, LocalDateTime newExpirationTime);

    /**
     * 销毁用户会话
     * @param token JWT令牌
     */
    void destroySession(String token);

    /**
     * 销毁用户的所有会话
     * @param username 用户名
     */
    void destroyAllUserSessions(String username);

    /**
     * 清理过期会话
     */
    void cleanupExpiredSessions();

    /**
     * 获取会话剩余时间（分钟）
     * @param token JWT令牌
     * @return 剩余时间，如果会话不存在或已过期返回0
     */
    long getSessionRemainingMinutes(String token);

    /**
     * 检查用户是否有活跃会话
     * @param username 用户名
     * @return 是否有活跃会话
     */
    boolean hasActiveSession(String username);
}