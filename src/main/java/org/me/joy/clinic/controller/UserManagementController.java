package org.me.joy.clinic.controller;

import jakarta.validation.Valid;
import org.me.joy.clinic.dto.CreateUserRequest;
import org.me.joy.clinic.dto.UpdateUserRequest;
import org.me.joy.clinic.dto.UserResponse;
import org.me.joy.clinic.security.RequiresPermission;
import org.me.joy.clinic.service.UserManagementService;
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
 * 用户管理控制器
 * 处理用户账号的创建、编辑、禁用、删除等管理功能
 */
@RestController
@RequestMapping("/users")
@CrossOrigin(origins = "*", maxAge = 3600)
public class UserManagementController {

    private static final Logger logger = LoggerFactory.getLogger(UserManagementController.class);

    @Autowired
    private UserManagementService userManagementService;

    /**
     * 创建新用户
     */
    @PostMapping
    @RequiresPermission("USER_CREATE")
    public ResponseEntity<?> createUser(@Valid @RequestBody CreateUserRequest createUserRequest) {
        try {
            logger.info("收到创建用户请求: {}", createUserRequest.getUsername());
            
            UserResponse userResponse = userManagementService.createUser(createUserRequest);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "用户创建成功");
            response.put("data", userResponse);
            response.put("timestamp", System.currentTimeMillis());
            
            logger.info("用户创建成功: {}", createUserRequest.getUsername());
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
            
        } catch (Exception e) {
            logger.error("用户创建失败: {}", e.getMessage());
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", e.getMessage());
            errorResponse.put("timestamp", System.currentTimeMillis());
            
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    /**
     * 更新用户信息
     */
    @PutMapping("/{userId}")
    @RequiresPermission("USER_UPDATE")
    public ResponseEntity<?> updateUser(@PathVariable Long userId, 
                                       @Valid @RequestBody UpdateUserRequest updateUserRequest) {
        try {
            logger.info("收到更新用户请求: {}", userId);
            
            UserResponse userResponse = userManagementService.updateUser(userId, updateUserRequest);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "用户信息更新成功");
            response.put("data", userResponse);
            response.put("timestamp", System.currentTimeMillis());
            
            logger.info("用户信息更新成功: {}", userId);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("用户信息更新失败: {}", e.getMessage());
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", e.getMessage());
            errorResponse.put("timestamp", System.currentTimeMillis());
            
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    /**
     * 根据ID获取用户信息
     */
    @GetMapping("/{userId}")
    @RequiresPermission("USER_VIEW")
    public ResponseEntity<?> getUserById(@PathVariable Long userId) {
        try {
            UserResponse userResponse = userManagementService.getUserById(userId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", userResponse);
            response.put("timestamp", System.currentTimeMillis());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("获取用户信息失败: {}", e.getMessage());
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", e.getMessage());
            errorResponse.put("timestamp", System.currentTimeMillis());
            
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    /**
     * 根据用户名获取用户信息
     */
    @GetMapping("/username/{username}")
    @RequiresPermission("USER_VIEW")
    public ResponseEntity<?> getUserByUsername(@PathVariable String username) {
        try {
            UserResponse userResponse = userManagementService.getUserByUsername(username);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", userResponse);
            response.put("timestamp", System.currentTimeMillis());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("获取用户信息失败: {}", e.getMessage());
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", e.getMessage());
            errorResponse.put("timestamp", System.currentTimeMillis());
            
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    /**
     * 获取所有用户列表
     */
    @GetMapping
    @RequiresPermission("USER_VIEW")
    public ResponseEntity<?> getAllUsers(@RequestParam(required = false) Boolean enabled,
                                        @RequestParam(required = false) String department,
                                        @RequestParam(required = false) String keyword) {
        try {
            List<UserResponse> users;
            
            if (keyword != null && !keyword.trim().isEmpty()) {
                users = userManagementService.searchUsers(keyword);
            } else if (enabled != null) {
                users = userManagementService.getUsersByEnabled(enabled);
            } else if (department != null && !department.trim().isEmpty()) {
                users = userManagementService.getUsersByDepartment(department);
            } else {
                users = userManagementService.getAllUsers();
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", users);
            response.put("total", users.size());
            response.put("timestamp", System.currentTimeMillis());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("获取用户列表失败: {}", e.getMessage());
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", e.getMessage());
            errorResponse.put("timestamp", System.currentTimeMillis());
            
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    /**
     * 禁用用户
     */
    @PutMapping("/{userId}/disable")
    @RequiresPermission("USER_UPDATE")
    public ResponseEntity<?> disableUser(@PathVariable Long userId) {
        try {
            logger.info("收到禁用用户请求: {}", userId);
            
            userManagementService.disableUser(userId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "用户禁用成功");
            response.put("timestamp", System.currentTimeMillis());
            
            logger.info("用户禁用成功: {}", userId);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("用户禁用失败: {}", e.getMessage());
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", e.getMessage());
            errorResponse.put("timestamp", System.currentTimeMillis());
            
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    /**
     * 启用用户
     */
    @PutMapping("/{userId}/enable")
    @RequiresPermission("USER_UPDATE")
    public ResponseEntity<?> enableUser(@PathVariable Long userId) {
        try {
            logger.info("收到启用用户请求: {}", userId);
            
            userManagementService.enableUser(userId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "用户启用成功");
            response.put("timestamp", System.currentTimeMillis());
            
            logger.info("用户启用成功: {}", userId);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("用户启用失败: {}", e.getMessage());
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", e.getMessage());
            errorResponse.put("timestamp", System.currentTimeMillis());
            
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    /**
     * 删除用户
     */
    @DeleteMapping("/{userId}")
    @RequiresPermission("USER_DELETE")
    public ResponseEntity<?> deleteUser(@PathVariable Long userId) {
        try {
            logger.info("收到删除用户请求: {}", userId);
            
            userManagementService.deleteUser(userId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "用户删除成功");
            response.put("timestamp", System.currentTimeMillis());
            
            logger.info("用户删除成功: {}", userId);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("用户删除失败: {}", e.getMessage());
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", e.getMessage());
            errorResponse.put("timestamp", System.currentTimeMillis());
            
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    /**
     * 为用户分配角色
     */
    @PostMapping("/{userId}/roles/{roleId}")
    @RequiresPermission("USER_UPDATE")
    public ResponseEntity<?> assignRoleToUser(@PathVariable Long userId, @PathVariable Long roleId) {
        try {
            logger.info("收到用户角色分配请求: userId={}, roleId={}", userId, roleId);
            
            userManagementService.assignRoleToUser(userId, roleId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "用户角色分配成功");
            response.put("timestamp", System.currentTimeMillis());
            
            logger.info("用户角色分配成功: userId={}, roleId={}", userId, roleId);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("用户角色分配失败: {}", e.getMessage());
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", e.getMessage());
            errorResponse.put("timestamp", System.currentTimeMillis());
            
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    /**
     * 移除用户角色
     */
    @DeleteMapping("/{userId}/roles/{roleId}")
    @RequiresPermission("USER_UPDATE")
    public ResponseEntity<?> removeRoleFromUser(@PathVariable Long userId, @PathVariable Long roleId) {
        try {
            logger.info("收到移除用户角色请求: userId={}, roleId={}", userId, roleId);
            
            userManagementService.removeRoleFromUser(userId, roleId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "用户角色移除成功");
            response.put("timestamp", System.currentTimeMillis());
            
            logger.info("用户角色移除成功: userId={}, roleId={}", userId, roleId);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("用户角色移除失败: {}", e.getMessage());
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", e.getMessage());
            errorResponse.put("timestamp", System.currentTimeMillis());
            
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    /**
     * 重置用户密码
     */
    @PutMapping("/{userId}/reset-password")
    @RequiresPermission("USER_UPDATE")
    public ResponseEntity<?> resetUserPassword(@PathVariable Long userId, 
                                              @RequestBody Map<String, String> request) {
        try {
            logger.info("收到重置用户密码请求: {}", userId);
            
            String newPassword = request.get("newPassword");
            if (newPassword == null || newPassword.trim().isEmpty()) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("message", "新密码不能为空");
                errorResponse.put("timestamp", System.currentTimeMillis());
                return ResponseEntity.badRequest().body(errorResponse);
            }
            
            userManagementService.resetUserPassword(userId, newPassword);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "用户密码重置成功");
            response.put("timestamp", System.currentTimeMillis());
            
            logger.info("用户密码重置成功: {}", userId);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("用户密码重置失败: {}", e.getMessage());
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", e.getMessage());
            errorResponse.put("timestamp", System.currentTimeMillis());
            
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    /**
     * 检查用户名是否存在
     */
    @GetMapping("/check-username/{username}")
    @RequiresPermission("USER_VIEW")
    public ResponseEntity<?> checkUsernameExists(@PathVariable String username) {
        try {
            boolean exists = userManagementService.existsByUsername(username);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("exists", exists);
            response.put("timestamp", System.currentTimeMillis());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("检查用户名失败: {}", e.getMessage());
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", e.getMessage());
            errorResponse.put("timestamp", System.currentTimeMillis());
            
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    /**
     * 检查邮箱是否存在
     */
    @GetMapping("/check-email/{email}")
    @RequiresPermission("USER_VIEW")
    public ResponseEntity<?> checkEmailExists(@PathVariable String email) {
        try {
            boolean exists = userManagementService.existsByEmail(email);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("exists", exists);
            response.put("timestamp", System.currentTimeMillis());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("检查邮箱失败: {}", e.getMessage());
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", e.getMessage());
            errorResponse.put("timestamp", System.currentTimeMillis());
            
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
}