-- V10__Add_role_permissions_to_admin.sql
-- 为admin用户的角色添加角色管理相关权限

-- 插入角色相关权限（如果不存在）
INSERT IGNORE INTO permissions (permission_code, permission_name, description, module, enabled, created_at, updated_at, deleted)
VALUES 
    ('ROLE_VIEW', '查看角色', '查看角色列表和详细信息', 'ROLE_MANAGEMENT', 1, NOW(), NOW(), 0),
    ('ROLE_CREATE', '创建角色', '创建新角色', 'ROLE_MANAGEMENT', 1, NOW(), NOW(), 0),
    ('ROLE_UPDATE', '更新角色', '更新角色信息和权限', 'ROLE_MANAGEMENT', 1, NOW(), NOW(), 0),
    ('ROLE_DELETE', '删除角色', '删除角色', 'ROLE_MANAGEMENT', 1, NOW(), NOW(), 0);

-- 为ADMIN角色分配角色管理权限
INSERT IGNORE INTO role_permissions (role_id, permission_id, created_at, updated_at, deleted)
SELECT r.id, p.id, NOW(), NOW(), 0
FROM roles r 
CROSS JOIN permissions p
WHERE r.role_code = 'ADMIN' 
AND p.permission_code IN ('ROLE_VIEW', 'ROLE_CREATE', 'ROLE_UPDATE', 'ROLE_DELETE')
AND r.deleted = 0 
AND p.deleted = 0;

-- 为SUPER_ADMIN角色分配角色管理权限（如果存在）
INSERT IGNORE INTO role_permissions (role_id, permission_id, created_at, updated_at, deleted)
SELECT r.id, p.id, NOW(), NOW(), 0
FROM roles r 
CROSS JOIN permissions p
WHERE r.role_code = 'SUPER_ADMIN' 
AND p.permission_code IN ('ROLE_VIEW', 'ROLE_CREATE', 'ROLE_UPDATE', 'ROLE_DELETE')
AND r.deleted = 0 
AND p.deleted = 0;