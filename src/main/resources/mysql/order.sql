CREATE TABLE IF NOT EXISTS product (
    product_id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY COMMENT '产品ID',
    product_code VARCHAR(32) NOT NULL UNIQUE COMMENT '产品型号',
    product_name VARCHAR(64) NOT NULL COMMENT '产品名称',
    specifications JSON NOT NULL COMMENT '产品规格参数（JSON格式）',
    standard_cycle_time INT NOT NULL COMMENT '标准节拍（秒/件）',
    safety_stock INT COMMENT '安全库存量',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_product_code (product_code)
) ENGINE=InnoDB COMMENT='产品基础信息表';

CREATE TABLE IF NOT EXISTS production_order (
    order_id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY COMMENT '工单ID',
    order_no VARCHAR(32) NOT NULL UNIQUE COMMENT '工单号（规则：YYMMDD+4位流水）',
    rework_of BIGINT UNSIGNED COMMENT '原工单ID（返工专用）',
    product_id BIGINT UNSIGNED NOT NULL COMMENT '产品ID',
    order_quantity INT NOT NULL COMMENT '计划数量',
    completed_quantity INT DEFAULT 0 COMMENT '已完成数量',
    defective_quantity INT DEFAULT 0 COMMENT '不良品数量',
    priority TINYINT NOT NULL DEFAULT 2 COMMENT '优先级：1-紧急 2-高 3-普通 4-低',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态：1-待排程 2-已排程 3-生产中 4-暂停 5-已完成 6-已关闭',
    planned_start_date DATE COMMENT '计划开始日期',
    planned_end_date DATE COMMENT '计划完成日期',
    actual_start_date DATETIME COMMENT '实际开始时间',
    actual_end_date DATETIME COMMENT '实际完成时间',
    production_line VARCHAR(32) COMMENT '生产线代码',
    creator BIGINT UNSIGNED NOT NULL COMMENT '创建人',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (product_id) REFERENCES product(product_id),
    INDEX idx_order_status (status),
    INDEX idx_planned_date (planned_start_date),
    INDEX idx_rework (rework_of)
) ENGINE=InnoDB COMMENT='生产工单主表';

CREATE TABLE IF NOT EXISTS process_definition (
    process_id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY COMMENT '工序ID',
    process_code VARCHAR(32) NOT NULL UNIQUE COMMENT '工序编码',
    process_name VARCHAR(64) NOT NULL COMMENT '工序名称',
    product_id BIGINT UNSIGNED NOT NULL COMMENT '关联产品ID',
    sequence INT NOT NULL COMMENT '工序顺序',
    equipment_type INT UNSIGNED COMMENT '适用设备类型',
    standard_time INT NOT NULL COMMENT '标准工时（秒）',
    quality_checkpoints JSON COMMENT '质量检测点配置',
    previous_process BIGINT UNSIGNED COMMENT '前道工序ID',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    FOREIGN KEY (product_id) REFERENCES product(product_id),
    FOREIGN KEY (equipment_type) REFERENCES equipment_type(type_id),
    UNIQUE idx_process_sequence (product_id, sequence)
) ENGINE=InnoDB COMMENT='产品工序定义表';

CREATE TABLE IF NOT EXISTS production_schedule (
    schedule_id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY COMMENT '排程ID',
    order_id BIGINT UNSIGNED NOT NULL COMMENT '工单ID',
    process_id BIGINT UNSIGNED NOT NULL COMMENT '工序ID',
    equipment_id BIGINT UNSIGNED NOT NULL COMMENT '设备ID',
    planned_start_time DATETIME NOT NULL COMMENT '计划开始时间',
    planned_end_time DATETIME NOT NULL COMMENT '计划结束时间',
    actual_start_time DATETIME COMMENT '实际开始时间',
    actual_end_time DATETIME COMMENT '实际结束时间',
    schedule_status TINYINT NOT NULL DEFAULT 1 COMMENT '状态：1-待执行 2-执行中 3-已完成 4-已延迟',
    operator BIGINT UNSIGNED COMMENT '操作人员',
    FOREIGN KEY (order_id) REFERENCES production_order(order_id),
    FOREIGN KEY (process_id) REFERENCES process_definition(process_id),
    FOREIGN KEY (equipment_id) REFERENCES equipment(equipment_id),
    INDEX idx_schedule_time (planned_start_time, planned_end_time),
    INDEX idx_schedule_status (schedule_status)
) ENGINE=InnoDB COMMENT='生产排程计划表';

