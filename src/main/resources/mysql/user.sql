-- 用户表
CREATE TABLE IF NOT EXISTS sys_user (
    user_id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '用户ID',
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    password VARCHAR(100) NOT NULL COMMENT '加密密码' DEFAULT '$2a$10$.0brTBYitG6.GVWfB8.7e.OolO2ec1j35d7Qpq8J/etjQLf/Yp4sa',
    real_name VARCHAR(50) COMMENT '真实姓名',
    sex TINYINT(1) COMMENT '性别(0:女 1:男)',
    dept_id BIGINT COMMENT '部门id',
    post_id BIGINT COMMENT '岗位id',
    head_img VARCHAR(200) COMMENT '头像URL' DEFAULT 'https://img0.baidu.com/it/u=1053130193,1078694950&fm=253&fmt=auto&app=138&f=GIF?w=500&h=280',
    theme_color VARCHAR(50) COMMENT '主题色' DEFAULT '#409EFF',
    email VARCHAR(100) COMMENT '邮箱',
    phone VARCHAR(20) COMMENT '联系电话',
    status TINYINT(1) DEFAULT 1 COMMENT '状态(0:禁用 1:启用)',
    account_locked TINYINT(1) DEFAULT 1 COMMENT '账户是否锁定(0:已锁定;1:未锁定)',
    login_attempts INT DEFAULT 0 COMMENT '连续登录失败次数',
    last_login DATETIME COMMENT '最后登录时间',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 角色表
CREATE TABLE IF NOT EXISTS sys_role (
    role_id INT PRIMARY KEY AUTO_INCREMENT COMMENT '角色ID',
    role_code VARCHAR(50) NOT NULL UNIQUE COMMENT '角色编码',
    role_name VARCHAR(50) NOT NULL COMMENT '角色名称',
    description VARCHAR(255) COMMENT '角色描述'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色表';

-- 权限表（菜单权限+操作权限）
CREATE TABLE IF NOT EXISTS sys_permission (
    perm_id INT PRIMARY KEY AUTO_INCREMENT COMMENT '权限ID',
    perm_code VARCHAR(200) NOT NULL UNIQUE COMMENT '权限编码',
    perm_name VARCHAR(50) NOT NULL COMMENT '权限名称',
    perm_type varchar(20) NOT NULL COMMENT '权限类型(MENU,BUTTON,API)',
    status TINYINT(1) DEFAULT 1 COMMENT '状态(0:禁用 1:启用)',
    parent_id INT DEFAULT 0 COMMENT '父权限ID',
    path VARCHAR(100) COMMENT '前端路由路径',
    component VARCHAR(100) COMMENT '前端组件',
    icon VARCHAR(50) COMMENT '菜单图标',
    order_num INT DEFAULT 0 COMMENT '显示顺序',
    api_path VARCHAR(100) COMMENT '后端接口路径',
    method VARCHAR(10) COMMENT '请求方法'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='权限表';

-- 部门表
CREATE TABLE IF NOT EXISTS sys_dept (
    dept_id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '部门ID',
    dept_name VARCHAR(50) NOT NULL COMMENT '部门名称',
    parent_id BIGINT COMMENT '父部门ID（支持树形层级）',
    order_num INT DEFAULT 0 COMMENT '显示顺序',
    status TINYINT(1) DEFAULT 1 COMMENT '状态(0:停用 1:启用)',
    leader_id BIGINT COMMENT '负责人ID（关联用户）',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (parent_id) REFERENCES sys_dept(dept_id),
    FOREIGN KEY (leader_id) REFERENCES sys_user(user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='部门表';

-- 岗位表
CREATE TABLE IF NOT EXISTS sys_post (
    post_id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '岗位ID',
    post_name VARCHAR(50) NOT NULL COMMENT '岗位名称',
    post_code VARCHAR(50) NOT NULL UNIQUE COMMENT '岗位编码',
    dept_id BIGINT NOT NULL COMMENT '所属部门ID',
    order_num INT DEFAULT 0 COMMENT '显示顺序',
    status TINYINT(1) DEFAULT 1 COMMENT '状态(0:停用 1:启用)',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (dept_id) REFERENCES sys_dept(dept_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='岗位表';

-- 用户角色关系表
CREATE TABLE IF NOT EXISTS sys_user_role (
    user_id BIGINT NOT NULL,
    role_id INT NOT NULL,
    PRIMARY KEY (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES sys_user(user_id),
    FOREIGN KEY (role_id) REFERENCES sys_role(role_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户角色关系表';

-- 角色权限关系表
CREATE TABLE IF NOT EXISTS sys_role_permission (
    role_id INT NOT NULL,
    perm_id INT NOT NULL,
    PRIMARY KEY (role_id, perm_id),
    FOREIGN KEY (role_id) REFERENCES sys_role(role_id),
    FOREIGN KEY (perm_id) REFERENCES sys_permission(perm_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色权限关系表';

-- 插入角色数据
INSERT INTO sys_role (role_code, role_name, description) VALUES
    ('SUPER_ADMIN', '超级管理员', '拥有系统所有权限(系统初始化的唯一角色)'),
    ('ADMIN', '管理员', '负责全系统配置与安全审计'),
    ('PRODUCTION_MANAGER', '生产经理', '全局生产监控与调度'),
    ('TEAM_LEADER', '班组长', '本班组生产执行与异常处理'),
    ('QUALITY_CHECKER', '质量检验员', '质量检测与缺陷上报'),
    ('EQUIPMENT_ENGINEER', '设备工程师', '设备状态监控与维护计划'),
    ('DATA_ANALYST', '数据分析师', '生产KPI建模与可视化');


-- 插入权限数据
INSERT INTO sys_permission (perm_code, perm_name, perm_type, parent_id, path, component, api_path, method) VALUES
    --  系统管理(1)
    ('system:manage', '系统管理', 'MENU', 0, '/system', NULL, '/api/system', NULL),

    -- 用户管理模块(2~8)
    ('system:user:manage', '用户管理', 'MENU', 1, '/system', '/Index.vue', '/api/system', NULL),
    ('system:user:add', '新增用户', 'BUTTON', 2, NULL, NULL, '/api/system/user/add', 'POST'),
    ('system:user:delete', '删除用户', 'BUTTON', 2, NULL, NULL, '/api/system/user/delete', 'POST'),
    ('system:user:update', '编辑用户', 'BUTTON', 2, NULL, NULL, '/api/system/user/update', 'POST'),
    ('system:user:list', '查询用户', 'BUTTON', 2, NULL, NULL, '/api/system/user/list', 'GET'),
    ('system:user:import', '导入用户', 'BUTTON', 2, NULL, NULL, '/api/system/user/import', 'POST'),
    ('system:user:export', '导出用户', 'BUTTON', 2, NULL, NULL, '/api/system/user/export', 'GET'),

    -- 角色管理模块(9~14)
    ('system:role:manage', '角色管理', 'MENU', 1, '/system/role', '/Index.vue', '/api/system/role', NULL),
    ('system:role:add', '新增角色', 'BUTTON', 9, NULL, NULL, '/api/system/role/add', 'POST'),
    ('system:role:delete', '删除角色', 'BUTTON', 9, NULL, NULL, '/api/system/role/delete', 'POST'),
    ('system:role:update', '编辑角色', 'BUTTON', 9, NULL, NULL, '/api/system/role/update', 'POST'),
    ('system:role:list', '查询角色', 'BUTTON', 9, NULL, NULL, '/api/system/role/list', 'GET'),
    ('system:role:export', '导出角色', 'BUTTON', 9, NULL, NULL, '/api/system/role/export', 'GET'),

    -- 部门管理模块(15~20)
    ('system:dept:manage', '部门管理', 'MENU', 1, '/system/dept', '/Index.vue', '/api/system/dept', NULL),
    ('system:dept:add', '新增部门', 'BUTTON', 15, NULL, NULL, '/api/system/dept/add', 'POST'),
    ('system:dept:delete', '删除部门', 'BUTTON', 15, NULL, NULL, '/api/system/dept/delete', 'POST'),
    ('system:dept:update', '编辑部门', 'BUTTON', 15, NULL, NULL, '/api/system/dept/update', 'POST'),
    ('system:dept:list', '查询部门', 'BUTTON', 15, NULL, NULL, '/api/system/dept/list', 'GET'),
    ('system:dept:export', '导出部门', 'BUTTON', 15, NULL, NULL, '/api/system/dept/export', 'GET'),

    -- 岗位管理模块(21~26)
    ('system:post:manage', '岗位管理', 'MENU', 1, '/system/post', '/Index.vue', '/api/system/post', NULL),
    ('system:post:add', '新增岗位', 'BUTTON', 21, NULL, NULL, '/api/system/post/add', 'POST'),
    ('system:post:delete', '删除岗位', 'BUTTON', 21, NULL, NULL, '/api/system/post/delete', 'POST'),
    ('system:post:update', '编辑岗位', 'BUTTON', 21, NULL, NULL, '/api/system/post/update', 'POST'),
    ('system:post:list', '查询岗位', 'BUTTON', 21, NULL, NULL, '/api/system/post/list', 'GET'),
    ('system:post:export', '导出岗位', 'BUTTON', 21, NULL, NULL, '/api/system/post/export', 'GET'),

    -- 报警配置模块(27)
    ('system:alarm:config', '报警配置', 'MENU', 36, 'rule', '@/views/alarm/rule/Index.vue', '/api/alarm/rule', 'GET'),

    -- 生产监控模块(28)
    ('monitor', '生产监控', 'MENU', 0, '/production', 'Layout', '/api/production', NULL),

    -- 生产排程模块(29~31)
    ('production:manage', '生产排程', 'MENU', 0, '/scheduling', 'Layout', '/api/scheduling', NULL),
    ('production:order:manage', '工单管理', 'MENU', 30, 'orders', '@/views/scheduling/orders/Index.vue', '/api/scheduling/orders', 'GET'),
    ('production:record:view', '生产记录', 'MENU', 30, 'plan', '@/views/scheduling/record/Index.vue', '/api/scheduling/record', 'GET'),

    -- 设备管理模块(32~34)
    ('equipment:manage', '设备管理', 'MENU', 0, '/equipment', 'Layout', '/api/equipment', NULL),
    ('equipment:status:view', '设备状态', 'MENU', 33, 'status', '@/views/equipment/status/Index.vue', '/api/equipment/status', 'GET'),
    ('equipment:maintenance:view', '维护记录', 'MENU', 33, 'maintenance', '@/views/equipment/maintenance/Index.vue', '/api/equipment/maintenance', 'GET'),

    -- 报警中心模块(35~37)
    ('alarm:manage', '报警中心', 'MENU', 0, '/alarm', 'Layout', '/api/alarm', NULL),
    ('alarm:current:view', '实时报警', 'MENU', 36, 'current', '@/views/alarm/current/Index.vue', '/api/alarm/current', 'GET'),
    ('alarm:history:view', '报警历史', 'MENU', 36, 'history', '@/views/alarm/history/Index.vue', '/api/alarm/history', 'GET'),

    -- 生产报表模块(38~40)
    ('report:manage', '生产报表', 'MENU', 0, '/report', 'Layout', '/api/report', NULL),
    ('report:daily:view', '生产日报', 'MENU', 39, 'daily', '@/views/report/daily/Index.vue', '/api/report/daily', 'GET'),
    ('report:quality:view', '质量分析', 'MENU', 39, 'quality', '@/views/report/quality/Index.vue', '/api/report/quality', 'GET'),

    -- 质量管理模块(41~42)
    ('quality:manage', '质量管理', 'MENU', 0, '/quality', 'Layout', '/api/quality', NULL),
    ('quality:inspection:view', '质量检测', 'MENU', 42, 'quality', '@/views/quality/inspection/Index.vue', '/api/quality/inspection', 'GET');

-- 插入用户数据 测试密码统一为123456（使用BCrypt加密存储）
INSERT INTO sys_user (username, real_name, sex, dept_id, post_id, email, phone) VALUES
    ('admin', '超级管理员', 1,  1, 1, '4736289150@qq.com', '13524689753'),
    ('lhy',  '林海洋', 1,  1, 2, '2752362782@qq.com', '18245697330'),
    ('lqh',  '李青霞', 0,  1, 2, '2825646787@qq.com', '13855605201'),
    ('ydf', '杨东风', 1,  7, 3, '8219537460@qq.com', '18765432109'),
    ('yjq', '杨佳琪', 0,  3, 5, '3507462891@qq.com', '15987654321'),
    ('yzz', '杨泽政', 1,  4, 6, '9162835740@qq.com', '17890123456'),
    ('wjx', '王佳欣', 1,  5, 7, '2849173650@qq.com', '14736985210'),
    ('ljb', '刘建波', 1,  6, 8, '7053916284@qq.com', '19182736455'),
    ('wwb', '王卫斌', 0,  9, 9, '5391628470@qq.com', '16655443322'),
    ('lkw',  '李凯文', 0,  10, 11, '1647392580@qq.com', '13214567890'),
    ('wyj',  '王雨洁', 1,  12, 13, '6925814370@qq.com', '18809123456'),
    ('dty',  '董天逸', 0,  13, 14, '3746289150@qq.com', '17156789012'),
    ('wcz',  '王成章', 1,  12, 15, '8519372640@qq.com', '19923456789'),
    ('zq',  '张强', 1,  6, 8, 'worker1@factory.com', '13800138001'),
    ('ln',  '李娜', 0,  6, 8, 'worker2@factory.com', '13800138002'),
    ('cl',  '陈磊', 1,  12, 13, 'engineer1@factory.com', '13800138003'),
    ('wf',  '王芳', 0,  12, 13, 'engineer2@factory.com', '13800138004'),
    ('zm',  '赵敏', 0,  9, 9, 'inspector1@factory.com', '13800138005'),
    ('zj',  '周杰', 1,  9, 9, 'inspector2@factory.com', '13800138006'),
    ('zmy', '周明远', 1, 15, 17, 'strat_analyst1@factory.com', '13800138100'),
    ('wxh', '吴晓慧', 0, 15, 18, 'process_expert1@factory.com', '13800138101'),
    ('zty', '郑天佑', 1, 13, 13, 'mould_spec1@factory.com', '13800138102'),
    ('ljh', '刘建华', 1, 7, 3, 'prod_plan2@factory.com', '13800138103'),
    ('xjy', '徐静雅', 0, 14, 15, 'mes_admin2@factory.com', '13800138104'),
    ('mzq', '马志强', 1, 14, 16, 'data_spec3@factory.com', '13800138105'),
    ('hwj', '黄伟杰', 1, 12, 12, 'plc_eng3@factory.com', '13800138106'),
    ('hxn', '韩雪宁', 0, 12, 14, 'equip_monitor1@factory.com', '13800138107'),
    ('lxf', '林晓芳', 0, 10, 11, 'qda_analyst2@factory.com', '13800138108'),
    ('czp', '陈志鹏', 1, 4, 6, 'welding_eng2@factory.com', '13800138109'),
    ('zyt', '张雨桐', 0, 5, 7, 'paint_insp2@factory.com', '13800138110'),
    ('wzy', '王振宇', 1, 6, 8, 'assembly_ld3@factory.com', '13800138111'),
    ('zxy', '赵雪莹', 0, 9, 10, 'pqc_eng3@factory.com', '13800138112'),
    ('sht', '孙浩天', 1, 15, 17, 'eff_analyst2@factory.com', '13800138113'),
    ('ljn', '李佳宁', 0, 13, 13, 'mould_spec2@factory.com', '13800138114'),
    ('zyh', '周宇航', 1, 7, 3, 'prod_plan3@factory.com', '13800138115'),
    ('wyx', '吴雨欣', 0, 14, 16, 'data_spec4@factory.com', '13800138116'),
    ('zhr', '郑浩然', 1, 12, 12, 'plc_eng4@factory.com', '13800138117'),
    ('wln', '王丽娜', 0, 10, 11, 'qda_analyst3@factory.com', '13800138118'),
    ('lhr', '刘昊然', 1, 15, 18, 'process_expert2@factory.com', '13800138119');

-- 插入部门
INSERT INTO sys_dept (dept_name, parent_id, order_num, status, leader_id) VALUES
    ('总经办', null, 1, 1, 1),  # 1

    ('生产部', 1, 2, 1, null),  # 2
    ('冲压车间', 2, 1, 1, null),
    ('焊装车间', 2, 2, 1, null),
    ('涂装车间', 2, 3, 1, null),
    ('总装车间', 2, 4, 1, null),
    ('生产计划科', 2, 5, 1, null),

    ('质量部', 1, 3, 1, null),  # 8
    ('质量检测科', 8, 1, 1, null),
    ('质量分析科', 8, 2, 1, null),

    ('设备部', 1, 4, 1, null),  # 11
    ('设备运维科', 11, 1, 1, null),
    ('模具管理科', 11, 2, 1, null),

    ('管理部', 1, 5, 1, null),  # 14

    ('战略决策部', 1, 6, 1, null);  # 15

-- 插入岗位
INSERT INTO sys_post (post_name, post_code, dept_id, order_num, status) VALUES
    -- 总部岗位
    ('超级管理员', 'SUPER_ADMIN', 1, 1, 1),
    ('管理员', 'ADMIN', 14, 2, 1),

    -- 生产部岗位（含子部门）
    ('生产计划员', 'PROD_PLANNER', 7, 1, 1),       -- 生产计划科 [2,5](@ref)
    ('车间调度主管', 'WORKSHOP_SCHEDULER', 2, 2, 1), -- 生产部 [2,3](@ref)
    ('设备操作员', 'EQUIP_OPERATOR', 3, 3, 1),      -- 冲压车间 [1,3](@ref)
    ('焊接工艺师', 'WELDING_ENGINEER', 4, 4, 1),    -- 焊装车间 [1,3](@ref)
    ('涂装质检员', 'PAINT_INSPECTOR', 5, 5, 1),     -- 涂装车间 [3,9](@ref)
    ('总装线长', 'ASSEMBLY_LEADER', 6, 6, 1),      -- 总装车间 [1,3](@ref)

    -- 质量部岗位
    ('来料检验员', 'IQC_INSPECTOR', 9, 1, 1),      -- 质量检测科 [9,10](@ref)
    ('过程质量控制员', 'PQC_ENGINEER', 9, 2, 1),    -- 质量检测科 [9,10](@ref)
    ('质量数据分析师', 'QDA_ANALYST', 10, 3, 1),   -- 质量分析科 [10,11](@ref)

    -- 设备部岗位
    ('PLC运维工程师', 'PLC_ENGINEER', 12, 1, 1),    -- 设备运维科 [1,12](@ref)
    ('模具保养专员', 'MOULD_MAINTENANCE', 13, 2, 1),-- 模具管理科 [1,12](@ref)
    ('设备状态监控员', 'EQUIP_MONITOR', 12, 3, 1), -- 设备运维科 [3,12](@ref)

    -- 管理部岗位
    ('MES系统管理员', 'MES_ADMIN', 14, 3, 1),      -- 管理部 [2,17](@ref)
    ('生产数据专员', 'DATA_SPECIALIST', 14, 4, 1), -- 管理部 [4,17](@ref)

    -- 战略决策部岗位
    ('生产效能分析师', 'EFFICIENCY_ANALYST', 15, 1, 1), -- [19,20](@ref)
    ('工艺优化专家', 'PROCESS_OPTIMIZER', 15, 2, 1);    -- [1,19](@ref)

-- 用户角色关系
INSERT INTO sys_user_role (user_id, role_id) VALUES
    -- 超级管理员
    (1,1),   -- admin -> SUPER_ADMIN

    -- 系统管理员
    (2,2),   -- lhy -> ADMIN
    (3,2),   -- lqh -> ADMIN

    -- 生产经理（管理岗）
    (4,3),   -- ydf -> PRODUCTION_MANAGER（生产计划科）

    -- 班组长（产线负责人）
    (7,4),   -- wjx -> TEAM_LEADER（涂装车间）
    (8,4),   -- ljb -> TEAM_LEADER（总装车间）
    (13,4),  -- zq -> TEAM_LEADER（总装线长）
    (14,4),  -- ln -> TEAM_LEADER（总装线长）

    -- 质量检验员
    (9,5),   -- wwb -> QUALITY_CHECKER（来料检验）
    (17,5),  -- zm -> QUALITY_CHECKER（质量检测科）
    (18,5),  -- zj -> QUALITY_CHECKER（质量检测科）
    (19, 5),

    -- 设备工程师
    (10,6),  -- wyj -> EQUIPMENT_ENGINEER（PLC运维）
    (11,6),  -- dty -> EQUIPMENT_ENGINEER（模具保养）
    (12,6),  -- wcz -> EQUIPMENT_ENGINEER（设备监控）
    (15,6),  -- cl -> EQUIPMENT_ENGINEER（设备运维）
    (16,6),  -- wf -> EQUIPMENT_ENGINEER（设备运维）

    -- 数据分析师
    (5,7),   -- yjq -> DATA_ANALYST（生产数据）
    (6,7),   -- yzz -> DATA_ANALYST（质量分析）
    (9,7),   -- lkw -> DATA_ANALYST（质量分析科）
    (10,7),  -- wyj -> DATA_ANALYST（设备数据分析）

    (20,7), (21,7),   -- 战略决策部分析师
    (22,6),   -- 模具保养专员->设备工程师
    (23,3),            -- 生产计划员->生产经理
    (24,2), (25,7),   -- 管理部岗位
    (26,6), (27,6),   -- 设备运维岗位
    (28,7), (29,6),   -- 质量分析/设备岗
    (30,4), (31,5),   -- 产线负责人/质检
    (32,7), (33,6),   -- 数据分析/设备
    (34,7), (35,6),   -- 战略分析/模具
    (36,3), (37,7),   -- 生产计划/数据分析
    (38,6), (39,7);   -- 设备/质量分析

-- 角色权限关系
-- 超级管理员（全权限）
INSERT INTO sys_role_permission (role_id, perm_id)
SELECT 1, perm_id FROM sys_permission;

-- 其他角色
INSERT INTO sys_role_permission (role_id, perm_id) VALUES
    -- 系统管理员（系统管理+监控）
    (2,1),(2,2),(2,3),(2,4),(2,5),(2,6),(2,7),(2,8),  -- 用户管理
    (2,9),(2,10),(2,11),(2,12),(2,13),(2,14),        -- 角色管理
    (2,15),(2,16),(2,17),(2,18),(2,19),(2,20),       -- 部门管理
    (2,21),(2,22),(2,23),(2,24),(2,25),(2,26),       -- 岗位管理
    (2,36),(2,37),                                   -- 报警中心

    -- 生产经理（生产全流程）
    (3,28),(3,29),(3,30),(3,31),                     -- 生产监控
    (3,32),(3,33),(3,34),                            -- 设备管理
    (3,36),(3,37),                                   -- 报警处理
    (3,38),(3,39),(3,40),                            -- 报表分析
    (3,41),(3,42),                                  -- 质量管理

    -- 班组长（生产执行）
    (4,28),(4,29),(4,31),                            -- 生产监控
    (4,33),(4,34),                                   -- 设备状态
    (4,36),                                         -- 实时报警

    -- 质量检验员
    (5,41),(5,42),                                   -- 质量检测
    (5,39),(5,40),                                   -- 质量报表

    -- 设备工程师
    (6,32),(6,33),(6,34),(6,35),                     -- 设备管理
    (6,36),                                          -- 报警查看

    -- 数据分析师
    (7,38),(7,39),(7,40),                            -- 生产报表
    (7,41),(7,42);