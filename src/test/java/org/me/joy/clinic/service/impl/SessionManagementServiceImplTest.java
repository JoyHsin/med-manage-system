package org.me.joy.clinic.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 会话管理服务实现类测试
 */
@ExtendWith(MockitoExtension.class)
class SessionManagementServiceImplTest {

    private SessionManagementServiceImpl sessionManagementService;

    @BeforeEach
    void setUp() {
        sessionManagementService = new SessionManagementServiceImpl();
    }

    @Test
    void testCreateSession_Success() {
        // Given
        String username = "testuser";
        String token = "test-jwt-token";
        LocalDateTime expirationTime = LocalDateTime.now().plusMinutes(30);

        // When
        sessionManagementService.createSession(username, token, expirationTime);

        // Then
        assertTrue(sessionManagementService.isSessionValid(token));
        assertTrue(sessionManagementService.hasActiveSession(username));
    }

    @Test
    void testIsSessionValid_ValidSession() {
        // Given
        String token = "valid-token";
        LocalDateTime expirationTime = LocalDateTime.now().plusMinutes(30);
        sessionManagementService.createSession("testuser", token, expirationTime);

        // When
        boolean result = sessionManagementService.isSessionValid(token);

        // Then
        assertTrue(result);
    }

    @Test
    void testIsSessionValid_ExpiredSession() {
        // Given
        String token = "expired-token";
        LocalDateTime expirationTime = LocalDateTime.now().minusMinutes(1); // 已过期
        sessionManagementService.createSession("testuser", token, expirationTime);

        // When
        boolean result = sessionManagementService.isSessionValid(token);

        // Then
        assertFalse(result);
    }

    @Test
    void testIsSessionValid_NonExistentSession() {
        // When
        boolean result = sessionManagementService.isSessionValid("non-existent-token");

        // Then
        assertFalse(result);
    }

    @Test
    void testIsSessionValid_NullToken() {
        // When
        boolean result = sessionManagementService.isSessionValid(null);

        // Then
        assertFalse(result);
    }

    @Test
    void testIsSessionValid_EmptyToken() {
        // When
        boolean result = sessionManagementService.isSessionValid("");

        // Then
        assertFalse(result);
    }

    @Test
    void testRefreshSession_Success() {
        // Given
        String token = "refresh-token";
        LocalDateTime originalExpiration = LocalDateTime.now().plusMinutes(10);
        LocalDateTime newExpiration = LocalDateTime.now().plusMinutes(30);
        
        sessionManagementService.createSession("testuser", token, originalExpiration);

        // When
        sessionManagementService.refreshSession(token, newExpiration);

        // Then
        assertTrue(sessionManagementService.isSessionValid(token));
        long remainingMinutes = sessionManagementService.getSessionRemainingMinutes(token);
        assertTrue(remainingMinutes > 25); // 应该接近30分钟
    }

    @Test
    void testRefreshSession_NonExistentSession() {
        // Given
        String token = "non-existent-token";
        LocalDateTime newExpiration = LocalDateTime.now().plusMinutes(30);

        // When
        sessionManagementService.refreshSession(token, newExpiration);

        // Then
        assertFalse(sessionManagementService.isSessionValid(token));
    }

    @Test
    void testDestroySession_Success() {
        // Given
        String token = "destroy-token";
        LocalDateTime expirationTime = LocalDateTime.now().plusMinutes(30);
        sessionManagementService.createSession("testuser", token, expirationTime);

        // When
        sessionManagementService.destroySession(token);

        // Then
        assertFalse(sessionManagementService.isSessionValid(token));
        assertFalse(sessionManagementService.hasActiveSession("testuser"));
    }

    @Test
    void testDestroySession_NonExistentSession() {
        // When & Then (should not throw exception)
        assertDoesNotThrow(() -> sessionManagementService.destroySession("non-existent-token"));
    }

    @Test
    void testDestroyAllUserSessions_Success() {
        // Given
        String username = "testuser";
        String token = "user-token";
        LocalDateTime expirationTime = LocalDateTime.now().plusMinutes(30);
        sessionManagementService.createSession(username, token, expirationTime);

        // When
        sessionManagementService.destroyAllUserSessions(username);

        // Then
        assertFalse(sessionManagementService.isSessionValid(token));
        assertFalse(sessionManagementService.hasActiveSession(username));
    }

