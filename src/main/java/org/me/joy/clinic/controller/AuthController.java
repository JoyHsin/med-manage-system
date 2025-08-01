package org.me.joy.clinic.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.me.joy.clinic.dto.AuthenticationResponse;
import org.me.joy.clinic.dto.ChangePasswordRequest;
import org.me.joy.clinic.dto.LoginRequest;
import org.me.joy.clinic.security.JwtUtil;
import org.me.joy.clinic.service.AuthenticationService;
import org.me.joy.clinic.service.SessionManagementService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 认证控制器
 * 处理用户登录、登出等认证相关请求
 */
@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*", maxAge = 3600)
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private SessionManagementService sessionManagementService;

    /**
     * 用户登录
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            logger.info("收到登录请求: {}", loginRequest.getUsername());
            
            AuthenticationResponse response = authenticationService.authenticate(loginRequest);
            
            logger.info("用户登录成功: {}", loginRequest.getUsername());
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("登录失败: {}", e.getMessage());
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", e.getMessage());
            errorResponse.put("timestamp", System.currentTimeMillis());
            
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    /**
     * 用户登出
     */
    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request) {
        try {
            String token = request.getHeader("Authorization");
            authenticationService.logout(token);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "登出成功");
            response.put("timestamp", System.currentTimeMillis());
            
            logger.info("用户登出成功");
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("登出失败: {}", e.getMessage());
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "登出失败");
            errorResponse.put("timestamp", System.currentTimeMillis());
            
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    /**
     * 验证令牌
     */
    @GetMapping("/validate")
    public ResponseEntity<?> validateToken(HttpServletRequest request) {
        try {
            String token = request.getHeader("Authorization");
            if (token != null && token.startsWith("Bearer ")) {
                String jwtToken = token.substring(7);
                boolean isValid = authenticationService.validateToken(jwtToken);
                
                Map<String, Object> response = new HashMap<>();
                response.put("valid", isValid);
                response.put("timestamp", System.currentTimeMillis());
                
                return ResponseEntity.ok(response);
            } else {
                Map<String, Object> response = new HashMap<>();
                response.put("valid", false);
                response.put("message", "无效的令牌格式");
                response.put("timestamp", System.currentTimeMillis());
                
                return ResponseEntity.badRequest().body(response);
            }
        } catch (Exception e) {
            logger.error("令牌验证失败: {}", e.getMessage());
            
            Map<String, Object> response = new HashMap<>();
            response.put("valid", false);
            response.put("message", "令牌验证失败");
            response.put("timestamp", System.currentTimeMillis());
            
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 刷新令牌
     */
    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(HttpServletRequest request) {
        try {
            String token = request.getHeader("Authorization");
            if (token != null && token.startsWith("Bearer ")) {
                String jwtToken = token.substring(7);
                AuthenticationResponse response = authenticationService.refreshToken(jwtToken);
                
                logger.info("令牌刷新成功");
                return ResponseEntity.ok(response);
            } else {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("message", "无效的令牌格式");
                errorResponse.put("timestamp", System.currentTimeMillis());
                
                return ResponseEntity.badRequest().body(errorResponse);
            }
        } catch (Exception e) {
            logger.error("令牌刷新失败: {}", e.getMessage());
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", e.getMessage());
            errorResponse.put("timestamp", System.currentTimeMillis());
            
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    /**
     * 修改密码
     */
    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@Valid @RequestBody ChangePasswordRequest changePasswordRequest, 
                                          HttpServletRequest request) {
        try {
            // 从JWT令牌中获取用户名
            String token = request.getHeader("Authorization");
            if (token == null || !token.startsWith("Bearer ")) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("message", "未提供有效的认证令牌");
                errorResponse.put("timestamp", System.currentTimeMillis());
                return ResponseEntity.badRequest().body(errorResponse);
            }

            String jwtToken = token.substring(7);
            String username = jwtUtil.extractUsername(jwtToken);
            
            logger.info("用户 {} 尝试修改密码", username);
            
            authenticationService.changePassword(username, changePasswordRequest);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "密码修改成功");
            response.put("timestamp", System.currentTimeMillis());
            
            logger.info("用户 {} 密码修改成功", username);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("密码修改失败: {}", e.getMessage());
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", e.getMessage());
            errorResponse.put("timestamp", System.currentTimeMillis());
            
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    /**
     * 获取会话信息
     */
    @GetMapping("/session-info")
    public ResponseEntity<?> getSessionInfo(HttpServletRequest request) {
        try {
            String token = request.getHeader("Authorization");
            if (token == null || !token.startsWith("Bearer ")) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("message", "未提供有效的认证令牌");
                errorResponse.put("timestamp", System.currentTimeMillis());
                return ResponseEntity.badRequest().body(errorResponse);
            }

            String jwtToken = token.substring(7);
            long remainingMinutes = sessionManagementService.getSessionRemainingMinutes(jwtToken);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("remainingMinutes", remainingMinutes);
            response.put("isValid", remainingMinutes > 0);
            response.put("timestamp", System.currentTimeMillis());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("获取会话信息失败: {}", e.getMessage());
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "获取会话信息失败");
            errorResponse.put("timestamp", System.currentTimeMillis());
            
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    /**
     * 获取当前用户信息
     */
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(HttpServletRequest request) {
        try {
            // TODO: 在后续任务中实现获取当前用户信息的功能
            Map<String, Object> response = new HashMap<>();
            response.put("message", "功能待实现");
            response.put("timestamp", System.currentTimeMillis());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("获取用户信息失败: {}", e.getMessage());
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "获取用户信息失败");
            errorResponse.put("timestamp", System.currentTimeMillis());
            
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
}