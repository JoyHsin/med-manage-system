-- 创建医护人员表
CREATE TABLE staff (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    staff_number VARCHAR(50) NOT NULL UNIQUE COMMENT '员工编号',
    name VARCHAR(100) NOT NULL COMMENT '员工姓名',
    phone VARCHAR(20) COMMENT '手机号码',
    id_card VARCHAR(18) COMMENT '身份证号码',
    birth_date DATE COMMENT '出生日期',
    gender VARCHAR(10) NOT NULL COMMENT '性别',
    email VARCHAR(100) COMMENT '邮箱地址',
    address VARCHAR(500) COMMENT '联系地址',
    emergency_contact_name VARCHAR(100) COMMENT '紧急联系人姓名',
    emergency_contact_phone VARCHAR(20) COMMENT '紧急联系人电话',
    emergency_contact_relation VARCHAR(50) COMMENT '紧急联系人关系',
    position VARCHAR(20) NOT NULL COMMENT '职位类型',
    department VARCHAR(100) COMMENT '科室',
    title VARCHAR(100) COMMENT '职称',
    specialization VARCHAR(500) COMMENT '专业特长',
    education VARCHAR(20) COMMENT '学历',
    graduate_school VARCHAR(200) COMMENT '毕业院校',
    license_number VARCHAR(100) COMMENT '执业证书编号',
    license_expiry_date DATE COMMENT '执业证书有效期',
    hire_date DATE NOT NULL COMMENT '入职日期',
    resignation_date DATE COMMENT '离职日期',
    work_status VARCHAR(20) NOT NULL DEFAULT '在职' COMMENT '工作状态',
    base_salary DECIMAL(10,2) COMMENT '基本工资',
    experience_years INT COMMENT '工作经验年数',
    remarks TEXT COMMENT '备注信息',
    is_schedulable BOOLEAN NOT NULL DEFAULT TRUE COMMENT '是否可排班',
    last_work_date DATE COMMENT '最后工作日期',
    user_id BIGINT COMMENT '关联的用户ID',
    created_at DATETIME NOT NULL COMMENT '创建时间',
    updated_at DATETIME NOT NULL COMMENT '更新时间',
    deleted INT NOT NULL DEFAULT 0 COMMENT '逻辑删除标识',
    INDEX idx_staff_number (staff_number),
    INDEX idx_name (name),
    INDEX idx_phone (phone),
    INDEX idx_id_card (id_card),
    INDEX idx_position (position),
    INDEX idx_department (department),
    INDEX idx_work_status (work_status),
    INDEX idx_hire_date (hire_date),
    INDEX idx_is_schedulable (is_schedulable),
    INDEX idx_user_id (user_id),
    INDEX idx_license_expiry_date (license_expiry_date),
    INDEX idx_deleted (deleted),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='医护人员表';

-- 创建排班表
CREATE TABLE schedules (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    staff_id BIGINT NOT NULL COMMENT '员工ID',
    schedule_date DATE NOT NULL COMMENT '排班日期',
    shift_type VARCHAR(20) NOT NULL COMMENT '班次类型',
    start_time TIME COMMENT '开始时间',
    end_time TIME COMMENT '结束时间',
    work_location VARCHAR(100) COMMENT '工作地点',
    department VARCHAR(100) COMMENT '科室',
    status VARCHAR(20) NOT NULL DEFAULT '正常' COMMENT '排班状态',
    is_overtime BOOLEAN NOT NULL DEFAULT FALSE COMMENT '是否为加班',
    remarks VARCHAR(500) COMMENT '备注信息',
    created_by BIGINT COMMENT '创建人员ID',
    last_updated_by BIGINT COMMENT '最后更新人员ID',
    created_at DATETIME NOT NULL COMMENT '创建时间',
    updated_at DATETIME NOT NULL COMMENT '更新时间',
    deleted INT NOT NULL DEFAULT 0 COMMENT '逻辑删除标识',
    INDEX idx_staff_id (staff_id),
    INDEX idx_schedule_date (schedule_date),
    INDEX idx_shift_type (shift_type),
    INDEX idx_department (department),
    INDEX idx_status (status),
    INDEX idx_is_overtime (is_overtime),
    INDEX idx_created_by (created_by),
    INDEX idx_deleted (deleted),
    UNIQUE KEY uk_staff_date (staff_id, schedule_date, deleted),
    FOREIGN KEY (staff_id) REFERENCES staff(id) ON DELETE CASCADE,
    FOREIGN KEY (created_by) REFERENCES users(id) ON DELETE SET NULL,
    FOREIGN KEY (last_updated_by) REFERENCES users(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='排班表';

-- 插入医护人员管理相关权限
INSERT INTO permissions (permission_code, permission_name, description, module, enabled, created_at, updated_at) VALUES
('STAFF_VIEW', '查看员工', '查看员工信息的权限', 'STAFF_MANAGEMENT', TRUE, NOW(), NOW()),
('STAFF_CREATE', '创建员工', '创建新员工的权限', 'STAFF_MANAGEMENT', TRUE, NOW(), NOW()),
('STAFF_UPDATE', '更新员工', '更新员工信息的权限', 'STAFF_MANAGEMENT', TRUE, NOW(), NOW()),
('STAFF_DELETE', '删除员工', '删除员工的权限', 'STAFF_MANAGEMENT', TRUE, NOW(), NOW()),
('SCHEDULE_VIEW', '查看排班', '查看排班信息的权限', 'SCHEDULE_MANAGEMENT', TRUE, NOW(), NOW()),
('SCHEDULE_CREATE', '创建排班', '创建排班的权限', 'SCHEDULE_MANAGEMENT', TRUE, NOW(), NOW()),
('SCHEDULE_UPDATE', '更新排班', '更新排班信息的权限', 'SCHEDULE_MANAGEMENT', TRUE, NOW(), NOW()),
('SCHEDULE_DELETE', '删除排班', '删除排班的权限', 'SCHEDULE_MANAGEMENT', TRUE, NOW(), NOW());

-- 为超级管理员角色分配医护人员管理权限
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id 
FROM roles r, permissions p 
WHERE r.role_code = 'SUPER_ADMIN' 
AND p.permission_code IN ('STAFF_VIEW', 'STAFF_CREATE', 'STAFF_UPDATE', 'STAFF_DELETE', 
                         'SCHEDULE_VIEW', 'SCHEDULE_CREATE', 'SCHEDULE_UPDATE', 'SCHEDULE_DELETE');

-- 为管理员角色分配医护人员查看和排班管理权限
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id 
FROM roles r, permissions p 
WHERE r.role_code = 'ADMIN' 
AND p.permission_code IN ('STAFF_VIEW', 'STAFF_UPDATE', 
                         'SCHEDULE_VIEW', 'SCHEDULE_CREATE', 'SCHEDULE_UPDATE', 'SCHEDULE_DELETE');

-- 为医生角色分配员工查看和排班查看权限
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id 
FROM roles r, permissions p 
WHERE r.role_code = 'DOCTOR' 
AND p.permission_code IN ('STAFF_VIEW', 'SCHEDULE_VIEW');

-- 为护士角色分配员工查看和排班查看权限
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id 
FROM roles r, permissions p 
WHERE r.role_code = 'NURSE' 
AND p.permission_code IN ('STAFF_VIEW', 'SCHEDULE_VIEW');

-- 为前台文员角色分配员工查看和排班查看权限
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id 
FROM roles r, permissions p 
WHERE r.role_code = 'RECEPTIONIST' 
AND p.permission_code IN ('STAFF_VIEW', 'SCHEDULE_VIEW');