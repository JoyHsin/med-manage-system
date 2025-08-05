-- 集成测试基础数据初始化脚本

-- 清理现有数据
DELETE FROM user_role;
DELETE FROM role_permission;
DELETE FROM dispense_item;
DELETE FROM dispense_record;
DELETE FROM prescription_item;
DELETE FROM prescription;
DELETE FROM bill_item;
DELETE FROM bill;
DELETE FROM medical_order;
DELETE FROM vital_signs;
DELETE FROM patient_queue;
DELETE FROM diagnosis;
DELETE FROM medical_record;
DELETE FROM registration;
DELETE FROM appointment;
DELETE FROM stock_transaction;
DELETE FROM inventory_level;
DELETE FROM medicine;
DELETE FROM allergy_history;
DELETE FROM medical_history;
DELETE FROM patient;
DELETE FROM schedule;
DELETE FROM staff;
DELETE FROM user;
DELETE FROM permission;
DELETE FROM role;

-- 重置自增ID
ALTER SEQUENCE role_seq RESTART WITH 1;
ALTER SEQUENCE permission_seq RESTART WITH 1;
ALTER SEQUENCE user_seq RESTART WITH 1;
ALTER SEQUENCE staff_seq RESTART WITH 1;
ALTER SEQUENCE patient_seq RESTART WITH 1;
ALTER SEQUENCE medicine_seq RESTART WITH 1;
ALTER SEQUENCE appointment_seq RESTART WITH 1;
ALTER SEQUENCE registration_seq RESTART WITH 1;
ALTER SEQUENCE medical_record_seq RESTART WITH 1;
ALTER SEQUENCE prescription_seq RESTART WITH 1;
ALTER SEQUENCE bill_seq RESTART WITH 1;
ALTER SEQUENCE patient_queue_seq RESTART WITH 1;
ALTER SEQUENCE vital_signs_seq RESTART WITH 1;
ALTER SEQUENCE medical_order_seq RESTART WITH 1;

-- 插入角色数据
INSERT INTO role (id, name, description, created_at) VALUES
(1, 'ADMIN', '系统管理员', CURRENT_TIMESTAMP),
(2, 'DOCTOR', '医生', CURRENT_TIMESTAMP),
(3, 'NURSE', '护士', CURRENT_TIMESTAMP),
(4, 'PHARMACIST', '药剂师', CURRENT_TIMESTAMP),
(5, 'RECEPTIONIST', '前台接待', CURRENT_TIMESTAMP);

-- 插入权限数据
INSERT INTO permission (id, name, description, module) VALUES
-- 系统管理权限
(1, 'SYSTEM_ADMIN', '系统管理', 'SYSTEM'),
(2, 'USER_MANAGEMENT', '用户管理', 'SYSTEM'),
(3, 'ROLE_MANAGEMENT', '角色管理', 'SYSTEM'),

-- 患者管理权限
(4, 'PATIENT_READ', '患者信息查看', 'PATIENT'),
(5, 'PATIENT_WRITE', '患者信息编辑', 'PATIENT'),
(6, 'PATIENT_CREATE', '患者信息创建', 'PATIENT'),

-- 医疗功能权限
(7, 'MEDICAL_RECORD_READ', '病历查看', 'MEDICAL'),
(8, 'MEDICAL_RECORD_WRITE', '病历编辑', 'MEDICAL'),
(9, 'PRESCRIPTION_CREATE', '处方开具', 'MEDICAL'),
(10, 'PRESCRIPTION_READ', '处方查看', 'MEDICAL'),

-- 护士功能权限
(11, 'TRIAGE_MANAGEMENT', '分诊管理', 'NURSE'),
(12, 'VITAL_SIGNS_RECORD', '生命体征录入', 'NURSE'),
(13, 'MEDICAL_ORDER_EXECUTE', '医嘱执行', 'NURSE'),

-- 药房功能权限
(14, 'PHARMACY_MANAGEMENT', '药房管理', 'PHARMACY'),
(15, 'PRESCRIPTION_DISPENSE', '处方调剂', 'PHARMACY'),
(16, 'STOCK_MANAGEMENT', '库存管理', 'PHARMACY'),

-- 财务功能权限
(17, 'BILLING_MANAGEMENT', '费用管理', 'BILLING'),
(18, 'PAYMENT_PROCESS', '收费处理', 'BILLING'),
(19, 'FINANCIAL_REPORT', '财务报表', 'BILLING');

-- 插入角色权限关联
INSERT INTO role_permission (role_id, permission_id) VALUES
-- 管理员权限（所有权限）
(1, 1), (1, 2), (1, 3), (1, 4), (1, 5), (1, 6), (1, 7), (1, 8), (1, 9), (1, 10),
(1, 11), (1, 12), (1, 13), (1, 14), (1, 15), (1, 16), (1, 17), (1, 18), (1, 19),

-- 医生权限
(2, 4), (2, 5), (2, 6), (2, 7), (2, 8), (2, 9), (2, 10),

-- 护士权限
(3, 4), (3, 5), (3, 11), (3, 12), (3, 13),

-- 药剂师权限
(4, 10), (4, 14), (4, 15), (4, 16),

-- 前台权限
(5, 4), (5, 5), (5, 6), (5, 17), (5, 18);

