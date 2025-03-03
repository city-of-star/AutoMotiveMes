-- 删除表（先子表后主表）
DROP TABLE IF EXISTS sys_user_role;
DROP TABLE IF EXISTS sys_role_permission;
DROP TABLE IF EXISTS sys_user;
DROP TABLE IF EXISTS sys_role;
DROP TABLE IF EXISTS sys_permission;

CREATE TABLE sys_user (
    user_id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '用户ID',
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    password VARCHAR(100) NOT NULL COMMENT '加密密码',
    real_name VARCHAR(50) COMMENT '真实姓名',
    email VARCHAR(100) COMMENT '邮箱',
    phone VARCHAR(20) COMMENT '联系电话',
    status TINYINT(1) DEFAULT 1 COMMENT '状态(0:禁用 1:启用)',
    account_non_locked BOOLEAN DEFAULT 1 COMMENT '账户是否未锁定',
    login_attempts INT DEFAULT 0 COMMENT '连续登录失败次数',
    last_login DATETIME COMMENT '最后登录时间',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 角色表
CREATE TABLE sys_role (
    role_id INT PRIMARY KEY AUTO_INCREMENT COMMENT '角色ID',
    role_code VARCHAR(50) NOT NULL UNIQUE COMMENT '角色编码',
    role_name VARCHAR(50) NOT NULL COMMENT '角色名称',
    description VARCHAR(255) COMMENT '角色描述'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 权限表（菜单权限+操作权限）
CREATE TABLE sys_permission (
    perm_id INT PRIMARY KEY AUTO_INCREMENT COMMENT '权限ID',
    perm_code VARCHAR(200) NOT NULL UNIQUE COMMENT '权限编码',
    perm_name VARCHAR(50) NOT NULL COMMENT '权限名称',
    perm_type ENUM('MENU','BUTTON','API') NOT NULL COMMENT '权限类型',
    parent_id INT DEFAULT 0 COMMENT '父权限ID',
    path VARCHAR(100) COMMENT '前端路由路径',
    component VARCHAR(100) COMMENT '前端组件',
    icon VARCHAR(50) COMMENT '菜单图标',
    order_num INT DEFAULT 0 COMMENT '显示顺序',
    api_path VARCHAR(100) COMMENT '后端接口路径',
    method VARCHAR(10) COMMENT '请求方法'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 用户角色关系表
CREATE TABLE sys_user_role (
    user_id BIGINT NOT NULL,
    role_id INT NOT NULL,
    PRIMARY KEY (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES sys_user(user_id),
    FOREIGN KEY (role_id) REFERENCES sys_role(role_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 角色权限关系表
CREATE TABLE sys_role_permission (
    role_id INT NOT NULL,
    perm_id INT NOT NULL,
    PRIMARY KEY (role_id, perm_id),
    FOREIGN KEY (role_id) REFERENCES sys_role(role_id),
    FOREIGN KEY (perm_id) REFERENCES sys_permission(perm_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 插入用户数据（10个用户） 测试密码统一为123456（使用BCrypt加密存储）
INSERT INTO sys_user (username, password, real_name, email, phone, status, account_non_locked, login_attempts, last_login) VALUES
    ('admin', '$2a$10$vxLs8bAYj.ymIsVsooGzEOhTa1g/V27f2cvGWexgdn3yqegpb7uoq', '系统管理员', 'admin@mes.com', '13800138000', 1, false, 0, '2024-03-20 09:25:00'),
    ('prod_mgr1', '$2a$10$vxLs8bAYj.ymIsVsooGzEOhTa1g/V27f2cvGWexgdn3yqegpb7uoq', '王强', 'wangqiang@mes.com', '13912345678', 1, false, 0, '2024-03-20 10:15:00'),
    ('quality_mgr', '$2a$10$vxLs8bAYj.ymIsVsooGzEOhTa1g/V27f2cvGWexgdn3yqegpb7uoq', '李芳', 'lifang@mes.com', '13566778899', 1, false, 0, '2024-03-19 16:30:00'),
    ('equip_tech', '$2a$10$vxLs8bAYj.ymIsVsooGzEOhTa1g/V27f2cvGWexgdn3yqegpb7uoq', '张伟', 'zhangwei@mes.com', '13611223344', 1, false, 0, '2024-03-20 08:45:00'),
    ('operator1', '$2a$10$vxLs8bAYj.ymIsVsooGzEOhTa1g/V27f2cvGWexgdn3yqegpb7uoq', '陈刚', 'chengang@mes.com', '15822334455', 1, false, 0, '2024-03-19 17:00:00'),
    ('material_mgr', '$2a$10$vxLs8bAYj.ymIsVsooGzEOhTa1g/V27f2cvGWexgdn3yqegpb7uoq', '刘华', 'liuhua@mes.com', '18600112233', 1, false, 0, '2024-03-20 11:20:00'),
    ('operator2', '$2a$10$vxLs8bAYj.ymIsVsooGzEOhTa1g/V27f2cvGWexgdn3yqegpb7uoq', '赵敏', 'zhaomin@mes.com', '15944556677', 0, false, 0, '2024-03-18 14:00:00'),
    ('report_analyst', '$2a$10$vxLs8bAYj.ymIsVsooGzEOhTa1g/V27f2cvGWexgdn3yqegpb7uoq', '周涛', 'zhoutao@mes.com', '13788990011', 1, false, 0, '2024-03-20 13:15:00'),
    ('prod_mgr2', '$2a$10$vxLs8bAYj.ymIsVsooGzEOhTa1g/V27f2cvGWexgdn3yqegpb7uoq', '吴磊', 'wulei@mes.com', '13255667788', 1, false, 0, '2024-03-20 09:05:00'),
    ('guest', '$2a$10$vxLs8bAYj.ymIsVsooGzEOhTa1g/V27f2cvGWexgdn3yqegpb7uoq', '访客', 'guest@mes.com', '13111222333', 1, false, 0, NULL);

-- 插入角色数据（6个角色）
INSERT INTO sys_role (role_code, role_name, description) VALUES
    ('SUPER_ADMIN', '系统管理员', '拥有系统所有权限'),
    ('PRODUCTION_MGR', '生产管理', '生产计划与工单管理'),
    ('QUALITY_CTRL', '质量管理', '质量检测与异常处理'),
    ('EQUIP_TECH', '设备技术员', '设备监控与维护'),
    ('MATERIAL_MGR', '物料管理', '物料追踪与库存管理'),
    ('OPERATOR', '产线操作员', '基础操作与数据录入');

-- 插入权限数据（20个权限）
INSERT INTO sys_permission (perm_code, perm_name, perm_type, parent_id, path, component, api_path, method) VALUES
    -- 用户管理模块
    ('user:manage', '用户管理', 'MENU', 0, '/system/user', 'system/User', '/api/users', 'GET'),
    ('user:add', '新增用户', 'BUTTON', 1, NULL, NULL, '/api/users', 'POST'),
    ('user:edit', '编辑用户', 'BUTTON', 1, NULL, NULL, '/api/users/*', 'PUT'),
    ('user:delete', '删除用户', 'BUTTON', 1, NULL, NULL, '/api/users/*', 'DELETE'),

    -- 生产计划管理
    ('plan:manage', '生产计划', 'MENU', 0, '/production/plan', 'prod/Plan', '/api/plans', 'GET'),
    ('plan:add', '新建计划', 'BUTTON', 5, NULL, NULL, '/api/plans', 'POST'),
    ('plan:edit', '修改计划', 'BUTTON', 5, NULL, NULL, '/api/plans/*', 'PUT'),

    -- 设备监控
    ('equipment:monitor', '设备监控', 'MENU', 0, '/equipment/monitor', 'equip/Monitor', '/api/equipments', 'GET'),
    ('equipment:history', '历史数据', 'MENU', 8, '/equipment/history', 'equip/History', '/api/equipments/history', 'GET'),

    -- 质量管理
    ('quality:inspect', '质量检测', 'MENU', 0, '/quality/inspection', 'quality/Inspection', '/api/inspections', 'POST'),
    ('quality:report', '质量报表', 'MENU', 0, '/quality/reports', 'quality/Report', '/api/quality/reports', 'GET'),

    -- 生产报表
    ('report:production', '生产报表', 'MENU', 0, '/reports/production', 'report/Prod', '/api/reports/production', 'GET'),

    -- 实时告警
    ('alarm:monitor', '实时告警', 'MENU', 0, '/alarms', 'alarm/List', '/api/alarms', 'GET'),
    ('alarm:ack', '确认告警', 'BUTTON', 13, NULL, NULL, '/api/alarms/*/ack', 'PUT'),

    -- 物料追踪
    ('material:trace', '物料追踪', 'MENU', 0, '/materials', 'material/Trace', '/api/materials', 'GET'),
    ('material:update', '更新库存', 'BUTTON', 15, NULL, NULL, '/api/materials/*', 'PUT'),

    -- 工单管理
    ('order:manage', '工单跟踪', 'MENU', 0, '/production/orders', 'prod/Order', '/api/orders', 'GET'),
    ('order:start', '启动工单', 'BUTTON', 17, NULL, NULL, '/api/orders/*/start', 'POST'),
    ('order:finish', '完成工单', 'BUTTON', 17, NULL, NULL, '/api/orders/*/finish', 'POST');

-- 用户角色关系
INSERT INTO sys_user_role (user_id, role_id) VALUES
    (1, 1),   -- admin -> SUPER_ADMIN
    (2, 2), (9, 2),  -- 生产经理
    (3, 3),   -- 质量主管
    (4, 4),   -- 设备技术员
    (5, 6), (7, 6),  -- 操作员
    (6, 5),   -- 物料主管
    (8, 2), (8, 3),  -- 报表分析师
    (10, 6);  -- 访客

-- 角色权限关系
INSERT INTO sys_role_permission (role_id, perm_id) VALUES
    -- 超级管理员
    (1, 1),(1, 2),(1, 3),(1, 4),(1, 5),(1, 6),(1, 7),(1, 8),(1, 9),(1,10),(1,11),(1,12),(1,13),(1,14),(1,15),(1,16),(1,17),(1,18),(1,19),

    -- 生产管理
    (2, 5),(2, 6),(2, 7),(2, 8),(2, 9),(2,17),(2,18),(2,19),

    -- 质量管理
    (3,10),(3,11),(3,12),(3,13),

    -- 设备技术员
    (4,8),(4,9),(4,13),(4,14),

    -- 物料管理
    (5,15),(5,16),

    -- 操作员
    (6,5),(6,8),(6,9),(6,17),(6,18);