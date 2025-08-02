-- 重新设置admin用户密码为admin123（使用正确的哈希）
UPDATE users 
SET password = '$2a$10$sBnVfcrUZJ4QoDbl.bqkM.TqDWy8MW.i5FU.vgnIZwnwapgdkHynC',
    failed_login_attempts = 0,
    updated_at = NOW()
WHERE username = 'admin' AND deleted = 0;