-- 插入测试用户
INSERT INTO user (id, username, password, full_name, email, phone, enabled, created_at) VALUES
(1, 'admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa', '系统管理员', 'admin@clinic.com', '13800000001', true, CURRENT_TIMESTAMP),
(2, 'doctor001', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa', '张医生', 'doctor@clinic.com', '13800000002', true, CURRENT_TIMESTAMP),
(3, 'nurse001', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa', '李护士', 'nurse@clinic.com', '13800000003', true, CURRENT_TIMESTAMP),
(4, 'pharmacist001', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa', '王药师', 'pharmacist@clinic.com', '13800000004', true, CURRENT_TIMESTAMP),
(5, 'receptionist001', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa', '赵前台', 'receptionist@clinic.com', '13800000005', true, CURRENT_TIMESTAMP);

-- 插入用户角色关联
INSERT INTO user_role (user_id, role_id) VALUES
(1, 1), -- admin -> ADMIN
(2, 2), -- doctor001 -> DOCTOR
(3, 3), -- nurse001 -> NURSE
(4, 4), -- pharmacist001 -> PHARMACIST
(5, 5); -- receptionist001 -> RECEPTIONIST

-- 插入员工信息
INSERT INTO staff (id, staff_number, name, department, position, qualification, phone, email, hire_date, active) VALUES
(1, 'ADM001', '系统管理员', '管理部', '系统管理员', '管理资格', '13800000001', 'admin@clinic.com', '2020-01-01', true),
(2, 'DOC001', '张医生', '内科', '主治医师', '执业医师', '13800000002', 'doctor@clinic.com', '2021-01-01', true),
(3, 'NUR001', '李护士', '护理部', '主管护师', '护士执业证', '13800000003', 'nurse@clinic.com', '2021-06-01', true),
(4, 'PHA001', '王药师', '药房', '主管药师', '执业药师', '13800000004', 'pharmacist@clinic.com', '2020-06-01', true),
(5, 'REC001', '赵前台', '前台', '接待员', '岗位证书', '13800000005', 'receptionist@clinic.com', '2022-01-01', true);

-- 插入测试药品
INSERT INTO medicine (id, medicine_code, name, specification, manufacturer, batch_number, expiry_date, unit_price, stock_quantity, min_stock_level) VALUES
(1, 'MED001', '阿莫西林胶囊', '0.25g*24粒', '华北制药', '20240101', '2026-01-01', 15.50, 1000, 100),
(2, 'MED002', '布洛芬片', '0.2g*20片', '扬子江药业', '20240102', '2027-01-02', 8.80, 500, 50),
(3, 'MED003', '维生素C片', '0.1g*100片', '石药集团', '20240103', '2026-01-03', 12.00, 800, 80),
(4, 'MED004', '感冒灵颗粒', '10g*12袋', '同仁堂', '20240104', '2025-12-31', 25.00, 300, 30),
(5, 'MED005', '头孢克肟胶囊', '0.1g*12粒', '齐鲁制药', '20240105', '2026-06-30', 32.50, 200, 20);

-- 插入库存记录
INSERT INTO inventory_level (id, medicine_id, current_stock, min_level, max_level, last_updated) VALUES
(1, 1, 1000, 100, 2000, CURRENT_TIMESTAMP),
(2, 2, 500, 50, 1000, CURRENT_TIMESTAMP),
(3, 3, 800, 80, 1500, CURRENT_TIMESTAMP),
(4, 4, 300, 30, 600, CURRENT_TIMESTAMP),
(5, 5, 200, 20, 400, CURRENT_TIMESTAMP);

-- 插入测试患者
INSERT INTO patient (id, patient_number, name, phone, id_card, birth_date, gender, address, created_at) VALUES
(1, 'P000001', '测试患者1', '13800001001', '110101199001011001', '1990-01-01', '男', '北京市朝阳区测试街道1号', CURRENT_TIMESTAMP),
(2, 'P000002', '测试患者2', '13800001002', '110101199002021002', '1990-02-02', '女', '北京市海淀区测试街道2号', CURRENT_TIMESTAMP),
(3, 'P000003', '测试患者3', '13800001003', '110101199003031003', '1990-03-03', '男', '北京市西城区测试街道3号', CURRENT_TIMESTAMP);

-- 插入测试预约
INSERT INTO appointment (id, patient_id, doctor_id, appointment_time, appointment_type, status, notes, created_at) VALUES
(1, 1, 2, DATEADD('HOUR', 1, CURRENT_TIMESTAMP), '普通门诊', '已预约', '常规检查', CURRENT_TIMESTAMP),
(2, 2, 2, DATEADD('HOUR', 2, CURRENT_TIMESTAMP), '专家门诊', '已预约', '复查', CURRENT_TIMESTAMP);

-- 插入测试挂号
INSERT INTO registration (id, patient_id, appointment_id, registration_number, registration_date, department, status, registration_fee, created_at) VALUES
(1, 1, 1, 'REG20240101001', CURRENT_DATE, '内科', '已挂号', 10.00, CURRENT_TIMESTAMP),
(2, 2, 2, 'REG20240101002', CURRENT_DATE, '内科', '已挂号', 15.00, CURRENT_TIMESTAMP);

-- 插入患者队列
INSERT INTO patient_queue (id, patient_id, queue_date, queue_number, status) VALUES
(1, 1, CURRENT_DATE, 1, '等待叫号'),
(2, 2, CURRENT_DATE, 2, '等待叫号'),
(3, 3, CURRENT_DATE, 3, '等待叫号');

-- 提交事务
COMMIT;