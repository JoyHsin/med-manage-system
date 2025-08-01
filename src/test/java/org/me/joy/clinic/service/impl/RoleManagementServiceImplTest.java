package org.me.joy.clinic.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.me.joy.clinic.dto.CreateRoleRequest;
import org.me.joy.clinic.dto.UpdateRoleRequest;
import org.me.joy.clinic.entity.Permission;
import org.me.joy.clinic.entity.Role;
import org.me.joy.clinic.entity.User;
import org.me.joy.clinic.exception.BusinessException;
import org.me.joy.clinic.exception.ValidationException;
import org.me.joy.clinic.mapper.PermissionMapper;
import org.me.joy.clinic.mapper.RoleMapper;
import org.me.joy.clinic.mapper.RolePermissionMapper;
import org.me.joy.clinic.mapper.UserMapper;
import org.me.joy.clinic.mapper.UserRoleMapper;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * 角色管理服务测试类
 */
@ExtendWith(MockitoExtension.class)
class RoleManagementServiceImplTest {

    @Mock
    private RoleMapper roleMapper;

    @Mock
    private PermissionMapper permissionMapper;

    @Mock
    private UserRoleMapper userRoleMapper;

    @Mock
    private RolePermissionMapper rolePermissionMapper;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private RoleManagementServiceImpl roleManagementService;

