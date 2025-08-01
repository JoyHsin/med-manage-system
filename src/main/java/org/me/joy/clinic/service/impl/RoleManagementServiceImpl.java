package org.me.joy.clinic.service.impl;

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
import org.me.joy.clinic.service.RoleManagementService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 角色管理服务实现类
 */
@Service
@Transactional
public class RoleManagementServiceImpl implements RoleManagementService {

    private static final Logger logger = LoggerFactory.getLogger(RoleManagementServiceImpl.class);

    @Autowired
    private RoleMapper roleMapper;

    @Autowired
    private PermissionMapper permissionMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserRoleMapper userRoleMapper;

    @Autowired
    private RolePermissionMapper rolePermissionMapper;

    @Override
    @Transactional(readOnly = true)
    public List<Role> getAllRoles() {
        logger.debug("获取所有角色");
        return roleMapper.selectList(null);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Role> getEnabledRoles() {
        logger.debug("获取所有启用的角色");
        return roleMapper.findAllEnabled();
    }

    @Override
    @Transactional(readOnly = true)
    public Role getRoleById(Long roleId) {
        if (roleId == null) {
            throw new ValidationException("ROLE_ID_NULL", "角色ID不能为空");
        }
        
        logger.debug("根据ID获取角色: {}", roleId);
        Role role = roleMapper.selectById(roleId);
        if (role == null) {
            throw new BusinessException("ROLE_NOT_FOUND", "角色不存在");
        }
        return role;
    }

    @Override
    @Transactional(readOnly = true)
    public Role getRoleByCode(String roleCode) {
        if (!StringUtils.hasText(roleCode)) {
            throw new ValidationException("ROLE_CODE_EMPTY", "角色代码不能为空");
        }
        
        logger.debug("根据角色代码获取角色: {}", roleCode);
        Role role = roleMapper.findByRoleCode(roleCode);
        if (role == null) {
            throw new BusinessException("ROLE_NOT_FOUND", "角色不存在");
        }
        return role;
    }

    @Override
    public Role createRole(CreateRoleRequest request) {
        if (request == null) {
            throw new ValidationException("CREATE_ROLE_REQUEST_NULL", "创建角色请求不能为空");
        }
        
        logger.info("创建新角色: {}", request.getRoleCode());
        
        // 检查角色代码是否已存在
        Role existingRole = roleMapper.findByRoleCode(request.getRoleCode());
        if (existingRole != null) {
            throw new BusinessException("ROLE_CODE_EXISTS", "角色代码已存在");
        }
        
        // 创建角色
        Role role = new Role();
        role.setRoleCode(request.getRoleCode());
        role.setRoleName(request.getRoleName());
        role.setDescription(request.getDescription());
        role.setEnabled(request.getEnabled() != null ? request.getEnabled() : true);
        role.setIsSystemRole(false); // 用户创建的角色都不是系统角色
        
        roleMapper.insert(role);
        logger.info("角色创建成功: {}", role.getId());
        
        return role;
    }

    @Override
    public Role updateRole(Long roleId, UpdateRoleRequest request) {
        if (roleId == null) {
            throw new ValidationException("ROLE_ID_NULL", "角色ID不能为空");
        }
        if (request == null) {
            throw new ValidationException("UPDATE_ROLE_REQUEST_NULL", "更新角色请求不能为空");
        }
        
        logger.info("更新角色: {}", roleId);
        
        Role role = getRoleById(roleId);
        
        // 更新角色信息
        if (StringUtils.hasText(request.getRoleName())) {
            role.setRoleName(request.getRoleName());
        }
        if (request.getDescription() != null) {
            role.setDescription(request.getDescription());
        }
        if (request.getEnabled() != null) {
            role.setEnabled(request.getEnabled());
        }
        
        roleMapper.updateById(role);
        logger.info("角色更新成功: {}", roleId);
        
        return role;
    }

    @Override
    public void deleteRole(Long roleId) {
        if (roleId == null) {
            throw new ValidationException("ROLE_ID_NULL", "角色ID不能为空");
        }
        
        logger.info("删除角色: {}", roleId);
        
        Role role = getRoleById(roleId);
        
        // 检查是否为系统预设角色
        if (role.getIsSystemRole()) {
            throw new BusinessException("SYSTEM_ROLE_CANNOT_DELETE", "系统预设角色不能删除");
        }
        
        // 检查是否有用户使用该角色
        List<User> usersWithRole = userMapper.findByRoleId(roleId);
        if (usersWithRole != null && !usersWithRole.isEmpty()) {
            throw new BusinessException("ROLE_HAS_USERS", "该角色下还有用户，不能删除");
        }
        
        // 先删除角色权限关联
        rolePermissionMapper.removeAllRolePermissions(roleId);
        
        // 删除角色
        roleMapper.deleteById(roleId);
        logger.info("角色删除成功: {}", roleId);
    }

    @Override
    public void enableRole(Long roleId) {
        if (roleId == null) {
            throw new ValidationException("ROLE_ID_NULL", "角色ID不能为空");
        }
        
        logger.info("启用角色: {}", roleId);
        
        Role role = getRoleById(roleId);
        role.setEnabled(true);
        
        roleMapper.updateById(role);
        logger.info("角色启用成功: {}", roleId);
    }

    @Override
    public void disableRole(Long roleId) {
        if (roleId == null) {
            throw new ValidationException("ROLE_ID_NULL", "角色ID不能为空");
        }
        
        logger.info("禁用角色: {}", roleId);
        
        Role role = getRoleById(roleId);
        
        // 检查是否为系统预设角色
        if (role.getIsSystemRole()) {
            throw new BusinessException("SYSTEM_ROLE_CANNOT_DISABLE", "系统预设角色不能禁用");
        }
        
        role.setEnabled(false);
        
        roleMapper.updateById(role);
        logger.info("角色禁用成功: {}", roleId);
    }

    @Override
    public void assignRoleToUser(Long userId, Long roleId) {
        if (userId == null) {
            throw new ValidationException("USER_ID_NULL", "用户ID不能为空");
        }
        if (roleId == null) {
            throw new ValidationException("ROLE_ID_NULL", "角色ID不能为空");
        }
        
        logger.info("为用户分配角色: userId={}, roleId={}", userId, roleId);
        
        // 检查用户是否存在
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("USER_NOT_FOUND", "用户不存在");
        }
        
        // 检查角色是否存在且启用
        Role role = getRoleById(roleId);
        if (!role.getEnabled()) {
            throw new BusinessException("ROLE_DISABLED", "角色已禁用，不能分配");
        }
        
        // 检查是否已经分配
        if (userRoleMapper.countUserRole(userId, roleId) > 0) {
            throw new BusinessException("USER_ROLE_EXISTS", "用户已拥有该角色");
        }
        
        // 分配角色
        userRoleMapper.addUserRole(userId, roleId);
        logger.info("角色分配成功: userId={}, roleId={}", userId, roleId);
    }

