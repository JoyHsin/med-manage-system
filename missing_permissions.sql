-- 添加缺失的API权限数据

-- 生命体征相关权限
INSERT IGNORE INTO permissions (permission_code, permission_name, description, module, enabled, created_at, updated_at) VALUES
('VITAL_SIGNS_VIEW', '查看生命体征', '查看患者生命体征记录的权限', 'VITAL_SIGNS', TRUE, NOW(), NOW()),
('VITAL_SIGNS_RECORD', '记录生命体征', '记录患者生命体征的权限', 'VITAL_SIGNS', TRUE, NOW(), NOW()),
('VITAL_SIGNS_UPDATE', '更新生命体征', '更新患者生命体征记录的权限', 'VITAL_SIGNS', TRUE, NOW(), NOW());

-- 分诊管理相关权限
INSERT IGNORE INTO permissions (permission_code, permission_name, description, module, enabled, created_at, updated_at) VALUES
('TRIAGE_MANAGEMENT', '分诊管理', '管理患者分诊队列的权限', 'TRIAGE', TRUE, NOW(), NOW());

-- 药房管理相关权限
INSERT IGNORE INTO permissions (permission_code, permission_name, description, module, enabled, created_at, updated_at) VALUES
('PHARMACY_MANAGEMENT', '药房管理', '管理药品库存和药房操作的权限', 'PHARMACY', TRUE, NOW(), NOW());

-- 计费管理相关权限
INSERT IGNORE INTO permissions (permission_code, permission_name, description, module, enabled, created_at, updated_at) VALUES
('BILLING_VIEW', '查看账单', '查看患者账单信息的权限', 'BILLING', TRUE, NOW(), NOW()),
('BILLING_CREATE', '创建账单', '创建患者账单的权限', 'BILLING', TRUE, NOW(), NOW()),
('BILLING_UPDATE', '更新账单', '更新账单信息和状态的权限', 'BILLING', TRUE, NOW(), NOW());

-- 为超级管理员角色分配所有新权限
INSERT IGNORE INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id 
FROM roles r, permissions p 
WHERE r.role_code = 'SUPER_ADMIN' 
AND p.permission_code IN (
    'VITAL_SIGNS_VIEW', 'VITAL_SIGNS_RECORD', 'VITAL_SIGNS_UPDATE',
    'TRIAGE_MANAGEMENT', 'PHARMACY_MANAGEMENT',
    'BILLING_VIEW', 'BILLING_CREATE', 'BILLING_UPDATE'
);

-- 为医生角色分配医疗相关权限
INSERT IGNORE INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id 
FROM roles r, permissions p 
WHERE r.role_code = 'DOCTOR' 
AND p.permission_code IN (
    'VITAL_SIGNS_VIEW', 'VITAL_SIGNS_RECORD', 'VITAL_SIGNS_UPDATE',
    'TRIAGE_MANAGEMENT', 'BILLING_VIEW'
);

-- 为护士角色分配护理相关权限
INSERT IGNORE INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id 
FROM roles r, permissions p 
WHERE r.role_code = 'NURSE' 
AND p.permission_code IN (
    'VITAL_SIGNS_VIEW', 'VITAL_SIGNS_RECORD', 'VITAL_SIGNS_UPDATE', 
    'TRIAGE_MANAGEMENT'
);

-- 为药剂师角色分配药房相关权限
INSERT IGNORE INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id 
FROM roles r, permissions p 
WHERE r.role_code = 'PHARMACIST' 
AND p.permission_code IN (
    'PHARMACY_MANAGEMENT'
);

-- 为前台文员角色分配基础权限
INSERT IGNORE INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id 
FROM roles r, permissions p 
WHERE r.role_code = 'RECEPTIONIST' 
AND p.permission_code IN (
    'TRIAGE_MANAGEMENT', 'BILLING_VIEW', 'BILLING_CREATE', 'BILLING_UPDATE'
);

-- 为管理员角色分配额外权限
INSERT IGNORE INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id 
FROM roles r, permissions p 
WHERE r.role_code = 'ADMIN' 
AND p.permission_code IN (
    'VITAL_SIGNS_VIEW', 'TRIAGE_MANAGEMENT',
    'PHARMACY_MANAGEMENT', 'BILLING_VIEW', 'BILLING_CREATE', 'BILLING_UPDATE'
);