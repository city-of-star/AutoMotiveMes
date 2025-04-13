-- 用户表
CREATE TABLE sys_user (
    user_id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '用户ID',
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    password VARCHAR(100) NOT NULL COMMENT '加密密码',
    real_name VARCHAR(50) COMMENT '真实姓名',
    dept_id BIGINT COMMENT '部门id',
    post_id BIGINT COMMENT '岗位id',
    head_img VARCHAR(200) COMMENT '头像URL',
    email VARCHAR(100) COMMENT '邮箱',
    phone VARCHAR(20) COMMENT '联系电话',
    status TINYINT(1) DEFAULT 1 COMMENT '状态(0:禁用 1:启用)',
    account_locked TINYINT(1) DEFAULT 1 COMMENT '账户是否锁定(0:已锁定;1:未锁定)',
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
    perm_type varchar(20) NOT NULL COMMENT '权限类型(MENU,BUTTON,API)',
    parent_id INT DEFAULT 0 COMMENT '父权限ID',
    path VARCHAR(100) COMMENT '前端路由路径',
    component VARCHAR(100) COMMENT '前端组件',
    icon VARCHAR(50) COMMENT '菜单图标',
    order_num INT DEFAULT 0 COMMENT '显示顺序',
    api_path VARCHAR(100) COMMENT '后端接口路径',
    method VARCHAR(10) COMMENT '请求方法'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

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

-- 插入角色数据
INSERT INTO sys_role (role_code, role_name, description) VALUES
    ('SUPER_ADMIN', '超级管理员', '拥有系统所有权限(系统初始化的唯一角色)'),
    ('ADMIN', '管理员', '拥有系统所有权限'),
    ('PRODUCTION_STAFF', '生产人员', '生产车间操作人员'),
    ('MAINTENANCE_STAFF', '设备维护', '设备维护人员'),
    ('QUALITY_STAFF', '质检人员', '质量检验人员');

-- 插入权限数据
INSERT INTO sys_permission (perm_code, perm_name, perm_type, parent_id, path, component, api_path, method) VALUES
    --  系统管理
    ('system:manage', '系统管理', 'MENU', 0, '/system', NULL, '/api/system', NULL),

    -- 用户管理模块
    ('system:user:manage', '用户管理', 'MENU', 1, '/system', '/Index.vue', '/api/system', NULL),  -- 2
    ('system:user:add', '新增用户', 'BUTTON', 2, NULL, NULL, '/api/system/user/add', 'POST'),
    ('system:user:delete', '删除用户', 'BUTTON', 2, NULL, NULL, '/api/system/user/delete', 'POST'),
    ('system:user:update', '编辑用户', 'BUTTON', 2, NULL, NULL, '/api/system/user/update', 'POST'),
    ('system:user:list', '查询用户', 'BUTTON', 2, NULL, NULL, '/api/system/user/list', 'GET'),
    ('system:user:import', '导入用户', 'BUTTON', 2, NULL, NULL, '/api/system/user/import', 'POST'),
    ('system:user:export', '导出用户', 'BUTTON', 2, NULL, NULL, '/api/system/user/export', 'GET'),

    -- 角色管理模块
    ('system:role:manage', '角色管理', 'MENU', 1, '/system/role', '/Index.vue', '/api/system/role', NULL),  -- 9
    ('system:role:add', '新增角色', 'BUTTON', 9, NULL, NULL, '/api/system/role/add', 'POST'),
    ('system:role:delete', '删除角色', 'BUTTON', 9, NULL, NULL, '/api/system/role/delete', 'POST'),
    ('system:role:update', '编辑角色', 'BUTTON', 9, NULL, NULL, '/api/system/role/update', 'POST'),
    ('system:role:list', '查询角色', 'BUTTON', 9, NULL, NULL, '/api/system/role/list', 'GET'),
    ('system:role:export', '导出角色', 'BUTTON', 9, NULL, NULL, '/api/system/role/export', 'GET'),

    -- 部门管理模块
    ('system:dept:manage', '部门管理', 'MENU', 1, '/system/dept', '/Index.vue', '/api/system/dept', NULL),  -- 15
    ('system:dept:add', '新增部门', 'BUTTON', 15, NULL, NULL, '/api/system/dept/add', 'POST'),
    ('system:dept:delete', '删除部门', 'BUTTON', 15, NULL, NULL, '/api/system/dept/delete', 'POST'),
    ('system:dept:update', '编辑部门', 'BUTTON', 15, NULL, NULL, '/api/system/dept/update', 'POST'),
    ('system:dept:list', '查询部门', 'BUTTON', 15, NULL, NULL, '/api/system/dept/list', 'GET'),
    ('system:dept:export', '导出部门', 'BUTTON', 15, NULL, NULL, '/api/system/dept/export', 'GET'),

    -- 岗位管理模块
    ('system:post:manage', '岗位管理', 'MENU', 1, '/system/post', '/Index.vue', '/api/system/post', NULL),  -- 21
    ('system:post:add', '新增岗位', 'BUTTON', 21, NULL, NULL, '/api/system/post/add', 'POST'),
    ('system:post:delete', '删除岗位', 'BUTTON', 21, NULL, NULL, '/api/system/post/delete', 'POST'),
    ('system:post:update', '编辑岗位', 'BUTTON', 21, NULL, NULL, '/api/system/post/update', 'POST'),
    ('system:post:list', '查询岗位', 'BUTTON', 21, NULL, NULL, '/api/system/post/list', 'GET'),
    ('system:post:export', '导出岗位', 'BUTTON', 21, NULL, NULL, '/api/system/post/export', 'GET'),

    -- 生产监控模块
    ('production:monitor', '生产监控', 'MENU', 0, '/production', 'Layout', '/api/production', NULL), -- 27
    ('production:monitor:real-time', '实时监控', 'MENU', 27, 'real-time', '@/views/production/real-time/Index.vue', '/api/production/realtime', 'GET'), -- 28
    ('production:monitor:history', '历史查询', 'MENU', 27, 'history', '@/views/production/history/Index.vue', '/api/production/history', 'GET'), -- 29

    -- 生产排程模块
    ('scheduling:manage', '生产排程', 'MENU', 0, '/scheduling', 'Layout', '/api/scheduling', NULL), -- 30
    ('scheduling:order:manage', '工单管理', 'MENU', 30, 'orders', '@/views/scheduling/orders/Index.vue', '/api/scheduling/orders', 'GET'), -- 31
    ('scheduling:plan:manage', '排程计划', 'MENU', 30, 'plan', '@/views/scheduling/plan/Index.vue', '/api/scheduling/plan', 'GET'), -- 32

    -- 设备管理模块
    ('equipment:manage', '设备管理', 'MENU', 0, '/equipment', 'Layout', '/api/equipment', NULL), -- 33
    ('equipment:status:view', '设备状态', 'MENU', 33, 'status', '@/views/equipment/status/Index.vue', '/api/equipment/status', 'GET'), -- 34
    ('equipment:maintenance:manage', '维护记录', 'MENU', 33, 'maintenance', '@/views/equipment/maintenance/Index.vue', '/api/equipment/maintenance', 'GET'), -- 35

    -- 报警中心模块
    ('alarm:manage', '报警中心', 'MENU', 0, '/alarm', 'Layout', '/api/alarm', NULL), -- 36
    ('alarm:current:view', '实时报警', 'MENU', 36, 'current', '@/views/alarm/current/Index.vue', '/api/alarm/current', 'GET'), -- 37
    ('alarm:history:view', '报警历史', 'MENU', 36, 'history', '@/views/alarm/history/Index.vue', '/api/alarm/history', 'GET'), -- 38

    -- 生产报表模块
    ('report:view', '生产报表', 'MENU', 0, '/report', 'Layout', '/api/report', NULL), -- 39
    ('report:daily:view', '生产日报', 'MENU', 39, 'daily', '@/views/report/daily/Index.vue', '/api/report/daily', 'GET'), -- 40
    ('report:quality:view', '质量分析', 'MENU', 39, 'quality', '@/views/report/quality/Index.vue', '/api/report/quality', 'GET'); -- 41

-- 插入用户数据 测试密码统一为123456（使用BCrypt加密存储）
INSERT INTO sys_user (username, password, real_name, dept_id, post_id, email, phone, status, account_locked, login_attempts, last_login) VALUES
    -- 超级管理员(1-3)
    ('admin', '$2a$10$.0brTBYitG6.GVWfB8.7e.OolO2ec1j35d7Qpq8J/etjQLf/Yp4sa', '超级管理员', 1, 1, '4736289150@qq.com', '13524689753',  1, 1, 0, '2024-03-20 09:25:00'),
    ('lhy', '$2a$10$.0brTBYitG6.GVWfB8.7e.OolO2ec1j35d7Qpq8J/etjQLf/Yp4sa', '李鸿羽', 1, 1, '2722562862@qq.com', '18255097030',  1, 1, 0, '2024-03-20 08:45:00'),
    ('lqh', '$2a$10$.0brTBYitG6.GVWfB8.7e.OolO2ec1j35d7Qpq8J/etjQLf/Yp4sa', '刘齐慧', 1, 1, '2825646787@qq.com', '13855605201',  1, 1, 0, '2024-03-20 08:45:00'),

    -- 管理员(4-13)
    ('ydf', '$2a$10$.0brTBYitG6.GVWfB8.7e.OolO2ec1j35d7Qpq8J/etjQLf/Yp4sa', '杨东风', 1, 2, '8219537460@qq.com', '18765432109',  1, 1, 0, '2024-03-20 09:25:00'),
    ('yjq', '$2a$10$.0brTBYitG6.GVWfB8.7e.OolO2ec1j35d7Qpq8J/etjQLf/Yp4sa', '杨佳倩', 1, 2, '3507462891@qq.com', '15987654321',  1, 1, 0, '2024-03-20 09:25:00'),
    ('yzz', '$2a$10$.0brTBYitG6.GVWfB8.7e.OolO2ec1j35d7Qpq8J/etjQLf/Yp4sa', '杨智喆', 1, 2, '9162835740@qq.com', '17890123456',  1, 1, 0, '2024-03-20 09:25:00'),
    ('wjx', '$2a$10$.0brTBYitG6.GVWfB8.7e.OolO2ec1j35d7Qpq8J/etjQLf/Yp4sa', '汪俊祥', 1, 2, '2849173650@qq.com', '14736985210',  1, 1, 0, '2024-03-20 09:25:00'),
    ('ljb', '$2a$10$.0brTBYitG6.GVWfB8.7e.OolO2ec1j35d7Qpq8J/etjQLf/Yp4sa', '李佳宝', 1, 2, '7053916284@qq.com', '19182736455',  1, 1, 0, '2024-03-20 09:25:00'),
    ('wwb', '$2a$10$.0brTBYitG6.GVWfB8.7e.OolO2ec1j35d7Qpq8J/etjQLf/Yp4sa', '王雯博', 1, 2, '5391628470@qq.com', '16655443322',  1, 1, 0, '2024-03-20 09:25:00'),
    ('lkw', '$2a$10$.0brTBYitG6.GVWfB8.7e.OolO2ec1j35d7Qpq8J/etjQLf/Yp4sa', '刘楷雯', 1, 2, '1647392580@qq.com', '13214567890',  1, 1, 0, '2024-03-20 09:25:00'),
    ('wyj', '$2a$10$.0brTBYitG6.GVWfB8.7e.OolO2ec1j35d7Qpq8J/etjQLf/Yp4sa', '王永杰', 1, 2, '6925814370@qq.com', '18809123456',  1, 1, 0, '2024-03-20 09:25:00'),
    ('dty', '$2a$10$.0brTBYitG6.GVWfB8.7e.OolO2ec1j35d7Qpq8J/etjQLf/Yp4sa', '董婷英', 1, 2, '3746289150@qq.com', '17156789012',  1, 1, 0, '2024-03-20 09:25:00'),
    ('wcz', '$2a$10$.0brTBYitG6.GVWfB8.7e.OolO2ec1j35d7Qpq8J/etjQLf/Yp4sa', '吴成周', 1, 2, '8519372640@qq.com', '19923456789',  1, 1, 0, '2024-03-20 09:25:00'),

    -- 生产部用户(14-15)
    ('worker1', '$2a$10$.0brTBYitG6.GVWfB8.7e.OolO2ec1j35d7Qpq8J/etjQLf/Yp4sa', '张强', 5, 7, 'worker1@factory.com', '13800138001', 1, 1, 0, '2024-03-20 09:25:00'),
    ('worker2', '$2a$10$.0brTBYitG6.GVWfB8.7e.OolO2ec1j35d7Qpq8J/etjQLf/Yp4sa', '李娜', 5, 7, 'worker2@factory.com', '13800138002', 1, 1, 0, '2024-03-20 09:25:00'),

    -- 设备部用户(16-17)
    ('engineer1', '$2a$10$.0brTBYitG6.GVWfB8.7e.OolO2ec1j35d7Qpq8J/etjQLf/Yp4sa', '陈磊', 3, 9, 'engineer1@factory.com', '13800138003', 1, 1, 0, '2024-03-20 09:25:00'),
    ('engineer2', '$2a$10$.0brTBYitG6.GVWfB8.7e.OolO2ec1j35d7Qpq8J/etjQLf/Yp4sa', '王芳', 3, 9, 'engineer2@factory.com', '13800138004', 1, 1, 0, '2024-03-20 09:25:00'),

    -- 质量部用户(18-19)
    ('inspector1', '$2a$10$.0brTBYitG6.GVWfB8.7e.OolO2ec1j35d7Qpq8J/etjQLf/Yp4sa', '赵敏', 4, 11, 'inspector1@factory.com', '13800138005', 1, 1, 0, '2024-03-20 09:25:00'),
    ('inspector2', '$2a$10$.0brTBYitG6.GVWfB8.7e.OolO2ec1j35d7Qpq8J/etjQLf/Yp4sa', '周杰', 4, 11, 'inspector2@factory.com', '13800138006', 1, 1, 0, '2024-03-20 09:25:00');

-- 插入部门
INSERT INTO sys_dept (dept_name, parent_id, order_num, status, leader_id) VALUES
    ('总部', null, 1, 1, 1),  -- 顶级部门，负责人为admin
    ('生产部', 1, 2, 1, 3),  -- dept_id=2 父部门是总部，负责人杨东风
    ('设备部', 1, 3, 1, 5),  -- dept_id=3 父部门是总部，负责人杨智喆
    ('质量部', 1, 4, 1, 7),  -- dept_id=4 父部门是总部，负责人李佳宝
    ('生产一部', 2, 1, 1, 6),  -- dept_id=5 父部门是生产部，负责人汪俊祥
    ('生产二部', 2, 2, 1, 9);  -- dept_id=6 父部门是生产部，负责人刘楷雯

-- 插入岗位
INSERT INTO sys_post (post_name, post_code, dept_id, order_num, status) VALUES
    -- 总部岗位
    ('超级管理员', 'SUPER_ADMIN', 1, 1, 1),
    ('管理员', 'ADMIN', 1, 2, 1),

    -- 生产部岗位
    ('生产经理', 'PRODUCTION_MANAGER', 2, 1, 1),
    ('车间主任', 'WORKSHOP_DIRECTOR', 5, 2, 1),
    ('操作员', 'OPERATOR', 5, 3, 1),

    -- 设备部岗位
    ('设备主管', 'EQUIPMENT_LEADER', 3, 1, 1),
    ('维护工程师', 'MAINTENANCE_ENGINEER', 3, 2, 1),

    -- 质量部岗位
    ('质量总监', 'QUALITY_DIRECTOR', 4, 1, 1),
    ('检验员', 'INSPECTOR', 4, 2, 1);

-- 用户角色关系
INSERT INTO sys_user_role (user_id, role_id) VALUES
    (1, 1),(2, 1),(3, 1),

    (4, 2),(5, 2),(6, 2),(7, 2),(8, 2),
    (9, 2),(10, 2),(11, 2),(12, 2),(13, 2),

    (14, 3),(15, 3),

    (16, 4),(17, 4),

    (18, 5),(19, 5);

-- 角色权限关系
INSERT INTO sys_role_permission (role_id, perm_id) VALUES
    -- 超级管理员
    (1, 1),(1, 2),(1, 3),(1, 4),
    (1, 5),(1, 6),(1, 7),(1, 8),
    (1, 9),(1,10),(1, 11),(1, 12),
    (1, 13),(1, 14),(1, 15),(1, 16),
    (1, 17),(1, 18),(1, 19),(1, 20),
    (1, 21),(1, 22),(1, 23),(1, 24),
    (1, 25),(1, 26),(1, 27),(1, 28),
    (1, 29),(1, 30),(1, 31),(1, 32),
    (1, 33),(1, 34),(1, 35),(1, 36),
    (1, 37),(1, 38),(1, 39),(1, 40),
    (1, 41),

    -- 管理员
    (2, 1),(2, 2),(2, 3),(2, 4),
    (2, 5),(2, 6),(2, 7),(2, 8),
    (2, 9),(2,10),(2, 11),(2, 12),
    (2, 13),(2, 14),(2, 15),(2, 16),
    (2, 17),(2, 18),(2, 19),(2, 20),
    (2, 21),(2, 22),(2, 23),(2, 24),
    (2, 25),(2, 26),(2, 27),(2, 28),
    (2, 29),(2, 30),(2, 31),(2, 32),
    (2, 33),(2, 34),(2, 35),(2, 36),
    (2, 37),(2, 38),(2, 39),(2, 40),
    (2, 41),

    -- 生产人员
    (3, 27),(3, 28),(3, 29),
    (3, 30),(3, 31),(3, 32),

    -- 设备维护人员
    (4, 33),(4, 34),(4, 35),
    (4, 36),(4, 37),(4, 38),

    -- 质检人员
    (5, 39),(5, 40),(5, 41);