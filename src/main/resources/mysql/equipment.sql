/* 设备基础信息表（equipment） */
CREATE TABLE equipment (
    equipment_id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY COMMENT '设备唯一标识',
    equipment_code VARCHAR(32) NOT NULL UNIQUE COMMENT '设备编码（规则：车间代码+流水号）',
    equipment_name VARCHAR(64) NOT NULL COMMENT '设备名称',
    equipment_model VARCHAR(32) NOT NULL COMMENT '设备型号',
    equipment_type INT NOT NULL COMMENT '设备类型（关联equipment_type.type_id）',
    location VARCHAR(128) COMMENT '安装位置（格式：车间/生产线/工位）',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '当前状态：1-正常 2-待机 3-维护中 4-已报废',
    manufacturer VARCHAR(64) COMMENT '制造商',
    production_date DATE COMMENT '生产日期',
    installation_date DATE NOT NULL COMMENT '安装日期',
    last_maintenance_date DATE COMMENT '上次维护日期',
    maintenance_cycle INT COMMENT '维护周期（单位：天）',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后更新时间',
    INDEX idx_type (equipment_type),
    INDEX idx_status (status)
) ENGINE=InnoDB COMMENT='设备基础信息表';

/* 设备类型表（equipment_type） */
CREATE TABLE equipment_type (
    type_id INT UNSIGNED AUTO_INCREMENT PRIMARY KEY COMMENT '类型ID',
    type_name VARCHAR(32) NOT NULL UNIQUE COMMENT '类型名称（如：冲压机/焊接机器人）',
    description TEXT COMMENT '类型描述',
    parameters_config JSON COMMENT '参数配置模板（JSON格式）',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'
) ENGINE=InnoDB COMMENT='设备类型分类表';

/* 设备状态记录表（equipment_status） */
CREATE TABLE equipment_status (
    status_id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY COMMENT '状态记录ID',
    equipment_id BIGINT UNSIGNED NOT NULL COMMENT '设备ID',
    status_code SMALLINT NOT NULL COMMENT '状态编码（1-运行 2-空闲 3-故障）',
    status_detail VARCHAR(128) COMMENT '状态详细描述',
    start_time DATETIME NOT NULL COMMENT '状态开始时间',
    end_time DATETIME COMMENT '状态结束时间',
    duration INT COMMENT '持续时间（秒）',
    FOREIGN KEY (equipment_id) REFERENCES equipment(equipment_id),
    INDEX idx_status_time (start_time, end_time)
) ENGINE=InnoDB COMMENT='设备状态历史记录表';

/* 设备运行参数记录表（equipment_parameters） */
CREATE TABLE equipment_parameters (
    param_id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY COMMENT '参数记录ID',
    equipment_id BIGINT UNSIGNED NOT NULL COMMENT '设备ID',
    param_name VARCHAR(32) NOT NULL COMMENT '参数名称（如：温度/压力）',
    param_value DECIMAL(12,4) NOT NULL COMMENT '参数数值',
    unit VARCHAR(16) COMMENT '计量单位',
    collect_time DATETIME NOT NULL COMMENT '采集时间',
    is_normal TINYINT NOT NULL COMMENT '是否正常：0-异常 1-正常',
    FOREIGN KEY (equipment_id) REFERENCES equipment(equipment_id),
    INDEX idx_collect_time (collect_time)
) ENGINE=InnoDB COMMENT='设备运行参数记录表';

/* 设备报警记录表（equipment_alarm） */
CREATE TABLE equipment_alarm (
    alarm_id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY COMMENT '报警记录ID',
    equipment_id BIGINT UNSIGNED NOT NULL COMMENT '设备ID',
    alarm_code VARCHAR(32) NOT NULL COMMENT '报警编码（按标准编码规则）',
    alarm_reason VARCHAR(200) COMMENT '报警原因',
    alarm_level TINYINT NOT NULL COMMENT '报警等级：1-警告 2-一般故障 3-严重故障',
    start_time DATETIME NOT NULL COMMENT '报警开始时间',
    end_time DATETIME COMMENT '报警解除时间',
    duration INT COMMENT '持续时间（秒）',
    status TINYINT NOT NULL DEFAULT 0 COMMENT '处理状态：0-未处理 1-处理中 2-已处理',
    handler VARCHAR(32) COMMENT '处理人',
    solution TEXT COMMENT '处理方案',
    FOREIGN KEY (equipment_id) REFERENCES equipment(equipment_id),
    INDEX idx_alarm_time (start_time)
) ENGINE=InnoDB COMMENT='设备报警记录表';

