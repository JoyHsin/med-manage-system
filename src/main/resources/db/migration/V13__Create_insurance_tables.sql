-- 创建医保相关表

-- 医保提供商表
CREATE TABLE insurance_provider (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    provider_code VARCHAR(50) NOT NULL UNIQUE COMMENT '医保机构代码',
    provider_name VARCHAR(100) NOT NULL COMMENT '医保机构名称',
    insurance_type VARCHAR(20) NOT NULL COMMENT '医保类型',
    contact_phone VARCHAR(20) COMMENT '联系电话',
    contact_address VARCHAR(200) COMMENT '联系地址',
    reimbursement_rate DECIMAL(5,4) NOT NULL DEFAULT 0.7000 COMMENT '报销比例',
    deductible_amount DECIMAL(10,2) NOT NULL DEFAULT 0.00 COMMENT '起付线金额',
    annual_limit DECIMAL(10,2) NOT NULL DEFAULT 100000.00 COMMENT '年度报销上限',
    enabled BOOLEAN NOT NULL DEFAULT TRUE COMMENT '是否启用',
    api_endpoint VARCHAR(200) COMMENT 'API接口地址',
    api_key VARCHAR(100) COMMENT 'API密钥',
    remarks TEXT COMMENT '备注',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    created_by VARCHAR(50) COMMENT '创建人',
    updated_by VARCHAR(50) COMMENT '更新人'
);

-- 医保资格表
CREATE TABLE insurance_eligibility (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    patient_id BIGINT NOT NULL COMMENT '患者ID',
    insurance_provider_id BIGINT NOT NULL COMMENT '医保提供商ID',
    insurance_card_number VARCHAR(50) NOT NULL COMMENT '医保卡号',
    eligibility_status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE' COMMENT '参保状态',
    start_date DATE NOT NULL COMMENT '参保开始日期',
    end_date DATE NOT NULL COMMENT '参保结束日期',
    used_amount DECIMAL(10,2) NOT NULL DEFAULT 0.00 COMMENT '本年度已使用额度',
    remaining_amount DECIMAL(10,2) NOT NULL DEFAULT 0.00 COMMENT '本年度剩余额度',
    personal_account_balance DECIMAL(10,2) NOT NULL DEFAULT 0.00 COMMENT '个人账户余额',
    last_verification_date DATE COMMENT '最后验证时间',
    verification_result VARCHAR(20) COMMENT '验证结果',
    remarks TEXT COMMENT '备注',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    created_by VARCHAR(50) COMMENT '创建人',
    updated_by VARCHAR(50) COMMENT '更新人',
    FOREIGN KEY (patient_id) REFERENCES patient(id),
    FOREIGN KEY (insurance_provider_id) REFERENCES insurance_provider(id),
    UNIQUE KEY uk_patient_provider (patient_id, insurance_provider_id)
);

-- 医保理赔表
CREATE TABLE insurance_claim (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    claim_number VARCHAR(50) NOT NULL UNIQUE COMMENT '理赔单号',
    patient_id BIGINT NOT NULL COMMENT '患者ID',
    bill_id BIGINT NOT NULL COMMENT '账单ID',
    insurance_provider_id BIGINT NOT NULL COMMENT '医保提供商ID',
    insurance_card_number VARCHAR(50) NOT NULL COMMENT '医保卡号',
    total_amount DECIMAL(10,2) NOT NULL COMMENT '总费用金额',
    reimbursable_amount DECIMAL(10,2) COMMENT '可报销金额',
    actual_reimbursement DECIMAL(10,2) COMMENT '实际报销金额',
    personal_payment DECIMAL(10,2) COMMENT '个人支付金额',
    claim_status VARCHAR(20) NOT NULL DEFAULT 'PENDING' COMMENT '理赔状态',
    claim_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '申请时间',
    review_date TIMESTAMP COMMENT '审核时间',
    completed_date TIMESTAMP COMMENT '完成时间',
    reviewed_by VARCHAR(50) COMMENT '审核人员',
    rejection_reason TEXT COMMENT '拒绝原因',
    external_claim_id VARCHAR(100) COMMENT '外部理赔单号',
    remarks TEXT COMMENT '备注',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    created_by VARCHAR(50) COMMENT '创建人',
    updated_by VARCHAR(50) COMMENT '更新人',
    FOREIGN KEY (patient_id) REFERENCES patient(id),
    FOREIGN KEY (bill_id) REFERENCES bill(id),
    FOREIGN KEY (insurance_provider_id) REFERENCES insurance_provider(id),
    INDEX idx_patient_id (patient_id),
    INDEX idx_bill_id (bill_id),
    INDEX idx_claim_status (claim_status),
    INDEX idx_claim_date (claim_date)
);

-- 插入默认医保提供商数据
INSERT INTO insurance_provider (provider_code, provider_name, insurance_type, reimbursement_rate, deductible_amount, annual_limit, enabled) VALUES
('BASIC_001', '城镇职工基本医疗保险', 'BASIC_MEDICAL', 0.8000, 200.00, 200000.00, TRUE),
('BASIC_002', '城乡居民基本医疗保险', 'BASIC_MEDICAL', 0.7000, 100.00, 150000.00, TRUE),
('COMM_001', '中国人寿商业医疗保险', 'COMMERCIAL', 0.9000, 0.00, 500000.00, TRUE),
('COMM_002', '平安商业医疗保险', 'COMMERCIAL', 0.8500, 0.00, 300000.00, TRUE),
('GOV_001', '公务员医疗补助', 'GOVERNMENT', 0.9500, 0.00, 1000000.00, TRUE);

-- 添加医保相关权限
INSERT INTO permission (name, description, module) VALUES
('INSURANCE_CLAIM_CREATE', '创建医保理赔', 'INSURANCE'),
('INSURANCE_CLAIM_READ', '查看医保理赔', 'INSURANCE'),
('INSURANCE_CLAIM_PROCESS', '处理医保理赔', 'INSURANCE'),
('INSURANCE_CLAIM_REVIEW', '审核医保理赔', 'INSURANCE'),
('INSURANCE_ELIGIBILITY_CHECK', '检查医保资格', 'INSURANCE'),
('INSURANCE_ELIGIBILITY_READ', '查看医保资格', 'INSURANCE'),
('INSURANCE_PROVIDER_READ', '查看医保提供商', 'INSURANCE');

-- 为超级管理员角色添加医保权限
INSERT INTO role_permission (role_id, permission_id)
SELECT r.id, p.id
FROM role r, permission p
WHERE r.name = 'SUPER_ADMIN' AND p.module = 'INSURANCE';

-- 为前台文员角色添加部分医保权限
INSERT INTO role_permission (role_id, permission_id)
SELECT r.id, p.id
FROM role r, permission p
WHERE r.name = 'RECEPTIONIST' AND p.name IN ('INSURANCE_CLAIM_CREATE', 'INSURANCE_CLAIM_READ', 'INSURANCE_ELIGIBILITY_CHECK', 'INSURANCE_ELIGIBILITY_READ', 'INSURANCE_PROVIDER_READ');