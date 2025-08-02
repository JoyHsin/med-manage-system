-- V18__Add_patient_management_permissions.sql
-- 添加患者管理相关权限

-- 添加患者管理权限
INSERT IGNORE INTO permissions (permission_code, permission_name, description, module, enabled, created_at, updated_at, deleted) VALUES
('PATIENT_VIEW', '查看患者', '查看患者档案信息', 'PATIENT_MANAGEMENT', 1, NOW(), NOW(), 0),
('PATIENT_CREATE', '创建患者', '创建新患者档案', 'PATIENT_MANAGEMENT', 1, NOW(), NOW(), 0),
('PATIENT_UPDATE', '更新患者', '修改患者档案信息', 'PATIENT_MANAGEMENT', 1, NOW(), NOW(), 0),
('PATIENT_DELETE', '删除患者', '删除患者档案', 'PATIENT_MANAGEMENT', 1, NOW(), NOW(), 0);

-- 为管理员角色添加患者管理权限
INSERT IGNORE INTO role_permissions (role_id, permission_id)
SELECT 2, p.id
FROM permissions p 
WHERE p.permission_code IN ('PATIENT_VIEW', 'PATIENT_CREATE', 'PATIENT_UPDATE', 'PATIENT_DELETE') 
AND p.deleted = 0;