/* 设备维护记录表（equipment_maintenance） */
CREATE TABLE equipment_maintenance (
    maintenance_id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY COMMENT '维护记录ID',
    equipment_id BIGINT UNSIGNED NOT NULL COMMENT '设备ID',
    plan_date DATE NOT NULL COMMENT '计划维护日期',
    actual_date DATE COMMENT '实际维护日期',
    maintenance_type TINYINT NOT NULL COMMENT '维护类型：1-日常保养 2-定期维护 3-紧急维修',
    maintenance_content TEXT NOT NULL COMMENT '维护内容',
    operator VARCHAR(32) NOT NULL COMMENT '维护人员',
    result TEXT COMMENT '维护结果',
    cost DECIMAL(10,2) COMMENT '维护成本',
    FOREIGN KEY (equipment_id) REFERENCES equipment(equipment_id),
    INDEX idx_maintenance_date (plan_date)
) ENGINE=InnoDB COMMENT='设备维护记录表';


-- 插入设备类型数据
INSERT INTO equipment_type (type_name, description, parameters_config) VALUES
   ('冲压机', '金属板材成型设备',
    '[
      {
        "name": "主电机电流",
        "unit": "A",
        "normalMin": 80.0,
        "normalMax": 120.0,
        "abnormalMin": 120.0,
        "abnormalMax": 150.0,
        "abnormalProbability": 0.0005
      },
      {
        "name": "滑块压力",
        "unit": "MPa",
        "normalMin": 18.0,
        "normalMax": 22.0,
        "abnormalMin": 22.0,
        "abnormalMax": 25.0,
        "abnormalProbability": 0.0003
      },
      {
        "name": "工作温度",
        "unit": "℃",
        "normalMin": 50.0,
        "normalMax": 80.0,
        "abnormalMin": 80.0,
        "abnormalMax": 100.0,
        "abnormalProbability": 0.0005
      },
      {
        "name": "振动幅度",
        "unit": "mm",
        "normalMin": 0.1,
        "normalMax": 0.5,
        "abnormalMin": 0.5,
        "abnormalMax": 1.0,
        "abnormalProbability": 0.0002
      }
    ]'),

   ('焊接机器人', '六轴自动化焊接设备',
    '[
      {
        "name": "焊接电流",
        "unit": "A",
        "normalMin": 200.0,
        "normalMax": 250.0,
        "abnormalMin": 250.0,
        "abnormalMax": 300.0,
        "abnormalProbability": 0.0005
      },
      {
        "name": "焊接电压",
        "unit": "V",
        "normalMin": 22.0,
        "normalMax": 28.0,
        "abnormalMin": 28.0,
        "abnormalMax": 35.0,
        "abnormalProbability": 0.0003
      },
      {
        "name": "气体流量",
        "unit": "L/min",
        "normalMin": 15.0,
        "normalMax": 20.0,
        "abnormalMin": 20.0,
        "abnormalMax": 25.0,
        "abnormalProbability": 0.0004
      },
      {
        "name": "臂架负载率",
        "unit": "%",
        "normalMin": 60.0,
        "normalMax": 90.0,
        "abnormalMin": 90.0,
        "abnormalMax": 100.0,
        "abnormalProbability": 0.0005
      }
    ]'),

   ('CNC加工中心', '数控精密加工设备',
    '[
      {
        "name": "主轴转速",
        "unit": "rpm",
        "normalMin": 18000.0,
        "normalMax": 22000.0,
        "abnormalMin": 22000.0,
        "abnormalMax": 25000.0,
        "abnormalProbability": 0.0003
      },
      {
        "name": "进给速率",
        "unit": "mm/min",
        "normalMin": 2000.0,
        "normalMax": 5000.0,
        "abnormalMin": 5000.0,
        "abnormalMax": 6000.0,
        "abnormalProbability": 0.0002
      },
      {
        "name": "刀具温度",
        "unit": "℃",
        "normalMin": 30.0,
        "normalMax": 50.0,
        "abnormalMin": 50.0,
        "abnormalMax": 70.0,
        "abnormalProbability": 0.0004
      },
      {
        "name": "切削力",
        "unit": "kN",
        "normalMin": 5.0,
        "normalMax": 15.0,
        "abnormalMin": 15.0,
        "abnormalMax": 25.0,
        "abnormalProbability": 0.0003
      }
    ]'),

   ('AGV小车', '自动导航运输车',
    '[
      {
        "name": "电池电压",
        "unit": "V",
        "normalMin": 48.0,
        "normalMax": 52.0,
        "abnormalMin": 52.0,
        "abnormalMax": 60.0,
        "abnormalProbability": 0.0005
      },
      {
        "name": "行驶速度",
        "unit": "m/s",
        "normalMin": 1.0,
        "normalMax": 1.5,
        "abnormalMin": 1.5,
        "abnormalMax": 2.0,
        "abnormalProbability": 0.0005
      },
      {
        "name": "载重负荷",
        "unit": "kg",
        "normalMin": 0.0,
        "normalMax": 800.0,
        "abnormalMin": 800.0,
        "abnormalMax": 1000.0,
        "abnormalProbability": 0.0003
      },
      {
        "name": "导航信号强度",
        "unit": "%",
        "normalMin": 80.0,
        "normalMax": 100.0,
        "abnormalMin": 0.0,
        "abnormalMax": 80.0,
        "abnormalProbability": 0.0005
      }
    ]'),

   ('注塑机', '塑料成型设备',
    '[
      {
        "name": "注射压力",
        "unit": "bar",
        "normalMin": 800.0,
        "normalMax": 1200.0,
        "abnormalMin": 1200.0,
        "abnormalMax": 1500.0,
        "abnormalProbability": 0.0004
      },
      {
        "name": "料筒温度",
        "unit": "℃",
        "normalMin": 200.0,
        "normalMax": 250.0,
        "abnormalMin": 250.0,
        "abnormalMax": 300.0,
        "abnormalProbability": 0.0003
      },
      {
        "name": "锁模力",
        "unit": "ton",
        "normalMin": 800.0,
        "normalMax": 850.0,
        "abnormalMin": 850.0,
        "abnormalMax": 900.0,
        "abnormalProbability": 0.0002
      },
      {
        "name": "循环时间",
        "unit": "s",
        "normalMin": 30.0,
        "normalMax": 40.0,
        "abnormalMin": 40.0,
        "abnormalMax": 60.0,
        "abnormalProbability": 0.0005
      }
    ]');