    private Role testRole;
    private Permission testPermission;
    private User testUser;

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

        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setFullName("测试用户");
        testUser.setEnabled(true);
    }

    @Test
    void getAllRoles_ShouldReturnAllRoles() {
        // Given
        List<Role> expectedRoles = Arrays.asList(testRole);
        when(roleMapper.selectList(null)).thenReturn(expectedRoles);

        // When
        List<Role> actualRoles = roleManagementService.getAllRoles();

        // Then
        assertEquals(expectedRoles, actualRoles);
        verify(roleMapper).selectList(null);
    }

    @Test
    void getEnabledRoles_ShouldReturnEnabledRoles() {
        // Given
        List<Role> expectedRoles = Arrays.asList(testRole);
        when(roleMapper.findByEnabledTrue()).thenReturn(expectedRoles);

        // When
        List<Role> actualRoles = roleManagementService.getEnabledRoles();

        // Then
        assertEquals(expectedRoles, actualRoles);
        verify(roleMapper).findByEnabledTrue();
    }

    @Test
    void getRoleById_WithValidId_ShouldReturnRole() {
        // Given
        when(roleMapper.selectById(1L)).thenReturn(testRole);

        // When
        Role actualRole = roleManagementService.getRoleById(1L);

        // Then
        assertEquals(testRole, actualRole);
        verify(roleMapper).selectById(1L);
    }

    @Test
    void getRoleById_WithNullId_ShouldThrowValidationException() {
        // When & Then
        assertThrows(ValidationException.class, () -> roleManagementService.getRoleById(null));
    }

    @Test
    void getRoleById_WithNonExistentId_ShouldThrowBusinessException() {
        // Given
        when(roleMapper.selectById(1L)).thenReturn(null);

        // When & Then
        assertThrows(BusinessException.class, () -> roleManagementService.getRoleById(1L));
    }

    @Test
    void getRoleByCode_WithValidCode_ShouldReturnRole() {
        // Given
        when(roleMapper.selectOne(any(QueryWrapper.class))).thenReturn(testRole);

        // When
        Role actualRole = roleManagementService.getRoleByCode("TEST_ROLE");

        // Then
        assertEquals(testRole, actualRole);
        verify(roleMapper).selectOne(any(QueryWrapper.class));
    }

    @Test
    void getRoleByCode_WithEmptyCode_ShouldThrowValidationException() {
        // When & Then
        assertThrows(ValidationException.class, () -> roleManagementService.getRoleByCode(""));
    }

    @Test
    void createRole_WithValidRequest_ShouldCreateRole() {
        // Given
        CreateRoleRequest request = new CreateRoleRequest("NEW_ROLE", "新角色", "新创建的角色");
        when(roleMapper.selectCount(any(QueryWrapper.class))).thenReturn(0L);
        when(roleMapper.insert(any(Role.class))).thenReturn(1);

        // When
        Role createdRole = roleManagementService.createRole(request);

        // Then
        assertNotNull(createdRole);
        assertEquals("NEW_ROLE", createdRole.getRoleCode());
        assertEquals("新角色", createdRole.getRoleName());
        assertEquals("新创建的角色", createdRole.getDescription());
        assertTrue(createdRole.getEnabled());
        assertFalse(createdRole.getIsSystemRole());
        verify(roleMapper).insert(any(Role.class));
    }

    @Test
    void createRole_WithNullRequest_ShouldThrowValidationException() {
        // When & Then
        assertThrows(ValidationException.class, () -> roleManagementService.createRole(null));
    }

    @Test
    void createRole_WithExistingRoleCode_ShouldThrowBusinessException() {
        // Given
        CreateRoleRequest request = new CreateRoleRequest("EXISTING_ROLE", "已存在角色", "已存在的角色");
        when(roleMapper.selectCount(any(QueryWrapper.class))).thenReturn(1L);

        // When & Then
        assertThrows(BusinessException.class, () -> roleManagementService.createRole(request));
    }

    @Test
    void updateRole_WithValidRequest_ShouldUpdateRole() {
        // Given
        UpdateRoleRequest request = new UpdateRoleRequest("更新后的角色", "更新后的描述", false);
        when(roleMapper.selectById(1L)).thenReturn(testRole);
        when(roleMapper.updateById(any(Role.class))).thenReturn(1);

        // When
        Role updatedRole = roleManagementService.updateRole(1L, request);

        // Then
        assertEquals("更新后的角色", updatedRole.getRoleName());
        assertEquals("更新后的描述", updatedRole.getDescription());
        assertFalse(updatedRole.getEnabled());
        verify(roleMapper).updateById(any(Role.class));
    }

    @Test
    void updateRole_WithNullId_ShouldThrowValidationException() {
        // Given
        UpdateRoleRequest request = new UpdateRoleRequest();

        // When & Then
        assertThrows(ValidationException.class, () -> roleManagementService.updateRole(null, request));
    }

    @Test
    void deleteRole_WithValidId_ShouldDeleteRole() {
        // Given
        when(roleMapper.selectById(1L)).thenReturn(testRole);
        when(roleMapper.countUsersByRoleId(1L)).thenReturn(0L);
        when(roleMapper.deleteById(1L)).thenReturn(1);

        // When
        roleManagementService.deleteRole(1L);

        // Then
        verify(rolePermissionMapper).removeAllRolePermissions(1L);
        verify(roleMapper).deleteById(1L);
    }

    @Test
    void deleteRole_WithSystemRole_ShouldThrowBusinessException() {
        // Given
        testRole.setIsSystemRole(true);
        when(roleMapper.selectById(1L)).thenReturn(testRole);

        // When & Then
        assertThrows(BusinessException.class, () -> roleManagementService.deleteRole(1L));
    }

    @Test
    void deleteRole_WithUsersAssigned_ShouldThrowBusinessException() {
        // Given
        when(roleMapper.selectById(1L)).thenReturn(testRole);
        when(roleMapper.countUsersByRoleId(1L)).thenReturn(1L);

        // When & Then
        assertThrows(BusinessException.class, () -> roleManagementService.deleteRole(1L));
    }

    @Test
    void assignRoleToUser_WithValidIds_ShouldAssignRole() {
        // Given
        when(userMapper.selectById(1L)).thenReturn(testUser);
        when(roleMapper.selectById(1L)).thenReturn(testRole);
        when(userRoleMapper.countUserRole(1L, 1L)).thenReturn(0);

        // When
        roleManagementService.assignRoleToUser(1L, 1L);

        // Then
        verify(userRoleMapper).addUserRole(1L, 1L);
    }

    @Test
    void assignRoleToUser_WithNonExistentUser_ShouldThrowBusinessException() {
        // Given
        when(userMapper.selectById(1L)).thenReturn(null);

        // When & Then
        assertThrows(BusinessException.class, () -> roleManagementService.assignRoleToUser(1L, 1L));
    }

    @Test
    void assignRoleToUser_WithDisabledRole_ShouldThrowBusinessException() {
        // Given
        testRole.setEnabled(false);
        when(userMapper.selectById(1L)).thenReturn(testUser);
        when(roleMapper.selectById(1L)).thenReturn(testRole);

        // When & Then
        assertThrows(BusinessException.class, () -> roleManagementService.assignRoleToUser(1L, 1L));
    }

    @Test
    void assignRoleToUser_WithExistingAssignment_ShouldThrowBusinessException() {
        // Given
        when(userMapper.selectById(1L)).thenReturn(testUser);
        when(roleMapper.selectById(1L)).thenReturn(testRole);
        when(userRoleMapper.countUserRole(1L, 1L)).thenReturn(1);

        // When & Then
        assertThrows(BusinessException.class, () -> roleManagementService.assignRoleToUser(1L, 1L));
    }

    @Test
    void removeRoleFromUser_WithValidIds_ShouldRemoveRole() {
        // Given
        when(userMapper.selectById(1L)).thenReturn(testUser);
        when(userRoleMapper.countUserRole(1L, 1L)).thenReturn(1);

        // When
        roleManagementService.removeRoleFromUser(1L, 1L);

        // Then
        verify(userRoleMapper).removeUserRole(1L, 1L);
    }

    @Test
    void removeRoleFromUser_WithNonExistentAssignment_ShouldThrowBusinessException() {
        // Given
        when(userMapper.selectById(1L)).thenReturn(testUser);
        when(userRoleMapper.countUserRole(1L, 1L)).thenReturn(0);

        // When & Then
        assertThrows(BusinessException.class, () -> roleManagementService.removeRoleFromUser(1L, 1L));
    }

    @Test
    void getUserRoles_WithValidUserId_ShouldReturnRoles() {
        // Given
        List<Role> expectedRoles = Arrays.asList(testRole);
        when(userRoleMapper.findRolesByUserId(1L)).thenReturn(expectedRoles);

        // When
        List<Role> actualRoles = roleManagementService.getUserRoles(1L);

        // Then
        assertEquals(expectedRoles, actualRoles);
        verify(userRoleMapper).findRolesByUserId(1L);
    }

    @Test
    void getRolePermissions_WithValidRoleId_ShouldReturnPermissions() {
        // Given
        List<Permission> expectedPermissions = Arrays.asList(testPermission);
        when(rolePermissionMapper.findPermissionsByRoleId(1L)).thenReturn(expectedPermissions);

        // When
        List<Permission> actualPermissions = roleManagementService.getRolePermissions(1L);

        // Then
        assertEquals(expectedPermissions, actualPermissions);
        verify(rolePermissionMapper).findPermissionsByRoleId(1L);
    }

    @Test
    void assignPermissionToRole_WithValidIds_ShouldAssignPermission() {
        // Given
        when(roleMapper.selectById(1L)).thenReturn(testRole);
        when(permissionMapper.selectById(1L)).thenReturn(testPermission);
        when(rolePermissionMapper.countRolePermission(1L, 1L)).thenReturn(0);

        // When
        roleManagementService.assignPermissionToRole(1L, 1L);

        // Then
        verify(rolePermissionMapper).addRolePermission(1L, 1L);
    }

    @Test
    void assignPermissionToRole_WithNonExistentPermission_ShouldThrowBusinessException() {
        // Given
        when(roleMapper.selectById(1L)).thenReturn(testRole);
        when(permissionMapper.selectById(1L)).thenReturn(null);

        // When & Then
        assertThrows(BusinessException.class, () -> roleManagementService.assignPermissionToRole(1L, 1L));
    }

    @Test
    void assignPermissionToRole_WithExistingAssignment_ShouldThrowBusinessException() {
        // Given
        when(roleMapper.selectById(1L)).thenReturn(testRole);
        when(permissionMapper.selectById(1L)).thenReturn(testPermission);
        when(rolePermissionMapper.countRolePermission(1L, 1L)).thenReturn(1);

        // When & Then
        assertThrows(BusinessException.class, () -> roleManagementService.assignPermissionToRole(1L, 1L));
    }

    @Test
    void removePermissionFromRole_WithValidIds_ShouldRemovePermission() {
        // Given
        when(roleMapper.selectById(1L)).thenReturn(testRole);
        when(rolePermissionMapper.countRolePermission(1L, 1L)).thenReturn(1);

        // When
        roleManagementService.removePermissionFromRole(1L, 1L);

        // Then
        verify(rolePermissionMapper).removeRolePermission(1L, 1L);
    }

    @Test
    void removePermissionFromRole_WithNonExistentAssignment_ShouldThrowBusinessException() {
        // Given
        when(roleMapper.selectById(1L)).thenReturn(testRole);
        when(rolePermissionMapper.countRolePermission(1L, 1L)).thenReturn(0);

        // When & Then
        assertThrows(BusinessException.class, () -> roleManagementService.removePermissionFromRole(1L, 1L));
    }

    @Test
    void assignPermissionsToRole_WithValidIds_ShouldAssignPermissions() {
        // Given
        List<Long> permissionIds = Arrays.asList(1L, 2L);
        Permission permission2 = new Permission();
        permission2.setId(2L);
        
        when(roleMapper.selectById(1L)).thenReturn(testRole);
        when(permissionMapper.selectById(1L)).thenReturn(testPermission);
        when(permissionMapper.selectById(2L)).thenReturn(permission2);

        // When
        roleManagementService.assignPermissionsToRole(1L, permissionIds);

        // Then
        verify(rolePermissionMapper).removeAllRolePermissions(1L);
        verify(rolePermissionMapper).addRolePermission(1L, 1L);
        verify(rolePermissionMapper).addRolePermission(1L, 2L);
    }

    @Test
    void hasRole_WithValidUserAndRole_ShouldReturnTrue() {
        // Given
        List<Role> userRoles = Arrays.asList(testRole);
        when(userRoleMapper.findRolesByUserId(1L)).thenReturn(userRoles);

        // When
        boolean hasRole = roleManagementService.hasRole(1L, "TEST_ROLE");

        // Then
        assertTrue(hasRole);
    }

    @Test
    void hasRole_WithInvalidRole_ShouldReturnFalse() {
        // Given
        List<Role> userRoles = Arrays.asList(testRole);
        when(userRoleMapper.findRolesByUserId(1L)).thenReturn(userRoles);

        // When
        boolean hasRole = roleManagementService.hasRole(1L, "INVALID_ROLE");

        // Then
        assertFalse(hasRole);
    }

    @Test
    void hasPermission_WithValidRoleAndPermission_ShouldReturnTrue() {
        // Given
        List<Permission> rolePermissions = Arrays.asList(testPermission);
        when(rolePermissionMapper.findPermissionsByRoleId(1L)).thenReturn(rolePermissions);

        // When
        boolean hasPermission = roleManagementService.hasPermission(1L, "TEST_PERMISSION");

        // Then
        assertTrue(hasPermission);
    }

    @Test
    void hasPermission_WithInvalidPermission_ShouldReturnFalse() {
        // Given
        List<Permission> rolePermissions = Arrays.asList(testPermission);
        when(rolePermissionMapper.findPermissionsByRoleId(1L)).thenReturn(rolePermissions);

        // When
        boolean hasPermission = roleManagementService.hasPermission(1L, "INVALID_PERMISSION");

        // Then
        assertFalse(hasPermission);
    }

    @Test
    void initializeDefaultRoles_WhenNoSystemRoles_ShouldCreateDefaultRoles() {
        // Given
        when(roleMapper.findByIsSystemRoleTrue()).thenReturn(Arrays.asList());
        when(roleMapper.insert(any(Role.class))).thenReturn(1);

        // When
        roleManagementService.initializeDefaultRoles();

        // Then
        verify(roleMapper, times(6)).insert(any(Role.class)); // 6个默认角色
    }

    @Test
    void initializeDefaultRoles_WhenSystemRolesExist_ShouldSkipInitialization() {
        // Given
        when(roleMapper.findByIsSystemRoleTrue()).thenReturn(Arrays.asList(testRole));

        // When
        roleManagementService.initializeDefaultRoles();

        // Then
        verify(roleMapper, never()).insert(any(Role.class));
    }
}