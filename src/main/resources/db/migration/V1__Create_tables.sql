-- 创建权限表
CREATE TABLE permissions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    permission_code VARCHAR(100) NOT NULL UNIQUE COMMENT '权限代码',
    permission_name VARCHAR(100) NOT NULL COMMENT '权限名称',
    description VARCHAR(500) COMMENT '权限描述',
    module VARCHAR(50) COMMENT '权限模块',
    enabled BOOLEAN NOT NULL DEFAULT TRUE COMMENT '是否启用',
    created_at DATETIME NOT NULL COMMENT '创建时间',
    updated_at DATETIME NOT NULL COMMENT '更新时间',
    deleted INT NOT NULL DEFAULT 0 COMMENT '逻辑删除标识',
    INDEX idx_permission_code (permission_code),
    INDEX idx_module (module),
    INDEX idx_enabled (enabled),
    INDEX idx_deleted (deleted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='权限表';

-- 创建角色表
CREATE TABLE roles (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    role_code VARCHAR(50) NOT NULL UNIQUE COMMENT '角色代码',
    role_name VARCHAR(100) NOT NULL COMMENT '角色名称',
    description VARCHAR(500) COMMENT '角色描述',
    enabled BOOLEAN NOT NULL DEFAULT TRUE COMMENT '是否启用',
    is_system_role BOOLEAN NOT NULL DEFAULT FALSE COMMENT '是否为系统预设角色',
    created_at DATETIME NOT NULL COMMENT '创建时间',
    updated_at DATETIME NOT NULL COMMENT '更新时间',
    deleted INT NOT NULL DEFAULT 0 COMMENT '逻辑删除标识',
    INDEX idx_role_code (role_code),
    INDEX idx_enabled (enabled),
    INDEX idx_system_role (is_system_role),
    INDEX idx_deleted (deleted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色表';

-- 创建用户表
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    password VARCHAR(255) NOT NULL COMMENT '密码',
    full_name VARCHAR(100) NOT NULL COMMENT '员工姓名',
    email VARCHAR(100) COMMENT '邮箱地址',
    phone VARCHAR(20) COMMENT '手机号码',
    hire_date DATE COMMENT '入职日期',
    employee_id VARCHAR(50) UNIQUE COMMENT '员工工号',
    department VARCHAR(100) COMMENT '部门',
    position VARCHAR(100) COMMENT '职位',
    enabled BOOLEAN NOT NULL DEFAULT TRUE COMMENT '账号是否启用',
    account_non_expired BOOLEAN NOT NULL DEFAULT TRUE COMMENT '账号是否未过期',
    account_non_locked BOOLEAN NOT NULL DEFAULT TRUE COMMENT '账号是否未锁定',
    credentials_non_expired BOOLEAN NOT NULL DEFAULT TRUE COMMENT '密码是否未过期',
    last_login_time DATETIME COMMENT '最后登录时间',
    last_login_ip VARCHAR(50) COMMENT '最后登录IP',
    password_changed_time DATETIME COMMENT '密码最后修改时间',
    failed_login_attempts INT NOT NULL DEFAULT 0 COMMENT '登录失败次数',
    locked_time DATETIME COMMENT '账号锁定时间',
    created_at DATETIME NOT NULL COMMENT '创建时间',
    updated_at DATETIME NOT NULL COMMENT '更新时间',
    deleted INT NOT NULL DEFAULT 0 COMMENT '逻辑删除标识',
    INDEX idx_username (username),
    INDEX idx_employee_id (employee_id),
    INDEX idx_email (email),
    INDEX idx_phone (phone),
    INDEX idx_department (department),
    INDEX idx_position (position),
    INDEX idx_enabled (enabled),
    INDEX idx_deleted (deleted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 创建用户角色关联表
CREATE TABLE user_roles (
    user_id BIGINT NOT NULL COMMENT '用户ID',
    role_id BIGINT NOT NULL COMMENT '角色ID',
    PRIMARY KEY (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户角色关联表';

-- 创建角色权限关联表
CREATE TABLE role_permissions (
    role_id BIGINT NOT NULL COMMENT '角色ID',
    permission_id BIGINT NOT NULL COMMENT '权限ID',
    PRIMARY KEY (role_id, permission_id),
    FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE,
    FOREIGN KEY (permission_id) REFERENCES permissions(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色权限关联表';

-- 插入基础权限数据
INSERT INTO permissions (permission_code, permission_name, description, module, enabled, created_at, updated_at) VALUES
('USER_VIEW', '查看用户', '查看用户信息的权限', 'USER_MANAGEMENT', TRUE, NOW(), NOW()),
('USER_CREATE', '创建用户', '创建新用户的权限', 'USER_MANAGEMENT', TRUE, NOW(), NOW()),
('USER_UPDATE', '更新用户', '更新用户信息的权限', 'USER_MANAGEMENT', TRUE, NOW(), NOW()),
('USER_DELETE', '删除用户', '删除用户的权限', 'USER_MANAGEMENT', TRUE, NOW(), NOW()),
('ROLE_VIEW', '查看角色', '查看角色信息的权限', 'ROLE_MANAGEMENT', TRUE, NOW(), NOW()),
('ROLE_CREATE', '创建角色', '创建新角色的权限', 'ROLE_MANAGEMENT', TRUE, NOW(), NOW()),
('ROLE_UPDATE', '更新角色', '更新角色信息的权限', 'ROLE_MANAGEMENT', TRUE, NOW(), NOW()),
('ROLE_DELETE', '删除角色', '删除角色的权限', 'ROLE_MANAGEMENT', TRUE, NOW(), NOW()),
('PERMISSION_VIEW', '查看权限', '查看权限信息的权限', 'PERMISSION_MANAGEMENT', TRUE, NOW(), NOW()),
('SYSTEM_CONFIG', '系统配置', '系统配置管理权限', 'SYSTEM_MANAGEMENT', TRUE, NOW(), NOW());

-- 插入基础角色数据
INSERT INTO roles (role_code, role_name, description, enabled, is_system_role, created_at, updated_at) VALUES
('SUPER_ADMIN', '超级管理员', '系统超级管理员，拥有所有权限', TRUE, TRUE, NOW(), NOW()),
('ADMIN', '管理员', '系统管理员，拥有大部分管理权限', TRUE, TRUE, NOW(), NOW()),
('DOCTOR', '医生', '医生角色，拥有诊疗相关权限', TRUE, TRUE, NOW(), NOW()),
('NURSE', '护士', '护士角色，拥有护理相关权限', TRUE, TRUE, NOW(), NOW()),
('PHARMACIST', '药剂师', '药剂师角色，拥有药品管理权限', TRUE, TRUE, NOW(), NOW()),
('RECEPTIONIST', '前台文员', '前台文员角色，拥有基础操作权限', TRUE, TRUE, NOW(), NOW());

-- 为超级管理员角色分配所有权限
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id 
FROM roles r, permissions p 
WHERE r.role_code = 'SUPER_ADMIN';

-- 为管理员角色分配用户和角色管理权限
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id 
FROM roles r, permissions p 
WHERE r.role_code = 'ADMIN' 
AND p.permission_code IN ('USER_VIEW', 'USER_CREATE', 'USER_UPDATE', 'ROLE_VIEW', 'PERMISSION_VIEW');

-- 创建默认超级管理员用户（密码：admin123，需要在应用中加密）
INSERT INTO users (username, password, full_name, email, department, position, enabled, created_at, updated_at) VALUES
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBaLIU8pjik5Dq', '系统管理员', 'admin@clinic.com', 'IT部门', '系统管理员', TRUE, NOW(), NOW());

-- 为默认管理员分配超级管理员角色
INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id 
FROM users u, roles r 
WHERE u.username = 'admin' AND r.role_code = 'SUPER_ADMIN';