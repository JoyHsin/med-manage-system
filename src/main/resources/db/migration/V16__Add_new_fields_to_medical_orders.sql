-- 添加新字段到 medical_orders 表
ALTER TABLE medical_orders
ADD COLUMN route VARCHAR(255) COMMENT '给药途径' AFTER order_type,
ADD COLUMN notes VARCHAR(500) COMMENT '备注' AFTER execution_notes,
ADD COLUMN price DECIMAL(10, 2) COMMENT '价格' AFTER content,
ADD COLUMN quantity DECIMAL(10, 2) COMMENT '数量' AFTER price,
ADD COLUMN unit VARCHAR(50) COMMENT '单位' AFTER quantity,
ADD COLUMN postponed_by BIGINT COMMENT '暂缓人ID' AFTER postpone_reason,
ADD COLUMN postponed_at DATETIME COMMENT '暂缓时间' AFTER postponed_by,
ADD COLUMN cancel_reason VARCHAR(500) COMMENT '取消原因' AFTER postponed_at,
ADD COLUMN cancelled_by BIGINT COMMENT '取消人ID' AFTER cancel_reason,
ADD COLUMN cancelled_at DATETIME COMMENT '取消时间' AFTER cancelled_by;

-- 添加外键约束
ALTER TABLE medical_orders
ADD CONSTRAINT fk_postponed_by FOREIGN KEY (postponed_by) REFERENCES users(id) ON DELETE RESTRICT,
ADD CONSTRAINT fk_cancelled_by FOREIGN KEY (cancelled_by) REFERENCES users(id) ON DELETE RESTRICT;