-- 插入设备基础数据
INSERT INTO equipment (equipment_code, equipment_name, equipment_model, equipment_type, location, status, manufacturer, production_date, installation_date, last_maintenance_date, maintenance_cycle) VALUES
    ('WSH-001', '数控冲压机', 'HFP-200', 1, '冲压车间/A线/工位1', 1, '上海重机', '2020-03-15', '2020-05-20', '2025-04-01', 90),
    ('ROB-010', '弧焊机器人', 'FANUC-R2000', 2, '焊接车间/新能源线/工位3', 1, '发那科', '2022-01-10', '2022-02-01', '2025-04-01', 60),
    ('AGV-005', '激光导航AGV', 'NDC-800', 4, '总装车间/物流区', 1, '新松机器人', '2021-09-01', '2025-04-01', '2025-04-01', 180),
    ('CNC-100', '五轴加工中心', 'MAZAK-500', 3, '机加车间/精密区', 1, '山崎马扎克', '2019-11-30', '2020-01-10', '2025-04-01', 60),
    ('OLD-001', '液压冲床', 'YH32-300', 5, '报废设备区', 1, '北京第一机床', '2010-05-01', '2010-07-01', '2025-04-01', 90),
    ('WSH-002', '高速冲压机', 'HFP-300', 1, '冲压车间/B线/工位2', 1, '济南二机床', '2021-06-20', '2021-08-15', '2025-04-15', 60),
    ('ROB-011', '点焊机器人', 'KUKA-KR16', 2, '焊接车间/传统线/工位5', 1, '库卡机器人', '2023-03-01', '2023-04-10', '2025-04-20', 45),
    ('AGV-006', '磁导航AGV', 'MIRO-600', 4, '总装车间/暂存区', 1, '深圳麦格米特', '2022-05-15', '2022-06-01', '2025-04-25', 120),
    ('CNC-101', '三轴加工中心', 'HAAS-VF2', 3, '机加车间/普通区', 1, '哈斯自动化', '2020-08-01', '2020-09-20', '2025-04-05', 30),
    ('INJ-001', '注塑成型机', '海天-120T', 5, '注塑车间/1号机', 1, '海天国际', '2021-10-01', '2021-11-10', '2025-04-10', 90);