    @Override
    public void removeRoleFromUser(Long userId, Long roleId) {
        if (userId == null) {
            throw new ValidationException("USER_ID_NULL", "用户ID不能为空");
        }
        if (roleId == null) {
            throw new ValidationException("ROLE_ID_NULL", "角色ID不能为空");
        }
        
        logger.info("移除用户角色: userId={}, roleId={}", userId, roleId);
        
        // 检查用户是否存在
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("USER_NOT_FOUND", "用户不存在");
        }
        
        // 检查是否拥有该角色
        if (userRoleMapper.countUserRole(userId, roleId) == 0) {
            throw new BusinessException("USER_ROLE_NOT_EXISTS", "用户未拥有该角色");
        }
        
        // 移除角色
        userRoleMapper.removeUserRole(userId, roleId);
        logger.info("角色移除成功: userId={}, roleId={}", userId, roleId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Role> getUserRoles(Long userId) {
        if (userId == null) {
            throw new ValidationException("USER_ID_NULL", "用户ID不能为空");
        }
        
        logger.debug("获取用户角色: {}", userId);
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("USER_NOT_FOUND", "用户不存在");
        }
        return userRoleMapper.findRolesByUserId(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Permission> getRolePermissions(Long roleId) {
        if (roleId == null) {
            throw new ValidationException("ROLE_ID_NULL", "角色ID不能为空");
        }
        
        logger.debug("获取角色权限: {}", roleId);
        return rolePermissionMapper.findPermissionsByRoleId(roleId);
    }

    @Override
    public void assignPermissionToRole(Long roleId, Long permissionId) {
        if (roleId == null) {
            throw new ValidationException("ROLE_ID_NULL", "角色ID不能为空");
        }
        if (permissionId == null) {
            throw new ValidationException("PERMISSION_ID_NULL", "权限ID不能为空");
        }
        
        logger.info("为角色分配权限: roleId={}, permissionId={}", roleId, permissionId);
        
        // 检查角色是否存在
        Role role = getRoleById(roleId);
        
        // 检查权限是否存在
        Permission permission = permissionMapper.selectById(permissionId);
        if (permission == null) {
            throw new BusinessException("PERMISSION_NOT_FOUND", "权限不存在");
        }
        
        // 检查是否已经分配
        if (rolePermissionMapper.countRolePermission(roleId, permissionId) > 0) {
            throw new BusinessException("ROLE_PERMISSION_EXISTS", "角色已拥有该权限");
        }
        
        // 分配权限
        rolePermissionMapper.addRolePermission(roleId, permissionId);
        logger.info("权限分配成功: roleId={}, permissionId={}", roleId, permissionId);
    }

    @Override
    public void removePermissionFromRole(Long roleId, Long permissionId) {
        if (roleId == null) {
            throw new ValidationException("ROLE_ID_NULL", "角色ID不能为空");
        }
        if (permissionId == null) {
            throw new ValidationException("PERMISSION_ID_NULL", "权限ID不能为空");
        }
        
        logger.info("移除角色权限: roleId={}, permissionId={}", roleId, permissionId);
        
        // 检查角色是否存在
        Role role = getRoleById(roleId);
        
        Permission permission = permissionMapper.selectById(permissionId);
        if (permission == null) {
            throw new BusinessException("PERMISSION_NOT_FOUND", "权限不存在");
        }
        
        // 检查是否拥有该权限
        if (rolePermissionMapper.countRolePermission(roleId, permissionId) == 0) {
            throw new BusinessException("ROLE_PERMISSION_NOT_EXISTS", "角色未拥有该权限");
        }
        
        // 移除权限
        rolePermissionMapper.removeRolePermission(roleId, permissionId);
        logger.info("权限移除成功: roleId={}, permissionId={}", roleId, permissionId);
    }

    @Override
    public void assignPermissionsToRole(Long roleId, List<Long> permissionIds) {
        if (roleId == null) {
            throw new ValidationException("ROLE_ID_NULL", "角色ID不能为空");
        }
        if (permissionIds == null || permissionIds.isEmpty()) {
            throw new ValidationException("PERMISSION_IDS_EMPTY", "权限ID列表不能为空");
        }
        
        logger.info("批量为角色分配权限: roleId={}, permissionCount={}", roleId, permissionIds.size());
        
        // 检查角色是否存在
        Role role = getRoleById(roleId);
        
        // 先清除现有权限
        rolePermissionMapper.removeAllRolePermissions(roleId);
        
        // 分配新权限
        for (Long permissionId : permissionIds) {
            // 检查权限是否存在
            Permission permission = permissionMapper.selectById(permissionId);
            if (permission == null) {
                throw new BusinessException("PERMISSION_NOT_FOUND", "权限不存在: " + permissionId);
            }
            
            rolePermissionMapper.addRolePermission(roleId, permissionId);
        }
        
        logger.info("批量权限分配成功: roleId={}, permissionCount={}", roleId, permissionIds.size());
    }

    @Override
    @Transactional(readOnly = true)
    public boolean hasRole(Long userId, String roleCode) {
        if (userId == null || !StringUtils.hasText(roleCode)) {
            return false;
        }
        
        List<Role> userRoles = getUserRoles(userId);
        return userRoles.stream().anyMatch(role -> roleCode.equals(role.getRoleCode()));
    }

    @Override
    @Transactional(readOnly = true)
    public boolean hasPermission(Long roleId, String permissionCode) {
        if (roleId == null || !StringUtils.hasText(permissionCode)) {
            return false;
        }
        
        List<Permission> rolePermissions = getRolePermissions(roleId);
        return rolePermissions.stream().anyMatch(permission -> permissionCode.equals(permission.getPermissionCode()));
    }

    @Override
    public void initializeDefaultRoles() {
        logger.info("初始化系统默认角色");
        
        // 检查是否已经初始化
        List<Role> systemRoles = roleMapper.findByIsSystemRoleTrue();
        if (!systemRoles.isEmpty()) {
            logger.info("系统默认角色已存在，跳过初始化");
            return;
        }
        
        // 创建默认角色
        createDefaultRole("SUPER_ADMIN", "超级管理员", "系统超级管理员，拥有所有权限");
        createDefaultRole("ADMIN", "管理员", "系统管理员，拥有大部分管理权限");
        createDefaultRole("DOCTOR", "医生", "医生角色，拥有诊疗相关权限");
        createDefaultRole("NURSE", "护士", "护士角色，拥有护理相关权限");
        createDefaultRole("PHARMACIST", "药剂师", "药剂师角色，拥有药品管理权限");
        createDefaultRole("RECEPTIONIST", "前台文员", "前台文员角色，拥有基础操作权限");
        
        logger.info("系统默认角色初始化完成");
    }

    /**
     * 创建默认角色
     */
    private void createDefaultRole(String roleCode, String roleName, String description) {
        Role role = new Role();
        role.setRoleCode(roleCode);
        role.setRoleName(roleName);
        role.setDescription(description);
        role.setEnabled(true);
        role.setIsSystemRole(true);
        
        roleMapper.insert(role);
        logger.info("创建默认角色: {} - {}", roleCode, roleName);
    }
}