package org.me.joy.clinic.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.me.joy.clinic.entity.User;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

/**
 * 会话超时测试
 */
@ExtendWith(MockitoExtension.class)
class SessionTimeoutTest {

    @Mock
    private UserDetails userDetails;

    @InjectMocks
    private JwtUtil jwtUtil;

    private User testUser;

    @BeforeEach
    void setUp() {
        // 设置JWT配置
        ReflectionTestUtils.setField(jwtUtil, "secret", "mySecretKeyForTesting");
        ReflectionTestUtils.setField(jwtUtil, "expiration", 1800000L); // 30分钟

        testUser = new User();
        testUser.setUsername("testuser");
        testUser.setFullName("Test User");

        when(userDetails.getUsername()).thenReturn("testuser");
    }

    @Test
    void testTokenExpiration_30Minutes() {
        // Given - 生成token
        String token = jwtUtil.generateToken(userDetails);
        
        // When - 验证token立即有效
        assertTrue(jwtUtil.validateToken(token));
        
        // Then - 验证过期时间设置正确（30分钟）
        Date expiration = jwtUtil.extractExpiration(token);
        Date now = new Date();
        long timeDiff = expiration.getTime() - now.getTime();
        
        // 允许一些时间误差（±1秒）
        assertTrue(timeDiff >= 1799000 && timeDiff <= 1801000, 
            "Token expiration should be approximately 30 minutes (1800000ms), but was: " + timeDiff);
    }

    @Test
    void testTokenValidation_BeforeExpiration() {
        // Given
        String token = jwtUtil.generateToken(userDetails);
        
        // When & Then - token应该在过期前有效
        assertTrue(jwtUtil.validateToken(token));
        assertTrue(jwtUtil.validateToken(token, userDetails));
    }

    @Test
    void testTokenValidation_AfterExpiration() {
        // Given - 创建一个已过期的token（设置过期时间为1毫秒）
        ReflectionTestUtils.setField(jwtUtil, "expiration", 1L);
        String expiredToken = jwtUtil.generateToken(userDetails);
        
        // 等待token过期
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        // When & Then - 过期的token应该无效
        assertFalse(jwtUtil.validateToken(expiredToken));
        assertFalse(jwtUtil.validateToken(expiredToken, userDetails));
    }

    @Test
    void testGetTokenRemainingTime() {
        // Given
        String token = jwtUtil.generateToken(userDetails);
        
        // When
        Long remainingTime = jwtUtil.getTokenRemainingTime(token);
        
        // Then - 剩余时间应该接近30分钟
        assertTrue(remainingTime > 1790000 && remainingTime <= 1800000,
            "Remaining time should be close to 30 minutes, but was: " + remainingTime);
    }

    @Test
    void testTokenRefresh() {
        // Given
        String originalToken = jwtUtil.generateToken(userDetails);
        
        // When
        String refreshedToken = jwtUtil.refreshToken(originalToken);
        
        // Then
        assertNotEquals(originalToken, refreshedToken);
        assertTrue(jwtUtil.validateToken(refreshedToken));
        
        // 新token的剩余时间应该接近30分钟
        Long remainingTime = jwtUtil.getTokenRemainingTime(refreshedToken);
        assertTrue(remainingTime > 1790000 && remainingTime <= 1800000);
    }

    @Test
    void testExtractUsername() {
        // Given
        String token = jwtUtil.generateToken(userDetails);
        
        // When
        String extractedUsername = jwtUtil.extractUsername(token);
        
        // Then
        assertEquals("testuser", extractedUsername);
    }

    @Test
    void testInvalidToken() {
        // Given
        String invalidToken = "invalid.jwt.token";
        
        // When & Then
        assertFalse(jwtUtil.validateToken(invalidToken));
        
        assertThrows(IllegalArgumentException.class, () -> {
            jwtUtil.extractUsername(invalidToken);
        });
    }

    @Test
    void testNullToken() {
        // When & Then
        assertFalse(jwtUtil.validateToken(null));
        
        assertThrows(IllegalArgumentException.class, () -> {
            jwtUtil.extractUsername(null);
        });
    }

    @Test
    void testEmptyToken() {
        // When & Then
        assertFalse(jwtUtil.validateToken(""));
        
        assertThrows(IllegalArgumentException.class, () -> {
            jwtUtil.extractUsername("");
        });
    }
}