CREATE TABLE IF NOT EXISTS production_record (
    record_id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY COMMENT '记录ID',
    order_id BIGINT UNSIGNED NOT NULL COMMENT '工单ID',
    process_id BIGINT UNSIGNED NOT NULL COMMENT '工序ID',
    equipment_id BIGINT UNSIGNED NOT NULL COMMENT '设备ID',
    output_quantity INT NOT NULL COMMENT '产出数量',
    defective_quantity INT DEFAULT 0 COMMENT '不良数量',
    quality_check_generated TINYINT NOT NULL COMMENT '质检任务生成标记：0-未生成 1-已生成',
    start_time DATETIME NOT NULL COMMENT '开始时间',
    end_time DATETIME NOT NULL COMMENT '结束时间',
    operator BIGINT UNSIGNED NOT NULL COMMENT '操作员',
    remark VARCHAR(255) COMMENT '异常备注',
    FOREIGN KEY (order_id) REFERENCES production_order(order_id),
    INDEX idx_production_time (start_time, end_time)
) ENGINE=InnoDB COMMENT='生产执行记录表';

CREATE TABLE IF NOT EXISTS quality_inspection_item (
    item_id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY COMMENT '质检项ID',
    product_id BIGINT UNSIGNED NOT NULL COMMENT '产品ID',
    inspection_name VARCHAR(64) NOT NULL COMMENT '检测项目名称',
    inspection_standard VARCHAR(255) NOT NULL COMMENT '检测标准',
    sampling_method VARCHAR(50) COMMENT '抽检方式（全检/抽检）',
    acceptance_criteria JSON NOT NULL COMMENT '合格标准（JSON格式）',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    FOREIGN KEY (product_id) REFERENCES product(product_id),
INDEX idx_product_inspection (product_id)
) ENGINE=InnoDB COMMENT='质量检测项目表';

CREATE TABLE IF NOT EXISTS quality_inspection_record (
    inspection_id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY COMMENT '质检记录ID',
    order_id BIGINT UNSIGNED NOT NULL COMMENT '工单ID',
    item_id BIGINT UNSIGNED NOT NULL COMMENT '质检项ID',
    record_id BIGINT UNSIGNED NOT NULL COMMENT '生产记录ID',
    inspection_result TINYINT NOT NULL COMMENT '检测结果：1-合格 2-不合格 3-待复检',
    inspection_data JSON COMMENT '检测数据记录',
    inspector BIGINT UNSIGNED NOT NULL DEFAULT 1 COMMENT '检验员',
    inspection_time DATETIME NOT NULL DEFAULT '1970-01-01 00:00:00' COMMENT '检验时间',
    remark VARCHAR(255) COMMENT '备注说明',
    FOREIGN KEY (order_id) REFERENCES production_order(order_id),
    FOREIGN KEY (item_id) REFERENCES quality_inspection_item(item_id),
    FOREIGN KEY (record_id) REFERENCES production_record(record_id),
    INDEX idx_inspection_time (inspection_time)
) ENGINE=InnoDB COMMENT='质量检测记录表';

-- 插入产品数据（3个不同产品）
INSERT INTO product (product_code, product_name, specifications, standard_cycle_time, safety_stock) VALUES
    ('A-1001', '智能控制器', '{"材质":"铝合金","尺寸":"120×80×30mm","电压":"24VDC"}', 85, 500),
    ('B-2002', '伺服电机', '{"功率":"750W","扭矩":"2.4N·m","防护等级":"IP65"}', 120, 300),
    ('C-3003', '工业机器人臂', '{"负载":"5kg","重复精度":"±0.02mm","轴数":6}', 360, 50),
    ('D-4004', '精密齿轮箱', '{"材质":"合金钢","传动比":"10:1","扭矩":"50N·m"}', 240, 200),
    ('E-5005', '工业传感器', '{"测量范围":"0-100mm","精度":"±0.1mm","输出信号":"4-20mA"}', 150, 100);

