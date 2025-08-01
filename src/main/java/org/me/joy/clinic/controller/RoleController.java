package org.me.joy.clinic.controller;

import jakarta.validation.Valid;
import org.me.joy.clinic.dto.CreateRoleRequest;
import org.me.joy.clinic.dto.UpdateRoleRequest;
import org.me.joy.clinic.entity.Permission;
import org.me.joy.clinic.entity.Role;
import org.me.joy.clinic.security.RequiresPermission;
import org.me.joy.clinic.service.RoleManagementService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 角色管理控制器
 * 提供角色的CRUD操作API
 */
@RestController
@RequestMapping("/api/roles")
@RequiresPermission("ROLE_VIEW")
public class RoleController {

    private static final Logger logger = LoggerFactory.getLogger(RoleController.class);

    @Autowired
    private RoleManagementService roleManagementService;

    /**
     * 获取所有角色
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllRoles() {
        logger.info("获取所有角色");
        
        List<Role> roles = roleManagementService.getAllRoles();
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "获取角色列表成功");
        response.put("data", roles);
        
        return ResponseEntity.ok(response);
    }

    /**
     * 获取所有启用的角色
     */
    @GetMapping("/enabled")
    public ResponseEntity<Map<String, Object>> getEnabledRoles() {
        logger.info("获取所有启用的角色");
        
        List<Role> roles = roleManagementService.getEnabledRoles();
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "获取启用角色列表成功");
        response.put("data", roles);
        
