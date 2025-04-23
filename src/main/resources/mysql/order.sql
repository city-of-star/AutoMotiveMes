CREATE TABLE work_order (
    order_id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '工单ID',
    order_no VARCHAR(20) NOT NULL UNIQUE COMMENT '工单编号（例：WO202312001）',
    equipment_id BIGINT UNSIGNED  NOT NULL COMMENT '关联设备ID',
    order_type VARCHAR(20) COMMENT '类型（生产/维护/维修）',
    content TEXT COMMENT '工单内容',
    status VARCHAR(20) DEFAULT '待处理' COMMENT '状态（待处理/进行中/已完成）',
    creator_id BIGINT NOT NULL COMMENT '创建人',
    assignee_id BIGINT COMMENT '处理人',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    finish_time DATETIME COMMENT '完成时间',
    FOREIGN KEY (equipment_id) REFERENCES equipment(equipment_id),
    FOREIGN KEY (creator_id) REFERENCES sys_user(user_id),
    FOREIGN KEY (assignee_id) REFERENCES sys_user(user_id)
) ENGINE=InnoDB COMMENT='工单表';

CREATE TABLE scheduling_plan (
    plan_id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '计划ID',
    order_id BIGINT NOT NULL COMMENT '关联工单ID',
    equipment_id BIGINT UNSIGNED NOT NULL COMMENT '设备ID',
    plan_date DATE COMMENT '计划日期',
    start_time TIME COMMENT '开始时间',
    end_time TIME COMMENT '结束时间',
    progress INT DEFAULT 0 COMMENT '进度百分比',
    remarks VARCHAR(200) COMMENT '备注',
    FOREIGN KEY (order_id) REFERENCES work_order(order_id),
    FOREIGN KEY (equipment_id) REFERENCES equipment(equipment_id)
) ENGINE=InnoDB COMMENT='排程计划表';

CREATE TABLE maintenance_plan (
    plan_id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '计划ID',
    equipment_id BIGINT UNSIGNED NOT NULL COMMENT '设备ID',
    plan_date DATE COMMENT '计划日期',
    cycle_days INT COMMENT '维护周期（天）',
    last_date DATE COMMENT '上次维护日期',
    content VARCHAR(200) COMMENT '维护内容',
    FOREIGN KEY (equipment_id) REFERENCES equipment(equipment_id)
) ENGINE=InnoDB COMMENT='维护计划表';

-- 生成工单数据（每个设备至少2个工单，覆盖不同状态和类型）
INSERT INTO work_order (order_no, equipment_id, order_type, content, status, creator_id, assignee_id, create_time, finish_time) VALUES
-- 设备1（冲压机）的工单
('WO202401001', 1, '生产', 'A型汽车底盘冲压生产', '已完成', 14, 15, '2024-01-05 08:30:00', '2024-01-10 17:00:00'),
('WO202402003', 1, '维护', '季度维护：液压系统检查', '进行中', 16, 16, '2024-02-20 14:00:00', NULL),

-- 设备2（焊接机器人）的工单
('WO202312002', 2, '维修', '焊枪定位异常维修', '已完成', 15, 17, '2023-12-28 10:15:00', '2024-01-03 11:30:00'),
('WO202402004', 2, '生产', '新能源电池盒焊接', '待处理', 14, 17, '2024-02-25 09:00:00', NULL),

-- 设备3（AGV小车）的工单
('WO202401005', 3, '维护', '导航系统校准', '已完成', 16, 17, '2024-01-15 13:20:00', '2024-01-16 16:45:00'),
('WO202403006', 3, '生产', '发动机部件运输任务', '进行中', 14, 15, '2024-03-01 08:45:00', NULL),

-- 设备4（CNC加工中心）的工单
('WO202402007', 4, '维修', '主轴异响故障排查', '进行中', 15, 16, '2024-02-18 11:00:00', NULL),
('WO202403008', 4, '生产', '精密模具加工', '待处理', 14, 17, '2024-03-05 14:30:00', NULL);

-- 生成排程计划（每个工单1-2个排程）
INSERT INTO scheduling_plan (order_id, equipment_id, plan_date, start_time, end_time, progress, remarks) VALUES
-- 已完成工单的排程
(1, 1, '2024-01-05', '08:00:00', '20:00:00', 100, '两班倒生产'),
(3, 2, '2023-12-29', '09:00:00', '12:00:00', 100, '故障诊断'),
(3, 2, '2024-01-02', '13:30:00', '17:30:00', 100, '更换定位传感器'),
(5, 3, '2024-01-15', '13:00:00', '15:30:00', 100, '激光雷达校准'),

-- 进行中工单的排程
(2, 1, '2024-02-21', '10:00:00', '12:00:00', 60, '液压油检测'),
(6, 3, '2024-03-02', '09:00:00', '18:00:00', 30, '连续运输任务'),
(7, 4, '2024-02-20', '14:00:00', '17:00:00', 80, '拆解检查主轴'),

-- 待处理工单的排程（尚未开始）
(4, 2, '2024-03-05', '08:30:00', '16:00:00', 0, '等待物料到位'),
(8, 4, '2024-03-10', '09:00:00', '18:00:00', 0, '预留加工时间');

-- 生成维护计划（基于设备维护周期生成未来3个月计划）
INSERT INTO maintenance_plan (equipment_id, plan_date, cycle_days, last_date, content) VALUES
-- 设备1（维护周期90天）
(1, '2024-03-25', 90, '2023-12-25', '1.液压系统整体检查 2.滑块导轨润滑 3.电气柜除尘'),

-- 设备2（维护周期60天）
(2, '2024-02-27', 60, '2023-12-28', '1.焊枪电缆检查 2.冷却系统维护 3.程序备份'),
(2, '2024-04-28', 60, NULL, '定期维护（预测日期）'),

-- 设备4（维护周期60天）
(4, '2024-04-15', 60, '2023-06-15', '1.主轴轴承更换 2.切削液过滤 3.精度校准'),

-- 设备3（维护周期180天，上次无记录）
(3, '2024-04-10', 180, NULL, '1.电池健康检测 2.驱动轮磨损检查 3.导航系统升级');