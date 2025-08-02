-- 重置admin用户密码为admin123
UPDATE users 
SET password = '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi6',
    failed_login_attempts = 0,
    updated_at = NOW()
WHERE username = 'admin' AND deleted = 0;