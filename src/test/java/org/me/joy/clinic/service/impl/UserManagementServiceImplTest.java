package org.me.joy.clinic.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.me.joy.clinic.dto.CreateUserRequest;
import org.me.joy.clinic.dto.UpdateUserRequest;
import org.me.joy.clinic.dto.UserResponse;
import org.me.joy.clinic.entity.Role;
import org.me.joy.clinic.entity.User;
import org.me.joy.clinic.exception.BusinessException;
import org.me.joy.clinic.exception.ValidationException;
import org.me.joy.clinic.mapper.RoleMapper;
import org.me.joy.clinic.mapper.UserMapper;
import org.me.joy.clinic.service.PasswordService;
import org.me.joy.clinic.service.RoleManagementService;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

/**
 * 用户管理服务实现类测试
 */
@ExtendWith(MockitoExtension.class)
class UserManagementServiceImplTest {

    @Mock
    private UserMapper userMapper;

    @Mock
    private RoleMapper roleMapper;



    @Mock
    private PasswordService passwordService;

    @Mock
    private RoleManagementService roleManagementService;

    @InjectMocks
    private UserManagementServiceImpl userManagementService;

    private CreateUserRequest createUserRequest;
    private UpdateUserRequest updateUserRequest;
    private User testUser;

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

        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setPassword("encodedPassword");
        testUser.setFullName("测试用户");
        testUser.setEmail("test@example.com");
        testUser.setPhone("13800138000");
        testUser.setHireDate(LocalDate.now());
        testUser.setEmployeeId("EMP001");
        testUser.setDepartment("IT部门");
        testUser.setPosition("软件工程师");
        testUser.setEnabled(true);
        testUser.setAccountNonExpired(true);
        testUser.setAccountNonLocked(true);
        testUser.setCredentialsNonExpired(true);
        testUser.setCreatedAt(LocalDateTime.now());
        testUser.setUpdatedAt(LocalDateTime.now());
    }

    @Test
    void testCreateUser_Success() {
        // Given
        when(userMapper.selectCount(any())).thenReturn(0L); // 用户名、邮箱、员工工号都不存在
        when(passwordService.encodePassword("TestPass123!")).thenReturn("encodedPassword");
        when(userMapper.insert(any(User.class))).thenReturn(1);

        // When
        UserResponse result = userManagementService.createUser(createUserRequest);

        // Then
        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        assertEquals("测试用户", result.getFullName());
        assertEquals("test@example.com", result.getEmail());
        assertTrue(result.getEnabled());

        verify(userMapper, times(3)).selectCount(any()); // 检查用户名、邮箱、员工工号
        verify(passwordService).encodePassword("TestPass123!");
        verify(userMapper).insert(any(User.class));
    }

    @Test
    void testCreateUser_UsernameExists() {
        // Given
        when(userMapper.selectCount(any())).thenReturn(1L); // 用户名已存在

        // When & Then
        ValidationException exception = assertThrows(ValidationException.class,
            () -> userManagementService.createUser(createUserRequest));

        assertEquals("2010", exception.getErrorCode());
        assertEquals("用户名已存在", exception.getMessage());
    }

    @Test
    void testCreateUser_EmailExists() {
        // Given
        when(userMapper.selectCount(any()))
            .thenReturn(0L) // 用户名不存在
            .thenReturn(1L); // 邮箱已存在

        // When & Then
        ValidationException exception = assertThrows(ValidationException.class,
            () -> userManagementService.createUser(createUserRequest));

        assertEquals("2011", exception.getErrorCode());
        assertEquals("邮箱已存在", exception.getMessage());
    }

    @Test
    void testUpdateUser_Success() {
        // Given
        when(userMapper.selectById(1L)).thenReturn(testUser);
        when(userMapper.selectCount(any())).thenReturn(0L); // 邮箱和员工工号不存在冲突
        when(userMapper.updateById(any(User.class))).thenReturn(1);

        // When
        UserResponse result = userManagementService.updateUser(1L, updateUserRequest);

        // Then
        assertNotNull(result);
        assertEquals("更新用户", result.getFullName());
        assertEquals("updated@example.com", result.getEmail());
        assertEquals("技术部", result.getDepartment());

        verify(userMapper).selectById(1L);
        verify(userMapper).updateById(any(User.class));
    }

    @Test
    void testUpdateUser_UserNotFound() {
        // Given
        when(userMapper.selectById(1L)).thenReturn(null);

        // When & Then
        BusinessException exception = assertThrows(BusinessException.class,
            () -> userManagementService.updateUser(1L, updateUserRequest));

        assertEquals("2001", exception.getErrorCode());
        assertEquals("用户不存在", exception.getMessage());
    }

    @Test
    void testGetUserById_Success() {
        // Given
        when(userMapper.selectById(1L)).thenReturn(testUser);
        when(roleManagementService.getUserRoles(1L)).thenReturn(Arrays.asList());

        // When
        UserResponse result = userManagementService.getUserById(1L);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("testuser", result.getUsername());
        assertEquals("测试用户", result.getFullName());

        verify(userMapper).selectById(1L);
    }

    @Test
    void testGetUserById_UserNotFound() {
        // Given
        when(userMapper.selectById(1L)).thenReturn(null);

        // When & Then
        BusinessException exception = assertThrows(BusinessException.class,
            () -> userManagementService.getUserById(1L));

        assertEquals("2001", exception.getErrorCode());
        assertEquals("用户不存在", exception.getMessage());
    }

    @Test
    void testGetUserByUsername_Success() {
        // Given
        when(userMapper.findByUsernameOptional("testuser")).thenReturn(Optional.of(testUser));
        when(roleManagementService.getUserRoles(1L)).thenReturn(Arrays.asList());

        // When
        UserResponse result = userManagementService.getUserByUsername("testuser");

        // Then
        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        assertEquals("测试用户", result.getFullName());

        verify(userMapper).findByUsernameOptional("testuser");
    }

    @Test
    void testGetUserByUsername_UserNotFound() {
        // Given
        when(userMapper.findByUsernameOptional("nonexistent")).thenReturn(Optional.empty());

        // When & Then
        BusinessException exception = assertThrows(BusinessException.class,
            () -> userManagementService.getUserByUsername("nonexistent"));

        assertEquals("2001", exception.getErrorCode());
        assertEquals("用户不存在", exception.getMessage());
    }

    @Test
    void testGetAllUsers_Success() {
        // Given
        List<User> users = Arrays.asList(testUser);
        when(userMapper.selectList(null)).thenReturn(users);
        when(roleManagementService.getUserRoles(anyLong())).thenReturn(Arrays.asList());

        // When
        List<UserResponse> result = userManagementService.getAllUsers();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("testuser", result.get(0).getUsername());

        verify(userMapper).selectList(null);
    }

    @Test
    void testDisableUser_Success() {
        // Given
        when(userMapper.selectById(1L)).thenReturn(testUser);
        when(userMapper.updateById(any(User.class))).thenReturn(1);

        // When
        userManagementService.disableUser(1L);

        // Then
        verify(userMapper).selectById(1L);
        verify(userMapper).updateById(any(User.class));
    }

    @Test
    void testEnableUser_Success() {
        // Given
        testUser.setEnabled(false);
        testUser.setAccountNonLocked(false);
        testUser.setFailedLoginAttempts(3);
        
        when(userMapper.selectById(1L)).thenReturn(testUser);
        when(userMapper.updateById(any(User.class))).thenReturn(1);

        // When
        userManagementService.enableUser(1L);

        // Then
        verify(userMapper).selectById(1L);
        verify(userMapper).updateById(any(User.class));
    }

    @Test
    void testDeleteUser_Success() {
        // Given
        when(userMapper.selectById(1L)).thenReturn(testUser);
        when(userMapper.deleteById(1L)).thenReturn(1);

        // When
        userManagementService.deleteUser(1L);

        // Then
        verify(userMapper).selectById(1L);
        verify(userMapper).deleteById(1L);
    }

    @Test
    void testAssignRoleToUser_Success() {
        // Given
        when(userMapper.selectById(1L)).thenReturn(testUser);

        // When
        userManagementService.assignRoleToUser(1L, 2L);

        // Then
        verify(userMapper).selectById(1L);
        verify(roleManagementService).assignRoleToUser(1L, 2L);
    }

    @Test
    void testRemoveRoleFromUser_Success() {
        // Given
        when(userMapper.selectById(1L)).thenReturn(testUser);

        // When
        userManagementService.removeRoleFromUser(1L, 2L);

        // Then
        verify(userMapper).selectById(1L);
        verify(roleManagementService).removeRoleFromUser(1L, 2L);
    }

    @Test
    void testResetUserPassword_Success() {
        // Given
        when(userMapper.selectById(1L)).thenReturn(testUser);
        when(passwordService.validatePasswordComplexity("NewPass123!")).thenReturn(true);
        when(passwordService.encodePassword("NewPass123!")).thenReturn("newEncodedPassword");
        when(userMapper.updateById(any(User.class))).thenReturn(1);

        // When
        userManagementService.resetUserPassword(1L, "NewPass123!");

        // Then
        verify(userMapper).selectById(1L);
        verify(passwordService).validatePasswordComplexity("NewPass123!");
        verify(passwordService).encodePassword("NewPass123!");
        verify(userMapper).updateById(any(User.class));
    }

    @Test
    void testResetUserPassword_WeakPassword() {
        // Given
        when(userMapper.selectById(1L)).thenReturn(testUser);
        when(passwordService.validatePasswordComplexity("weak")).thenReturn(false);

        // When & Then
        ValidationException exception = assertThrows(ValidationException.class,
            () -> userManagementService.resetUserPassword(1L, "weak"));

        assertEquals("2003", exception.getErrorCode());
        assertEquals("新密码不符合复杂度要求", exception.getMessage());
    }

    @Test
    void testExistsByUsername_True() {
        // Given
        when(userMapper.selectCount(any())).thenReturn(1L);

        // When
        boolean result = userManagementService.existsByUsername("testuser");

        // Then
        assertTrue(result);
        verify(userMapper).selectCount(any());
    }

    @Test
    void testExistsByUsername_False() {
        // Given
        when(userMapper.selectCount(any())).thenReturn(0L);

        // When
        boolean result = userManagementService.existsByUsername("nonexistent");

        // Then
        assertFalse(result);
        verify(userMapper).selectCount(any());
    }

    @Test
    void testExistsByEmail_True() {
        // Given
        when(userMapper.selectCount(any())).thenReturn(1L);

        // When
        boolean result = userManagementService.existsByEmail("test@example.com");

        // Then
        assertTrue(result);
        verify(userMapper).selectCount(any());
    }

    @Test
    void testExistsByEmail_False() {
        // Given
        when(userMapper.selectCount(any())).thenReturn(0L);

        // When
        boolean result = userManagementService.existsByEmail("nonexistent@example.com");

        // Then
        assertFalse(result);
        verify(userMapper).selectCount(any());
    }

    @Test
    void testExistsByEmail_NullEmail() {
        // When
        boolean result = userManagementService.existsByEmail(null);

        // Then
        assertFalse(result);
        verify(userMapper, never()).selectCount(any());
    }

    @Test
    void testSearchUsers_WithKeyword() {
        // Given
        List<User> users = Arrays.asList(testUser);
        when(userMapper.selectList(any())).thenReturn(users);
        when(roleManagementService.getUserRoles(anyLong())).thenReturn(Arrays.asList());

        // When
        List<UserResponse> result = userManagementService.searchUsers("test");

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("testuser", result.get(0).getUsername());

        verify(userMapper).selectList(any());
    }

    @Test
    void testSearchUsers_EmptyKeyword() {
        // Given
        List<User> users = Arrays.asList(testUser);
        when(userMapper.selectList(null)).thenReturn(users);
        when(roleManagementService.getUserRoles(anyLong())).thenReturn(Arrays.asList());

        // When
        List<UserResponse> result = userManagementService.searchUsers("");

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());

        verify(userMapper).selectList(null);
    }
}