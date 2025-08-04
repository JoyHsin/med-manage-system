package org.me.joy.clinic.security;

import org.me.joy.clinic.entity.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 自定义用户主体
 * 实现Spring Security的UserDetails接口
 */
public class CustomUserPrincipal implements UserDetails {

    private final User user;
    private final List<String> permissions;

    public CustomUserPrincipal(User user) {
        this.user = user;
        this.permissions = Collections.emptyList();
    }

    public CustomUserPrincipal(User user, List<String> permissions) {
        this.user = user;
        this.permissions = permissions != null ? permissions : Collections.emptyList();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return permissions.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return user.getAccountNonExpired() != null ? user.getAccountNonExpired() : true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return user.getAccountNonLocked() != null ? user.getAccountNonLocked() : true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return user.getCredentialsNonExpired() != null ? user.getCredentialsNonExpired() : true;
    }

    @Override
    public boolean isEnabled() {
        return user.getEnabled() != null ? user.getEnabled() : true;
    }

    /**
     * 获取用户实体
     */
    public User getUser() {
        return user;
    }

    /**
     * 获取用户ID
     */
    public Long getUserId() {
        return user.getId();
    }

    /**
     * 获取用户全名
     */
    public String getFullName() {
        return user.getFullName();
    }

    /**
     * 获取用户邮箱
     */
    public String getEmail() {
        return user.getEmail();
    }

    /**
     * 获取用户部门
     */
    public String getDepartment() {
        return user.getDepartment();
    }

    /**
     * 获取用户职位
     */
    public String getPosition() {
        return user.getPosition();
    }
}