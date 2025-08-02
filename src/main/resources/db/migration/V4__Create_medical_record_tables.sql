-- 创建病历表
CREATE TABLE medical_records (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    patient_id BIGINT NOT NULL COMMENT '患者ID',
    doctor_id BIGINT NOT NULL COMMENT '医生ID',
    registration_id BIGINT COMMENT '挂号ID',
    record_number VARCHAR(50) NOT NULL UNIQUE COMMENT '病历编号',
    chief_complaint TEXT COMMENT '主诉',
    present_illness TEXT COMMENT '现病史',
    past_history TEXT COMMENT '既往史',
    personal_history TEXT COMMENT '个人史',
    family_history TEXT COMMENT '家族史',
    physical_examination TEXT COMMENT '体格检查',
    auxiliary_examination TEXT COMMENT '辅助检查',
    preliminary_diagnosis TEXT COMMENT '初步诊断',
    final_diagnosis TEXT COMMENT '最终诊断',
    treatment_plan TEXT COMMENT '治疗方案',
    medical_orders TEXT COMMENT '医嘱',
    condition_assessment TEXT COMMENT '病情评估',
    prognosis VARCHAR(500) COMMENT '预后',
    follow_up_advice TEXT COMMENT '随访建议',
    status VARCHAR(20) NOT NULL DEFAULT '草稿' COMMENT '病历状态',
    record_date DATETIME NOT NULL COMMENT '记录日期',
    review_doctor_id BIGINT COMMENT '审核医生ID',
    review_time DATETIME COMMENT '审核时间',
    review_comments VARCHAR(500) COMMENT '审核意见',
    department VARCHAR(100) COMMENT '科室',
    record_type VARCHAR(20) NOT NULL DEFAULT '门诊病历' COMMENT '病历类型',
    is_infectious BOOLEAN NOT NULL DEFAULT FALSE COMMENT '是否传染病',
    is_chronic_disease BOOLEAN NOT NULL DEFAULT FALSE COMMENT '是否慢性病',
    summary VARCHAR(500) COMMENT '病历摘要',
    remarks TEXT COMMENT '备注信息',
    created_by BIGINT COMMENT '创建医生ID',
    last_updated_by BIGINT COMMENT '最后更新医生ID',
    created_at DATETIME NOT NULL COMMENT '创建时间',
    updated_at DATETIME NOT NULL COMMENT '更新时间',
    deleted INT NOT NULL DEFAULT 0 COMMENT '逻辑删除标识',
    INDEX idx_patient_id (patient_id),
    INDEX idx_doctor_id (doctor_id),
    INDEX idx_registration_id (registration_id),
    INDEX idx_record_number (record_number),
    INDEX idx_status (status),
    INDEX idx_record_date (record_date),
    INDEX idx_department (department),
    INDEX idx_record_type (record_type),
    INDEX idx_is_infectious (is_infectious),
    INDEX idx_is_chronic_disease (is_chronic_disease),
    INDEX idx_deleted (deleted),
    FOREIGN KEY (patient_id) REFERENCES patients(id),
    FOREIGN KEY (doctor_id) REFERENCES users(id),
    -- FOREIGN KEY (registration_id) REFERENCES registrations(id), -- 暂时注释，等registrations表创建后再启用
    FOREIGN KEY (review_doctor_id) REFERENCES users(id),
    FOREIGN KEY (created_by) REFERENCES users(id),
    FOREIGN KEY (last_updated_by) REFERENCES users(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='病历表';

-- 创建诊断表
CREATE TABLE diagnoses (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    medical_record_id BIGINT NOT NULL COMMENT '病历ID',
    diagnosis_code VARCHAR(20) COMMENT '诊断编码（ICD-10）',
    diagnosis_name VARCHAR(200) NOT NULL COMMENT '诊断名称',
    diagnosis_type VARCHAR(20) NOT NULL DEFAULT '主要诊断' COMMENT '诊断类型',
    description TEXT COMMENT '诊断描述',
    severity VARCHAR(20) COMMENT '严重程度',
    status VARCHAR(20) NOT NULL DEFAULT '初步诊断' COMMENT '诊断状态',
    evidence TEXT COMMENT '诊断依据',
    doctor_id BIGINT NOT NULL COMMENT '诊断医生ID',
    diagnosis_time DATETIME NOT NULL COMMENT '诊断时间',
    is_primary BOOLEAN NOT NULL DEFAULT FALSE COMMENT '是否主诊断',
    is_chronic_disease BOOLEAN NOT NULL DEFAULT FALSE COMMENT '是否慢性病',
    is_infectious BOOLEAN NOT NULL DEFAULT FALSE COMMENT '是否传染病',
    is_hereditary BOOLEAN NOT NULL DEFAULT FALSE COMMENT '是否遗传病',
    prognosis VARCHAR(500) COMMENT '预后评估',
    treatment_advice TEXT COMMENT '治疗建议',
    follow_up_requirement VARCHAR(500) COMMENT '随访要求',
    remarks VARCHAR(500) COMMENT '备注信息',
    sort_order INT NOT NULL DEFAULT 1 COMMENT '排序序号',
    created_at DATETIME NOT NULL COMMENT '创建时间',
    updated_at DATETIME NOT NULL COMMENT '更新时间',
    deleted INT NOT NULL DEFAULT 0 COMMENT '逻辑删除标识',
    INDEX idx_medical_record_id (medical_record_id),
    INDEX idx_diagnosis_code (diagnosis_code),
    INDEX idx_diagnosis_type (diagnosis_type),
    INDEX idx_status (status),
    INDEX idx_doctor_id (doctor_id),
    INDEX idx_diagnosis_time (diagnosis_time),
    INDEX idx_is_primary (is_primary),
    INDEX idx_sort_order (sort_order),
    INDEX idx_deleted (deleted),
    FOREIGN KEY (medical_record_id) REFERENCES medical_records(id) ON DELETE CASCADE,
    FOREIGN KEY (doctor_id) REFERENCES users(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='诊断表';

-- 创建处方表
CREATE TABLE prescriptions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    medical_record_id BIGINT NOT NULL COMMENT '病历ID',
    doctor_id BIGINT NOT NULL COMMENT '医生ID',
    prescription_number VARCHAR(50) NOT NULL UNIQUE COMMENT '处方编号',
    prescription_type VARCHAR(20) NOT NULL DEFAULT '普通处方' COMMENT '处方类型',
    status VARCHAR(20) NOT NULL DEFAULT '草稿' COMMENT '处方状态',
    prescribed_at DATETIME NOT NULL COMMENT '开具时间',
    review_doctor_id BIGINT COMMENT '审核医生ID',
    reviewed_at DATETIME COMMENT '审核时间',
    pharmacist_id BIGINT COMMENT '调配药师ID',
    dispensed_at DATETIME COMMENT '调配时间',
    delivered_at DATETIME COMMENT '发药时间',
    total_amount DECIMAL(10,2) COMMENT '处方总金额',
    dosage_instructions TEXT COMMENT '用法用量说明',
    precautions TEXT COMMENT '注意事项',
    clinical_diagnosis VARCHAR(500) COMMENT '临床诊断',
    validity_days INT NOT NULL DEFAULT 3 COMMENT '处方有效期（天）',
    is_emergency BOOLEAN NOT NULL DEFAULT FALSE COMMENT '是否急诊处方',
    is_child_prescription BOOLEAN NOT NULL DEFAULT FALSE COMMENT '是否儿童处方',
    is_chronic_disease_prescription BOOLEAN NOT NULL DEFAULT FALSE COMMENT '是否慢性病处方',
    repeat_times INT NOT NULL DEFAULT 1 COMMENT '重复次数',
    review_comments VARCHAR(500) COMMENT '审核意见',
    cancel_reason VARCHAR(500) COMMENT '取消原因',
    cancelled_at DATETIME COMMENT '取消时间',
    remarks TEXT COMMENT '备注信息',
    created_at DATETIME NOT NULL COMMENT '创建时间',
    updated_at DATETIME NOT NULL COMMENT '更新时间',
    deleted INT NOT NULL DEFAULT 0 COMMENT '逻辑删除标识',
    INDEX idx_medical_record_id (medical_record_id),
    INDEX idx_doctor_id (doctor_id),
    INDEX idx_prescription_number (prescription_number),
    INDEX idx_prescription_type (prescription_type),
    INDEX idx_status (status),
    INDEX idx_prescribed_at (prescribed_at),
    INDEX idx_pharmacist_id (pharmacist_id),
    INDEX idx_is_emergency (is_emergency),
    INDEX idx_deleted (deleted),
    FOREIGN KEY (medical_record_id) REFERENCES medical_records(id) ON DELETE CASCADE,
    FOREIGN KEY (doctor_id) REFERENCES users(id),
    FOREIGN KEY (review_doctor_id) REFERENCES users(id),
    FOREIGN KEY (pharmacist_id) REFERENCES users(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='处方表';

-- 创建处方项目表
CREATE TABLE prescription_items (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    prescription_id BIGINT NOT NULL COMMENT '处方ID',
    medicine_id BIGINT NOT NULL COMMENT '药品ID',
    quantity INT NOT NULL COMMENT '数量',
    unit_price DECIMAL(10,2) NOT NULL COMMENT '单价',
    subtotal DECIMAL(10,2) NOT NULL COMMENT '小计',
    dosage VARCHAR(100) COMMENT '剂量',
    frequency VARCHAR(100) COMMENT '频次',
    duration VARCHAR(100) COMMENT '疗程',
    instructions TEXT COMMENT '用药说明',
    sort_order INT NOT NULL DEFAULT 1 COMMENT '排序序号',
    created_at DATETIME NOT NULL COMMENT '创建时间',
    updated_at DATETIME NOT NULL COMMENT '更新时间',
    deleted INT NOT NULL DEFAULT 0 COMMENT '逻辑删除标识',
    INDEX idx_prescription_id (prescription_id),
    INDEX idx_medicine_id (medicine_id),
    INDEX idx_sort_order (sort_order),
    INDEX idx_deleted (deleted),
    FOREIGN KEY (prescription_id) REFERENCES prescriptions(id) ON DELETE CASCADE,
    -- FOREIGN KEY (medicine_id) REFERENCES medicines(id) -- 暂时注释，等medicines表创建后再启用
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='处方项目表';

-- 添加医疗相关权限
INSERT INTO permissions (permission_code, permission_name, description, module, enabled, created_at, updated_at) VALUES
('MEDICAL_RECORD_READ', '查看病历', '查看病历信息的权限', 'MEDICAL_RECORD', TRUE, NOW(), NOW()),
('MEDICAL_RECORD_WRITE', '编辑病历', '创建和编辑病历的权限', 'MEDICAL_RECORD', TRUE, NOW(), NOW()),
('MEDICAL_RECORD_REVIEW', '审核病历', '审核病历的权限', 'MEDICAL_RECORD', TRUE, NOW(), NOW()),
('MEDICAL_RECORD_DELETE', '删除病历', '删除病历的权限', 'MEDICAL_RECORD', TRUE, NOW(), NOW()),
('PRESCRIPTION_CREATE', '开具处方', '开具处方的权限', 'PRESCRIPTION', TRUE, NOW(), NOW()),
('PRESCRIPTION_REVIEW', '审核处方', '审核处方的权限', 'PRESCRIPTION', TRUE, NOW(), NOW()),
('PRESCRIPTION_DISPENSE', '调配处方', '调配处方的权限', 'PRESCRIPTION', TRUE, NOW(), NOW()),
('DIAGNOSIS_MANAGE', '诊断管理', '管理诊断信息的权限', 'DIAGNOSIS', TRUE, NOW(), NOW());

-- 为医生角色分配医疗相关权限
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id 
FROM roles r, permissions p 
WHERE r.role_code = 'DOCTOR' 
AND p.permission_code IN ('MEDICAL_RECORD_READ', 'MEDICAL_RECORD_WRITE', 'MEDICAL_RECORD_REVIEW', 'PRESCRIPTION_CREATE', 'PRESCRIPTION_REVIEW', 'DIAGNOSIS_MANAGE');

-- 为护士角色分配查看病历权限
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id 
FROM roles r, permissions p 
WHERE r.role_code = 'NURSE' 
AND p.permission_code IN ('MEDICAL_RECORD_READ');

-- 为药剂师角色分配处方相关权限
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id 
FROM roles r, permissions p 
WHERE r.role_code = 'PHARMACIST' 
AND p.permission_code IN ('PRESCRIPTION_REVIEW', 'PRESCRIPTION_DISPENSE');

-- 为超级管理员角色分配新增的权限
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id 
FROM roles r, permissions p 
WHERE r.role_code = 'SUPER_ADMIN' 
AND p.permission_code IN ('MEDICAL_RECORD_READ', 'MEDICAL_RECORD_WRITE', 'MEDICAL_RECORD_REVIEW', 'MEDICAL_RECORD_DELETE', 'PRESCRIPTION_CREATE', 'PRESCRIPTION_REVIEW', 'PRESCRIPTION_DISPENSE', 'DIAGNOSIS_MANAGE');