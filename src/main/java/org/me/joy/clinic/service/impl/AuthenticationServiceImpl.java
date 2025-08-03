package org.me.joy.clinic.service.impl;

import org.me.joy.clinic.dto.AuthenticationResponse;
import org.me.joy.clinic.dto.ChangePasswordRequest;
import org.me.joy.clinic.dto.LoginRequest;
import org.me.joy.clinic.dto.UserResponse;
import org.me.joy.clinic.entity.User;
import org.me.joy.clinic.exception.BusinessException;
import org.me.joy.clinic.mapper.UserMapper;
import org.me.joy.clinic.security.CustomUserPrincipal;
import org.me.joy.clinic.security.JwtUtil;
import org.me.joy.clinic.service.AuthenticationService;
import org.me.joy.clinic.service.PasswordService;
import org.me.joy.clinic.service.SessionManagementService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 认证服务实现类
 */
@Service
@Transactional
public class AuthenticationServiceImpl implements AuthenticationService {

    private static final Logger logger = LoggerFactory.getLogger(AuthenticationServiceImpl.class);

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PasswordService passwordService;

    @Autowired
    private SessionManagementService sessionManagementService;

    @Value("${jwt.expiration:1800000}") // 30分钟
    private Long jwtExpiration;

    // 用于存储已登出的令牌（简单实现，生产环境建议使用Redis）
    private final Set<String> blacklistedTokens = ConcurrentHashMap.newKeySet();

    @Override
    public AuthenticationResponse authenticate(LoginRequest loginRequest) {
        try {
            logger.info("用户登录尝试: {}", loginRequest.getUsername());

            // 使用Spring Security进行认证
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    loginRequest.getUsername(), 
                    loginRequest.getPassword()
                )
            );

            CustomUserPrincipal userPrincipal = (CustomUserPrincipal) authentication.getPrincipal();
            User user = userPrincipal.getUser();

            // 生成JWT令牌
            String token = jwtUtil.generateToken(userPrincipal);

            // 创建会话
            LocalDateTime sessionExpiration = LocalDateTime.now().plusSeconds(jwtExpiration / 1000);
            sessionManagementService.createSession(user.getUsername(), token, sessionExpiration);

            // 更新用户最后登录信息
            user.setLastLoginTime(LocalDateTime.now());
            user.setLastLoginIp(getClientIpAddress());
            
            // 重置登录失败次数
            if (user.getFailedLoginAttempts() > 0) {
                user.resetFailedLoginAttempts();
            }
            
            userMapper.updateById(user);

            logger.info("用户登录成功: {}", loginRequest.getUsername());