        return ResponseEntity.ok(response);
    }

    /**
     * 根据ID获取角色
     */
    @GetMapping("/{roleId}")
    public ResponseEntity<Map<String, Object>> getRoleById(@PathVariable Long roleId) {
        logger.info("根据ID获取角色: {}", roleId);
        
        Role role = roleManagementService.getRoleById(roleId);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "获取角色信息成功");
        response.put("data", role);
        
        return ResponseEntity.ok(response);
    }

    /**
     * 创建新角色
     */
    @PostMapping
    @RequiresPermission("ROLE_CREATE")
    public ResponseEntity<Map<String, Object>> createRole(@Valid @RequestBody CreateRoleRequest request) {
        logger.info("创建新角色: {}", request.getRoleCode());
        
        Role role = roleManagementService.createRole(request);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "角色创建成功");
        response.put("data", role);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * 更新角色信息
     */
    @PutMapping("/{roleId}")
    @RequiresPermission("ROLE_UPDATE")
    public ResponseEntity<Map<String, Object>> updateRole(
            @PathVariable Long roleId,
            @Valid @RequestBody UpdateRoleRequest request) {
        logger.info("更新角色: {}", roleId);
        
        Role role = roleManagementService.updateRole(roleId, request);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "角色更新成功");
        response.put("data", role);
        
        return ResponseEntity.ok(response);
    }

    /**
     * 删除角色
     */
    @DeleteMapping("/{roleId}")
    @RequiresPermission("ROLE_DELETE")
    public ResponseEntity<Map<String, Object>> deleteRole(@PathVariable Long roleId) {
        logger.info("删除角色: {}", roleId);
        
        roleManagementService.deleteRole(roleId);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "角色删除成功");
        
        return ResponseEntity.ok(response);
    }

    /**
     * 启用角色
     */
    @PutMapping("/{roleId}/enable")
    @RequiresPermission("ROLE_UPDATE")
    public ResponseEntity<Map<String, Object>> enableRole(@PathVariable Long roleId) {
        logger.info("启用角色: {}", roleId);
        
        roleManagementService.enableRole(roleId);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "角色启用成功");
        
        return ResponseEntity.ok(response);
    }

    /**
     * 禁用角色
     */
    @PutMapping("/{roleId}/disable")
    @RequiresPermission("ROLE_UPDATE")
    public ResponseEntity<Map<String, Object>> disableRole(@PathVariable Long roleId) {
        logger.info("禁用角色: {}", roleId);
        
        roleManagementService.disableRole(roleId);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "角色禁用成功");
        
        return ResponseEntity.ok(response);
    }

    /**
     * 为用户分配角色
     */
    @PostMapping("/{roleId}/users/{userId}")
    @RequiresPermission({"ROLE_UPDATE", "USER_UPDATE"})
    public ResponseEntity<Map<String, Object>> assignRoleToUser(
            @PathVariable Long roleId,
            @PathVariable Long userId) {
        logger.info("为用户分配角色: userId={}, roleId={}", userId, roleId);
        
        roleManagementService.assignRoleToUser(userId, roleId);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "角色分配成功");
        
        return ResponseEntity.ok(response);
    }

    /**
     * 移除用户角色
     */
    @DeleteMapping("/{roleId}/users/{userId}")
    @RequiresPermission({"ROLE_UPDATE", "USER_UPDATE"})
    public ResponseEntity<Map<String, Object>> removeRoleFromUser(
            @PathVariable Long roleId,
            @PathVariable Long userId) {
        logger.info("移除用户角色: userId={}, roleId={}", userId, roleId);
        
        roleManagementService.removeRoleFromUser(userId, roleId);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "角色移除成功");
        
        return ResponseEntity.ok(response);
    }

    /**
     * 获取用户的所有角色
     */
    @GetMapping("/users/{userId}")
    public ResponseEntity<Map<String, Object>> getUserRoles(@PathVariable Long userId) {
        logger.info("获取用户角色: {}", userId);
        
        List<Role> roles = roleManagementService.getUserRoles(userId);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "获取用户角色成功");
        response.put("data", roles);
        
        return ResponseEntity.ok(response);
    }

    /**
     * 获取角色的所有权限
     */
    @GetMapping("/{roleId}/permissions")
    public ResponseEntity<Map<String, Object>> getRolePermissions(@PathVariable Long roleId) {
        logger.info("获取角色权限: {}", roleId);
        
        List<Permission> permissions = roleManagementService.getRolePermissions(roleId);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "获取角色权限成功");
        response.put("data", permissions);
        
        return ResponseEntity.ok(response);
    }

    /**
     * 为角色分配权限
     */
    @PostMapping("/{roleId}/permissions/{permissionId}")
    @RequiresPermission("ROLE_UPDATE")
    public ResponseEntity<Map<String, Object>> assignPermissionToRole(
            @PathVariable Long roleId,
            @PathVariable Long permissionId) {
        logger.info("为角色分配权限: roleId={}, permissionId={}", roleId, permissionId);
        
        roleManagementService.assignPermissionToRole(roleId, permissionId);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "权限分配成功");
        
        return ResponseEntity.ok(response);
    }

    /**
     * 移除角色权限
     */
    @DeleteMapping("/{roleId}/permissions/{permissionId}")
    @RequiresPermission("ROLE_UPDATE")
    public ResponseEntity<Map<String, Object>> removePermissionFromRole(
            @PathVariable Long roleId,
            @PathVariable Long permissionId) {
        logger.info("移除角色权限: roleId={}, permissionId={}", roleId, permissionId);
        
        roleManagementService.removePermissionFromRole(roleId, permissionId);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "权限移除成功");
        
        return ResponseEntity.ok(response);
    }

    /**
     * 批量为角色分配权限
     */
    @PutMapping("/{roleId}/permissions")
    @RequiresPermission("ROLE_UPDATE")
    public ResponseEntity<Map<String, Object>> assignPermissionsToRole(
            @PathVariable Long roleId,
            @RequestBody List<Long> permissionIds) {
        logger.info("批量为角色分配权限: roleId={}, permissionCount={}", roleId, permissionIds.size());
        
        roleManagementService.assignPermissionsToRole(roleId, permissionIds);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "权限批量分配成功");
        
        return ResponseEntity.ok(response);
    }

    /**
     * 初始化系统默认角色
     */
    @PostMapping("/initialize")
    @RequiresPermission("SYSTEM_CONFIG")
    public ResponseEntity<Map<String, Object>> initializeDefaultRoles() {
        logger.info("初始化系统默认角色");
        
        roleManagementService.initializeDefaultRoles();
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "系统默认角色初始化成功");
        
        return ResponseEntity.ok(response);
    }
}