-- V15__Add_testuser001_permissions.sql
-- 为 testuser001 添加用户管理相关权限

-- 首先确保权限存在
INSERT IGNORE INTO permissions (permission_code, permission_name, description, module, enabled, created_at, updated_at, deleted) VALUES
('USER_VIEW', '查看用户', '查看用户列表和详情', 'USER_MANAGEMENT', 1, NOW(), NOW(), 0),
('USER_CREATE', '创建用户', '创建新用户', 'USER_MANAGEMENT', 1, NOW(), NOW(), 0),
('USER_UPDATE', '更新用户', '修改用户信息', 'USER_MANAGEMENT', 1, NOW(), NOW(), 0),
('USER_DELETE', '删除用户', '删除用户', 'USER_MANAGEMENT', 1, NOW(), NOW(), 0),
('ROLE_VIEW', '查看角色', '查看角色列表', 'ROLE_MANAGEMENT', 1, NOW(), NOW(), 0);

-- 为 testuser001 分配管理员角色 (role_id = 2)
INSERT IGNORE INTO user_roles (user_id, role_id) VALUES (2, 2);

-- 为管理员角色添加权限
INSERT IGNORE INTO role_permissions (role_id, permission_id)
SELECT 2, p.id
FROM permissions p 
WHERE p.permission_code IN ('USER_VIEW', 'USER_CREATE', 'USER_UPDATE', 'USER_DELETE', 'ROLE_VIEW') 
AND p.deleted = 0;