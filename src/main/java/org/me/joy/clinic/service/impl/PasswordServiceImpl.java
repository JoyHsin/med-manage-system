package org.me.joy.clinic.service.impl;

import org.me.joy.clinic.dto.ChangePasswordRequest;
import org.me.joy.clinic.entity.User;
import org.me.joy.clinic.exception.BusinessException;
import org.me.joy.clinic.exception.ValidationException;
import org.me.joy.clinic.mapper.UserMapper;
import org.me.joy.clinic.service.PasswordService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.regex.Pattern;

/**
 * 密码管理服务实现类
 */
@Service
@Transactional
public class PasswordServiceImpl implements PasswordService {

    private static final Logger logger = LoggerFactory.getLogger(PasswordServiceImpl.class);

    /**
     * 密码复杂度正则表达式
     * 至少8位，包含大小写字母、数字和特殊字符
     */
    private static final String PASSWORD_PATTERN = 
        "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$";

    private static final Pattern pattern = Pattern.compile(PASSWORD_PATTERN);

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void changePassword(String username, ChangePasswordRequest changePasswordRequest) {
        logger.info("用户 {} 尝试修改密码", username);

        // 验证请求参数
        validateChangePasswordRequest(changePasswordRequest);

        // 查找用户
        User user = userMapper.findByUsernameOptional(username)
            .orElseThrow(() -> new BusinessException("2001", "用户不存在"));

        // 验证当前密码
        if (!validateCurrentPassword(username, changePasswordRequest.getCurrentPassword())) {
            logger.warn("用户 {} 修改密码时当前密码验证失败", username);
            throw new BusinessException("2002", "当前密码不正确");
        }

        // 验证新密码复杂度
        if (!validatePasswordComplexity(changePasswordRequest.getNewPassword())) {
            throw new ValidationException("2003", "新密码不符合复杂度要求");
        }

        // 验证新密码与当前密码不同
        if (matches(changePasswordRequest.getNewPassword(), user.getPassword())) {
            throw new ValidationException("2004", "新密码不能与当前密码相同");
        }

        // 验证新密码与确认密码一致
        if (!changePasswordRequest.getNewPassword().equals(changePasswordRequest.getConfirmPassword())) {
            throw new ValidationException("2005", "新密码与确认密码不一致");
        }

        // 更新密码
        String encodedPassword = encodePassword(changePasswordRequest.getNewPassword());
        user.setPassword(encodedPassword);
        user.setPasswordChangedTime(LocalDateTime.now());
        
        // 重置密码过期状态
        user.setCredentialsNonExpired(true);

        userMapper.updateById(user);

        logger.info("用户 {} 密码修改成功", username);
    }

    @Override
    public boolean validatePasswordComplexity(String password) {
        if (password == null || password.trim().isEmpty()) {
            return false;
        }
        
        // 检查长度
        if (password.length() < 8 || password.length() > 50) {
            return false;
        }
        
        // 检查复杂度
        return pattern.matcher(password).matches();
    }

    @Override
    public boolean validateCurrentPassword(String username, String currentPassword) {
        try {
            return userMapper.findByUsernameOptional(username)
                .map(user -> matches(currentPassword, user.getPassword()))
                .orElse(false);
        } catch (Exception e) {
            logger.error("验证当前密码时发生错误", e);
            return false;
        }
    }

    @Override
    public String encodePassword(String rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }

    @Override
    public boolean matches(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }

    /**
     * 验证修改密码请求参数
     */
    private void validateChangePasswordRequest(ChangePasswordRequest request) {
        if (request == null) {
            throw new ValidationException("2006", "修改密码请求不能为空");
        }

        if (request.getCurrentPassword() == null || request.getCurrentPassword().trim().isEmpty()) {
            throw new ValidationException("2007", "当前密码不能为空");
        }

        if (request.getNewPassword() == null || request.getNewPassword().trim().isEmpty()) {
            throw new ValidationException("2008", "新密码不能为空");
        }

        if (request.getConfirmPassword() == null || request.getConfirmPassword().trim().isEmpty()) {
            throw new ValidationException("2009", "确认密码不能为空");
        }
    }
}