# -- 插入工序定义（每个产品3道工序）
# INSERT INTO process_definition (process_code, process_name, product_id, sequence, equipment_type, standard_time, quality_checkpoints) VALUES
# -- 产品1（A-1001）
# ('PRC-001', '压铸成型', 1, 1, 1, 45, '["外观检查"]'),
# ('PRC-002', '电路板组装', 1, 2, 3, 120, '["导通测试"]'),
# ('PRC-003', '气密测试', 1, 3, 2, 60, '["气密性测试"]'),
#
# -- 产品2（B-2002）
# ('PRC-004', '定子绕线', 2, 1, 4, 180, '["绝缘测试"]'),
# ('PRC-005', '转子动平衡', 2, 2, 3, 90, '["空载电流"]'),
# ('PRC-006', '整机装配', 2, 3, 2, 150, '["振动测试"]'),
#
# -- 产品3（C-3003）
# ('PRC-007', '机械臂铸造', 3, 1, 1, 240, '["尺寸精度"]'),
# ('PRC-008', '精密加工', 3, 2, 3, 300, '["负载测试"]'),
# ('PRC-009', '关节组装', 3, 3, 2, 180, '["耐久测试"]'),
#
# -- 产品4（D-4004）工序
# ('PRC-010', '齿轮加工', 4, 1, 3, 180, '["齿形精度检测"]'),
# ('PRC-011', '箱体组装', 4, 2, 2, 120, '["密封性测试"]'),
# ('PRC-012', '性能测试', 4, 3, 5, 240, '["负载运行测试"]'),
#
# -- 产品5（E-5005）工序
# ('PRC-013', '芯片焊接', 5, 1, 2, 90, '["焊点检测"]'),
# ('PRC-014', '模块封装', 5, 2, 4, 60, '["外观检查"]'),
# ('PRC-015', '功能校验', 5, 3, 3, 120, '["信号稳定性测试"]');

-- 插入工序定义（每个产品3道工序）
INSERT INTO process_definition (process_code, process_name, product_id, sequence, equipment_type, standard_time, quality_checkpoints) VALUES
-- 产品1（A-1001）
('PRC-001', '压铸成型', 1, 1, 1, 1, '["外观检查"]'),
('PRC-002', '电路板组装', 1, 2, 3, 1, '["导通测试"]'),
('PRC-003', '气密测试', 1, 3, 2, 1, '["气密性测试"]'),

-- 产品2（B-2002）
('PRC-004', '定子绕线', 2, 1, 4, 1, '["绝缘测试"]'),
('PRC-005', '转子动平衡', 2, 2, 3, 1, '["空载电流"]'),
('PRC-006', '整机装配', 2, 3, 2, 1, '["振动测试"]'),

-- 产品3（C-3003）
('PRC-007', '机械臂铸造', 3, 1, 1, 1, '["尺寸精度"]'),
('PRC-008', '精密加工', 3, 2, 3, 1, '["负载测试"]'),
('PRC-009', '关节组装', 3, 3, 2, 1, '["耐久测试"]'),

-- 产品4（D-4004）工序
('PRC-010', '齿轮加工', 4, 1, 3, 1, '["齿形精度检测"]'),
('PRC-011', '箱体组装', 4, 2, 2, 1, '["密封性测试"]'),
('PRC-012', '性能测试', 4, 3, 5, 1, '["负载运行测试"]'),

-- 产品5（E-5005）工序
('PRC-013', '芯片焊接', 5, 1, 2, 1, '["焊点检测"]'),
('PRC-014', '模块封装', 5, 2, 4, 1, '["外观检查"]'),
('PRC-015', '功能校验', 5, 3, 3, 1, '["信号稳定性测试"]');

-- 插入生产工单（覆盖所有状态）
INSERT INTO production_order (order_no, product_id, order_quantity, completed_quantity, defective_quantity,
                              priority, status, planned_start_date, planned_end_date, actual_start_date, actual_end_date, production_line, creator) VALUES
-- 产品1的工单（3个不同状态）
('2401010001', 1, 1000, 800, 10, 1, 3, '2024-01-05', '2024-01-10', '2024-01-05 08:30:00', NULL, 'A-Line', 14),
('2401150002', 1, 2000, 2000, 25, 2, 5, '2024-01-15', '2024-01-20', '2024-01-15 09:00:00', '2024-01-19 17:00:00', 'B-Line', 15),
('2401200003', 1, 500, 0, 0, 4, 1, '2024-01-25', '2024-01-30', NULL, NULL, 'C-Line', 14),

-- 产品2的工单（包含暂停状态）
('2401080004', 2, 800, 600, 8, 2, 4, '2024-01-08', '2024-01-12', '2024-01-08 09:15:00', NULL, 'D-Line', 15),
('2401180005', 2, 1500, 1500, 15, 3, 6, '2024-01-18', '2024-01-25', '2024-01-18 08:45:00', '2024-01-24 16:30:00', 'E-Line', 14),

-- 产品3的工单（紧急订单）
('2401100006', 3, 200, 200, 2, 1, 5, '2024-01-10', '2024-01-15', '2024-01-10 07:30:00', '2024-01-14 20:00:00', 'F-Line', 15),

-- 产品4的工单（紧急优先级）
('2505070007', 4, 500, 300, 5, 1, 3, '2025-05-07', '2025-05-10', '2025-05-07 09:00:00', NULL, 'G-Line', 14),
-- 产品5的工单（暂停状态）
('2505070008', 5, 300, 150, 3, 2, 4, '2025-05-07', '2025-05-09', '2025-05-07 10:00:00', NULL, 'H-Line', 15);

