-- ========================================
-- 创建预约表
-- ========================================
CREATE TABLE appointments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    patient_id BIGINT NOT NULL COMMENT '患者ID',
    doctor_id BIGINT NOT NULL COMMENT '医生ID',
    appointment_time DATETIME NOT NULL COMMENT '预约时间',
    appointment_type VARCHAR(20) NOT NULL COMMENT '预约类型',
    status VARCHAR(20) NOT NULL DEFAULT '已预约' COMMENT '预约状态',
    department VARCHAR(100) COMMENT '科室',
    chief_complaint VARCHAR(500) COMMENT '主诉',
    notes VARCHAR(1000) COMMENT '预约备注',
    source VARCHAR(20) NOT NULL DEFAULT '现场' COMMENT '预约来源',
    priority INT NOT NULL DEFAULT 3 COMMENT '优先级（1-5）',
    appointment_fee DECIMAL(10,2) COMMENT '预约费用',
    need_reminder BOOLEAN NOT NULL DEFAULT TRUE COMMENT '是否需要提醒',
    reminder_minutes INT NOT NULL DEFAULT 30 COMMENT '提醒时间（预约前多少分钟提醒）',
    confirmed_at DATETIME COMMENT '预约确认时间',
    arrived_at DATETIME COMMENT '到达时间',
    started_at DATETIME COMMENT '开始时间',
    completed_at DATETIME COMMENT '完成时间',
    cancelled_at DATETIME COMMENT '取消时间',
    cancel_reason VARCHAR(500) COMMENT '取消原因',
    created_by BIGINT COMMENT '创建人员ID',
    last_updated_by BIGINT COMMENT '最后更新人员ID',
    created_at DATETIME NOT NULL COMMENT '创建时间',
    updated_at DATETIME NOT NULL COMMENT '更新时间',
    deleted INT NOT NULL DEFAULT 0 COMMENT '逻辑删除标识',
    INDEX idx_patient_id (patient_id),
    INDEX idx_doctor_id (doctor_id),
    INDEX idx_appointment_time (appointment_time),
    INDEX idx_appointment_type (appointment_type),
    INDEX idx_status (status),
    INDEX idx_department (department),
    INDEX idx_source (source),
    INDEX idx_priority (priority),
    INDEX idx_need_reminder (need_reminder),
    INDEX idx_deleted (deleted),
    FOREIGN KEY (patient_id) REFERENCES patients(id),
    FOREIGN KEY (doctor_id) REFERENCES users(id),
    FOREIGN KEY (created_by) REFERENCES users(id),
    FOREIGN KEY (last_updated_by) REFERENCES users(id),
    CONSTRAINT chk_appointment_type CHECK (appointment_type IN ('初诊', '复诊', '专家门诊', '急诊', '体检', '疫苗接种', '其他')),
    CONSTRAINT chk_appointment_status CHECK (status IN ('已预约', '已确认', '已到达', '进行中', '已完成', '已取消', '未到')),
    CONSTRAINT chk_appointment_source CHECK (source IN ('现场', '电话', '网络', '微信', '其他')),
    CONSTRAINT chk_appointment_priority CHECK (priority BETWEEN 1 AND 5)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='预约表';

