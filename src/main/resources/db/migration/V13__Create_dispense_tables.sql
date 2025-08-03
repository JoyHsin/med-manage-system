-- 创建处方调剂记录表
CREATE TABLE dispense_records (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    prescription_id BIGINT NOT NULL,
    prescription_number VARCHAR(50) NOT NULL,
    patient_id BIGINT NOT NULL,
    patient_name VARCHAR(100) NOT NULL,
    pharmacist_id BIGINT NOT NULL,
    pharmacist_name VARCHAR(100) NOT NULL,
    dispensing_pharmacist_id BIGINT,
    dispensing_pharmacist_name VARCHAR(100),
    status VARCHAR(20) NOT NULL DEFAULT '待调剂',
    dispense_start_time DATETIME,
    dispense_completed_time DATETIME,
    delivered_time DATETIME,
    total_amount DECIMAL(10,2),
    actual_amount DECIMAL(10,2),
    validation_result VARCHAR(20),
    stock_check_result VARCHAR(20),
    drug_interaction_check TEXT,
    allergy_check TEXT,
    dispense_notes TEXT,
    delivery_notes TEXT,
    medication_guidance TEXT,
    special_precautions TEXT,
    requires_refrigeration BOOLEAN DEFAULT FALSE,
    requires_special_packaging BOOLEAN DEFAULT FALSE,
    packaging_specification VARCHAR(200),
    return_reason VARCHAR(500),
    returned_time DATETIME,
    cancel_reason VARCHAR(500),
    cancelled_time DATETIME,
    quality_check_result VARCHAR(20) DEFAULT '合格',
    quality_check_notes VARCHAR(500),
    review_pharmacist_id BIGINT,
    review_pharmacist_name VARCHAR(100),
    reviewed_time DATETIME,
    review_comments VARCHAR(500),
    remarks TEXT,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE,
    
    INDEX idx_prescription_id (prescription_id),
    INDEX idx_patient_id (patient_id),
    INDEX idx_pharmacist_id (pharmacist_id),
    INDEX idx_status (status),
    INDEX idx_dispense_start_time (dispense_start_time),
    INDEX idx_delivered_time (delivered_time),
    
    CONSTRAINT fk_dispense_records_prescription 
        FOREIGN KEY (prescription_id) REFERENCES prescriptions(id),
    CONSTRAINT fk_dispense_records_patient 
        FOREIGN KEY (patient_id) REFERENCES patients(id),
    CONSTRAINT fk_dispense_records_pharmacist 
        FOREIGN KEY (pharmacist_id) REFERENCES users(id),
    CONSTRAINT fk_dispense_records_dispensing_pharmacist 
        FOREIGN KEY (dispensing_pharmacist_id) REFERENCES users(id),
    CONSTRAINT fk_dispense_records_review_pharmacist 
        FOREIGN KEY (review_pharmacist_id) REFERENCES users(id)
);

-- 创建调剂项目明细表
CREATE TABLE dispense_items (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    dispense_record_id BIGINT NOT NULL,
    prescription_item_id BIGINT NOT NULL,
    medicine_id BIGINT NOT NULL,
    medicine_name VARCHAR(200) NOT NULL,
    specification VARCHAR(100),
    batch_number VARCHAR(50),
    production_date DATE,
    expiry_date DATE,
    prescribed_quantity INT NOT NULL,
    dispensed_quantity INT,
    unit VARCHAR(20) NOT NULL,
    unit_price DECIMAL(8,2) NOT NULL,
    subtotal DECIMAL(10,2),
    status VARCHAR(20) NOT NULL DEFAULT '待调剂',
    stock_status VARCHAR(20),
    stock_before_dispense INT,
    stock_after_dispense INT,
    is_substitute BOOLEAN DEFAULT FALSE,
    original_medicine_id BIGINT,
    original_medicine_name VARCHAR(200),
    substitute_reason VARCHAR(200),
    usage VARCHAR(200),
    dosage VARCHAR(100),
    frequency VARCHAR(50),
    duration INT,
    special_instructions TEXT,
    dispense_notes TEXT,
    quality_check_result VARCHAR(20) DEFAULT '合格',
    quality_issues TEXT,
    dispensed_time DATETIME,
    dispensed_by BIGINT,
    dispensed_by_name VARCHAR(100),
    reviewed_by BIGINT,
    reviewed_by_name VARCHAR(100),
    reviewed_time DATETIME,
    review_comments TEXT,
    return_reason VARCHAR(200),
    returned_time DATETIME,
    sort_order INT DEFAULT 1,
    remarks TEXT,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE,
    
    INDEX idx_dispense_record_id (dispense_record_id),
    INDEX idx_prescription_item_id (prescription_item_id),
    INDEX idx_medicine_id (medicine_id),
    INDEX idx_status (status),
    INDEX idx_batch_number (batch_number),
    INDEX idx_expiry_date (expiry_date),
    INDEX idx_dispensed_time (dispensed_time),
    
    CONSTRAINT fk_dispense_items_dispense_record 
        FOREIGN KEY (dispense_record_id) REFERENCES dispense_records(id),
    CONSTRAINT fk_dispense_items_prescription_item 
        FOREIGN KEY (prescription_item_id) REFERENCES prescription_items(id),
    CONSTRAINT fk_dispense_items_medicine 
        FOREIGN KEY (medicine_id) REFERENCES medicines(id),
    CONSTRAINT fk_dispense_items_original_medicine 
        FOREIGN KEY (original_medicine_id) REFERENCES medicines(id),
    CONSTRAINT fk_dispense_items_dispensed_by 
        FOREIGN KEY (dispensed_by) REFERENCES users(id),
    CONSTRAINT fk_dispense_items_reviewed_by 
        FOREIGN KEY (reviewed_by) REFERENCES users(id)
);