    @Test
    void testDestroyAllUserSessions_NonExistentUser() {
        // When & Then (should not throw exception)
        assertDoesNotThrow(() -> sessionManagementService.destroyAllUserSessions("non-existent-user"));
    }

    @Test
    void testGetSessionRemainingMinutes_ValidSession() {
        // Given
        String token = "remaining-token";
        LocalDateTime expirationTime = LocalDateTime.now().plusMinutes(25);
        sessionManagementService.createSession("testuser", token, expirationTime);

        // When
        long remainingMinutes = sessionManagementService.getSessionRemainingMinutes(token);

        // Then
        assertTrue(remainingMinutes > 20 && remainingMinutes <= 25);
    }

    @Test
    void testGetSessionRemainingMinutes_ExpiredSession() {
        // Given
        String token = "expired-remaining-token";
        LocalDateTime expirationTime = LocalDateTime.now().minusMinutes(1);
        sessionManagementService.createSession("testuser", token, expirationTime);

        // When
        long remainingMinutes = sessionManagementService.getSessionRemainingMinutes(token);

        // Then
        assertEquals(0, remainingMinutes);
    }

    @Test
    void testGetSessionRemainingMinutes_NonExistentSession() {
        // When
        long remainingMinutes = sessionManagementService.getSessionRemainingMinutes("non-existent-token");

        // Then
        assertEquals(0, remainingMinutes);
    }

    @Test
    void testHasActiveSession_ActiveSession() {
        // Given
        String username = "activeuser";
        String token = "active-token";
        LocalDateTime expirationTime = LocalDateTime.now().plusMinutes(30);
        sessionManagementService.createSession(username, token, expirationTime);

        // When
        boolean hasActiveSession = sessionManagementService.hasActiveSession(username);

        // Then
        assertTrue(hasActiveSession);
    }

    @Test
    void testHasActiveSession_ExpiredSession() {
        // Given
        String username = "expireduser";
        String token = "expired-active-token";
        LocalDateTime expirationTime = LocalDateTime.now().minusMinutes(1);
        sessionManagementService.createSession(username, token, expirationTime);

        // When
        boolean hasActiveSession = sessionManagementService.hasActiveSession(username);

        // Then
        assertFalse(hasActiveSession);
    }

    @Test
    void testHasActiveSession_NoSession() {
        // When
        boolean hasActiveSession = sessionManagementService.hasActiveSession("no-session-user");

        // Then
        assertFalse(hasActiveSession);
    }

    @Test
    void testCleanupExpiredSessions() {
        // Given
        String validToken = "valid-cleanup-token";
        String expiredToken = "expired-cleanup-token";
        
        LocalDateTime validExpiration = LocalDateTime.now().plusMinutes(30);
        LocalDateTime expiredExpiration = LocalDateTime.now().minusMinutes(1);
        
        sessionManagementService.createSession("validuser", validToken, validExpiration);
        sessionManagementService.createSession("expireduser", expiredToken, expiredExpiration);

        // When
        sessionManagementService.cleanupExpiredSessions();

        // Then
        assertTrue(sessionManagementService.isSessionValid(validToken));
        assertFalse(sessionManagementService.isSessionValid(expiredToken));
        assertTrue(sessionManagementService.hasActiveSession("validuser"));
        assertFalse(sessionManagementService.hasActiveSession("expireduser"));
    }

    @Test
    void testMultipleSessionsForSameUser() {
        // Given
        String username = "multiuser";
        String firstToken = "first-token";
        String secondToken = "second-token";
        LocalDateTime expirationTime = LocalDateTime.now().plusMinutes(30);

        // When
        sessionManagementService.createSession(username, firstToken, expirationTime);
        sessionManagementService.createSession(username, secondToken, expirationTime);

        // Then
        // 当前实现允许同一用户有多个会话
        assertTrue(sessionManagementService.isSessionValid(firstToken));
        assertTrue(sessionManagementService.isSessionValid(secondToken));
        assertTrue(sessionManagementService.hasActiveSession(username));
    }
}