-- ========================================
-- 创建挂号表
-- ========================================
CREATE TABLE registrations (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    patient_id BIGINT NOT NULL COMMENT '患者ID',
    appointment_id BIGINT COMMENT '预约ID（可选，如果是从预约转来的挂号）',
    registration_number VARCHAR(50) NOT NULL UNIQUE COMMENT '挂号编号（唯一标识）',
    registration_date DATE NOT NULL COMMENT '挂号日期',
    registration_time DATETIME NOT NULL COMMENT '挂号时间',
    department VARCHAR(100) NOT NULL COMMENT '科室',
    doctor_id BIGINT COMMENT '医生ID',
    doctor_name VARCHAR(100) COMMENT '医生姓名',
    registration_type VARCHAR(20) NOT NULL COMMENT '挂号类型',
    status VARCHAR(20) NOT NULL DEFAULT '已挂号' COMMENT '挂号状态',
    queue_number INT COMMENT '队列号',
    priority INT NOT NULL DEFAULT 3 COMMENT '优先级（1-5）',
    registration_fee DECIMAL(10,2) NOT NULL COMMENT '挂号费',
    consultation_fee DECIMAL(10,2) COMMENT '诊疗费',
    total_fee DECIMAL(10,2) COMMENT '总费用',
    payment_status VARCHAR(20) NOT NULL DEFAULT '未支付' COMMENT '支付状态',
    payment_method VARCHAR(20) COMMENT '支付方式',
    chief_complaint VARCHAR(500) COMMENT '主诉',
    present_illness VARCHAR(1000) COMMENT '现病史',
    source VARCHAR(20) NOT NULL DEFAULT '现场' COMMENT '挂号来源',
    is_first_visit BOOLEAN NOT NULL DEFAULT TRUE COMMENT '是否初诊',
    is_emergency BOOLEAN NOT NULL DEFAULT FALSE COMMENT '是否急诊',
    called_at DATETIME COMMENT '叫号时间',
    arrived_at DATETIME COMMENT '到达时间',
    started_at DATETIME COMMENT '开始就诊时间',
    completed_at DATETIME COMMENT '完成时间',
    cancelled_at DATETIME COMMENT '取消时间',
    cancel_reason VARCHAR(500) COMMENT '取消原因',
    remarks VARCHAR(1000) COMMENT '备注信息',
    created_by BIGINT COMMENT '创建人员ID',
    last_updated_by BIGINT COMMENT '最后更新人员ID',
    created_at DATETIME NOT NULL COMMENT '创建时间',
    updated_at DATETIME NOT NULL COMMENT '更新时间',
    deleted INT NOT NULL DEFAULT 0 COMMENT '逻辑删除标识',
    INDEX idx_patient_id (patient_id),
    INDEX idx_appointment_id (appointment_id),
    INDEX idx_registration_number (registration_number),
    INDEX idx_registration_date (registration_date),
    INDEX idx_registration_time (registration_time),
    INDEX idx_department (department),
    INDEX idx_doctor_id (doctor_id),
    INDEX idx_registration_type (registration_type),
    INDEX idx_status (status),
    INDEX idx_queue_number (queue_number),
    INDEX idx_priority (priority),
    INDEX idx_payment_status (payment_status),
    INDEX idx_source (source),
    INDEX idx_is_first_visit (is_first_visit),
    INDEX idx_is_emergency (is_emergency),
    INDEX idx_deleted (deleted),
    FOREIGN KEY (patient_id) REFERENCES patients(id),
    FOREIGN KEY (doctor_id) REFERENCES users(id),
    FOREIGN KEY (appointment_id) REFERENCES appointments(id),
    FOREIGN KEY (created_by) REFERENCES users(id),
    FOREIGN KEY (last_updated_by) REFERENCES users(id),
    CONSTRAINT chk_registration_type CHECK (registration_type IN ('普通门诊', '专家门诊', '急诊', '专科门诊', '体检', '疫苗接种', '其他')),
    CONSTRAINT chk_registration_status CHECK (status IN ('已挂号', '已叫号', '已到达', '就诊中', '已完成', '已取消', '未到')),
    CONSTRAINT chk_registration_priority CHECK (priority BETWEEN 1 AND 5),
    CONSTRAINT chk_registration_payment_status CHECK (payment_status IN ('未支付', '已支付', '部分支付', '已退费')),
    CONSTRAINT chk_registration_payment_method CHECK (payment_method IN ('现金', '银行卡', '支付宝', '微信', '医保', '其他')),
    CONSTRAINT chk_registration_source CHECK (source IN ('现场', '预约', '急诊', '转诊', '其他'))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='挂号表';

-- ========================================
-- 创建药品表
-- ========================================
CREATE TABLE medicines (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    medicine_code VARCHAR(50) NOT NULL UNIQUE COMMENT '药品编码（唯一标识）',
    name VARCHAR(200) NOT NULL COMMENT '药品名称',
    generic_name VARCHAR(200) COMMENT '通用名称',
    brand_name VARCHAR(200) COMMENT '商品名称',
    category VARCHAR(20) NOT NULL COMMENT '药品分类',
    type VARCHAR(100) COMMENT '药品类型',
    dosage_form VARCHAR(50) COMMENT '剂型',
    specification VARCHAR(100) COMMENT '规格',
    unit VARCHAR(20) NOT NULL COMMENT '单位',
    manufacturer VARCHAR(200) COMMENT '生产厂家',
    approval_number VARCHAR(100) COMMENT '批准文号',
    drug_license_number VARCHAR(100) COMMENT '国药准字号',
    purchase_price DECIMAL(10,2) COMMENT '进价',
    selling_price DECIMAL(10,2) COMMENT '售价',
    storage_conditions VARCHAR(200) COMMENT '存储条件',
    indications VARCHAR(1000) COMMENT '适应症',
    contraindications VARCHAR(1000) COMMENT '禁忌症',
    adverse_reactions VARCHAR(1000) COMMENT '不良反应',
    dosage_and_usage VARCHAR(500) COMMENT '用法用量',
    precautions VARCHAR(1000) COMMENT '注意事项',
    drug_interactions VARCHAR(1000) COMMENT '药物相互作用',
    shelf_life_months INT COMMENT '有效期（月）',
    min_stock_level INT NOT NULL DEFAULT 0 COMMENT '最小库存量',
    max_stock_level INT NOT NULL DEFAULT 0 COMMENT '最大库存量',
    safety_stock_level INT NOT NULL DEFAULT 0 COMMENT '安全库存量',
    requires_prescription BOOLEAN NOT NULL DEFAULT FALSE COMMENT '是否需要处方',
    is_controlled_substance BOOLEAN NOT NULL DEFAULT FALSE COMMENT '是否为特殊管制药品',
    enabled BOOLEAN NOT NULL DEFAULT TRUE COMMENT '是否启用',
    remarks VARCHAR(500) COMMENT '备注信息',
    supplier_id BIGINT COMMENT '供应商ID',
    created_by BIGINT COMMENT '创建人员ID',
    last_updated_by BIGINT COMMENT '最后更新人员ID',
    created_at DATETIME NOT NULL COMMENT '创建时间',
    updated_at DATETIME NOT NULL COMMENT '更新时间',
    deleted INT NOT NULL DEFAULT 0 COMMENT '逻辑删除标识',
    INDEX idx_medicine_code (medicine_code),
    INDEX idx_name (name),
    INDEX idx_generic_name (generic_name),
    INDEX idx_category (category),
    INDEX idx_type (type),
    INDEX idx_manufacturer (manufacturer),
    INDEX idx_enabled (enabled),
    INDEX idx_requires_prescription (requires_prescription),
    INDEX idx_is_controlled_substance (is_controlled_substance),
    INDEX idx_deleted (deleted),
    FOREIGN KEY (created_by) REFERENCES users(id),
    FOREIGN KEY (last_updated_by) REFERENCES users(id),
    CONSTRAINT chk_medicine_category CHECK (category IN ('处方药', '非处方药', '中药', '西药', '生物制品', '疫苗', '其他'))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='药品表';

-- ========================================
-- 创建库存水平表
-- ========================================
CREATE TABLE inventory_levels (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    medicine_id BIGINT NOT NULL COMMENT '药品ID',
    batch_number VARCHAR(100) COMMENT '批次号',
    current_stock INT NOT NULL DEFAULT 0 COMMENT '当前库存量',
    available_stock INT NOT NULL DEFAULT 0 COMMENT '可用库存量',
    reserved_stock INT NOT NULL DEFAULT 0 COMMENT '预留库存量',
    expired_stock INT NOT NULL DEFAULT 0 COMMENT '过期库存量',
    damaged_stock INT NOT NULL DEFAULT 0 COMMENT '损坏库存量',
    production_date DATE COMMENT '生产日期',
    expiry_date DATE COMMENT '过期日期',
    purchase_price DECIMAL(10,2) COMMENT '进价',
    selling_price DECIMAL(10,2) COMMENT '售价',
    supplier_id BIGINT COMMENT '供应商ID',
    warehouse_location VARCHAR(100) COMMENT '仓库位置',
    last_updated_at DATETIME NOT NULL COMMENT '最后更新时间',
    created_at DATETIME NOT NULL COMMENT '创建时间',
    updated_at DATETIME NOT NULL COMMENT '更新时间',
    deleted INT NOT NULL DEFAULT 0 COMMENT '逻辑删除标识',
    INDEX idx_medicine_id (medicine_id),
    INDEX idx_batch_number (batch_number),
    INDEX idx_current_stock (current_stock),
    INDEX idx_available_stock (available_stock),
    INDEX idx_expiry_date (expiry_date),
    INDEX idx_warehouse_location (warehouse_location),
    INDEX idx_deleted (deleted),
    FOREIGN KEY (medicine_id) REFERENCES medicines(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='库存水平表';

-- ========================================
-- 创建库存交易表
-- ========================================
CREATE TABLE stock_transactions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    medicine_id BIGINT NOT NULL COMMENT '药品ID',
    transaction_type VARCHAR(20) NOT NULL COMMENT '交易类型',
    quantity INT NOT NULL COMMENT '数量',
    unit_price DECIMAL(10,2) COMMENT '单价',
    total_amount DECIMAL(10,2) COMMENT '总金额',
    batch_number VARCHAR(100) COMMENT '批次号',
    expiry_date DATE COMMENT '过期日期',
    supplier_id BIGINT COMMENT '供应商ID',
    reference_number VARCHAR(100) COMMENT '参考单号',
    reference_type VARCHAR(50) COMMENT '参考类型',
    reference_id BIGINT COMMENT '参考ID',
    warehouse_location VARCHAR(100) COMMENT '仓库位置',
    reason VARCHAR(200) COMMENT '交易原因',
    operator_id BIGINT NOT NULL COMMENT '操作员ID',
    transaction_date DATETIME NOT NULL COMMENT '交易日期',
    remarks VARCHAR(500) COMMENT '备注信息',
    created_at DATETIME NOT NULL COMMENT '创建时间',
    updated_at DATETIME NOT NULL COMMENT '更新时间',
    deleted INT NOT NULL DEFAULT 0 COMMENT '逻辑删除标识',
    INDEX idx_medicine_id (medicine_id),
    INDEX idx_transaction_type (transaction_type),
    INDEX idx_batch_number (batch_number),
    INDEX idx_reference_number (reference_number),
    INDEX idx_reference_type (reference_type),
    INDEX idx_reference_id (reference_id),
    INDEX idx_operator_id (operator_id),
    INDEX idx_transaction_date (transaction_date),
    INDEX idx_deleted (deleted),
    FOREIGN KEY (medicine_id) REFERENCES medicines(id),
    FOREIGN KEY (operator_id) REFERENCES users(id),
    CONSTRAINT chk_stock_transaction_type CHECK (transaction_type IN ('入库', '出库', '调拨', '盘点', '报损', '退货', '其他'))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='库存交易表';

-- ========================================
-- 创建患者队列表
-- ========================================
CREATE TABLE patient_queue (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    patient_id BIGINT NOT NULL COMMENT '患者ID',
    registration_id BIGINT NOT NULL COMMENT '挂号ID',
    queue_date DATE NOT NULL COMMENT '队列日期',
    queue_number INT NOT NULL COMMENT '队列号码',
    status VARCHAR(20) NOT NULL DEFAULT 'WAITING' COMMENT '队列状态：WAITING-等待中, CALLED-已叫号, ARRIVED-已到达, ABSENT-未到, COMPLETED-已完成',
    priority INT NOT NULL DEFAULT 3 COMMENT '优先级 (1-最高, 5-最低)',
    called_at DATETIME COMMENT '叫号时间',
    arrived_at DATETIME COMMENT '到达时间',
    completed_at DATETIME COMMENT '完成时间',
    call_count INT NOT NULL DEFAULT 0 COMMENT '叫号次数',
    notes TEXT COMMENT '备注',
    called_by BIGINT COMMENT '叫号护士ID',
    confirmed_by BIGINT COMMENT '确认到达护士ID',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    created_by BIGINT COMMENT '创建人',
    updated_by BIGINT COMMENT '更新人',
    
    INDEX idx_patient_queue_date (queue_date),
    INDEX idx_patient_queue_status (status),
    INDEX idx_patient_queue_patient_id (patient_id),
    INDEX idx_patient_queue_registration_id (registration_id),
    INDEX idx_patient_queue_priority (priority),
    UNIQUE KEY uk_registration_queue_date (registration_id, queue_date),
    
    FOREIGN KEY (patient_id) REFERENCES patients(id),
    FOREIGN KEY (registration_id) REFERENCES registrations(id),
    FOREIGN KEY (called_by) REFERENCES users(id),
    FOREIGN KEY (confirmed_by) REFERENCES users(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='患者队列表';

-- ========================================
-- 修复医疗记录表的外键约束
-- ========================================
-- 为 medical_records 表添加 registration_id 外键约束
ALTER TABLE medical_records 
ADD CONSTRAINT fk_medical_records_registration_id 
FOREIGN KEY (registration_id) REFERENCES registrations(id);

-- 为 prescription_items 表添加 medicine_id 外键约束
ALTER TABLE prescription_items 
ADD CONSTRAINT fk_prescription_items_medicine_id 
FOREIGN KEY (medicine_id) REFERENCES medicines(id);

-- ========================================
-- 插入权限数据
-- ========================================

-- 预约管理相关权限
INSERT INTO permissions (permission_code, permission_name, description, module, enabled, created_at, updated_at) VALUES
('APPOINTMENT_VIEW', '查看预约', '查看预约信息的权限', 'APPOINTMENT', TRUE, NOW(), NOW()),
('APPOINTMENT_CREATE', '创建预约', '创建新预约的权限', 'APPOINTMENT', TRUE, NOW(), NOW()),
('APPOINTMENT_UPDATE', '更新预约', '更新预约信息的权限', 'APPOINTMENT', TRUE, NOW(), NOW()),
('APPOINTMENT_DELETE', '删除预约', '删除预约的权限', 'APPOINTMENT', TRUE, NOW(), NOW()),
('APPOINTMENT_CANCEL', '取消预约', '取消预约的权限', 'APPOINTMENT', TRUE, NOW(), NOW()),
('APPOINTMENT_CONFIRM', '确认预约', '确认预约的权限', 'APPOINTMENT', TRUE, NOW(), NOW());

-- 挂号管理相关权限
INSERT INTO permissions (permission_code, permission_name, description, module, enabled, created_at, updated_at) VALUES
('REGISTRATION_VIEW', '查看挂号', '查看挂号信息的权限', 'REGISTRATION', TRUE, NOW(), NOW()),
('REGISTRATION_CREATE', '创建挂号', '创建新挂号的权限', 'REGISTRATION', TRUE, NOW(), NOW()),
('REGISTRATION_UPDATE', '更新挂号', '更新挂号信息的权限', 'REGISTRATION', TRUE, NOW(), NOW()),
('REGISTRATION_DELETE', '删除挂号', '删除挂号的权限', 'REGISTRATION', TRUE, NOW(), NOW()),
('REGISTRATION_CANCEL', '取消挂号', '取消挂号的权限', 'REGISTRATION', TRUE, NOW(), NOW()),
('REGISTRATION_CALL', '叫号', '叫号的权限', 'REGISTRATION', TRUE, NOW(), NOW());

-- 库存管理相关权限
INSERT INTO permissions (permission_code, permission_name, description, module, enabled, created_at, updated_at) VALUES
('MEDICINE_VIEW', '查看药品', '查看药品信息的权限', 'INVENTORY', TRUE, NOW(), NOW()),
('MEDICINE_CREATE', '创建药品', '创建新药品的权限', 'INVENTORY', TRUE, NOW(), NOW()),
('MEDICINE_UPDATE', '更新药品', '更新药品信息的权限', 'INVENTORY', TRUE, NOW(), NOW()),
('MEDICINE_DELETE', '删除药品', '删除药品的权限', 'INVENTORY', TRUE, NOW(), NOW()),
('INVENTORY_VIEW', '查看库存', '查看库存信息的权限', 'INVENTORY', TRUE, NOW(), NOW()),
('INVENTORY_UPDATE', '更新库存', '更新库存的权限', 'INVENTORY', TRUE, NOW(), NOW()),
('STOCK_TRANSACTION_VIEW', '查看库存交易', '查看库存交易记录的权限', 'INVENTORY', TRUE, NOW(), NOW()),
('STOCK_TRANSACTION_CREATE', '创建库存交易', '创建库存交易的权限', 'INVENTORY', TRUE, NOW(), NOW());

-- ========================================
-- 角色权限分配
-- ========================================

-- 为超级管理员角色分配所有权限
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id 
FROM roles r, permissions p 
WHERE r.role_code = 'SUPER_ADMIN' 
AND p.permission_code IN (
    'APPOINTMENT_VIEW', 'APPOINTMENT_CREATE', 'APPOINTMENT_UPDATE', 'APPOINTMENT_DELETE', 'APPOINTMENT_CANCEL', 'APPOINTMENT_CONFIRM',
    'REGISTRATION_VIEW', 'REGISTRATION_CREATE', 'REGISTRATION_UPDATE', 'REGISTRATION_DELETE', 'REGISTRATION_CANCEL', 'REGISTRATION_CALL',
    'MEDICINE_VIEW', 'MEDICINE_CREATE', 'MEDICINE_UPDATE', 'MEDICINE_DELETE', 'INVENTORY_VIEW', 'INVENTORY_UPDATE', 'STOCK_TRANSACTION_VIEW', 'STOCK_TRANSACTION_CREATE'
);

-- 为医生角色分配相关权限
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id 
FROM roles r, permissions p 
WHERE r.role_code = 'DOCTOR' 
AND p.permission_code IN ('APPOINTMENT_VIEW', 'REGISTRATION_VIEW', 'MEDICINE_VIEW');

-- 为护士角色分配相关权限
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id 
FROM roles r, permissions p 
WHERE r.role_code = 'NURSE' 
AND p.permission_code IN ('APPOINTMENT_VIEW', 'APPOINTMENT_CONFIRM', 'REGISTRATION_VIEW', 'REGISTRATION_CALL', 'MEDICINE_VIEW');

-- 为前台文员角色分配相关权限
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id 
FROM roles r, permissions p 
WHERE r.role_code = 'RECEPTIONIST' 
AND p.permission_code IN (
    'APPOINTMENT_VIEW', 'APPOINTMENT_CREATE', 'APPOINTMENT_UPDATE', 'APPOINTMENT_CANCEL', 'APPOINTMENT_CONFIRM',
    'REGISTRATION_VIEW', 'REGISTRATION_CREATE', 'REGISTRATION_UPDATE', 'REGISTRATION_CANCEL'
);

-- 为药剂师角色分配相关权限
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id 
FROM roles r, permissions p 
WHERE r.role_code = 'PHARMACIST' 
AND p.permission_code IN ('MEDICINE_VIEW', 'INVENTORY_VIEW', 'INVENTORY_UPDATE', 'STOCK_TRANSACTION_VIEW', 'STOCK_TRANSACTION_CREATE');