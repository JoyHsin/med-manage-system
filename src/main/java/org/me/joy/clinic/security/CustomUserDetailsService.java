package org.me.joy.clinic.security;

import org.me.joy.clinic.entity.User;
import org.me.joy.clinic.mapper.PermissionMapper;
import org.me.joy.clinic.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 自定义用户详情服务
 * 实现Spring Security的UserDetailsService接口
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PermissionMapper permissionMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userMapper.findByUsername(username);
        if (user == null || !user.getEnabled()) {
            throw new UsernameNotFoundException("用户不存在: " + username);
        }
        
        // 加载用户权限
        List<String> permissions = permissionMapper.findPermissionsByUserId(user.getId())
                .stream()
                .map(permission -> permission.getPermissionCode())
                .collect(Collectors.toList());
        
        return new CustomUserPrincipal(user, permissions);
    }
}