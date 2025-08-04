-- 添加财务报表相关权限
INSERT INTO permissions (name, description, module) VALUES
('FINANCIAL_REPORT_READ', '查看财务报表', 'FINANCIAL_REPORT'),
('FINANCIAL_REPORT_EXPORT', '导出财务报表', 'FINANCIAL_REPORT');

-- 为超级管理员角色添加财务报表权限
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r, permissions p
WHERE r.name = 'SUPER_ADMIN'
AND p.name IN ('FINANCIAL_REPORT_READ', 'FINANCIAL_REPORT_EXPORT');

-- 为管理员角色添加财务报表查看权限
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r, permissions p
WHERE r.name = 'ADMIN'
AND p.name = 'FINANCIAL_REPORT_READ';