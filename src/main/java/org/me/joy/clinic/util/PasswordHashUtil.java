package org.me.joy.clinic.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * 密码哈希工具
 */
public class PasswordHashUtil {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String password = "admin123";
        
        // 测试现有的哈希
        String existingHash = "$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi6";
        System.out.println("测试现有哈希:");
        System.out.println("密码: " + password);
        System.out.println("哈希: " + existingHash);
        System.out.println("验证: " + encoder.matches(password, existingHash));
        
        System.out.println();
        
        // 生成新的哈希
        String newHash = encoder.encode(password);
        System.out.println("生成新哈希:");
        System.out.println("密码: " + password);
        System.out.println("哈希: " + newHash);
        System.out.println("验证: " + encoder.matches(password, newHash));
        
        // 测试其他可能的密码
        String[] testPasswords = {"admin", "password", "123456"};
        System.out.println("\n测试其他密码:");
        for (String testPwd : testPasswords) {
            System.out.println(testPwd + " -> " + encoder.matches(testPwd, existingHash));
        }
    }
}