            return new AuthenticationResponse(
                token,
                user.getId(),
                user.getUsername(),
                user.getFullName(),
                user.getEmail(),
                user.getDepartment(),
                user.getPosition(),
                jwtExpiration
            );

        } catch (DisabledException e) {
            logger.warn("用户账号已禁用: {}", loginRequest.getUsername());
            throw new BusinessException("1001", "账号已被禁用，请联系管理员");
        } catch (LockedException e) {
            logger.warn("用户账号已锁定: {}", loginRequest.getUsername());
            throw new BusinessException("1002", "账号已被锁定，请联系管理员或稍后再试");
        } catch (BadCredentialsException e) {
            logger.warn("用户登录失败，用户名或密码错误: {}", loginRequest.getUsername());
            
            // 增加登录失败次数
            try {
                User user = getUserByUsername(loginRequest.getUsername());
                if (user != null) {
                    user.incrementFailedLoginAttempts();
                    
                    // 检查是否需要锁定账号
                    if (user.getFailedLoginAttempts() >= 5) {
                        throw new BusinessException("1002", "登录失败次数过多，账号已被锁定30分钟");
                    }
                    
                    userMapper.updateById(user);
                }
            } catch (Exception ex) {
                logger.error("更新登录失败次数时出错", ex);
            }
            
            throw new BusinessException("1003", "用户名或密码错误");
        } catch (AuthenticationException e) {
            logger.error("认证过程中发生错误: {}", e.getMessage());
            throw new BusinessException("1004", "认证失败，请稍后再试");
        } catch (Exception e) {
            logger.error("登录过程中发生未知错误", e);
            throw new BusinessException("1005", "登录失败，请稍后再试");
        }
    }

    @Override
    public void logout(String token) {
        if (token != null && token.startsWith("Bearer ")) {
            String jwtToken = token.substring(7);
            blacklistedTokens.add(jwtToken);
            sessionManagementService.destroySession(jwtToken);
            logger.info("用户登出，令牌已加入黑名单并销毁会话");
        }
    }

    @Override
    public boolean validateToken(String token) {
        if (token == null || blacklistedTokens.contains(token)) {
            return false;
        }
        // 验证JWT令牌和会话
        return jwtUtil.validateToken(token) && sessionManagementService.isSessionValid(token);
    }

    @Override
    public AuthenticationResponse refreshToken(String token) {
        try {
            if (blacklistedTokens.contains(token)) {
                throw new BusinessException("1006", "令牌已失效");
            }

            String username = jwtUtil.extractUsername(token);
            User user = getUserByUsername(username);
            
            if (user == null) {
                throw new BusinessException("1007", "用户不存在");
            }

            CustomUserPrincipal userPrincipal = new CustomUserPrincipal(user);
            String newToken = jwtUtil.generateToken(userPrincipal);

            // 将旧令牌加入黑名单并销毁旧会话
            blacklistedTokens.add(token);
            sessionManagementService.destroySession(token);

            // 创建新会话
            LocalDateTime sessionExpiration = LocalDateTime.now().plusSeconds(jwtExpiration / 1000);
            sessionManagementService.createSession(user.getUsername(), newToken, sessionExpiration);

            return new AuthenticationResponse(
                newToken,
                user.getId(),
                user.getUsername(),
                user.getFullName(),
                user.getEmail(),
                user.getDepartment(),
                user.getPosition(),
                jwtExpiration
            );

        } catch (Exception e) {
            logger.error("刷新令牌时发生错误", e);
            throw new BusinessException("1008", "令牌刷新失败");
        }
    }

    /**
     * 根据用户名获取用户
     */
    private User getUserByUsername(String username) {
        try {
            return userMapper.findByUsername(username);
        } catch (Exception e) {
            logger.error("查询用户时发生错误: {}", username, e);
            return null;
        }
    }

    @Override
    public void changePassword(String username, ChangePasswordRequest changePasswordRequest) {
        passwordService.changePassword(username, changePasswordRequest);
    }

    @Override
    public UserResponse getCurrentUserInfo(String username) {
        try {
            User user = getUserByUsername(username);
            if (user == null) {
                throw new BusinessException("1004", "用户不存在");
            }

            // 创建UserResponse对象
            UserResponse userResponse = new UserResponse();
            userResponse.setId(user.getId());
            userResponse.setUsername(user.getUsername());
            userResponse.setFullName(user.getFullName());
            userResponse.setEmail(user.getEmail());
            userResponse.setPhone(user.getPhone());
            userResponse.setDepartment(user.getDepartment());
            userResponse.setPosition(user.getPosition());
            userResponse.setEnabled(user.getEnabled());
            userResponse.setAccountNonExpired(user.getAccountNonExpired());
            userResponse.setAccountNonLocked(user.getAccountNonLocked());
            userResponse.setCredentialsNonExpired(user.getCredentialsNonExpired());
            userResponse.setLastLoginTime(user.getLastLoginTime());
            userResponse.setLastLoginIp(user.getLastLoginIp());
            userResponse.setPasswordChangedTime(user.getPasswordChangedTime());
            userResponse.setFailedLoginAttempts(user.getFailedLoginAttempts());
            userResponse.setCreatedAt(user.getCreatedAt());
            userResponse.setUpdatedAt(user.getUpdatedAt());

            // 获取用户的权限信息
            List<String> permissions = userMapper.findPermissionsByUsername(username);
            userResponse.setPermissions(permissions);

            return userResponse;
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            logger.error("获取用户信息时发生错误: {}", username, e);
            throw new BusinessException("1009", "获取用户信息失败");
        }
    }

    /**
     * 获取客户端IP地址（简单实现）
     */
    private String getClientIpAddress() {
        // TODO: 在实际实现中，应该从HttpServletRequest中获取真实的客户端IP
        return "127.0.0.1";
    }
}