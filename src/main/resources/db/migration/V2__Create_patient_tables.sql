-- 创建患者表
CREATE TABLE patients (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    patient_number VARCHAR(50) NOT NULL UNIQUE COMMENT '患者编号',
    name VARCHAR(100) NOT NULL COMMENT '患者姓名',
    phone VARCHAR(20) COMMENT '手机号码',
    id_card VARCHAR(18) COMMENT '身份证号码',
    birth_date DATE COMMENT '出生日期',
    gender VARCHAR(10) NOT NULL COMMENT '性别',
    address VARCHAR(500) COMMENT '联系地址',
    emergency_contact_name VARCHAR(100) COMMENT '紧急联系人姓名',
    emergency_contact_phone VARCHAR(20) COMMENT '紧急联系人电话',
    emergency_contact_relation VARCHAR(50) COMMENT '紧急联系人关系',
    blood_type VARCHAR(10) COMMENT '血型',
    marital_status VARCHAR(20) COMMENT '婚姻状况',
    occupation VARCHAR(100) COMMENT '职业',
    ethnicity VARCHAR(50) COMMENT '民族',
    insurance_type VARCHAR(50) COMMENT '医保类型',
    insurance_number VARCHAR(100) COMMENT '医保号码',
    remarks TEXT COMMENT '备注信息',
    is_vip BOOLEAN NOT NULL DEFAULT FALSE COMMENT '是否为VIP患者',
    status VARCHAR(20) NOT NULL DEFAULT '正常' COMMENT '患者状态',
    first_visit_time DATETIME COMMENT '首次就诊时间',
    last_visit_time DATETIME COMMENT '最后就诊时间',
    visit_count INT NOT NULL DEFAULT 0 COMMENT '就诊次数',
    created_at DATETIME NOT NULL COMMENT '创建时间',
    updated_at DATETIME NOT NULL COMMENT '更新时间',
    deleted INT NOT NULL DEFAULT 0 COMMENT '逻辑删除标识',
    INDEX idx_patient_number (patient_number),
    INDEX idx_name (name),
    INDEX idx_phone (phone),
    INDEX idx_id_card (id_card),
    INDEX idx_gender (gender),
    INDEX idx_birth_date (birth_date),
    INDEX idx_status (status),
    INDEX idx_is_vip (is_vip),
    INDEX idx_visit_time (last_visit_time),
    INDEX idx_deleted (deleted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='患者表';

-- 创建过敏史表
CREATE TABLE allergy_histories (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    patient_id BIGINT NOT NULL COMMENT '患者ID',
    allergen VARCHAR(200) NOT NULL COMMENT '过敏原名称',
    allergen_type VARCHAR(20) NOT NULL COMMENT '过敏原类型',
    symptoms TEXT NOT NULL COMMENT '过敏反应症状',
    severity VARCHAR(20) NOT NULL COMMENT '过敏严重程度',
    first_discovered_time DATETIME COMMENT '首次发现时间',
    last_occurrence_time DATETIME COMMENT '最后发作时间',
    treatment TEXT COMMENT '处理措施',
    remarks VARCHAR(500) COMMENT '备注信息',
    is_confirmed BOOLEAN NOT NULL DEFAULT FALSE COMMENT '是否确认过敏',
    recorded_by BIGINT COMMENT '记录人员ID',
    recorded_time DATETIME COMMENT '记录时间',
    created_at DATETIME NOT NULL COMMENT '创建时间',
    updated_at DATETIME NOT NULL COMMENT '更新时间',
    deleted INT NOT NULL DEFAULT 0 COMMENT '逻辑删除标识',
    INDEX idx_patient_id (patient_id),
    INDEX idx_allergen (allergen),
    INDEX idx_allergen_type (allergen_type),
    INDEX idx_severity (severity),
    INDEX idx_is_confirmed (is_confirmed),
    INDEX idx_recorded_time (recorded_time),
    INDEX idx_deleted (deleted),
    FOREIGN KEY (patient_id) REFERENCES patients(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='过敏史表';

-- 创建病史表
CREATE TABLE medical_histories (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    patient_id BIGINT NOT NULL COMMENT '患者ID',
    history_type VARCHAR(20) NOT NULL COMMENT '病史类型',
    disease_name VARCHAR(200) NOT NULL COMMENT '疾病名称',
    disease_category VARCHAR(100) COMMENT '疾病分类',
    onset_date DATE COMMENT '发病时间',
    diagnosis_date DATE COMMENT '诊断时间',
    treatment_status TEXT COMMENT '治疗情况',
    treatment_result VARCHAR(20) COMMENT '治疗结果',
    hospital VARCHAR(200) COMMENT '治疗医院',
    doctor VARCHAR(100) COMMENT '主治医生',
    description TEXT COMMENT '详细描述',
    exam_results TEXT COMMENT '相关检查结果',
    medications TEXT COMMENT '用药情况',
    family_relation VARCHAR(50) COMMENT '家族关系',
    is_hereditary BOOLEAN NOT NULL DEFAULT FALSE COMMENT '是否遗传性疾病',
    is_chronic BOOLEAN NOT NULL DEFAULT FALSE COMMENT '是否慢性疾病',
    is_contagious BOOLEAN NOT NULL DEFAULT FALSE COMMENT '是否传染性疾病',
    severity VARCHAR(20) COMMENT '严重程度',
    current_status VARCHAR(20) COMMENT '当前状态',
    remarks VARCHAR(500) COMMENT '备注信息',
    recorded_by BIGINT COMMENT '记录人员ID',
    recorded_time DATETIME COMMENT '记录时间',
    last_updated_by BIGINT COMMENT '最后更新人员ID',
    created_at DATETIME NOT NULL COMMENT '创建时间',
    updated_at DATETIME NOT NULL COMMENT '更新时间',
    deleted INT NOT NULL DEFAULT 0 COMMENT '逻辑删除标识',
    INDEX idx_patient_id (patient_id),
    INDEX idx_history_type (history_type),
    INDEX idx_disease_name (disease_name),
    INDEX idx_disease_category (disease_category),
    INDEX idx_diagnosis_date (diagnosis_date),
    INDEX idx_is_hereditary (is_hereditary),
    INDEX idx_is_chronic (is_chronic),
    INDEX idx_is_contagious (is_contagious),
    INDEX idx_severity (severity),
    INDEX idx_current_status (current_status),
    INDEX idx_recorded_time (recorded_time),
    INDEX idx_deleted (deleted),
    FOREIGN KEY (patient_id) REFERENCES patients(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='病史表';

-- 插入患者管理相关权限
INSERT INTO permissions (permission_code, permission_name, description, module, enabled, created_at, updated_at) VALUES
('PATIENT_VIEW', '查看患者', '查看患者信息的权限', 'PATIENT_MANAGEMENT', TRUE, NOW(), NOW()),
('PATIENT_CREATE', '创建患者', '创建新患者的权限', 'PATIENT_MANAGEMENT', TRUE, NOW(), NOW()),
('PATIENT_UPDATE', '更新患者', '更新患者信息的权限', 'PATIENT_MANAGEMENT', TRUE, NOW(), NOW()),
('PATIENT_DELETE', '删除患者', '删除患者的权限', 'PATIENT_MANAGEMENT', TRUE, NOW(), NOW());

-- 为超级管理员角色分配患者管理权限
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id 
FROM roles r, permissions p 
WHERE r.role_code = 'SUPER_ADMIN' 
AND p.permission_code IN ('PATIENT_VIEW', 'PATIENT_CREATE', 'PATIENT_UPDATE', 'PATIENT_DELETE');

-- 为医生角色分配患者查看和更新权限
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id 
FROM roles r, permissions p 
WHERE r.role_code = 'DOCTOR' 
AND p.permission_code IN ('PATIENT_VIEW', 'PATIENT_UPDATE');

-- 为护士角色分配患者查看和更新权限
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id 
FROM roles r, permissions p 
WHERE r.role_code = 'NURSE' 
AND p.permission_code IN ('PATIENT_VIEW', 'PATIENT_UPDATE');

-- 为前台文员角色分配患者管理权限
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id 
FROM roles r, permissions p 
WHERE r.role_code = 'RECEPTIONIST' 
AND p.permission_code IN ('PATIENT_VIEW', 'PATIENT_CREATE', 'PATIENT_UPDATE');