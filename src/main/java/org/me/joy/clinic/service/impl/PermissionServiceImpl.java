package org.me.joy.clinic.service.impl;

import org.me.joy.clinic.entity.Permission;
import org.me.joy.clinic.entity.User;
import org.me.joy.clinic.exception.BusinessException;
import org.me.joy.clinic.exception.ValidationException;
import org.me.joy.clinic.mapper.PermissionMapper;
import org.me.joy.clinic.mapper.UserMapper;
import org.me.joy.clinic.security.CustomUserPrincipal;
import org.me.joy.clinic.service.PermissionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 权限服务实现类
 */
@Service
@Transactional
public class PermissionServiceImpl implements PermissionService {

    private static final Logger logger = LoggerFactory.getLogger(PermissionServiceImpl.class);

    @Autowired
    private PermissionMapper permissionMapper;

    @Autowired
    private UserMapper userMapper;

    @Override
    @Transactional(readOnly = true)
    public List<Permission> getAllPermissions() {
        logger.debug("获取所有权限");
        return permissionMapper.selectList(null);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Permission> getEnabledPermissions() {
        logger.debug("获取所有启用的权限");
        return permissionMapper.findAllEnabled();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Permission> getPermissionsByModule(String module) {
        if (!StringUtils.hasText(module)) {
            throw new ValidationException("MODULE_NAME_EMPTY", "模块名称不能为空");
        }
        
        logger.debug("根据模块获取权限: {}", module);
        return permissionMapper.findByModule(module);
    }

    @Override
    @Transactional(readOnly = true)
    public Permission getPermissionById(Long permissionId) {
        if (permissionId == null) {
            throw new ValidationException("PERMISSION_ID_NULL", "权限ID不能为空");
        }
        
        logger.debug("根据ID获取权限: {}", permissionId);
        Permission permission = permissionMapper.selectById(permissionId);
        if (permission == null) {
            throw new BusinessException("PERMISSION_NOT_FOUND", "权限不存在");
        }
        return permission;
    }

    @Override
    @Transactional(readOnly = true)
    public Permission getPermissionByCode(String permissionCode) {
        if (!StringUtils.hasText(permissionCode)) {
            throw new ValidationException("PERMISSION_CODE_EMPTY", "权限代码不能为空");
        }
        
        logger.debug("根据权限代码获取权限: {}", permissionCode);
        Permission permission = permissionMapper.findByPermissionCode(permissionCode);
        if (permission == null) {
            throw new BusinessException("PERMISSION_NOT_FOUND", "权限不存在");
        }
        return permission;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Permission> getUserPermissions(Long userId) {
        if (userId == null) {
            throw new ValidationException("USER_ID_NULL", "用户ID不能为空");
        }
        
        logger.debug("获取用户权限: {}", userId);
        return permissionMapper.findPermissionsByUserId(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<String> getUserPermissionCodes(Long userId) {
        List<Permission> permissions = getUserPermissions(userId);
        return permissions.stream()
                .map(Permission::getPermissionCode)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public boolean hasPermission(Long userId, String permissionCode) {
        if (userId == null || !StringUtils.hasText(permissionCode)) {
            return false;
        }
        
        List<String> userPermissions = getUserPermissionCodes(userId);
        return userPermissions.contains(permissionCode);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean hasAnyPermission(Long userId, String[] permissionCodes) {
        if (userId == null || permissionCodes == null || permissionCodes.length == 0) {
            return false;
        }
        
        List<String> userPermissions = getUserPermissionCodes(userId);
        for (String permissionCode : permissionCodes) {
            if (userPermissions.contains(permissionCode)) {
                return true;
            }
        }
        return false;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean hasAllPermissions(Long userId, String[] permissionCodes) {
        if (userId == null || permissionCodes == null || permissionCodes.length == 0) {
            return false;
        }
        
        List<String> userPermissions = getUserPermissionCodes(userId);
        for (String permissionCode : permissionCodes) {
            if (!userPermissions.contains(permissionCode)) {
                return false;
            }
        }
        return true;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean hasPermission(String permissionCode) {
        Long currentUserId = getCurrentUserId();
        if (currentUserId == null) {
            return false;
        }
        return hasPermission(currentUserId, permissionCode);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean hasAnyPermission(String[] permissionCodes) {
        Long currentUserId = getCurrentUserId();
        if (currentUserId == null) {
            return false;
        }
        return hasAnyPermission(currentUserId, permissionCodes);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean hasAllPermissions(String[] permissionCodes) {
        Long currentUserId = getCurrentUserId();
        if (currentUserId == null) {
            return false;
        }
        return hasAllPermissions(currentUserId, permissionCodes);
    }

    @Override
    @Transactional(readOnly = true)
    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }
        
        Object principal = authentication.getPrincipal();
        if (principal instanceof CustomUserPrincipal) {
            CustomUserPrincipal userPrincipal = (CustomUserPrincipal) principal;
            return userMapper.selectById(userPrincipal.getUserId());
        }
        
        return null;
    }

    @Override
    @Transactional(readOnly = true)
    public Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }
        
        Object principal = authentication.getPrincipal();
        if (principal instanceof CustomUserPrincipal) {
            CustomUserPrincipal userPrincipal = (CustomUserPrincipal) principal;
            return userPrincipal.getUserId();
        }
        
        return null;
    }
}