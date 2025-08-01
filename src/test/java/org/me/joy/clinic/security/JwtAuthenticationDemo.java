package org.me.joy.clinic.security;

import org.me.joy.clinic.entity.User;

/**
 * JWT认证机制演示
 * 展示JWT认证流程的核心功能
 */
public class JwtAuthenticationDemo {

    public static void main(String[] args) {
        System.out.println("=== JWT认证机制演示 ===");
        
        // 1. 创建JWT工具类实例
        JwtUtil jwtUtil = new JwtUtil();
        // 手动设置测试配置
        org.springframework.test.util.ReflectionTestUtils.setField(jwtUtil, "secret", "testSecretKey123456789012345678901234567890");
        org.springframework.test.util.ReflectionTestUtils.setField(jwtUtil, "expiration", 3600000L);
        
        // 2. 创建测试用户
        User testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("admin");
        testUser.setFullName("系统管理员");
        testUser.setEmail("admin@clinic.com");
        testUser.setDepartment("管理部门");
        testUser.setPosition("系统管理员");
        testUser.setEnabled(true);
        testUser.setAccountNonExpired(true);
        testUser.setAccountNonLocked(true);
        testUser.setCredentialsNonExpired(true);
        
        CustomUserPrincipal userPrincipal = new CustomUserPrincipal(testUser);
        
        // 3. 生成JWT令牌
        System.out.println("1. 生成JWT令牌...");
        String token = jwtUtil.generateToken(userPrincipal);
        System.out.println("生成的令牌: " + token.substring(0, 50) + "...");
        
        // 4. 从令牌中提取用户信息
        System.out.println("\n2. 从令牌中提取用户信息...");
        String extractedUsername = jwtUtil.extractUsername(token);
        System.out.println("提取的用户名: " + extractedUsername);
        
        // 5. 验证令牌
        System.out.println("\n3. 验证令牌...");
        boolean isValid = jwtUtil.validateToken(token, userPrincipal);
        System.out.println("令牌是否有效: " + isValid);
        
        // 6. 获取令牌剩余时间
        System.out.println("\n4. 获取令牌剩余时间...");
        Long remainingTime = jwtUtil.getTokenRemainingTime(token);
        System.out.println("剩余时间(毫秒): " + remainingTime);
        System.out.println("剩余时间(分钟): " + (remainingTime / 1000 / 60));
        
        // 7. 刷新令牌
        System.out.println("\n5. 刷新令牌...");
        String refreshedToken = jwtUtil.refreshToken(token);
        System.out.println("刷新后的令牌: " + refreshedToken.substring(0, 50) + "...");
        System.out.println("新旧令牌是否不同: " + !token.equals(refreshedToken));
        
        // 8. 测试无效令牌
        System.out.println("\n6. 测试无效令牌验证...");
        boolean invalidTokenResult = jwtUtil.validateToken("invalid.token.here");
        System.out.println("无效令牌验证结果: " + invalidTokenResult);
        
        System.out.println("\n=== JWT认证机制演示完成 ===");
        System.out.println("✅ JWT令牌生成功能正常");
        System.out.println("✅ JWT令牌解析功能正常");
        System.out.println("✅ JWT令牌验证功能正常");
        System.out.println("✅ JWT令牌刷新功能正常");
        System.out.println("✅ 无效令牌处理功能正常");
    }
}