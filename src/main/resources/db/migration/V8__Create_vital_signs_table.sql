-- 创建生命体征表
CREATE TABLE vital_signs (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    patient_id BIGINT NOT NULL COMMENT '患者ID',
    recorded_by BIGINT NOT NULL COMMENT '记录人员ID',
    systolic_bp INTEGER COMMENT '收缩压(mmHg)',
    diastolic_bp INTEGER COMMENT '舒张压(mmHg)',
    temperature DECIMAL(4,1) COMMENT '体温(°C)',
    heart_rate INTEGER COMMENT '心率(次/分钟)',
    respiratory_rate INTEGER COMMENT '呼吸频率(次/分钟)',
    oxygen_saturation INTEGER COMMENT '血氧饱和度(%)',
    weight DECIMAL(5,2) COMMENT '体重(kg)',
    height INTEGER COMMENT '身高(cm)',
    bmi DECIMAL(4,1) COMMENT 'BMI指数',
    pain_score INTEGER COMMENT '疼痛评分(0-10)',
    consciousness_level VARCHAR(20) COMMENT '意识状态',
    remarks TEXT COMMENT '备注信息',
    is_abnormal BOOLEAN NOT NULL DEFAULT FALSE COMMENT '是否异常',
    abnormal_indicators VARCHAR(500) COMMENT '异常指标说明',
    recorded_at DATETIME NOT NULL COMMENT '记录时间',
    created_at DATETIME NOT NULL COMMENT '创建时间',
    updated_at DATETIME NOT NULL COMMENT '更新时间',
    deleted INT NOT NULL DEFAULT 0 COMMENT '逻辑删除标识',
    INDEX idx_patient_id (patient_id),
    INDEX idx_recorded_by (recorded_by),
    INDEX idx_recorded_at (recorded_at),
    INDEX idx_is_abnormal (is_abnormal),
    INDEX idx_deleted (deleted),
    FOREIGN KEY (patient_id) REFERENCES patients(id) ON DELETE CASCADE,
    FOREIGN KEY (recorded_by) REFERENCES users(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='生命体征表';

-- 插入生命体征管理相关权限
INSERT INTO permissions (permission_code, permission_name, description, module, enabled, created_at, updated_at) VALUES
('VITAL_SIGNS_VIEW', '查看生命体征', '查看患者生命体征的权限', 'NURSE_WORKSTATION', TRUE, NOW(), NOW()),
('VITAL_SIGNS_RECORD', '录入生命体征', '录入患者生命体征的权限', 'NURSE_WORKSTATION', TRUE, NOW(), NOW()),
('VITAL_SIGNS_UPDATE', '更新生命体征', '更新患者生命体征的权限', 'NURSE_WORKSTATION', TRUE, NOW(), NOW());

-- 为超级管理员角色分配生命体征管理权限
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id 
FROM roles r, permissions p 
WHERE r.role_code = 'SUPER_ADMIN' 
AND p.permission_code IN ('VITAL_SIGNS_VIEW', 'VITAL_SIGNS_RECORD', 'VITAL_SIGNS_UPDATE');

-- 为医生角色分配生命体征查看权限
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id 
FROM roles r, permissions p 
WHERE r.role_code = 'DOCTOR' 
AND p.permission_code IN ('VITAL_SIGNS_VIEW');

-- 为护士角色分配生命体征管理权限
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id 
FROM roles r, permissions p 
WHERE r.role_code = 'NURSE' 
AND p.permission_code IN ('VITAL_SIGNS_VIEW', 'VITAL_SIGNS_RECORD', 'VITAL_SIGNS_UPDATE');