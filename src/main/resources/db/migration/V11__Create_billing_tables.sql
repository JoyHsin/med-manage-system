-- 创建账单表
CREATE TABLE bills (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    patient_id BIGINT NOT NULL COMMENT '患者ID',
    registration_id BIGINT COMMENT '挂号ID',
    bill_number VARCHAR(50) NOT NULL UNIQUE COMMENT '账单编号',
    total_amount DECIMAL(10,2) NOT NULL DEFAULT 0.00 COMMENT '总金额',
    paid_amount DECIMAL(10,2) NOT NULL DEFAULT 0.00 COMMENT '已付金额',
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING' COMMENT '账单状态',
    notes TEXT COMMENT '备注',
    created_by BIGINT COMMENT '创建人ID',
    updated_by BIGINT COMMENT '更新人ID',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    
    INDEX idx_patient_id (patient_id),
    INDEX idx_registration_id (registration_id),
    INDEX idx_bill_number (bill_number),
    INDEX idx_status (status),
    INDEX idx_created_at (created_at),
    
    FOREIGN KEY (patient_id) REFERENCES patients(id),
    FOREIGN KEY (registration_id) REFERENCES registrations(id),
    FOREIGN KEY (created_by) REFERENCES users(id),
    FOREIGN KEY (updated_by) REFERENCES users(id)
) COMMENT='账单表';

-- 创建账单项目表
CREATE TABLE bill_items (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    bill_id BIGINT NOT NULL COMMENT '账单ID',
    item_type VARCHAR(20) NOT NULL COMMENT '项目类型',
    item_name VARCHAR(100) NOT NULL COMMENT '项目名称',
    item_code VARCHAR(50) COMMENT '项目编码',
    unit_price DECIMAL(10,2) NOT NULL COMMENT '单价',
    quantity INT NOT NULL DEFAULT 1 COMMENT '数量',
    subtotal DECIMAL(10,2) NOT NULL COMMENT '小计金额',
    discount DECIMAL(10,2) NOT NULL DEFAULT 0.00 COMMENT '折扣金额',
    actual_amount DECIMAL(10,2) NOT NULL COMMENT '实际金额',
    specification VARCHAR(50) COMMENT '规格/单位',
    notes TEXT COMMENT '备注',
    prescription_item_id BIGINT COMMENT '关联的处方项目ID',
    medical_record_id BIGINT COMMENT '关联的医疗记录ID',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    
    INDEX idx_bill_id (bill_id),
    INDEX idx_item_type (item_type),
    INDEX idx_prescription_item_id (prescription_item_id),
    INDEX idx_medical_record_id (medical_record_id),
    
    FOREIGN KEY (bill_id) REFERENCES bills(id) ON DELETE CASCADE,
    FOREIGN KEY (prescription_item_id) REFERENCES prescription_items(id),
    FOREIGN KEY (medical_record_id) REFERENCES medical_records(id)
) COMMENT='账单项目表';

-- 账单状态枚举值说明:
-- PENDING: 待付款
-- PAID: 已付款  
-- PARTIALLY_PAID: 部分付款
-- CANCELLED: 已取消

-- 账单项目类型枚举值说明:
-- REGISTRATION: 挂号费
-- CONSULTATION: 诊疗费
-- MEDICINE: 药品费
-- EXAMINATION: 检查费
-- TREATMENT: 治疗费
-- OTHER: 其他费用