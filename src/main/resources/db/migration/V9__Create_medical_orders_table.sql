-- 创建医嘱表
CREATE TABLE medical_orders (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    patient_id BIGINT NOT NULL COMMENT '患者ID',
    prescribed_by BIGINT NOT NULL COMMENT '开具医嘱的医生ID',
    executed_by BIGINT COMMENT '执行医嘱的护士ID',
    order_type VARCHAR(50) NOT NULL COMMENT '医嘱类型',
    content VARCHAR(500) NOT NULL COMMENT '医嘱内容',
    dosage VARCHAR(100) COMMENT '剂量',
    frequency VARCHAR(100) COMMENT '频次',
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING' COMMENT '医嘱状态',
    priority VARCHAR(20) DEFAULT 'NORMAL' COMMENT '优先级',
    prescribed_at DATETIME NOT NULL COMMENT '医嘱开具时间',
    executed_at DATETIME COMMENT '医嘱执行时间',
    execution_notes VARCHAR(500) COMMENT '执行备注',
    postpone_reason VARCHAR(500) COMMENT '暂缓原因',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    created_by BIGINT COMMENT '创建人',
    updated_by BIGINT COMMENT '更新人',
    
    INDEX idx_patient_id (patient_id),
    INDEX idx_prescribed_by (prescribed_by),
    INDEX idx_executed_by (executed_by),
    INDEX idx_status (status),
    INDEX idx_priority (priority),
    INDEX idx_prescribed_at (prescribed_at),
    INDEX idx_executed_at (executed_at),
    
    FOREIGN KEY (patient_id) REFERENCES patients(id) ON DELETE CASCADE,
    FOREIGN KEY (prescribed_by) REFERENCES users(id) ON DELETE RESTRICT,
    FOREIGN KEY (executed_by) REFERENCES users(id) ON DELETE RESTRICT
) COMMENT='医嘱表';

-- 插入测试数据
INSERT INTO medical_orders (patient_id, prescribed_by, order_type, content, dosage, frequency, status, priority, prescribed_at) VALUES
(1, 1, 'INJECTION', '静脉注射生理盐水', '250ml', '每日一次', 'PENDING', 'NORMAL', NOW()),
(1, 1, 'ORAL_MEDICATION', '口服阿莫西林胶囊', '0.5g', '每日三次', 'PENDING', 'NORMAL', NOW()),
(2, 1, 'EXAMINATION', '测量血压', NULL, '每4小时一次', 'PENDING', 'URGENT', NOW()),
(2, 1, 'NURSING_CARE', '协助患者翻身', NULL, '每2小时一次', 'PENDING', 'NORMAL', NOW()),
(3, 1, 'TREATMENT', '伤口换药', NULL, '每日一次', 'EXECUTED', 'NORMAL', DATE_SUB(NOW(), INTERVAL 1 HOUR), NOW()),
(3, 1, 'OBSERVATION', '观察患者体温变化', NULL, '每小时一次', 'PENDING', 'URGENT', NOW());