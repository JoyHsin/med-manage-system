-- 创建患者队列表
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
    
    FOREIGN KEY (patient_id) REFERENCES patient(id),
    FOREIGN KEY (registration_id) REFERENCES registration(id),
    FOREIGN KEY (called_by) REFERENCES user(id),
    FOREIGN KEY (confirmed_by) REFERENCES user(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='患者队列表';