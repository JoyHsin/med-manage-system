package org.me.joy.clinic.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.me.joy.clinic.dto.ChangePasswordRequest;
import org.me.joy.clinic.dto.LoginRequest;
import org.me.joy.clinic.entity.User;
import org.me.joy.clinic.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 密码管理集成测试
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class PasswordManagementIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private User testUser;
    private String authToken;

    @BeforeEach
    void setUp() throws Exception {
        // 创建测试用户
        testUser = new User();
        testUser.setUsername("testuser");
        testUser.setPassword(passwordEncoder.encode("CurrentPass123!"));
        testUser.setFullName("Test User");
        testUser.setEmail("test@example.com");
        testUser.setEnabled(true);
        testUser.setAccountNonExpired(true);
        testUser.setAccountNonLocked(true);
        testUser.setCredentialsNonExpired(true);
        
        userMapper.insert(testUser);

        // 获取认证令牌
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("testuser");
        loginRequest.setPassword("CurrentPass123!");

        MvcResult loginResult = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn();

        String loginResponse = loginResult.getResponse().getContentAsString();
        // 从响应中提取token（简化处理，实际应该解析JSON）
        authToken = "Bearer " + extractTokenFromResponse(loginResponse);
    }

    @Test
    void testChangePassword_Success() throws Exception {
        // Given
        ChangePasswordRequest request = new ChangePasswordRequest();
        request.setCurrentPassword("CurrentPass123!");
        request.setNewPassword("NewPassword123!");
        request.setConfirmPassword("NewPassword123!");

        // When & Then
        mockMvc.perform(post("/api/auth/change-password")
                .header("Authorization", authToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("密码修改成功"));

        // 验证密码已更新
        User updatedUser = userMapper.findByUsername("testuser");
        assertNotNull(updatedUser);
        assertTrue(passwordEncoder.matches("NewPassword123!", updatedUser.getPassword()));
        assertNotNull(updatedUser.getPasswordChangedTime());
    }

    @Test
    void testChangePassword_InvalidCurrentPassword() throws Exception {
        // Given
        ChangePasswordRequest request = new ChangePasswordRequest();
        request.setCurrentPassword("WrongPassword123!");
        request.setNewPassword("NewPassword123!");
        request.setConfirmPassword("NewPassword123!");

        // When & Then
        mockMvc.perform(post("/api/auth/change-password")
                .header("Authorization", authToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("当前密码不正确"));
    }

    @Test
    void testChangePassword_WeakNewPassword() throws Exception {
        // Given
        ChangePasswordRequest request = new ChangePasswordRequest();
        request.setCurrentPassword("CurrentPass123!");
        request.setNewPassword("weak");
        request.setConfirmPassword("weak");

        // When & Then
        mockMvc.perform(post("/api/auth/change-password")
                .header("Authorization", authToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    void testChangePassword_PasswordMismatch() throws Exception {
        // Given
        ChangePasswordRequest request = new ChangePasswordRequest();
        request.setCurrentPassword("CurrentPass123!");
        request.setNewPassword("NewPassword123!");
        request.setConfirmPassword("DifferentPassword123!");

        // When & Then
        mockMvc.perform(post("/api/auth/change-password")
                .header("Authorization", authToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("新密码与确认密码不一致"));
    }

    @Test
    void testChangePassword_SameAsCurrentPassword() throws Exception {
        // Given
        ChangePasswordRequest request = new ChangePasswordRequest();
        request.setCurrentPassword("CurrentPass123!");
        request.setNewPassword("CurrentPass123!");
        request.setConfirmPassword("CurrentPass123!");

        // When & Then
        mockMvc.perform(post("/api/auth/change-password")
                .header("Authorization", authToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("新密码不能与当前密码相同"));
    }

    @Test
    void testChangePassword_NoAuthToken() throws Exception {
        // Given
        ChangePasswordRequest request = new ChangePasswordRequest();
        request.setCurrentPassword("CurrentPass123!");
        request.setNewPassword("NewPassword123!");
        request.setConfirmPassword("NewPassword123!");

        // When & Then
        mockMvc.perform(post("/api/auth/change-password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("未提供有效的认证令牌"));
    }

    @Test
    void testChangePassword_InvalidAuthToken() throws Exception {
        // Given
        ChangePasswordRequest request = new ChangePasswordRequest();
        request.setCurrentPassword("CurrentPass123!");
        request.setNewPassword("NewPassword123!");
        request.setConfirmPassword("NewPassword123!");

        // When & Then
        mockMvc.perform(post("/api/auth/change-password")
                .header("Authorization", "Bearer invalid-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    void testChangePassword_ValidationErrors() throws Exception {
        // Given - 空的请求体
        ChangePasswordRequest request = new ChangePasswordRequest();

        // When & Then
        mockMvc.perform(post("/api/auth/change-password")
                .header("Authorization", authToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    /**
     * 从登录响应中提取token（简化实现）
     */
    private String extractTokenFromResponse(String response) {
        try {
            // 简化的token提取，实际应该解析JSON
            int tokenStart = response.indexOf("\"token\":\"") + 9;
            int tokenEnd = response.indexOf("\"", tokenStart);
            return response.substring(tokenStart, tokenEnd);
        } catch (Exception e) {
            throw new RuntimeException("Failed to extract token from response", e);
        }
    }
}