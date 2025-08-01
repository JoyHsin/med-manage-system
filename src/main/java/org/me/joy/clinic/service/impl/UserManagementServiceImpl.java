package org.me.joy.clinic.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
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
import org.me.joy.clinic.service.UserManagementService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户管理服务实现类
 */
@Service
@Transactional
public class UserManagementServiceImpl implements UserManagementService {

    private static final Logger logger = LoggerFactory.getLogger(UserManagementServiceImpl.class);

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private RoleMapper roleMapper;

    @Autowired
    private PasswordService passwordService;

    @Autowired
    private RoleManagementService roleManagementService;

    @Override
    public UserResponse createUser(CreateUserRequest createUserRequest) {
        logger.info("创建新用户: {}", createUserRequest.getUsername());

        // 验证用户名是否已存在
        if (existsByUsername(createUserRequest.getUsername())) {
            throw new ValidationException("2010", "用户名已存在");
        }

        // 验证邮箱是否已存在
        if (createUserRequest.getEmail() != null && existsByEmail(createUserRequest.getEmail())) {
            throw new ValidationException("2011", "邮箱已存在");
        }

        // 验证员工工号是否已存在
        if (createUserRequest.getEmployeeId() != null && existsByEmployeeId(createUserRequest.getEmployeeId())) {
            throw new ValidationException("2012", "员工工号已存在");
        }

        // 创建用户实体
        User user = new User();
        user.setUsername(createUserRequest.getUsername());
        user.setPassword(passwordService.encodePassword(createUserRequest.getPassword()));
        user.setFullName(createUserRequest.getFullName());
        user.setEmail(createUserRequest.getEmail());
        user.setPhone(createUserRequest.getPhone());
        user.setHireDate(createUserRequest.getHireDate());
        user.setEmployeeId(createUserRequest.getEmployeeId());
        user.setDepartment(createUserRequest.getDepartment());
        user.setPosition(createUserRequest.getPosition());
        user.setEnabled(createUserRequest.getEnabled() != null ? createUserRequest.getEnabled() : true);
        user.setAccountNonExpired(true);
        user.setAccountNonLocked(true);
        user.setCredentialsNonExpired(true);
        user.setPasswordChangedTime(LocalDateTime.now());

        // 保存用户
        userMapper.insert(user);

        logger.info("用户创建成功: {}, ID: {}", user.getUsername(), user.getId());

        return convertToUserResponse(user);
    }

    @Override
    public UserResponse updateUser(Long userId, UpdateUserRequest updateUserRequest) {
        logger.info("更新用户信息: {}", userId);

        User user = getUserEntityById(userId);

        // 验证邮箱是否已被其他用户使用
        if (updateUserRequest.getEmail() != null && !updateUserRequest.getEmail().equals(user.getEmail())) {
            if (existsByEmail(updateUserRequest.getEmail())) {
                throw new ValidationException("2011", "邮箱已存在");
            }
        }

        // 验证员工工号是否已被其他用户使用
        if (updateUserRequest.getEmployeeId() != null && !updateUserRequest.getEmployeeId().equals(user.getEmployeeId())) {
            if (existsByEmployeeId(updateUserRequest.getEmployeeId())) {
                throw new ValidationException("2012", "员工工号已存在");
            }
        }

        // 更新用户信息
        if (updateUserRequest.getFullName() != null) {
            user.setFullName(updateUserRequest.getFullName());
        }
        if (updateUserRequest.getEmail() != null) {
            user.setEmail(updateUserRequest.getEmail());
        }
        if (updateUserRequest.getPhone() != null) {
            user.setPhone(updateUserRequest.getPhone());
        }
        if (updateUserRequest.getHireDate() != null) {
            user.setHireDate(updateUserRequest.getHireDate());
        }
        if (updateUserRequest.getEmployeeId() != null) {
            user.setEmployeeId(updateUserRequest.getEmployeeId());
        }
        if (updateUserRequest.getDepartment() != null) {
            user.setDepartment(updateUserRequest.getDepartment());
        }
        if (updateUserRequest.getPosition() != null) {
            user.setPosition(updateUserRequest.getPosition());
        }

        userMapper.updateById(user);

        logger.info("用户信息更新成功: {}", userId);

        return convertToUserResponse(user);
    }

    @Override
    public UserResponse getUserById(Long userId) {
        User user = getUserEntityById(userId);
        return convertToUserResponse(user);
    }

    @Override
    public UserResponse getUserByUsername(String username) {
        User user = userMapper.findByUsernameOptional(username)
            .orElseThrow(() -> new BusinessException("2001", "用户不存在"));
        return convertToUserResponse(user);
    }

    @Override
    public List<UserResponse> getAllUsers() {
        List<User> users = userMapper.selectList(null);
        return users.stream()
            .map(this::convertToUserResponse)
            .collect(Collectors.toList());
    }

    @Override
    public List<UserResponse> getUsersByEnabled(Boolean enabled) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("enabled", enabled);
        