-- 创建保险理赔表
CREATE TABLE insurance_claims (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    claim_number VARCHAR(50) NOT NULL UNIQUE,
    patient_id BIGINT NOT NULL,
    insurance_provider_id BIGINT NOT NULL,
    bill_id BIGINT NOT NULL,
    claim_amount DECIMAL(10,2) NOT NULL,
    approved_amount DECIMAL(10,2),
    status VARCHAR(20) NOT NULL DEFAULT '待提交',
    submitted_at DATETIME,
    processed_at DATETIME,
    paid_at DATETIME,
    rejection_reason VARCHAR(500),
    remarks TEXT,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE,
    
    INDEX idx_claim_number (claim_number),
    INDEX idx_patient_id (patient_id),
    INDEX idx_insurance_provider_id (insurance_provider_id),
    INDEX idx_bill_id (bill_id),
    INDEX idx_status (status),
    INDEX idx_submitted_at (submitted_at),
    
    CONSTRAINT fk_insurance_claims_patient 
        FOREIGN KEY (patient_id) REFERENCES patients(id),
    CONSTRAINT fk_insurance_claims_insurance_provider 
        FOREIGN KEY (insurance_provider_id) REFERENCES insurance_providers(id),
    CONSTRAINT fk_insurance_claims_bill 
        FOREIGN KEY (bill_id) REFERENCES bills(id)
);

-- 创建保险资格表
CREATE TABLE insurance_eligibilities (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    patient_id BIGINT NOT NULL,
    insurance_provider_id BIGINT NOT NULL,
    insurance_number VARCHAR(100) NOT NULL,
    insurance_type VARCHAR(50),
    effective_date DATE NOT NULL,
    expiry_date DATE,
    coverage_details TEXT,
    annual_limit DECIMAL(10,2),
    used_amount DECIMAL(10,2) DEFAULT 0.00,
    deductible DECIMAL(10,2) DEFAULT 0.00,
    copayment_rate DECIMAL(3,2) DEFAULT 0.00,
    status VARCHAR(20) NOT NULL DEFAULT '有效',
    verification_status VARCHAR(20) NOT NULL DEFAULT '未验证',
    remarks TEXT,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE,
    
    INDEX idx_patient_id (patient_id),
    INDEX idx_insurance_provider_id (insurance_provider_id),
    INDEX idx_insurance_number (insurance_number),
    INDEX idx_status (status),
    INDEX idx_effective_date (effective_date),
    INDEX idx_expiry_date (expiry_date),
    
    CONSTRAINT fk_insurance_eligibilities_patient 
        FOREIGN KEY (patient_id) REFERENCES patients(id),
    CONSTRAINT fk_insurance_eligibilities_insurance_provider 
        FOREIGN KEY (insurance_provider_id) REFERENCES insurance_providers(id),
    
    UNIQUE KEY uk_patient_provider (patient_id, insurance_provider_id)
);

-- 添加注释
ALTER TABLE dispense_records COMMENT = '处方调剂记录表';
ALTER TABLE dispense_items COMMENT = '调剂项目明细表';
ALTER TABLE insurance_claims COMMENT = '保险理赔表';
ALTER TABLE insurance_eligibilities COMMENT = '保险资格表';