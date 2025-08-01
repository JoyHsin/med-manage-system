package org.me.joy.clinic.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.me.joy.clinic.dto.ChangePasswordRequest;
import org.me.joy.clinic.entity.User;
import org.me.joy.clinic.exception.BusinessException;
import org.me.joy.clinic.exception.ValidationException;
import org.me.joy.clinic.mapper.UserMapper;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

/**
 * 密码服务实现类测试
 */
@ExtendWith(MockitoExtension.class)
class PasswordServiceImplTest {

    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private PasswordServiceImpl passwordService;

    private User testUser;
    private ChangePasswordRequest validRequest;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setPassword("$2a$10$encodedCurrentPassword");
        testUser.setFullName("Test User");
        testUser.setPasswordChangedTime(LocalDateTime.now().minusDays(30));

        validRequest = new ChangePasswordRequest();
        validRequest.setCurrentPassword("currentPassword123");
        validRequest.setNewPassword("NewPassword123!");
        validRequest.setConfirmPassword("NewPassword123!");
    }

    @Test
    void testChangePassword_Success() {
        // Given
        when(userMapper.findByUsernameOptional("testuser")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("currentPassword123", testUser.getPassword())).thenReturn(true);
        when(passwordEncoder.matches("NewPassword123!", testUser.getPassword())).thenReturn(false);
        when(passwordEncoder.encode("NewPassword123!")).thenReturn("$2a$10$encodedNewPassword");
        when(userMapper.updateById(any(User.class))).thenReturn(1);

        // When
        passwordService.changePassword("testuser", validRequest);

        // Then
        verify(userMapper, times(2)).findByUsernameOptional("testuser"); // Called twice: once in changePassword, once in validateCurrentPassword
        verify(passwordEncoder).encode("NewPassword123!");
        verify(userMapper).updateById(testUser);
        
        assertEquals("$2a$10$encodedNewPassword", testUser.getPassword());
        assertTrue(testUser.getCredentialsNonExpired());
        assertNotNull(testUser.getPasswordChangedTime());
    }

    @Test
    void testChangePassword_UserNotFound() {
        // Given
        when(userMapper.findByUsernameOptional("nonexistent")).thenReturn(Optional.empty());

        // When & Then
        BusinessException exception = assertThrows(BusinessException.class, 
            () -> passwordService.changePassword("nonexistent", validRequest));
        
        assertEquals("2001", exception.getErrorCode());
        assertEquals("用户不存在", exception.getMessage());
    }

    @Test
    void testChangePassword_CurrentPasswordIncorrect() {
        // Given
        when(userMapper.findByUsernameOptional("testuser")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("currentPassword123", testUser.getPassword())).thenReturn(false);

        // When & Then
        BusinessException exception = assertThrows(BusinessException.class, 
            () -> passwordService.changePassword("testuser", validRequest));
        
        assertEquals("2002", exception.getErrorCode());
        assertEquals("当前密码不正确", exception.getMessage());
    }

    @Test
    void testChangePassword_NewPasswordSameAsCurrent() {
        // Given - use a password that meets complexity requirements
        validRequest.setNewPassword("CurrentPass123!");
        validRequest.setConfirmPassword("CurrentPass123!");
        
        when(userMapper.findByUsernameOptional("testuser")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("currentPassword123", testUser.getPassword())).thenReturn(true);
        when(passwordEncoder.matches("CurrentPass123!", testUser.getPassword())).thenReturn(true); // Same as current

        // When & Then
        ValidationException exception = assertThrows(ValidationException.class, 
            () -> passwordService.changePassword("testuser", validRequest));
        
        assertEquals("2004", exception.getErrorCode());
        assertEquals("新密码不能与当前密码相同", exception.getMessage());
    }

    @Test
    void testChangePassword_PasswordMismatch() {
        // Given
        validRequest.setConfirmPassword("DifferentPassword123!");
        
        when(userMapper.findByUsernameOptional("testuser")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("currentPassword123", testUser.getPassword())).thenReturn(true);
        when(passwordEncoder.matches("NewPassword123!", testUser.getPassword())).thenReturn(false);

        // When & Then
        ValidationException exception = assertThrows(ValidationException.class, 
            () -> passwordService.changePassword("testuser", validRequest));
        
        assertEquals("2005", exception.getErrorCode());
        assertEquals("新密码与确认密码不一致", exception.getMessage());
    }

    @Test
    void testChangePassword_NullRequest() {
        // When & Then
        ValidationException exception = assertThrows(ValidationException.class, 
            () -> passwordService.changePassword("testuser", null));
        
        assertEquals("2006", exception.getErrorCode());
        assertEquals("修改密码请求不能为空", exception.getMessage());
    }

    @Test
    void testChangePassword_EmptyCurrentPassword() {
        // Given
        validRequest.setCurrentPassword("");

        // When & Then
        ValidationException exception = assertThrows(ValidationException.class, 
            () -> passwordService.changePassword("testuser", validRequest));
        
        assertEquals("2007", exception.getErrorCode());
        assertEquals("当前密码不能为空", exception.getMessage());
    }

    @Test
    void testChangePassword_EmptyNewPassword() {
        // Given
        validRequest.setNewPassword("");

        // When & Then
        ValidationException exception = assertThrows(ValidationException.class, 
            () -> passwordService.changePassword("testuser", validRequest));
        
        assertEquals("2008", exception.getErrorCode());
        assertEquals("新密码不能为空", exception.getMessage());
    }

    @Test
    void testChangePassword_EmptyConfirmPassword() {
        // Given
        validRequest.setConfirmPassword("");

        // When & Then
        ValidationException exception = assertThrows(ValidationException.class, 
            () -> passwordService.changePassword("testuser", validRequest));
        
        assertEquals("2009", exception.getErrorCode());
        assertEquals("确认密码不能为空", exception.getMessage());
    }

    @Test
    void testValidatePasswordComplexity_ValidPassword() {
        // Test valid passwords
        assertTrue(passwordService.validatePasswordComplexity("Password123!"));
        assertTrue(passwordService.validatePasswordComplexity("MySecure@Pass1"));
        assertTrue(passwordService.validatePasswordComplexity("Complex$Password9"));
    }

    @Test
    void testValidatePasswordComplexity_InvalidPasswords() {
        // Test invalid passwords
        assertFalse(passwordService.validatePasswordComplexity(null));
        assertFalse(passwordService.validatePasswordComplexity(""));
        assertFalse(passwordService.validatePasswordComplexity("   "));
        assertFalse(passwordService.validatePasswordComplexity("short")); // Too short
        assertFalse(passwordService.validatePasswordComplexity("password123")); // No uppercase
        assertFalse(passwordService.validatePasswordComplexity("PASSWORD123")); // No lowercase
        assertFalse(passwordService.validatePasswordComplexity("Password")); // No number
        assertFalse(passwordService.validatePasswordComplexity("Password123")); // No special character
        assertFalse(passwordService.validatePasswordComplexity("a".repeat(51))); // Too long
    }

    @Test
    void testValidateCurrentPassword_Success() {
        // Given
        when(userMapper.findByUsernameOptional("testuser")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("currentPassword", testUser.getPassword())).thenReturn(true);

        // When
        boolean result = passwordService.validateCurrentPassword("testuser", "currentPassword");

        // Then
        assertTrue(result);
    }

    @Test
    void testValidateCurrentPassword_UserNotFound() {
        // Given
        when(userMapper.findByUsernameOptional("nonexistent")).thenReturn(Optional.empty());

        // When
        boolean result = passwordService.validateCurrentPassword("nonexistent", "password");

        // Then
        assertFalse(result);
    }

    @Test
    void testValidateCurrentPassword_PasswordMismatch() {
        // Given
        when(userMapper.findByUsernameOptional("testuser")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("wrongPassword", testUser.getPassword())).thenReturn(false);

        // When
        boolean result = passwordService.validateCurrentPassword("testuser", "wrongPassword");

        // Then
        assertFalse(result);
    }

    @Test
    void testEncodePassword() {
        // Given
        when(passwordEncoder.encode("rawPassword")).thenReturn("encodedPassword");

        // When
        String result = passwordService.encodePassword("rawPassword");

        // Then
        assertEquals("encodedPassword", result);
        verify(passwordEncoder).encode("rawPassword");
    }

    @Test
    void testMatches() {
        // Given
        when(passwordEncoder.matches("rawPassword", "encodedPassword")).thenReturn(true);

        // When
        boolean result = passwordService.matches("rawPassword", "encodedPassword");

        // Then
        assertTrue(result);
        verify(passwordEncoder).matches("rawPassword", "encodedPassword");
    }
}