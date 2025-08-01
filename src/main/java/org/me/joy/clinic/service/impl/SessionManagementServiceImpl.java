package org.me.joy.clinic.service.impl;

import org.me.joy.clinic.service.SessionManagementService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 会话管理服务实现类
 * 使用内存存储会话信息（生产环境建议使用Redis）
 */
@Service
public class SessionManagementServiceImpl implements SessionManagementService {

    private static final Logger logger = LoggerFactory.getLogger(SessionManagementServiceImpl.class);

    /**
     * 会话信息存储
     * Key: JWT令牌, Value: 会话信息
     */
    private final Map<String, SessionInfo> sessions = new ConcurrentHashMap<>();

    /**
     * 用户会话映射
     * Key: 用户名, Value: JWT令牌集合
     */
    private final Map<String, String> userSessions = new ConcurrentHashMap<>();

    @Override
    public void createSession(String username, String token, LocalDateTime expirationTime) {
        logger.debug("创建用户会话: username={}, expirationTime={}", username, expirationTime);
        
        SessionInfo sessionInfo = new SessionInfo(username, token, expirationTime);
        sessions.put(token, sessionInfo);
        userSessions.put(username, token);
        
        logger.info("用户会话创建成功: {}", username);
    }

    @Override
    public boolean isSessionValid(String token) {
        if (token == null || token.trim().isEmpty()) {
            return false;
        }

        SessionInfo sessionInfo = sessions.get(token);
        if (sessionInfo == null) {
            logger.debug("会话不存在: {}", token.substring(0, Math.min(token.length(), 10)) + "...");
            return false;
        }

        LocalDateTime now = LocalDateTime.now();
        if (now.isAfter(sessionInfo.getExpirationTime())) {
            logger.debug("会话已过期: username={}, expirationTime={}", 
                sessionInfo.getUsername(), sessionInfo.getExpirationTime());
            // 清理过期会话
            destroySession(token);
            return false;
        }

        return true;
    }

    @Override
    public void refreshSession(String token, LocalDateTime newExpirationTime) {
        SessionInfo sessionInfo = sessions.get(token);
        if (sessionInfo != null) {
            sessionInfo.setExpirationTime(newExpirationTime);
            sessionInfo.setLastAccessTime(LocalDateTime.now());
            logger.debug("会话刷新成功: username={}, newExpirationTime={}", 
                sessionInfo.getUsername(), newExpirationTime);
        }
    }

    @Override
    public void destroySession(String token) {
        SessionInfo sessionInfo = sessions.remove(token);
        if (sessionInfo != null) {
            userSessions.remove(sessionInfo.getUsername());
            logger.info("会话销毁成功: {}", sessionInfo.getUsername());
        }
    }

    @Override
    public void destroyAllUserSessions(String username) {
        String token = userSessions.get(username);
        if (token != null) {
            sessions.remove(token);
            userSessions.remove(username);
            logger.info("用户所有会话销毁成功: {}", username);
        }
    }

    @Override
    public long getSessionRemainingMinutes(String token) {
        SessionInfo sessionInfo = sessions.get(token);
        if (sessionInfo == null) {
            return 0;
        }

        LocalDateTime now = LocalDateTime.now();
        if (now.isAfter(sessionInfo.getExpirationTime())) {
            return 0;
        }

        return ChronoUnit.MINUTES.between(now, sessionInfo.getExpirationTime());
    }

    @Override
    public boolean hasActiveSession(String username) {
        String token = userSessions.get(username);
        return token != null && isSessionValid(token);
    }

    @Override
    @Scheduled(fixedRate = 300000) // 每5分钟执行一次
    public void cleanupExpiredSessions() {
        LocalDateTime now = LocalDateTime.now();
        int cleanedCount = 0;

        // 使用迭代器安全地删除过期会话
        sessions.entrySet().removeIf(entry -> {
            SessionInfo sessionInfo = entry.getValue();
            if (now.isAfter(sessionInfo.getExpirationTime())) {
                userSessions.remove(sessionInfo.getUsername());
                return true;
            }
            return false;
        });

        if (cleanedCount > 0) {
            logger.info("清理过期会话完成，清理数量: {}", cleanedCount);
        }
    }

    /**
     * 会话信息内部类
     */
    private static class SessionInfo {
        private final String username;
        private final String token;
        private LocalDateTime expirationTime;
        private LocalDateTime lastAccessTime;

        public SessionInfo(String username, String token, LocalDateTime expirationTime) {
            this.username = username;
            this.token = token;
            this.expirationTime = expirationTime;
            this.lastAccessTime = LocalDateTime.now();
        }

        public String getUsername() {
            return username;
        }

        public String getToken() {
            return token;
        }

        public LocalDateTime getExpirationTime() {
            return expirationTime;
        }

        public void setExpirationTime(LocalDateTime expirationTime) {
            this.expirationTime = expirationTime;
        }

        public LocalDateTime getLastAccessTime() {
            return lastAccessTime;
        }

        public void setLastAccessTime(LocalDateTime lastAccessTime) {
            this.lastAccessTime = lastAccessTime;
        }
    }
}