package org.me.joy.clinic.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.me.joy.clinic.dto.CreateRoleRequest;
import org.me.joy.clinic.dto.UpdateRoleRequest;
import org.me.joy.clinic.entity.Permission;
import org.me.joy.clinic.entity.Role;
import org.me.joy.clinic.exception.BusinessException;
import org.me.joy.clinic.exception.ValidationException;
import org.me.joy.clinic.service.RoleManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 角色控制器集成测试类
 */
@WebMvcTest(RoleController.class)
class RoleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RoleManagementService roleManagementService;

    @Autowired
    private ObjectMapper objectMapper;

    private Role testRole;
    private Permission testPermission;

    @BeforeEach
    void setUp() {
        testRole = new Role();
        testRole.setId(1L);
        testRole.setRoleCode("TEST_ROLE");
        testRole.setRoleName("测试角色");
        testRole.setDescription("测试用角色");
        testRole.setEnabled(true);
        testRole.setIsSystemRole(false);
        testRole.setCreatedAt(LocalDateTime.now());
        testRole.setUpdatedAt(LocalDateTime.now());

        testPermission = new Permission();
        testPermission.setId(1L);
        testPermission.setPermissionCode("TEST_PERMISSION");
        testPermission.setPermissionName("测试权限");
        testPermission.setDescription("测试用权限");
        testPermission.setModule("TEST_MODULE");
        testPermission.setEnabled(true);
    }

    @Test
    void getAllRoles_ShouldReturnRolesList() throws Exception {
        // Given
        List<Role> roles = Arrays.asList(testRole);
        when(roleManagementService.getAllRoles()).thenReturn(roles);

        // When & Then
        mockMvc.perform(get("/api/roles"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("获取角色列表成功"))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].id").value(1))
                .andExpect(jsonPath("$.data[0].roleCode").value("TEST_ROLE"))
                .andExpect(jsonPath("$.data[0].roleName").value("测试角色"));

        verify(roleManagementService).getAllRoles();
    }

    @Test
    void getEnabledRoles_ShouldReturnEnabledRolesList() throws Exception {
        // Given
        List<Role> roles = Arrays.asList(testRole);
        when(roleManagementService.getEnabledRoles()).thenReturn(roles);

        // When & Then
        mockMvc.perform(get("/api/roles/enabled"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("获取启用角色列表成功"))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].id").value(1));

        verify(roleManagementService).getEnabledRoles();
    }

    @Test
    void getRoleById_WithValidId_ShouldReturnRole() throws Exception {
        // Given
        when(roleManagementService.getRoleById(1L)).thenReturn(testRole);

        // When & Then
        mockMvc.perform(get("/api/roles/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("获取角色信息成功"))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.roleCode").value("TEST_ROLE"));

        verify(roleManagementService).getRoleById(1L);
    }

    @Test
    void getRoleById_WithInvalidId_ShouldReturnError() throws Exception {
        // Given
        when(roleManagementService.getRoleById(999L)).thenThrow(new BusinessException("ROLE_NOT_FOUND", "角色不存在"));

        // When & Then
        mockMvc.perform(get("/api/roles/999"))
                .andExpect(status().isBadRequest());

        verify(roleManagementService).getRoleById(999L);
    }

    @Test
    void createRole_WithValidRequest_ShouldCreateRole() throws Exception {
        // Given
        CreateRoleRequest request = new CreateRoleRequest("NEW_ROLE", "新角色", "新创建的角色");
        when(roleManagementService.createRole(any(CreateRoleRequest.class))).thenReturn(testRole);

        // When & Then
        mockMvc.perform(post("/api/roles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("角色创建成功"))
                .andExpect(jsonPath("$.data.id").value(1));

        verify(roleManagementService).createRole(any(CreateRoleRequest.class));
    }

    @Test
    void createRole_WithInvalidRequest_ShouldReturnValidationError() throws Exception {
        // Given
        CreateRoleRequest request = new CreateRoleRequest("", "", ""); // 空的角色代码和名称

        // When & Then
        mockMvc.perform(post("/api/roles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createRole_WithDuplicateRoleCode_ShouldReturnError() throws Exception {
        // Given
        CreateRoleRequest request = new CreateRoleRequest("EXISTING_ROLE", "已存在角色", "已存在的角色");
        when(roleManagementService.createRole(any(CreateRoleRequest.class)))
                .thenThrow(new BusinessException("ROLE_CODE_EXISTS", "角色代码已存在"));

        // When & Then
        mockMvc.perform(post("/api/roles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(roleManagementService).createRole(any(CreateRoleRequest.class));
    }

    @Test
    void updateRole_WithValidRequest_ShouldUpdateRole() throws Exception {
        // Given
        UpdateRoleRequest request = new UpdateRoleRequest("更新后的角色", "更新后的描述", false);
        when(roleManagementService.updateRole(eq(1L), any(UpdateRoleRequest.class))).thenReturn(testRole);

        // When & Then
        mockMvc.perform(put("/api/roles/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("角色更新成功"))
                .andExpect(jsonPath("$.data.id").value(1));

        verify(roleManagementService).updateRole(eq(1L), any(UpdateRoleRequest.class));
    }

    @Test
    void deleteRole_WithValidId_ShouldDeleteRole() throws Exception {
        // Given
        doNothing().when(roleManagementService).deleteRole(1L);

        // When & Then
        mockMvc.perform(delete("/api/roles/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("角色删除成功"));

        verify(roleManagementService).deleteRole(1L);
    }

    @Test
    void deleteRole_WithSystemRole_ShouldReturnError() throws Exception {
        // Given
        doThrow(new BusinessException("SYSTEM_ROLE_CANNOT_DELETE", "系统预设角色不能删除")).when(roleManagementService).deleteRole(1L);

        // When & Then
        mockMvc.perform(delete("/api/roles/1"))
                .andExpect(status().isBadRequest());

        verify(roleManagementService).deleteRole(1L);
    }

    @Test
    void enableRole_WithValidId_ShouldEnableRole() throws Exception {
        // Given
        doNothing().when(roleManagementService).enableRole(1L);

        // When & Then
        mockMvc.perform(put("/api/roles/1/enable"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("角色启用成功"));

        verify(roleManagementService).enableRole(1L);
    }

    @Test
    void disableRole_WithValidId_ShouldDisableRole() throws Exception {
        // Given
        doNothing().when(roleManagementService).disableRole(1L);

        // When & Then
        mockMvc.perform(put("/api/roles/1/disable"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("角色禁用成功"));

        verify(roleManagementService).disableRole(1L);
    }

    @Test
    void assignRoleToUser_WithValidIds_ShouldAssignRole() throws Exception {
        // Given
        doNothing().when(roleManagementService).assignRoleToUser(1L, 1L);

        // When & Then
        mockMvc.perform(post("/api/roles/1/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("角色分配成功"));

        verify(roleManagementService).assignRoleToUser(1L, 1L);
    }

    @Test
    void removeRoleFromUser_WithValidIds_ShouldRemoveRole() throws Exception {
        // Given
        doNothing().when(roleManagementService).removeRoleFromUser(1L, 1L);

        // When & Then
        mockMvc.perform(delete("/api/roles/1/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("角色移除成功"));

        verify(roleManagementService).removeRoleFromUser(1L, 1L);
    }

    @Test
    void getUserRoles_WithValidUserId_ShouldReturnUserRoles() throws Exception {
        // Given
        List<Role> roles = Arrays.asList(testRole);
        when(roleManagementService.getUserRoles(1L)).thenReturn(roles);

        // When & Then
        mockMvc.perform(get("/api/roles/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("获取用户角色成功"))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].id").value(1));

        verify(roleManagementService).getUserRoles(1L);
    }

    @Test
    void getRolePermissions_WithValidRoleId_ShouldReturnRolePermissions() throws Exception {
        // Given
        List<Permission> permissions = Arrays.asList(testPermission);
        when(roleManagementService.getRolePermissions(1L)).thenReturn(permissions);

        // When & Then
        mockMvc.perform(get("/api/roles/1/permissions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("获取角色权限成功"))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].id").value(1));

        verify(roleManagementService).getRolePermissions(1L);
    }

    @Test
    void assignPermissionToRole_WithValidIds_ShouldAssignPermission() throws Exception {
        // Given
        doNothing().when(roleManagementService).assignPermissionToRole(1L, 1L);

        // When & Then
        mockMvc.perform(post("/api/roles/1/permissions/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("权限分配成功"));

        verify(roleManagementService).assignPermissionToRole(1L, 1L);
    }

    @Test
    void removePermissionFromRole_WithValidIds_ShouldRemovePermission() throws Exception {
        // Given
        doNothing().when(roleManagementService).removePermissionFromRole(1L, 1L);

        // When & Then
        mockMvc.perform(delete("/api/roles/1/permissions/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("权限移除成功"));

        verify(roleManagementService).removePermissionFromRole(1L, 1L);
    }

    @Test
    void assignPermissionsToRole_WithValidIds_ShouldAssignPermissions() throws Exception {
        // Given
        List<Long> permissionIds = Arrays.asList(1L, 2L, 3L);
        doNothing().when(roleManagementService).assignPermissionsToRole(1L, permissionIds);

        // When & Then
        mockMvc.perform(put("/api/roles/1/permissions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(permissionIds)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("权限批量分配成功"));

        verify(roleManagementService).assignPermissionsToRole(1L, permissionIds);
    }

    @Test
    void initializeDefaultRoles_ShouldInitializeRoles() throws Exception {
        // Given
        doNothing().when(roleManagementService).initializeDefaultRoles();

        // When & Then
        mockMvc.perform(post("/api/roles/initialize"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("系统默认角色初始化成功"));

        verify(roleManagementService).initializeDefaultRoles();
    }
}