-- V17__Add_role_management_permissions.sql
-- 添加角色管理相关权限

-- 添加角色管理权限
INSERT IGNORE INTO permissions (permission_code, permission_name, description, module, enabled, created_at, updated_at, deleted) VALUES
('ROLE_CREATE', '创建角色', '创建新角色', 'ROLE_MANAGEMENT', 1, NOW(), NOW(), 0),
('ROLE_UPDATE', '更新角色', '修改角色信息', 'ROLE_MANAGEMENT', 1, NOW(), NOW(), 0),
('ROLE_DELETE', '删除角色', '删除角色', 'ROLE_MANAGEMENT', 1, NOW(), NOW(), 0),
('PERMISSION_VIEW', '查看权限', '查看权限列表', 'PERMISSION_MANAGEMENT', 1, NOW(), NOW(), 0),
('PERMISSION_ASSIGN', '分配权限', '为角色分配权限', 'PERMISSION_MANAGEMENT', 1, NOW(), NOW(), 0);

-- 为管理员角色添加新权限
INSERT IGNORE INTO role_permissions (role_id, permission_id)
SELECT 2, p.id
FROM permissions p 
WHERE p.permission_code IN ('ROLE_CREATE', 'ROLE_UPDATE', 'ROLE_DELETE', 'PERMISSION_VIEW', 'PERMISSION_ASSIGN') 
AND p.deleted = 0;