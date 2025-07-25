-- 用户表
CREATE TABLE sys_user (
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
                          status TINYINT(1) DEFAULT 1 COMMENT '状态(0:禁用 1:启用)' DEFAULT 1,
                          account_locked TINYINT(1) DEFAULT 1 COMMENT '账户是否锁定(0:已锁定;1:未锁定)' DEFAULT 1,
                          login_attempts INT DEFAULT 0 COMMENT '连续登录失败次数' DEFAULT 0,
                          last_login DATETIME COMMENT '最后登录时间',
                          create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                          update_time DATETIME ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 角色表
CREATE TABLE sys_role (
                          role_id INT PRIMARY KEY AUTO_INCREMENT COMMENT '角色ID',
                          role_code VARCHAR(50) NOT NULL UNIQUE COMMENT '角色编码',
                          role_name VARCHAR(50) NOT NULL COMMENT '角色名称',
                          description VARCHAR(255) COMMENT '角色描述'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色表';

-- 权限表（菜单权限+操作权限）
CREATE TABLE sys_permission (
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
CREATE TABLE sys_dept (
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
CREATE TABLE sys_post (
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
CREATE TABLE sys_user_role (
                               user_id BIGINT NOT NULL,
                               role_id INT NOT NULL,
                               PRIMARY KEY (user_id, role_id),
                               FOREIGN KEY (user_id) REFERENCES sys_user(user_id),
                               FOREIGN KEY (role_id) REFERENCES sys_role(role_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户角色关系表';

-- 角色权限关系表
CREATE TABLE sys_role_permission (
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
                                                                                                                                                                                                          ('ROB-011', '点焊机器人', 'KUKA-KR16', 2, '焊接车间/传统线/工位5', 2, '库卡机器人', '2023-03-01', '2023-04-10', '2025-04-20', 45),
                                                                                                                                                                                                          ('AGV-006', '磁导航AGV', 'MIRO-600', 4, '总装车间/暂存区', 3, '深圳麦格米特', '2022-05-15', '2022-06-01', '2025-04-25', 120),
                                                                                                                                                                                                          ('CNC-101', '三轴加工中心', 'HAAS-VF2', 3, '机加车间/普通区', 1, '哈斯自动化', '2020-08-01', '2020-09-20', '2025-04-05', 30),
                                                                                                                                                                                                          ('INJ-001', '注塑成型机', '海天-120T', 5, '注塑车间/1号机', 1, '海天国际', '2021-10-01', '2021-11-10', '2025-04-10', 90);




CREATE TABLE product (
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

CREATE TABLE production_order (
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

CREATE TABLE process_definition (
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

CREATE TABLE production_schedule (
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

CREATE TABLE production_record (
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

CREATE TABLE quality_inspection_item (
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

CREATE TABLE quality_inspection_record (
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

-- 插入工序定义（每个产品3道工序）
INSERT INTO process_definition (process_code, process_name, product_id, sequence, equipment_type, standard_time, quality_checkpoints) VALUES
-- 产品1（A-1001）
('PRC-001', '压铸成型', 1, 1, 1, 45, '["外观检查"]'),
('PRC-002', '电路板组装', 1, 2, 3, 120, '["导通测试"]'),
('PRC-003', '气密测试', 1, 3, 2, 60, '["气密性测试"]'),

-- 产品2（B-2002）
('PRC-004', '定子绕线', 2, 1, 4, 180, '["绝缘测试"]'),
('PRC-005', '转子动平衡', 2, 2, 3, 90, '["空载电流"]'),
('PRC-006', '整机装配', 2, 3, 2, 150, '["振动测试"]'),

-- 产品3（C-3003）
('PRC-007', '机械臂铸造', 3, 1, 1, 240, '["尺寸精度"]'),
('PRC-008', '精密加工', 3, 2, 3, 300, '["负载测试"]'),
('PRC-009', '关节组装', 3, 3, 2, 180, '["耐久测试"]'),

-- 产品4（D-4004）工序
('PRC-010', '齿轮加工', 4, 1, 3, 180, '["齿形精度检测"]'),
('PRC-011', '箱体组装', 4, 2, 2, 120, '["密封性测试"]'),
('PRC-012', '性能测试', 4, 3, 5, 240, '["负载运行测试"]'),

-- 产品5（E-5005）工序
('PRC-013', '芯片焊接', 5, 1, 2, 90, '["焊点检测"]'),
('PRC-014', '模块封装', 5, 2, 4, 60, '["外观检查"]'),
('PRC-015', '功能校验', 5, 3, 3, 120, '["信号稳定性测试"]');

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


