package org.me.joy.clinic.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.me.joy.clinic.dto.CreateUserRequest;
import org.me.joy.clinic.dto.UpdateUserRequest;
import org.me.joy.clinic.dto.UserResponse;
import org.me.joy.clinic.service.UserManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 用户管理控制器测试
 */
@WebMvcTest(UserManagementController.class)
class UserManagementControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserManagementService userManagementService;

    private CreateUserRequest createUserRequest;
    private UpdateUserRequest updateUserRequest;
    private UserResponse userResponse;

    @BeforeEach
    void setUp() {
        createUserRequest = new CreateUserRequest();
        createUserRequest.setUsername("testuser");
        createUserRequest.setPassword("TestPass123!");
        createUserRequest.setFullName("测试用户");
        createUserRequest.setEmail("test@example.com");
        createUserRequest.setPhone("13800138000");
        createUserRequest.setHireDate(LocalDate.now());
        createUserRequest.setEmployeeId("EMP001");
        createUserRequest.setDepartment("IT部门");
        createUserRequest.setPosition("软件工程师");
        createUserRequest.setEnabled(true);

        updateUserRequest = new UpdateUserRequest();
        updateUserRequest.setFullName("更新用户");
        updateUserRequest.setEmail("updated@example.com");
        updateUserRequest.setPhone("13900139000");
        updateUserRequest.setDepartment("技术部");
        updateUserRequest.setPosition("高级工程师");

        userResponse = new UserResponse();
        userResponse.setId(1L);
        userResponse.setUsername("testuser");
        userResponse.setFullName("测试用户");
        userResponse.setEmail("test@example.com");
        userResponse.setPhone("13800138000");
        userResponse.setHireDate(LocalDate.now());
        userResponse.setEmployeeId("EMP001");
        userResponse.setDepartment("IT部门");
        userResponse.setPosition("软件工程师");
        userResponse.setEnabled(true);
        userResponse.setCreatedAt(LocalDateTime.now());
        userResponse.setUpdatedAt(LocalDateTime.now());
    }

    @Test
    @WithMockUser(authorities = {"USER_MANAGEMENT:CREATE"})
    void testCreateUser_Success() throws Exception {
        // Given
        when(userManagementService.createUser(any(CreateUserRequest.class))).thenReturn(userResponse);

        // When & Then
        mockMvc.perform(post("/api/users")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createUserRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("用户创建成功"))
                .andExpect(jsonPath("$.data.username").value("testuser"))
                .andExpect(jsonPath("$.data.fullName").value("测试用户"));

        verify(userManagementService).createUser(any(CreateUserRequest.class));
    }

    @Test
    @WithMockUser(authorities = {"USER_MANAGEMENT:CREATE"})
    void testCreateUser_ValidationError() throws Exception {
        // Given - 无效的请求数据
        createUserRequest.setUsername(""); // 用户名为空

        // When & Then
        mockMvc.perform(post("/api/users")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createUserRequest)))
                .andExpect(status().isBadRequest());

        verify(userManagementService, never()).createUser(any(CreateUserRequest.class));
    }

    @Test
    @WithMockUser(authorities = {"USER_MANAGEMENT:UPDATE"})
    void testUpdateUser_Success() throws Exception {
        // Given
        userResponse.setFullName("更新用户");
        when(userManagementService.updateUser(eq(1L), any(UpdateUserRequest.class))).thenReturn(userResponse);

        // When & Then
        mockMvc.perform(put("/api/users/1")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateUserRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("用户信息更新成功"))
                .andExpect(jsonPath("$.data.fullName").value("更新用户"));

        verify(userManagementService).updateUser(eq(1L), any(UpdateUserRequest.class));
    }

    @Test
    @WithMockUser(authorities = {"USER_MANAGEMENT:READ"})
    void testGetUserById_Success() throws Exception {
        // Given
        when(userManagementService.getUserById(1L)).thenReturn(userResponse);

        // When & Then
        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.username").value("testuser"));

        verify(userManagementService).getUserById(1L);
    }

    @Test
    @WithMockUser(authorities = {"USER_MANAGEMENT:READ"})
    void testGetUserByUsername_Success() throws Exception {
        // Given
        when(userManagementService.getUserByUsername("testuser")).thenReturn(userResponse);

        // When & Then
        mockMvc.perform(get("/api/users/username/testuser"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.username").value("testuser"));

        verify(userManagementService).getUserByUsername("testuser");
    }

    @Test
    @WithMockUser(authorities = {"USER_MANAGEMENT:READ"})
    void testGetAllUsers_Success() throws Exception {
        // Given
        List<UserResponse> users = Arrays.asList(userResponse);
        when(userManagementService.getAllUsers()).thenReturn(users);

        // When & Then
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].username").value("testuser"))
                .andExpect(jsonPath("$.total").value(1));

        verify(userManagementService).getAllUsers();
    }

    @Test
    @WithMockUser(authorities = {"USER_MANAGEMENT:READ"})
    void testGetAllUsers_WithEnabledFilter() throws Exception {
        // Given
        List<UserResponse> users = Arrays.asList(userResponse);
        when(userManagementService.getUsersByEnabled(true)).thenReturn(users);

        // When & Then
        mockMvc.perform(get("/api/users?enabled=true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.total").value(1));

        verify(userManagementService).getUsersByEnabled(true);
    }

    @Test
    @WithMockUser(authorities = {"USER_MANAGEMENT:READ"})
    void testGetAllUsers_WithDepartmentFilter() throws Exception {
        // Given
        List<UserResponse> users = Arrays.asList(userResponse);
        when(userManagementService.getUsersByDepartment("IT部门")).thenReturn(users);

        // When & Then
        mockMvc.perform(get("/api/users?department=IT部门"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.total").value(1));

        verify(userManagementService).getUsersByDepartment("IT部门");
    }

    @Test
    @WithMockUser(authorities = {"USER_MANAGEMENT:READ"})
    void testGetAllUsers_WithKeywordSearch() throws Exception {
        // Given
        List<UserResponse> users = Arrays.asList(userResponse);
        when(userManagementService.searchUsers("test")).thenReturn(users);

        // When & Then
        mockMvc.perform(get("/api/users?keyword=test"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.total").value(1));

        verify(userManagementService).searchUsers("test");
    }

    @Test
    @WithMockUser(authorities = {"USER_MANAGEMENT:UPDATE"})
    void testDisableUser_Success() throws Exception {
        // Given
        doNothing().when(userManagementService).disableUser(1L);

        // When & Then
        mockMvc.perform(put("/api/users/1/disable")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("用户禁用成功"));

        verify(userManagementService).disableUser(1L);
    }

    @Test
    @WithMockUser(authorities = {"USER_MANAGEMENT:UPDATE"})
    void testEnableUser_Success() throws Exception {
        // Given
        doNothing().when(userManagementService).enableUser(1L);

        // When & Then
        mockMvc.perform(put("/api/users/1/enable")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("用户启用成功"));

        verify(userManagementService).enableUser(1L);
    }

    @Test
    @WithMockUser(authorities = {"USER_MANAGEMENT:DELETE"})
    void testDeleteUser_Success() throws Exception {
        // Given
        doNothing().when(userManagementService).deleteUser(1L);

        // When & Then
        mockMvc.perform(delete("/api/users/1")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("用户删除成功"));

        verify(userManagementService).deleteUser(1L);
    }

    @Test
    @WithMockUser(authorities = {"USER_MANAGEMENT:UPDATE"})
    void testAssignRoleToUser_Success() throws Exception {
        // Given
        doNothing().when(userManagementService).assignRoleToUser(1L, 2L);

        // When & Then
        mockMvc.perform(post("/api/users/1/roles/2")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("用户角色分配成功"));

        verify(userManagementService).assignRoleToUser(1L, 2L);
    }

    @Test
    @WithMockUser(authorities = {"USER_MANAGEMENT:UPDATE"})
    void testRemoveRoleFromUser_Success() throws Exception {
        // Given
        doNothing().when(userManagementService).removeRoleFromUser(1L, 2L);

        // When & Then
        mockMvc.perform(delete("/api/users/1/roles/2")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("用户角色移除成功"));

        verify(userManagementService).removeRoleFromUser(1L, 2L);
    }

    @Test
    @WithMockUser(authorities = {"USER_MANAGEMENT:UPDATE"})
    void testResetUserPassword_Success() throws Exception {
        // Given
        Map<String, String> request = new HashMap<>();
        request.put("newPassword", "NewPass123!");
        
        doNothing().when(userManagementService).resetUserPassword(1L, "NewPass123!");

        // When & Then
        mockMvc.perform(put("/api/users/1/reset-password")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("用户密码重置成功"));

        verify(userManagementService).resetUserPassword(1L, "NewPass123!");
    }

    @Test
    @WithMockUser(authorities = {"USER_MANAGEMENT:UPDATE"})
    void testResetUserPassword_EmptyPassword() throws Exception {
        // Given
        Map<String, String> request = new HashMap<>();
        request.put("newPassword", "");

        // When & Then
        mockMvc.perform(put("/api/users/1/reset-password")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("新密码不能为空"));

        verify(userManagementService, never()).resetUserPassword(anyLong(), any());
    }

    @Test
    @WithMockUser(authorities = {"USER_MANAGEMENT:READ"})
    void testCheckUsernameExists_True() throws Exception {
        // Given
        when(userManagementService.existsByUsername("testuser")).thenReturn(true);

        // When & Then
        mockMvc.perform(get("/api/users/check-username/testuser"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.exists").value(true));

        verify(userManagementService).existsByUsername("testuser");
    }

    @Test
    @WithMockUser(authorities = {"USER_MANAGEMENT:READ"})
    void testCheckUsernameExists_False() throws Exception {
        // Given
        when(userManagementService.existsByUsername("nonexistent")).thenReturn(false);

        // When & Then
        mockMvc.perform(get("/api/users/check-username/nonexistent"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.exists").value(false));

        verify(userManagementService).existsByUsername("nonexistent");
    }

    @Test
    @WithMockUser(authorities = {"USER_MANAGEMENT:READ"})
    void testCheckEmailExists_True() throws Exception {
        // Given
        when(userManagementService.existsByEmail("test@example.com")).thenReturn(true);

        // When & Then
        mockMvc.perform(get("/api/users/check-email/test@example.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.exists").value(true));

        verify(userManagementService).existsByEmail("test@example.com");
    }

    @Test
    void testCreateUser_Unauthorized() throws Exception {
        // When & Then
        mockMvc.perform(post("/api/users")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createUserRequest)))
                .andExpect(status().isUnauthorized());

        verify(userManagementService, never()).createUser(any(CreateUserRequest.class));
    }
}