INSERT INTO production_order (order_no, product_id, order_quantity, completed_quantity, defective_quantity,
                                  priority, status, planned_start_date, planned_end_date, actual_start_date, actual_end_date, production_line, creator, rework_of) VALUES
-- 产品3的返工工单
('2505070009', 3, 100, 0, 0, 3, 1, '2025-05-12', '2025-05-15', NULL, NULL, 'F-Line', 14, 6);  -- rework_of=6（原工单6）

-- 插入生产排程（与工单关联）
INSERT INTO production_schedule (order_id, process_id, equipment_id, planned_start_time, planned_end_time,
                                 actual_start_time, actual_end_time, schedule_status, operator) VALUES
-- 工单1（进行中）
(1, 1, 1, '2024-01-05 08:30:00', '2024-01-05 16:00:00', '2024-01-05 08:32:00', '2024-01-05 15:55:00', 3, 14),
(1, 2, 3, '2024-01-06 08:00:00', '2024-01-07 17:00:00', '2024-01-06 08:05:00', NULL, 2, 15),
(1, 3, 2, '2024-01-08 08:00:00', '2024-01-08 12:00:00', NULL, NULL, 1, 14),

-- 工单2（已完成）
(2, 1, 1, '2024-01-15 09:00:00', '2024-01-16 18:00:00', '2024-01-15 09:05:00', '2024-01-16 17:50:00', 3, 15),
(2, 2, 3, '2024-01-17 08:30:00', '2024-01-18 12:00:00', '2024-01-17 08:35:00', '2024-01-18 11:45:00', 3, 14),
(2, 3, 2, '2024-01-19 08:00:00', '2024-01-19 15:00:00', '2024-01-19 08:10:00', '2024-01-19 14:55:00', 3, 15),

-- 工单4（暂停中）
(4, 4, 4, '2024-01-08 09:15:00', '2024-01-09 17:00:00', '2024-01-08 09:20:00', '2024-01-09 16:30:00', 3, 14),
(4, 5, 3, '2024-01-10 08:00:00', '2024-01-11 12:00:00', '2024-01-10 08:05:00', NULL, 4, 15),

-- 工单7（产品4）工序1排程
(7, 10, 6, '2025-05-07 09:00:00', '2025-05-07 16:00:00', '2025-05-07 09:10:00', NULL, 2, 14),
-- 工单8（产品5）工序2排程（延迟）
(8, 14, 3, '2025-05-07 13:00:00', '2025-05-07 15:00:00', '2025-05-07 13:30:00', NULL, 4, 15),
-- 返工工单9（产品3）工序7排程
(9, 7, 1, '2025-05-12 08:00:00', '2025-05-12 16:00:00', NULL, NULL, 1, 14);

-- 插入质检项目（每个产品3个检测项）
INSERT INTO quality_inspection_item (product_id, inspection_name, inspection_standard, sampling_method, acceptance_criteria) VALUES
-- 产品1
(1, '外观检查', '表面无划痕、无毛刺', '全检', '{"合格标准":"无可见缺陷"}'),
(1, '导通测试', '电路板导通性能', '抽检(5%)', '{"电阻值":"<0.5Ω","电压偏差":"±5%"}'),
(1, '气密性测试', '压力保持能力', '抽检(10%)', '{"测试压力":"0.5MPa","泄漏率":"<0.5Pa/min"}'),

-- 产品2
(2, '绝缘测试', '绕组绝缘性能', '全检', '{"测试电压":"1500V","持续时间":"60s"}'),
(2, '空载电流', '电机空载运行电流', '抽检(20%)', '{"额定电流":"3.2A","允许偏差":"±10%"}'),
(2, '振动测试', '运行平稳性检测', '抽检(5%)', '{"振动幅度":"<0.1mm","频率":"50Hz±2%"}'),

-- 产品3
(3, '尺寸精度', '关键尺寸检测', '全检', '{"公差范围":"±0.02mm"}'),
(3, '负载测试', '额定负载运行', '抽检(10%)', '{"偏移量":"<0.05mm","重复精度":"±0.01mm"}'),
(3, '耐久测试', '连续运行测试', '抽检(2%)', '{"测试时长":"72h","故障次数":"≤2次"}'),

-- 产品4新增检测项
(4, '齿面粗糙度', 'Ra≤0.8μm', '抽检(10%)', '{"检测工具":"粗糙度仪","合格标准":"Ra<0.8μm"}'),
-- 产品5新增检测项
(5, '信号响应时间', '≤5ms', '全检', '{"测试电压":"24VDC","合格标准":"响应时间<5ms"}');

