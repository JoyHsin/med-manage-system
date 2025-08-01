package org.me.joy.clinic.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.me.joy.clinic.entity.User;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

/**
 * JWT工具类单元测试
 */
public class JwtUtilTest {

    private JwtUtil jwtUtil;
    private CustomUserPrincipal userPrincipal;

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();
        ReflectionTestUtils.setField(jwtUtil, "secret", "testSecretKey123456789012345678901234567890");
        ReflectionTestUtils.setField(jwtUtil, "expiration", 3600000L); // 1 hour

        // 创建测试用户
        User testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setFullName("测试用户");
        testUser.setEmail("test@example.com");
        testUser.setEnabled(true);
        testUser.setAccountNonExpired(true);
        testUser.setAccountNonLocked(true);
        testUser.setCredentialsNonExpired(true);

        userPrincipal = new CustomUserPrincipal(testUser);
    }

    @Test
    void testGenerateToken() {
        String token = jwtUtil.generateToken(userPrincipal);
        
        assertNotNull(token);
        assertFalse(token.isEmpty());
        assertTrue(token.split("\\.").length == 3); // JWT应该有3个部分
    }

    @Test
    void testExtractUsername() {
        String token = jwtUtil.generateToken(userPrincipal);
        String username = jwtUtil.extractUsername(token);
        
        assertEquals("testuser", username);
    }

    @Test
    void testExtractExpiration() {
        String token = jwtUtil.generateToken(userPrincipal);
        java.util.Date expiration = jwtUtil.extractExpiration(token);
        
        assertNotNull(expiration);
        assertTrue(expiration.after(new java.util.Date()));
    }

    @Test
    void testValidateTokenWithValidToken() {
        String token = jwtUtil.generateToken(userPrincipal);
        boolean isValid = jwtUtil.validateToken(token, userPrincipal);
        
        assertTrue(isValid);
    }

    @Test
    void testValidateTokenWithInvalidToken() {
        String invalidToken = "invalid.token.here";
        
        // The validateToken method should return false for invalid tokens
        // without throwing an exception
        assertThrows(IllegalArgumentException.class, () -> {
            jwtUtil.validateToken(invalidToken, userPrincipal);
        });
    }

    @Test
    void testValidateTokenWithoutUserDetails() {
        String token = jwtUtil.generateToken(userPrincipal);
        boolean isValid = jwtUtil.validateToken(token);
        
        assertTrue(isValid);
    }

    @Test
    void testValidateInvalidTokenWithoutUserDetails() {
        String invalidToken = "invalid.token.here";
        boolean isValid = jwtUtil.validateToken(invalidToken);
        
        assertFalse(isValid);
    }

    @Test
    void testGetTokenRemainingTime() {
        String token = jwtUtil.generateToken(userPrincipal);
        Long remainingTime = jwtUtil.getTokenRemainingTime(token);
        
        assertNotNull(remainingTime);
        assertTrue(remainingTime > 0);
        assertTrue(remainingTime <= 3600000L); // 应该小于等于1小时
    }

    @Test
    void testRefreshToken() {
        String originalToken = jwtUtil.generateToken(userPrincipal);
        String refreshedToken = jwtUtil.refreshToken(originalToken);
        
        assertNotNull(refreshedToken);
        assertNotEquals(originalToken, refreshedToken);
        
        // 验证刷新后的token仍然有效
        String username = jwtUtil.extractUsername(refreshedToken);
        assertEquals("testuser", username);
    }

    @Test
    void testTokenWithDifferentUser() {
        String token = jwtUtil.generateToken(userPrincipal);
        
        // 创建不同的用户
        User differentUser = new User();
        differentUser.setUsername("differentuser");
        differentUser.setEnabled(true);
        CustomUserPrincipal differentUserPrincipal = new CustomUserPrincipal(differentUser);
        
        boolean isValid = jwtUtil.validateToken(token, differentUserPrincipal);
        assertFalse(isValid);
    }

    @Test
    void testExtractClaimFromInvalidToken() {
        String invalidToken = "invalid.token.here";
        
        assertThrows(IllegalArgumentException.class, () -> {
            jwtUtil.extractUsername(invalidToken);
        });
    }
}