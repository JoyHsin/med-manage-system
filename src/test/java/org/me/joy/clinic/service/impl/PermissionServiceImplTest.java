package org.me.joy.clinic.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.me.joy.clinic.entity.Permission;
import org.me.joy.clinic.entity.User;
import org.me.joy.clinic.exception.BusinessException;
import org.me.joy.clinic.exception.ValidationException;
import org.me.joy.clinic.mapper.PermissionMapper;
import org.me.joy.clinic.mapper.UserMapper;
import org.me.joy.clinic.security.CustomUserPrincipal;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * 权限服务测试类
 */
@ExtendWith(MockitoExtension.class)
class PermissionServiceImplTest {

    @Mock
    private PermissionMapper permissionMapper;

    @Mock
    private UserMapper userMapper;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private PermissionServiceImpl permissionService;

    private Permission testPermission;
    private User testUser;

    @BeforeEach
    void setUp() {
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
    void getAllPermissions_ShouldReturnAllPermissions() {
        // Given
        List<Permission> expectedPermissions = Arrays.asList(testPermission);
        when(permissionMapper.selectList(null)).thenReturn(expectedPermissions);

        // When
        List<Permission> actualPermissions = permissionService.getAllPermissions();

        // Then
        assertEquals(expectedPermissions, actualPermissions);
        verify(permissionMapper).selectList(null);
    }

    @Test
    void getEnabledPermissions_ShouldReturnEnabledPermissions() {
        // Given
        List<Permission> expectedPermissions = Arrays.asList(testPermission);
        when(permissionMapper.findByEnabledTrue()).thenReturn(expectedPermissions);

        // When
        List<Permission> actualPermissions = permissionService.getEnabledPermissions();

        // Then
        assertEquals(expectedPermissions, actualPermissions);
        verify(permissionMapper).findByEnabledTrue();
    }

    @Test
    void getPermissionsByModule_WithValidModule_ShouldReturnPermissions() {
        // Given
        List<Permission> expectedPermissions = Arrays.asList(testPermission);
        when(permissionMapper.findByModuleAndEnabledTrue("TEST_MODULE")).thenReturn(expectedPermissions);

        // When
        List<Permission> actualPermissions = permissionService.getPermissionsByModule("TEST_MODULE");

        // Then
        assertEquals(expectedPermissions, actualPermissions);
        verify(permissionMapper).findByModuleAndEnabledTrue("TEST_MODULE");
    }

    @Test
    void getPermissionsByModule_WithEmptyModule_ShouldThrowValidationException() {
        // When & Then
        assertThrows(ValidationException.class, () -> permissionService.getPermissionsByModule(""));
    }

    @Test
    void getPermissionById_WithValidId_ShouldReturnPermission() {
        // Given
        when(permissionMapper.selectById(1L)).thenReturn(testPermission);

        // When
        Permission actualPermission = permissionService.getPermissionById(1L);

        // Then
        assertEquals(testPermission, actualPermission);
        verify(permissionMapper).selectById(1L);
    }

    @Test
    void getPermissionById_WithNullId_ShouldThrowValidationException() {
        // When & Then
        assertThrows(ValidationException.class, () -> permissionService.getPermissionById(null));
    }

    @Test
    void getPermissionById_WithNonExistentId_ShouldThrowBusinessException() {
        // Given
        when(permissionMapper.selectById(1L)).thenReturn(null);

        // When & Then
        assertThrows(BusinessException.class, () -> permissionService.getPermissionById(1L));
    }

    @Test
    void getPermissionByCode_WithValidCode_ShouldReturnPermission() {
        // Given
        when(permissionMapper.selectOne(any(QueryWrapper.class))).thenReturn(testPermission);

        // When
        Permission actualPermission = permissionService.getPermissionByCode("TEST_PERMISSION");

        // Then
        assertEquals(testPermission, actualPermission);
        verify(permissionMapper).selectOne(any(QueryWrapper.class));
    }

    @Test
    void getPermissionByCode_WithEmptyCode_ShouldThrowValidationException() {
        // When & Then
        assertThrows(ValidationException.class, () -> permissionService.getPermissionByCode(""));
    }

    @Test
    void getUserPermissions_WithValidUserId_ShouldReturnPermissions() {
        // Given
        List<Permission> expectedPermissions = Arrays.asList(testPermission);
        when(permissionMapper.findByUserId(1L)).thenReturn(expectedPermissions);

        // When
        List<Permission> actualPermissions = permissionService.getUserPermissions(1L);

        // Then
        assertEquals(expectedPermissions, actualPermissions);
        verify(permissionMapper).findByUserId(1L);
    }

    @Test
    void getUserPermissions_WithNullUserId_ShouldThrowValidationException() {
        // When & Then
        assertThrows(ValidationException.class, () -> permissionService.getUserPermissions(null));
    }

    @Test
    void getUserPermissionCodes_WithValidUserId_ShouldReturnPermissionCodes() {
        // Given
        List<Permission> permissions = Arrays.asList(testPermission);
        when(permissionMapper.findByUserId(1L)).thenReturn(permissions);

        // When
        List<String> permissionCodes = permissionService.getUserPermissionCodes(1L);

        // Then
        assertEquals(1, permissionCodes.size());
        assertEquals("TEST_PERMISSION", permissionCodes.get(0));
    }

    @Test
    void hasPermission_WithValidUserAndPermission_ShouldReturnTrue() {
        // Given
        List<Permission> permissions = Arrays.asList(testPermission);
        when(permissionMapper.findByUserId(1L)).thenReturn(permissions);

        // When
        boolean hasPermission = permissionService.hasPermission(1L, "TEST_PERMISSION");

        // Then
        assertTrue(hasPermission);
    }

    @Test
    void hasPermission_WithInvalidPermission_ShouldReturnFalse() {
        // Given
        List<Permission> permissions = Arrays.asList(testPermission);
        when(permissionMapper.findByUserId(1L)).thenReturn(permissions);

        // When
        boolean hasPermission = permissionService.hasPermission(1L, "INVALID_PERMISSION");

        // Then
        assertFalse(hasPermission);
    }

    @Test
    void hasAnyPermission_WithValidPermissions_ShouldReturnTrue() {
        // Given
        List<Permission> permissions = Arrays.asList(testPermission);
        when(permissionMapper.findByUserId(1L)).thenReturn(permissions);
        String[] permissionCodes = {"TEST_PERMISSION", "OTHER_PERMISSION"};

        // When
        boolean hasPermission = permissionService.hasAnyPermission(1L, permissionCodes);

        // Then
        assertTrue(hasPermission);
    }

    @Test
    void hasAnyPermission_WithInvalidPermissions_ShouldReturnFalse() {
        // Given
        List<Permission> permissions = Arrays.asList(testPermission);
        when(permissionMapper.findByUserId(1L)).thenReturn(permissions);
        String[] permissionCodes = {"INVALID_PERMISSION", "OTHER_PERMISSION"};

        // When
        boolean hasPermission = permissionService.hasAnyPermission(1L, permissionCodes);

        // Then
        assertFalse(hasPermission);
    }

    @Test
    void hasAllPermissions_WithValidPermissions_ShouldReturnTrue() {
        // Given
        Permission permission2 = new Permission();
        permission2.setPermissionCode("OTHER_PERMISSION");
        List<Permission> permissions = Arrays.asList(testPermission, permission2);
        when(permissionMapper.findByUserId(1L)).thenReturn(permissions);
        String[] permissionCodes = {"TEST_PERMISSION", "OTHER_PERMISSION"};

        // When
        boolean hasPermission = permissionService.hasAllPermissions(1L, permissionCodes);

        // Then
        assertTrue(hasPermission);
    }

    @Test
    void hasAllPermissions_WithMissingPermission_ShouldReturnFalse() {
        // Given
        List<Permission> permissions = Arrays.asList(testPermission);
        when(permissionMapper.findByUserId(1L)).thenReturn(permissions);
        String[] permissionCodes = {"TEST_PERMISSION", "MISSING_PERMISSION"};

        // When
        boolean hasPermission = permissionService.hasAllPermissions(1L, permissionCodes);

        // Then
        assertFalse(hasPermission);
    }

    @Test
    void getCurrentUser_WithAuthenticatedUser_ShouldReturnUser() {
        // Given
        CustomUserPrincipal userPrincipal = new CustomUserPrincipal(testUser);
        
        try (MockedStatic<SecurityContextHolder> mockedSecurityContextHolder = mockStatic(SecurityContextHolder.class)) {
            mockedSecurityContextHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            when(securityContext.getAuthentication()).thenReturn(authentication);
            when(authentication.isAuthenticated()).thenReturn(true);
            when(authentication.getPrincipal()).thenReturn(userPrincipal);
            when(userMapper.selectById(1L)).thenReturn(testUser);

            // When
            User currentUser = permissionService.getCurrentUser();

            // Then
            assertEquals(testUser, currentUser);
            verify(userMapper).selectById(1L);
        }
    }

    @Test
    void getCurrentUser_WithNoAuthentication_ShouldReturnNull() {
        // Given
        try (MockedStatic<SecurityContextHolder> mockedSecurityContextHolder = mockStatic(SecurityContextHolder.class)) {
            mockedSecurityContextHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            when(securityContext.getAuthentication()).thenReturn(null);

            // When
            User currentUser = permissionService.getCurrentUser();

            // Then
            assertNull(currentUser);
        }
    }

    @Test
    void getCurrentUserId_WithAuthenticatedUser_ShouldReturnUserId() {
        // Given
        CustomUserPrincipal userPrincipal = new CustomUserPrincipal(testUser);
        
        try (MockedStatic<SecurityContextHolder> mockedSecurityContextHolder = mockStatic(SecurityContextHolder.class)) {
            mockedSecurityContextHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            when(securityContext.getAuthentication()).thenReturn(authentication);
            when(authentication.isAuthenticated()).thenReturn(true);
            when(authentication.getPrincipal()).thenReturn(userPrincipal);

            // When
            Long currentUserId = permissionService.getCurrentUserId();

            // Then
            assertEquals(1L, currentUserId);
        }
    }

    @Test
    void getCurrentUserId_WithNoAuthentication_ShouldReturnNull() {
        // Given
        try (MockedStatic<SecurityContextHolder> mockedSecurityContextHolder = mockStatic(SecurityContextHolder.class)) {
            mockedSecurityContextHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            when(securityContext.getAuthentication()).thenReturn(null);

            // When
            Long currentUserId = permissionService.getCurrentUserId();

            // Then
            assertNull(currentUserId);
        }
    }

    @Test
    void hasPermission_CurrentUser_WithValidPermission_ShouldReturnTrue() {
        // Given
        CustomUserPrincipal userPrincipal = new CustomUserPrincipal(testUser);
        List<Permission> permissions = Arrays.asList(testPermission);
        
        try (MockedStatic<SecurityContextHolder> mockedSecurityContextHolder = mockStatic(SecurityContextHolder.class)) {
            mockedSecurityContextHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            when(securityContext.getAuthentication()).thenReturn(authentication);
            when(authentication.isAuthenticated()).thenReturn(true);
            when(authentication.getPrincipal()).thenReturn(userPrincipal);
            when(permissionMapper.findByUserId(1L)).thenReturn(permissions);

            // When
            boolean hasPermission = permissionService.hasPermission("TEST_PERMISSION");

            // Then
            assertTrue(hasPermission);
        }
    }
}