-- 插入生产记录
INSERT INTO production_record (order_id, process_id, equipment_id, output_quantity, defective_quantity, quality_check_generated, start_time, end_time, operator) VALUES
-- 工单1（产品1）的工序1完成记录
(1, 1, 1, 800, 10, 1, '2024-01-05 08:30:00', '2024-01-05 15:55:00', 14),
-- 工单1的工序2完成记录
(1, 2, 3, 800, 5, 1, '2024-01-06 08:05:00', '2024-01-07 17:00:00', 15),
-- 工单1的工序3完成记录
(1, 3, 2, 800, 3, 1, '2024-01-08 08:00:00', '2024-01-08 12:00:00', 14),
-- 工单2（产品1）的工序1完成记录
(2, 1, 1, 2000, 25, 1, '2024-01-15 09:05:00', '2024-01-16 17:50:00', 15),
-- 工单2的工序2完成记录
(2, 2, 3, 2000, 10, 1, '2024-01-17 08:35:00', '2024-01-18 11:45:00', 14),
-- 工单2的工序3完成记录
(2, 3, 2, 2000, 5, 1, '2024-01-19 08:10:00', '2024-01-19 14:55:00', 15),
-- 工单4（产品2）的工序4完成记录
(4, 4, 4, 600, 8, 1, '2024-01-08 09:20:00', '2024-01-09 16:30:00', 14),
-- 工单6（产品3）的工序7完成记录
(6, 7, 1, 200, 2, 1, '2024-01-10 07:30:00', '2024-01-12 18:00:00', 15),
-- 工单6的工序8完成记录
(6, 8, 3, 200, 1, 1, '2024-01-13 08:00:00', '2024-01-14 12:00:00', 14),
-- 工单6的工序9完成记录
(6, 9, 2, 200, 1, 1, '2024-01-14 13:00:00', '2024-01-14 20:00:00', 15),
-- 工单7工序1执行记录
(7, 10, 6, 300, 5, 1, '2025-05-07 09:10:00', '2025-05-07 15:30:00', 14),
-- 工单8工序1执行记录
(8, 13, 2, 150, 3, 1, '2025-05-07 10:00:00', '2025-05-07 12:30:00', 15),
-- 工单6（产品3）工序8补录记录（假设之前漏记）
(6, 8, 3, 200, 1, 1, '2024-01-13 08:00:00', '2024-01-14 12:00:00', 14);

-- 插入质检记录（修正后的item_id）
INSERT INTO quality_inspection_record (order_id, item_id, record_id, inspection_result, inspector, inspection_time) VALUES
-- 工单1的工序1质检（外观检查，item_id=1）
(1, 1, 1, 1, 18, '2024-01-05 16:00:00'),
-- 工单1的工序2质检（导通测试，item_id=2）
(1, 2, 2, 1, 19, '2024-01-07 17:10:00'),
-- 工单1的工序3质检（气密性测试，item_id=3）
(1, 3, 3, 1, 18, '2024-01-08 12:30:00'),
-- 工单2的工序1质检（外观检查，item_id=1）
(2, 1, 4, 1, 19, '2024-01-16 18:00:00'),
-- 工单2的工序2质检（导通测试，item_id=2）
(2, 2, 5, 1, 18, '2024-01-18 12:00:00'),
-- 工单2的工序3质检（气密性测试，item_id=3）
(2, 3, 6, 1, 19, '2024-01-19 15:30:00'),
-- 工单4的工序4质检（绝缘测试，item_id=4）
(4, 4, 7, 1, 18, '2024-01-09 17:00:00'),
-- 工单6的工序7质检（尺寸精度，item_id=7）
(6, 7, 8, 1, 19, '2024-01-12 18:30:00'),
-- 工单6的工序8质检（负载测试，item_id=8）
(6, 8, 9, 1, 18, '2024-01-14 12:30:00'),
-- 工单6的工序9质检（耐久测试，item_id=9）
(6, 9, 10, 1, 19, '2024-01-14 20:30:00'),
-- 工单7工序1质检（齿形精度检测，item_id=10）
(7, 10, 11, 1, 18, '2025-05-07 16:00:00'),
-- 工单8工序1质检（信号响应时间，item_id=11）
(8, 11, 12, 2, 19, '2025-05-07 13:00:00'), -- 不合格需要复检
-- 返工工单9工序7质检（尺寸精度，item_id=7）
(9, 7, 13, 3, 18, '2025-05-12 16:30:00'); -- 待复检


