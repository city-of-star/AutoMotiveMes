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