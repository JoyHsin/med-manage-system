-- 创建支付记录表
CREATE TABLE payments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    bill_id BIGINT NOT NULL COMMENT '账单ID',
    amount DECIMAL(10,2) NOT NULL COMMENT '支付金额',
    payment_method VARCHAR(20) NOT NULL COMMENT '支付方式',
    transaction_id VARCHAR(100) COMMENT '交易流水号',
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING' COMMENT '支付状态',
    payment_time DATETIME COMMENT '支付时间',
    refund_amount DECIMAL(10,2) DEFAULT 0.00 COMMENT '退款金额',
    refund_time DATETIME COMMENT '退款时间',
    refund_reason VARCHAR(500) COMMENT '退款原因',
    notes TEXT COMMENT '备注',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    created_by VARCHAR(50) COMMENT '创建人',
    updated_by VARCHAR(50) COMMENT '更新人',
    
    INDEX idx_bill_id (bill_id),
    INDEX idx_payment_method (payment_method),
    INDEX idx_status (status),
    INDEX idx_payment_time (payment_time),
    INDEX idx_transaction_id (transaction_id),
    
    FOREIGN KEY (bill_id) REFERENCES bills(id)
) COMMENT='支付记录表';

-- 创建收据表
CREATE TABLE receipts (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    payment_id BIGINT NOT NULL COMMENT '支付ID',
    receipt_number VARCHAR(50) NOT NULL UNIQUE COMMENT '收据编号',
    patient_id BIGINT NOT NULL COMMENT '患者ID',
    patient_name VARCHAR(100) NOT NULL COMMENT '患者姓名',
    item_details TEXT COMMENT '收费项目明细',
    total_amount DECIMAL(10,2) NOT NULL COMMENT '总金额',
    paid_amount DECIMAL(10,2) NOT NULL COMMENT '实收金额',
    payment_method VARCHAR(20) NOT NULL COMMENT '支付方式',
    issued_by VARCHAR(50) NOT NULL COMMENT '开票人',
    issued_at DATETIME NOT NULL COMMENT '开票时间',
    status VARCHAR(20) NOT NULL DEFAULT 'VALID' COMMENT '收据状态',
    notes TEXT COMMENT '备注',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    created_by VARCHAR(50) COMMENT '创建人',
    updated_by VARCHAR(50) COMMENT '更新人',
    
    INDEX idx_payment_id (payment_id),
    INDEX idx_receipt_number (receipt_number),
    INDEX idx_patient_id (patient_id),
    INDEX idx_issued_at (issued_at),
    INDEX idx_status (status),
    
    FOREIGN KEY (payment_id) REFERENCES payments(id),
    FOREIGN KEY (patient_id) REFERENCES patients(id)
) COMMENT='收据表';