        List<User> users = userMapper.selectList(queryWrapper);
        return users.stream()
            .map(this::convertToUserResponse)
            .collect(Collectors.toList());
    }

    @Override
    public void disableUser(Long userId) {
        logger.info("禁用用户: {}", userId);
        
        User user = getUserEntityById(userId);
        user.setEnabled(false);
        userMapper.updateById(user);
        
        logger.info("用户禁用成功: {}", userId);
    }

    @Override
    public void enableUser(Long userId) {
        logger.info("启用用户: {}", userId);
        
        User user = getUserEntityById(userId);
        user.setEnabled(true);
        user.setAccountNonLocked(true);
        user.resetFailedLoginAttempts();
        userMapper.updateById(user);
        
        logger.info("用户启用成功: {}", userId);
    }

    @Override
    public void deleteUser(Long userId) {
        logger.info("删除用户: {}", userId);
        
        User user = getUserEntityById(userId);
        
        // 逻辑删除
        userMapper.deleteById(userId);
        
        logger.info("用户删除成功: {}", userId);
    }

    @Override
    public void assignRoleToUser(Long userId, Long roleId) {
        logger.info("为用户分配角色: userId={}, roleId={}", userId, roleId);
        
        // 验证用户存在
        getUserEntityById(userId);
        
        // 使用角色管理服务分配角色
        roleManagementService.assignRoleToUser(userId, roleId);
        
        logger.info("用户角色分配成功: userId={}, roleId={}", userId, roleId);
    }

    @Override
    public void removeRoleFromUser(Long userId, Long roleId) {
        logger.info("移除用户角色: userId={}, roleId={}", userId, roleId);
        
        // 验证用户存在
        getUserEntityById(userId);
        
        // 使用角色管理服务移除角色
        roleManagementService.removeRoleFromUser(userId, roleId);
        
        logger.info("用户角色移除成功: userId={}, roleId={}", userId, roleId);
    }

    @Override
    public void resetUserPassword(Long userId, String newPassword) {
        logger.info("重置用户密码: {}", userId);
        
        User user = getUserEntityById(userId);
        
        // 验证新密码复杂度
        if (!passwordService.validatePasswordComplexity(newPassword)) {
            throw new ValidationException("2003", "新密码不符合复杂度要求");
        }
        
        // 更新密码
        user.setPassword(passwordService.encodePassword(newPassword));
        user.setPasswordChangedTime(LocalDateTime.now());
        user.setCredentialsNonExpired(true);
        
        userMapper.updateById(user);
        
        logger.info("用户密码重置成功: {}", userId);
    }

    @Override
    public boolean existsByUsername(String username) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", username);
        return userMapper.selectCount(queryWrapper) > 0;
    }

    @Override
    public boolean existsByEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("email", email);
        return userMapper.selectCount(queryWrapper) > 0;
    }

    @Override
    public List<UserResponse> getUsersByDepartment(String department) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("department", department);
        
        List<User> users = userMapper.selectList(queryWrapper);
        return users.stream()
            .map(this::convertToUserResponse)
            .collect(Collectors.toList());
    }

    @Override
    public List<UserResponse> searchUsers(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllUsers();
        }
        
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.and(wrapper -> wrapper
            .like("username", keyword)
            .or()
            .like("full_name", keyword)
            .or()
            .like("email", keyword)
        );
        
        List<User> users = userMapper.selectList(queryWrapper);
        return users.stream()
            .map(this::convertToUserResponse)
            .collect(Collectors.toList());
    }

    /**
     * 检查员工工号是否存在
     */
    private boolean existsByEmployeeId(String employeeId) {
        if (employeeId == null || employeeId.trim().isEmpty()) {
            return false;
        }
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("employee_id", employeeId);
        return userMapper.selectCount(queryWrapper) > 0;
    }

    /**
     * 根据ID获取用户实体
     */
    private User getUserEntityById(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("2001", "用户不存在");
        }
        return user;
    }

    /**
     * 将用户实体转换为响应DTO
     */
    private UserResponse convertToUserResponse(User user) {
        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setUsername(user.getUsername());
        response.setFullName(user.getFullName());
        response.setEmail(user.getEmail());
        response.setPhone(user.getPhone());
        response.setHireDate(user.getHireDate());
        response.setEmployeeId(user.getEmployeeId());
        response.setDepartment(user.getDepartment());
        response.setPosition(user.getPosition());
        response.setEnabled(user.getEnabled());
        response.setAccountNonExpired(user.getAccountNonExpired());
        response.setAccountNonLocked(user.getAccountNonLocked());
        response.setCredentialsNonExpired(user.getCredentialsNonExpired());
        response.setLastLoginTime(user.getLastLoginTime());
        response.setLastLoginIp(user.getLastLoginIp());
        response.setPasswordChangedTime(user.getPasswordChangedTime());
        response.setFailedLoginAttempts(user.getFailedLoginAttempts());
        response.setLockedTime(user.getLockedTime());
        response.setCreatedAt(user.getCreatedAt());
        response.setUpdatedAt(user.getUpdatedAt());
        
        // 获取用户角色
        try {
            List<Role> userRoles = roleManagementService.getUserRoles(user.getId());
            response.setRoles(userRoles.stream().collect(Collectors.toSet()));
        } catch (Exception e) {
            logger.warn("获取用户角色失败: userId={}", user.getId(), e);
        }
        
        return response;
    }
}